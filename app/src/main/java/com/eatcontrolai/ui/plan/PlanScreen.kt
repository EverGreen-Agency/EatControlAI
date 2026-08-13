package com.eatcontrolai.ui.plan

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.eatcontrolai.core.model.Allergen
import com.eatcontrolai.core.model.EvidenceType
import com.eatcontrolai.core.model.Restriction
import com.eatcontrolai.core.model.RestrictionSeverity
import com.eatcontrolai.core.model.UncertaintyPolicy
import com.eatcontrolai.ui.EatControlViewModel
import com.eatcontrolai.ui.components.EcCard
import com.eatcontrolai.ui.components.EcChip
import com.eatcontrolai.ui.components.EcProgress
import com.eatcontrolai.ui.components.SectionHeader
import com.eatcontrolai.ui.home.DemoNote
import com.eatcontrolai.ui.theme.EcColors

/**
 * A camada de personalização — o que separa o Eat Control de um scanner genérico de rótulos.
 *
 * Tudo nesta tela é dado real e muda o comportamento do motor: ligar uma restrição, mudar a
 * severidade ou trocar a política de incerteza altera a próxima decisão.
 */
@Composable
fun PlanScreen(viewModel: EatControlViewModel) {
    val profile by viewModel.profile.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxWidth().statusBarsPadding(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            SectionHeader(
                title = "Meu plano",
                subtitle = "O Eat Control usa estas informações para interpretar cada escolha dentro " +
                    "do seu contexto — sem transformar inferência visual em prescrição."
            )
        }

        item {
            EcCard(
                title = "Restrições e alertas",
                subtitle = "Toque para ligar. Toque na severidade para alternar entre alta " +
                    "prioridade, atenção e preferência."
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Allergen.entries.forEach { allergen ->
                        RestrictionRow(
                            allergen = allergen,
                            restriction = profile.restrictionFor(allergen),
                            onToggle = { viewModel.toggleRestriction(allergen) },
                            onCycleSeverity = { viewModel.cycleSeverity(allergen) },
                            onPolicy = { viewModel.setUncertaintyPolicy(allergen, it) }
                        )
                    }
                }
            }
        }

        item {
            EcCard(
                title = "Como o Eat Control decide",
                subtitle = "A ordem de precedência é a mesma no código e nesta tela."
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    EvidenceType.entries.sortedBy { it.rank }.forEach { type ->
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "${type.rank}",
                                style = MaterialTheme.typography.titleMedium,
                                color = if (type.isProbabilistic) EcColors.Amber else EcColors.BlueSoft
                            )
                            Column(Modifier.weight(1f)) {
                                Text(type.label, style = MaterialTheme.typography.titleSmall)
                                Text(
                                    explain(type),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = EcColors.TextMuted
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(2.dp))
                    Text(
                        "Inferência visual isoladamente nunca afirma segurança. Ausência de " +
                            "declaração nunca vira permissão.",
                        style = MaterialTheme.typography.bodySmall,
                        color = EcColors.Mint
                    )
                }
            }
        }

        if (profile.usesGlp1) {
            item {
                EcCard(
                    title = "Jornada GLP-1",
                    subtitle = "Contexto declarado por você para personalização educativa.",
                    trailing = { EcChip("ATIVA", tone = EcColors.Mint, selected = true) }
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        profile.guidelines.forEach { guideline ->
                            Column {
                                Text(guideline.title, style = MaterialTheme.typography.titleSmall)
                                Spacer(Modifier.height(3.dp))
                                Text(
                                    guideline.detail,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = EcColors.TextMuted
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            EcCard(title = "Prioridades do dia") {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    listOf(
                        "Proteína" to 0.75f,
                        "Hidratação" to 0.78f,
                        "Vegetais" to 0.66f,
                        "Regularidade" to 0.84f
                    ).forEach { (label, value) ->
                        Column {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(label, style = MaterialTheme.typography.titleSmall)
                                Text(
                                    "${(value * 100).toInt()}%",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = EcColors.TextMuted
                                )
                            }
                            Spacer(Modifier.height(6.dp))
                            EcProgress(value)
                        }
                    }
                    Spacer(Modifier.height(2.dp))
                    DemoNote(
                        "Precisa de registro de refeição com porção, que é a trilha seguinte. " +
                            "Os números aqui são ilustrativos."
                    )
                }
            }
        }
    }
}

@Composable
private fun RestrictionRow(
    allergen: Allergen,
    restriction: Restriction?,
    onToggle: () -> Unit,
    onCycleSeverity: () -> Unit,
    onPolicy: (UncertaintyPolicy) -> Unit
) {
    val active = restriction != null
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(if (active) EcColors.SurfaceHigh else EcColors.Surface)
            .padding(12.dp)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            EcChip(
                label = allergen.displayName.replaceFirstChar { it.uppercase() },
                tone = if (active) EcColors.RedSoft else EcColors.TextMuted,
                selected = active,
                onClick = onToggle
            )
            if (restriction != null) {
                EcChip(
                    label = restriction.severity.label,
                    tone = severityTone(restriction.severity),
                    selected = true,
                    onClick = onCycleSeverity
                )
            }
        }

        if (restriction != null) {
            Spacer(Modifier.height(10.dp))
            Text(
                "QUANDO HOUVER INCERTEZA",
                style = MaterialTheme.typography.labelSmall,
                color = EcColors.TextMuted
            )
            Spacer(Modifier.height(6.dp))
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                UncertaintyPolicy.entries.forEach { policy ->
                    EcChip(
                        label = policy.label,
                        tone = EcColors.BlueSoft,
                        selected = policy == restriction.uncertaintyPolicy,
                        onClick = { onPolicy(policy) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

private fun severityTone(severity: RestrictionSeverity) = when (severity) {
    RestrictionSeverity.CRITICAL -> EcColors.RedSoft
    RestrictionSeverity.MODERATE -> EcColors.Amber
    RestrictionSeverity.PREFERENCE -> EcColors.Mint
}

private fun explain(type: EvidenceType) = when (type) {
    EvidenceType.PROFESSIONAL_RULE -> "Orientação cadastrada por médico ou nutricionista."
    EvidenceType.DECLARED_LABEL -> "O que o fabricante declara explicitamente na embalagem."
    EvidenceType.BARCODE_DATABASE -> "Composição vinda de base estruturada confiável."
    EvidenceType.USER_CONFIRMATION -> "Sua resposta a uma pergunta de confirmação."
    EvidenceType.OCR_TEXT -> "Texto lido pela câmera, ainda não interpretado."
    EvidenceType.VISUAL_INFERENCE -> "Estimativa de modelo de visão. Ajuda, mas não certifica."
}
