package com.eatcontrolai.glasses

import android.app.Activity
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import com.eatcontrolai.inference.InferenceMeta
import com.eatcontrolai.inference.TtsProvider
import com.meta.wearable.dat.camera.Camera
import com.meta.wearable.dat.camera.addCamera
import com.meta.wearable.dat.camera.removeCamera
import com.meta.wearable.dat.camera.types.PhotoData
import com.meta.wearable.dat.camera.types.StreamConfiguration
import com.meta.wearable.dat.camera.types.VideoFrame
import com.meta.wearable.dat.camera.types.VideoQuality
import com.meta.wearable.dat.core.Wearables
import com.meta.wearable.dat.core.selectors.AutoDeviceSelector
import com.meta.wearable.dat.camera.types.StreamState
import com.meta.wearable.dat.core.session.DeviceSession
import com.meta.wearable.dat.core.session.DeviceSessionState
import com.meta.wearable.dat.core.types.Permission
import com.meta.wearable.dat.core.types.PermissionStatus
import com.meta.wearable.dat.core.types.RegistrationState
import java.io.ByteArrayOutputStream
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeoutOrNull

/**
 * Fonte de captura sobre o Meta Wearables Device Access Toolkit 0.9.0.
 *
 * Mapa da API, levantado por inspeção dos AARs (a documentação pública não descreve tudo):
 *
 * ```text
 * Wearables.initialize(context)                    → DatResult<Unit, WearablesError>
 * Wearables.registrationState                      → StateFlow<RegistrationState>
 * Wearables.startRegistration(activity)            → abre o fluxo do Meta AI
 * Wearables.createSession(DeviceSelector)          → DatResult<DeviceSession, DeviceSessionError>
 *   session.start() / stop()
 *   session.addCamera(StreamConfiguration)         → DatResult<Camera, DeviceSessionError>
 *     camera.stream.start()
 *     camera.stream.capturePhoto()                 → DatResult<PhotoData, CaptureError>
 *     camera.stream.videoStream                    → Flow<VideoFrame>
 * ```
 *
 * **Não existe stream contínuo por padrão.** A sessão é aberta de propósito, a capability de câmera
 * é anexada de propósito, e a captura é um disparo. É o que o `NFR-009` pede e o que o checkpoint de
 * bateria do edital cobra: [disconnect] fecha tudo assim que a análise termina.
 *
 * ## Captura
 *
 * `capturePhoto()` devolve `PhotoData`, que é sealed: `PhotoData.Bitmap` (já decodificado) ou
 * `PhotoData.HEIC` (bytes codificados). Os dois viram JPEG para alimentar a mesma pipeline de OCR e
 * barcode do resto do app.
 *
 * O `videoStream` fica como plano B, para o caso de a captura de foto falhar. Ali os pixels vêm em
 * YUV cru (`compressVideo = false`) e o layout exato não está documentado, então [yuvToJpeg] falha
 * alto se o tamanho não bater — entregar bytes errados ao OCR produziria leitura silenciosamente
 * errada, o pior modo de falha possível neste produto.
 *
 * Só uma captura de foto por vez: uma segunda chamada com outra pendente devolve
 * `CaptureError.CaptureInProgress`.
 *
 * ## Ativação e áudio
 *
 * O wake word "Hey Meta" e o assistente Meta AI **não fazem parte do toolkit** — não dá para o
 * usuário chamar este app falando com os óculos. Quem abre a sessão é o telefone. O usuário controla
 * a sessão já aberta pelos próprios óculos: pausa, retoma ou encerra tocando neles, tirando-os ou
 * fechando as hastes.
 *
 * Microfone e alto-falantes **também não passam pelo DAT**: usam os perfis Bluetooth padrão do
 * Android (HFP para captura de voz). O toolkit cuida de câmera, registro, permissões e sessão.
 * É por isso que [startVoiceCapture] não existe aqui e a voz fica com o `SttProvider`.
 *
 * Só uma sessão ativa por dispositivo, e alguns recursos nativos dos óculos ficam indisponíveis
 * enquanto ela estiver aberta.
 */
