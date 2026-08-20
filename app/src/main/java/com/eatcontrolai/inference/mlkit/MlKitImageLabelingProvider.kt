package com.eatcontrolai.inference.mlkit

import android.graphics.BitmapFactory
import com.eatcontrolai.inference.Detection
import com.eatcontrolai.inference.DetectionResult
import com.eatcontrolai.inference.InferenceMeta
import com.eatcontrolai.inference.ObjectDetectionProvider
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.suspendCancellableCoroutine

/**
 * Rotulagem genérica on-device do ML Kit usada somente para sugerir componentes de prato.
 *
 * O modelo bundled fica disponível offline desde a instalação. Seus rótulos não são composição
 * nutricional e passam pelo recorte conservador de `PlateLabelMapper` antes de aparecer na UI.
 * Fonte da configuração: https://developers.google.com/ml-kit/vision/image-labeling/android
 */
class MlKitImageLabelingProvider : ObjectDetectionProvider {

    private val options = ImageLabelerOptions.Builder()
        .setConfidenceThreshold(PROVIDER_THRESHOLD)
        .build()
    private val labeler = ImageLabeling.getClient(options)

    override suspend fun detect(imageBytes: ByteArray): DetectionResult {
        val startedAt = System.nanoTime()
        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            ?: return DetectionResult(emptyList(), meta(startedAt))

        return try {
            val detections = suspendCancellableCoroutine<List<Detection>> { continuation ->
                labeler.process(InputImage.fromBitmap(bitmap, 0))
                    .addOnSuccessListener { labels ->
                        continuation.resume(
                            labels.map { label -> Detection(label.text, label.confidence) }
                        )
                    }
                    .addOnFailureListener(continuation::resumeWithException)
            }
            DetectionResult(detections, meta(startedAt))
        } finally {
            bitmap.recycle()
        }
    }

    private fun meta(startedAt: Long) = InferenceMeta(
        providerId = PROVIDER_ID,
        version = MODEL_VERSION,
        runtime = "mlkit-on-device",
        latencyMs = (System.nanoTime() - startedAt) / 1_000_000
    )

    companion object {
        const val PROVIDER_ID = "mlkit_image_labeling_v1"
        const val MODEL_VERSION = "default-bundled-17.0.9"
        private const val PROVIDER_THRESHOLD = 0.50f
    }
}
