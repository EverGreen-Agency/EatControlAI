package com.eatcontrolai.orchestration

import com.eatcontrolai.core.model.Decision
import com.eatcontrolai.core.model.Evidence
import com.eatcontrolai.core.model.UserProfile
import com.eatcontrolai.domain.barcode.BarcodeRepository
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
    override fun equals(other: Any?) = this === other
    override fun hashCode() = interactionId.hashCode()
}

/**
 * Costura as verticais do produto (UF-01 Rótulo e UF-02 Barcode de `docs/USER_FLOWS.md`):
 *
 * `captura → OCR / Barcode → parser / lookup → evidência → regra determinística → resposta curta → áudio`
 */
class InteractionOrchestrator(
    private val glasses: GlassesGateway,
    private val models: ModelRegistry,
    private val decisionEngine: FoodDecisionEngine,
    private val metrics: MetricsRecorder,
    private val barcodeRepository: BarcodeRepository = BarcodeRepository()
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
        val evidenceList = mutableListOf<Evidence>()
        evidenceList.addAll(EvidenceBuilder.fromLabelOcr(ocr.text, ocr.meta.providerId, ocr.meta.version))

        // Se houver leitor de barcode ativo, tenta extrair barcode do frame
        val barcodeProvider = models.current().barcode
        if (barcodeProvider != null) {
            val barcodeResult = barcodeProvider.decode(frame)
            record(interactionId, Stage.BARCODE, barcodeResult.meta.latencyMs, barcodeResult.meta.providerId, barcodeResult.meta.version)
            if (!barcodeResult.rawValue.isNullOrBlank()) {
                val product = barcodeRepository.findByEan(barcodeResult.rawValue)
                if (product != null) {
                    evidenceList.add(barcodeRepository.toEvidence(product))
                }
            }
        }

        val decision = decisionEngine.decide(profile, evidenceList)
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
