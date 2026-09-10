package com.eatcontrolai.ui.lab

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.eatcontrolai.BuildConfig
import com.eatcontrolai.benchmark.ProviderBenchmark
import com.eatcontrolai.ui.EatControlViewModel
import com.eatcontrolai.ui.components.EcCard
import com.eatcontrolai.ui.components.SectionHeader
import com.eatcontrolai.ui.theme.EcColors

/**
 * Laboratório de modelos — só existe em build de debug.
 *
 * Roda o mesmo [ProviderBenchmark] que o `./scripts/benchmark.sh` executa por linha de comando, no
 * mesmo dataset. A régua de escolha está em `docs/MODEL_BENCHMARK.md`: nenhum modelo entra no app
 * por preferência pessoal.
 */
@Composable
fun LabScreen(viewModel: EatControlViewModel, onClose: () -> Unit) {
    val lab by viewModel.lab.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxWidth().statusBarsPadding(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            SectionHeader(
                title = "Laboratório",
                subtitle = "Compara providers com execuções reais no aparelho. Mesmo código do " +
                    "benchmark de terminal."
            )
        }

        item {
            EcCard(title = "Provider ativo") {
                Text(
                    "O conjunto ativo vem do ModelRegistry, montado em AppContainer. " +
                        "Trocar de modelo é trocar a implementação registrada lá — nenhuma tela " +
                        "conhece o ML Kit.",
                    style = MaterialTheme.typography.bodySmall,
                    color = EcColors.TextMuted
                )
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = { viewModel.runBenchmark() },
                        enabled = !lab.running,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EcColors.Mint,
                            contentColor = EcColors.OnMint
                        )
                    ) {
                        if (lab.running) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = EcColors.OnMint
                            )
                            Text("   Rodando…")
                        } else {
                            Text("Rodar benchmark")
                        }
                    }
                    OutlinedButton(onClick = onClose) { Text("Fechar") }
                }
            }
        }

        item {
            EcCard(title = "Pelo terminal") {
                Text(
                    "Com o celular conectado por USB:",
                    style = MaterialTheme.typography.bodySmall,
                    color = EcColors.TextMuted
                )
                Spacer(Modifier.height(8.dp))
                Column(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(EcColors.BackgroundDeep)
                        .padding(12.dp)
                ) {
                    Text(
                        "./scripts/benchmark.sh",
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = FontFamily.Monospace,
                        color = EcColors.MintSoft
                    )
                }
            }
        }

        item {
            CloudConfigCard(
                onSaveGemini = { viewModel.setGeminiApiKey(it) },
                onSaveOpenRouter = { viewModel.setOpenRouterApiKey(it) },
                onSaveS3 = { bucket, region, endpoint -> viewModel.setS3Config(bucket, region, endpoint) }
            )
        }

        item {
            EcCard(
                title = "Testar primeira execução",
                subtitle = "Conveniência de desenvolvimento — não aparece para o usuário final."
            ) {
                OutlinedButton(onClick = viewModel::resetProfile, modifier = Modifier.fillMaxWidth()) {
                    Text("Remover perfil local e abrir onboarding")
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    "Apaga somente o perfil local. Nenhuma persona ou meta de demonstração é injetada.",
                    style = MaterialTheme.typography.bodySmall,
                    color = EcColors.TextFaint
                )
            }
        }

        lab.error?.let { message ->
            item {
                EcCard {
                    Text(message, style = MaterialTheme.typography.bodyMedium, color = EcColors.RedSoft)
                }
            }
        }

        items(lab.results.size) { index ->
            ResultCard(lab.results[index])
        }
    }
}

@Composable
private fun ResultCard(result: ProviderBenchmark.Result) {
    EcCard(
        title = result.providerId,
        subtitle = "${result.runtime} · ${result.modelVersion} · ${result.samples} amostras"
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Metric("p50", "${result.p50Ms} ms")
            Metric("p90", "${result.p90Ms} ms")
            Metric("p95", "${result.p95Ms} ms")
            Metric("máx", "${result.maxMs} ms")
        }
        Spacer(Modifier.height(14.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Decision Success Rate", style = MaterialTheme.typography.titleSmall)
            Text(
                "%.1f%% (%d/%d)".format(
                    result.decisionSuccessRate,
                    result.correctDecisions,
                    result.totalDecisions
                ),
                style = MaterialTheme.typography.titleSmall,
                color = if (result.decisionSuccessRate >= 100.0) EcColors.Mint else EcColors.Amber
            )
        }

        val divergences = result.outcomes.filterNot { it.correct }.distinctBy { it.sceneId }
        if (divergences.isNotEmpty()) {
            Spacer(Modifier.height(12.dp))
            Text("DIVERGÊNCIAS", style = MaterialTheme.typography.labelSmall, color = EcColors.Amber)
            Spacer(Modifier.height(6.dp))
            divergences.forEach {
                Text(
                    "${it.sceneId}: esperado ${it.expected}, obtido ${it.actual}",
                    style = MaterialTheme.typography.bodySmall,
                    color = EcColors.TextMuted
                )
            }
        }

        Spacer(Modifier.height(10.dp))
        Text(
            "Qualidade é medida por decisão acertada, não por texto idêntico: é a decisão que " +
                "chega ao usuário.",
            style = MaterialTheme.typography.bodySmall,
            color = EcColors.TextFaint
        )
    }
}

