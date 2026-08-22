package com.eatcontrolai.ui.analyze

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.PreviewView
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
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.eatcontrolai.EatControlApp
import com.eatcontrolai.glasses.CaptureSource
import com.eatcontrolai.ui.AnalyzeMode
import com.eatcontrolai.ui.EatControlViewModel
import com.meta.wearable.dat.core.Wearables
import com.meta.wearable.dat.core.types.Permission
import com.meta.wearable.dat.core.types.PermissionStatus
import com.meta.wearable.dat.core.types.RegistrationState
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
    val voice by viewModel.voice.collectAsState()
    val datRegistrationState by viewModel.datRegistrationState.collectAsState()
    val datUi by viewModel.datUi.collectAsState()
    val dailyProgress by viewModel.dailyProgress.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val context = LocalContext.current
    val mockGateway = remember { (context.applicationContext as EatControlApp).container.mockGlasses }
    var cameraGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED
        )
    }
    var bluetoothGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) ==
                PackageManager.PERMISSION_GRANTED
        )
    }
    val cameraPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        cameraGranted = granted
        if (!granted) viewModel.showToast("Sem permissão de câmera. Voltando para os óculos simulados.")
        if (!granted) viewModel.selectSource(CaptureSource.MOCK_GLASSES)
    }
    val audioPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> viewModel.onAudioPermissionResult(granted) }
    val bluetoothPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        bluetoothGranted = granted
        if (granted) {
            viewModel.selectSource(CaptureSource.DAT_GLASSES)
        } else {
            viewModel.showToast("Bluetooth não autorizado. Os óculos DAT não podem ser usados.")
        }
    }
    val datCameraPermission = rememberLauncherForActivityResult(
        Wearables.RequestPermissionContract()
    ) { status ->
        viewModel.onDatCameraPermissionResult(status == PermissionStatus.Granted)
    }

    LaunchedEffect(state.source, datRegistrationState) {
        if (state.source == CaptureSource.DAT_GLASSES &&
            datRegistrationState == RegistrationState.REGISTERED
        ) {
            datCameraPermission.launch(Permission.CAMERA)
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxWidth().statusBarsPadding(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            SectionHeader(
                title = "Analisar",
                subtitle = "A captura acontece no aparelho, o OCR e a leitura de barras rodam local, " +
                    "e o motor determinístico cruza com o seu plano antes de responder por áudio."
            )
        }

        item {
            EcCard(title = "Fonte de captura", subtitle = glasses.sourceLabel) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    CaptureSource.entries.forEach { source ->
                        EcChip(
                            label = source.label,
                            tone = EcColors.BlueSoft,
                            selected = source == state.source,
                            onClick = {
                                when {
                                    source == CaptureSource.PHONE_CAMERA && !cameraGranted ->
                                        cameraPermission.launch(Manifest.permission.CAMERA)

                                    source == CaptureSource.DAT_GLASSES && !bluetoothGranted ->
                                        bluetoothPermission.launch(Manifest.permission.BLUETOOTH_CONNECT)

                                    else -> viewModel.selectSource(source)
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                if (state.source == CaptureSource.PHONE_CAMERA) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "A câmera liga só nesta tela e captura sob demanda — sem stream contínuo (NFR-009).",
                        style = MaterialTheme.typography.bodySmall,
                        color = EcColors.TextFaint
                    )
                }
                if (state.source == CaptureSource.DAT_GLASSES) {
                    Spacer(Modifier.height(10.dp))
                    val datStatus = when {
                        datUi.connecting -> "Conectando sessão temporária…"
                        glasses.connected -> "Sessão temporária ativa"
                        datRegistrationState == RegistrationState.REGISTERED ->
                            "Autorizado · conecta ao analisar"
                        datUi.launchingRegistration -> "Abrindo Meta AI…"
                        else -> "Não autorizado no Meta AI"
                    }
                    Text(
                        datStatus,
                        style = MaterialTheme.typography.labelMedium,
                        color = if (datRegistrationState == RegistrationState.REGISTERED) {
                            EcColors.Mint
                        } else {
                            EcColors.TextMuted
                        }
                    )
                    Spacer(Modifier.height(8.dp))
                    if (datRegistrationState != RegistrationState.REGISTERED) {
                        OutlinedButton(
                            onClick = {
                                val activity = context.findActivity()
                                if (activity == null) {
                                    viewModel.showToast("Não consegui abrir o fluxo de pareamento.")
                                } else {
                                    viewModel.registerDatGlasses(activity)
                                }
                            },
                            enabled = !datUi.launchingRegistration &&
                                datRegistrationState != RegistrationState.REGISTERING &&
                                datRegistrationState != RegistrationState.UNREGISTERING,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                when {
                                    datUi.launchingRegistration -> "Abrindo Meta AI…"
                                    datRegistrationState == RegistrationState.REGISTERING ->
                                        "Aguardando autorização…"
                                    else -> "Parear e autorizar no Meta AI"
                                }
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                    }
                    Text(
                        if (datRegistrationState == RegistrationState.REGISTERED) {
                            "A autorização está pronta. A câmera e o stream técnico abrem somente durante cada captura e fecham em seguida."
                        } else {
                            "O botão deve abrir o Meta AI. Conclua o vínculo lá e volte ao Eat Control."
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = EcColors.TextFaint
                    )
                    listOfNotNull(datUi.registrationError, datUi.connectionError)
                        .distinct()
                        .forEach { error ->
                            Spacer(Modifier.height(6.dp))
                            Text(
                                error,
                                style = MaterialTheme.typography.bodySmall,
                                color = EcColors.RedSoft
                            )
                        }
                }
            }
        }

        item {
            val visibleModes = AnalyzeMode.entries.filter(AnalyzeMode::ready)
            EcCard(
                title = "O que você quer analisar?",
                subtitle = "Automático continua como entrada principal. Cardápio estrutura o texto; prato usa identificação assistida e exige confirmação."
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    visibleModes.chunked(2).forEach { row ->
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

        if (state.showsSceneSelector) {
            item {
                EcCard(title = "O que os óculos estão vendo") {
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
                        state.selectedScene?.let { scene ->
                            Spacer(Modifier.height(2.dp))
                            Text(
                                scene.subtitle,
                                style = MaterialTheme.typography.bodySmall,
                                color = EcColors.TextFaint
                            )
                        }
                    }
                }
            }
        }

        item {
            EcCard(title = "Ponto de vista") {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(EcColors.BackgroundDeep)
                        .border(1.dp, EcColors.Line, RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    when {
                        state.source == CaptureSource.PHONE_CAMERA && cameraGranted ->
                            CameraPreview(viewModel)

                        state.result?.frameJpeg != null -> {
                            val frame = state.result!!.frameJpeg
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

                        // Prévia da fonte simulada: mostra o que será analisado antes de analisar.
                        // Sem isto, o operador da demo aponta para uma caixa preta e pede confiança.
                        state.source == CaptureSource.MOCK_GLASSES && state.selectedScene != null -> {
                            val scene = state.selectedScene!!
                            val preview = remember(scene.id) {
                                runCatching { mockGateway.render(scene) }.getOrNull()
                                    ?.let { BitmapFactory.decodeByteArray(it, 0, it.size)?.asImageBitmap() }
                            }
                            if (preview == null) {
                                Text(
                                    "Prévia indisponível para esta cena",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = EcColors.TextFaint
                                )
                            } else {
                                Image(
                                    bitmap = preview,
                                    contentDescription = "Prévia da cena simulada",
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }

                        else -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("◉", style = MaterialTheme.typography.headlineMedium, color = EcColors.TextFaint)
                            Spacer(Modifier.height(6.dp))
                            Text(
                                "O frame capturado aparece aqui",
                                style = MaterialTheme.typography.bodySmall,
                                color = EcColors.TextFaint
                            )
                        }
                    }
                }
                if (state.source == CaptureSource.MOCK_GLASSES) {
                    Spacer(Modifier.height(10.dp))
                    Text(
                        "A embalagem é renderizada e lida pelo OCR e pelo leitor de barras reais. " +
                            "O mock substitui o hardware, não a inteligência.",
                        style = MaterialTheme.typography.bodySmall,
                        color = EcColors.TextFaint
                    )
                }
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
                    Text(
                        if (state.mode.ready) "Analisar ${state.mode.label.lowercase()}"
                        else "Trilha não implementada"
                    )
                }
            }
        }

        item { VoiceCard(voice, viewModel) { audioPermission.launch(Manifest.permission.RECORD_AUDIO) } }

        item {
            Text(
                "Perfil ativo: " + profile.restrictions.joinToString { it.allergen.displayName }
                    .ifBlank { "sem restrições cadastradas" },
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
                onDismiss = viewModel::dismissResult,
                portions = state.portions,
                consumed = state.consumedNutrients,
                dailyProgress = dailyProgress,
                goalsConfigured = profile.macroGoals.isConfigured,
                consumptionLogged = state.consumptionLogged,
                onPortionsChange = viewModel::setPortions,
                onRegisterConsumption = viewModel::registerConsumption,
                onSelectMenuOption = viewModel::selectMenuOption,
                onRegisterMenuOption = viewModel::registerMenuSelection,
                onTogglePlateComponent = viewModel::togglePlateComponent,
                onRegisterPlate = viewModel::registerPlateSelection
            )
        }
    }
}

/**
 * Preview da câmera do celular.
 *
 * A câmera é ligada ao entrar e desligada ao sair — é o duty-cycle do NFR-009, não um detalhe de
 * implementação.
 */
@Composable
private fun CameraPreview(viewModel: EatControlViewModel) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val gateway = remember { (context.applicationContext as EatControlApp).container.phoneCamera }
    val previewView = remember { PreviewView(context).apply { scaleType = PreviewView.ScaleType.FILL_CENTER } }

    LaunchedEffect(previewView) {
        runCatching { gateway.bind(lifecycleOwner, previewView.surfaceProvider) }
            .onFailure { viewModel.showToast("Falha ao abrir a câmera: ${it.message}") }
        viewModel.onCameraBindingChanged()
    }

    DisposableEffect(Unit) {
        onDispose {
            gateway.unbind()
            viewModel.onCameraBindingChanged()
        }
    }

    AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())
}

@Composable
private fun VoiceCard(
    voice: com.eatcontrolai.ui.VoiceState,
    viewModel: EatControlViewModel,
    onRequestPermission: () -> Unit
) {
    EcCard(
        title = "Perguntar por voz",
        subtitle = if (voice.available) {
            buildString {
                append("Reconhecimento offline no aparelho")
                if (voice.capturedOnGlasses) append(" · microfone dos óculos (HFP)")
            }
        } else {
            "Reconhecimento offline pt-BR indisponível; análise por toque continua ativa"
        }
    ) {
        OutlinedButton(
            onClick = onRequestPermission,
            enabled = voice.available && !voice.listening,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (voice.listening) {
                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                Text("   Ouvindo…")
            } else {
                Text("⌁  Falar")
            }
        }

        if (voice.transcript.isNotBlank()) {
            Spacer(Modifier.height(10.dp))
            Text("VOCÊ DISSE", style = MaterialTheme.typography.labelSmall, color = EcColors.TextMuted)
            Spacer(Modifier.height(4.dp))
            Text("\"${voice.transcript}\"", style = MaterialTheme.typography.bodyMedium)
        }

        if (voice.permissionDenied) {
            Spacer(Modifier.height(8.dp))
            Text(
                "Sem permissão de microfone. A trilha de voz fica desligada; o resto do app continua funcionando.",
                style = MaterialTheme.typography.bodySmall,
                color = EcColors.Amber
            )
        }

        Spacer(Modifier.height(8.dp))
        Text(
            "O comando é interpretado por regra determinística, não por LLM: \"código de barras\" " +
                "leva para a trilha de produto, o resto vai para rótulo. Com os óculos conectados, " +
                "a captura usa o array de microfones deles por Bluetooth HFP — o DAT não tem API de áudio.",
            style = MaterialTheme.typography.bodySmall,
            color = EcColors.TextFaint
        )
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

/** O fluxo de autorização do DAT precisa de uma Activity; o Compose só entrega um Context. */
private tailrec fun android.content.Context.findActivity(): android.app.Activity? = when (this) {
    is android.app.Activity -> this
    is android.content.ContextWrapper -> baseContext.findActivity()
    else -> null
}
