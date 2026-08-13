package com.eatcontrolai.glasses

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.eatcontrolai.inference.InferenceMeta
import com.eatcontrolai.inference.TtsProvider
import java.io.ByteArrayOutputStream
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext

/**
 * Câmera traseira do celular como fonte de frame (FR-002).
 *
 * Serve a dois propósitos que o edital cobra: é a entrada por câmera enquanto o DAT está bloqueado
 * (ADR-0006), e é o plano B se os óculos falharem no dia — item do `MVP_CHECKLIST`.
 *
 * A pipeline a jusante não muda: o [InteractionOrchestrator] continua recebendo JPEG por
 * [GlassesGateway], sem saber se veio de óculos, do mock ou daqui (`contexto-gpt.md` §13).
 *
 * Sobre bateria (NFR-009): a câmera só é ligada quando a tela de análise está visível, e a captura
 * é sob demanda. Não há stream contínuo nem análise por frame.
 */
class PhoneCameraGateway(
    context: Context,
    private val tts: TtsProvider
) : GlassesGateway {

    private val appContext = context.applicationContext
    private var imageCapture: ImageCapture? = null
    private var cameraProvider: ProcessCameraProvider? = null

    override val sourceId: String = "phone_camera"

    override var isConnected: Boolean = false
        private set

    override val status: GlassesStatus
        get() = GlassesStatus(
            sourceLabel = "Câmera do celular",
            isMock = false,
            connected = isConnected,
            // O nível de bateria aqui é o do próprio telefone, não de um wearable: não faz sentido
            // reportar como se fosse status de dispositivo externo.
            batteryPercent = null,
            cameraReady = isConnected,
            audioReady = true
        )

    /**
     * Liga a câmera e conecta o preview. Chamado pela tela, que é dona do ciclo de vida.
     * [surfaceProvider] vem do `PreviewView` do CameraX.
     */
    suspend fun bind(
        lifecycleOwner: LifecycleOwner,
        surfaceProvider: Preview.SurfaceProvider
    ) = withContext(Dispatchers.Main) {
        val provider = awaitCameraProvider()
        cameraProvider = provider

        val preview = Preview.Builder().build().apply {
            setSurfaceProvider(surfaceProvider)
        }
        val capture = ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()

        provider.unbindAll()
        provider.bindToLifecycle(
            lifecycleOwner,
            CameraSelector.DEFAULT_BACK_CAMERA,
            preview,
            capture
        )
        imageCapture = capture
        isConnected = true
    }

    /** Desliga a câmera. A tela chama ao sair — é o duty-cycle do NFR-009. */
    fun unbind() {
        cameraProvider?.unbindAll()
        imageCapture = null
        isConnected = false
    }

    override suspend fun connect() = Unit

    override suspend fun disconnect() = unbind()

    override suspend fun capturePhoto(): ByteArray {
        val capture = imageCapture
            ?: error("Câmera não está ligada. Abra a tela de análise e conceda a permissão.")

        val proxy = suspendCancellableCoroutine { continuation ->
            capture.takePicture(
                ContextCompat.getMainExecutor(appContext),
                object : ImageCapture.OnImageCapturedCallback() {
                    override fun onCaptureSuccess(image: ImageProxy) {
                        if (continuation.isActive) continuation.resume(image) else image.close()
                    }

                    override fun onError(exception: ImageCaptureException) {
                        if (continuation.isActive) continuation.resumeWithException(exception)
                    }
                }
            )
        }

        return proxy.use { image ->
            val buffer = image.planes[0].buffer
            val bytes = ByteArray(buffer.remaining())
            buffer.get(bytes)
            applyRotation(bytes, image.imageInfo.rotationDegrees)
        }
    }

    override suspend fun startVoiceCapture(): ByteArray =
        throw UnsupportedOperationException("A captura de voz é feita pelo SttProvider.")

    override suspend fun playSpeech(text: String): InferenceMeta = tts.speak(text)

    /**
     * O OCR recebe bytes, e `BitmapFactory` ignora a orientação EXIF. Sem esta rotação, um rótulo
     * fotografado em retrato chega deitado no ML Kit e a leitura despenca.
     */
    private fun applyRotation(jpeg: ByteArray, degrees: Int): ByteArray {
        if (degrees == 0) return jpeg
        val source = BitmapFactory.decodeByteArray(jpeg, 0, jpeg.size) ?: return jpeg
        val matrix = Matrix().apply { postRotate(degrees.toFloat()) }
        val rotated = Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
        source.recycle()
        return ByteArrayOutputStream().use { stream ->
            rotated.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, stream)
            rotated.recycle()
            stream.toByteArray()
        }
    }

    private suspend fun awaitCameraProvider(): ProcessCameraProvider =
        suspendCancellableCoroutine { continuation ->
            val future = ProcessCameraProvider.getInstance(appContext)
            future.addListener(
                { runCatching { future.get() }.fold(continuation::resume, continuation::resumeWithException) },
                ContextCompat.getMainExecutor(appContext)
            )
        }

    private companion object {
        const val JPEG_QUALITY = 92
    }
}
