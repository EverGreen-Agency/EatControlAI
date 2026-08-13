package com.eatcontrolai.domain.decision

import com.eatcontrolai.core.model.Allergen
import com.eatcontrolai.core.model.ClaimPolarity
import com.eatcontrolai.core.model.Decision
import com.eatcontrolai.core.model.DecisionReason
import com.eatcontrolai.core.model.DecisionState
import com.eatcontrolai.core.model.Evidence
import com.eatcontrolai.core.model.EvidenceType
import com.eatcontrolai.core.model.Restriction
import com.eatcontrolai.core.model.RestrictionSeverity
import com.eatcontrolai.core.model.UncertaintyPolicy
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
 *
 * A severidade da restrição muda o comportamento de verdade: uma
 * [RestrictionSeverity.PREFERENCE] informa mas não bloqueia; uma [RestrictionSeverity.CRITICAL]
 * bloqueia mesmo quando a única pendência é incerteza.
 */
class FoodDecisionEngine {

    private enum class Verdict { CONTAINS, MAY_CONTAIN, FREE_OF, UNRESOLVED }

    private data class Check(
        val restriction: Restriction,
        val verdict: Verdict,
        val evidenceType: EvidenceType?,
        val sourceText: String?
    ) {
        val allergen: Allergen get() = restriction.allergen

        /** Preferência nunca muda o estado — só entra como observação. */
        val blocks: Boolean get() = restriction.severity != RestrictionSeverity.PREFERENCE
    }

    fun decide(profile: UserProfile, evidence: List<Evidence>): Decision {
        if (evidence.isEmpty()) {
            return insufficient(evidence, "Não recebi nenhuma informação para analisar.")
        }

        // Precedência de docs/SPEC.md: menor rank primeiro.
        val ranked = evidence.sortedBy { it.type.rank }
        val hasDeclaredSource = ranked.any { !it.type.isProbabilistic && it.claims.isNotEmpty() }

        if (ranked.all { it.type.isProbabilistic }) {
            return insufficient(
                evidence,
                "Só tenho uma estimativa visual. Não dá para confirmar com segurança."
            )
        }

        if (profile.restrictions.isEmpty()) {
            return noRestrictionsDecision(profile, evidence, hasDeclaredSource)
        }

        val checks = profile.restrictions.map { check(it, ranked) }
        val blocking = checks.filter { it.blocks }
        val notes = checks.filterNot { it.blocks }
            .filter { it.verdict == Verdict.CONTAINS || it.verdict == Verdict.MAY_CONTAIN }
            .map { it.toReason("preferência sua, sinalizada mas não bloqueante") }

        val contains = blocking.filter { it.verdict == Verdict.CONTAINS }
        val mayContain = blocking.filter { it.verdict == Verdict.MAY_CONTAIN }

        // Política de incerteza: INFORM_ONLY não trava a decisão quando nada foi declarado.
        val unresolved = blocking.filter {
            it.verdict == Verdict.UNRESOLVED &&
                it.restriction.uncertaintyPolicy != UncertaintyPolicy.INFORM_ONLY
        }
        val informOnly = blocking.filter {
            it.verdict == Verdict.UNRESOLVED &&
                it.restriction.uncertaintyPolicy == UncertaintyPolicy.INFORM_ONLY
        }

        return when {
            contains.isNotEmpty() -> Decision(
                state = DecisionState.INCOMPATIBLE,
                shortMessage = "Não está alinhado ao seu perfil. " +
                    "${sentenceCase(names(contains))} ${verb(contains)} declarado no rótulo.",
                evidence = evidence,
                reasons = contains.map { it.toReason("declarado explicitamente") } +
                    mayContain.map { it.toReason("possível contaminação cruzada") } + notes,
                unresolved = unresolved.map { it.allergen }
            )

            mayContain.isNotEmpty() -> Decision(
                state = DecisionState.NEEDS_CONFIRMATION,
                shortMessage = mayContainMessage(mayContain),
                evidence = evidence,
                reasons = mayContain.map { it.toReason("possível contaminação cruzada") } +
                    unresolved.map { it.toReason("não declarado no rótulo") } + notes,
                unresolved = (mayContain + unresolved).map { it.allergen }
            )

            unresolved.isNotEmpty() && hasDeclaredSource -> Decision(
                state = DecisionState.NEEDS_CONFIRMATION,
                shortMessage = "Li o rótulo, mas ele não diz nada sobre " +
                    "${names(unresolved)}. Confirme os ingredientes.",
                evidence = evidence,
                reasons = unresolved.map {
                    it.toReason("ausência de declaração não comprova ausência")
                } + notes,
                unresolved = unresolved.map { it.allergen }
            )

            unresolved.isNotEmpty() -> insufficient(
                evidence,
                "Não consegui ler informação suficiente. Verifique os ingredientes."
            ).copy(unresolved = unresolved.map { it.allergen })

            else -> Decision(
                state = DecisionState.COMPATIBLE,
                shortMessage = compatibleMessage(blocking),
                evidence = evidence,
                reasons = blocking.filter { it.verdict == Verdict.FREE_OF }
                    .map { it.toReason("ausência declarada explicitamente") } +
                    informOnly.map { it.toReason("sem declaração; configurada como apenas informar") } +
                    notes
            )
        }
    }

