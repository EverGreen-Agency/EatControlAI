package com.eatcontrolai.ui.history

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.eatcontrolai.core.model.DecisionState
import com.eatcontrolai.core.model.MealRecord
import com.eatcontrolai.ui.Destination
import com.eatcontrolai.ui.EatControlViewModel
import com.eatcontrolai.ui.components.EcCard
import com.eatcontrolai.ui.components.EcChip
import com.eatcontrolai.ui.components.SectionHeader
import com.eatcontrolai.ui.components.tone
import com.eatcontrolai.ui.home.RecordRow
import com.eatcontrolai.ui.components.StateBadge
import com.eatcontrolai.ui.theme.EcColors
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val dayFormat = SimpleDateFormat("EEEE, d 'de' MMMM", Locale.forLanguageTag("pt-BR"))
private val fullDateFormat = SimpleDateFormat("dd/MM/yyyy · HH:mm", Locale.forLanguageTag("pt-BR"))

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(viewModel: EatControlViewModel, onNavigate: (Destination) -> Unit) {
    val history by viewModel.history.collectAsState()
    var selectedRecord by remember { mutableStateOf<MealRecord?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LazyColumn(
        modifier = Modifier.fillMaxWidth().statusBarsPadding(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            SectionHeader(
                title = "Histórico",
                subtitle = "Toque em qualquer refeição para abrir a foto, orientações clínicas do GLP-1 e evidências auditadas."
            )
        }

        if (history.isEmpty()) {
            item {
                EcCard {
                    Text(
                        "Nada registrado ainda.",
                        style = MaterialTheme.typography.titleSmall
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Faça uma análise na aba Analisar e ela aparece aqui, com o estado de decisão, a foto salva e a latência medida.",
                        style = MaterialTheme.typography.bodySmall,
                        color = EcColors.TextMuted
                    )
                }
            }
        } else {
            item { Summary(states = history.map { it.decisionState }) }

            val grouped = history.groupBy { dayFormat.format(Date(it.timestampMillis)) }
            grouped.forEach { (day, records) ->
                item {
                    Text(
                        day.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = EcColors.TextMuted
                    )
                }
                items(records.size) { index ->
                    val record = records[index]
                    val bitmap = remember(record.photoPath) {
                        record.photoPath?.let { path ->
                            runCatching {
                                if (File(path).exists()) BitmapFactory.decodeFile(path)?.asImageBitmap()
                                else null
                            }.getOrNull()
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { selectedRecord = record }
                    ) {
                        EcCard {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                if (bitmap != null) {
                                    Image(
                                        bitmap = bitmap,
                                        contentDescription = "Foto da refeição",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(56.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    RecordRow(record)
                                    Spacer(Modifier.height(8.dp))
                                    Row(
                                        Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            record.evidenceLabels.distinct().joinToString(" · "),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = EcColors.TextFaint
                                        )
                                        Text(
                                            "${record.endToEndMs} ms ➔",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = EcColors.Mint
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    selectedRecord?.let { record ->
        ModalBottomSheet(
            onDismissRequest = { selectedRecord = null },
            sheetState = sheetState,
            containerColor = EcColors.SurfaceRaised,
            contentColor = EcColors.TextPrimary
        ) {
            MealDetailContent(record = record, onClose = { selectedRecord = null })
        }
    }
}

@Composable
private fun MealDetailContent(record: MealRecord, onClose: () -> Unit) {
    val bitmap = remember(record.photoPath) {
        record.photoPath?.let { path ->
            runCatching {
                if (File(path).exists()) BitmapFactory.decodeFile(path)?.asImageBitmap()
                else null
            }.getOrNull()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(bottom = 32.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap,
                contentDescription = "Foto da Refeição",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp)
                    .clip(RoundedCornerShape(16.dp))
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StateBadge(record.decisionState)
            Text(
                fullDateFormat.format(Date(record.timestampMillis)),
                style = MaterialTheme.typography.labelSmall,
                color = EcColors.TextMuted
            )
        }

        Text(
            text = record.title,
            style = MaterialTheme.typography.headlineSmall,
            color = EcColors.TextPrimary
        )

        // Cartão de Orientação Clínica GLP-1
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(record.decisionState.tone().copy(alpha = 0.12f))
                .padding(14.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    "ORIENTAÇÃO DE SEGURANÇA ALIMENTAR",
                    style = MaterialTheme.typography.labelSmall,
                    color = record.decisionState.tone()
                )
                Text(
                    text = record.shortMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = EcColors.TextPrimary
                )
            }
        }

        if (record.confirmedItems.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    "ITENS CONFIRMADOS",
                    style = MaterialTheme.typography.labelSmall,
                    color = EcColors.TextMuted
                )
                Text(
                    text = record.confirmedItems.joinToString(" · "),
                    style = MaterialTheme.typography.bodyMedium,
                    color = EcColors.TextSoft
                )
            }
        }

        if (record.consumedNutrients.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    "NUTRIENTES REGISTRADOS",
                    style = MaterialTheme.typography.labelSmall,
                    color = EcColors.TextMuted
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    record.consumedNutrients.take(4).forEach { nutrient ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(EcColors.SurfaceHigh)
                                .padding(8.dp)
                        ) {
                            Column {
                                Text(
                                    nutrient.nutrient.displayName,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = EcColors.TextMuted
                                )
                                Text(
                                    "${nutrient.value.toInt()}${nutrient.nutrient.unit}",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = EcColors.Mint
                                )
                            }
                        }
                    }
                }
            }
        }

        if (record.recognizedText.isNotBlank()) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    "TEXTO EXTRAÍDO DA IMAGEM (OCR)",
                    style = MaterialTheme.typography.labelSmall,
                    color = EcColors.TextMuted
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(EcColors.Background)
                        .padding(12.dp)
                ) {
                    Text(
                        text = record.recognizedText,
                        style = MaterialTheme.typography.bodySmall,
                        color = EcColors.TextMuted
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "Tempo de inferência: ${record.endToEndMs} ms",
                style = MaterialTheme.typography.labelSmall,
                color = EcColors.TextFaint
            )
            Text(
                record.evidenceLabels.joinToString(" · "),
                style = MaterialTheme.typography.labelSmall,
                color = EcColors.TextFaint
            )
        }
    }
}

@Composable
private fun Summary(states: List<DecisionState>) {
    val counts = DecisionState.entries.associateWith { state -> states.count { it == state } }
        .filterValues { it > 0 }

    EcCard(title = "Nesta sessão") {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            counts.forEach { (state, count) ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(
                        state.name,
                        style = MaterialTheme.typography.bodySmall,
                        color = state.tone()
                    )
                    Text("$count", style = MaterialTheme.typography.titleSmall)
                }
            }
        }
    }
}
