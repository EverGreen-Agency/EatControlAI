package com.eatcontrolai.ui.plan

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.eatcontrolai.domain.glp1.PersonalRule
import com.eatcontrolai.domain.glp1.PersonalRuleAction
import com.eatcontrolai.ui.components.EcCard
import com.eatcontrolai.ui.components.EcChip
import com.eatcontrolai.ui.theme.EcColors

/**
 * As regras que a própria pessoa cria.
 *
 * É a entrada da R4 do rule pack e a única parte do produto onde a regra não vem do rótulo nem do
 * consenso clínico: vem da experiência dela. Por isso o vocabulário aqui é dela — "evitar" ou
 * "observar" —, nunca "proibido" ou "liberado".
 */
@Composable
fun PersonalRulesCard(
    rules: List<PersonalRule>,
    onSave: (String, PersonalRuleAction) -> Unit,
    onRemove: (String) -> Unit
) {
    var target by remember { mutableStateOf("") }
    var action by remember { mutableStateOf(PersonalRuleAction.AVOID) }

    EcCard(
        title = "Minhas regras",
        subtitle = "O que você aprendeu sobre você. Entra na análise junto com o rótulo, " +
            "citando que foi você quem pediu."
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            rules.forEach { rule ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(EcColors.Surface)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(
                            "${rule.action.verb()} ${rule.target}",
                            style = MaterialTheme.typography.titleSmall
                        )
                        Text(
                            rule.origin.label,
                            style = MaterialTheme.typography.labelSmall,
                            color = EcColors.TextFaint
                        )
                    }
                    Text(
                        "remover",
                        style = MaterialTheme.typography.labelSmall,
                        color = EcColors.RedSoft,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onRemove(rule.id) }
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                    )
                }
            }

            if (rules.isEmpty()) {
                Text(
                    "Nenhuma regra ainda. Um exemplo: evitar camarão, observar molho branco.",
                    style = MaterialTheme.typography.bodySmall,
                    color = EcColors.TextMuted
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PersonalRuleAction.entries.forEach { option ->
                    EcChip(
                        label = option.verb(),
                        tone = if (option == PersonalRuleAction.AVOID) EcColors.Amber else EcColors.BlueSoft,
                        selected = option == action,
                        onClick = { action = option }
                    )
                }
            }

            OutlinedTextField(
                value = target,
                onValueChange = { target = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("O que você quer ${action.verb()}?") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        onSave(target, action)
                        target = ""
                    }
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = EcColors.TextPrimary,
                    unfocusedTextColor = EcColors.TextPrimary,
                    focusedBorderColor = EcColors.Blue,
                    unfocusedBorderColor = EcColors.Line,
                    focusedLabelColor = EcColors.BlueSoft,
                    unfocusedLabelColor = EcColors.TextMuted,
                    cursorColor = EcColors.Blue
                )
            )

            Button(
                onClick = {
                    onSave(target, action)
                    target = ""
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = EcColors.Mint,
                    contentColor = EcColors.OnMint
                )
            ) { Text("Salvar regra") }

            Text(
                "A palavra que você escrever é procurada no texto lido. O app não inventa " +
                    "sinônimo: quem decide o que a sua regra significa é você.",
                style = MaterialTheme.typography.labelSmall,
                color = EcColors.TextFaint
            )
        }
    }
}

private fun PersonalRuleAction.verb(): String = when (this) {
    PersonalRuleAction.AVOID -> "evitar"
    PersonalRuleAction.OBSERVE -> "observar"
}