class DatGlassesGateway(
    context: Context,
    private val tts: TtsProvider
) : GlassesGateway {

    private val appContext = context.applicationContext

    private var session: DeviceSession? = null
    private var camera: Camera? = null
    private var initialized = false
    private val lifecycleMutex = Mutex()

    override val sourceId: String = "dat_glasses"

    override var isConnected: Boolean = false
        private set

    override val status: GlassesStatus
        get() = GlassesStatus(
            sourceLabel = "Ray-Ban Meta (DAT 0.9.0)",
            isMock = false,
            connected = isConnected,
            // O SDK 0.9.0 não expõe nível de bateria dos óculos pela API pública.
            batteryPercent = null,
            cameraReady = camera != null,
            audioReady = isConnected
        )

    val registrationState: StateFlow<RegistrationState> get() = Wearables.registrationState

    /** `true` quando o app roda em Developer Mode — nesse caso não se declara APPLICATION_ID. */
    fun isDevMode(): Boolean = Wearables.isDevMode

    @Synchronized
    fun initialize(): Result<Unit> {
        if (initialized) return Result.success(Unit)
        val result = Wearables.initialize(appContext)
        return result.fold(
            { initialized = true; Result.success(Unit) },
            { error, throwable ->
                Result.failure(throwable ?: IllegalStateException("Falha ao inicializar o DAT: $error"))
            }
        )
    }

    /** Abre o fluxo de pareamento/autorização dentro do app Meta AI. */
    fun startRegistration(activity: Activity): Result<Unit> = runCatching {
        // No DAT 0.9.0 esta API é fire-and-forget (não devolve DatResult como initialize()).
        // Ainda assim, exceções síncronas precisam chegar à UI em vez de desaparecerem.
        Wearables.startRegistration(activity)
        Unit
    }

    override suspend fun connect() = lifecycleMutex.withLock {
        if (isConnected && session != null && camera != null) return@withLock

        initialize().getOrThrow()
        check(Wearables.registrationState.value == RegistrationState.REGISTERED) {
            "O Eat Control ainda não foi autorizado no app Meta AI."
        }
        check(Wearables.checkPermissionStatus(Permission.CAMERA) == PermissionStatus.Granted) {
            "A câmera dos óculos ainda não foi autorizada no app Meta AI."
        }

        // Remove resíduos de uma tentativa anterior antes de criar uma sessão nova.
        disconnectLocked()

        val newSession = Wearables.createSession(AutoDeviceSelector()).getOrNull()
            ?: error("Nenhum par de óculos disponível. Confira o pareamento no app Meta AI.")

        try {
            // start() é fire-and-forget: quem confirma é o state. Anexar a câmera antes de STARTED
            // falha de forma silenciosa e difícil de diagnosticar.
            newSession.start()
            val started = withTimeoutOrNull(SESSION_TIMEOUT_MS) {
                newSession.state.first { it == DeviceSessionState.STARTED }
            }
            if (started == null) {
                newSession.stop()
                error("A sessão não chegou a STARTED em $SESSION_TIMEOUT_MS ms.")
            }
            session = newSession

            val newCamera = newSession.addCamera(
                StreamConfiguration(
                    videoQuality = VideoQuality.HIGH,
                    frameRate = FRAME_RATE,
                    compressVideo = false
                )
            ).getOrNull() ?: error(
                "Os óculos negaram a capability de câmera. Verifique a permissão no Meta AI."
            )

            // Sem start() nenhum frame chega, e frames só são entregues em STREAMING.
            newCamera.stream.start()
            camera = newCamera
            isConnected = true
        } catch (throwable: Throwable) {
            disconnectLocked()
            throw throwable
        }
    }

    override suspend fun disconnect() = lifecycleMutex.withLock {
        disconnectLocked()
    }

    override suspend fun capturePhoto(): ByteArray = lifecycleMutex.withLock {
        val stream = camera?.stream ?: error("Sessão de câmera não está aberta.")

        withTimeoutOrNull(SESSION_TIMEOUT_MS) {
            stream.state.first { it == StreamState.STREAMING }
        } ?: error("O stream não chegou a STREAMING. Sem isso, nenhum frame é entregue.")

        val photo = withTimeoutOrNull(CAPTURE_TIMEOUT_MS) { stream.capturePhoto() }?.getOrNull()
        if (photo != null) return@withLock photo.toJpeg()

        // Plano B: a foto falhou, então pega um frame do stream que já está rodando.
        val frame = withTimeoutOrNull(CAPTURE_TIMEOUT_MS) {
            stream.videoStream.first { !it.isCodecConfig }
        } ?: error("Os óculos não entregaram foto nem frame em ${CAPTURE_TIMEOUT_MS} ms.")

        frame.yuvToJpeg()
    }

    /** Fecha cada recurso mesmo se uma etapa de cleanup falhar. Chamado sempre sob [lifecycleMutex]. */
    private fun disconnectLocked() {
        val currentCamera = camera
        val currentSession = session
        camera = null
        session = null
        isConnected = false

        runCatching { currentCamera?.stream?.stop() }
        runCatching { currentCamera?.stop() }
        if (currentSession != null) {
            runCatching { currentSession.removeCamera() }
            runCatching { currentSession.stop() }
        }
    }

    private fun PhotoData.toJpeg(): ByteArray = when (this) {
        is PhotoData.Bitmap -> ByteArrayOutputStream().use { stream ->
            bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, JPEG_QUALITY, stream)
            stream.toByteArray()
        }

        is PhotoData.HEIC -> {
            val bytes = ByteArray(data.remaining()).also { data.get(it) }
            // O Android decodifica HEIC desde a API 28; o ML Kit espera JPEG.
            val decoded = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                ?: error("Não consegui decodificar o HEIC devolvido pelos óculos.")
            ByteArrayOutputStream().use { stream ->
                decoded.compress(android.graphics.Bitmap.CompressFormat.JPEG, JPEG_QUALITY, stream)
                decoded.recycle()
                stream.toByteArray()
            }
        }

        else -> error("Formato de foto desconhecido: ${this::class.java.simpleName}")
    }

    override suspend fun startVoiceCapture(): ByteArray =
        throw UnsupportedOperationException(
            "O DAT 0.9.0 não expõe captura de microfone isolada. A voz usa o SttProvider no telefone."
        )

    override suspend fun playSpeech(text: String): InferenceMeta = tts.speak(text)

    /**
     * Converte o frame em JPEG para alimentar a mesma pipeline de OCR e barcode do resto do app.
     *
     * Falha alto de propósito quando o frame vem comprimido: decodificar H.264 exigiria MediaCodec,
     * e entregar bytes errados para o OCR produziria leitura silenciosamente errada — o pior modo de
     * falha possível neste produto.
     */
    private fun VideoFrame.yuvToJpeg(): ByteArray {
        check(!isCompressed) {
            "Frame comprimido não é suportado neste caminho. Configure compressVideo = false."
        }

        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)

        val expectedNv21Size = width * height * 3 / 2
        check(bytes.size >= expectedNv21Size) {
            "Frame de ${bytes.size} bytes não corresponde a NV21 ${width}x$height " +
                "(esperado ao menos $expectedNv21Size). Formato precisa ser confirmado em hardware."
        }

        return ByteArrayOutputStream().use { stream ->
            YuvImage(bytes, ImageFormat.NV21, width, height, null)
                .compressToJpeg(Rect(0, 0, width, height), JPEG_QUALITY, stream)
            stream.toByteArray()
        }
    }

    private companion object {
        const val FRAME_RATE = 15
        const val JPEG_QUALITY = 92
        const val CAPTURE_TIMEOUT_MS = 5_000L
        const val SESSION_TIMEOUT_MS = 10_000L
    }
}
