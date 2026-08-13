package com.eatcontrolai.inference

/**
 * Contratos de percepção (`docs/adr/0003`).
 *
 * O domínio depende destas interfaces, nunca de ML Kit, MediaPipe, LiteRT ou Whisper. Trocar de
 * modelo é trocar a implementação registrada no [ModelRegistry] — o resto do app não muda.
 *
 * Todo resultado carrega [InferenceMeta] porque `docs/SRS.md` NFR-007 exige que cada provider
 * exponha versão, runtime e latência, e `docs/MODEL_BENCHMARK.md` compara implementações nesses eixos.
 */
data class InferenceMeta(
    val providerId: String,
    val version: String,
    val runtime: String,
    val latencyMs: Long
)

data class OcrResult(val text: String, val meta: InferenceMeta)
data class BarcodeResult(val rawValue: String?, val meta: InferenceMeta)
data class SttResult(val text: String, val meta: InferenceMeta)
data class Detection(val label: String, val confidence: Float)
data class DetectionResult(val detections: List<Detection>, val meta: InferenceMeta)

interface OcrProvider {
    suspend fun recognize(imageBytes: ByteArray): OcrResult
}

interface BarcodeProvider {
    suspend fun decode(imageBytes: ByteArray): BarcodeResult
}

interface SttProvider {
    suspend fun transcribe(audioPcm: ByteArray): SttResult
}

interface TtsProvider {
    /** Devolve a latência até o início do áudio — `time-to-first-audio` de `docs/METRICS.md`. */
    suspend fun speak(text: String): InferenceMeta
}

interface ObjectDetectionProvider {
    suspend fun detect(imageBytes: ByteArray): DetectionResult
}
