package com.eatcontrolai.domain.nutrition

import com.eatcontrolai.core.model.Decision
import com.eatcontrolai.core.model.DecisionState
import com.eatcontrolai.core.model.Evidence
import com.eatcontrolai.core.model.EvidenceType
import com.eatcontrolai.core.model.MealRecord
import com.eatcontrolai.core.model.Nutrient
import com.eatcontrolai.core.model.NutrientAmount
import com.eatcontrolai.core.model.NutritionBasis
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MealRecordUpdaterTest {

    private val consumed = listOf(
        NutrientAmount(Nutrient.PROTEIN, 18.0, NutritionBasis.PER_PORTION)
    )

    private val original = MealRecord(
        id = "meal-1",
        timestampMillis = 1234L,
        title = "Refeição",
        decisionState = DecisionState.NEEDS_CONFIRMATION,
        shortMessage = "Preciso confirmar.",
        recognizedText = "texto anterior",
        evidenceLabels = listOf(EvidenceType.OCR_TEXT.label),
        endToEndMs = 42L,
        userConfirmed = true,
        consumedNutrients = consumed
    )

    @Test
    fun `alterar consumo preserva confirmacao e metadados do registro`() {
        val replacement = listOf(
            NutrientAmount(Nutrient.PROTEIN, 27.0, NutritionBasis.PER_PORTION)
        )

        val updated = MealRecordUpdater.withConsumption(original, replacement)

        assertEquals(original.copy(consumedNutrients = replacement), updated)
        assertTrue(updated.userConfirmed)
        assertEquals(1234L, updated.timestampMillis)
    }

    @Test
    fun `confirmar ingrediente preserva consumo e atualiza somente a decisao`() {
        val evidence = Evidence(
            type = EvidenceType.USER_CONFIRMATION,
            value = "usuário confirmou",
            source = "confirmação do usuário"
        )
        val decision = Decision(
            state = DecisionState.COMPATIBLE,
            shortMessage = "Não encontrei conflito nas evidências disponíveis.",
            evidence = listOf(evidence)
        )

        val updated = MealRecordUpdater.withDecision(original, decision, "texto revisado")

        assertEquals(consumed, updated.consumedNutrients)
        assertTrue(updated.userConfirmed)
        assertEquals(DecisionState.COMPATIBLE, updated.decisionState)
        assertEquals(decision.shortMessage, updated.shortMessage)
        assertEquals("texto revisado", updated.recognizedText)
        assertEquals(listOf(EvidenceType.USER_CONFIRMATION.label), updated.evidenceLabels)
        assertEquals(original.id, updated.id)
        assertEquals(original.timestampMillis, updated.timestampMillis)
    }
}
