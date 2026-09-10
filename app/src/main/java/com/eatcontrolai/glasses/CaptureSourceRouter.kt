package com.eatcontrolai.glasses

import com.eatcontrolai.inference.InferenceMeta

enum class CaptureSource(val label: String) {
    MOCK_GLASSES("Óculos simulados"),
    PHONE_CAMERA("Câmera do celular"),
    DAT_GLASSES("Ray-Ban Meta")
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
    private val phone: PhoneCameraGateway,
    private val dat: DatGlassesGateway
) : GlassesGateway {

    var active: CaptureSource = CaptureSource.PHONE_CAMERA
        private set

    fun select(source: CaptureSource) {
        active = source
    }

    fun gatewayFor(source: CaptureSource): GlassesGateway = when (source) {
        CaptureSource.MOCK_GLASSES -> mock
        CaptureSource.PHONE_CAMERA -> phone
        CaptureSource.DAT_GLASSES -> dat
    }

    private val current: GlassesGateway get() = gatewayFor(active)

    override val sourceId: String get() = current.sourceId
    override val isConnected: Boolean get() = current.isConnected
    override val status: GlassesStatus get() = current.status

    override suspend fun connect() = current.connect()
    override suspend fun disconnect() = current.disconnect()
    override suspend fun capturePhoto(): ByteArray = current.capturePhoto()
    override suspend fun startVoiceCapture(): ByteArray = current.startVoiceCapture()
    override suspend fun playSpeech(text: String): InferenceMeta = current.playSpeech(text)
}
