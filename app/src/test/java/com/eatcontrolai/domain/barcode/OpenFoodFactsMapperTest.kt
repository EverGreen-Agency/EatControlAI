package com.eatcontrolai.domain.barcode

import com.eatcontrolai.core.model.Allergen
import com.eatcontrolai.core.model.ClaimPolarity
import com.eatcontrolai.core.model.DecisionState
import com.eatcontrolai.core.model.Nutrient
import com.eatcontrolai.core.model.NutritionBasis
import com.eatcontrolai.core.model.Restriction
import com.eatcontrolai.core.model.UserProfile
import com.eatcontrolai.domain.decision.FoodDecisionEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Protege a tradução da base pública para o modelo do produto.
 *
 * O caso mais perigoso do arquivo é o do sódio: o Open Food Facts publica em gramas e o modelo usa
 * miligramas. Errar essa conversão por mil não quebra nada visivelmente — só faz produto salgado
 * parecer inofensivo, que é o pior modo de falha possível nesta camada.
 */
class OpenFoodFactsMapperTest {

    private val engine = FoodDecisionEngine()

    private fun json(product: String) = """{"status":1,"code":"789","product":$product}"""

    @Test
    fun `produto desconhecido devolve nulo`() {
        assertNull(OpenFoodFactsMapper.map("""{"status":0}""", "789"))
    }

    @Test
    fun `json invalido devolve nulo em vez de explodir`() {
        assertNull(OpenFoodFactsMapper.map("nao é json", "789"))
    }

    @Test
    fun `registro sem nome devolve nulo`() {
        assertNull(OpenFoodFactsMapper.map(json("""{"brands":"Marca"}"""), "789"))
    }

    @Test
    fun `prefere o nome em portugues quando existe`() {
        val product = OpenFoodFactsMapper.map(
            json("""{"product_name":"Whole Milk","product_name_pt":"Leite Integral"}"""),
            "789"
        )
        assertEquals("Leite Integral", product?.productName)
    }

    @Test
    fun `converte sodio de gramas para miligramas`() {
        val product = OpenFoodFactsMapper.map(
            json("""{"product_name":"Salgadinho","nutriments":{"sodium_100g":0.8}}"""),
            "789"
        )
        val sodium = product?.nutrition?.amountOf(Nutrient.SODIUM, NutritionBasis.PER_100)
        assertEquals(800.0, sodium!!.value, 0.001)
    }

    @Test
    fun `nutriente ausente nao vira zero`() {
        val product = OpenFoodFactsMapper.map(
            json("""{"product_name":"Água","nutriments":{"proteins_100g":0}}"""),
            "789"
        )
        val nutrition = product!!.nutrition
        assertEquals(
            0.0,
            nutrition.amountOf(Nutrient.PROTEIN, NutritionBasis.PER_100)!!.value,
            0.001
        )
        assertNull(nutrition.amountOf(Nutrient.SODIUM, NutritionBasis.PER_100))
        assertNull(nutrition.amountOf(Nutrient.ENERGY, NutritionBasis.PER_100))
    }

    @Test
    fun `produto sem nutriments fica com tabela vazia`() {
        val product = OpenFoodFactsMapper.map(json("""{"product_name":"Item"}"""), "789")
        assertTrue(product!!.nutrition.isEmpty)
    }

    @Test
    fun `nutriments sem nenhum campo conhecido fica vazio`() {
        val product = OpenFoodFactsMapper.map(
            json("""{"product_name":"Item","nutriments":{"alcohol_100g":2}}"""),
            "789"
        )
        assertTrue(product!!.nutrition.isEmpty)
    }

    @Test
    fun `alergeno declarado vira afirmacao pelo mesmo parser de rotulo`() {
        val product = OpenFoodFactsMapper.map(
            json(
                """{"product_name":"Biscoito","ingredients_text_pt":"farinha, açúcar",
                   "allergens_tags":["en:milk","en:gluten"]}"""
            ),
            "789"
        )
        val claims = product!!.claims
        assertEquals(
            ClaimPolarity.CONTAINS,
            claims.firstOrNull { it.allergen == Allergen.MILK }?.polarity
        )
        assertEquals(
            ClaimPolarity.CONTAINS,
            claims.firstOrNull { it.allergen == Allergen.GLUTEN }?.polarity
        )
    }

    @Test
    fun `traco declarado vira pode conter e nao contem`() {
        val product = OpenFoodFactsMapper.map(
            json("""{"product_name":"Barra","traces_tags":["en:peanuts"]}"""),
            "789"
        )
        val peanut = product!!.claims.firstOrNull { it.allergen == Allergen.PEANUT }
        assertEquals(ClaimPolarity.MAY_CONTAIN, peanut?.polarity)
    }

    @Test
    fun `tag desconhecida e preservada em vez de descartada`() {
        val product = OpenFoodFactsMapper.map(
            json("""{"product_name":"Item","allergens_tags":["en:lupin"]}"""),
            "789"
        )
        assertTrue(product!!.ingredientsText.contains("lupin"))
    }

    @Test
    fun `produto da base decide como evidencia estruturada`() {
        val product = OpenFoodFactsMapper.map(
            json("""{"product_name":"Iogurte","allergens_tags":["en:milk"]}"""),
            "7891000100103"
        )!!
        val repository = BarcodeRepository()
        val evidence = repository.toEvidence(product)

        assertTrue(evidence.source.contains(OpenFoodFactsMapper.SOURCE))
        val decision = engine.decide(
            UserProfile(id = "t", restrictions = setOf(Restriction(Allergen.MILK))),
            listOf(evidence)
        )
        assertEquals(DecisionState.INCOMPATIBLE, decision.state)
    }

    @Test
    fun `marca ausente nao vira a palavra null`() {
        val product = OpenFoodFactsMapper.map(
            json("""{"product_name":"Item","brands":null}"""),
            "789"
        )
        assertNotNull(product)
        assertEquals("", product!!.brand)
    }
}
