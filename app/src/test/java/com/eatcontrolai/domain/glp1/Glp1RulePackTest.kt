package com.eatcontrolai.domain.glp1

import com.eatcontrolai.core.model.Allergen
import com.eatcontrolai.core.model.GoalStatus
import com.eatcontrolai.core.model.Nutrient
import com.eatcontrolai.core.model.NutrientProgress
import com.eatcontrolai.core.model.Restriction
import com.eatcontrolai.domain.plate.PlateFoodClass
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Verifica as regras não regulatórias de `glp1-rules-v1`: metas configuradas, atenções qualitativas,
 * perguntas de incerteza, regras pessoais e encaminhamento.
 *
 * Duas invariantes aparecem repetidas de propósito, porque são as que protegem o usuário: nenhuma
 * meta é inventada pelo aplicativo, e nenhuma imagem afirma ingrediente oculto.
 */
class Glp1RulePackTest {

    private val pack = Glp1RulePackV1()

    private fun progresso(
        nutrient: Nutrient,
        consumed: Double,
        goal: Double?,
        status: GoalStatus
    ) = NutrientProgress(nutrient, consumed, goal, status)

    // ------------------------------------------------------------------ metas

    @Test
    fun `informa quanto falta para a meta configurada`() {
        val avaliacao = pack.evaluate(
            Glp1Context(
                dailyProgress = listOf(progresso(Nutrient.PROTEIN, 75.0, 100.0, GoalStatus.BELOW))
            )
        )

        val achado = avaliacao.findings.first { it.code == FindingCode.GOAL_REMAINING }
        assertEquals(Nutrient.PROTEIN, achado.nutrient)
        assertTrue(achado.text.contains("25"))
    }

    @Test
    fun `trata sodio como limite configurado e nao como meta a alcancar`() {
        val avaliacao = pack.evaluate(
            Glp1Context(
                dailyProgress = listOf(progresso(Nutrient.SODIUM, 2500.0, 2000.0, GoalStatus.ABOVE))
            )
        )

        val codes = avaliacao.findings.map { it.code }
        assertTrue(codes.contains(FindingCode.CONFIGURED_LIMIT_EXCEEDED))
        assertFalse(codes.contains(FindingCode.GOAL_EXCEEDED))
    }

    @Test
    fun `sinaliza meta excedida para nutriente que nao e limite`() {
        val avaliacao = pack.evaluate(
            Glp1Context(
                dailyProgress = listOf(progresso(Nutrient.ENERGY, 2200.0, 1800.0, GoalStatus.ABOVE))
            )
        )

        assertTrue(avaliacao.findings.any { it.code == FindingCode.GOAL_EXCEEDED })
    }

    @Test
    fun `sem meta configurada nao emite julgamento`() {
        val avaliacao = pack.evaluate(
            Glp1Context(
                dailyProgress = listOf(progresso(Nutrient.PROTEIN, 40.0, null, GoalStatus.NO_GOAL))
            )
        )

        val codes = avaliacao.findings.map { it.code }
        assertFalse(codes.contains(FindingCode.GOAL_REMAINING))
        assertFalse(codes.contains(FindingCode.GOAL_EXCEEDED))
        assertFalse(codes.contains(FindingCode.CONFIGURED_LIMIT_EXCEEDED))
    }

    @Test
    fun `meta atingida nao vira alerta`() {
        val avaliacao = pack.evaluate(
            Glp1Context(
                dailyProgress = listOf(progresso(Nutrient.PROTEIN, 100.0, 100.0, GoalStatus.MET))
            )
        )

        assertTrue(avaliacao.findings.none { it.code == FindingCode.GOAL_REMAINING })
    }

    // ----------------------------------------------------------- qualitativas

    @Test
    fun `aponta fritura como ponto de atencao`() {
        val avaliacao = pack.evaluate(
            Glp1Context(confirmedComponents = setOf(PlateFoodClass.FRIED_FOOD))
        )

        assertTrue(avaliacao.findings.any { it.code == FindingCode.PREPARATION_FRIED })
    }