@Composable
private fun Metric(label: String, value: String) {
    Column {
        Text(label, style = MaterialTheme.typography.labelSmall, color = EcColors.TextMuted)
        Spacer(Modifier.height(3.dp))
        Text(value, style = MaterialTheme.typography.titleSmall, fontFamily = FontFamily.Monospace)
    }
}

@Composable
private fun CloudConfigCard(
    onSaveGemini: (String) -> Unit,
    onSaveOpenRouter: (String) -> Unit,
    onSaveS3: (String, String, String) -> Unit
) {
    var geminiKey by remember { mutableStateOf(BuildConfig.GEMINI_API_KEY) }
    var openRouterKey by remember { mutableStateOf(BuildConfig.OPENROUTER_API_KEY) }
    var s3Bucket by remember { mutableStateOf(BuildConfig.S3_BUCKET) }
    var s3Region by remember { mutableStateOf(BuildConfig.S3_REGION) }
    var s3Endpoint by remember { mutableStateOf(BuildConfig.S3_ENDPOINT) }

    EcCard(
        title = "Visão em Nuvem & Telemetria S3",
        subtitle = "Conecte chaves de API para identificação de pratos complexos e bucket de pesquisa."
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                "Google Gemini (AI Studio - Tier Free)",
                style = MaterialTheme.typography.labelLarge,
                color = EcColors.Mint
            )
            Text(
                "Obtenha gratuitamente em aistudio.google.com. 15 RPM grátis sem cartão.",
                style = MaterialTheme.typography.bodySmall,
                color = EcColors.TextMuted
            )
            OutlinedTextField(
                value = geminiKey,
                onValueChange = { geminiKey = it },
                label = { Text("Chave API Gemini") },
                placeholder = { Text("AIzaSy...") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(Modifier.height(4.dp))
            Text(
                "OpenRouter (Modelos Free como Gemini 2.0 / Qwen 2.5)",
                style = MaterialTheme.typography.labelLarge,
                color = EcColors.BlueSoft
            )
            OutlinedTextField(
                value = openRouterKey,
                onValueChange = { openRouterKey = it },
                label = { Text("Chave API OpenRouter") },
                placeholder = { Text("sk-or-v1-...") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(Modifier.height(4.dp))
            Text(
                "AWS S3 / Cloudflare R2 (Fotos Opt-in)",
                style = MaterialTheme.typography.labelLarge,
                color = EcColors.Amber
            )
            OutlinedTextField(
                value = s3Bucket,
                onValueChange = { s3Bucket = it },
                label = { Text("Nome do Bucket S3") },
                placeholder = { Text("eatcontrol-telemetry") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = s3Region,
                    onValueChange = { s3Region = it },
                    label = { Text("Região") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                OutlinedTextField(
                    value = s3Endpoint,
                    onValueChange = { s3Endpoint = it },
                    label = { Text("Endpoint (opcional)") },
                    placeholder = { Text("R2 / MinIO") },
                    modifier = Modifier.weight(1.5f),
                    singleLine = true
                )
            }

            Spacer(Modifier.height(6.dp))
            Button(
                onClick = {
                    onSaveGemini(geminiKey)
                    onSaveOpenRouter(openRouterKey)
                    onSaveS3(s3Bucket, s3Region, s3Endpoint)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = EcColors.Mint,
                    contentColor = EcColors.OnMint
                )
            ) {
                Text("Salvar chaves no aparelho")
            }

            Text(
                "Dica: você também pode definir permanentemente no arquivo local.properties do projeto.",
                style = MaterialTheme.typography.bodySmall,
                color = EcColors.TextFaint
            )
        }
    }
}
