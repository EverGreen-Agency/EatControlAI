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
import com.eatcontrolai.ui.components.EcToggle
import com.eatcontrolai.ui.components.SectionHeader
import com.eatcontrolai.ui.components.StatusDot
import com.eatcontrolai.ui.theme.EcColors

@Composable
fun ProfileScreen(viewModel: EatControlViewModel, onNavigate: (Destination) -> Unit) {
    val profile by viewModel.profile.collectAsState()
    val privacy by viewModel.privacy.collectAsState()
    val glasses by viewModel.glasses.collectAsState()

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
                    detail = if (profile.usesGlp1) "Jornada GLP-1 ativa" else "Perfil principal",
                    glyphTone = EcColors.Purple
                )
                EcRow(
                    glyph = "✓",
                    title = "Meu plano",
                    detail = "${profile.restrictions.size} restrição(ões) · ${profile.goals.size} metas",
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
            EcCard(
                title = "Privacidade",
                subtitle = "Padrões conservadores por decisão de projeto (NFR-005)."
            ) {
                EcRow(
                    glyph = "IMG",
                    title = "Salvar fotos analisadas",
                    detail = if (privacy.savePhotos) "Ligado" else "Desligado por padrão",
                    trailing = { EcToggle(privacy.savePhotos, viewModel::toggleSavePhotos) }
                )
                EcRow(
                    glyph = "AI",
                    title = "Usar dados para melhoria",
                    detail = "Opt-in voluntário, desligado por padrão",
                    trailing = {
                        EcToggle(privacy.shareForImprovement, viewModel::toggleShareForImprovement)
                    }
                )
                EcRow(
                    glyph = "↕",
                    title = "Sincronizar histórico",
                    detail = "Backend ainda não existe; a chave já reflete a escolha",
                    trailing = { EcToggle(privacy.syncHistory, viewModel::toggleSyncHistory) }
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "O app não grava imagem em disco em nenhuma configuração. O que fica guardado " +
                        "no aparelho é texto: perfil, preferências e o histórico de decisões. A " +
                        "telemetria tem apenas latências, identificador de provider e versão de " +
                        "modelo (NFR-008).",
                    style = MaterialTheme.typography.bodySmall,
                    color = EcColors.TextFaint
                )
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(onClick = viewModel::clearHistory, modifier = Modifier.weight(1f)) {
                        Text("Apagar histórico")
                    }
                    OutlinedButton(onClick = viewModel::resetProfile, modifier = Modifier.weight(1f)) {
                        Text("Restaurar perfil")
                    }
                }
            }
        }

        item {
            EcCard(
                title = glasses.sourceLabel,
                subtitle = if (glasses.isMock)
                    "Fonte simulada. O DAT 0.9.0 entra quando saírem o token do GitHub com " +
                        "read:packages e o APPLICATION_ID/CLIENT_TOKEN do Wearables Developer Center."
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
                    detail = "OCR e TTS rodam local. Nada do caminho crítico depende de internet.",
                    glyphTone = EcColors.Mint,
                    trailing = { StatusDot(EcColors.Mint, "ATIVO") }
                )
                EcRow(
                    glyph = "☁",
                    title = "Sincronização cloud",
                    detail = "Fora do caminho crítico por decisão (ADR-0002). Ainda não implementada.",
                    glyphTone = EcColors.TextMuted,
                    trailing = { StatusDot(EcColors.TextFaint, "PENDENTE") }
                )
            }
        }
    }
}
