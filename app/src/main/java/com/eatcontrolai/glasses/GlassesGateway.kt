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
interface GlassesGateway {

    /** Identificação legível da fonte de captura, para a UI e para o log de métricas. */
    val sourceId: String

    val isConnected: Boolean

    suspend fun connect()

    suspend fun disconnect()

    /** Frame em JPEG. Bytes crus de propósito: mantém o domínio neutro em relação ao SDK. */
    suspend fun capturePhoto(): ByteArray

    /** PCM cru do microfone. Ainda não usado — entra na trilha de STT. */
    suspend fun startVoiceCapture(): ByteArray

    /** Reproduz a resposta. Nos óculos reais, sai pelos alto-falantes open-ear. */
    suspend fun playSpeech(text: String): InferenceMeta
}

/**
 * Implementação real sobre o Meta DAT.
 *
 * Intencionalmente não implementada: o `README.md` explica que nenhuma versão do DAT foi fixada
 * neste scaffold até que `docs/adr/0006` registre versão, coordenadas Gradle e known issues.
 * A palestra de DAT do Ideathon (15/08, 10h30) é o momento de resolver isso.
 */
class DatGlassesGateway : GlassesGateway {
    override val sourceId: String = "dat_glasses"
    override val isConnected: Boolean = false
    override suspend fun connect() = TODO("Integrar a versão do DAT registrada no ADR-0006")
    override suspend fun disconnect() = TODO("Integrar DAT")
    override suspend fun capturePhoto(): ByteArray = TODO("Integrar câmera via DAT")
    override suspend fun startVoiceCapture(): ByteArray = TODO("Integrar microfone/HFP via DAT")
    override suspend fun playSpeech(text: String): InferenceMeta = TODO("Rotear áudio para os óculos")
}
