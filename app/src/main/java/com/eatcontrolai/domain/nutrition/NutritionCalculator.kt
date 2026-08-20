package com.eatcontrolai.domain.nutrition

import com.eatcontrolai.core.model.GoalStatus
import com.eatcontrolai.core.model.MacroGoals
import com.eatcontrolai.core.model.Nutrient
import com.eatcontrolai.core.model.NutrientAmount
import com.eatcontrolai.core.model.NutrientProgress
import com.eatcontrolai.core.model.NutritionBasis
import com.eatcontrolai.core.model.NutritionFacts
import kotlin.math.abs

/**
 * Aritmética da camada nutricional. Nada aqui recomenda: tudo aqui é conta reproduzível.
 *
 * A conversão de `100 g/ml` para porção só acontece quando a porção foi realmente declarada no
 * rótulo. Sem porção, o valor não é convertido nem estimado — a UI mostra o dado por 100 g e diz
 * que não pode somar. Estimar a porção aqui seria inventar quantidade consumida.
 */
object NutritionCalculator {

    /** Tolerância para considerar uma meta atingida em vez de acima/abaixo. */
    private const val GOAL_TOLERANCE = 0.05

    /**
     * Quantidade efetivamente consumida, dado o número de porções confirmado pelo usuário.
     *
     * Prioriza o valor declarado por porção; recorre ao valor por 100 apenas quando há porção
     * declarada para fazer a regra de três.
     */
    fun consumed(facts: NutritionFacts, portions: Double): List<NutrientAmount> {
        if (portions <= 0.0) return emptyList()

        return Nutrient.entries.mapNotNull { nutrient ->
            val perPortion = facts.amountOf(nutrient, NutritionBasis.PER_PORTION)
            val base = perPortion ?: convertFrom100(facts, nutrient) ?: return@mapNotNull null

            NutrientAmount(
                nutrient = nutrient,
                value = base.value * portions,
                basis = NutritionBasis.PER_PORTION,
                sourceText = base.sourceText
            )
        }
    }

    /** Converte o valor por 100 g/ml em valor por porção declarada. */
    private fun convertFrom100(facts: NutritionFacts, nutrient: Nutrient): NutrientAmount? {
        val per100 = facts.amountOf(nutrient, NutritionBasis.PER_100) ?: return null
        val portion = facts.portion ?: return null
        return per100.copy(
            value = per100.value * portion.amount / 100.0,
            basis = NutritionBasis.PER_PORTION
        )
    }

    /** Soma consumos de várias refeições. Só agrega valores na mesma unidade do nutriente. */
    fun total(entries: List<NutrientAmount>): Map<Nutrient, Double> {
        val totals = LinkedHashMap<Nutrient, Double>()
        entries.filter { it.basis == NutritionBasis.PER_PORTION }.forEach { amount ->
            totals[amount.nutrient] = (totals[amount.nutrient] ?: 0.0) + amount.value
        }
        return totals
    }

    /**
     * Compara consumo com metas configuradas.
     *
     * Nutriente sem meta aparece como [GoalStatus.NO_GOAL] com o consumo informado — o usuário vê o
     * número, sem julgamento. É esta função que sustenta "faltam X g de proteína": é subtração
     * contra uma meta que o próprio usuário ou o profissional dele definiu.
     */
    fun progress(totals: Map<Nutrient, Double>, goals: MacroGoals): List<NutrientProgress> =
        Nutrient.entries.mapNotNull { nutrient ->
            val consumed = totals[nutrient]
            val goal = goals.goalFor(nutrient)
            if (consumed == null && goal == null) return@mapNotNull null

            NutrientProgress(
                nutrient = nutrient,
                consumed = consumed ?: 0.0,
                goal = goal,
                status = statusFor(consumed ?: 0.0, goal)
            )
        }

    private fun statusFor(consumed: Double, goal: Double?): GoalStatus {
        if (goal == null || goal <= 0.0) return GoalStatus.NO_GOAL
        val tolerance = goal * GOAL_TOLERANCE
        return when {
            abs(consumed - goal) <= tolerance -> GoalStatus.MET
            consumed < goal -> GoalStatus.BELOW
            else -> GoalStatus.ABOVE
        }
    }
}
