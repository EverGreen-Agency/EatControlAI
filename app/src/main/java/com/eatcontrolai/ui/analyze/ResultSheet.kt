package com.eatcontrolai.ui.analyze

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.eatcontrolai.core.model.Allergen
import com.eatcontrolai.orchestration.InteractionResult
import com.eatcontrolai.ui.components.StateBadge
import com.eatcontrolai.ui.components.tone
import com.eatcontrolai.ui.theme.EcColors

/**
 * Resultado da análise.
 *
 * Mostra a cadeia inteira porque é ela que sustenta a tese do produto: qual evidência foi usada,
 * em que ordem de precedência, e quanto tempo cada etapa levou. Quando algo ficou sem resposta, a
 * folha oferece a pergunta de confirmação em vez de arriscar um palpite — é o Cenário 3 da demo
 * ideal (`contexto-gpt.md` §56).
 */
@Composable
fun ResultSheet(
    result: InteractionResult,
    onConfirm: (Allergen, Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    val decision = result.decision
    val tone = decision.state.tone()

    Column(
        Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .navigationBarsPadding()
            .padding(horizontal = 20.dp)
            .padding(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Column {
            StateBadge(decision.state)
            Spacer(Modifier.height(10.dp))
            Text(decision.shortMessage, style = MaterialTheme.typography.headlineSmall)
            result.scannedEan?.let { ean ->
                Spacer(Modifier.height(8.dp))
                Text(
                    result.productName?.let { "$it · EAN $ean" } ?: "EAN $ean · fora do catálogo",
                    style = MaterialTheme.typography.bodySmall,
                    color = EcColors.TextMuted
                )
            }
        }

        if (decision.reasons.isNotEmpty()) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(tone.copy(alpha = 0.07f))
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text("POR QUE", style = MaterialTheme.typography.labelSmall, color = tone)
                decision.reasons.forEach {
                    Text("• ${it.text}", style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        if (decision.unresolved.isNotEmpty()) {
            ConfirmationBlock(unresolved = decision.unresolved, onConfirm = onConfirm)
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("EVIDÊNCIAS USADAS", style = MaterialTheme.typography.labelSmall, color = EcColors.TextMuted)
            decision.evidence.sortedBy { it.type.rank }.forEach { evidence ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(EcColors.SurfaceRaised)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        Modifier
                            .size(26.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(EcColors.Blue.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "${evidence.type.rank}",
                            style = MaterialTheme.typography.labelMedium,
                            color = EcColors.BlueSoft
                        )
                    }
                    Column(Modifier.weight(1f)) {
                        Text(evidence.type.label, style = MaterialTheme.typography.titleSmall)
                        Text(
                            evidence.source,
                            style = MaterialTheme.typography.labelSmall,
                            color = EcColors.TextFaint
                        )
                    }
                    if (evidence.type.isProbabilistic) {
                        Text(
                            "probabilística",
                            style = MaterialTheme.typography.labelSmall,
                            color = EcColors.Amber
                        )
                    }
                }
            }
            Text(
                "Rank menor = evidência mais confiável. A ordem é a de docs/SPEC.md.",
                style = MaterialTheme.typography.bodySmall,
                color = EcColors.TextFaint
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("LATÊNCIA", style = MaterialTheme.typography.labelSmall, color = EcColors.TextMuted)
            result.metrics.forEach { metric ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(
                        metric.stage.key,
                        style = MaterialTheme.typography.bodySmall,
                        color = EcColors.TextMuted
                    )
                    Text(
                        "${metric.latencyMs} ms",
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        if (result.recognizedText.isNotBlank()) {
            Column {
                Text(
                    "TEXTO RECONHECIDO",
                    style = MaterialTheme.typography.labelSmall,
                    color = EcColors.TextMuted
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    result.recognizedText,
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = FontFamily.Monospace,
                    color = EcColors.TextFaint
                )
            }
        }

        OutlinedButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
            Text("Fechar")
        }

        Text(
            "Orientação educativa baseada no perfil configurado. Não diagnostica, não altera doses " +
                "e não substitui acompanhamento profissional.",
            style = MaterialTheme.typography.labelSmall,
            color = EcColors.TextFaint
        )
    }
}

@Composable
private fun ConfirmationBlock(
    unresolved: List<Allergen>,
    onConfirm: (Allergen, Boolean) -> Unit
) {
    val allergen = unresolved.first()
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(EcColors.Blue.copy(alpha = 0.07f))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("PERGUNTA DE CONFIRMAÇÃO", style = MaterialTheme.typography.labelSmall, color = EcColors.BlueSoft)
        Text(
            "Este preparo leva ${allergen.displayName}?",
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            "Sua resposta entra como evidência de rank 4 e a decisão é recalculada pelo mesmo motor.",
            style = MaterialTheme.typography.bodySmall,
            color = EcColors.TextMuted
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = { onConfirm(allergen, true) },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = EcColors.Red.copy(alpha = 0.18f),
                    contentColor = EcColors.RedSoft
                )
            ) { Text("Sim, leva") }
            Button(
                onClick = { onConfirm(allergen, false) },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = EcColors.Mint,
                    contentColor = EcColors.OnMint
                )
            ) { Text("Não leva") }
        }
    }
}
