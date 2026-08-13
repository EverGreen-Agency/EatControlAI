package com.eatcontrolai.domain.decision

import com.eatcontrolai.core.model.Allergen
import com.eatcontrolai.core.model.ClaimPolarity
import com.eatcontrolai.core.model.Decision
import com.eatcontrolai.core.model.DecisionReason
import com.eatcontrolai.core.model.DecisionState
import com.eatcontrolai.core.model.Evidence
import com.eatcontrolai.core.model.EvidenceType
import com.eatcontrolai.core.model.UserProfile

/**
 * Motor determinístico de decisão.
 *
 * Implementa `docs/SPEC.md`:
 *  - precedência de evidência ([EvidenceType.rank]);
 *  - os quatro estados de decisão, todos alcançáveis;
 *  - a regra de segurança de que inferência visual isolada não sustenta afirmação de segurança
 *    (NFR-003).
 *
 * A regra central de projeto, e o motivo de este componente não ser um LLM
 * (`contexto-gpt.md` §47): **ausência de declaração nunca vira permissão.** Um rótulo que não fala
 * sobre leite não é um rótulo que garante ausência de leite. Só uma afirmação explícita de ausência,
 * uma confirmação do usuário ou uma regra profissional levam a [DecisionState.COMPATIBLE].
 */
class FoodDecisionEngine {

    private enum class Verdict { CONTAINS, MAY_CONTAIN, FREE_OF, UNRESOLVED }

    private data class RestrictionCheck(
        val allergen: Allergen,
        val verdict: Verdict,
        val evidenceType: EvidenceType?,
        val sourceText: String?
    )

    fun decide(profile: UserProfile, evidence: List<Evidence>): Decision {
        if (evidence.isEmpty()) return insufficient(evidence, "Não recebi nenhuma informação para analisar.")

        // Precedência de docs/SPEC.md: menor rank primeiro.
        val ranked = evidence.sortedBy { it.type.rank }
        val hasDeclaredSource = ranked.any {
            !it.type.isProbabilistic && it.claims.isNotEmpty()
        }
        val onlyProbabilistic = ranked.all { it.type.isProbabilistic }

        if (onlyProbabilistic) {
            return insufficient(
                evidence,
                "Só tenho uma estimativa visual. Não dá para confirmar com segurança."
            )
        }

        if (profile.restrictions.isEmpty()) {
            return noRestrictionsDecision(profile, evidence, hasDeclaredSource)
        }

        val checks = profile.restrictions.map { allergen -> check(allergen, ranked) }

        val contains = checks.filter { it.verdict == Verdict.CONTAINS }
        val mayContain = checks.filter { it.verdict == Verdict.MAY_CONTAIN }
        val unresolved = checks.filter { it.verdict == Verdict.UNRESOLVED }

        return when {
            contains.isNotEmpty() -> Decision(
                state = DecisionState.INCOMPATIBLE,
                shortMessage = "Não está alinhado ao seu perfil. " +
                    "${sentenceCase(listNames(contains))} ${verb(contains)} declarado no rótulo.",
                evidence = evidence,
                reasons = contains.map { it.toReason("declarado explicitamente") } +
                    mayContain.map { it.toReason("possível contaminação cruzada") }
            )

            mayContain.isNotEmpty() -> Decision(
                state = DecisionState.NEEDS_CONFIRMATION,
                shortMessage = "Atenção: o rótulo diz que pode conter " +
                    "${listNames(mayContain)}. Confirme antes de consumir.",
                evidence = evidence,
                reasons = mayContain.map { it.toReason("possível contaminação cruzada") } +
                    unresolved.map { it.toReason("não declarado no rótulo") }
            )

            unresolved.isNotEmpty() && hasDeclaredSource -> Decision(
                state = DecisionState.NEEDS_CONFIRMATION,
                shortMessage = "Li o rótulo, mas ele não diz nada sobre " +
                    "${listNames(unresolved)}. Confirme os ingredientes.",
                evidence = evidence,
                reasons = unresolved.map { it.toReason("ausência de declaração não comprova ausência") }
            )

            unresolved.isNotEmpty() -> insufficient(
                evidence,
                "Não consegui ler informação suficiente. Verifique os ingredientes."
            )

            else -> Decision(
                state = DecisionState.COMPATIBLE,
                shortMessage = "Compatível com o seu perfil. " +
                    "O rótulo declara ausência de ${listNames(checks)}.",
                evidence = evidence,
                reasons = checks.map { it.toReason("ausência declarada explicitamente") }
            )
        }
    }

