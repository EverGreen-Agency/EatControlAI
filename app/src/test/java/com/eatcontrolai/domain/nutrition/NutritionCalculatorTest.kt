package com.eatcontrolai.domain.nutrition

import com.eatcontrolai.core.model.GoalSource
import com.eatcontrolai.core.model.GoalStatus
import com.eatcontrolai.core.model.MacroGoals
import com.eatcontrolai.core.model.Nutrient
import com.eatcontrolai.core.model.NutrientAmount
import com.eatcontrolai.core.model.NutritionBasis
import com.eatcontrolai.core.model.NutritionFacts
import com.eatcontrolai.core.model.PortionInfo
import com.eatcontrolai.core.model.PortionUnit
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Verifica a aritmética da camada nutricional.
 *
 * A regra que estes testes protegem: valor por 100 g só é convertido em consumo quando a porção foi
 * declarada. Estimar porção seria inventar quantidade consumida.
 */
class NutritionCalculatorTest {

    private val porPorcao = NutritionFacts(
        amounts = listOf(
            NutrientAmount(Nutrient.PROTEIN, 10.0, NutritionBasis.PER_PORTION),
            NutrientAmount(Nutrient.CARBOHYDRATE, 20.0, NutritionBasis.PER_PORTION),
            NutrientAmount(Nutrient.ENERGY, 150.0, NutritionBasis.PER_PORTION)
        ),
        portion = PortionInfo(50.0, PortionUnit.GRAM, "porção de 50 g")
    )

    @Test
    fun `multiplica pelo numero de porcoes confirmado`() {
        val consumido = NutritionCalculator.consumed(porPorcao, portions = 2.0)
        val totais = NutritionCalculator.total(consumido)

        assertEquals(20.0, totais[Nutrient.PROTEIN]!!, 0.001)
        assertEquals(40.0, totais[Nutrient.CARBOHYDRATE]!!, 0.001)
        assertEquals(300.0, totais[Nutrient.ENERGY]!!, 0.001)
    }

    @Test
    fun `aceita fracao de porcao`() {
        val consumido = NutritionCalculator.consumed(porPorcao, portions = 0.5)

        assertEquals(
            5.0,
            consumido.first { it.nutrient == Nutrient.PROTEIN }.value,
            0.001
        )
    }

    @Test
    fun `converte valor por 100 g usando a porcao declarada`() {
        val facts = NutritionFacts(
            amounts = listOf(NutrientAmount(Nutrient.PROTEIN, 8.0, NutritionBasis.PER_100)),
            portion = PortionInfo(25.0, PortionUnit.GRAM, "porção de 25 g")
        )

        val consumido = NutritionCalculator.consumed(facts, portions = 1.0)

        assertEquals(2.0, consumido.single().value, 0.001)
        assertEquals(NutritionBasis.PER_PORTION, consumido.single().basis)
    }

    @Test
    fun `sem porcao declarada nao converte valor por 100 g`() {
        val facts = NutritionFacts(
            amounts = listOf(NutrientAmount(Nutrient.PROTEIN, 8.0, NutritionBasis.PER_100))
        )

        assertTrue(NutritionCalculator.consumed(facts, portions = 1.0).isEmpty())
    }

    @Test
    fun `porcoes invalidas nao produzem consumo`() {
        assertTrue(NutritionCalculator.consumed(porPorcao, portions = 0.0).isEmpty())
        assertTrue(NutritionCalculator.consumed(porPorcao, portions = -1.0).isEmpty())
    }

    @Test
    fun `soma refeicoes diferentes no total do dia`() {
        val manha = NutritionCalculator.consumed(porPorcao, portions = 1.0)
        val tarde = NutritionCalculator.consumed(porPorcao, portions = 2.0)

        val totais = NutritionCalculator.total(manha + tarde)

        assertEquals(30.0, totais[Nutrient.PROTEIN]!!, 0.001)
    }

    @Test
    fun `informa quanto falta para a meta configurada`() {
        val goals = MacroGoals(proteinG = 90.0, definedBy = GoalSource.USER)
        val progresso = NutritionCalculator.progress(mapOf(Nutrient.PROTEIN to 40.0), goals)

        val proteina = progresso.single()
        assertEquals(GoalStatus.BELOW, proteina.status)
        assertEquals(50.0, proteina.remaining!!, 0.001)
        assertEquals(0.0, proteina.excess!!, 0.001)
    }

    @Test
    fun `informa quanto excedeu a meta configurada`() {
        val goals = MacroGoals(fatG = 60.0, definedBy = GoalSource.HEALTH_PROFESSIONAL)
        val progresso = NutritionCalculator.progress(mapOf(Nutrient.TOTAL_FAT to 75.0), goals)

        val gordura = progresso.single()
        assertEquals(GoalStatus.ABOVE, gordura.status)
        assertEquals(15.0, gordura.excess!!, 0.001)
        assertEquals(0.0, gordura.remaining!!, 0.001)
    }

    @Test
    fun `meta atingida dentro da tolerancia`() {
        val goals = MacroGoals(proteinG = 100.0, definedBy = GoalSource.USER)
        val progresso = NutritionCalculator.progress(mapOf(Nutrient.PROTEIN to 98.0), goals)

        assertEquals(GoalStatus.MET, progresso.single().status)
    }

    @Test
    fun `sem meta configurada apenas informa o consumo`() {
        val progresso = NutritionCalculator.progress(mapOf(Nutrient.PROTEIN to 40.0), MacroGoals())

        val proteina = progresso.single()
        assertEquals(GoalStatus.NO_GOAL, proteina.status)
        assertEquals(40.0, proteina.consumed, 0.001)
        assertNull("Sem meta não existe 'faltando'", proteina.remaining)
    }

    @Test
    fun `meta configurada aparece mesmo sem consumo`() {
        val progresso = NutritionCalculator.progress(
            totals = emptyMap(),
            goals = MacroGoals(proteinG = 90.0, definedBy = GoalSource.USER)
        )

        val proteina = progresso.single()
        assertEquals(Nutrient.PROTEIN, proteina.nutrient)
        assertEquals(0.0, proteina.consumed, 0.001)
        assertEquals(90.0, proteina.remaining!!, 0.001)
        assertEquals(GoalStatus.BELOW, proteina.status)
    }
}
