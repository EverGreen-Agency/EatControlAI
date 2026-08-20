package com.eatcontrolai.domain.nutrition

import com.eatcontrolai.core.model.Decision
import com.eatcontrolai.core.model.MealRecord
import com.eatcontrolai.core.model.NutrientAmount

/**
 * Atualizações preservadoras para um registro já persistido.
 *
 * Uma confirmação ou nova quantidade altera somente os campos correspondentes. Recriar o registro
 * a partir do resultado da pipeline apagaria confirmação/consumo anteriores e mudaria o timestamp.
 */
object MealRecordUpdater {

    fun withConsumption(
        current: MealRecord,
        consumedNutrients: List<NutrientAmount>
    ): MealRecord = current.copy(consumedNutrients = consumedNutrients)

    fun withDecision(
        current: MealRecord,
        decision: Decision,
        recognizedText: String
    ): MealRecord = current.copy(
        decisionState = decision.state,
        shortMessage = decision.shortMessage,
        recognizedText = recognizedText,
        evidenceLabels = decision.evidence.map { it.type.label },
        userConfirmed = true
    )
}
