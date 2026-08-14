package com.eatcontrolai.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.eatcontrolai.core.model.DecisionState
import com.eatcontrolai.ui.theme.EcColors

/** Cartão padrão do app: fundo em gradiente sutil, borda fina, título opcional. */
@Composable
fun EcCard(
    modifier: Modifier = Modifier,
    title: String? = null,
    subtitle: String? = null,
    trailing: @Composable (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.verticalGradient(
                    listOf(EcColors.SurfaceRaised, EcColors.Surface)
                )
            )
            .border(1.dp, EcColors.Line, RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        if (title != null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(Modifier.weight(1f)) {
                    Text(title, style = MaterialTheme.typography.titleSmall, color = EcColors.TextPrimary)
                    if (subtitle != null) {

                        Spacer(Modifier.height(4.dp))
                        Text(
                            subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = EcColors.TextMuted
                        )
                    }
                }
                trailing?.invoke()
            }
            Spacer(Modifier.height(12.dp))
        }
        content()
    }
}

/** Chip de rótulo. [selected] o acende; [tone] define a cor semântica. */
@Composable
fun EcChip(
    label: String,
    modifier: Modifier = Modifier,
    tone: Color = EcColors.TextSoft,
    selected: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    val base = if (selected) tone else EcColors.TextSoft
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(if (selected) tone.copy(alpha = 0.12f) else Color.Transparent)
            .border(
                1.dp,
                if (selected) tone.copy(alpha = 0.4f) else EcColors.Line,
                CircleShape
            )
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = base)
    }
}

/**
 * Marca visual para número que o app ainda não calcula.
 *
 * Existe por decisão de projeto: mostrar um valor inventado sem sinalizar seria o mesmo
 * "fingir certeza" que o produto inteiro se propõe a evitar (`contexto-gpt.md` §18).
 */
@Composable
fun DemoTag(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(EcColors.Purple.copy(alpha = 0.14f))
            .padding(horizontal = 6.dp, vertical = 3.dp)
    ) {
        Text(
            "DEMO",
            style = MaterialTheme.typography.labelSmall,
            color = EcColors.Purple
        )
    }
}

/** Cores e texto canônicos de cada estado de decisão. UI e domínio falam a mesma língua. */
fun DecisionState.tone(): Color = when (this) {
    DecisionState.COMPATIBLE -> EcColors.Mint
    DecisionState.INCOMPATIBLE -> EcColors.Red
    DecisionState.NEEDS_CONFIRMATION -> EcColors.Amber
    DecisionState.INSUFFICIENT_INFORMATION -> EcColors.Slate
}

fun DecisionState.label(): String = when (this) {
    DecisionState.COMPATIBLE -> "COMPATÍVEL"
    DecisionState.INCOMPATIBLE -> "INCOMPATÍVEL"
    DecisionState.NEEDS_CONFIRMATION -> "PRECISA CONFIRMAR"
    DecisionState.INSUFFICIENT_INFORMATION -> "INFORMAÇÃO INSUFICIENTE"
}

@Composable
fun StateBadge(state: DecisionState, modifier: Modifier = Modifier) {
    val tone = state.tone()
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(tone.copy(alpha = 0.12f))
            .padding(horizontal = 9.dp, vertical = 5.dp)
    ) {
        Text(state.label(), style = MaterialTheme.typography.labelSmall, color = tone)
    }
}

@Composable
fun EcProgress(fraction: Float, modifier: Modifier = Modifier, tone: Color = EcColors.Mint) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(7.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.06f))
    ) {
        Box(
            Modifier
                .fillMaxWidth(fraction.coerceIn(0f, 1f))
                .fillMaxHeight()
                .clip(CircleShape)
                .background(Brush.horizontalGradient(listOf(EcColors.Blue, tone)))
        )
    }
}

/** Cartão de métrica. [isDemo] marca valores que ainda não vêm de dado real. */
@Composable
fun StatCard(
    caption: String,
    value: String,
    detail: String,
    progress: Float?,
    modifier: Modifier = Modifier,
    tone: Color = EcColors.Blue,
    isDemo: Boolean = false
) {
    EcCard(modifier = modifier) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(caption, style = MaterialTheme.typography.bodySmall, color = EcColors.TextMuted)
            if (isDemo) DemoTag() else Box(
                Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(tone.copy(alpha = 0.5f))
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(value, style = MaterialTheme.typography.headlineMedium, color = EcColors.TextPrimary)
        Spacer(Modifier.height(4.dp))
        Text(detail, style = MaterialTheme.typography.bodySmall, color = EcColors.TextMuted)
        if (progress != null) {
            Spacer(Modifier.height(10.dp))
            EcProgress(progress, tone = tone)
        }
    }
}

/** Linha de lista com ícone textual, título, descrição e um slot à direita. */
@Composable
fun EcRow(
    glyph: String,
    title: String,
    detail: String,
    modifier: Modifier = Modifier,
    glyphTone: Color = EcColors.BlueSoft,
    onClick: (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(11.dp)
    ) {
        Box(
            Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(11.dp))
                .background(glyphTone.copy(alpha = 0.10f)),
            contentAlignment = Alignment.Center
        ) {
            Text(glyph, style = MaterialTheme.typography.labelMedium, color = glyphTone)
        }
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleSmall, color = EcColors.TextPrimary)
            Spacer(Modifier.height(3.dp))
            Text(detail, style = MaterialTheme.typography.bodySmall, color = EcColors.TextMuted)
        }
        trailing?.invoke()
    }
}

@Composable
fun EcToggle(checked: Boolean, onToggle: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .width(44.dp)
            .height(25.dp)
            .clip(CircleShape)
            .background(if (checked) EcColors.Mint.copy(alpha = 0.30f) else EcColors.SurfaceHigh)
            .clickable { onToggle() }
            .padding(3.dp),
        contentAlignment = if (checked) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Box(
            Modifier
                .size(19.dp)
                .clip(CircleShape)
                .background(if (checked) EcColors.Mint else EcColors.TextSoft)
        )
    }
}

@Composable
fun SectionHeader(title: String, subtitle: String, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxWidth()) {
        Text(title, style = MaterialTheme.typography.headlineMedium, color = EcColors.TextPrimary)
        Spacer(Modifier.height(6.dp))
        Text(subtitle, style = MaterialTheme.typography.bodySmall, color = EcColors.TextMuted)
    }
}


@Composable
fun StatusDot(tone: Color, label: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .clip(CircleShape)
            .background(tone.copy(alpha = 0.10f))
            .padding(horizontal = 9.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(Modifier.size(7.dp).clip(CircleShape).background(tone))
        Text(label, style = MaterialTheme.typography.labelSmall, color = tone, fontWeight = FontWeight.Bold)
    }
}
