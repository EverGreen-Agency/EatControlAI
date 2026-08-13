package com.eatcontrolai.inference.mlkit

import android.graphics.BitmapFactory
import com.eatcontrolai.inference.InferenceMeta
import com.eatcontrolai.inference.OcrProvider
import com.eatcontrolai.inference.OcrResult
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.suspendCancellableCoroutine

/**
 * OCR on-device via ML Kit (`docs/adr/0004`).
 *
 * Baseline, não escolha definitiva: entra como `mlkit_text_v2` em `benchmark/candidates.yaml` e só
 * permanece se vencer o protocolo de `docs/MODEL_BENCHMARK.md` num aparelho físico.
 */
class MlKitOcrProvider : OcrProvider {

    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    override suspend fun recognize(imageBytes: ByteArray): OcrResult {
        val startedAt = System.nanoTime()
        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            ?: return OcrResult("", meta(startedAt))

        val text = suspendCancellableCoroutine { continuation ->
            recognizer.process(InputImage.fromBitmap(bitmap, 0))
                .addOnSuccessListener { continuation.resume(it.text) }
                .addOnFailureListener { continuation.resumeWithException(it) }
        }

        return OcrResult(text, meta(startedAt))
    }

    private fun meta(startedAt: Long) = InferenceMeta(
        providerId = PROVIDER_ID,
        version = TextRecognizerOptions.DEFAULT_OPTIONS.javaClass.`package`?.implementationVersion
            ?: "bundled-latin",
        runtime = "mlkit",
        latencyMs = (System.nanoTime() - startedAt) / 1_000_000
    )

    companion object {
        const val PROVIDER_ID = "mlkit_text_v2"
    }
}
