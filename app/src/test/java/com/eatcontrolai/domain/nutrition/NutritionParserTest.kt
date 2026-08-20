package com.eatcontrolai.domain.nutrition

import com.eatcontrolai.core.model.Nutrient
import com.eatcontrolai.core.model.NutritionBasis
import com.eatcontrolai.core.model.PortionUnit
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Verifica a leitura da tabela nutricional no formato brasileiro (IN 75/2020).
 *
 * O que estes testes protegem não é "o parser roda": é a diferença entre `por 100 g` e `por porção`.
 * Trocar as duas colunas produziria totais errados com aparência perfeitamente plausível — o pior
 * modo de falha desta camada.
 */
class NutritionParserTest {

    private val duasColunas = """
        INFORMAÇÃO NUTRICIONAL
        Porção de 30 g (2 unidades)
        100 g | porção | %VD
        Valor energético 450 kcal (1882 kJ) | 135 kcal (565 kJ) | 7 %VD
        Carboidratos 60 g | 18 g | 6 %VD
        Açúcares totais 20 g | 6 g
        Açúcares adicionados 15 g | 4,5 g
        Proteínas 8 g | 2,4 g | 5 %VD
        Gorduras totais 18 g | 5,4 g | 10 %VD
        Gorduras saturadas 9 g | 2,7 g | 12 %VD
        Gorduras trans 0 g | 0 g
        Fibra alimentar 3 g | 0,9 g | 4 %VD
        Sódio 400 mg | 120 mg | 5 %VD
    """.trimIndent()

    @Test
    fun `le porcao declarada`() {
        val facts = NutritionParser.parse(duasColunas)

        val portion = facts.portion
        assertNotNull("A porção deveria ter sido identificada", portion)
        assertEquals(30.0, portion!!.amount, 0.001)
        assertEquals(PortionUnit.GRAM, portion.unit)
    }

    @Test
    fun `separa coluna de 100 g da coluna de porcao`() {
        val facts = NutritionParser.parse(duasColunas)

        assertEquals(
            8.0,
            facts.amountOf(Nutrient.PROTEIN, NutritionBasis.PER_100)!!.value,
            0.001
        )
        assertEquals(
            2.4,
            facts.amountOf(Nutrient.PROTEIN, NutritionBasis.PER_PORTION)!!.value,
            0.001
        )
    }

    @Test
    fun `ignora percentual de valor diario`() {
        val facts = NutritionParser.parse(duasColunas)

        // 5 %VD da proteína não pode virar quantidade em nenhuma base.
        val proteinValues = facts.amounts
            .filter { it.nutrient == Nutrient.PROTEIN }
            .map { it.value }
        assertEquals(listOf(8.0, 2.4), proteinValues)
    }

    @Test
    fun `registra energia apenas em kcal`() {
        val facts = NutritionParser.parse(duasColunas)

        assertEquals(
            450.0,
            facts.amountOf(Nutrient.ENERGY, NutritionBasis.PER_100)!!.value,
            0.001
        )
        assertEquals(
            135.0,
            facts.amountOf(Nutrient.ENERGY, NutritionBasis.PER_PORTION)!!.value,
            0.001
        )
        // 1882 kJ e 565 kJ não entram: somar kJ com kcal seria erro de unidade.
        assertTrue(facts.amounts.none { it.value == 1882.0 || it.value == 565.0 })
    }

    @Test
    fun `distingue gordura total de saturada e trans`() {
        val facts = NutritionParser.parse(duasColunas)

        assertEquals(
            18.0,
            facts.amountOf(Nutrient.TOTAL_FAT, NutritionBasis.PER_100)!!.value,
            0.001
        )
        assertEquals(
            9.0,
            facts.amountOf(Nutrient.SATURATED_FAT, NutritionBasis.PER_100)!!.value,
            0.001
        )
        assertEquals(
            0.0,
            facts.amountOf(Nutrient.TRANS_FAT, NutritionBasis.PER_100)!!.value,
            0.001
        )
    }

    @Test
    fun `distingue acucares totais de adicionados`() {
        val facts = NutritionParser.parse(duasColunas)

        assertEquals(
            20.0,
            facts.amountOf(Nutrient.TOTAL_SUGARS, NutritionBasis.PER_100)!!.value,
            0.001
        )
        assertEquals(
            15.0,
            facts.amountOf(Nutrient.ADDED_SUGARS, NutritionBasis.PER_100)!!.value,
            0.001
        )
    }

    @Test
    fun `le sodio em miligramas`() {
        val facts = NutritionParser.parse(duasColunas)

        assertEquals(
            120.0,
            facts.amountOf(Nutrient.SODIUM, NutritionBasis.PER_PORTION)!!.value,
            0.001
        )
    }

    @Test
    fun `coluna unica com porcao declarada assume base da porcao e avisa`() {
        val texto = """
            Informação nutricional
            Porção de 200 ml
            Valor energético 90 kcal
            Proteínas 7,2 g
            Gorduras totais 4,8 g
            Carboidratos 9 g
        """.trimIndent()

        val facts = NutritionParser.parse(texto)

        assertEquals(
            7.2,
            facts.amountOf(Nutrient.PROTEIN, NutritionBasis.PER_PORTION)!!.value,
            0.001
        )
        assertEquals(PortionUnit.MILLILITER, facts.portion!!.unit)
        assertTrue(
            "A suposição de base precisa ficar visível para o usuário",
            facts.warnings.any { it.contains("cabeçalho", ignoreCase = true) }
        )
    }

    @Test
    fun `texto sem tabela devolve resultado vazio`() {
        val facts = NutritionParser.parse("BISCOITO RECHEADO SABOR BAUNILHA")

        assertTrue(facts.isEmpty)
        assertTrue(facts.usableAmounts.isEmpty())
        assertNull(facts.portion)
    }

    @Test
    fun `sem porcao nem cabecalho os valores nao entram em conta`() {
        val texto = """
            Proteínas 7,2 g
            Carboidratos 9 g
        """.trimIndent()

        val facts = NutritionParser.parse(texto)

        assertTrue("Sem base declarada nada pode ser somado", facts.usableAmounts.isEmpty())
        assertTrue(facts.amounts.all { it.basis == NutritionBasis.UNKNOWN })
        assertTrue(facts.warnings.isNotEmpty())
    }

    @Test
    fun `cobertura de leitura cai quando a porcao nao e declarada`() {
        val completa = NutritionParser.parse(duasColunas)
        val semPorcao = NutritionParser.parse(
            """
            100 g
            Valor energético 450 kcal
            Carboidratos 60 g
            Proteínas 8 g
            Gorduras totais 18 g
            """.trimIndent()
        )

        assertEquals(1.0f, completa.readingCoverage, 0.001f)
        assertTrue(semPorcao.readingCoverage < completa.readingCoverage)
    }
}
