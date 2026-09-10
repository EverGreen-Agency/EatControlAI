package com.eatcontrolai.ui.profile

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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.eatcontrolai.ui.Destination
import com.eatcontrolai.ui.EatControlViewModel
import com.eatcontrolai.ui.components.EcCard
import com.eatcontrolai.ui.components.EcRow
import com.eatcontrolai.ui.components.SectionHeader
import com.eatcontrolai.ui.components.StatusDot
import com.eatcontrolai.ui.theme.EcColors

@Composable
fun ProfileScreen(viewModel: EatControlViewModel, onNavigate: (Destination) -> Unit) {
    val profile by viewModel.profile.collectAsState()
    val glasses by viewModel.glasses.collectAsState()
    val privacySettings by viewModel.privacySettings.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxWidth().statusBarsPadding(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            SectionHeader(
                title = "Perfil e dispositivo",
                subtitle = "Suas configurações, privacidade e a integração com os óculos."
            )
        }

        item {
            EcCard {
                EcRow(
                    glyph = profile.displayName.take(1).ifBlank { "?" },
                    title = profile.displayName.ifBlank { "Sem nome" },
                    detail = if (profile.usesGlp1) "Uso de GLP-1 informado" else "Perfil local",
                    glyphTone = EcColors.Purple
                )
                EcRow(
                    glyph = "✓",
                    title = "Meu plano",
                    detail = "${profile.restrictions.size} restrição(ões) · " +
                        if (profile.macroGoals.isConfigured) "metas configuradas" else "sem metas",
                    onClick = { onNavigate(Destination.PLAN) },
                    trailing = { Text("›", style = MaterialTheme.typography.headlineSmall, color = EcColors.TextFaint) }
                )
                EcRow(
                    glyph = "◈",
                    title = "Profissional conectado",
                    detail = "Nenhum. A plataforma profissional é pós-MVP.",
                    glyphTone = EcColors.TextMuted
                )
            }
        }

        item {
            ProfileEditor(profile = profile, onSave = viewModel::saveProfile)
        }

        item {
            EcCard(
                title = "Privacidade & Telemetria S3",
                subtitle = "Transparência total sobre o armazenamento local e envio para pesquisa."
            ) {
                EcRow(
                    glyph = "EDGE",
                    title = "Processamento no telefone",
                    detail = "OCR, barcode e regras rodam localmente com fallback offline.",
                    glyphTone = EcColors.Mint
                )
                EcRow(
                    glyph = "IMG",
                    title = "Fotos salvas no aparelho",
                    detail = "Armazenadas na pasta privada do app para consulta no Histórico.",
                    glyphTone = EcColors.Mint
                )
                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Opt-in de pesquisa (S3)",
                            style = MaterialTheme.typography.titleSmall,
                            color = EcColors.TextPrimary
                        )
                        Text(
                            "Permite enviar cópias anonimizadas das fotos ao bucket S3 para treinamento do modelo.",
                            style = MaterialTheme.typography.bodySmall,
                            color = EcColors.TextMuted
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    Switch(
                        checked = privacySettings.shareForImprovement,
                        onCheckedChange = viewModel::toggleShareForImprovement,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = EcColors.OnMint,
                            checkedTrackColor = EcColors.Mint
                        )
                    )
                }
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = viewModel::testS3Upload,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Testar S3")
                    }
                    OutlinedButton(
                        onClick = viewModel::clearHistory,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Limpar histórico")
                    }
                }
            }
        }

        item {
            EcCard(
                title = glasses.sourceLabel,
                subtitle = if (glasses.isMock)
                    "Fonte simulada. Troque para os Ray-Ban Meta na aba Analisar."
                else null,
                trailing = {
                    StatusDot(
                        tone = if (glasses.connected) EcColors.Mint else EcColors.TextFaint,
                        label = if (glasses.connected) "CONECTADO" else "OFFLINE"
                    )
                }
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(onClick = viewModel::testCamera, modifier = Modifier.weight(1f)) {
                        Text("Testar câmera")
                    }
                    OutlinedButton(onClick = viewModel::testAudio, modifier = Modifier.weight(1f)) {
                        Text("Testar áudio")
                    }
                }
            }
        }

        item {
            EcCard(title = "Processamento") {
                EcRow(
                    glyph = "▣",
                    title = "Edge AI no aparelho",
                    detail = "OCR e barcode são locais; voz só é habilitada com recursos offline instalados.",
                    glyphTone = EcColors.Mint,
                    trailing = { StatusDot(EcColors.Mint, "ATIVO") }
                )
                EcRow(
                    glyph = "☁",
                    title = "Sincronização cloud",
                    detail = "Fora do caminho crítico por decisão (ADR-0002). Não implementada.",
                    glyphTone = EcColors.TextMuted,
                    trailing = { StatusDot(EcColors.TextFaint, "FORA DO MVP") }
                )
            }
        }
    }
}
