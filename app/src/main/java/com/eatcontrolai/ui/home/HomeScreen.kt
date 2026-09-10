package com.eatcontrolai.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.eatcontrolai.core.model.MealRecord
import com.eatcontrolai.ui.Destination
import com.eatcontrolai.ui.EatControlViewModel
import com.eatcontrolai.ui.components.DemoTag
import com.eatcontrolai.ui.components.EcCard
import com.eatcontrolai.ui.components.EcChip
import com.eatcontrolai.ui.components.StateBadge
import com.eatcontrolai.ui.components.StatCard
import com.eatcontrolai.ui.components.StatusDot
import com.eatcontrolai.ui.components.tone
import com.eatcontrolai.ui.theme.EcColors
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    viewModel: EatControlViewModel,
    onNavigate: (Destination) -> Unit,
    onOpenLab: (() -> Unit)?
) {
    val profile by viewModel.profile.collectAsState()
    val glasses by viewModel.glasses.collectAsState()
    val history by viewModel.history.collectAsState()
    val dailyProgress by viewModel.dailyProgress.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxWidth().statusBarsPadding(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Olá, ${profile.displayName}",
                        style = MaterialTheme.typography.headlineSmall,
                        color = EcColors.TextPrimary
                    )

                    Text(
                        if (profile.macroGoals.isConfigured) {
                            "Metas ${profile.macroGoals.definedBy.label}"
                        } else {
                            "Perfil local ativo · sem metas configuradas"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = EcColors.TextMuted
                    )
                }
                val (dotTone, dotLabel) = when {
                    glasses.connected -> EcColors.Mint to "EDGE AI PRONTO"
                    glasses.sourceLabel.contains("celular", ignoreCase = true) -> EcColors.Mint to "IA LOCAL PRONTA"
                    else -> EcColors.Amber to "PROCURANDO ÓCULOS"
                }
                StatusDot(
                    tone = dotTone,
                    label = dotLabel
                )
            }
        }

        item { HeroCard(onAnalyze = { onNavigate(Destination.ANALYZE) }, onPlan = { onNavigate(Destination.PLAN) }) }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    "Acompanhamento de hoje",
                    style = MaterialTheme.typography.titleMedium,
                    color = EcColors.TextPrimary
                )
                if (dailyProgress.isEmpty()) {
                    EcCard {
                        Text(
                            "Nenhuma meta ou consumo confirmado ainda.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = EcColors.TextMuted
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Configure metas no Perfil ou registre uma porção lida de um rótulo.",
                            style = MaterialTheme.typography.bodySmall,
                            color = EcColors.TextFaint
                        )
                    }
                } else {
                    dailyProgress.chunked(2).forEach { row ->
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            row.forEach { progress ->
                                val formatter = java.text.DecimalFormat("0.#")
                                val unit = progress.unit.symbol
                                val goal = progress.goal
                                StatCard(
                                    caption = progress.nutrient.displayName.replaceFirstChar { it.uppercase() },
                                    value = "${formatter.format(progress.consumed)} $unit",
                                    detail = if (goal != null) {
                                        "de ${formatter.format(goal)} $unit · ${profile.macroGoals.definedBy.label}"
                                    } else {
                                        "consumo confirmado · sem meta"
                                    },
                                    progress = goal?.takeIf { it > 0.0 }
                                        ?.let { (progress.consumed / it).coerceIn(0.0, 1.0).toFloat() },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            if (row.size == 1) Spacer(Modifier.weight(1f))
                        }
                    }
                }
            }
        }

        item {
            StatCard(
                caption = "Análises registradas",
                value = "${history.size}",
                detail = if (history.isEmpty()) "nenhuma análise ainda nesta sessão"
                else "registros locais neste aparelho",
                progress = null,
                tone = EcColors.Purple
            )
        }

        item {
            EcCard(
                title = "Últimas escolhas",
                subtitle = if (history.isEmpty()) null else "dados reais desta sessão",
                trailing = {
                    if (history.isNotEmpty()) {
                        EcChip("Ver tudo", tone = EcColors.BlueSoft, selected = true) {
                            onNavigate(Destination.HISTORY)
                        }
                    }
                }
            ) {
                if (history.isEmpty()) {
                    EmptyHistory { onNavigate(Destination.ANALYZE) }
                } else {
                    history.take(3).forEach { record ->
                        RecordRow(record)
                        Spacer(Modifier.height(8.dp))
                    }
                }
            }
        }

        item {
            EcCard(
                title = glasses.sourceLabel,
                subtitle = if (glasses.isMock)
                    "DAT 0.9.0 aguardando credenciais do Wearables Developer Center (ADR-0006)"
                else null,
                trailing = {
                    StatusDot(
                        tone = if (glasses.connected) EcColors.Mint else EcColors.TextFaint,
                        label = if (glasses.connected) "CONECTADO" else "OFFLINE"
                    )
                }
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    DeviceStat("Bateria", glasses.batteryPercent?.let { "$it%" } ?: "—", Modifier.weight(1f))
                    DeviceStat("Câmera", if (glasses.cameraReady) "OK" else "—", Modifier.weight(1f))
                    DeviceStat("Áudio", if (glasses.audioReady) "OK" else "—", Modifier.weight(1f))
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    "A fonte simulada não reporta bateria, então o campo fica vazio em vez de exibir " +
                        "um número que não medimos.",
                    style = MaterialTheme.typography.bodySmall,
                    color = EcColors.TextFaint
                )
            }
        }

        if (onOpenLab != null) {
            item {
                OutlinedButton(onClick = onOpenLab, modifier = Modifier.fillMaxWidth()) {
                    Text("Laboratório de modelos (debug)")
                }
            }
        }
    }
}

