package com.eatcontrolai.metrics

/**
 * Observabilidade desde cedo (`docs/METRICS.md`, `contexto-gpt.md` §57).
 *
 * Latência por etapa não é enfeite: é um dos eixos que decidem qual modelo entra no app
 * (`docs/MODEL_BENCHMARK.md`) e é o que sustenta a afirmação de "Edge AI" diante da banca.
 */
enum class Stage(val key: String) {
    CAPTURE("capture_ms"),
    TRANSPORT("transport_ms"),
    STT("stt_ms"),
    OCR("ocr_ms"),
    BARCODE("barcode_ms"),
    VISION_INFERENCE("vision_inference_ms"),
    RULE_ENGINE("rule_engine_ms"),
    TTS_START("tts_start_ms"),
    END_TO_END("end_to_end_ms")
}

data class StageMetric(
    val interactionId: String,
    val stage: Stage,
    val latencyMs: Long,
    val providerId: String? = null,
    val modelVersion: String? = null,
    val success: Boolean = true,
    val errorCode: String? = null
)

interface MetricsRecorder {
    fun record(metric: StageMetric)
    fun snapshot(interactionId: String): List<StageMetric>
}

/**
 * Implementação de memória para o MVP.
 *
 * NFR-005 / checkpoint de privacidade do edital: guarda apenas números, identificadores de provider
 * e versões de modelo. Nunca a imagem, nunca o texto do rótulo, nunca dado do usuário.
 */
class InMemoryMetricsRecorder(private val maxInteractions: Int = 50) : MetricsRecorder {

    private val byInteraction = LinkedHashMap<String, MutableList<StageMetric>>()

    @Synchronized
    override fun record(metric: StageMetric) {
        val list = byInteraction.getOrPut(metric.interactionId) { mutableListOf() }
        list += metric
        while (byInteraction.size > maxInteractions) {
            val oldest = byInteraction.keys.first()
            byInteraction.remove(oldest)
        }
    }

    @Synchronized
    override fun snapshot(interactionId: String): List<StageMetric> =
        byInteraction[interactionId].orEmpty().toList()
}
