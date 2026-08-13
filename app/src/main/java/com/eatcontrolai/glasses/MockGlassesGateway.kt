package com.eatcontrolai.glasses

import com.eatcontrolai.inference.InferenceMeta
import com.eatcontrolai.inference.TtsProvider
import kotlinx.coroutines.delay

/**
 * Óculos simulados — o "Mock Device Kit" da nossa camada.
 *
 * Vale distinguir de duas outras coisas:
 *  - o **Mock Device Kit da Meta** (`mwdat-mockdevice`) simula o dispositivo para a API do DAT, e
 *    depende das credenciais listadas no ADR-0006;
 *  - este mock simula o dispositivo para o **nosso app**, e não depende de nada.
 *
 * O edital deixa isso mais importante do que parece: os óculos e o smartphone só ficam disponíveis
 * no dia do hackathon presencial (18/09) e são fornecidos pela organização. Desenvolver contra este
 * mock não é plano B — é o caminho principal.
 *
 * A imagem é renderizada como uma embalagem, não injetada como texto: o frame passa por OCR real.
 */
class MockGlassesGateway(
    private val tts: TtsProvider,
    private val scenes: List<MockScene> = MockScenes.all
) : GlassesGateway {

    override val sourceId: String = "mock_glasses"

    override var isConnected: Boolean = false
        private set

    /**
     * `batteryPercent` fica nulo de propósito: o mock não tem bateria para reportar, e inventar
     * "74%" na tela seria mentir para a própria banca.
     */
    override val status: GlassesStatus
        get() = GlassesStatus(
            sourceLabel = "Óculos simulados",
            isMock = true,
            connected = isConnected,
            batteryPercent = null,
            cameraReady = isConnected,
            audioReady = isConnected
        )

    var currentScene: MockScene = scenes.first()
        private set

    fun selectScene(sceneId: String) {
        currentScene = scenes.firstOrNull { it.id == sceneId } ?: currentScene
    }

    override suspend fun connect() {
        delay(SIMULATED_CONNECT_MS)
        isConnected = true
    }

    override suspend fun disconnect() {
        isConnected = false
    }

    override suspend fun capturePhoto(): ByteArray {
        // Latência simulada da captura + transporte dos óculos para o telefone.
        delay(SIMULATED_CAPTURE_MS)
        return MockLabelRenderer.render(currentScene.labelText)
    }

    override suspend fun startVoiceCapture(): ByteArray {
        throw UnsupportedOperationException("Trilha de STT ainda não implementada")
    }

    override suspend fun playSpeech(text: String): InferenceMeta = tts.speak(text)

    private companion object {
        const val SIMULATED_CONNECT_MS = 300L
        const val SIMULATED_CAPTURE_MS = 120L
    }
}
