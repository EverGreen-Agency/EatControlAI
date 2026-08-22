package com.eatcontrolai.domain.glp1

import com.eatcontrolai.core.model.EvidenceType
import com.eatcontrolai.core.model.GoalStatus
import com.eatcontrolai.core.model.Nutrient
import com.eatcontrolai.core.model.NutritionBasis
import com.eatcontrolai.core.model.NutritionFacts
import com.eatcontrolai.core.model.PortionUnit
import com.eatcontrolai.domain.plate.PlateFoodClass
import java.text.Normalizer
import java.util.Locale
import kotlin.math.abs
import kotlin.math.roundToLong

/**
 * `glp1-rules-v1` — implementação da especificação em `docs/RULE_PACK_GLP1.md`.
 *
 * Ordem de avaliação, conforme validado: encaminhamento, regras pessoais, limites regulatórios,
 * metas configuradas, atenções qualitativas e, por fim, as perguntas de incerteza.
 *
 * O parâmetro [underReview] começa `true` de propósito: o conteúdo clínico foi validado, mas o
 * registro formal da validação ainda não existe. Enquanto isso, a interface precisa dizer que a
 * regra está em revisão.
 */
class Glp1RulePackV1(override val underReview: Boolean = true) : RulePack {

    override val id: String = ID

    override fun evaluate(context: Glp1Context): Glp1Assessment {
        val escalation = escalationFor(context)
        val suppressed = escalation == EscalationLevel.MEDICAL_EVALUATION

        val findings = if (suppressed) emptyList() else buildFindings(context)
        val questions = if (suppressed) emptyList() else buildQuestions(context)

        val composed = Glp1MessageComposer.compose(
            findings = findings,
            questions = questions,
            escalation = escalation,
            underReview = underReview,
            analysisSuppressed = suppressed
        )

        return Glp1Assessment(
            packId = id,
            underReview = underReview,
            findings = findings,
            questions = questions,
            escalation = escalation,
            analysisSuppressed = suppressed,
            shortMessage = composed.short,
            detail = composed.detail
        )
    }

    // ------------------------------------------------------------ encaminhamento

    /**
     * Sintoma relatado tem precedência sobre qualquer análise alimentar.
     *
     * O volume de exclusões também escalona: quando a lista de itens evitados cresce a ponto de
     * restringir grupos inteiros, isso deixa de ser preferência e merece acompanhamento.
     */
    private fun escalationFor(context: Glp1Context): EscalationLevel {
        if (context.symptomReports.any { it.level == EscalationLevel.MEDICAL_EVALUATION }) {
            return EscalationLevel.MEDICAL_EVALUATION
        }
        val avoided = context.personalRules.count { it.action == PersonalRuleAction.AVOID }
        val professional = context.symptomReports.any {
            it.level == EscalationLevel.PROFESSIONAL
        } || avoided >= MANY_EXCLUSIONS_THRESHOLD

        return if (professional) EscalationLevel.PROFESSIONAL else EscalationLevel.NONE
    }

    // ------------------------------------------------------------------ achados

    private fun buildFindings(context: Glp1Context): List<Finding> = buildList {
        addAll(regulatoryFindings(context.facts))
        addAll(personalRuleFindings(context))
        addAll(goalFindings(context))
        addAll(qualitativeFindings(context))
    }

    /**
     * R1 — critério "ALTO EM" da rotulagem frontal.
     *
     * Duas guardas fazem a diferença entre alerta legítimo e alerta inventado: exige composição
     * declarada e exige porção declarada, porque é a unidade da porção que diz se o produto é sólido
     * ou líquido. Estimativa visual nunca entra aqui.
     */
    private fun regulatoryFindings(facts: NutritionFacts): List<Finding> {
        val portion = facts.portion ?: return emptyList()
        val liquid = portion.unit == PortionUnit.MILLILITER

        return REGULATORY_LIMITS.mapNotNull { limit ->
            val value = per100(facts, limit.nutrient) ?: return@mapNotNull null
            val threshold = if (liquid) limit.liquid else limit.solid
            if (value < threshold) return@mapNotNull null

            val unit = limit.nutrient.unit.symbol
            val base = if (liquid) "100 ml" else "100 g"
            Finding(
                code = limit.code,
                text = "${limit.nutrient.displayName.capitalizeFirst()} declarado: " +
                    "${format(value)} $unit por $base, no critério de rotulagem frontal " +
                    "(${format(threshold)} $unit).",
                audioLabel = "alto em ${limit.nutrient.displayName}",
                origin = EvidenceType.DECLARED_LABEL,
                nutrient = limit.nutrient
            )
        }
    }

