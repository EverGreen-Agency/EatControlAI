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

    /**
     * Confirmação sonora imediata. A palestra do Ideathon é explícita: sem display, o usuário
     * interpreta silêncio como falha, e depois de 3 s ele repete o comando — gerando duas
     * requisições concorrentes. O "tic" no início compra tempo.
     */
    EARCON("earcon_ms"),

    /**
     * **Interação → primeira sílaba.** É a métrica que o usuário sente, e não a soma das etapas.
     * Alvo da palestra: abaixo de 1 s parece instantâneo; acima de 3 s o usuário repete.
     */
    FIRST_AUDIO("first_audio_ms"),

    /** Roteamento de contexto: qual trilha a cascata escolheu. */
    ROUTING("routing_ms"),

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
