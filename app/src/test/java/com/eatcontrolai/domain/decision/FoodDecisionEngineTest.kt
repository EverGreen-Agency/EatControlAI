package com.eatcontrolai.domain.decision

import com.eatcontrolai.core.model.Allergen
import com.eatcontrolai.core.model.ClaimPolarity
import com.eatcontrolai.core.model.DecisionState
import com.eatcontrolai.core.model.Evidence
import com.eatcontrolai.core.model.EvidenceType
import com.eatcontrolai.core.model.LabelClaim
import com.eatcontrolai.core.model.Restriction
import com.eatcontrolai.core.model.RestrictionSeverity
import com.eatcontrolai.core.model.UncertaintyPolicy
import com.eatcontrolai.core.model.UserProfile
import com.eatcontrolai.domain.evidence.EvidenceBuilder
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Testa as invariantes de segurança de `docs/SPEC.md` e `docs/SRS.md` diretamente — sem depender
 * de OCR, de Android ou de rede.
 */
class FoodDecisionEngineTest {

    private val engine = FoodDecisionEngine()
    private val milkProfile = UserProfile(id = "t", restrictions = setOf(Restriction(Allergen.MILK)))

    private fun decideFromLabel(text: String, profile: UserProfile = milkProfile) =
        engine.decide(profile, EvidenceBuilder.fromLabelOcr(text, "test_ocr"))

    @Test
    fun `todos os quatro estados sao alcancaveis`() {
        val states = setOf(
            decideFromLabel("ALÉRGICOS: CONTÉM LEITE.").state,
            decideFromLabel("ALÉRGICOS: NÃO CONTÉM LEITE.").state,
            decideFromLabel("ALÉRGICOS: PODE CONTER LEITE.").state,
            decideFromLabel("NOVO! LEVE 3 PAGUE 2.").state
        )
        assertEquals(
            setOf(
                DecisionState.INCOMPATIBLE,
                DecisionState.COMPATIBLE,
                DecisionState.NEEDS_CONFIRMATION,
                DecisionState.INSUFFICIENT_INFORMATION
            ),
            states
        )
    }

    @Test
    fun `ausencia de declaracao nunca vira compativel`() {
        val decision = decideFromLabel("ALÉRGICOS: CONTÉM TRIGO E SOJA.")
        assertEquals(DecisionState.NEEDS_CONFIRMATION, decision.state)
        assertNotEquals(DecisionState.COMPATIBLE, decision.state)
    }

    @Test
    fun `sem evidencia declara incerteza`() {
        assertEquals(
            DecisionState.INSUFFICIENT_INFORMATION,
            engine.decide(milkProfile, emptyList()).state
        )
    }

    @Test
    fun `NFR-003 inferencia visual isolada nunca afirma seguranca`() {
        val visualOnly = listOf(
            Evidence(
                type = EvidenceType.VISUAL_INFERENCE,
                value = "prato com salada e frango",
                source = "detector_v0",
                confidence = 0.93f,
                claims = listOf(LabelClaim(Allergen.MILK, ClaimPolarity.FREE_OF, "sem laticínio visível"))
            )
        )
        assertEquals(
            DecisionState.INSUFFICIENT_INFORMATION,
            engine.decide(milkProfile, visualOnly).state
        )
    }

    @Test
    fun `regra profissional supera o rotulo`() {
        val evidence = listOf(
            Evidence(
                type = EvidenceType.DECLARED_LABEL,
                value = "FREE_OF leite",
                source = "label_parser_v1",
                claims = listOf(LabelClaim(Allergen.MILK, ClaimPolarity.FREE_OF, "NAO CONTEM … LEITE"))
            ),
            Evidence(
                type = EvidenceType.PROFESSIONAL_RULE,
                value = "nutricionista suspendeu laticínios nesta fase",
                source = "guideline_123",
                claims = listOf(LabelClaim(Allergen.MILK, ClaimPolarity.CONTAINS, "orientação profissional"))
            )
        )
        assertEquals(DecisionState.INCOMPATIBLE, engine.decide(milkProfile, evidence).state)
    }