    @Test
    fun `aponta molho cremoso observado no texto`() {
        val avaliacao = pack.evaluate(Glp1Context(observedTerms = setOf("cremoso")))

        assertTrue(avaliacao.findings.any { it.code == FindingCode.CREAMY_SAUCE })
    }

    @Test
    fun `reconhece presenca de vegetais e de fonte proteica`() {
        val avaliacao = pack.evaluate(
            Glp1Context(
                confirmedComponents = setOf(PlateFoodClass.SALAD, PlateFoodClass.CHICKEN)
            )
        )

        val codes = avaliacao.findings.map { it.code }
        assertTrue(codes.contains(FindingCode.VEGETABLES_PRESENT))
        assertTrue(codes.contains(FindingCode.PROTEIN_PRESENT))
    }

    @Test
    fun `compara porcao confirmada com a porcao prevista no plano`() {
        val avaliacao = pack.evaluate(
            Glp1Context(confirmedPortions = 2.0, plannedPortions = 1.0)
        )

        assertTrue(avaliacao.findings.any { it.code == FindingCode.PORTION_ABOVE_PLAN })
    }

    @Test
    fun `sem plano cadastrado nao julga o tamanho da porcao`() {
        val avaliacao = pack.evaluate(Glp1Context(confirmedPortions = 3.0))

        assertTrue(avaliacao.findings.none { it.code == FindingCode.PORTION_ABOVE_PLAN })
    }

    @Test
    fun `porcao dentro do plano nao gera atencao`() {
        val avaliacao = pack.evaluate(
            Glp1Context(confirmedPortions = 1.0, plannedPortions = 1.0)
        )

        assertTrue(avaliacao.findings.none { it.code == FindingCode.PORTION_ABOVE_PLAN })
    }

    // -------------------------------------------------------------- incerteza

    @Test
    fun `molho com restricao de leite gera pergunta e nao afirmacao`() {
        val avaliacao = pack.evaluate(
            Glp1Context(
                observedTerms = setOf("molho"),
                restrictions = setOf(Restriction(Allergen.MILK)),
                hasVisualInference = true
            )
        )

        assertTrue(avaliacao.questions.any { it.contains("leite") })
        assertTrue(avaliacao.findings.none { it.text.contains("não contém") })
    }

    @Test
    fun `inferencia visual sem quantidade confirmada pede confirmacao`() {
        val avaliacao = pack.evaluate(
            Glp1Context(
                confirmedComponents = setOf(PlateFoodClass.RICE),
                hasVisualInference = true
            )
        )

        assertTrue(avaliacao.questions.any { it.contains("quantidade") })
    }

    @Test
    fun `quantidade confirmada remove a pergunta de quantidade`() {
        val avaliacao = pack.evaluate(
            Glp1Context(
                confirmedComponents = setOf(PlateFoodClass.RICE),
                confirmedPortions = 1.0,
                hasVisualInference = true
            )
        )

        assertTrue(avaliacao.questions.none { it.contains("quantidade") })
    }

    // --------------------------------------------------------- regras pessoais

    @Test
    fun `regra pessoal correspondente gera atencao citando o cadastro do usuario`() {
        val regra = PersonalRule(
            id = "leite",
            target = "derivados de leite",
            action = PersonalRuleAction.AVOID,
            origin = PersonalRuleOrigin.REPORTED_DISCOMFORT,
            matchClasses = setOf(PlateFoodClass.CHEESE)
        )

        val avaliacao = pack.evaluate(
            Glp1Context(
                confirmedComponents = setOf(PlateFoodClass.CHEESE),
                personalRules = listOf(regra)
            )
        )

        val achado = avaliacao.findings.first { it.code == FindingCode.PERSONAL_RULE_MATCH }
        assertTrue(achado.text.contains("derivados de leite"))
        assertTrue(achado.text.contains("queijo"))
    }