    /** Valor por 100 g/ml: declarado, ou convertido quando a massa da porção está declarada. */
    private fun per100(facts: NutritionFacts, nutrient: Nutrient): Double? {
        facts.amountOf(nutrient, NutritionBasis.PER_100)?.let { return it.value }
        val portion = facts.portion ?: return null
        if (portion.amount <= 0.0) return null
        val perPortion = facts.amountOf(nutrient, NutritionBasis.PER_PORTION) ?: return null
        return perPortion.value * 100.0 / portion.amount
    }

    /**
     * R2 — metas configuradas.
     *
     * Nutriente sem meta não gera achado: sem meta, o aplicativo informa consumo e não julga. Sódio
     * é tratado como limite, não como alvo a alcançar.
     */
    private fun goalFindings(context: Glp1Context): List<Finding> = buildList {
        context.dailyProgress.forEach { progress ->
            val goal = progress.goal ?: return@forEach
            val nutrient = progress.nutrient
            val unit = nutrient.unit.symbol

            when (progress.status) {
                GoalStatus.ABOVE -> {
                    val excess = progress.excess ?: 0.0
                    if (excess <= 0.0) return@forEach
                    if (nutrient in LIMIT_NUTRIENTS) {
                        add(
                            Finding(
                                code = FindingCode.CONFIGURED_LIMIT_EXCEEDED,
                                text = "Consumo de ${nutrient.displayName} hoje: " +
                                    "${format(progress.consumed)} $unit, acima do limite " +
                                    "configurado de ${format(goal)} $unit.",
                                audioLabel = "limite de ${nutrient.displayName} configurado",
                                origin = EvidenceType.USER_CONFIRMATION,
                                nutrient = nutrient
                            )
                        )
                    } else {
                        add(
                            Finding(
                                code = FindingCode.GOAL_EXCEEDED,
                                text = "Consumo de ${nutrient.displayName} hoje: " +
                                    "${format(progress.consumed)} $unit, acima da meta " +
                                    "configurada de ${format(goal)} $unit.",
                                audioLabel = "meta de ${nutrient.displayName} excedida",
                                origin = EvidenceType.USER_CONFIRMATION,
                                nutrient = nutrient
                            )
                        )
                    }
                }

                GoalStatus.BELOW -> {
                    val remaining = progress.remaining ?: 0.0
                    if (remaining <= 0.0) return@forEach
                    add(
                        Finding(
                            code = FindingCode.GOAL_REMAINING,
                            text = "Faltam ${format(remaining)} $unit para sua meta configurada de " +
                                "${nutrient.displayName} hoje.",
                            audioLabel = "Faltam ${format(remaining)} $unit para sua meta de " +
                                "${nutrient.displayName} hoje.",
                            origin = EvidenceType.USER_CONFIRMATION,
                            nutrient = nutrient
                        )
                    )
                }

                GoalStatus.MET, GoalStatus.NO_GOAL -> Unit
            }
        }
    }