    @Test
    fun `confirmacao do usuario resolve a incerteza`() {
        // Rótulo lido, mas silencioso sobre leite: precisa confirmar.
        val base = EvidenceBuilder.fromLabelOcr("ALÉRGICOS: CONTÉM SOJA.", "test_ocr")
        val before = engine.decide(milkProfile, base)
        assertEquals(DecisionState.NEEDS_CONFIRMATION, before.state)
        assertEquals(listOf(Allergen.MILK), before.unresolved)

        val confirmed = base + Evidence(
            type = EvidenceType.USER_CONFIRMATION,
            value = "usuário confirmou ausência de leite",
            source = "user",
            claims = listOf(LabelClaim(Allergen.MILK, ClaimPolarity.FREE_OF, "confirmado por você"))
        )
        assertEquals(DecisionState.COMPATIBLE, engine.decide(milkProfile, confirmed).state)
    }

    @Test
    fun `pior caso prevalece entre multiplas restricoes`() {
        val profile = UserProfile(
            id = "t",
            restrictions = setOf(Restriction(Allergen.MILK), Restriction(Allergen.PEANUT))
        )
        assertEquals(
            DecisionState.INCOMPATIBLE,
            decideFromLabel("ALÉRGICOS: CONTÉM LEITE. PODE CONTER AMENDOIM.", profile).state
        )
    }

    @Test
    fun `preferencia informa mas nao bloqueia`() {
        val profile = UserProfile(
            id = "t",
            restrictions = setOf(
                Restriction(Allergen.SOY, severity = RestrictionSeverity.PREFERENCE)
            )
        )
        val decision = decideFromLabel("ALÉRGICOS: CONTÉM SOJA.", profile)
        assertEquals(DecisionState.COMPATIBLE, decision.state)
        assertTrue(
            "A preferência deveria aparecer como observação",
            decision.reasons.any { it.allergen == Allergen.SOY }
        )
    }

    @Test
    fun `politica apenas informar nao trava por falta de declaracao`() {
        val profile = UserProfile(
            id = "t",
            restrictions = setOf(
                Restriction(
                    allergen = Allergen.MILK,
                    severity = RestrictionSeverity.MODERATE,
                    uncertaintyPolicy = UncertaintyPolicy.INFORM_ONLY
                )
            )
        )
        assertEquals(
            DecisionState.COMPATIBLE,
            decideFromLabel("ALÉRGICOS: CONTÉM SOJA.", profile).state
        )
    }

    @Test
    fun `politica ignorar visual descarta evidencia probabilistica`() {
        val profile = UserProfile(
            id = "t",
            restrictions = setOf(
                Restriction(Allergen.MILK, uncertaintyPolicy = UncertaintyPolicy.IGNORE_VISUAL)
            )
        )
        val evidence = listOf(
            Evidence(
                type = EvidenceType.DECLARED_LABEL,
                value = "CONTAINS soja",
                source = "label_parser_v1",
                claims = listOf(LabelClaim(Allergen.SOY, ClaimPolarity.CONTAINS, "CONTEM … SOJA"))
            ),
            Evidence(
                type = EvidenceType.VISUAL_INFERENCE,
                value = "parece ter queijo",
                source = "detector_v0",
                confidence = 0.71f,
                claims = listOf(LabelClaim(Allergen.MILK, ClaimPolarity.CONTAINS, "queijo visível"))
            )
        )
        // A visão apontou leite, mas a política do usuário manda ignorá-la para esta restrição.
        assertEquals(DecisionState.NEEDS_CONFIRMATION, engine.decide(profile, evidence).state)
    }

    @Test
    fun `perfil sem restricao nao gera alarme`() {
        val profile = UserProfile(id = "t", restrictions = emptySet())
        assertEquals(
            DecisionState.COMPATIBLE,
            decideFromLabel("ALÉRGICOS: CONTÉM LEITE.", profile).state
        )
    }

    @Test
    fun `restricao em texto livre pede confirmacao em vez de fingir cobertura`() {
        val profile = UserProfile(
            id = "t",
            restrictions = emptySet(),
            freeTextRestrictions = setOf("evitar frituras")
        )
        assertEquals(
            DecisionState.NEEDS_CONFIRMATION,
            decideFromLabel("ALÉRGICOS: CONTÉM SOJA.", profile).state
        )
    }

    @Test
    fun `resposta e curta o suficiente para TTS`() {
        val decision = decideFromLabel("ALÉRGICOS: CONTÉM LEITE.")
        // contexto-gpt.md §21: resposta curta, não um podcast.
        assertTrue(
            "Mensagem longa demais para áudio: ${decision.shortMessage.length} caracteres",
            decision.shortMessage.length <= 160
        )
    }
}
