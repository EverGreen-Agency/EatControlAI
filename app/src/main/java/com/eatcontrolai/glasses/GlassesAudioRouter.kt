package com.eatcontrolai.glasses

import android.content.Context
import android.media.AudioDeviceInfo
import android.media.AudioManager

/**
 * Roteia entrada e saída de voz para os óculos por HFP/SCO.
 *
 * O DAT não expõe áudio: câmera usa DAT; microfone e alto-falantes usam as APIs Bluetooth padrão do
 * Android. O HFP precisa subir antes da sessão de câmera e permanecer ativo até o fim do TTS.
 */
class GlassesAudioRouter(context: Context) {

    private val audioManager =
        context.applicationContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    private var previousMode: Int? = null
    private var routed = false

    fun isGlassesAudioAvailable(): Boolean = findScoDevice() != null

    /**
     * Aponta captura e reprodução de voz para os óculos.
     *
     * Sem SCO, devolve `false` e o Android mantém microfone/alto-falante do telefone.
     */
    @Synchronized
    fun routeToGlasses(): Boolean {
        if (routed) return true
        val device = findScoDevice() ?: return false

        previousMode = audioManager.mode
        audioManager.mode = AudioManager.MODE_IN_COMMUNICATION
        routed = audioManager.setCommunicationDevice(device)

        if (!routed) {
            audioManager.mode = previousMode ?: AudioManager.MODE_NORMAL
            previousMode = null
        }
        return routed
    }

    /** Libera o SCO e restaura o modo de áudio anterior. */
    @Synchronized
    fun release() {
        if (routed) audioManager.clearCommunicationDevice()
        previousMode?.let { audioManager.mode = it }
        previousMode = null
        routed = false
    }

    fun currentRouteLabel(): String =
        audioManager.communicationDevice?.productName?.toString() ?: "padrão do sistema"

    private fun findScoDevice(): AudioDeviceInfo? =
        audioManager.availableCommunicationDevices
            .firstOrNull { it.type == AudioDeviceInfo.TYPE_BLUETOOTH_SCO }
}
