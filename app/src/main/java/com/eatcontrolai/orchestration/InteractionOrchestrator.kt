package com.eatcontrolai.orchestration

import com.eatcontrolai.core.model.Decision
import com.eatcontrolai.core.model.UserProfile
import com.eatcontrolai.domain.decision.FoodDecisionEngine
import com.eatcontrolai.domain.evidence.EvidenceBuilder
import com.eatcontrolai.glasses.GlassesGateway
import com.eatcontrolai.inference.ModelRegistry
import com.eatcontrolai.metrics.MetricsRecorder
import com.eatcontrolai.metrics.Stage
import com.eatcontrolai.metrics.StageMetric
import java.util.UUID

data class InteractionResult(
    val interactionId: String,
    val decision: Decision,
    val recognizedText: String,
    val frameJpeg: ByteArray,
    val metrics: List<StageMetric>
) {
    // frameJpeg é ByteArray: equals/hashCode gerados por data class comparariam referência.
    override fun equals(other: Any?) = this === other
    override fun hashCode() = interactionId.hashCode()
}

/**
 * Costura a vertical do rótulo (UF-01 de `docs/USER_FLOWS.md`):
 *
 * `captura → OCR → parser → evidência → regra determinística → resposta curta → áudio`
 *
 * Cada etapa é cronometrada. A pipeline é a mesma independentemente de o frame vir dos óculos, da
 * câmera do celular ou do mock (`contexto-gpt.md` §13) — é o [GlassesGateway] que muda, não isto.
 */
class InteractionOrchestrator(
    private val glasses: GlassesGateway,
    private val models: ModelRegistry,
    private val decisionEngine: FoodDecisionEngine,
    private val metrics: MetricsRecorder
) {

    suspend fun analyzeLabel(profile: UserProfile, speakResult: Boolean = true): InteractionResult {
        val interactionId = UUID.randomUUID().toString()
        val startedAt = System.nanoTime()

        val captureStart = System.nanoTime()
        val frame = glasses.capturePhoto()
        record(interactionId, Stage.CAPTURE, sinceMs(captureStart), glasses.sourceId)

        val ocr = models.current().ocr.recognize(frame)
        record(interactionId, Stage.OCR, ocr.meta.latencyMs, ocr.meta.providerId, ocr.meta.version)

        val ruleStart = System.nanoTime()
        val evidence = EvidenceBuilder.fromLabelOcr(ocr.text, ocr.meta.providerId, ocr.meta.version)
        val decision = decisionEngine.decide(profile, evidence)
        record(interactionId, Stage.RULE_ENGINE, sinceMs(ruleStart), "deterministic_rules_v1")

        if (speakResult) {
            val ttsMeta = glasses.playSpeech(decision.shortMessage)
            record(interactionId, Stage.TTS_START, ttsMeta.latencyMs, ttsMeta.providerId, ttsMeta.version)
        }

        record(interactionId, Stage.END_TO_END, sinceMs(startedAt))

        return InteractionResult(
            interactionId = interactionId,
            decision = decision,
            recognizedText = ocr.text,
            frameJpeg = frame,
            metrics = metrics.snapshot(interactionId)
        )
    }

    private fun record(
        interactionId: String,
        stage: Stage,
        latencyMs: Long,
        providerId: String? = null,
        modelVersion: String? = null
    ) = metrics.record(
        StageMetric(
            interactionId = interactionId,
            stage = stage,
            latencyMs = latencyMs,
            providerId = providerId,
            modelVersion = modelVersion
        )
    )

    private fun sinceMs(startNanos: Long) = (System.nanoTime() - startNanos) / 1_000_000
}
