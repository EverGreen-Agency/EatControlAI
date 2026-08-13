package com.eatcontrolai.glasses

import android.net.Uri
import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.eatcontrolai.core.model.Allergen
import com.eatcontrolai.core.model.DecisionState
import com.eatcontrolai.core.model.Restriction
import com.eatcontrolai.core.model.UserProfile
import com.eatcontrolai.domain.decision.FoodDecisionEngine
import com.eatcontrolai.domain.evidence.EvidenceBuilder
import com.eatcontrolai.inference.InferenceMeta
import com.eatcontrolai.inference.TtsProvider
import com.eatcontrolai.inference.mlkit.MlKitOcrProvider
import com.meta.wearable.dat.core.types.Permission
import com.meta.wearable.dat.core.types.PermissionStatus
import com.meta.wearable.dat.mockdevice.MockDeviceKit
import com.meta.wearable.dat.mockdevice.api.GlassesModel
import com.meta.wearable.dat.mockdevice.api.MockDeviceKitConfig
import java.io.File
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Roda a vertical do rótulo **através do DAT de verdade**, sem óculos físicos.
 *
 * Usa o Mock Device Kit oficial da Meta: o `DatGlassesGateway` fala com a API real
 * (`Wearables.createSession`, `addCamera`, `capturePhoto`), e quem responde do outro lado é um par
 * de óculos simulado pelo próprio SDK. Isso valida a integração — sessão, capability de câmera,
 * captura — antes de qualquer hardware chegar.
 *
 * O que este teste **não** valida: o formato real do frame que os óculos físicos entregam. O mock
 * devolve a imagem que a gente injeta. Esse ponto continua dependendo do teste com hardware descrito
 * em `checklists/REAL_GLASSES_TEST.md`.
 *
 * Como rodar, com o celular conectado por USB:
 *
 * ```
 * ./gradlew :app:connectedDebugAndroidTest --tests "*DatMockDeviceTest*"
 * ```
 */
@RunWith(AndroidJUnit4::class)
class DatMockDeviceTest {

    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val mockKit = MockDeviceKit.getInstance(context)
    private val engine = FoodDecisionEngine()
    private val milkProfile = UserProfile(id = "dat-test", restrictions = setOf(Restriction(Allergen.MILK)))

    /** O gateway só precisa de TTS para falar o resultado; aqui não interessa reproduzir áudio. */
    private val silentTts = object : TtsProvider {
        override suspend fun speak(text: String) =
            InferenceMeta("silent_tts", "test", "test", 0)
    }

    @Before
    fun setUp() {
        grantAndroidPermissions()
        mockKit.enable(
            MockDeviceKitConfig(
                initiallyRegistered = true,
                initialPermissionsGranted = true
            )
        )
        mockKit.permissions.set(Permission.CAMERA, PermissionStatus.Granted)
    }

    @After
    fun tearDown() {
        mockKit.pairedDevices.toList().forEach { mockKit.unpairDevice(it) }
        mockKit.disable()
    }

    @Test
    fun capturaPeloDatProduzDecisaoCorreta() = runBlocking {
        val glasses = mockKit.pairGlasses(GlassesModel.RAYBAN_META).getOrNull()
            ?: error("Mock Device Kit não conseguiu parear os óculos simulados")

        // Injeta um rótulo conhecido como a foto que os óculos vão "tirar".
        val scene = MockScenes.labels.first { it.id == "iogurte_zero_lactose" }
        glasses.services.camera.setCapturedImage(writeLabelToCache(scene))

        val gateway = DatGlassesGateway(context, silentTts)
        gateway.connect()
        assertTrue("O gateway deveria estar conectado após connect()", gateway.isConnected)

        try {
            val frame = gateway.capturePhoto()
            assertTrue("Frame vazio vindo do DAT", frame.isNotEmpty())
            Log.i(TAG, "frame recebido do DAT: ${frame.size / 1024} KB")

            val ocr = MlKitOcrProvider().recognize(frame)
            Log.i(TAG, "OCR (${ocr.meta.latencyMs} ms): ${ocr.text.take(120)}")

            val evidence = EvidenceBuilder.fromLabelOcr(ocr.text, ocr.meta.providerId, ocr.meta.version)
            val decision = engine.decide(milkProfile, evidence)
            Log.i(TAG, "decisão: ${decision.state} — ${decision.shortMessage}")

            // O rótulo diz "ZERO LACTOSE" na frente e "CONTÉM LEITE" no verso.
            assertEquals(scene.expectedForMilkProfile, decision.state)
            assertEquals(DecisionState.INCOMPATIBLE, decision.state)
        } finally {
            gateway.disconnect()
        }
    }

    /** Renderiza o rótulo e devolve um `file://` que o mock consegue ler. */
    private fun writeLabelToCache(scene: MockScene): Uri {
        val file = File(context.cacheDir, "${scene.id}.jpg")
        file.writeBytes(MockLabelRenderer.render(scene.labelText))
        return Uri.fromFile(file)
    }

    private fun grantAndroidPermissions() {
        val automation = InstrumentationRegistry.getInstrumentation().uiAutomation
        listOf(
            "android.permission.BLUETOOTH_CONNECT",
            "android.permission.CAMERA"
        ).forEach { permission ->
            automation.executeShellCommand("pm grant ${context.packageName} $permission").close()
        }
    }

    private companion object {
        const val TAG = "EC_DAT"
    }
}
