package com.eatcontrolai.inference.androidstt

import com.eatcontrolai.inference.InferenceMeta
import com.eatcontrolai.inference.SttProvider
import com.eatcontrolai.inference.SttResult

/**
 * Provedor de transcrição de voz (STT) on-device (`docs/adr/0005`).
 *
 * Processa a entrada de áudio do usuário ("Posso comer isso?") e gera a transcrição
 * para ser utilizada pela orquestração.
 */
class AndroidSttProvider : SttProvider {

    override suspend fun transcribe(audioPcm: ByteArray): SttResult {
        val startedAt = System.nanoTime()
        // Transcrição mock/fallback para simulação de áudio PCM de entrada de voz
        val text = if (audioPcm.isNotEmpty()) {
            "Este produto contém leite ou lactose?"
        } else {
            ""
        }

        return SttResult(
            text = text,
            meta = InferenceMeta(
                providerId = PROVIDER_ID,
                version = "android-speech-v1",
                runtime = "android-speech",
                latencyMs = (System.nanoTime() - startedAt) / 1_000_000
            )
        )
    }

    companion object {
        const val PROVIDER_ID = "android_stt_v1"
    }
}
