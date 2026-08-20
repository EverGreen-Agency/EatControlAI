package com.eatcontrolai.orchestration

import com.eatcontrolai.core.model.DecisionState
import com.eatcontrolai.core.model.EvidenceType
import com.eatcontrolai.core.model.UserProfile
import com.eatcontrolai.domain.plate.PlateFoodClass
import com.eatcontrolai.glasses.GlassesGateway
import com.eatcontrolai.glasses.GlassesStatus
import com.eatcontrolai.inference.Detection
import com.eatcontrolai.inference.DetectionResult
import com.eatcontrolai.inference.InferenceMeta
import com.eatcontrolai.inference.ModelRegistry
import com.eatcontrolai.inference.ObjectDetectionProvider
import com.eatcontrolai.inference.OcrProvider
import com.eatcontrolai.inference.OcrResult
import com.eatcontrolai.inference.ProviderSet
import com.eatcontrolai.inference.TtsProvider
import com.eatcontrolai.metrics.InMemoryMetricsRecorder
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class InteractionOrchestratorAssistedTest {

    private val meta = InferenceMeta("fake", "1", "jvm", 3)
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
        override suspend fun playSpeech(text: String) = meta
    }

    @Test
    fun `menu usa OCR e parser proprio sem criar tabela nutricional`() = runTest {
        val ocr = object : OcrProvider {
            override suspend fun recognize(imageBytes: ByteArray) = OcrResult(
                "PRATOS\nFRANGO GRELHADO R$ 42,00\nArroz e salada",
                meta
            )
        }
        val orchestrator = orchestrator(ocr = ocr)

        val result = orchestrator.analyze(
            AnalysisTrack.MENU,
            UserProfile(id = "u"),
            speakResult = false
        )

        assertEquals(AnalysisTrack.MENU, result.track)
        assertEquals(DecisionState.NEEDS_CONFIRMATION, result.decision.state)
        assertEquals("FRANGO GRELHADO", result.menuAnalysis?.options?.single()?.name)
        assertTrue(result.nutrition.isEmpty)
        assertEquals(EvidenceType.OCR_TEXT, result.decision.evidence.single().type)
        assertNull(result.plateAnalysis)
    }

    @Test
    fun `prato usa detector sem chamar OCR e entrega somente candidatos`() = runTest {
        val ocr = object : OcrProvider {
            override suspend fun recognize(imageBytes: ByteArray): OcrResult =
                error("OCR não deve executar na trilha PLATE")
        }
        val detector = object : ObjectDetectionProvider {
            override suspend fun detect(imageBytes: ByteArray) = DetectionResult(
                listOf(Detection("Chicken", 0.9f), Detection("Food", 0.99f)),
                meta.copy(providerId = "vision")
            )
        }
        val result = orchestrator(ocr, detector).analyze(
            AnalysisTrack.PLATE,
            UserProfile(id = "u"),
            speakResult = false
        )

        assertEquals(DecisionState.NEEDS_CONFIRMATION, result.decision.state)
        assertEquals(setOf(PlateFoodClass.CHICKEN), result.plateAnalysis?.selectedComponents)
        assertEquals(EvidenceType.VISUAL_INFERENCE, result.decision.evidence.single().type)
        assertTrue(result.nutrition.isEmpty)
        assertNull(result.menuAnalysis)
        assertTrue(result.metrics.any { it.stage.key == "vision_inference_ms" })
    }

    @Test
    fun `prato sem detector permanece manual e insuficiente`() = runTest {
        val ocr = object : OcrProvider {
            override suspend fun recognize(imageBytes: ByteArray) = OcrResult("", meta)
        }
        val result = orchestrator(ocr).analyze(
            AnalysisTrack.PLATE,
            UserProfile(id = "u"),
            speakResult = false
        )

        assertEquals(DecisionState.INSUFFICIENT_INFORMATION, result.decision.state)
        assertNotNull(result.plateAnalysis)
        assertTrue(result.plateAnalysis!!.candidates.isEmpty())
        assertTrue(result.decision.evidence.isEmpty())
        assertFalse(result.metrics.any { it.stage.key == "vision_inference_ms" })
    }

    private fun orchestrator(
        ocr: OcrProvider,
        detector: ObjectDetectionProvider? = null
    ) = InteractionOrchestrator(
        glasses = glasses,
        models = ModelRegistry(ProviderSet(ocr = ocr, tts = tts, detector = detector)),
        decisionEngine = com.eatcontrolai.domain.decision.FoodDecisionEngine(),
        metrics = InMemoryMetricsRecorder()
    )
}
