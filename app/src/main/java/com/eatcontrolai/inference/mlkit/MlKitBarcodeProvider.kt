package com.eatcontrolai.inference.mlkit

import android.graphics.BitmapFactory
import com.eatcontrolai.inference.BarcodeProvider
import com.eatcontrolai.inference.BarcodeResult
import com.eatcontrolai.inference.InferenceMeta
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.suspendCancellableCoroutine

/**
 * Leitor de código de barras on-device via ML Kit (`docs/adr/0004`).
 *
 * Suporta formatos EAN-13, EAN-8, UPC-A, UPC-E e QR Code.
 */
class MlKitBarcodeProvider : BarcodeProvider {

    private val scanner = BarcodeScanning.getClient()

    override suspend fun decode(imageBytes: ByteArray): BarcodeResult {
        val startedAt = System.nanoTime()
        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            ?: return BarcodeResult(null, meta(startedAt))

        val rawValue = suspendCancellableCoroutine<String?> { continuation ->
            scanner.process(InputImage.fromBitmap(bitmap, 0))
                .addOnSuccessListener { barcodes ->
                    val firstCode = barcodes.firstOrNull()?.rawValue
                    continuation.resume(firstCode)
                }
                .addOnFailureListener { continuation.resumeWithException(it) }
        }

        return BarcodeResult(rawValue, meta(startedAt))
    }

    private fun meta(startedAt: Long) = InferenceMeta(
        providerId = PROVIDER_ID,
        version = "mlkit-barcode-17.3.0",
        runtime = "mlkit",
        latencyMs = (System.nanoTime() - startedAt) / 1_000_000
    )

    companion object {
        const val PROVIDER_ID = "mlkit_barcode_v1"
    }
}
