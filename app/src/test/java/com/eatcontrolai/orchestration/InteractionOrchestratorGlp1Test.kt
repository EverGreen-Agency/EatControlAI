package com.eatcontrolai.orchestration

import com.eatcontrolai.core.model.UserProfile
import com.eatcontrolai.domain.decision.FoodDecisionEngine
import com.eatcontrolai.domain.glp1.FindingCode
import com.eatcontrolai.domain.glp1.Glp1Context
import com.eatcontrolai.domain.glp1.Glp1RulePackV1
import com.eatcontrolai.domain.plate.PlateAnalysis
import com.eatcontrolai.domain.plate.PlateCandidate
import com.eatcontrolai.domain.plate.PlateFoodClass
import com.eatcontrolai.glasses.GlassesGateway
import com.eatcontrolai.glasses.GlassesStatus
import com.eatcontrolai.inference.InferenceMeta
import com.eatcontrolai.inference.ModelRegistry
import com.eatcontrolai.inference.OcrProvider
import com.eatcontrolai.inference.OcrResult
import com.eatcontrolai.inference.ProviderSet
import com.eatcontrolai.inference.TtsProvider
import com.eatcontrolai.metrics.InMemoryMetricsRecorder
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * O rule pack GLP-1 dentro da pipeline: ele roda na análise, entra no resultado e chega ao áudio.
 *
 * Antes desta ligação o pack existia no grafo de dependências e não era consultado por ninguém —
 * cobertura alta em código que nunca rodava em produção. Estes casos existem para que ele não volte
 * a ser desligado sem alguém perceber.
 */
class InteractionOrchestratorGlp1Test {

    private val meta = InferenceMeta("fake", "1", "jvm", 3)
    private val spoken = mutableListOf<String>()

    private val tts = object : TtsProvider {
        override suspend fun speak(text: String) = meta
    }

    private val glasses = object : GlassesGateway {
        override val sourceId = "fixture"
        override val isConnected = true
        override val status = GlassesStatus("fixture", true, true)
        override suspend fun connect() = Unit
        override suspend fun disconnect() = Unit
        override suspend fun capturePhoto() = byteArrayOf(1, 2, 3)
        override suspend fun startVoiceCapture() = byteArrayOf()
        override suspend fun playSpeech(text: String) = meta.also { spoken += text }
    }

    /** Rótulo com açúcares adicionados e gordura saturada acima do critério de rotulagem frontal. */
    private val rotuloAltoEm = """
        INFORMAÇÃO NUTRICIONAL
        Porção de 30 g (2 unidades)
        100 g | porção | %VD
        Carboidratos 60 g | 18 g | 6 %VD
        Açúcares adicionados 15 g | 4,5 g
        Proteínas 8 g | 2,4 g | 5 %VD
        Gorduras saturadas 9 g | 2,7 g | 12 %VD
        Sódio 400 mg | 120 mg | 5 %VD
    """.trimIndent()

    @Test
    fun `rotulo com contexto produz achados regulatorios e fala composta`() = runTest {
        val result = orchestrator(ocr(rotuloAltoEm)).analyze(
            AnalysisTrack.LABEL,
            UserProfile(id = "u"),
            glp1Context = Glp1Context()
        )

        val assessment = requireNotNull(result.glp1)
        assertEquals(Glp1RulePackV1.ID, assessment.packId)
        assertTrue(assessment.underReview)
        assertTrue(
            assessment.findings.map { it.code }.containsAll(
                listOf(FindingCode.HIGH_IN_ADDED_SUGARS, FindingCode.HIGH_IN_SATURATED_FAT)
            )
        )
        assertTrue(spoken.single().startsWith(result.decision.shortMessage))
        assertTrue(spoken.single().contains("Atenção"))
    }

    @Test
    fun `sem contexto da pessoa nao existe avaliacao e a fala e a do motor`() = runTest {
        val result = orchestrator(ocr(rotuloAltoEm)).analyze(AnalysisTrack.LABEL, UserProfile(id = "u"))

        assertNull(result.glp1)
        assertEquals(result.decision.shortMessage, spoken.single())
    }

    @Test
    fun `sem rule pack configurado o orquestrador continua respondendo`() = runTest {
        val result = orchestrator(ocr(rotuloAltoEm), rulePack = null).analyze(
            AnalysisTrack.LABEL,
            UserProfile(id = "u"),
            glp1Context = Glp1Context()
        )

        assertNull(result.glp1)
        assertEquals(result.decision.shortMessage, spoken.single())
    }

    @Test
    fun `cardapio nao atribui a pessoa o prato que ela nao escolheu`() = runTest {
        val cardapio = """
            PRATOS
            FRANGO GRELHADO R$ 42,00
            Arroz e salada
            ISCA DE PEIXE FRITA R$ 48,00
            Empanada, com molho cremoso
        """.trimIndent()

        val result = orchestrator(ocr(cardapio)).analyze(
            AnalysisTrack.MENU,
            UserProfile(id = "u"),
            glp1Context = Glp1Context()
        )

        val assessment = requireNotNull(result.glp1)
        assertFalse(
            "Fritura de uma opção não escolhida não pode virar achado",
            assessment.findings.any { it.code == FindingCode.PREPARATION_FRIED }
        )
    }

    @Test
    fun `candidato visual so conta depois de registrado`() {
        val analysis = PlateAnalysis(
            candidates = listOf(PlateCandidate("Fried food", 0.9f, PlateFoodClass.FRIED_FOOD)),
            providerId = "vision",
            providerVersion = "1",
            selectedComponents = setOf(PlateFoodClass.FRIED_FOOD)
        )

        val sugerido = glp1PerceptionOf(
            track = AnalysisTrack.PLATE,
            nutrition = com.eatcontrolai.core.model.NutritionFacts(),
            recognizedText = "",
            menuAnalysis = null,
            plateAnalysis = analysis
        )
        val confirmado = glp1PerceptionOf(
            track = AnalysisTrack.PLATE,
            nutrition = com.eatcontrolai.core.model.NutritionFacts(),
            recognizedText = "",
            menuAnalysis = null,
            plateAnalysis = analysis.copy(registered = true)
        )

        assertTrue(sugerido.confirmedComponents.isEmpty())
        assertTrue(sugerido.hasVisualInference)
        assertEquals(setOf(PlateFoodClass.FRIED_FOOD), confirmado.confirmedComponents)
    }

    private fun ocr(text: String) = object : OcrProvider {
        override suspend fun recognize(imageBytes: ByteArray) = OcrResult(text, meta)
    }

    private fun orchestrator(
        ocr: OcrProvider,
        rulePack: Glp1RulePackV1? = Glp1RulePackV1()
    ) = InteractionOrchestrator(
        glasses = glasses,
        models = ModelRegistry(ProviderSet(ocr = ocr, tts = tts)),
        decisionEngine = FoodDecisionEngine(),
        metrics = InMemoryMetricsRecorder(),
        rulePack = rulePack
    )
}
