package com.eatcontrolai.domain.glp1

import com.eatcontrolai.core.model.DecisionState
import com.eatcontrolai.core.model.MacroGoals
import com.eatcontrolai.core.model.MealRecord
import com.eatcontrolai.core.model.Nutrient
import com.eatcontrolai.core.model.NutrientAmount
import com.eatcontrolai.core.model.NutritionBasis
import com.eatcontrolai.domain.plate.PlateFoodClass
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.ZoneId

/**
 * Alertas baseados em histórico, aprovados como diferencial em `VAL-GLP1-R1`.
 *
 * A regra que estes testes protegem: o alerta compara com a meta **cadastrada** e cita desconforto
 * que a própria pessoa registrou. Ele não infere causa nem cria meta.
 */
class Glp1HistoryAlertsTest {

    private val zone = ZoneId.of("America/Sao_Paulo")

    /** 20/08/2026, 12:00 em São Paulo. */
    private val agora = 1787317200000L
    private val umDia = 86_400_000L

    private fun refeicao(
        id: String,
        diasAtras: Long,
        proteina: Double,
        itens: List<String> = emptyList()
    ) = MealRecord(
        id = id,
        timestampMillis = agora - diasAtras * umDia,
        title = "Refeição $id",
        decisionState = DecisionState.INSUFFICIENT_INFORMATION,
        shortMessage = "",
        recognizedText = "",
        evidenceLabels = emptyList(),
        endToEndMs = 0L,
        consumedNutrients = listOf(
            NutrientAmount(Nutrient.PROTEIN, proteina, NutritionBasis.PER_PORTION)
        ),
        confirmedItems = itens
    )

    @Test
    fun `alerta quando proteina fica abaixo da meta em dias recentes`() {
        val alertas = Glp1HistoryAlerts.evaluate(
            records = listOf(
                refeicao("d1", diasAtras = 1, proteina = 40.0),
                refeicao("d2", diasAtras = 2, proteina = 35.0),
                refeicao("d3", diasAtras = 3, proteina = 30.0)
            ),
            goals = MacroGoals(proteinG = 100.0),
            symptomReports = emptyList(),
            nowMillis = agora,
            zone = zone
        )

        val alerta = alertas.first { it.code == HistoryAlertCode.GOAL_BELOW_RECENT_DAYS }
        assertTrue(alerta.text.contains("proteínas"))
        assertTrue(alerta.text.contains("meta"))
    }

    @Test
    fun `nao alerta quando a meta foi atingida nos dias anteriores`() {
        val alertas = Glp1HistoryAlerts.evaluate(
            records = listOf(
                refeicao("d1", diasAtras = 1, proteina = 120.0),
                refeicao("d2", diasAtras = 2, proteina = 110.0),
                refeicao("d3", diasAtras = 3, proteina = 105.0)
            ),
            goals = MacroGoals(proteinG = 100.0),
            symptomReports = emptyList(),
            nowMillis = agora,
            zone = zone
        )

        assertTrue(alertas.none { it.code == HistoryAlertCode.GOAL_BELOW_RECENT_DAYS })
    }

    @Test
    fun `nao alerta sem meta cadastrada`() {
        val alertas = Glp1HistoryAlerts.evaluate(
            records = listOf(
                refeicao("d1", diasAtras = 1, proteina = 10.0),
                refeicao("d2", diasAtras = 2, proteina = 10.0),
                refeicao("d3", diasAtras = 3, proteina = 10.0)
            ),
            goals = MacroGoals(),
            symptomReports = emptyList(),
            nowMillis = agora,
            zone = zone
        )

        assertEquals(emptyList<HistoryAlert>(), alertas)
    }

    @Test
    fun `nao alerta com poucos dias abaixo da meta`() {
        val alertas = Glp1HistoryAlerts.evaluate(
            records = listOf(refeicao("d1", diasAtras = 1, proteina = 40.0)),
            goals = MacroGoals(proteinG = 100.0),
            symptomReports = emptyList(),
            nowMillis = agora,
            zone = zone
        )

        assertTrue(alertas.none { it.code == HistoryAlertCode.GOAL_BELOW_RECENT_DAYS })
    }