@Composable
private fun HeroCard(onAnalyze: () -> Unit, onPlan: () -> Unit) {
    Box(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.linearGradient(
                    listOf(EcColors.SurfaceHigh, EcColors.Surface, EcColors.BackgroundDeep)
                )
            )
            .padding(20.dp)
    ) {
        Column {
            Text(
                "SEU COPILOTO ALIMENTAR",
                style = MaterialTheme.typography.labelSmall,
                color = EcColors.Mint
            )
            Spacer(Modifier.height(12.dp))
            Text("Olhe. Pergunte.", style = MaterialTheme.typography.headlineLarge, color = EcColors.TextPrimary)

            Text(
                "Decida com contexto.",
                style = MaterialTheme.typography.headlineLarge,
                color = EcColors.Mint
            )
            Spacer(Modifier.height(12.dp))
            Text(
                "O Eat Control cruza o que você está olhando com o seu plano antes de responder — " +
                    "e diz quando não tem certeza.",
                style = MaterialTheme.typography.bodySmall,
                color = EcColors.TextSoft
            )
            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onAnalyze,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EcColors.Mint,
                        contentColor = EcColors.OnMint
                    )
                ) { Text("Analisar") }
                OutlinedButton(onClick = onPlan) { Text("Meu plano") }
            }
        }
    }
}

@Composable
private fun DeviceStat(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier
            .clip(RoundedCornerShape(12.dp))
            .background(EcColors.Surface)
            .padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(value, style = MaterialTheme.typography.titleSmall)
        Spacer(Modifier.height(2.dp))
        Text(label, style = MaterialTheme.typography.labelSmall, color = EcColors.TextMuted)
    }
}

@Composable
private fun EmptyHistory(onAnalyze: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Text(
            "Nenhuma análise ainda.",
            style = MaterialTheme.typography.bodyMedium,
            color = EcColors.TextMuted
        )
        Spacer(Modifier.height(4.dp))
        Text(
            "O histórico só mostra registros locais neste aparelho.",
            style = MaterialTheme.typography.bodySmall,
            color = EcColors.TextFaint
        )
        Spacer(Modifier.height(12.dp))
        Button(
            onClick = onAnalyze,
            colors = ButtonDefaults.buttonColors(
                containerColor = EcColors.Mint,
                contentColor = EcColors.OnMint
            )
        ) { Text("Fazer a primeira análise") }
    }
}

private val timeFormat = SimpleDateFormat("HH:mm", Locale.forLanguageTag("pt-BR"))

@Composable
fun RecordRow(record: MealRecord, modifier: Modifier = Modifier) {
    Row(
        modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            Modifier
                .height(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(record.decisionState.tone().copy(alpha = 0.12f))
                .padding(horizontal = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                timeFormat.format(Date(record.timestampMillis)),
                style = MaterialTheme.typography.labelMedium,
                color = record.decisionState.tone()
            )
        }
        Column(Modifier.weight(1f)) {
            Text(record.title, style = MaterialTheme.typography.titleSmall)
            Spacer(Modifier.height(3.dp))
            Text(
                record.shortMessage,
                style = MaterialTheme.typography.bodySmall,
                color = EcColors.TextMuted
            )
            if (record.confirmedItems.isNotEmpty()) {
                Spacer(Modifier.height(4.dp))
                Text(
                    record.confirmedItems.joinToString(),
                    style = MaterialTheme.typography.bodySmall,
                    color = EcColors.TextSoft
                )
            }
            Spacer(Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                StateBadge(record.decisionState)
                if (record.userConfirmed) {
                    EcChip("confirmado", tone = EcColors.BlueSoft, selected = true)
                }
                if (record.containsVisualEstimate) {
                    EcChip("visão assistida", tone = EcColors.Amber, selected = true)
                }
            }
        }
    }
}

@Composable
fun DemoNote(text: String, modifier: Modifier = Modifier) {
    Row(
        modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        DemoTag()
        Text(text, style = MaterialTheme.typography.bodySmall, color = EcColors.TextFaint)
    }
}
