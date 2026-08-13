package com.eatcontrolai.ui.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.eatcontrolai.core.model.DecisionState
import com.eatcontrolai.ui.Destination
import com.eatcontrolai.ui.EatControlViewModel
import com.eatcontrolai.ui.components.EcCard
import com.eatcontrolai.ui.components.SectionHeader
import com.eatcontrolai.ui.components.tone
import com.eatcontrolai.ui.home.RecordRow
import com.eatcontrolai.ui.theme.EcColors
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val dayFormat = SimpleDateFormat("EEEE, d 'de' MMMM", Locale.forLanguageTag("pt-BR"))

/**
 * Histórico real: cada item veio de uma execução da pipeline nesta sessão.
 *
 * Persistência entre execuções é a trilha seguinte — hoje o histórico vive em memória, o que
 * também significa que nenhuma imagem é gravada em disco.
 */
@Composable
fun HistoryScreen(viewModel: EatControlViewModel, onNavigate: (Destination) -> Unit) {
    val history by viewModel.history.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxWidth().statusBarsPadding(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            SectionHeader(
                title = "Histórico",
                subtitle = "Mais do que uma lista: cada registro guarda a decisão, as evidências " +
                    "usadas e o que você confirmou."
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
                        "Faça uma análise na aba Analisar e ela aparece aqui, com o estado de " +
                            "decisão e a latência medida.",
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
                    EcCard {
                        RecordRow(record)
                        Spacer(Modifier.height(10.dp))
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                record.evidenceLabels.distinct().joinToString(" · "),
                                style = MaterialTheme.typography.labelSmall,
                                color = EcColors.TextFaint
                            )
                            Text(
                                "${record.endToEndMs} ms",
                                style = MaterialTheme.typography.labelSmall,
                                color = EcColors.TextFaint
                            )
                        }
                    }
                }
            }
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
