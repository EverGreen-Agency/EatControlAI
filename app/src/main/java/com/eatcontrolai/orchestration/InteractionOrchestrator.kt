package com.eatcontrolai.orchestration

import com.eatcontrolai.core.model.Decision
import com.eatcontrolai.core.model.DecisionReason
import com.eatcontrolai.core.model.DecisionState
import com.eatcontrolai.core.model.Evidence
import com.eatcontrolai.core.model.EvidenceType
import com.eatcontrolai.core.model.NutritionFacts
import com.eatcontrolai.core.model.UserProfile
import com.eatcontrolai.domain.menu.MenuAnalysis
import com.eatcontrolai.domain.menu.MenuParser
import com.eatcontrolai.domain.nutrition.NutritionParser
import com.eatcontrolai.domain.plate.PlateAnalysis
import com.eatcontrolai.domain.plate.PlateLabelMapper
import com.eatcontrolai.domain.barcode.BarcodeRepository
import com.eatcontrolai.domain.decision.FoodDecisionEngine
import com.eatcontrolai.domain.evidence.EvidenceBuilder
import com.eatcontrolai.domain.glp1.Glp1Assessment
import com.eatcontrolai.domain.glp1.Glp1Context
import com.eatcontrolai.domain.glp1.Glp1Perception
import com.eatcontrolai.domain.glp1.Glp1SpokenLine
import com.eatcontrolai.domain.glp1.RulePack
import com.eatcontrolai.domain.glp1.withPerception
import com.eatcontrolai.domain.routing.ContextRouter
import com.eatcontrolai.glasses.Earcon
import com.eatcontrolai.glasses.GlassesGateway
import com.eatcontrolai.inference.ModelRegistry
import com.eatcontrolai.metrics.MetricsRecorder
import com.eatcontrolai.metrics.Stage
import com.eatcontrolai.metrics.StageMetric
import java.util.UUID

/** Trilhas de análise implementadas ponta a ponta. */
enum class AnalysisTrack { LABEL, BARCODE, MENU, PLATE }

data class InteractionResult(
    val interactionId: String,
    val track: AnalysisTrack,
    val decision: Decision,
    val recognizedText: String,
    val scannedEan: String?,
    val productName: String?,
    val frameJpeg: ByteArray,
    val metrics: List<StageMetric>,
    /** Por que a cascata escolheu esta trilha. Nulo quando a trilha foi escolhida à mão. */
    val routingReason: String? = null,
    /**
     * Tabela nutricional lida, quando existir.
     *
     * Deliberadamente **fora** de [decision]: número de macronutriente não decide compatibilidade
     * nem entra na hierarquia de evidência de alérgeno. É informação, não veredito.
     */
    val nutrition: NutritionFacts = NutritionFacts(),
    val menuAnalysis: MenuAnalysis? = null,
    val plateAnalysis: PlateAnalysis? = null,
    /**
     * Leitura do rule pack GLP-1 sobre esta interação.
     *
     * Nula quando não há rule pack configurado ou quando a camada de aplicação não forneceu o
     * contexto da pessoa — sem metas, restrições e regras cadastradas não existe avaliação GLP-1,
     * e inventar um contexto vazio produziria silêncio com cara de resposta.
     */
    val glp1: Glp1Assessment? = null
) {
    // frameJpeg é ByteArray: equals/hashCode gerados por data class comparariam referência.
    override fun equals(other: Any?) = this === other
    override fun hashCode() = interactionId.hashCode()
}

/**
 * A metade da percepção do contexto GLP-1, derivada do que a interação observou.
 *
 * Três recortes deliberados, e todos existem para não atribuir à pessoa o que ela não confirmou:
 *
 * 1. em cardápio, o texto livre **não** vira termo observado — a página inteira tem os pratos que
 *    ela não escolheu; valem apenas os termos da opção selecionada;
 * 2. componente de prato só conta depois de registrado, nunca como candidato do modelo;
 * 3. porção só existe quando informada.
 */
