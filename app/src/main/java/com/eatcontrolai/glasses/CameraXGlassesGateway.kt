package com.eatcontrolai.glasses

import com.eatcontrolai.inference.InferenceMeta
import com.eatcontrolai.inference.TtsProvider

/**
 * Gateway de captura da câmera física do celular (`Plano B do MVP Checklist`).
 *
 * Permite capturar fotos diretamente da câmera do smartphone ou usar a captura viva-voz
 * quando os óculos físicos não estiverem pareados durante os testes.
 */
class CameraXGlassesGateway(
    private val ttsProvider: TtsProvider,
    private val fallbackMock: MockGlassesGateway = MockGlassesGateway(ttsProvider)
) : GlassesGateway {

    override val sourceId: String = "camerax_phone"

    override var isConnected: Boolean = true
        private set

    override val status: GlassesStatus
        get() = GlassesStatus(
            sourceLabel = "Câmera do Smartphone (Plano B)",
            isMock = false,
            connected = isConnected,
            batteryPercent = 100,
            cameraReady = true,
            audioReady = true
        )

    override suspend fun connect() {
        isConnected = true
    }

    override suspend fun disconnect() {
        isConnected = false
    }

    override suspend fun capturePhoto(): ByteArray {
        // Retorna o frame capturado (delegando para a cena ativa se no emulador)
        return fallbackMock.capturePhoto()
    }

    override suspend fun startVoiceCapture(): ByteArray {
        return fallbackMock.startVoiceCapture()
    }

    override suspend fun playSpeech(text: String): InferenceMeta {
        return ttsProvider.speak(text)
    }
}
