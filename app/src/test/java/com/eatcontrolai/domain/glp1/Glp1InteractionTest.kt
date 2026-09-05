package com.eatcontrolai.domain.glp1

import com.eatcontrolai.core.model.Decision
import com.eatcontrolai.core.model.DecisionState
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
 * Cobre a costura entre a análise e o rule pack: como o contexto é montado e o que chega ao áudio.
 *
 * A regra que mais importa aqui é de prioridade, não de redação. Quando o motor determinístico diz
 * que existe conflito com o plano, é isso que a pessoa precisa ouvir; quando o rule pack manda
 * procurar avaliação médica, a análise alimentar para de existir. Empilhar as duas respostas seria
 * mais informação e menos decisão.
 */
class Glp1InteractionTest {

    private val pack = Glp1RulePackV1()

    private val altoEmSaturada = NutritionFacts(
        amounts = listOf(NutrientAmount(Nutrient.SATURATED_FAT, 9.0, NutritionBasis.PER_100)),
        portion = PortionInfo(30.0, PortionUnit.GRAM, "porção de 30 g")
    )

    private fun decision(state: DecisionState, message: String = "mensagem do motor") =
        Decision(state = state, shortMessage = message, evidence = emptyList())

    // ------------------------------------------------------------------ contexto

    @Test
    fun `percepcao soma termos do parser e termos do texto livre`() {
        val context = Glp1Context().withPerception(
            Glp1Perception(
                recognizedText = "Porção de batatas fritas",
                knownTerms = setOf("molho")
            )
        )

        assertEquals(setOf("molho", "frito"), context.observedTerms)
    }

    @Test
    fun `percepcao preserva o que a pessoa confirmou`() {
        val context = Glp1Context().withPerception(
            Glp1Perception(
                facts = altoEmSaturada,
                confirmedComponents = setOf(PlateFoodClass.SALAD),
                confirmedPortions = 2.0,
                hasVisualInference = true
            )
        )

        assertEquals(altoEmSaturada, context.facts)
        assertEquals(setOf(PlateFoodClass.SALAD), context.confirmedComponents)
        assertEquals(2.0, context.confirmedPortions!!, 0.001)
        assertTrue(context.hasVisualInference)
    }

    @Test
    fun `percepcao nova substitui a anterior em vez de acumular`() {
        val comFritura = Glp1Context().withPerception(
            Glp1Perception(recognizedText = "batata frita")
        )

        val depoisDaCorrecao = comFritura.withPerception(
            Glp1Perception(recognizedText = "frango grelhado")
        )

        assertEquals(setOf("grelhado"), depoisDaCorrecao.observedTerms)
    }

    // ------------------------------------------------------------------ silêncio

    @Test
    fun `sem meta sem regra e dentro dos limites o pack nao tem o que dizer`() {
        val assessment = pack.evaluate(
            Glp1Context().withPerception(
                Glp1Perception(
                    facts = NutritionFacts(
                        amounts = listOf(
                            NutrientAmount(Nutrient.SATURATED_FAT, 1.0, NutritionBasis.PER_100)
                        ),
                        portion = PortionInfo(30.0, PortionUnit.GRAM, "porção de 30 g")
                    )
                )
            )
        )

        assertFalse(assessment.hasSomethingToSay)
    }

    @Test
    fun `achado atencao encaminhamento e pergunta rompem o silencio`() {
        val comAchado = pack.evaluate(Glp1Context().withPerception(Glp1Perception(altoEmSaturada)))
        val comEncaminhamento = pack.evaluate(
            Glp1Context(symptomReports = listOf(SymptomKind.MUSCLE_WEAKNESS))
        )
        val suprimido = pack.evaluate(
            Glp1Context(symptomReports = listOf(SymptomKind.REPEATED_VOMITING))
        )

        assertTrue(comAchado.hasSomethingToSay)
        assertTrue(comEncaminhamento.hasSomethingToSay)
        assertTrue(suprimido.hasSomethingToSay)
    }

    // --------------------------------------------------------------------- fala

    @Test
    fun `sem rule pack a fala e a do motor deterministico`() {
        val decision = decision(DecisionState.COMPATIBLE)

        assertEquals(decision.shortMessage, Glp1SpokenLine.compose(decision, null))
    }

