package com.eatcontrolai.ui.analyze

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.eatcontrolai.domain.glp1.EscalationLevel
import com.eatcontrolai.domain.glp1.Finding
import com.eatcontrolai.domain.glp1.FindingKind
import com.eatcontrolai.domain.glp1.Glp1Assessment
import com.eatcontrolai.domain.glp1.HistoryAlert
import com.eatcontrolai.domain.glp1.hasSomethingToSay
import com.eatcontrolai.ui.components.EcChip
import com.eatcontrolai.ui.theme.EcColors

/**
 * A leitura do rule pack GLP-1 sobre a refeição.
 *
 * Painel separado do veredito de compatibilidade de propósito: são duas réguas diferentes. O motor
 * determinístico responde sobre conflito com o plano de restrições; isto aqui responde sobre o
 * recorte GLP-1, e cada achado mostra de onde veio para que nada pareça mais certo do que é.
 */
@Composable
fun Glp1Panel(assessment: Glp1Assessment, historyAlerts: List<HistoryAlert> = emptyList()) {
    val suppressed = assessment.analysisSuppressed
    val tone = when {
        suppressed -> EcColors.Red
        assessment.findings.any { it.kind == FindingKind.ATTENTION } -> EcColors.Amber
        else -> EcColors.BlueSoft
    }

    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(if (suppressed) tone.copy(alpha = 0.12f) else EcColors.SurfaceRaised)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                if (suppressed) "ENCAMINHAMENTO" else "SEU PLANO GLP-1",
                style = MaterialTheme.typography.labelSmall,
                color = tone
            )
            if (assessment.underReview) EcChip(label = "regra em revisão")
        }

        // Quando só há lembrete de histórico, a frase do pack seria "não avaliei esta refeição" —
        // verdadeira e fora de contexto num painel que existe para mostrar o lembrete.
        if (assessment.hasSomethingToSay) {
            Text(assessment.shortMessage, style = MaterialTheme.typography.titleMedium)
        }

        assessment.findings.forEach { FindingRow(it) }

        if (assessment.questions.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    "PERGUNTAS",
                    style = MaterialTheme.typography.labelSmall,
                    color = EcColors.TextMuted
                )
                assessment.questions.forEach {
                    Text(it, style = MaterialTheme.typography.bodySmall, color = EcColors.TextSoft)
                }
            }
        }

        // As ressalvas obrigatórias da validação — exercício de força junto de meta de proteína,
        // encaminhamento profissional, regra em revisão — são compostas pelo próprio rule pack.
        // Filtrar por igualdade o que já foi mostrado evita reimplementar essas regras aqui e sair
        // de sincronia com a redação aprovada.
        val shown = assessment.findings.map { it.text }.toSet() + assessment.questions
        assessment.detail.filterNot { it in shown }.forEach { line ->
            Text(line, style = MaterialTheme.typography.bodySmall, color = EcColors.TextMuted)
        }

        if (assessment.escalation == EscalationLevel.PROFESSIONAL && !suppressed) {
            Text(
                "Vale levar este ponto ao seu profissional de saúde.",
                style = MaterialTheme.typography.labelSmall,
                color = EcColors.Amber
            )
        }

        if (historyAlerts.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    "DO SEU HISTÓRICO",
                    style = MaterialTheme.typography.labelSmall,
                    color = EcColors.TextMuted
                )
                historyAlerts.forEach {
                    Text(
                        it.text,
                        style = MaterialTheme.typography.bodySmall,
                        color = EcColors.TextSoft
                    )
                }
            }
        }

        Text(
            "${assessment.packId} · orientação educativa, sem diagnóstico e sem alterar medicação.",
            style = MaterialTheme.typography.labelSmall,
            color = EcColors.TextFaint
        )
    }
}

@Composable
private fun FindingRow(finding: Finding) {
    val tone = finding.kind.tone()
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            finding.kind.glyph(),
            style = MaterialTheme.typography.bodySmall,
            color = tone,
            modifier = Modifier.width(14.dp)
        )
        Column(Modifier.weight(1f)) {
            Text(finding.text, style = MaterialTheme.typography.bodySmall)
            // Nenhum achado existe sem origem: é a invariante do rule pack, visível na tela.
            Text(
                finding.origin.label,
                style = MaterialTheme.typography.labelSmall,
                color = EcColors.TextFaint
            )
        }
    }
}

private fun FindingKind.tone(): Color = when (this) {
    FindingKind.ATTENTION -> EcColors.Amber
    FindingKind.POSITIVE -> EcColors.Mint
    FindingKind.INFO -> EcColors.BlueSoft
}

private fun FindingKind.glyph(): String = when (this) {
    FindingKind.ATTENTION -> "!"
    FindingKind.POSITIVE -> "✓"
    FindingKind.INFO -> "•"
}