fun glp1PerceptionOf(
    track: AnalysisTrack,
    nutrition: NutritionFacts,
    recognizedText: String,
    menuAnalysis: MenuAnalysis?,
    plateAnalysis: PlateAnalysis?,
    confirmedPortions: Double? = null
): Glp1Perception = Glp1Perception(
    facts = nutrition,
    recognizedText = if (track == AnalysisTrack.MENU) "" else recognizedText,
    knownTerms = menuAnalysis?.selectedOption
        ?.takeIf { menuAnalysis.registered }
        ?.observedTerms
        ?.toSet()
        .orEmpty(),
    confirmedComponents = plateAnalysis?.takeIf { it.registered }?.selectedComponents.orEmpty(),
    confirmedPortions = confirmedPortions,
    hasVisualInference = plateAnalysis != null
)

/** A mesma derivação, para quando a interface recalcula depois de uma confirmação. */
fun InteractionResult.glp1Perception(confirmedPortions: Double? = null): Glp1Perception =
    glp1PerceptionOf(
        track = track,
        nutrition = nutrition,
        recognizedText = recognizedText,
        menuAnalysis = menuAnalysis,
        plateAnalysis = plateAnalysis,
        confirmedPortions = confirmedPortions
    )

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
    private val barcodeRepository: BarcodeRepository = BarcodeRepository(),
    private val earcon: Earcon = Earcon(),
    /**
     * Rule pack GLP-1, quando houver.
     *
     * Opcional porque as trilhas de percepção e o motor determinístico não dependem dele: um
     * orquestrador sem rule pack continua respondendo sobre conflito com o plano de restrições.
     */
    private val rulePack: RulePack? = null
) {

    /**
     * Deixa a cascata escolher a trilha, em vez de o usuário escolher o modo.
     *
     * Ordem por custo, como a palestra do Ideathon propõe: o leitor de barras é barato e roda
     * primeiro; se resolver, para por aí e o OCR nem é chamado. Só quando não há código de barras é
     * que se paga o OCR, e o [ContextRouter] então decide entre rótulo e "não sei".
     */
    suspend fun analyzeAuto(
        profile: UserProfile,
        speakResult: Boolean = true,
        glp1Context: Glp1Context? = null
    ): InteractionResult {
        val interactionId = UUID.randomUUID().toString()
        val startedAt = System.nanoTime()

        // Confirmação sonora antes de qualquer inferência: o usuário sabe na hora que foi ouvido.
        record(interactionId, Stage.EARCON, earcon.confirm())

        val captureStart = System.nanoTime()
        val frame = glasses.capturePhoto()
        record(interactionId, Stage.CAPTURE, sinceMs(captureStart), glasses.sourceId)

        val routeStart = System.nanoTime()
        val ean = models.current().barcode?.let { provider ->
            val scan = provider.decode(frame)
            record(interactionId, Stage.BARCODE, scan.meta.latencyMs, scan.meta.providerId, scan.meta.version)
            scan.rawValue
        }

        var routing = ContextRouter.route(ean, null)
        var perception: Perception? = null

        if (routing is ContextRouter.Decision.Product) {
            perception = resolveProduct(routing.ean)
        }

        // Sem produto resolvido, paga-se o OCR e decide-se de novo — agora com texto na mão.
        if (perception == null) {
            val ocr = models.current().ocr.recognize(frame)
            record(interactionId, Stage.OCR, ocr.meta.latencyMs, ocr.meta.providerId, ocr.meta.version)
            routing = ContextRouter.route(null, ocr.text)
            // A tabela nutricional é lida mesmo quando o roteador não classifica como rótulo: ela é
            // informação independente do veredito de compatibilidade.
            val nutrition = NutritionParser.parse(ocr.text)
            perception = when (routing) {
                is ContextRouter.Decision.Label -> Perception(
                    evidence = EvidenceBuilder.fromLabelOcr(ocr.text, ocr.meta.providerId, ocr.meta.version),
                    recognizedText = ocr.text,
                    nutrition = nutrition
                )

                is ContextRouter.Decision.Menu -> Perception(
                    evidence = emptyList(),
                    recognizedText = ocr.text,
                    nutrition = nutrition,
                    menuAnalysis = MenuParser.parse(ocr.text)
                )

                // Prato é o degrau mais caro da cascata: só chega aqui quem não tinha código de
                // barras nem texto analisável.
                is ContextRouter.Decision.Plate -> readPlate(interactionId, frame)
                    .copy(recognizedText = ocr.text, nutrition = nutrition)

                else -> Perception(
                    evidence = emptyList(),
                    recognizedText = ocr.text,
                    nutrition = nutrition
                )
            }
        }
        record(interactionId, Stage.ROUTING, sinceMs(routeStart), "context_router_v1")

        val ruleStart = System.nanoTime()
        // Cada trilha tem a sua régua: rótulo e produto passam pelo motor determinístico; cardápio e
        // prato passam por guardrails assistivos, que nunca afirmam compatibilidade.
        val decision = when (routing) {
            is ContextRouter.Decision.Menu ->
                menuDecision(requireNotNull(perception.menuAnalysis), perception.evidence)
            is ContextRouter.Decision.Plate ->
                plateDecision(requireNotNull(perception.plateAnalysis), perception.evidence)
            else -> decisionEngine.decide(profile, perception.evidence)
        }
        val ruleProvider = when (routing) {
            is ContextRouter.Decision.Menu, is ContextRouter.Decision.Plate -> "assistive_guardrails_v1"
            else -> "deterministic_rules_v1"
        }
        val assessment = assess(glp1Context, routing.track ?: AnalysisTrack.LABEL, perception)
        record(interactionId, Stage.RULE_ENGINE, sinceMs(ruleStart), ruleProvider)

        if (speakResult) {
            speakAndRecord(interactionId, startedAt, Glp1SpokenLine.compose(decision, assessment))
        }
        record(interactionId, Stage.END_TO_END, sinceMs(startedAt))

        return InteractionResult(
            interactionId = interactionId,
            track = routing.track ?: AnalysisTrack.LABEL,
            glp1 = assessment,
            decision = decision,
            recognizedText = perception.recognizedText,
            scannedEan = perception.ean ?: ean,
            productName = perception.productName,
            frameJpeg = frame,
            metrics = metrics.snapshot(interactionId),
            routingReason = routing.reason,
            menuAnalysis = perception.menuAnalysis,
            plateAnalysis = perception.plateAnalysis,
            nutrition = perception.nutrition
        )
    }

    private suspend fun resolveProduct(ean: String): Perception? {
        val product = barcodeRepository.lookup(ean) ?: return null
        return Perception(
            evidence = listOf(barcodeRepository.toEvidence(product)),
            recognizedText = product.ingredientsText,
            ean = ean,
            productName = product.productName,
            // Composição declarada pela base entra sem passar por OCR: é a evidência mais forte
            // que a trilha de produto consegue produzir.
            nutrition = product.nutrition
        )
    }

    private suspend fun speakAndRecord(interactionId: String, startedAt: Long, message: String) {
        val ttsMeta = glasses.playSpeech(message)
        record(interactionId, Stage.TTS_START, ttsMeta.latencyMs, ttsMeta.providerId, ttsMeta.version)
        // O que o usuário sente é isto, não a soma das etapas.
        record(interactionId, Stage.FIRST_AUDIO, sinceMs(startedAt))
    }

    suspend fun analyze(
        track: AnalysisTrack,
        profile: UserProfile,
        speakResult: Boolean = true,
        glp1Context: Glp1Context? = null
    ): InteractionResult {
        val interactionId = UUID.randomUUID().toString()
        val startedAt = System.nanoTime()

        val captureStart = System.nanoTime()
        val frame = glasses.capturePhoto()
        record(interactionId, Stage.CAPTURE, sinceMs(captureStart), glasses.sourceId)

        val perception = when (track) {
            AnalysisTrack.LABEL -> readLabel(interactionId, frame)
            AnalysisTrack.BARCODE -> readBarcode(interactionId, frame)
            AnalysisTrack.MENU -> readMenu(interactionId, frame)
            AnalysisTrack.PLATE -> readPlate(interactionId, frame)
        }

        val ruleStart = System.nanoTime()
        val decision = when (track) {
            AnalysisTrack.LABEL, AnalysisTrack.BARCODE ->
                decisionEngine.decide(profile, perception.evidence)
            AnalysisTrack.MENU -> menuDecision(requireNotNull(perception.menuAnalysis), perception.evidence)
            AnalysisTrack.PLATE -> plateDecision(requireNotNull(perception.plateAnalysis), perception.evidence)
        }
        val ruleProvider = if (track == AnalysisTrack.LABEL || track == AnalysisTrack.BARCODE) {
            "deterministic_rules_v1"
        } else {
            "assistive_guardrails_v1"
        }
        val assessment = assess(glp1Context, track, perception)
        record(interactionId, Stage.RULE_ENGINE, sinceMs(ruleStart), ruleProvider)

        if (speakResult) {
            speakAndRecord(interactionId, startedAt, Glp1SpokenLine.compose(decision, assessment))
        }

        record(interactionId, Stage.END_TO_END, sinceMs(startedAt))

        return InteractionResult(
            interactionId = interactionId,
            track = track,
            glp1 = assessment,
            decision = decision,
            recognizedText = perception.recognizedText,
            scannedEan = perception.ean,
            productName = perception.productName,
            frameJpeg = frame,
            metrics = metrics.snapshot(interactionId),
            nutrition = perception.nutrition,
            menuAnalysis = perception.menuAnalysis,
            plateAnalysis = perception.plateAnalysis
        )
    }

    /**
     * Avalia a interação pelo rule pack GLP-1.
     *
     * Devolve nulo quando falta o pack ou o contexto da pessoa — e nulo aqui significa "não avaliei",
     * não "está tudo bem". Quem consome distingue os dois casos.
     */
    private fun assess(
        seed: Glp1Context?,
        track: AnalysisTrack,
        perception: Perception
    ): Glp1Assessment? {
        val pack = rulePack ?: return null
        val context = seed ?: return null
        return pack.evaluate(
            context.withPerception(
                glp1PerceptionOf(
                    track = track,
                    nutrition = perception.nutrition,
                    recognizedText = perception.recognizedText,
                    menuAnalysis = perception.menuAnalysis,
                    plateAnalysis = perception.plateAnalysis
                )
            )
        )
    }

    private data class Perception(
        val evidence: List<Evidence>,
        val recognizedText: String = "",
        val ean: String? = null,
        val productName: String? = null,
        val nutrition: NutritionFacts = NutritionFacts(),
        val menuAnalysis: MenuAnalysis? = null,
        val plateAnalysis: PlateAnalysis? = null
    )

    private suspend fun readLabel(interactionId: String, frame: ByteArray): Perception {
        val ocr = models.current().ocr.recognize(frame)
        record(interactionId, Stage.OCR, ocr.meta.latencyMs, ocr.meta.providerId, ocr.meta.version)
        return Perception(
            evidence = EvidenceBuilder.fromLabelOcr(ocr.text, ocr.meta.providerId, ocr.meta.version),
            recognizedText = ocr.text,
            nutrition = NutritionParser.parse(ocr.text)
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

        val product = barcodeRepository.lookup(ean)
        if (product != null) {
            return Perception(
                evidence = listOf(barcodeRepository.toEvidence(product)),
                recognizedText = product.ingredientsText,
                ean = ean,
                productName = product.productName,
                nutrition = product.nutrition
            )
        }

        // EAN lido, produto desconhecido: sabemos a identidade, não a composição.
        val ocr = models.current().ocr.recognize(frame)
        record(interactionId, Stage.OCR, ocr.meta.latencyMs, ocr.meta.providerId, ocr.meta.version)
        return Perception(
            evidence = EvidenceBuilder.fromLabelOcr(ocr.text, ocr.meta.providerId, ocr.meta.version),
            recognizedText = ocr.text,
            ean = ean,
            nutrition = NutritionParser.parse(ocr.text)
        )
    }

    /** Cardápio tem parser próprio: texto de preço/descrição nunca passa como tabela nutricional. */
    private suspend fun readMenu(interactionId: String, frame: ByteArray): Perception {
        val ocr = models.current().ocr.recognize(frame)
        record(interactionId, Stage.OCR, ocr.meta.latencyMs, ocr.meta.providerId, ocr.meta.version)
        val analysis = MenuParser.parse(ocr.text)
        val evidence = if (ocr.text.isBlank()) {
            emptyList()
        } else {
            listOf(
                Evidence(
                    type = EvidenceType.OCR_TEXT,
                    value = ocr.text,
                    source = "OCR de cardápio · ${ocr.meta.providerId}",
                    modelVersion = ocr.meta.version
                )
            )
        }
        return Perception(
            evidence = evidence,
            recognizedText = ocr.text,
            menuAnalysis = analysis
        )
    }

    /** Visão fornece candidatos; a seleção do usuário é feita depois, na camada de interação. */
    private suspend fun readPlate(interactionId: String, frame: ByteArray): Perception {
        val detection = models.current().detector?.detect(frame)
        val analysis = if (detection == null) {
            PlateLabelMapper.unavailable()
        } else {
            record(
                interactionId,
                Stage.VISION_INFERENCE,
                detection.meta.latencyMs,
                detection.meta.providerId,
                detection.meta.version
            )
            PlateLabelMapper.map(detection)
        }
        val evidence = detection
            ?.takeIf { it.detections.isNotEmpty() }
            ?.let { result ->
                listOf(
                    Evidence(
                        type = EvidenceType.VISUAL_INFERENCE,
                        value = result.detections.joinToString { it.label },
                        source = result.meta.providerId,
                        confidence = result.detections.maxOfOrNull { it.confidence },
                        modelVersion = result.meta.version
                    )
                )
            }
            .orEmpty()
        val detectedDish = analysis.detectedDishName
            ?: detection?.detections?.firstOrNull()?.label?.replaceFirstChar { it.uppercase() }
        return Perception(
            evidence = evidence,
            recognizedText = detection?.detections
                ?.joinToString(separator = "\n") { "${it.label}: ${(it.confidence * 100).toInt()}%" }
                .orEmpty(),
            productName = detectedDish,
            plateAnalysis = analysis
        )
    }

    private fun menuDecision(analysis: MenuAnalysis, evidence: List<Evidence>): Decision =
        if (analysis.options.isEmpty()) {
            Decision(
                state = DecisionState.INSUFFICIENT_INFORMATION,
                shortMessage = "Não consegui estruturar opções deste cardápio.",
                evidence = evidence,
                reasons = listOf(
                    DecisionReason(
                        "O OCR não sustentou nome e descrição de uma opção revisável.",
                        EvidenceType.OCR_TEXT
                    )
                )
            )
        } else {
            Decision(
                state = DecisionState.NEEDS_CONFIRMATION,
                shortMessage = "Encontrei ${analysis.options.size} opções. Revise antes de registrar.",
                evidence = evidence,
                reasons = listOf(
                    DecisionReason(
                        "O cardápio informa texto, não receita completa, porção ou macros.",
                        EvidenceType.OCR_TEXT
                    )
                )
            )
        }

    private fun plateDecision(analysis: PlateAnalysis, evidence: List<Evidence>): Decision {
        val hasMappedCandidate = analysis.selectedComponents.isNotEmpty()
        return Decision(
            state = if (hasMappedCandidate) {
                DecisionState.NEEDS_CONFIRMATION
            } else {
                DecisionState.INSUFFICIENT_INFORMATION
            },
            shortMessage = if (hasMappedCandidate) {
                "Isto é só um palpite visual. Confirme os componentes."
            } else {
                "Não reconheci o prato. Selecione os componentes manualmente."
            },
            evidence = evidence,
            reasons = listOf(
                DecisionReason(
                    "Uma foto não determina receita, ingrediente oculto, quantidade ou macros.",
                    EvidenceType.VISUAL_INFERENCE
                )
            )
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
