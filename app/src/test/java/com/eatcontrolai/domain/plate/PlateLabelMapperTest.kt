package com.eatcontrolai.domain.plate

import com.eatcontrolai.inference.Detection
import com.eatcontrolai.inference.DetectionResult
import com.eatcontrolai.inference.InferenceMeta
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PlateLabelMapperTest {

    private fun result(vararg detections: Detection) = DetectionResult(
        detections = detections.toList(),
        meta = InferenceMeta("fake-labeler", "1", "jvm", 7)
    )

    @Test
    fun `mapeia apenas classes fechadas acima do gate`() {
        val analysis = PlateLabelMapper.map(
            result(
                Detection("Food", 0.99f),
                Detection("Chicken", 0.91f),
                Detection("Rice", 0.84f),
                Detection("Salad", 0.60f)
            )
        )

        assertEquals("fake-labeler", analysis.providerId)
        assertEquals(setOf(PlateFoodClass.CHICKEN, PlateFoodClass.RICE), analysis.selectedComponents)
        assertNull(analysis.candidates.first { it.rawLabel == "Food" }.foodClass)
        assertNull(analysis.candidates.first { it.rawLabel == "Salad" }.foodClass)
        assertTrue(analysis.warnings.any { it.contains("macros") })
    }

    @Test
    fun `classe desconhecida nunca e forcada para alimento conhecido`() {
        val analysis = PlateLabelMapper.map(result(Detection("Tableware", 0.95f)))

        assertTrue(analysis.selectedComponents.isEmpty())
        assertNull(analysis.candidates.single().foodClass)
        assertTrue(analysis.warnings.any { it.contains("classes fechadas") })
    }

    @Test
    fun `normaliza idioma acento e sinonimos`() {
        assertEquals(PlateFoodClass.BEANS, PlateLabelMapper.classFor("Feijão caseiro"))
        assertEquals(PlateFoodClass.FRIED_FOOD, PlateLabelMapper.classFor("French fries"))
        assertEquals(PlateFoodClass.DESSERT, PlateLabelMapper.classFor("Chocolate cake"))
        assertEquals(PlateFoodClass.EGG, PlateLabelMapper.classFor("Omelet"))
        assertNull(PlateLabelMapper.classFor("Dining table"))
    }

    @Test
    fun `deduplica sugestoes da mesma classe e limita candidatos`() {
        val detections = (1..12).map { index ->
            Detection(if (index % 2 == 0) "Chicken" else "Poultry", 0.99f - index / 100f)
        }
        val analysis = PlateLabelMapper.map(result(*detections.toTypedArray()))

        assertEquals(8, analysis.candidates.size)
        assertEquals(setOf(PlateFoodClass.CHICKEN), analysis.selectedComponents)
        assertTrue(analysis.candidates.zipWithNext().all { (a, b) -> a.confidence >= b.confidence })
    }

    @Test
    fun `sem deteccao orienta selecao manual`() {
        val analysis = PlateLabelMapper.map(result())

        assertTrue(analysis.candidates.isEmpty())
        assertTrue(analysis.selectedComponents.isEmpty())
        assertTrue(analysis.warnings.any { it.contains("manualmente") })
    }

    @Test
    fun `provider indisponivel nao simula candidato`() {
        val analysis = PlateLabelMapper.unavailable()

        assertEquals("not_configured", analysis.providerId)
        assertTrue(analysis.candidates.isEmpty())
        assertTrue(analysis.warnings.any { it.contains("indisponível") })
    }

    @Test
    fun `confidence e limitada ao intervalo exibivel`() {
        val analysis = PlateLabelMapper.map(
            result(Detection("Rice", 2.0f), Detection("Bean", -1.0f))
        )

        assertEquals(1.0f, analysis.candidates[0].confidence, 0.0f)
        assertEquals(0.0f, analysis.candidates[1].confidence, 0.0f)
    }
}
