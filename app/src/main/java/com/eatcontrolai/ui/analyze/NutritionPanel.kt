package com.eatcontrolai.ui.analyze

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.eatcontrolai.core.model.GoalStatus
import com.eatcontrolai.core.model.NutrientAmount
import com.eatcontrolai.core.model.NutrientProgress
import com.eatcontrolai.core.model.NutritionFacts
import com.eatcontrolai.ui.components.EcChip
import com.eatcontrolai.ui.theme.EcColors
import java.util.Locale

/**
 * Painel nutricional do resultado (etapa N1).
 *
 * Regras de exibição que sustentam a honestidade da tela:
 *
 *  - sem tabela legível, mostra ausência de dado — nunca zeros;
 *  - a quantidade consumida só aparece depois de o usuário confirmar quantas porções comeu;
 *  - avisos do parser são exibidos, não escondidos;
 *  - sem meta configurada, mostra consumo sem dizer se está alto ou baixo.
 */
@Composable
fun NutritionPanel(
    facts: NutritionFacts,
    portions: Double,
    consumed: List<NutrientAmount>,
    dailyProgress: List<NutrientProgress>,
    goalsConfigured: Boolean,
    consumptionLogged: Boolean,
    onPortionsChange: (Double) -> Unit,
    onRegister: () -> Unit
) {
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(EcColors.SurfaceRaised)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            "TABELA NUTRICIONAL",
            style = MaterialTheme.typography.labelSmall,
            color = EcColors.TextMuted
        )

        if (facts.isEmpty) {
            Text(
                "Não encontrei tabela nutricional legível nesta imagem.",
                style = MaterialTheme.typography.bodySmall,
                color = EcColors.TextFaint
            )
            facts.warnings.forEach { warning ->
                Text(
                    "• $warning",
                    style = MaterialTheme.typography.bodySmall,
                    color = EcColors.Amber
                )
            }
            return@Column
        }

        facts.portion?.let { portion ->
            Text(
                "Porção declarada: ${portion.amount.format()} ${portion.unit.symbol}",
                style = MaterialTheme.typography.bodySmall,
                color = EcColors.TextMuted
            )
        }

        Text("QUANTAS PORÇÕES VOCÊ COMEU", style = MaterialTheme.typography.labelSmall, color = EcColors.TextMuted)
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
            listOf(0.5, 1.0, 1.5, 2.0).forEach { option ->
                EcChip(
                    label = option.format(),
                    tone = EcColors.BlueSoft,
                    selected = option == portions,
                    onClick = { onPortionsChange(option) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        if (consumed.isEmpty()) {
            Text(
                "Os valores lidos não têm base declarada (porção ou 100 g), então não posso calcular o consumo.",
                style = MaterialTheme.typography.bodySmall,
                color = EcColors.Amber
            )
        } else {
            Spacer(Modifier.height(2.dp))
            Text("CONSUMO NESTA REFEIÇÃO", style = MaterialTheme.typography.labelSmall, color = EcColors.TextMuted)
            consumed.forEach { amount ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(
                        amount.nutrient.displayName,
                        style = MaterialTheme.typography.bodySmall,
                        color = EcColors.TextPrimary
                    )
                    Text(
                        "${amount.value.format()} ${amount.unit.symbol}",
                        style = MaterialTheme.typography.bodySmall,
                        color = EcColors.TextPrimary
                    )
                }
            }

            OutlinedButton(
                onClick = onRegister,
                enabled = !consumptionLogged,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (consumptionLogged) "Registrado no dia" else "Registrar no total do dia")
            }
        }

        facts.warnings.forEach { warning ->
            Text("• $warning", style = MaterialTheme.typography.bodySmall, color = EcColors.Amber)
        }

        if (dailyProgress.isNotEmpty()) {
            Spacer(Modifier.height(2.dp))
            Text("TOTAL DE HOJE", style = MaterialTheme.typography.labelSmall, color = EcColors.TextMuted)
            dailyProgress.forEach { progress ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(
                        progress.nutrient.displayName,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        progress.summary(),
                        style = MaterialTheme.typography.bodySmall,
                        color = when (progress.status) {
                            GoalStatus.BELOW -> EcColors.BlueSoft
                            GoalStatus.MET -> EcColors.Mint
                            GoalStatus.ABOVE -> EcColors.Amber
                            GoalStatus.NO_GOAL -> EcColors.TextMuted
                        }
                    )
                }
            }
        }

        if (!goalsConfigured) {
            Text(
                "Sem metas configuradas, mostro apenas o consumo. Metas são definidas por você ou pelo " +
                    "seu profissional de saúde — o aplicativo não define meta sozinho.",
                style = MaterialTheme.typography.bodySmall,
                color = EcColors.TextFaint
            )
        }
    }
}

/** Frase puramente aritmética: subtração contra a meta configurada, sem julgar o alimento. */
private fun NutrientProgress.summary(): String {
    val unit = nutrient.unit.symbol
    return when (status) {
        GoalStatus.NO_GOAL -> "${consumed.format()} $unit"
        GoalStatus.MET -> "${consumed.format()} $unit · meta atingida"
        GoalStatus.BELOW -> "${consumed.format()} $unit · faltam ${remaining!!.format()} $unit"
        GoalStatus.ABOVE -> "${consumed.format()} $unit · ${excess!!.format()} $unit acima da meta"
    }
}

/** Uma casa decimal, vírgula decimal, sem `.0` inútil. */
private fun Double.format(): String {
    val rounded = String.format(Locale.forLanguageTag("pt-BR"), "%.1f", this)
    return rounded.removeSuffix(",0")
}