    @Test
    fun `encaminhamento medico suprime o veredito alimentar`() {
        val assessment = pack.evaluate(
            Glp1Context(symptomReports = listOf(SymptomKind.REPEATED_VOMITING))
        )

        val spoken = Glp1SpokenLine.compose(decision(DecisionState.COMPATIBLE), assessment)

        assertEquals(Glp1MessageComposer.MEDICAL_SHORT, spoken)
        assertFalse(spoken.contains("mensagem do motor"))
    }

    @Test
    fun `conflito com o plano tem precedencia sobre atencao nutricional`() {
        val assessment = pack.evaluate(Glp1Context().withPerception(Glp1Perception(altoEmSaturada)))
        val decision = decision(DecisionState.INCOMPATIBLE, "Conflito com o seu plano.")

        assertTrue(assessment.findings.any { it.kind == FindingKind.ATTENTION })
        assertEquals(decision.shortMessage, Glp1SpokenLine.compose(decision, assessment))
    }

    @Test
    fun `pergunta pendente tambem tem precedencia`() {
        val assessment = pack.evaluate(Glp1Context().withPerception(Glp1Perception(altoEmSaturada)))
        val decision = decision(DecisionState.NEEDS_CONFIRMATION, "Este preparo leva leite?")

        assertEquals(decision.shortMessage, Glp1SpokenLine.compose(decision, assessment))
    }

    @Test
    fun `atencao nutricional entra na fala quando o motor nao tem conflito`() {
        val assessment = pack.evaluate(Glp1Context().withPerception(Glp1Perception(altoEmSaturada)))
        val decision = decision(DecisionState.COMPATIBLE, "Não encontrei conflito.")

        val spoken = Glp1SpokenLine.compose(decision, assessment)

        assertTrue(spoken.startsWith("Não encontrei conflito."))
        assertTrue(spoken.contains("alto em ${Nutrient.SATURATED_FAT.displayName}"))
    }

    @Test
    fun `pergunta do rule pack chega ao audio quando nao ha achado`() {
        val assessment = pack.evaluate(
            Glp1Context(
                personalRules = listOf(
                    PersonalRule(
                        id = "r1",
                        target = "leite",
                        action = PersonalRuleAction.AVOID,
                        origin = PersonalRuleOrigin.REPORTED_DISCOMFORT,
                        possibleTerms = setOf("molho")
                    )
                )
            ).withPerception(Glp1Perception(recognizedText = "prato com molho"))
        )

        val spoken = Glp1SpokenLine.compose(
            decision(DecisionState.INSUFFICIENT_INFORMATION, "Não sei dizer."),
            assessment
        )

        assertTrue(assessment.findings.isEmpty())
        assertTrue(spoken.contains("Quer confirmar os ingredientes?"))
    }

    @Test
    fun `achado apenas positivo fica na tela e nao ocupa o audio`() {
        val assessment = pack.evaluate(
            Glp1Context().withPerception(
                Glp1Perception(confirmedComponents = setOf(PlateFoodClass.SALAD))
            )
        )
        val decision = decision(DecisionState.COMPATIBLE, "Não encontrei conflito.")

        assertEquals(
            listOf(FindingKind.POSITIVE),
            assessment.findings.map { it.kind }.distinct()
        )
        assertEquals(decision.shortMessage, Glp1SpokenLine.compose(decision, assessment))
    }

    @Test
    fun `nenhuma fala composta usa termo proibido`() {
        val assessments = listOf(
            pack.evaluate(Glp1Context().withPerception(Glp1Perception(altoEmSaturada))),
            pack.evaluate(Glp1Context(symptomReports = listOf(SymptomKind.REPEATED_VOMITING))),
            pack.evaluate(
                Glp1Context(symptomReports = listOf(SymptomKind.MUSCLE_WEAKNESS))
                    .withPerception(Glp1Perception(recognizedText = "molho cremoso"))
            )
        )

        DecisionState.entries.forEach { state ->
            assessments.forEach { assessment ->
                val spoken = Glp1SpokenLine.compose(decision(state, "Não encontrei conflito."), assessment)
                assertTrue(
                    "Termo proibido em: $spoken",
                    ForbiddenLexicon.isClean(spoken)
                )
            }
        }
    }
}
