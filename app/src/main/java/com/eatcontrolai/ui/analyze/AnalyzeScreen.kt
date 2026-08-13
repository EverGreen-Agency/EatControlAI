package com.eatcontrolai.ui.analyze

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.eatcontrolai.ui.AnalyzeMode
import com.eatcontrolai.ui.EatControlViewModel
import com.eatcontrolai.ui.components.EcCard
import com.eatcontrolai.ui.components.EcChip
import com.eatcontrolai.ui.components.SectionHeader
import com.eatcontrolai.ui.theme.EcColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyzeScreen(viewModel: EatControlViewModel) {
    val state by viewModel.analyze.collectAsState()
    val profile by viewModel.profile.collectAsState()
    val glasses by viewModel.glasses.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LazyColumn(
        modifier = Modifier.fillMaxWidth().statusBarsPadding(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            SectionHeader(
                title = "Analisar",
                subtitle = "A câmera captura, o OCR roda no aparelho e o motor determinístico cruza " +
                    "com o seu plano antes de responder por áudio."
            )
        }

        item {
            EcCard(title = "Modo de entrada") {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    AnalyzeMode.entries.chunked(2).forEach { row ->
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            row.forEach { mode ->
                                ModeCard(
                                    mode = mode,
                                    selected = mode == state.mode,
                                    onClick = { viewModel.selectMode(mode) },
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
            EcCard(
                title = "O que os óculos estão vendo",
                subtitle = glasses.sourceLabel
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    state.scenes.chunked(2).forEach { row ->
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            row.forEach { scene ->
                                EcChip(
                                    label = scene.title,
                                    tone = EcColors.BlueSoft,
                                    selected = scene.id == state.selectedSceneId,
                                    onClick = { viewModel.selectScene(scene.id) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            if (row.size == 1) Spacer(Modifier.weight(1f))
                        }
                    }
                    Spacer(Modifier.height(2.dp))
                    Text(
                        state.selectedScene.subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = EcColors.TextFaint
                    )
                }
            }
        }

        item {
            EcCard(title = "Ponto de vista") {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(230.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(EcColors.BackgroundDeep)
                        .border(1.dp, EcColors.Line, RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    val frame = state.result?.frameJpeg
                    if (frame == null) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("◉", style = MaterialTheme.typography.headlineMedium, color = EcColors.TextFaint)
                            Spacer(Modifier.height(6.dp))
                            Text(
                                "O frame capturado aparece aqui",
                                style = MaterialTheme.typography.bodySmall,
                                color = EcColors.TextFaint
                            )
                        }
                    } else {
                        val image = remember(frame) {
                            BitmapFactory.decodeByteArray(frame, 0, frame.size).asImageBitmap()
                        }
                        Image(
                            bitmap = image,
                            contentDescription = "Frame capturado",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                Spacer(Modifier.height(10.dp))
                Text(
                    "O rótulo é renderizado e lido pelo OCR real. O mock substitui o hardware, " +
                        "não a inteligência.",
                    style = MaterialTheme.typography.bodySmall,
                    color = EcColors.TextFaint
                )
            }
        }

        item {
            Button(
                onClick = viewModel::analyze,
                enabled = !state.isAnalyzing && state.mode.ready,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = EcColors.Mint,
                    contentColor = EcColors.OnMint
                )
            ) {
                if (state.isAnalyzing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = EcColors.OnMint
                    )
                    Text("   Analisando…")
                } else {
                    Text(if (state.mode.ready) "Analisar ${state.mode.label.lowercase()}" else "Modo indisponível")
                }
            }
        }

        item {
            Text(
                "Perfil ativo: ${profile.restrictions.joinToString { it.allergen.displayName }
                    .ifBlank { "sem restrições cadastradas" }}",
                style = MaterialTheme.typography.bodySmall,
                color = EcColors.TextMuted
            )
        }

        state.error?.let { message ->
            item {
                EcCard {
                    Text(message, style = MaterialTheme.typography.bodyMedium, color = EcColors.RedSoft)
                }
            }
        }
    }

    val result = state.result
    if (state.showResult && result != null) {
        ModalBottomSheet(
            onDismissRequest = viewModel::dismissResult,
            sheetState = sheetState,
            containerColor = EcColors.Surface
        ) {
            ResultSheet(
                result = result,
                onConfirm = viewModel::confirmIngredient,
                onDismiss = viewModel::dismissResult
            )
        }
    }
}

@Composable
private fun ModeCard(
    mode: AnalyzeMode,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tone = if (mode.ready) EcColors.BlueSoft else EcColors.TextFaint
    Column(
        modifier
            .clip(RoundedCornerShape(14.dp))
            .background(if (selected) EcColors.SurfaceHigh else EcColors.Surface)
            .border(
                1.dp,
                if (selected) EcColors.Blue.copy(alpha = 0.4f) else EcColors.Line,
                RoundedCornerShape(14.dp)
            )
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                Modifier
                    .size(30.dp)
                    .clip(RoundedCornerShape(9.dp))
                    .background(tone.copy(alpha = 0.10f)),
                contentAlignment = Alignment.Center
            ) {
                Text(mode.glyph, style = MaterialTheme.typography.labelMedium, color = tone)
            }
            Text(
                mode.label,
                style = MaterialTheme.typography.titleSmall,
                color = if (mode.ready) EcColors.TextPrimary else EcColors.TextMuted
            )
        }
        Spacer(Modifier.height(6.dp))
        Text(
            if (mode.ready) "pronto" else "não implementado",
            style = MaterialTheme.typography.labelSmall,
            color = if (mode.ready) EcColors.Mint else EcColors.TextFaint
        )
    }
}

@Composable
internal fun sheetDivider() {
    Box(Modifier.fillMaxWidth().height(1.dp).background(Color.White.copy(alpha = 0.06f)))
}
