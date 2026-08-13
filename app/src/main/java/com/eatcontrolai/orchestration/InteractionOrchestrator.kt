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

/** Trilhas de análise implementadas ponta a ponta. */
enum class AnalysisTrack { LABEL, BARCODE }

data class InteractionResult(
    val interactionId: String,
    val track: AnalysisTrack,
    val decision: Decision,
    val recognizedText: String,
    val scannedEan: String?,
    val productName: String?,
    val frameJpeg: ByteArray,
    val metrics: List<StageMetric>
) {
    // frameJpeg é ByteArray: equals/hashCode gerados por data class comparariam referência.
    override fun equals(other: Any?) = this === other
    override fun hashCode() = interactionId.hashCode()
}

/**
 * Costura as verticais do produto (UF-01 rótulo e UF-02 barcode de `docs/USER_FLOWS.md`).
 *
 * A pipeline é a mesma independentemente de o frame vir dos óculos, da câmera do celular ou do mock
 * (`contexto-gpt.md` §13) — é o [GlassesGateway] que muda, não isto.
 */
class InteractionOrchestrator(
    private val glasses: GlassesGateway,
    private val models: ModelRegistry,
    private val decisionEngine: FoodDecisionEngine,
    private val metrics: MetricsRecorder,
    private val barcodeRepository: BarcodeRepository = BarcodeRepository()
) {

    suspend fun analyze(
        track: AnalysisTrack,
        profile: UserProfile,
        speakResult: Boolean = true
    ): InteractionResult {
        val interactionId = UUID.randomUUID().toString()
        val startedAt = System.nanoTime()

        val captureStart = System.nanoTime()
        val frame = glasses.capturePhoto()
        record(interactionId, Stage.CAPTURE, sinceMs(captureStart), glasses.sourceId)

        val perception = when (track) {
            AnalysisTrack.LABEL -> readLabel(interactionId, frame)
            AnalysisTrack.BARCODE -> readBarcode(interactionId, frame)
        }

        val ruleStart = System.nanoTime()
        val decision = decisionEngine.decide(profile, perception.evidence)
        record(interactionId, Stage.RULE_ENGINE, sinceMs(ruleStart), "deterministic_rules_v1")

        if (speakResult) {
            val ttsMeta = glasses.playSpeech(decision.shortMessage)
            record(interactionId, Stage.TTS_START, ttsMeta.latencyMs, ttsMeta.providerId, ttsMeta.version)
        }

        record(interactionId, Stage.END_TO_END, sinceMs(startedAt))

        return InteractionResult(
            interactionId = interactionId,
            track = track,
            decision = decision,
            recognizedText = perception.recognizedText,
            scannedEan = perception.ean,
            productName = perception.productName,
            frameJpeg = frame,
            metrics = metrics.snapshot(interactionId)
        )
    }

    private data class Perception(
        val evidence: List<Evidence>,
        val recognizedText: String = "",
        val ean: String? = null,
        val productName: String? = null
    )

    private suspend fun readLabel(interactionId: String, frame: ByteArray): Perception {
        val ocr = models.current().ocr.recognize(frame)
        record(interactionId, Stage.OCR, ocr.meta.latencyMs, ocr.meta.providerId, ocr.meta.version)
        return Perception(
            evidence = EvidenceBuilder.fromLabelOcr(ocr.text, ocr.meta.providerId, ocr.meta.version),
            recognizedText = ocr.text
        )
    }

    /**
     * Barcode primeiro, OCR como plano B.
     *
     * Quando o EAN resolve no catálogo, a composição vem de base estruturada — rank 3, acima de
     * qualquer OCR. Nesse caso não vale gastar a latência de OCR. Quando o EAN não resolve, o que
     * sobra é o que estiver escrito na embalagem, e aí o OCR entra.
     */
    private suspend fun readBarcode(interactionId: String, frame: ByteArray): Perception {
        val provider = models.current().barcode
            ?: return Perception(
                evidence = emptyList(),
                recognizedText = "Nenhum leitor de código de barras configurado."
            )

        val scan = provider.decode(frame)
        record(interactionId, Stage.BARCODE, scan.meta.latencyMs, scan.meta.providerId, scan.meta.version)

        val ean = scan.rawValue
        if (ean.isNullOrBlank()) {
            return Perception(evidence = emptyList(), recognizedText = "Nenhum código de barras legível.")
        }

        val product = barcodeRepository.findByEan(ean)
        if (product != null) {
            return Perception(
                evidence = listOf(barcodeRepository.toEvidence(product)),
                recognizedText = product.ingredientsText,
                ean = ean,
                productName = product.productName
            )
        }

        // EAN lido, produto desconhecido: sabemos a identidade, não a composição.
        val ocr = models.current().ocr.recognize(frame)
        record(interactionId, Stage.OCR, ocr.meta.latencyMs, ocr.meta.providerId, ocr.meta.version)
        return Perception(
            evidence = EvidenceBuilder.fromLabelOcr(ocr.text, ocr.meta.providerId, ocr.meta.version),
            recognizedText = ocr.text,
            ean = ean
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
