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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.eatcontrolai.core.model.Allergen
import com.eatcontrolai.core.model.EvidenceType
import com.eatcontrolai.core.model.GoalSource
import com.eatcontrolai.core.model.MacroGoals
import kotlin.math.roundToInt
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
    val dailyProgress by viewModel.dailyProgress.collectAsState()
    val personalRules by viewModel.personalRules.collectAsState()
    val symptomReports by viewModel.symptomReports.collectAsState()

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
            PersonalRulesCard(
                rules = personalRules,
                onSave = { target, action -> viewModel.savePersonalRule(target, action) },
                onRemove = viewModel::removePersonalRule
            )
        }

        item {
            SymptomCard(
                reports = symptomReports,
                onReport = { viewModel.reportSymptom(it) },
                onClear = viewModel::clearSymptoms
            )
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
                    if (profile.guidelines.isEmpty()) {
                        Text(
                            "Nenhuma regra clínica automática está ativa. Metas e orientações só entram quando você ou um profissional as configuram.",
                            style = MaterialTheme.typography.bodySmall,
                            color = EcColors.TextMuted
                        )
                    } else {
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
        }

        item {
            Glp1MacroCalculatorCard(
                onCalculate = { goals -> viewModel.updateMacroGoals(goals) }
            )
        }

        item {
            EcCard(
                title = "Acompanhamento de hoje",
                subtitle = if (profile.macroGoals.isConfigured) {
                    "Consumo confirmado versus metas ${profile.macroGoals.definedBy.label}."
                } else {
                    "Somente consumo confirmado; nenhuma meta foi configurada."
                }
            ) {
                if (dailyProgress.isEmpty()) {
                    Text(
                        "Ainda não há metas nem nutrientes consumidos para mostrar.",
                        style = MaterialTheme.typography.bodySmall,
                        color = EcColors.TextMuted
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        dailyProgress.forEach { progress ->
                            val formatter = java.text.DecimalFormat("0.#")
                            val goal = progress.goal
                            val unit = progress.unit.symbol
                            Column {
                                Row(
                                    Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        progress.nutrient.displayName.replaceFirstChar { it.uppercase() },
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                    Text(
                                        if (goal != null) {
                                            "${formatter.format(progress.consumed)} / ${formatter.format(goal)} $unit"
                                        } else {
                                            "${formatter.format(progress.consumed)} $unit · sem meta"
                                        },
                                        style = MaterialTheme.typography.bodySmall,
                                        color = EcColors.TextMuted
                                    )
                                }
                                if (goal != null && goal > 0.0) {
                                    Spacer(Modifier.height(6.dp))
                                    EcProgress((progress.consumed / goal).coerceIn(0.0, 1.0).toFloat())
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(Modifier.height(40.dp))
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

@Composable
private fun Glp1MacroCalculatorCard(
    onCalculate: (MacroGoals) -> Unit
) {
    var weightText by remember { mutableStateOf("75") }
    var heightText by remember { mutableStateOf("170") }
    var isMale by remember { mutableStateOf(false) }
    var goalType by remember { mutableStateOf(0) }

    EcCard(
        title = "Calculadora de Metas GLP-1",
        subtitle = "Calcule metas baseadas em ciência: proteína para evitar perda muscular e fibras para motilidade gástrica."
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = weightText,
                    onValueChange = { weightText = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("Peso (kg)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = heightText,
                    onValueChange = { heightText = it.filter { c -> c.isDigit() } },
                    label = { Text("Altura (cm)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
            }

            Text("SEXO BIOLÓGICO", style = MaterialTheme.typography.labelSmall, color = EcColors.TextMuted)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                EcChip("Feminino", tone = EcColors.Mint, selected = !isMale, onClick = { isMale = false })
                EcChip("Masculino", tone = EcColors.Mint, selected = isMale, onClick = { isMale = true })
            }

            Text("OBJETIVO PRINCIPAL", style = MaterialTheme.typography.labelSmall, color = EcColors.TextMuted)
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                EcChip(
                    label = "Emagrecer com GLP-1 (preservar músculo · 1.4g/kg)",
                    tone = EcColors.Mint,
                    selected = goalType == 0,
                    onClick = { goalType = 0 },
                    modifier = Modifier.fillMaxWidth()
                )
                EcChip(
                    label = "Manutenção e Saúde (1.2g/kg)",
                    tone = EcColors.BlueSoft,
                    selected = goalType == 1,
                    onClick = { goalType = 1 },
                    modifier = Modifier.fillMaxWidth()
                )
                EcChip(
                    label = "Adaptação a Doses Iniciais / Náusea (1.0g/kg)",
                    tone = EcColors.Amber,
                    selected = goalType == 2,
                    onClick = { goalType = 2 },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.height(4.dp))

            Button(
                onClick = {
                    val weight = weightText.toDoubleOrNull() ?: 70.0
                    val proteinFactor = when (goalType) {
                        0 -> 1.4
                        1 -> 1.2
                        else -> 1.0
                    }
                    val protein = (weight * proteinFactor).roundToInt()
                    val calories = when (goalType) {
                        0 -> (weight * 22).roundToInt()
                        1 -> (weight * 28).roundToInt()
                        else -> (weight * 24).roundToInt()
                    }
                    val fiber = if (isMale) 35 else 25
                    val fat = (weight * 0.7).roundToInt()

                    onCalculate(
                        MacroGoals(
                            energyKcal = calories.toDouble(),
                            proteinG = protein.toDouble(),
                            fatG = fat.toDouble(),
                            fiberG = fiber.toDouble(),
                            sodiumMg = 2000.0,
                            definedBy = GoalSource.USER
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = EcColors.Mint,
                    contentColor = EcColors.OnMint
                )
            ) {
                Text("Calcular e Aplicar Metas Diárias")
            }
        }
    }
}
