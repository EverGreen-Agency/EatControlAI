package com.eatcontrolai.domain.glp1

import com.eatcontrolai.core.model.Allergen
import com.eatcontrolai.core.model.GoalStatus
import com.eatcontrolai.core.model.Nutrient
import com.eatcontrolai.core.model.NutrientAmount
import com.eatcontrolai.core.model.NutrientProgress
import com.eatcontrolai.core.model.NutritionBasis
import com.eatcontrolai.core.model.NutritionFacts
import com.eatcontrolai.core.model.PortionInfo
import com.eatcontrolai.core.model.PortionUnit
import com.eatcontrolai.core.model.Restriction
import com.eatcontrolai.domain.plate.PlateFoodClass
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Protege a redação aprovada na validação clínica: identificar, contextualizar, orientar.
 *
 * O teste de léxico proibido é o mais importante do arquivo. Ele roda sobre **todas** as saídas
 * geradas por uma bateria de contextos, então uma frase nova com palavra proibida quebra o build em
 * vez de chegar ao usuário.
 */
class Glp1MessageComposerTest {

    private val pack = Glp1RulePackV1()

    private val contextos: List<Glp1Context> = listOf(
        Glp1Context(),
        Glp1Context(
            facts = NutritionFacts(
                amounts = listOf(
                    NutrientAmount(Nutrient.SODIUM, 800.0, NutritionBasis.PER_100),
                    NutrientAmount(Nutrient.SATURATED_FAT, 9.0, NutritionBasis.PER_100),
                    NutrientAmount(Nutrient.ADDED_SUGARS, 18.0, NutritionBasis.PER_100)
                ),
                portion = PortionInfo(40.0, PortionUnit.GRAM, "porção de 40 g")
            )
        ),
        Glp1Context(
            dailyProgress = listOf(
                NutrientProgress(Nutrient.PROTEIN, 75.0, 100.0, GoalStatus.BELOW),
                NutrientProgress(Nutrient.SODIUM, 2400.0, 2000.0, GoalStatus.ABOVE)
            )
        ),
        Glp1Context(
            confirmedComponents = setOf(
                PlateFoodClass.FRIED_FOOD,
                PlateFoodClass.CHEESE,
                PlateFoodClass.SALAD,
                PlateFoodClass.CHICKEN
            ),
            observedTerms = setOf("cremoso", "molho"),
            restrictions = setOf(Restriction(Allergen.MILK)),
            confirmedPortions = 2.0,
            plannedPortions = 1.0,
            hasVisualInference = true
        ),
        Glp1Context(
            personalRules = listOf(
                PersonalRule(
                    id = "leite",
                    target = "derivados de leite",
                    action = PersonalRuleAction.AVOID,
                    origin = PersonalRuleOrigin.REPORTED_DISCOMFORT,
                    matchClasses = setOf(PlateFoodClass.CHEESE),
                    possibleTerms = setOf("molho")
                )
            ),
            confirmedComponents = setOf(PlateFoodClass.CHEESE),
            observedTerms = setOf("molho")
        ),
        Glp1Context(symptomReports = listOf(SymptomKind.PERSISTENT_GI)),
        Glp1Context(symptomReports = listOf(SymptomKind.REPEATED_VOMITING)),
        Glp1Context(symptomReports = listOf(SymptomKind.PREGNANCY))
    )

    @Test
    fun `frase principal respeita o limite de palavras para audio`() {
        contextos.forEach { contexto ->
            val avaliacao = pack.evaluate(contexto)
            val palavras = avaliacao.shortMessage.trim().split(Regex("\\s+")).count { it.isNotBlank() }

            assertTrue(
                "Frase longa demais para áudio: ${avaliacao.shortMessage}",
                palavras <= Glp1MessageComposer.MAX_WORDS
            )
        }
    }

    @Test
    fun `nenhuma saida usa o lexico proibido`() {
        contextos.forEach { contexto ->
            val avaliacao = pack.evaluate(contexto)
            val textos = buildList {
                add(avaliacao.shortMessage)
                addAll(avaliacao.detail)
                addAll(avaliacao.questions)
                addAll(avaliacao.findings.map { it.text })
            }

            textos.forEach { texto ->
                assertEquals(
                    "Texto com termo proibido: $texto",
                    emptyList<String>(),
                    ForbiddenLexicon.violations(texto)
                )
            }
        }
    }

    @Test
    fun `meta de proteina vem acompanhada da ressalva de exercicio`() {
        val avaliacao = pack.evaluate(
            Glp1Context(
                dailyProgress = listOf(NutrientProgress(Nutrient.PROTEIN, 60.0, 100.0, GoalStatus.BELOW))
            )
        )

        assertTrue(avaliacao.detail.any { it.contains("exercício de força") })
    }

    @Test
    fun `sem meta de proteina nao repete a ressalva de exercicio`() {
        val avaliacao = pack.evaluate(
            Glp1Context(confirmedComponents = setOf(PlateFoodClass.SALAD))
        )

        assertTrue(avaliacao.detail.none { it.contains("exercício de força") })
    }

    @Test
    fun `estado de revisao aparece no detalhe enquanto falta registro formal`() {
        val avaliacao = pack.evaluate(Glp1Context())

        assertTrue(avaliacao.detail.any { it.contains("em revisão") })
    }

    @Test
    fun `pack registrado nao anuncia revisao`() {
        val registrado = Glp1RulePackV1(underReview = false)

        val avaliacao = registrado.evaluate(Glp1Context())

        assertTrue(avaliacao.detail.none { it.contains("em revisão") })
    }

    @Test
    fun `atencao tem precedencia sobre achado positivo na frase curta`() {
        val avaliacao = pack.evaluate(
            Glp1Context(
                confirmedComponents = setOf(
                    PlateFoodClass.SALAD,
                    PlateFoodClass.CHICKEN,
                    PlateFoodClass.FRIED_FOOD
                )
            )
        )

        assertTrue(avaliacao.shortMessage.contains("fritura"))
    }

    @Test
    fun `sem nenhum achado a frase declara ausencia de informacao suficiente`() {
        val avaliacao = pack.evaluate(Glp1Context())

        assertTrue(avaliacao.shortMessage.isNotBlank())
        assertTrue(avaliacao.findings.isEmpty())
    }

    @Test
    fun `detecta termos proibidos em texto arbitrario`() {
        assertEquals(listOf("seguro"), ForbiddenLexicon.violations("Este alimento é seguro"))
        assertEquals(listOf("vai causar"), ForbiddenLexicon.violations("Isso vai causar desconforto"))
        assertEquals(listOf("contraindicado"), ForbiddenLexicon.violations("Contraindicado para você"))
    }

    @Test
    fun `nao confunde palavra legitima com termo proibido`() {
        assertEquals(emptyList<String>(), ForbiddenLexicon.violations("tratamento com GLP-1"))
        assertEquals(emptyList<String>(), ForbiddenLexicon.violations("curador de conteúdo"))
    }
}
