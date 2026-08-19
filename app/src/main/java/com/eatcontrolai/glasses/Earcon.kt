package com.eatcontrolai.glasses

import android.media.AudioManager
import android.media.ToneGenerator

/**
 * Confirmação sonora imediata.
 *
 * Sem display, o silêncio é a única coisa que o usuário percebe enquanto o app pensa — e ele
 * interpreta silêncio como falha. A palestra do Ideathon mede isso: acima de 3 segundos o usuário
 * repete o comando, e aí existem duas requisições concorrentes e uma pessoa irritada.
 *
 * O tom sai antes de qualquer inferência começar. Custa alguns milissegundos e compra a janela
 * inteira de processamento.
 *
 * Usa [ToneGenerator] em vez de um arquivo de áudio de propósito: sem asset, sem decodificação, sem
 * latência de carregamento — e sai pelo mesmo caminho de áudio que estiver ativo, incluindo os
 * óculos.
 */
class Earcon {

    private val tone by lazy {
        runCatching { ToneGenerator(AudioManager.STREAM_MUSIC, VOLUME) }.getOrNull()
    }

    /** Toca o "tic" de confirmação. Devolve os milissegundos gastos até disparar o som. */
    fun confirm(): Long {
        val startedAt = System.nanoTime()
        tone?.startTone(ToneGenerator.TONE_PROP_BEEP, DURATION_MS)
        return (System.nanoTime() - startedAt) / 1_000_000
    }

    fun release() = tone?.release()

    private companion object {
        const val VOLUME = 70
        const val DURATION_MS = 90
    }
}