    /**
     * R3 — atenções qualitativas.
     *
     * Nenhuma delas recebe limite numérico. Volume só vira atenção quando existe porção prevista no
     * plano: sem plano, o aplicativo descreve e não julga o tamanho.
     */
    private fun qualitativeFindings(context: Glp1Context): List<Finding> = buildList {
        val terms = context.observedTerms.map(::normalize).toSet()
        val components = context.confirmedComponents

        val friedByClass = PlateFoodClass.FRIED_FOOD in components
        if (friedByClass || terms.any { it in FRIED_TERMS }) {
            add(
                Finding(
                    code = FindingCode.PREPARATION_FRIED,
                    text = "Fritura identificada nesta refeição; é um ponto de atenção para tolerância.",
                    audioLabel = "fritura",
                    origin = if (friedByClass) EvidenceType.VISUAL_INFERENCE else EvidenceType.OCR_TEXT
                )
            )
        }

        if (terms.any { it in CREAMY_TERMS }) {
            add(
                Finding(
                    code = FindingCode.CREAMY_SAUCE,
                    text = "Molho cremoso observado; a composição depende do rótulo ou da descrição.",
                    audioLabel = "molho cremoso",
                    origin = EvidenceType.OCR_TEXT
                )
            )
        }

        portionAbovePlan(context)?.let(::add)

        val vegetablesByClass = components.any { it in VEGETABLE_CLASSES }
        if (vegetablesByClass || terms.any { it in VEGETABLE_TERMS }) {
            add(
                Finding(
                    code = FindingCode.VEGETABLES_PRESENT,
                    text = "Presença de vegetais identificada nesta refeição.",
                    audioLabel = "vegetais",
                    origin = if (vegetablesByClass) EvidenceType.VISUAL_INFERENCE else EvidenceType.OCR_TEXT
                )
            )
        }

        if (components.any { it in PROTEIN_CLASSES }) {
            add(
                Finding(
                    code = FindingCode.PROTEIN_PRESENT,
                    text = "Presença de fonte proteica identificada nesta refeição.",
                    audioLabel = "proteína",
                    origin = EvidenceType.VISUAL_INFERENCE
                )
            )
        }
    }

    private fun portionAbovePlan(context: Glp1Context): Finding? {
        val confirmed = context.confirmedPortions ?: return null
        val planned = context.plannedPortions ?: return null
        if (planned <= 0.0 || confirmed <= planned * PORTION_TOLERANCE) return null

        return Finding(
            code = FindingCode.PORTION_ABOVE_PLAN,
            text = "Porções confirmadas: ${format(confirmed)}, acima de ${format(planned)} " +
                "prevista no seu plano.",
            audioLabel = "porção acima do plano",
            origin = EvidenceType.USER_CONFIRMATION
        )
    }

    /**
     * R4 — regras pessoais.
     *
     * Correspondência direta vira atenção citando o que o usuário cadastrou. Correspondência apenas
     * plausível não vira afirmação: vira pergunta, tratada em [buildQuestions].
     */
    private fun personalRuleFindings(context: Glp1Context): List<Finding> {
        val terms = context.observedTerms.map(::normalize).toSet()

        return context.personalRules.mapNotNull { rule ->
            val matchedClasses = rule.matchClasses.filter { it in context.confirmedComponents }
            val matchedTerms = rule.matchTerms.map(::normalize).filter { it in terms }
            val matched = matchedClasses.map { it.displayName } + matchedTerms
            if (matched.isEmpty()) return@mapNotNull null

            val verb = if (rule.action == PersonalRuleAction.AVOID) "evitar" else "observar"
            Finding(
                code = FindingCode.PERSONAL_RULE_MATCH,
                text = "Identifiquei ${matched.joinToString()} nesta refeição. " +
                    "Você cadastrou $verb ${rule.target}.",
                audioLabel = "${matched.first()}, que você pediu para $verb",
                origin = if (matchedClasses.isNotEmpty()) {
                    EvidenceType.VISUAL_INFERENCE
                } else {
                    EvidenceType.OCR_TEXT
                }
            )
        }
    }

    // ---------------------------------------------------------------- perguntas

