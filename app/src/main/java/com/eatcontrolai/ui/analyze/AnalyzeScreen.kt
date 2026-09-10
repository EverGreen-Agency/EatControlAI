package com.eatcontrolai.ui.analyze

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.eatcontrolai.EatControlApp
import com.eatcontrolai.core.model.Allergen
import com.eatcontrolai.core.model.UserProfile
import com.eatcontrolai.domain.plate.PlateFoodClass
import com.eatcontrolai.glasses.CaptureSource
import com.eatcontrolai.glasses.LiveDetection
import com.eatcontrolai.ui.AnalyzeMode
import com.eatcontrolai.ui.EatControlViewModel
import com.eatcontrolai.ui.components.EcCard
import com.eatcontrolai.ui.components.EcChip
import com.eatcontrolai.ui.theme.EcColors
import com.meta.wearable.dat.core.Wearables
import com.meta.wearable.dat.core.types.Permission
import com.meta.wearable.dat.core.types.PermissionStatus
import com.meta.wearable.dat.core.types.RegistrationState

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
    val hardwareSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showHardwareMenu by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val mockGateway = remember { (context.applicationContext as EatControlApp).container.mockGlasses }

    var cameraGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED
        )
    }
    var audioGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) ==
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
        if (!granted) viewModel.showToast("Câmera necessária para o escaneamento ao vivo.")
    }

    val audioPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        audioGranted = granted
        viewModel.onAudioPermissionResult(granted)
        if (granted) {
            viewModel.startContinuousSpeech()
        }
    }

    val bluetoothPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        bluetoothGranted = granted
        if (granted) {
            viewModel.selectSource(CaptureSource.DAT_GLASSES)
        } else {
            viewModel.showToast("Bluetooth não autorizado para óculos DAT.")
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

    // Escuta contínua de voz passiva enquanto a câmera estiver na tela
    LaunchedEffect(audioGranted) {
        if (audioGranted) {
            viewModel.startContinuousSpeech()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.stopContinuousSpeech()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // ------------------------------------------------------------- 1. Camada de Câmera Fullscreen
        when {
            state.source == CaptureSource.PHONE_CAMERA -> {
                if (cameraGranted) {
                    CameraPreview(viewModel)
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Câmera Desativada",
                            style = MaterialTheme.typography.titleLarge,
                            color = EcColors.TextPrimary
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "O EatControl precisa da câmera para identificar códigos de barras, rótulos e pratos automaticamente.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = EcColors.TextMuted,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(Modifier.height(16.dp))
                        Button(
                            onClick = { cameraPermission.launch(Manifest.permission.CAMERA) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = EcColors.Mint,
                                contentColor = EcColors.OnMint
                            )
                        ) {
                            Text("Ativar Câmera")
                        }
                    }
                }
            }

            state.source == CaptureSource.MOCK_GLASSES -> {
                state.selectedScene?.let { scene ->
                    val preview = remember(scene.id) {
                        runCatching { mockGateway.render(scene) }.getOrNull()
                            ?.let { BitmapFactory.decodeByteArray(it, 0, it.size)?.asImageBitmap() }
                    }
                    if (preview != null) {
                        Image(
                            bitmap = preview,
                            contentDescription = "Cena simulada",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            state.source == CaptureSource.DAT_GLASSES -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Óculos Inteligentes (DAT)",
                        style = MaterialTheme.typography.titleLarge,
                        color = EcColors.TextPrimary
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        if (glasses.connected) "Conectado. A captura ocorre sob demanda."
                        else "Conecte os óculos no menu de configurações.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = EcColors.TextMuted
                    )
                }
            }
        }

        // ------------------------------------------------------------- 2. HUD de Mira Central
        HudViewfinder(
            mode = state.mode,
            detection = state.liveDetection,
            profile = profile,
            modifier = Modifier.align(Alignment.Center)
        )

        // ------------------------------------------------------------- 3. Barra Superior Flutuante
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Pill com status de modo e perfil
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.Black.copy(alpha = 0.65f))
                    .border(1.dp, EcColors.Line, RoundedCornerShape(20.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(state.mode.glyph, color = EcColors.Mint, style = MaterialTheme.typography.labelMedium)
                val filterSummary = when {
                    profile.restrictions.isEmpty() -> "Sem restrições"
                    profile.restrictions.size == 1 -> "Filtro: ${profile.restrictions.first().allergen.displayName}"
                    else -> "${profile.restrictions.size} filtros ativos"
                }
                Text(
                    "${state.mode.label}  •  $filterSummary",
                    style = MaterialTheme.typography.labelSmall,
                    color = EcColors.TextPrimary
                )
            }

            // Botão sutil de hardware / configurações
            IconButton(
                onClick = { showHardwareMenu = true },
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.65f))
                    .border(1.dp, EcColors.Line, CircleShape)
            ) {
                Text("⚙", color = EcColors.TextPrimary, style = MaterialTheme.typography.titleSmall)
            }
        }

        // ------------------------------------------------------------- 4. Área Inferior Flutuante
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Indicador de fala em tempo real (quando alguém fala)
            AnimatedVisibility(
                visible = state.isListeningSpeech || state.liveSpokenText.isNotBlank(),
                enter = fadeIn() + slideInVertically { it / 2 },
                exit = fadeOut() + slideOutVertically { it / 2 }
            ) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.Black.copy(alpha = 0.85f))
                        .border(1.dp, EcColors.Mint.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("🎙️", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        text = state.liveSpokenText.ifBlank { "Ouvindo voz…" },
                        style = MaterialTheme.typography.bodySmall,
                        color = EcColors.Mint
                    )
                }
            }

            // Pílula Live de Decisão no Buffet / Macros Estimados em Tempo Real (Detalhe da Imagem 2)
            AnimatedVisibility(
                visible = state.liveDetection is LiveDetection.Plate || state.liveDetection is LiveDetection.Label,
                enter = fadeIn() + slideInVertically { it / 2 },
                exit = fadeOut() + slideOutVertically { it / 2 }
            ) {
                when (val det = state.liveDetection) {
                    is LiveDetection.Plate -> {
                        val hasFried = det.components.contains(PlateFoodClass.FRIED_FOOD)
                        val hasProtein = det.components.any { it in listOf(PlateFoodClass.CHICKEN, PlateFoodClass.MEAT, PlateFoodClass.FISH, PlateFoodClass.EGG) }
                        val isAllergenConflict = profile.restrictions.any { r ->
                            (r.allergen == Allergen.MILK && det.components.contains(PlateFoodClass.CHEESE)) ||
                            (r.allergen == Allergen.EGG && det.components.contains(PlateFoodClass.EGG)) ||
                            (r.allergen == Allergen.FISH && det.components.contains(PlateFoodClass.FISH))
                        }

                        val isAlert = hasFried || isAllergenConflict
                        val badgeColor = if (isAlert) EcColors.Red else EcColors.Mint
                        val badgeText = when {
                            isAllergenConflict -> "⚠️ Alerta Alérgeno: Conflito com seu perfil"
                            hasFried -> "⚠️ Fritura detectada · Risco de náusea GLP-1"
                            hasProtein -> "🥩 Alta Proteína (~25-30g) · Alinhado ao GLP-1"
                            det.components.contains(PlateFoodClass.SALAD) -> "🥗 Fibras e Saciedade · Liberado"
                            else -> "🍽 Alimento identificado · Toque para ver detalhes"
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color.Black.copy(alpha = 0.82f))
                                .border(1.5.dp, badgeColor.copy(alpha = 0.85f), RoundedCornerShape(20.dp))
                                .clickable { viewModel.analyze() }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(badgeColor)
                                )
                                Text(
                                    text = badgeText,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                    color = Color.White
                                )
                            }
                        }
                    }
                    is LiveDetection.Label -> {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color.Black.copy(alpha = 0.82f))
                                .border(1.5.dp, EcColors.Mint.copy(alpha = 0.85f), RoundedCornerShape(20.dp))
                                .clickable { viewModel.analyze() }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text("✦", color = EcColors.Mint, style = MaterialTheme.typography.labelSmall)
                                Text(
                                    text = "Rótulo detectado · Toque para checar",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                    color = Color.White
                                )
                            }
                        }
                    }
                    else -> Unit
                }
            }

            // Carrossel de Modos Estilo Câmera Nativa (Conceito 1: Apple Camera / Google Lens)
            val modesScrollState = rememberScrollState()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(modesScrollState)
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(Modifier.size(16.dp))
                AnalyzeMode.entries.forEach { mode ->
                    val isSelected = state.mode == mode
                    val modeLabel = when (mode) {
                        AnalyzeMode.AUTO -> "AUTOMÁTICO"
                        AnalyzeMode.PLATE -> "PRATO"
                        AnalyzeMode.LABEL -> "RÓTULO"
                        AnalyzeMode.MENU -> "CARDÁPIO"
                        AnalyzeMode.BARCODE -> "CÓDIGO"
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable { viewModel.selectMode(mode) }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = modeLabel,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                letterSpacing = 1.2.sp
                            ),
                            color = if (isSelected) EcColors.Mint else Color.White.copy(alpha = 0.55f)
                        )
                        Spacer(Modifier.height(3.dp))
                        Box(
                            modifier = Modifier
                                .size(4.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) EcColors.Mint else Color.Transparent)
                        )
                    }
                }
                Spacer(Modifier.size(16.dp))
            }

            // Controles de Ação na Base
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Microfone / Voz
                IconButton(
                    onClick = {
                        if (!audioGranted) {
                            audioPermission.launch(Manifest.permission.RECORD_AUDIO)
                        } else {
                            if (state.isContinuousListening) {
                                viewModel.stopContinuousSpeech()
                                viewModel.showToast("Escuta de voz pausada")
                            } else {
                                viewModel.startContinuousSpeech()
                                viewModel.showToast("Escuta contínua de voz ativa")
                            }
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            if (state.isContinuousListening) EcColors.Mint.copy(alpha = 0.2f)
                            else Color.Black.copy(alpha = 0.6f)
                        )
                        .border(
                            1.dp,
                            if (state.isContinuousListening) EcColors.Mint else EcColors.Line,
                            CircleShape
                        )
                ) {
                    Text(
                        text = if (state.isContinuousListening) "🎙️" else "🎤",
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                // Shutter Manual Central (para forçar disparo manual a qualquer momento)
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .border(3.dp, Color.White, CircleShape)
                        .padding(5.dp)
                        .clip(CircleShape)
                        .background(if (state.isAnalyzing) EcColors.Mint else Color.White.copy(alpha = 0.9f))
                        .clickable(enabled = !state.isAnalyzing) { viewModel.analyze() },
                    contentAlignment = Alignment.Center
                ) {
                    if (state.isAnalyzing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.Black,
                            strokeWidth = 2.dp
                        )
                    }
                }

                // Indicador e alternador de modo (cicla entre Auto, Prato, Rótulo, etc.)
                IconButton(
                    onClick = {
                        val modes = com.eatcontrolai.ui.AnalyzeMode.entries
                        val nextMode = modes[(state.mode.ordinal + 1) % modes.size]
                        viewModel.selectMode(nextMode)
                        viewModel.showToast("Modo: ${nextMode.label}")
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.6f))
                        .border(1.dp, EcColors.Line, CircleShape)
                ) {
                    Text(
                        text = state.mode.glyph,
                        color = EcColors.Mint,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }

    // ------------------------------------------------------------- 5. Modal de Resultado (ResultSheet)
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
                historyAlerts = state.historyAlerts,
                onPortionsChange = viewModel::setPortions,
                onRegisterConsumption = viewModel::registerConsumption,
                onSelectMenuOption = viewModel::selectMenuOption,
                onRegisterMenuOption = viewModel::registerMenuSelection,
                onTogglePlateComponent = viewModel::togglePlateComponent,
                onRegisterPlate = viewModel::registerPlateSelection
            )
        }
    }

    // ------------------------------------------------------------- 6. Modal de Hardware & Configurações
    if (showHardwareMenu) {
        ModalBottomSheet(
            onDismissRequest = { showHardwareMenu = false },
            sheetState = hardwareSheetState,
            containerColor = EcColors.Surface
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Text(
                        "Configurações de Captura & Hardware",
                        style = MaterialTheme.typography.titleMedium,
                        color = EcColors.TextPrimary
                    )
                }

                item {
                    EcCard(title = "Fonte de captura", subtitle = glasses.sourceLabel) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                            CaptureSource.entries.filter { it != CaptureSource.MOCK_GLASSES }.forEach { source ->
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
                    }
                }

                if (state.source == CaptureSource.DAT_GLASSES) {
                    item {
                        EcCard(title = "Óculos Meta DAT", subtitle = "Conexão Bluetooth") {
                            if (datRegistrationState != RegistrationState.REGISTERED) {
                                OutlinedButton(
                                    onClick = {
                                        val activity = context.findActivity()
                                        if (activity != null) {
                                            viewModel.registerDatGlasses(activity)
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Parear com Meta AI")
                                }
                            } else {
                                Text("Óculos Autorizados", color = EcColors.Mint, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }

                item {
                    Button(
                        onClick = { showHardwareMenu = false },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = EcColors.SurfaceHigh)
                    ) {
                        Text("Fechar")
                    }
                }
            }
        }
    }
}

/** HUD com cantoneiras animadas de mira central e contorno adaptativo para Buffet / Rótulo. */
@Composable
private fun HudViewfinder(
    mode: AnalyzeMode,
    detection: LiveDetection,
    profile: UserProfile,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    val isPlateContext = mode == AnalyzeMode.PLATE || detection is LiveDetection.Plate
    val boxSize = if (isPlateContext) 310.dp else 280.dp

    // Avaliação clínica em tempo real no Buffet
    val isAllergenAlert = if (detection is LiveDetection.Plate) {
        profile.restrictions.any { r ->
            (r.allergen == Allergen.MILK && detection.components.contains(PlateFoodClass.CHEESE)) ||
            (r.allergen == Allergen.EGG && detection.components.contains(PlateFoodClass.EGG)) ||
            (r.allergen == Allergen.FISH && detection.components.contains(PlateFoodClass.FISH))
        }
    } else false

    val isFriedAlert = if (detection is LiveDetection.Plate) {
        detection.components.contains(PlateFoodClass.FRIED_FOOD)
    } else false

    val isPlateAlert = isAllergenAlert || isFriedAlert

    val color = when {
        detection is LiveDetection.Barcode -> EcColors.Mint
        detection is LiveDetection.Label -> EcColors.MintSoft
        detection is LiveDetection.Plate && isPlateAlert -> EcColors.Red
        detection is LiveDetection.Plate -> EcColors.Mint
        mode == AnalyzeMode.PLATE -> EcColors.Mint.copy(alpha = 0.85f)
        mode == AnalyzeMode.BARCODE -> EcColors.Mint.copy(alpha = 0.85f)
        mode == AnalyzeMode.LABEL -> EcColors.MintSoft.copy(alpha = 0.85f)
        mode == AnalyzeMode.MENU -> Color(0xFF64B5F6)
        else -> Color.White.copy(alpha = 0.35f)
    }

    val labelText = when {
        detection is LiveDetection.Barcode -> "Código de barras detectado"
        detection is LiveDetection.Label -> "Rótulo de ingredientes"
        detection is LiveDetection.Plate && isFriedAlert -> "⚠️ Fritura detectada · Risco de náusea GLP-1"
        detection is LiveDetection.Plate && isAllergenAlert -> "⚠️ Alerta Alérgeno · Conflito com perfil"
        detection is LiveDetection.Plate -> {
            val items = detection.components.take(2).joinToString { it.displayName }
            if (items.isNotBlank()) "🍽 Prato: $items" else "🍽 Alimento identificado"
        }
        mode == AnalyzeMode.PLATE -> "🍽 Modo Prato / Buffet · Mira contínua ativada"
        mode == AnalyzeMode.BARCODE -> "▦ Modo Código · Enquadre o código de barras"
        mode == AnalyzeMode.LABEL -> "Aa Modo Rótulo · Enquadre os ingredientes"
        mode == AnalyzeMode.MENU -> "≡ Modo Cardápio · Enquadre o texto"
        else -> "Aponte para código de barras, rótulo ou prato"
    }

    Box(
        modifier = modifier.size(boxSize),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = (if (isPlateContext) 4.0.dp else 3.5.dp).toPx()
            val cornerLength = (if (isPlateContext) 36.dp else 30.dp).toPx()
            val w = size.width
            val h = size.height
            val strokeColor = color.copy(alpha = if (detection != LiveDetection.Idle || mode != AnalyzeMode.AUTO) alpha else 0.45f)

            // Top-Left
            drawLine(strokeColor, Offset(0f, 0f), Offset(cornerLength, 0f), strokeWidth)
            drawLine(strokeColor, Offset(0f, 0f), Offset(0f, cornerLength), strokeWidth)

            // Top-Right
            drawLine(strokeColor, Offset(w, 0f), Offset(w - cornerLength, 0f), strokeWidth)
            drawLine(strokeColor, Offset(w, 0f), Offset(w, cornerLength), strokeWidth)

            // Bottom-Left
            drawLine(strokeColor, Offset(0f, h), Offset(cornerLength, h), strokeWidth)
            drawLine(strokeColor, Offset(0f, h), Offset(0f, h - cornerLength), strokeWidth)

            // Bottom-Right
            drawLine(strokeColor, Offset(w, h), Offset(w - cornerLength, h), strokeWidth)
            drawLine(strokeColor, Offset(w, h), Offset(w, h - cornerLength), strokeWidth)
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 10.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.Black.copy(alpha = 0.75f))
                .border(1.dp, color.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(
                text = labelText,
                style = MaterialTheme.typography.labelSmall,
                color = color
            )
        }
    }
}

/** Preview da câmera do celular. */
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

/** O fluxo de autorização do DAT precisa de uma Activity; o Compose só entrega um Context. */
private tailrec fun android.content.Context.findActivity(): android.app.Activity? = when (this) {
    is android.app.Activity -> this
    is android.content.ContextWrapper -> baseContext.findActivity()
    else -> null
}
