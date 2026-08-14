package com.eatcontrolai.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.eatcontrolai.BuildConfig
import com.eatcontrolai.ui.analyze.AnalyzeScreen
import com.eatcontrolai.ui.history.HistoryScreen
import com.eatcontrolai.ui.home.HomeScreen
import com.eatcontrolai.ui.lab.LabScreen
import com.eatcontrolai.ui.plan.PlanScreen
import com.eatcontrolai.ui.profile.ProfileScreen
import com.eatcontrolai.ui.theme.EcColors
import kotlinx.coroutines.delay

enum class Destination(val label: String, val glyph: String) {
    HOME("Hoje", "⌂"),
    ANALYZE("Analisar", "◉"),
    PLAN("Plano", "✓"),
    HISTORY("Histórico", "↺"),
    PROFILE("Perfil", "◎")
}

/**
 * Navegação escrita à mão.
 *
 * São cinco destinos de topo sem hierarquia — uma biblioteca de navegação aqui só acrescentaria
 * dependência e configuração. O botão voltar retorna para Hoje, que é o comportamento esperado de
 * uma barra inferior.
 */
@Composable
fun EatControlRoot(viewModel: EatControlViewModel) {
    var destination by rememberSaveable { mutableStateOf(Destination.HOME) }
    var showLab by remember { mutableStateOf(false) }
    val toast by viewModel.toast.collectAsState()

    BackHandler(enabled = showLab || destination != Destination.HOME) {
        if (showLab) showLab = false else destination = Destination.HOME
    }

    LaunchedEffect(toast) {
        if (toast != null) {
            delay(3200)
            viewModel.clearToast()
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = EcColors.Background,
        contentColor = EcColors.TextPrimary
    ) {
        Box(Modifier.fillMaxSize()) {
            Box(Modifier.fillMaxSize().padding(bottom = 78.dp)) {
                when {
                    showLab -> LabScreen(viewModel = viewModel, onClose = { showLab = false })
                    destination == Destination.HOME -> HomeScreen(
                        viewModel = viewModel,
                        onNavigate = { destination = it },
                        onOpenLab = if (BuildConfig.DEBUG) ({ showLab = true }) else null
                    )
                    destination == Destination.ANALYZE -> AnalyzeScreen(viewModel)
                    destination == Destination.PLAN -> PlanScreen(viewModel)
                    destination == Destination.HISTORY -> HistoryScreen(viewModel, onNavigate = { destination = it })
                    destination == Destination.PROFILE -> ProfileScreen(viewModel, onNavigate = { destination = it })
                }
            }

            BottomBar(
                current = destination,
                onSelect = { destination = it; showLab = false },
                modifier = Modifier.align(Alignment.BottomCenter)
            )

            if (toast != null) {
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 14.dp)
                        .padding(bottom = 92.dp),
                    containerColor = EcColors.SurfaceHigh,
                    contentColor = EcColors.TextPrimary
                ) {
                    Text(toast.orEmpty(), style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
private fun BottomBar(
    current: Destination,
    onSelect: (Destination) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 12.dp, vertical = 10.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(EcColors.SurfaceRaised)
            .padding(6.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Destination.entries.forEach { item ->
            val selected = item == current
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        if (selected) EcColors.Blue.copy(alpha = 0.16f)
                        else androidx.compose.ui.graphics.Color.Transparent
                    )
                    .clickable { onSelect(item) }
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    item.glyph,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (selected) EcColors.TextPrimary else EcColors.TextMuted
                )
                Spacer(Modifier.height(3.dp))
                Text(
                    item.label,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (selected) EcColors.TextPrimary else EcColors.TextMuted
                )
            }
        }
    }
}
