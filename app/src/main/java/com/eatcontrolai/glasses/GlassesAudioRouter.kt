package com.eatcontrolai.glasses

import android.content.Context
import android.media.AudioDeviceInfo
import android.media.AudioManager

/**
 * Roteia entrada e saída de áudio para os óculos.
 *
 * **O DAT não tem API de áudio.** Microfone e alto-falantes dos Ray-Ban Meta são alcançados pelos
 * perfis Bluetooth padrão do Android:
 *
 * - **A2DP** — só saída, alta qualidade, para mídia.
 * - **HFP/SCO** — bidirecional, voz. É o perfil que abre o microfone dos óculos.
 *
 * O array de 5 microfones aplica beamforming e isola a voz de quem está usando, atenuando o
 * ambiente — o que importa no cenário real do produto, que é supermercado e restaurante. Em troca,
 * o HFP entrega 8 kHz mono.
 *
 * Ordem importa: **configure o HFP antes de abrir a sessão de streaming.** Fazer o contrário
 * derruba o áudio no meio.
 *
 * Fonte: material do curso, unidade 13.6.
 */
class GlassesAudioRouter(context: Context) {

    private val audioManager =
        context.applicationContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    /** `true` quando existe um dispositivo Bluetooth de voz disponível para roteamento. */
    fun isGlassesAudioAvailable(): Boolean = findScoDevice() != null

    /**
     * Aponta captura e reprodução de voz para os óculos.
     *
     * Devolve `false` quando não há dispositivo SCO — nesse caso o app continua funcionando com o
     * microfone e o alto-falante do telefone, que é a degradação esperada pelo NFR-004.
     */
    fun routeToGlasses(): Boolean {
        val device = findScoDevice() ?: return false
        return audioManager.setCommunicationDevice(device)
    }

    /**
     * Devolve o áudio ao roteamento padrão do sistema.
     *
     * Precisa ser chamado ao terminar: deixar o dispositivo de comunicação fixado prende o áudio do
     * telefone inteiro no perfil de voz.
     */
    fun release() = audioManager.clearCommunicationDevice()

    /** Nome legível do destino atual, para a tela de diagnóstico. */
    fun currentRouteLabel(): String =
        audioManager.communicationDevice?.productName?.toString() ?: "padrão do sistema"

    private fun findScoDevice(): AudioDeviceInfo? =
        audioManager.availableCommunicationDevices
            .firstOrNull { it.type == AudioDeviceInfo.TYPE_BLUETOOTH_SCO }
}
