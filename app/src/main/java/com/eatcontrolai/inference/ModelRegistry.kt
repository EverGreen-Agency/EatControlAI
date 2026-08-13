package com.eatcontrolai.inference

/**
 * Conjunto de implementações ativas. Só OCR e TTS são obrigatórios hoje — as demais trilhas do
 * `README.md` entram nulas até serem implementadas, e o app degrada em vez de quebrar (NFR-004).
 */
data class ProviderSet(
    val ocr: OcrProvider,
    val tts: TtsProvider,
    val barcode: BarcodeProvider? = null,
    val stt: SttProvider? = null,
    val detector: ObjectDetectionProvider? = null
)

/**
 * O app depende de interfaces, não de modelos concretos.
 * O benchmark de `docs/MODEL_BENCHMARK.md` decide qual [ProviderSet] fica ativo.
 */
class ModelRegistry(private var active: ProviderSet) {
    fun current(): ProviderSet = active
    fun swap(next: ProviderSet) {
        active = next
    }
}
