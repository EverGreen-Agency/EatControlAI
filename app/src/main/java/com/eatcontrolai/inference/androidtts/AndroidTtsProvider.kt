package com.eatcontrolai.inference.androidtts

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import com.eatcontrolai.inference.InferenceMeta
import com.eatcontrolai.inference.TtsProvider
import java.util.Locale
import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine

/**
 * TTS nativo do Android (`docs/adr/0005`, Fase A).
 *
 * O objetivo do MVP não é naturalidade de voz, é `time-to-first-audio` baixo e funcionamento local
 * (`contexto-gpt.md` §39). Quando o DAT entrar, o áudio sai pelos alto-falantes dos óculos pela
 * rota de áudio do sistema — esta classe não deveria precisar mudar.
 */
class AndroidTtsProvider(context: Context) : TtsProvider {

    private val utteranceCounter = AtomicInteger(0)
    private var ready = false

    // Tipo explícito: o callback referencia `engine`, e sem a anotação o Kotlin cai em
    // inferência recursiva.
    private val engine: TextToSpeech = TextToSpeech(context.applicationContext) { status ->
        ready = status == TextToSpeech.SUCCESS
        if (ready) engine.language = Locale.forLanguageTag("pt-BR")
    }

    override suspend fun speak(text: String): InferenceMeta {
        val startedAt = System.nanoTime()
        if (!ready) return meta(startedAt, failed = true)

        val utteranceId = "eat-control-${utteranceCounter.incrementAndGet()}"

        val firstAudioAt = suspendCancellableCoroutine { continuation ->
            engine.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(id: String?) {
                    if (id == utteranceId && continuation.isActive) continuation.resume(System.nanoTime())
                }

                override fun onDone(id: String?) = Unit

                @Deprecated("Sobrescrita obrigatória da classe abstrata.")
                override fun onError(id: String?) {
                    if (id == utteranceId && continuation.isActive) continuation.resume(-1L)
                }
            })
            engine.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
            continuation.invokeOnCancellation { engine.stop() }
        }

        return if (firstAudioAt < 0) meta(startedAt, failed = true)
        else InferenceMeta(
            providerId = PROVIDER_ID,
            version = "platform",
            runtime = "android",
            latencyMs = (firstAudioAt - startedAt) / 1_000_000
        )
    }

    fun shutdown() {
        engine.stop()
        engine.shutdown()
    }

    private fun meta(startedAt: Long, failed: Boolean) = InferenceMeta(
        providerId = if (failed) "${PROVIDER_ID}_unavailable" else PROVIDER_ID,
        version = "platform",
        runtime = "android",
        latencyMs = (System.nanoTime() - startedAt) / 1_000_000
    )

    companion object {
        const val PROVIDER_ID = "android_tts"
    }
}