    /**
     * Escolhe a afirmação de maior precedência sobre [allergen].
     * [ranked] já vem ordenado, então a primeira evidência que fala do alérgeno vence — é assim que
     * uma regra profissional supera um rótulo, e um rótulo supera uma inferência visual.
     */
    private fun check(allergen: Allergen, ranked: List<Evidence>): RestrictionCheck {
        for (item in ranked) {
            val claim = item.claims.firstOrNull { it.allergen == allergen } ?: continue

            // NFR-003: evidência probabilística não pode sustentar ausência.
            if (item.type.isProbabilistic && claim.polarity == ClaimPolarity.FREE_OF) continue

            val verdict = when (claim.polarity) {
                ClaimPolarity.CONTAINS -> Verdict.CONTAINS
                ClaimPolarity.MAY_CONTAIN -> Verdict.MAY_CONTAIN
                ClaimPolarity.FREE_OF -> Verdict.FREE_OF
            }
            return RestrictionCheck(allergen, verdict, item.type, claim.sourceText)
        }
        return RestrictionCheck(allergen, Verdict.UNRESOLVED, null, null)
    }

    private fun noRestrictionsDecision(
        profile: UserProfile,
        evidence: List<Evidence>,
        hasDeclaredSource: Boolean
    ): Decision = when {
        profile.freeTextRestrictions.isNotEmpty() -> Decision(
            state = DecisionState.NEEDS_CONFIRMATION,
            shortMessage = "Você tem restrições que eu ainda não sei avaliar sozinho. " +
                "Confira o rótulo.",
            evidence = evidence,
            reasons = profile.freeTextRestrictions.map {
                DecisionReason("Restrição em texto livre, sem regra determinística: $it", EvidenceType.PROFESSIONAL_RULE)
            }
        )

        hasDeclaredSource -> Decision(
            state = DecisionState.COMPATIBLE,
            shortMessage = "Nada no rótulo conflita com as restrições cadastradas no seu perfil.",
            evidence = evidence,
            reasons = listOf(
                DecisionReason(
                    "Nenhuma restrição alimentar cadastrada no perfil.",
                    EvidenceType.PROFESSIONAL_RULE
                )
            )
        )

        else -> insufficient(evidence, "Não consegui ler o rótulo com clareza.")
    }

    private fun insufficient(evidence: List<Evidence>, message: String) = Decision(
        state = DecisionState.INSUFFICIENT_INFORMATION,
        shortMessage = message,
        evidence = evidence,
        reasons = listOf(
            DecisionReason(
                "Declarar incerteza é o comportamento esperado quando falta evidência.",
                EvidenceType.OCR_TEXT
            )
        )
    )

    private fun RestrictionCheck.toReason(explanation: String) = DecisionReason(
        text = "${allergen.displayName.replaceFirstChar { it.uppercase() }}: $explanation" +
            (sourceText?.let { " (\"$it\")" } ?: ""),
        evidenceType = evidenceType ?: EvidenceType.OCR_TEXT,
        allergen = allergen
    )

    private fun listNames(checks: List<RestrictionCheck>): String {
        val names = checks.map { it.allergen.displayName }
        return when (names.size) {
            0 -> ""
            1 -> names.first()
            else -> names.dropLast(1).joinToString(", ") + " e " + names.last()
        }
    }

    private fun verb(checks: List<RestrictionCheck>) = if (checks.size == 1) "está" else "estão"

    private fun sentenceCase(text: String) = text.replaceFirstChar { it.uppercase() }
}
