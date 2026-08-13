package com.eatcontrolai.glasses

import com.eatcontrolai.inference.InferenceMeta

/**
 * Fronteira do dispositivo (`contexto-gpt.md` §67).
 *
 * Todo detalhe do Meta Wearables Device Access Toolkit fica **abaixo** desta interface. O domínio do
 * Eat Control não conhece tipos do SDK, callbacks, registro nem Bluetooth. O DAT está em developer
 * preview e vai mudar; quando mudar, só as implementações desta interface mudam.
 *
 * Existem duas implementações previstas:
 *  - [MockGlassesGateway] — funciona hoje, sem hardware e sem SDK;
 *  - [DatGlassesGateway] — bloqueada até `docs/adr/0006-dat-version.md` sair de TODO.
 */
/**
 * Estado do dispositivo para a UI.
 *
 * [batteryPercent] é nulo quando a fonte não reporta bateria — e o mock não reporta. A tela mostra
 * "—" nesse caso em vez de um número inventado.
 */
data class GlassesStatus(
    val sourceLabel: String,
    val isMock: Boolean,
    val connected: Boolean,
    val batteryPercent: Int? = null,
    val cameraReady: Boolean = false,
    val audioReady: Boolean = false
)

interface GlassesGateway {

    /** Identificação legível da fonte de captura, para a UI e para o log de métricas. */
    val sourceId: String

    val isConnected: Boolean

    val status: GlassesStatus

    suspend fun connect()

    suspend fun disconnect()

    /** Frame em JPEG. Bytes crus de propósito: mantém o domínio neutro em relação ao SDK. */
    suspend fun capturePhoto(): ByteArray

    /** PCM cru do microfone. Ainda não usado — entra na trilha de STT. */
    suspend fun startVoiceCapture(): ByteArray

    /** Reproduz a resposta. Nos óculos reais, sai pelos alto-falantes open-ear. */
    suspend fun playSpeech(text: String): InferenceMeta
}
