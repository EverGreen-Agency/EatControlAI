package com.eatcontrolai.domain.decision

import com.eatcontrolai.core.model.Allergen
import com.eatcontrolai.core.model.ClaimPolarity
import com.eatcontrolai.core.model.DecisionState
import com.eatcontrolai.core.model.Evidence
import com.eatcontrolai.core.model.EvidenceType
import com.eatcontrolai.core.model.LabelClaim
import com.eatcontrolai.core.model.UserProfile
import com.eatcontrolai.domain.evidence.EvidenceBuilder
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

/**
 * Testa as invariantes de segurança de `docs/SPEC.md` e `docs/SRS.md` diretamente — sem depender
 * de OCR, de Android ou de rede.
 */
class FoodDecisionEngineTest {

    private val engine = FoodDecisionEngine()
    private val milkProfile = UserProfile(id = "t", restrictions = setOf(Allergen.MILK))

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
        // O rótulo foi lido e declara alérgicos, mas não fala de leite.
        val decision = decideFromLabel("ALÉRGICOS: CONTÉM TRIGO E SOJA.")
        assertEquals(DecisionState.NEEDS_CONFIRMATION, decision.state)
        assertNotEquals(DecisionState.COMPATIBLE, decision.state)
    }

    @Test
    fun `sem evidencia declara incerteza`() {
        val decision = engine.decide(milkProfile, emptyList())
        assertEquals(DecisionState.INSUFFICIENT_INFORMATION, decision.state)
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
        val decision = engine.decide(milkProfile, visualOnly)
        assertEquals(DecisionState.INSUFFICIENT_INFORMATION, decision.state)
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
    fun `pior caso prevalece entre multiplas restricoes`() {
        val profile = UserProfile(id = "t", restrictions = setOf(Allergen.MILK, Allergen.PEANUT))
        val decision = decideFromLabel("ALÉRGICOS: CONTÉM LEITE. PODE CONTER AMENDOIM.", profile)
        assertEquals(DecisionState.INCOMPATIBLE, decision.state)
    }

    @Test
    fun `perfil sem restricao nao gera alarme`() {
        val profile = UserProfile(id = "t", restrictions = emptySet())
        assertEquals(DecisionState.COMPATIBLE, decideFromLabel("ALÉRGICOS: CONTÉM LEITE.", profile).state)
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
        assert(decision.shortMessage.length <= 160) {
            "Mensagem longa demais para áudio: ${decision.shortMessage.length} caracteres"
        }
    }
}
