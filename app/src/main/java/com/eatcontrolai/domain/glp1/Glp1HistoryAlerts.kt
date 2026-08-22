package com.eatcontrolai.domain.glp1

import com.eatcontrolai.core.model.MacroGoals
import com.eatcontrolai.core.model.MealRecord
import com.eatcontrolai.core.model.Nutrient
import com.eatcontrolai.domain.plate.PlateFoodClass
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import kotlin.math.roundToLong

/**
 * Alertas de histórico aprovados em `VAL-GLP1-R1` como diferencial do produto.
 *
 * Duas fronteiras deliberadas:
 *
 * 1. a comparação é sempre contra a meta **cadastrada**; sem meta, não há alerta;
 * 2. desconforto só entra quando a própria pessoa registrou e vinculou a uma refeição. O aplicativo
 *    não atribui causa — ele lembra o que ela mesma anotou.
 */
object Glp1HistoryAlerts {

    /** Dias completos abaixo da meta necessários para o alerta aparecer. */
    const val MIN_DAYS_BELOW = 3

    /** Janela de dias completos considerada, sem contar o dia em andamento. */
    const val WINDOW_DAYS = 7

    fun evaluate(
        records: List<MealRecord>,
        goals: MacroGoals,
        symptomReports: List<SymptomReport>,
        currentComponents: Set<PlateFoodClass> = emptySet(),
        nowMillis: Long,
        zone: ZoneId = ZoneId.systemDefault()
    ): List<HistoryAlert> = buildList {
        proteinAlert(records, goals, nowMillis, zone)?.let(::add)
        discomfortAlert(records, symptomReports, currentComponents)?.let(::add)
    }

    /**
     * Proteína abaixo da meta em dias completos recentes.
     *
     * O dia de hoje é ignorado de propósito: ele ainda está em andamento e apontar défice no meio da
     * tarde seria alarme falso.
     */
    private fun proteinAlert(
        records: List<MealRecord>,
        goals: MacroGoals,
        nowMillis: Long,
        zone: ZoneId
    ): HistoryAlert? {
        val goal = goals.proteinG?.takeIf { it > 0.0 } ?: return null
        val today = dayOf(nowMillis, zone)

        val totalsByDay = records
            .asSequence()
            .filter { it.consumedNutrients.isNotEmpty() }
            .map { record -> dayOf(record.timestampMillis, zone) to record }
            .filter { (day, _) -> day < today && day >= today.minusDays(WINDOW_DAYS.toLong()) }
            .groupBy({ it.first }, { it.second })
            .mapValues { (_, dayRecords) ->
                dayRecords.sumOf { record ->
                    record.consumedNutrients
                        .filter { it.nutrient == Nutrient.PROTEIN }
                        .sumOf { it.value }
                }
            }

        val daysBelow = totalsByDay.count { (_, total) -> total < goal }
        if (daysBelow < MIN_DAYS_BELOW) return null

        return HistoryAlert(
            code = HistoryAlertCode.GOAL_BELOW_RECENT_DAYS,
            text = "Em $daysBelow dos últimos dias o consumo de ${Nutrient.PROTEIN.displayName} " +
                "ficou abaixo da sua meta cadastrada de ${format(goal)} " +
                "${Nutrient.PROTEIN.unit.symbol}."
        )
    }

    /**
     * Desconforto que a pessoa registrou em refeição com o mesmo componente da escolha atual.
     *
     * Correspondência por nome do componente confirmado, não por inferência: o vínculo precisa existir
     * no registro dela.
     */
    private fun discomfortAlert(
        records: List<MealRecord>,
        symptomReports: List<SymptomReport>,
        currentComponents: Set<PlateFoodClass>
    ): HistoryAlert? {
        if (currentComponents.isEmpty()) return null

        val relatedIds = symptomReports.mapNotNull { it.relatedRecordId }.toSet()
        if (relatedIds.isEmpty()) return null

        val currentNames = currentComponents.map { it.displayName }.toSet()
        val matched = records
            .filter { it.id in relatedIds }
            .flatMap { it.confirmedItems }
            .map { it.lowercase() }
            .filter { item -> currentNames.any { name -> item.contains(name) } }
            .distinct()

        if (matched.isEmpty()) return null

        return HistoryAlert(
            code = HistoryAlertCode.DISCOMFORT_AFTER_SIMILAR_MEAL,
            text = "Você registrou desconforto após refeição com ${matched.joinToString()}. " +
                "Vale observar esta escolha."
        )
    }

    private fun dayOf(millis: Long, zone: ZoneId): LocalDate =
        Instant.ofEpochMilli(millis).atZone(zone).toLocalDate()

    private fun format(value: Double): String = value.roundToLong().toString()
}

enum class HistoryAlertCode { GOAL_BELOW_RECENT_DAYS, DISCOMFORT_AFTER_SIMILAR_MEAL }

data class HistoryAlert(val code: HistoryAlertCode, val text: String)
