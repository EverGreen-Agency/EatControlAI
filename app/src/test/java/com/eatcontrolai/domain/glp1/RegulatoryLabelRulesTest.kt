package com.eatcontrolai.domain.glp1

import com.eatcontrolai.core.model.Nutrient
import com.eatcontrolai.core.model.NutrientAmount
import com.eatcontrolai.core.model.NutritionBasis
import com.eatcontrolai.core.model.NutritionFacts
import com.eatcontrolai.core.model.PortionInfo
import com.eatcontrolai.core.model.PortionUnit
import com.eatcontrolai.domain.plate.PlateFoodClass
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Protege o limite mais delicado do rule pack: o critério "ALTO EM" da rotulagem frontal vale para
 * alimento embalado, por 100 g ou 100 ml de composição **declarada**.
 *
 * Aplicar esse critério a prato estimado por foto produziria alerta com aparência regulatória e sem
 * base. Estes testes existem para que isso nunca aconteça por descuido.
 */
class RegulatoryLabelRulesTest {

    private val pack = Glp1RulePackV1()

    private fun solido(vararg amounts: NutrientAmount) = NutritionFacts(
        amounts = amounts.toList(),
        portion = PortionInfo(30.0, PortionUnit.GRAM, "porção de 30 g")
    )

    private fun liquido(vararg amounts: NutrientAmount) = NutritionFacts(
        amounts = amounts.toList(),
        portion = PortionInfo(200.0, PortionUnit.MILLILITER, "porção de 200 ml")
    )

    private fun codes(facts: NutritionFacts) =
        pack.evaluate(Glp1Context(facts = facts)).findings.map { it.code }

    @Test
    fun `sinaliza alto em sodio quando solido atinge o limite`() {
        val achados = codes(solido(NutrientAmount(Nutrient.SODIUM, 700.0, NutritionBasis.PER_100)))

        assertTrue(achados.contains(FindingCode.HIGH_IN_SODIUM))
    }

    @Test
    fun `nao sinaliza sodio de solido abaixo do limite`() {
        val achados = codes(solido(NutrientAmount(Nutrient.SODIUM, 500.0, NutritionBasis.PER_100)))

        assertFalse(achados.contains(FindingCode.HIGH_IN_SODIUM))
    }

    @Test
    fun `usa limite de liquido quando a porcao esta em mililitros`() {
        val valor = NutrientAmount(Nutrient.SODIUM, 350.0, NutritionBasis.PER_100)

        assertTrue(codes(liquido(valor)).contains(FindingCode.HIGH_IN_SODIUM))
        assertFalse(codes(solido(valor)).contains(FindingCode.HIGH_IN_SODIUM))
    }

    @Test
    fun `sinaliza alto em acucar adicionado e gordura saturada`() {
        val achados = codes(
            solido(
                NutrientAmount(Nutrient.ADDED_SUGARS, 20.0, NutritionBasis.PER_100),
                NutrientAmount(Nutrient.SATURATED_FAT, 8.0, NutritionBasis.PER_100)
            )
        )

        assertTrue(achados.contains(FindingCode.HIGH_IN_ADDED_SUGARS))
        assertTrue(achados.contains(FindingCode.HIGH_IN_SATURATED_FAT))
    }

    @Test
    fun `nao sinaliza acucar adicionado exatamente abaixo do limite`() {
        val achados = codes(
            solido(NutrientAmount(Nutrient.ADDED_SUGARS, 14.9, NutritionBasis.PER_100))
        )

        assertFalse(achados.contains(FindingCode.HIGH_IN_ADDED_SUGARS))
    }

    @Test
    fun `converte valor por porcao quando a massa da porcao esta declarada`() {
        // 300 mg em 30 g equivalem a 1000 mg por 100 g: acima do limite de sólidos.
        val achados = codes(
            solido(NutrientAmount(Nutrient.SODIUM, 300.0, NutritionBasis.PER_PORTION))
        )

        assertTrue(achados.contains(FindingCode.HIGH_IN_SODIUM))
    }

    @Test
    fun `nao emite achado regulatorio sem porcao declarada`() {
        val semPorcao = NutritionFacts(
            amounts = listOf(NutrientAmount(Nutrient.SODIUM, 900.0, NutritionBasis.PER_PORTION))
        )

        assertTrue(codes(semPorcao).isEmpty())
    }

    @Test
    fun `nao emite achado regulatorio quando a base e desconhecida`() {
        val baseIncerta = solido(NutrientAmount(Nutrient.SODIUM, 900.0, NutritionBasis.UNKNOWN))

        assertFalse(codes(baseIncerta).contains(FindingCode.HIGH_IN_SODIUM))
    }

    @Test
    fun `nunca aplica limite regulatorio a prato identificado por imagem`() {
        val avaliacao = pack.evaluate(
            Glp1Context(
                confirmedComponents = setOf(PlateFoodClass.FRIED_FOOD, PlateFoodClass.CHEESE),
                observedTerms = setOf("cremoso"),
                hasVisualInference = true
            )
        )

        val regulatorios = avaliacao.findings.filter { it.code.isRegulatory }
        assertEquals(emptyList<Finding>(), regulatorios)
    }

    @Test
    fun `achado regulatorio declara a origem como rotulo declarado`() {
        val avaliacao = pack.evaluate(
            Glp1Context(facts = solido(NutrientAmount(Nutrient.SODIUM, 700.0, NutritionBasis.PER_100)))
        )

        val achado = avaliacao.findings.first { it.code == FindingCode.HIGH_IN_SODIUM }
        assertEquals(Nutrient.SODIUM, achado.nutrient)
        assertTrue(achado.text.contains("rotulagem"))
    }
}