    /**
     * Escolhe a afirmação de maior precedência sobre o alérgeno.
     * [ranked] já vem ordenado, então a primeira evidência que fala do alérgeno vence — é assim que
     * uma regra profissional supera um rótulo, e um rótulo supera uma inferência visual.
     */
    private fun check(restriction: Restriction, ranked: List<Evidence>): Check {
        for (item in ranked) {
            // Política do usuário: esta restrição não deve ser avaliada por visão.
            if (item.type.isProbabilistic &&
                restriction.uncertaintyPolicy == UncertaintyPolicy.IGNORE_VISUAL
            ) continue

            val claim = item.claims.firstOrNull { it.allergen == restriction.allergen } ?: continue

            // NFR-003: evidência probabilística não pode sustentar ausência.
            if (item.type.isProbabilistic && claim.polarity == ClaimPolarity.FREE_OF) continue

            val verdict = when (claim.polarity) {
                ClaimPolarity.CONTAINS -> Verdict.CONTAINS
                ClaimPolarity.MAY_CONTAIN -> Verdict.MAY_CONTAIN
                ClaimPolarity.FREE_OF -> Verdict.FREE_OF
            }
            return Check(restriction, verdict, item.type, claim.sourceText)
        }
        return Check(restriction, Verdict.UNRESOLVED, null, null)
    }

    private fun mayContainMessage(checks: List<Check>): String {
        val critical = checks.any { it.restriction.severity == RestrictionSeverity.CRITICAL }
        val prefix = if (critical) "Atenção. Para uma restrição de alta prioridade, "
        else "Atenção: "
        return "$prefix o rótulo diz que pode conter ${names(checks)}. Confirme antes de consumir."
    }

    private fun compatibleMessage(checks: List<Check>): String {
        val declared = checks.filter { it.verdict == Verdict.FREE_OF }
        return if (declared.isEmpty()) "Nada no rótulo conflita com o seu perfil."
        else "Compatível com o seu perfil. O rótulo declara ausência de ${names(declared)}."
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
                DecisionReason(
                    "Restrição em texto livre, sem regra determinística: $it",
                    EvidenceType.PROFESSIONAL_RULE
                )
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

    private fun Check.toReason(explanation: String) = DecisionReason(
        text = "${allergen.displayName.replaceFirstChar { it.uppercase() }}: $explanation" +
            (sourceText?.let { " (\"$it\")" } ?: ""),
        evidenceType = evidenceType ?: EvidenceType.OCR_TEXT,
        allergen = allergen
    )

    private fun names(checks: List<Check>): String {
        val list = checks.map { it.allergen.displayName }
        return when (list.size) {
            0 -> ""
            1 -> list.first()
            else -> list.dropLast(1).joinToString(", ") + " e " + list.last()
        }
    }

    private fun verb(checks: List<Check>) = if (checks.size == 1) "está" else "estão"

    private fun sentenceCase(text: String) = text.replaceFirstChar { it.uppercase() }
}
