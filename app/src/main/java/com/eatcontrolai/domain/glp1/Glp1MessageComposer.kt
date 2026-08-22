package com.eatcontrolai.domain.glp1

import com.eatcontrolai.core.model.Nutrient

/**
 * Compõe a resposta na ordem aprovada em `VAL-GLP1-R1`: identificar, contextualizar, orientar.
 *
 * A frase falada é curta por decisão de produto — quem está diante de uma escolha alimentar não
 * ouve parágrafo. O detalhamento vai para a tela, onde cada achado aparece com a origem.
 */
object Glp1MessageComposer {

    /** Limite de palavras da frase falada. */
    const val MAX_WORDS = 15

    const val PROTEIN_CAVEAT =
        "Proteína é parte do cuidado; preservação muscular também envolve exercício de força."
    const val PROFESSIONAL_LINE =
        "Esse ponto depende da sua condição ou plano individual. Confirme com seu profissional."
    const val MEDICAL_SHORT = "Procure avaliação médica agora. Não vou avaliar esta refeição."
    const val MEDICAL_DETAIL =
        "Os sinais relatados exigem avaliação médica antes de qualquer orientação alimentar."
    const val INSUFFICIENT = "Não tenho informação suficiente para avaliar esta refeição."
    const val REVIEW_LINE =
        "Regra em revisão: aguarda registro formal da validação profissional."

    private const val MAX_AUDIO_FINDINGS = 2

    data class Composed(val short: String, val detail: List<String>)

    fun compose(
        findings: List<Finding>,
        questions: List<String>,
        escalation: EscalationLevel,
        underReview: Boolean,
        analysisSuppressed: Boolean
    ): Composed = Composed(
        short = shortMessage(findings, questions, escalation, analysisSuppressed),
        detail = detail(findings, questions, escalation, underReview, analysisSuppressed)
    )

    private fun shortMessage(
        findings: List<Finding>,
        questions: List<String>,
        escalation: EscalationLevel,
        analysisSuppressed: Boolean
    ): String {
        if (analysisSuppressed) return MEDICAL_SHORT

        val attention = findings.filter { it.kind == FindingKind.ATTENTION }
        val positive = findings.filter { it.kind == FindingKind.POSITIVE }
        val info = findings.filter { it.kind == FindingKind.INFO }

        return when {
            attention.isNotEmpty() -> "Atenção: ${join(attention)}."
            positive.isNotEmpty() -> "Boa presença de ${join(positive)}."
            info.isNotEmpty() -> info.first().audioLabel
            questions.isNotEmpty() -> questions.first()
            escalation == EscalationLevel.PROFESSIONAL -> PROFESSIONAL_LINE
            else -> INSUFFICIENT
        }
    }

    private fun join(findings: List<Finding>) = findings
        .take(MAX_AUDIO_FINDINGS)
        .joinToString(separator = " e ") { it.audioLabel }

    private fun detail(
        findings: List<Finding>,
        questions: List<String>,
        escalation: EscalationLevel,
        underReview: Boolean,
        analysisSuppressed: Boolean
    ): List<String> = buildList {
        if (analysisSuppressed) {
            add(MEDICAL_DETAIL)
        } else {
            addAll(findings.map { it.text })
            addAll(questions)
            if (mentionsProteinGoal(findings)) add(PROTEIN_CAVEAT)
            if (escalation == EscalationLevel.PROFESSIONAL) add(PROFESSIONAL_LINE)
        }
        if (underReview) add(REVIEW_LINE)
    }

    /** A ressalva de exercício é obrigatória sempre que a resposta fala de meta de proteína. */
    private fun mentionsProteinGoal(findings: List<Finding>) = findings.any {
        it.nutrient == Nutrient.PROTEIN &&
            (it.code == FindingCode.GOAL_REMAINING || it.code == FindingCode.GOAL_EXCEEDED)
    }
}
