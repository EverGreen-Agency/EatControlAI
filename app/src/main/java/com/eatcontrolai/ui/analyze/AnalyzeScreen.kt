package com.eatcontrolai.ui.analyze

import android.graphics.BitmapFactory
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import androidx.compose.runtime.collectAsState
import com.eatcontrolai.core.model.Allergen
import com.eatcontrolai.core.model.DecisionState
import com.eatcontrolai.metrics.StageMetric

/**
 * Tela única do MVP: a vertical do rótulo, de ponta a ponta e visível.
 *
 * Deliberadamente mostra o "trabalho" — POV, texto reconhecido, evidências e latência por etapa.
 * No produto final nada disso aparece; aqui é o que prova viabilidade técnica para a banca.
 */
@Composable
fun AnalyzeScreen(viewModel: AnalyzeViewModel, modifier: Modifier = Modifier) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Header(connected = state.glassesConnected)

        ProfileCard(
            restrictions = state.profile.restrictions,
            onToggle = viewModel::toggleRestriction
        )

        SceneSelector(
            scenes = state.scenes.map { it.id to it.title },
            selectedId = state.selectedSceneId,
            onSelect = viewModel::selectScene
        )

        GlassesPovCard(
            subtitle = state.selectedScene.subtitle,
            frameJpeg = state.result?.frameJpeg
        )

        Button(
            onClick = viewModel::analyze,
            enabled = !state.isAnalyzing,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (state.isAnalyzing) {
                CircularProgressIndicator(
                    modifier = Modifier.height(18.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Text("  Analisando…")
            } else {
                Text("Analisar rótulo")
            }
        }

        state.error?.let { ErrorCard(it) }

        state.result?.let { result ->
            DecisionCard(
                state = result.decision.state,
                message = result.decision.shortMessage,
                reasons = result.decision.reasons.map { it.text }
            )
            EvidenceCard(
                recognizedText = result.recognizedText,
                evidenceLines = result.decision.evidence.map {
                    "${it.type.name} (rank ${it.type.rank}) · ${it.source}"
                }
            )
            MetricsCard(result.metrics)
        }
    }
}

@Composable
private fun Header(connected: Boolean) {
    Column {
        Text("Eat Control AI", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text(
            if (connected) "● Óculos simulados conectados" else "○ Conectando…",
            style = MaterialTheme.typography.bodySmall,
            color = if (connected) Color(0xFF2E7D32) else MaterialTheme.colorScheme.outline
        )
    }
}

@Composable
private fun ProfileCard(restrictions: Set<Allergen>, onToggle: (Allergen) -> Unit) {
    SectionCard(title = "Perfil — João") {
        Text(
            "Toque para alternar a restrição e ver a mesma imagem mudar de decisão.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.outline
        )
        Spacer(Modifier.height(8.dp))
        ChipRow(
            items = Allergen.entries.take(5).map { it to it.displayName },
            isSelected = { it in restrictions },
            onClick = onToggle
        )
    }
}

@Composable
private fun SceneSelector(
    scenes: List<Pair<String, String>>,
    selectedId: String,
    onSelect: (String) -> Unit
) {
    SectionCard(title = "O que os óculos estão vendo") {
        ChipRow(
            items = scenes,
            isSelected = { it == selectedId },
            onClick = onSelect
        )
    }
}

@Composable
private fun GlassesPovCard(subtitle: String, frameJpeg: ByteArray?) {
    SectionCard(title = "Ponto de vista") {
        Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
        Spacer(Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .background(Color(0xFF111111), RoundedCornerShape(12.dp))
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (frameJpeg == null) {
                Text(
                    "O frame capturado aparece aqui",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF888888)
                )
            } else {
                val image = remember(frameJpeg) {
                    BitmapFactory.decodeByteArray(frameJpeg, 0, frameJpeg.size).asImageBitmap()
                }
                Image(
                    bitmap = image,
                    contentDescription = "Frame capturado pelos óculos simulados",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun DecisionCard(state: DecisionState, message: String, reasons: List<String>) {
    val (label, color) = when (state) {
        DecisionState.COMPATIBLE -> "COMPATÍVEL" to Color(0xFF2E7D32)
        DecisionState.INCOMPATIBLE -> "INCOMPATÍVEL" to Color(0xFFC62828)
        DecisionState.NEEDS_CONFIRMATION -> "PRECISA CONFIRMAR" to Color(0xFFEF6C00)
        DecisionState.INSUFFICIENT_INFORMATION -> "INFORMAÇÃO INSUFICIENTE" to Color(0xFF546E7A)
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.08f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(label, color = color, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.height(8.dp))
            Text(message, style = MaterialTheme.typography.titleMedium)
            if (reasons.isNotEmpty()) {
                Spacer(Modifier.height(12.dp))
                HorizontalDivider()
                Spacer(Modifier.height(8.dp))
                reasons.forEach {
                    Text("• $it", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
private fun EvidenceCard(recognizedText: String, evidenceLines: List<String>) {
    SectionCard(title = "Evidências") {
        evidenceLines.forEach {
            Text("• $it", style = MaterialTheme.typography.bodySmall)
        }
        Spacer(Modifier.height(10.dp))
        Text("Texto reconhecido pelo OCR", style = MaterialTheme.typography.labelMedium)
        Spacer(Modifier.height(4.dp))
        Text(
            recognizedText.ifBlank { "(nada reconhecido)" },
            style = MaterialTheme.typography.bodySmall,
            fontFamily = FontFamily.Monospace,
            color = MaterialTheme.colorScheme.outline
        )
    }
}

@Composable
private fun MetricsCard(metrics: List<StageMetric>) {
    SectionCard(title = "Latência por etapa") {
        metrics.forEach { metric ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(metric.stage.key, style = MaterialTheme.typography.bodySmall)
                Text(
                    "${metric.latencyMs} ms",
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Medium
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(
            "Alvo do NFR-002: p95 < 2500 ms no fluxo de rótulo.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.outline
        )
    }
}

@Composable
private fun ErrorCard(message: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(message, Modifier.padding(16.dp), style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun SectionCard(title: String, content: @Composable () -> Unit) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))
            content()
        }
    }
}

@Composable
private fun <T> ChipRow(
    items: List<Pair<T, String>>,
    isSelected: (T) -> Boolean,
    onClick: (T) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        items.chunked(2).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                row.forEach { (value, label) ->
                    FilterChip(
                        selected = isSelected(value),
                        onClick = { onClick(value) },
                        label = { Text(label, style = MaterialTheme.typography.labelMedium) },
                        colors = FilterChipDefaults.filterChipColors(),
                        modifier = Modifier.weight(1f)
                    )
                }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}
