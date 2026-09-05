package com.eatcontrolai.ui.plan

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.eatcontrolai.domain.glp1.EscalationLevel
import com.eatcontrolai.domain.glp1.SymptomKind
import com.eatcontrolai.domain.glp1.SymptomReport
import com.eatcontrolai.ui.components.EcCard
import com.eatcontrolai.ui.theme.EcColors
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Registro de como a pessoa se sentiu.
 *
 * Duas fronteiras que a validação clínica exigiu e que esta tela precisa deixar visíveis: sintoma é
 * **relato**, nunca inferência do aplicativo; e sinal de alerta interrompe a orientação alimentar em
 * vez de virar mais um alerta no meio da lista.
 */
@Composable
fun SymptomCard(
    reports: List<SymptomReport>,
    onReport: (SymptomKind) -> Unit,
    onClear: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    EcCard(
        title = "Como você tem se sentido",
        subtitle = "Fica só neste aparelho. Serve para o app lembrar do que você mesma anotou — " +
            "não para adivinhar causa."
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            if (reports.isNotEmpty()) {
                reports.take(3).forEach { report ->
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            report.kind.displayName.replaceFirstChar { it.uppercase() },
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            dateFormat.format(Date(report.timestampMillis)),
                            style = MaterialTheme.typography.labelSmall,
                            color = EcColors.TextFaint
                        )
                    }
                }
                Text(
                    "Apagar registros",
                    style = MaterialTheme.typography.labelSmall,
                    color = EcColors.RedSoft,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(onClick = onClear)
                        .padding(vertical = 4.dp)
                )
            }

            Text(
                if (expanded) "Toque para registrar" else "Registrar como me sinto",
                style = MaterialTheme.typography.labelSmall,
                color = EcColors.BlueSoft,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { expanded = !expanded }
                    .padding(vertical = 4.dp)
            )

            if (expanded) {
                SymptomGroup(
                    caption = "DESCONFORTO",
                    hint = "Entra como contexto e pode virar orientação de procurar seu profissional.",
                    kinds = SymptomKind.entries.filter { it.level == EscalationLevel.PROFESSIONAL },
                    tone = EcColors.Amber,
                    onReport = onReport
                )
                SymptomGroup(
                    caption = "SINAIS DE ALERTA",
                    hint = "Se você registrar um destes, o app para de avaliar refeição e " +
                        "orienta procurar avaliação médica.",
                    kinds = SymptomKind.entries.filter { it.level == EscalationLevel.MEDICAL_EVALUATION },
                    tone = EcColors.Red,
                    onReport = onReport
                )
            }
        }
    }
}

@Composable
private fun SymptomGroup(
    caption: String,
    hint: String,
    kinds: List<SymptomKind>,
    tone: androidx.compose.ui.graphics.Color,
    onReport: (SymptomKind) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(caption, style = MaterialTheme.typography.labelSmall, color = tone)
        Text(hint, style = MaterialTheme.typography.labelSmall, color = EcColors.TextFaint)
        kinds.forEach { kind ->
            Text(
                kind.displayName.replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(tone.copy(alpha = 0.07f))
                    .clickable { onReport(kind) }
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            )
        }
    }
}

private val dateFormat = SimpleDateFormat("dd/MM HH:mm", Locale.forLanguageTag("pt-BR"))
