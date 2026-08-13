package com.eatcontrolai.domain.barcode

import com.eatcontrolai.core.model.Allergen
import com.eatcontrolai.core.model.ClaimPolarity
import com.eatcontrolai.core.model.DecisionState
import com.eatcontrolai.core.model.EvidenceType
import com.eatcontrolai.core.model.Restriction
import com.eatcontrolai.core.model.UserProfile
import com.eatcontrolai.domain.decision.FoodDecisionEngine
import com.eatcontrolai.glasses.Ean13Renderer
import com.eatcontrolai.glasses.MockScenes
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class BarcodeRepositoryTest {

    private val repository = BarcodeRepository()
    private val engine = FoodDecisionEngine()
    private val milkProfile = UserProfile(id = "t", restrictions = setOf(Restriction(Allergen.MILK)))

    @Test
    fun `encontra produto por EAN`() {
        val product = repository.findByEan("7891000100103")
        assertNotNull(product)
        assertEquals("Iogurte Natural Integral", product?.productName)
    }

    @Test
    fun `EAN fora do catalogo devolve nulo`() {
        assertNull(repository.findByEan("7899999999999"))
    }

    @Test
    fun `evidencia de barcode entra como base estruturada`() {
        val evidence = repository.toEvidence(repository.findByEan("7891000100103")!!)
        assertEquals(EvidenceType.BARCODE_DATABASE, evidence.type)
        assertEquals(3, evidence.type.rank)
    }

    @Test
    fun `afirmacoes vem do mesmo parser de rotulo`() {
        val product = repository.findByEan("7891000100103")!!
        val milk = product.claims.firstOrNull { it.allergen == Allergen.MILK }
        assertEquals(ClaimPolarity.CONTAINS, milk?.polarity)
    }

    @Test
    fun `iogurte zero lactose continua incompativel para restricao a leite`() {
        val product = repository.findByEan("7891000200100")!!
        val decision = engine.decide(milkProfile, listOf(repository.toEvidence(product)))
        assertEquals(DecisionState.INCOMPATIBLE, decision.state)
    }

    @Test
    fun `bebida de amendoa declara ausencia de leite`() {
        val product = repository.findByEan("7891000300107")!!
        val decision = engine.decide(milkProfile, listOf(repository.toEvidence(product)))
        assertEquals(DecisionState.COMPATIBLE, decision.state)
    }

    @Test
    fun `todo EAN do catalogo tem digito verificador valido`() {
        repository.all().forEach { product ->
            assertTrue(
                "EAN inválido no catálogo: ${product.ean} (${product.productName})",
                Ean13Renderer.isValid(product.ean)
            )
        }
    }

    @Test
    fun `toda cena de barcode tem EAN valido`() {
        MockScenes.barcodes.forEach { scene ->
            val ean = scene.ean
            assertNotNull("Cena ${scene.id} sem EAN", ean)
            assertTrue("EAN inválido na cena ${scene.id}: $ean", Ean13Renderer.isValid(ean!!))
        }
    }

    /**
     * O gabarito das cenas de barcode precisa bater com o que o motor realmente decide. Sem isso, o
     * benchmark mediria contra uma expectativa errada.
     */
    @Test
    fun `gabarito das cenas de barcode bate com a decisao do motor`() {
        MockScenes.barcodes.forEach { scene ->
            val product = repository.findByEan(scene.ean!!)
            val evidence = product?.let { listOf(repository.toEvidence(it)) } ?: emptyList()
            val actual = engine.decide(milkProfile, evidence).state
            assertEquals(
                "Cena ${scene.id} diverge do gabarito",
                scene.expectedForMilkProfile,
                actual
            )
        }
    }
}