    /**
     * R5 — incerteza vira pergunta, nunca afirmação.
     *
     * É aqui que a política de imagem se mantém honesta: identificar um molho é permitido; dizer que
     * ele não contém leite exige rótulo, descrição ou confirmação da pessoa.
     */
    private fun buildQuestions(context: Glp1Context): List<String> = buildList {
        val terms = context.observedTerms.map(::normalize).toSet()
        val hasSauce = terms.any { it in SAUCE_TERMS }

        if (hasSauce) {
            context.restrictions.sortedBy { it.allergen.ordinal }.forEach { restriction ->
                add(
                    "Esse molho pode conter ${restriction.allergen.displayName}. " +
                        "Quer confirmar os ingredientes?"
                )
            }
        }

        context.personalRules.forEach { rule ->
            val possible = rule.possibleTerms.map(::normalize).filter { it in terms }
            val alreadyDirect = rule.matchClasses.any { it in context.confirmedComponents } ||
                rule.matchTerms.map(::normalize).any { it in terms }
            if (possible.isEmpty() || alreadyDirect) return@forEach
            add(
                "Esse ${possible.first()} pode conter ${rule.target}. " +
                    "Quer confirmar os ingredientes?"
            )
        }

        if (context.hasVisualInference &&
            context.confirmedPortions == null &&
            context.confirmedComponents.isNotEmpty()
        ) {
            add("Não consigo confirmar a quantidade apenas pela imagem. Quer informar a porção?")
        }
    }

    // ------------------------------------------------------------------ helpers

    private fun normalize(value: String): String {
        val decomposed = Normalizer.normalize(value, Normalizer.Form.NFD)
        return DIACRITICS.replace(decomposed, "").lowercase().trim()
    }

    private fun format(value: Double): String {
        val rounded = value.roundToLong()
        return if (abs(value - rounded) < 0.05) {
            rounded.toString()
        } else {
            String.format(Locale.forLanguageTag("pt-BR"), "%.1f", value)
        }
    }

    private fun String.capitalizeFirst(): String =
        replaceFirstChar { it.titlecase(Locale.forLanguageTag("pt-BR")) }

    private data class RegulatoryLimit(
        val nutrient: Nutrient,
        val solid: Double,
        val liquid: Double,
        val code: FindingCode
    )

    companion object {
        const val ID = "glp1-rules-v1"

        /**
         * Quantas exclusões cadastradas escalonam para acompanhamento profissional.
         *
         * Número de engenharia, não clínico: serve para detectar restrição alimentar ampla, que a
         * validação apontou como situação de encaminhamento.
         */
        const val MANY_EXCLUSIONS_THRESHOLD = 5

        /** Folga antes de tratar porção confirmada como acima do plano. */
        const val PORTION_TOLERANCE = 1.05

        /**
         * Limites do critério "ALTO EM" da rotulagem frontal brasileira, por 100 g ou 100 ml.
         *
         * Fonte registrada em `docs/DATA_SOURCES.md` (`REG-ANVISA-001`). Vale para alimento embalado
         * com composição declarada — nunca para prato estimado por imagem.
         */
        private val REGULATORY_LIMITS = listOf(
            RegulatoryLimit(Nutrient.ADDED_SUGARS, 15.0, 7.5, FindingCode.HIGH_IN_ADDED_SUGARS),
            RegulatoryLimit(Nutrient.SATURATED_FAT, 6.0, 3.0, FindingCode.HIGH_IN_SATURATED_FAT),
            RegulatoryLimit(Nutrient.SODIUM, 600.0, 300.0, FindingCode.HIGH_IN_SODIUM)
        )

        /** Nutrientes cuja meta configurada é teto, não alvo. */
        private val LIMIT_NUTRIENTS = setOf(Nutrient.SODIUM)

        private val VEGETABLE_CLASSES = setOf(PlateFoodClass.SALAD, PlateFoodClass.VEGETABLES)
        private val PROTEIN_CLASSES = setOf(
            PlateFoodClass.CHICKEN,
            PlateFoodClass.MEAT,
            PlateFoodClass.FISH,
            PlateFoodClass.EGG,
            PlateFoodClass.CHEESE,
            PlateFoodClass.BEANS
        )

        private val FRIED_TERMS = setOf("frito", "fritura", "empanado")
        private val CREAMY_TERMS = setOf("cremoso", "creme")
        private val SAUCE_TERMS = setOf("molho", "cremoso", "creme")
        private val VEGETABLE_TERMS = setOf("salada", "legumes")

        private val DIACRITICS = Regex("\\p{Mn}+")
    }
}
