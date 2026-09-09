package com.eatcontrolai.glasses

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.eatcontrolai.domain.label.TextNormalizer
import com.eatcontrolai.domain.plate.PlateFoodClass
import com.eatcontrolai.domain.plate.PlateLabelMapper
import com.eatcontrolai.inference.InferenceMeta
import com.eatcontrolai.inference.TtsProvider
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.ByteArrayOutputStream
import java.util.concurrent.Executors
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext

/** Eventos de identificação contínua da câmera em tempo real (Camera-First). */
sealed interface LiveDetection {
    data class Barcode(val ean: String) : LiveDetection
    data class Label(val sampleText: String) : LiveDetection
    data class Plate(val components: List<PlateFoodClass>) : LiveDetection
    data object Idle : LiveDetection
}

/**
 * Câmera traseira do celular como fonte de frame (FR-002) com detecção contínua (ImageAnalysis).
 *
 * Suporta stream contínuo passivo com amostragem inteligente (~4 FPS) para não superaquecer
 * nem drenar bateria, identificando código de barras, rótulos de ingredientes e pratos de comida
 * automaticamente.
 */
class PhoneCameraGateway(
    context: Context,
    private val tts: TtsProvider
) : GlassesGateway {

    private val appContext = context.applicationContext
    private var imageCapture: ImageCapture? = null
    private var imageAnalysis: ImageAnalysis? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private val analysisExecutor = Executors.newSingleThreadExecutor()
    private var lastAnalyzedTimestamp = 0L

    private val barcodeScanner = BarcodeScanning.getClient()
    private val textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    private val imageLabeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)

    private val _liveDetection = MutableStateFlow<LiveDetection>(LiveDetection.Idle)
    val liveDetection: StateFlow<LiveDetection> = _liveDetection.asStateFlow()

    fun resetLiveDetection() {
        _liveDetection.value = LiveDetection.Idle
    }

    override val sourceId: String = "phone_camera"

    override var isConnected: Boolean = false
        private set

    override val status: GlassesStatus
        get() = GlassesStatus(
            sourceLabel = "Câmera do celular",
            isMock = false,
            connected = isConnected,
            batteryPercent = null,
            cameraReady = isConnected,
            audioReady = true
        )

    /**
     * Liga a câmera, conecta o preview e o ImageAnalysis contínuo.
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
        val analysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()

        analysis.setAnalyzer(analysisExecutor) { imageProxy ->
            processFrame(imageProxy)
        }

        provider.unbindAll()
        provider.bindToLifecycle(
            lifecycleOwner,
            CameraSelector.DEFAULT_BACK_CAMERA,
            preview,
            capture,
            analysis
        )
        imageCapture = capture
        imageAnalysis = analysis
        isConnected = true
    }

    @OptIn(ExperimentalGetImage::class)
    private fun processFrame(imageProxy: ImageProxy) {
        val now = System.currentTimeMillis()
        if (now - lastAnalyzedTimestamp < 250L) {
            imageProxy.close()
            return
        }
        lastAnalyzedTimestamp = now

        val mediaImage = imageProxy.image
        if (mediaImage == null) {
            imageProxy.close()
            return
        }

        val rotation = imageProxy.imageInfo.rotationDegrees
        val inputImage = InputImage.fromMediaImage(mediaImage, rotation)

        // 1. Código de barras contínuo (prioridade máxima e menor custo)
        barcodeScanner.process(inputImage)
            .addOnSuccessListener { barcodes ->
                val ean = barcodes.firstOrNull()?.rawValue
                if (!ean.isNullOrBlank()) {
                    _liveDetection.value = LiveDetection.Barcode(ean)
                    imageProxy.close()
                } else {
                    // 2. OCR contínuo para rótulo
                    textRecognizer.process(inputImage)
                        .addOnSuccessListener { visionText ->
                            val text = visionText.text
                            val normalized = TextNormalizer.normalize(text)
                            val hasMarker = LABEL_MARKERS.any { it in normalized }
                            val density = normalized.count { it.isLetterOrDigit() }
                            if (hasMarker || density >= 35) {
                                _liveDetection.value = LiveDetection.Label(text.take(80))
                                imageProxy.close()
                            } else {
                                // 3. Detecção visual de prato / comida
                                imageLabeler.process(inputImage)
                                    .addOnSuccessListener { labels ->
                                        val components = labels.mapNotNull { detection ->
                                            if (detection.confidence >= 0.55f) {
                                                PlateLabelMapper.classFor(detection.text)
                                            } else null
                                        }.distinct()

                                        if (components.isNotEmpty()) {
                                            _liveDetection.value = LiveDetection.Plate(components)
                                        } else {
                                            _liveDetection.value = LiveDetection.Idle
                                        }
                                        imageProxy.close()
                                    }
                                    .addOnFailureListener { imageProxy.close() }
                            }
                        }
                        .addOnFailureListener { imageProxy.close() }
                }
            }
            .addOnFailureListener { imageProxy.close() }
    }

    /** Desliga a câmera e o analisador contínuo. */
    fun unbind() {
        imageAnalysis?.clearAnalyzer()
        imageAnalysis = null
        cameraProvider?.unbindAll()
        imageCapture = null
        isConnected = false
        _liveDetection.value = LiveDetection.Idle
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
        val LABEL_MARKERS = listOf("INGREDIENTES", "CONTEM", "ALERGICOS", "PODE CONTER", "NAO CONTEM", "TABELA NUTRICIONAL")
    }
}