    @Test
    fun `ignora o dia de hoje que ainda esta em andamento`() {
        val alertas = Glp1HistoryAlerts.evaluate(
            records = listOf(
                refeicao("hoje", diasAtras = 0, proteina = 5.0),
                refeicao("d1", diasAtras = 1, proteina = 120.0),
                refeicao("d2", diasAtras = 2, proteina = 130.0),
                refeicao("d3", diasAtras = 3, proteina = 140.0)
            ),
            goals = MacroGoals(proteinG = 100.0),
            symptomReports = emptyList(),
            nowMillis = agora,
            zone = zone
        )

        assertTrue(alertas.none { it.code == HistoryAlertCode.GOAL_BELOW_RECENT_DAYS })
    }

    @Test
    fun `alerta desconforto registrado em refeicao com o mesmo componente`() {
        val registro = refeicao("ontem", diasAtras = 1, proteina = 50.0, itens = listOf("queijo"))

        val alertas = Glp1HistoryAlerts.evaluate(
            records = listOf(registro),
            goals = MacroGoals(),
            symptomReports = listOf(
                SymptomReport(
                    kind = SymptomKind.PERSISTENT_GI,
                    timestampMillis = registro.timestampMillis + 3_600_000L,
                    relatedRecordId = "ontem"
                )
            ),
            currentComponents = setOf(PlateFoodClass.CHEESE),
            nowMillis = agora,
            zone = zone
        )

        val alerta = alertas.first { it.code == HistoryAlertCode.DISCOMFORT_AFTER_SIMILAR_MEAL }
        assertTrue(alerta.text.contains("queijo"))
    }

    @Test
    fun `nao alerta desconforto quando o componente atual e diferente`() {
        val registro = refeicao("ontem", diasAtras = 1, proteina = 50.0, itens = listOf("queijo"))

        val alertas = Glp1HistoryAlerts.evaluate(
            records = listOf(registro),
            goals = MacroGoals(),
            symptomReports = listOf(
                SymptomReport(
                    kind = SymptomKind.PERSISTENT_GI,
                    timestampMillis = registro.timestampMillis,
                    relatedRecordId = "ontem"
                )
            ),
            currentComponents = setOf(PlateFoodClass.SALAD),
            nowMillis = agora,
            zone = zone
        )

        assertTrue(alertas.none { it.code == HistoryAlertCode.DISCOMFORT_AFTER_SIMILAR_MEAL })
    }

    @Test
    fun `desconforto sem refeicao vinculada nao gera alerta`() {
        val alertas = Glp1HistoryAlerts.evaluate(
            records = listOf(refeicao("ontem", diasAtras = 1, proteina = 50.0, itens = listOf("queijo"))),
            goals = MacroGoals(),
            symptomReports = listOf(
                SymptomReport(kind = SymptomKind.PERSISTENT_GI, timestampMillis = agora)
            ),
            currentComponents = setOf(PlateFoodClass.CHEESE),
            nowMillis = agora,
            zone = zone
        )

        assertTrue(alertas.none { it.code == HistoryAlertCode.DISCOMFORT_AFTER_SIMILAR_MEAL })
    }

    @Test
    fun `alertas nunca usam o lexico proibido`() {
        val registro = refeicao("ontem", diasAtras = 1, proteina = 20.0, itens = listOf("queijo"))
        val alertas = Glp1HistoryAlerts.evaluate(
            records = listOf(
                registro,
                refeicao("d2", diasAtras = 2, proteina = 20.0),
                refeicao("d3", diasAtras = 3, proteina = 20.0)
            ),
            goals = MacroGoals(proteinG = 120.0),
            symptomReports = listOf(
                SymptomReport(
                    kind = SymptomKind.PERSISTENT_GI,
                    timestampMillis = registro.timestampMillis,
                    relatedRecordId = "ontem"
                )
            ),
            currentComponents = setOf(PlateFoodClass.CHEESE),
            nowMillis = agora,
            zone = zone
        )

        assertTrue(alertas.isNotEmpty())
        alertas.forEach { alerta ->
            assertEquals(
                "Alerta com termo proibido: ${alerta.text}",
                emptyList<String>(),
                ForbiddenLexicon.violations(alerta.text)
            )
        }
    }
}
