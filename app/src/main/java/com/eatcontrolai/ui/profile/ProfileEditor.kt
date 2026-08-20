package com.eatcontrolai.ui.profile

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
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.eatcontrolai.core.model.GoalSource
import com.eatcontrolai.core.model.MacroGoals
import com.eatcontrolai.core.model.UserProfile
import com.eatcontrolai.ui.components.EcCard
import com.eatcontrolai.ui.components.EcChip
import com.eatcontrolai.ui.components.SectionHeader
import com.eatcontrolai.ui.theme.EcColors

@Composable
fun OnboardingScreen(onSave: (String, Boolean, MacroGoals) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth().statusBarsPadding(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            SectionHeader(
                title = "Configure seu perfil",
                subtitle = "Nada vem preenchido como recomendação. Informe apenas metas definidas por você ou por um profissional."
            )
        }
        item {
            ProfileEditor(
                profile = UserProfile(id = ""),
                submitLabel = "Criar perfil local",
                onSave = onSave
            )
        }
        item {
            Text(
                "O perfil e o histórico ficam somente neste aparelho. O Eat Control não prescreve metas nem ajusta seu medicamento.",
                style = MaterialTheme.typography.bodySmall,
                color = EcColors.TextFaint,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }
    }
}

@Composable
fun ProfileEditor(
    profile: UserProfile,
    submitLabel: String = "Salvar perfil e metas",
    onSave: (String, Boolean, MacroGoals) -> Unit
) {
    var displayName by rememberSaveable(profile.id) { mutableStateOf(profile.displayName) }
    var usesGlp1 by rememberSaveable(profile.id) { mutableStateOf(profile.usesGlp1) }
    var energy by rememberSaveable(profile.id) { mutableStateOf(profile.macroGoals.energyKcal.asInput()) }
    var protein by rememberSaveable(profile.id) { mutableStateOf(profile.macroGoals.proteinG.asInput()) }
    var carbohydrate by rememberSaveable(profile.id) { mutableStateOf(profile.macroGoals.carbohydrateG.asInput()) }
    var fat by rememberSaveable(profile.id) { mutableStateOf(profile.macroGoals.fatG.asInput()) }
    var fiber by rememberSaveable(profile.id) { mutableStateOf(profile.macroGoals.fiberG.asInput()) }
    var sodium by rememberSaveable(profile.id) { mutableStateOf(profile.macroGoals.sodiumMg.asInput()) }
    var sourceName by rememberSaveable(profile.id) {
        mutableStateOf(
            profile.macroGoals.definedBy
                .takeUnless { it == GoalSource.NOT_CONFIGURED }
                ?.name ?: GoalSource.USER.name
        )
    }

    val goalInputs = listOf(energy, protein, carbohydrate, fat, fiber, sodium)
    val hasInvalidGoal = goalInputs.any { it.isNotBlank() && parsePositiveGoal(it) == null }
    val hasGoal = goalInputs.any { it.isNotBlank() }
    val canSave = displayName.trim().isNotEmpty() && !hasInvalidGoal

    EcCard(
        title = "Dados locais",
        subtitle = "Campos de meta vazios permanecem não configurados."
    ) {
        OutlinedTextField(
            value = displayName,
            onValueChange = { displayName = it },
            label = { Text("Como você quer ser chamado") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text("Uso de GLP-1 informado", style = MaterialTheme.typography.titleSmall)
                Text(
                    "Serve como contexto; não ativa regra clínica automática.",
                    style = MaterialTheme.typography.bodySmall,
                    color = EcColors.TextMuted
                )
            }
            Switch(checked = usesGlp1, onCheckedChange = { usesGlp1 = it })
        }

        Spacer(Modifier.height(16.dp))
        Text("METAS DIÁRIAS OPCIONAIS", style = MaterialTheme.typography.labelSmall, color = EcColors.TextMuted)
        Spacer(Modifier.height(8.dp))
        GoalField("Energia (kcal)", energy) { energy = it }
        GoalField("Proteína (g)", protein) { protein = it }
        GoalField("Carboidratos (g)", carbohydrate) { carbohydrate = it }
        GoalField("Gorduras totais (g)", fat) { fat = it }
        GoalField("Fibra (g)", fiber) { fiber = it }
        GoalField("Sódio (mg)", sodium) { sodium = it }

        if (hasGoal) {
            Spacer(Modifier.height(10.dp))
            Text("QUEM DEFINIU AS METAS", style = MaterialTheme.typography.labelSmall, color = EcColors.TextMuted)
            Spacer(Modifier.height(7.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                listOf(GoalSource.USER, GoalSource.HEALTH_PROFESSIONAL).forEach { source ->
                    EcChip(
                        label = source.label,
                        tone = EcColors.BlueSoft,
                        selected = source.name == sourceName,
                        onClick = { sourceName = source.name },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        if (hasInvalidGoal) {
            Spacer(Modifier.height(8.dp))
            Text(
                "Use apenas números maiores que zero; vírgula e ponto são aceitos.",
                style = MaterialTheme.typography.bodySmall,
                color = EcColors.Amber
            )
        }

        Spacer(Modifier.height(16.dp))
        Button(
            onClick = {
                val source = if (hasGoal) GoalSource.valueOf(sourceName) else GoalSource.NOT_CONFIGURED
                onSave(
                    displayName.trim(),
                    usesGlp1,
                    MacroGoals(
                        energyKcal = parsePositiveGoal(energy),
                        proteinG = parsePositiveGoal(protein),
                        carbohydrateG = parsePositiveGoal(carbohydrate),
                        fatG = parsePositiveGoal(fat),
                        fiberG = parsePositiveGoal(fiber),
                        sodiumMg = parsePositiveGoal(sodium),
                        definedBy = source
                    )
                )
            },
            enabled = canSave,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(submitLabel)
        }
    }
}

@Composable
private fun GoalField(label: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        isError = value.isNotBlank() && parsePositiveGoal(value) == null,
        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp)
    )
}

internal fun parsePositiveGoal(raw: String): Double? = raw
    .trim()
    .replace(',', '.')
    .takeIf { it.isNotEmpty() }
    ?.toDoubleOrNull()
    ?.takeIf { it.isFinite() && it > 0.0 }

private fun Double?.asInput(): String = this?.toString().orEmpty()
