package com.eatcontrolai.glasses

import com.eatcontrolai.inference.InferenceMeta

enum class CaptureSource(val label: String) {
    MOCK_GLASSES("Óculos simulados"),
    PHONE_CAMERA("Câmera do celular")
    // DAT_GLASSES entra aqui quando o ADR-0006 for destravado.
}

/**
 * Encaminha a captura para a fonte ativa.
 *
 * É um [GlassesGateway] que delega a outro [GlassesGateway]. O orquestrador continua conhecendo uma
 * única interface, e trocar de fonte no meio da demo não recria nada a jusante — o que importa no
 * dia do hackathon, quando a alternativa a "os óculos não parearam" é apertar um botão.
 */
class CaptureSourceRouter(
    private val mock: MockGlassesGateway,
    private val phone: PhoneCameraGateway
) : GlassesGateway {

    var active: CaptureSource = CaptureSource.MOCK_GLASSES
        private set

    fun select(source: CaptureSource) {
        active = source
    }

    private val current: GlassesGateway
        get() = when (active) {
            CaptureSource.MOCK_GLASSES -> mock
            CaptureSource.PHONE_CAMERA -> phone
        }

    override val sourceId: String get() = current.sourceId
    override val isConnected: Boolean get() = current.isConnected
    override val status: GlassesStatus get() = current.status

    override suspend fun connect() = current.connect()
    override suspend fun disconnect() = current.disconnect()
    override suspend fun capturePhoto(): ByteArray = current.capturePhoto()
    override suspend fun startVoiceCapture(): ByteArray = current.startVoiceCapture()
    override suspend fun playSpeech(text: String): InferenceMeta = current.playSpeech(text)
}
