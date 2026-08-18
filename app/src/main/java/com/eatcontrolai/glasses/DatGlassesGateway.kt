package com.eatcontrolai.glasses

import android.app.Activity
import android.content.Context
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import com.eatcontrolai.inference.InferenceMeta
import com.eatcontrolai.inference.TtsProvider
import com.meta.wearable.dat.camera.Camera
import com.meta.wearable.dat.camera.addCamera
import com.meta.wearable.dat.camera.removeCamera
import com.meta.wearable.dat.camera.types.StreamConfiguration
import com.meta.wearable.dat.camera.types.VideoFrame
import com.meta.wearable.dat.camera.types.VideoQuality
import com.meta.wearable.dat.core.Wearables
import com.meta.wearable.dat.core.selectors.AutoDeviceSelector
import com.meta.wearable.dat.camera.types.StreamState
import com.meta.wearable.dat.core.session.DeviceSession
import com.meta.wearable.dat.core.session.DeviceSessionState
import com.meta.wearable.dat.core.types.RegistrationState
import java.io.ByteArrayOutputStream
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
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
 * ## O que ainda precisa de hardware para validar
 *
 * 1. **`PhotoData` é uma interface vazia na 0.9.0.** `capturePhoto()` devolve `DatResult<PhotoData,
 *    CaptureError>`, mas o tipo público não expõe nenhum membro — os bytes existem só na
 *    implementação interna. Então o disparo serve para acionar o obturador e o indicador dos óculos,
 *    e a imagem é lida do `videoStream`. Se uma versão seguinte abrir `PhotoData`, este é o primeiro
 *    trecho a simplificar.
 * 2. **O formato do frame não está documentado.** Pedimos `compressVideo = false` e tratamos como
 *    NV21, que é o mais comum em captura Android. Se vier outro layout, [toJpeg] falha de forma
 *    explícita em vez de devolver imagem corrompida — e o OCR não recebe lixo achando que é rótulo.
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
    fun startRegistration(activity: Activity) = Wearables.startRegistration(activity)

    override suspend fun connect() {
        initialize().getOrThrow()

        val newSession = Wearables.createSession(AutoDeviceSelector()).getOrNull()
            ?: error("Nenhum par de óculos disponível. Confira o pareamento no app Meta AI.")

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
        ).getOrNull() ?: run {
            newSession.stop()
            session = null
            error("Os óculos negaram a capability de câmera. Verifique a permissão no Meta AI.")
        }

        // Sem start() nenhum frame chega, e frames só são entregues em STREAMING.
        newCamera.stream.start()
        camera = newCamera
        isConnected = true
    }

    override suspend fun disconnect() {
        camera?.stream?.stop()
        camera?.stop()
        session?.let {
            it.removeCamera()
            it.stop()
        }
        camera = null
        session = null
        isConnected = false
    }

    override suspend fun capturePhoto(): ByteArray {
        val stream = camera?.stream ?: error("Sessão de câmera não está aberta.")

        withTimeoutOrNull(SESSION_TIMEOUT_MS) {
            stream.state.first { it == StreamState.STREAMING }
        } ?: error("O stream não chegou a STREAMING. Sem isso, nenhum frame é entregue.")

        // Dispara o obturador nos óculos. O retorno é ignorado de propósito: PhotoData não expõe
        // bytes na 0.9.0 (ver KDoc da classe).
        stream.capturePhoto()

        val frame = withTimeoutOrNull(CAPTURE_TIMEOUT_MS) {
            stream.videoStream.first { !it.isCodecConfig }
        } ?: error("Os óculos não entregaram frame em ${CAPTURE_TIMEOUT_MS} ms.")

        return frame.toJpeg()
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
    private fun VideoFrame.toJpeg(): ByteArray {
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
        const val FRAME_RATE = 30
        const val JPEG_QUALITY = 92
        const val CAPTURE_TIMEOUT_MS = 5_000L
        const val SESSION_TIMEOUT_MS = 10_000L
    }
}