    @Test
    fun `correspondencia apenas possivel vira pergunta`() {
        val regra = PersonalRule(
            id = "leite",
            target = "derivados de leite",
            action = PersonalRuleAction.AVOID,
            origin = PersonalRuleOrigin.REPORTED_DISCOMFORT,
            possibleTerms = setOf("molho")
        )

        val avaliacao = pack.evaluate(
            Glp1Context(observedTerms = setOf("molho"), personalRules = listOf(regra))
        )

        assertTrue(avaliacao.findings.none { it.code == FindingCode.PERSONAL_RULE_MATCH })
        assertTrue(avaliacao.questions.any { it.contains("derivados de leite") })
    }

    @Test
    fun `regra pessoal nao cria meta nem limite numerico`() {
        val regra = PersonalRule(
            id = "leite",
            target = "derivados de leite",
            action = PersonalRuleAction.AVOID,
            origin = PersonalRuleOrigin.PREFERENCE,
            matchClasses = setOf(PlateFoodClass.CHEESE)
        )

        val avaliacao = pack.evaluate(
            Glp1Context(confirmedComponents = setOf(PlateFoodClass.CHEESE), personalRules = listOf(regra))
        )

        assertTrue(avaliacao.findings.none { it.code.isRegulatory })
        assertTrue(avaliacao.findings.none { it.code == FindingCode.CONFIGURED_LIMIT_EXCEEDED })
    }

    // ------------------------------------------------------------ encaminhamento

    @Test
    fun `vomito repetido interrompe a analise alimentar`() {
        val avaliacao = pack.evaluate(
            Glp1Context(
                confirmedComponents = setOf(PlateFoodClass.FRIED_FOOD),
                symptomReports = listOf(SymptomKind.REPEATED_VOMITING)
            )
        )

        assertEquals(EscalationLevel.MEDICAL_EVALUATION, avaliacao.escalation)
        assertTrue(avaliacao.analysisSuppressed)
        assertTrue(avaliacao.findings.isEmpty())
        assertTrue(avaliacao.shortMessage.contains("avaliação médica"))
    }

    @Test
    fun `sintoma persistente orienta profissional sem interromper a analise`() {
        val avaliacao = pack.evaluate(
            Glp1Context(
                confirmedComponents = setOf(PlateFoodClass.SALAD),
                symptomReports = listOf(SymptomKind.PERSISTENT_GI)
            )
        )

        assertEquals(EscalationLevel.PROFESSIONAL, avaliacao.escalation)
        assertFalse(avaliacao.analysisSuppressed)
        assertTrue(avaliacao.findings.isNotEmpty())
    }

    @Test
    fun `muitas exclusoes cadastradas escalonam para profissional`() {
        val regras = (1..8).map { index ->
            PersonalRule(
                id = "regra-$index",
                target = "item $index",
                action = PersonalRuleAction.AVOID,
                origin = PersonalRuleOrigin.PREFERENCE
            )
        }

        val avaliacao = pack.evaluate(Glp1Context(personalRules = regras))

        assertEquals(EscalationLevel.PROFESSIONAL, avaliacao.escalation)
    }

    @Test
    fun `poucas exclusoes nao escalonam`() {
        val regras = listOf(
            PersonalRule("a", "leite", PersonalRuleAction.AVOID, PersonalRuleOrigin.PREFERENCE),
            PersonalRule("b", "glúten", PersonalRuleAction.OBSERVE, PersonalRuleOrigin.PREFERENCE)
        )

        val avaliacao = pack.evaluate(Glp1Context(personalRules = regras))

        assertEquals(EscalationLevel.NONE, avaliacao.escalation)
    }

    @Test
    fun `nivel dois tem precedencia sobre nivel um`() {
        val avaliacao = pack.evaluate(
            Glp1Context(
                symptomReports = listOf(SymptomKind.PERSISTENT_GI, SymptomKind.CANNOT_KEEP_LIQUIDS)
            )
        )

        assertEquals(EscalationLevel.MEDICAL_EVALUATION, avaliacao.escalation)
    }

    // -------------------------------------------------------------- metadados

    @Test
    fun `expõe a versao do pack e o estado de revisao`() {
        val avaliacao = pack.evaluate(Glp1Context())

        assertEquals("glp1-rules-v1", avaliacao.packId)
        assertTrue(avaliacao.underReview)
    }
}
