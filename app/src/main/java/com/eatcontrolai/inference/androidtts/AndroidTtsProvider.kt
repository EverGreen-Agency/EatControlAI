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
 * TTS nativo restrito a uma voz pt-BR que não exige rede.
 *
 * Algumas engines Android oferecem vozes de maior qualidade dependentes de conexão. O provider não
 * usa essas vozes: se o aparelho não tiver uma voz portuguesa local, devolve metadata indisponível
 * e preserva o resultado visual em vez de transmitir texto para um serviço externo.
 */
class AndroidTtsProvider(context: Context) : TtsProvider {

    private val utteranceCounter = AtomicInteger(0)
    private val locale = Locale.forLanguageTag("pt-BR")
    private var ready = false

    private lateinit var engine: TextToSpeech

    init {
        engine = TextToSpeech(context.applicationContext) { status ->
            if (status != TextToSpeech.SUCCESS) return@TextToSpeech

            val languageStatus = engine.setLanguage(locale)
            val localVoice = engine.voices
                ?.firstOrNull {
                    it.locale.toLanguageTag().equals("pt-BR", ignoreCase = true) &&
                        !it.isNetworkConnectionRequired
                }
                ?: engine.voices
                    ?.firstOrNull {
                        it.locale.language == locale.language && !it.isNetworkConnectionRequired
                    }

            if (localVoice != null && languageStatus != TextToSpeech.LANG_MISSING_DATA &&
                languageStatus != TextToSpeech.LANG_NOT_SUPPORTED
            ) {
                engine.voice = localVoice
                ready = engine.voice?.isNetworkConnectionRequired == false
            }
        }
    }

    fun isOfflineAvailable(): Boolean = ready && engine.voice?.isNetworkConnectionRequired == false

    override suspend fun speak(text: String): InferenceMeta {
        val startedAt = System.nanoTime()
        if (!isOfflineAvailable()) return meta(startedAt, failed = true)

        val utteranceId = "eat-control-${utteranceCounter.incrementAndGet()}"

        return suspendCancellableCoroutine { continuation ->
            var firstAudioAt: Long? = null
            engine.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(id: String?) {
                    if (id == utteranceId) firstAudioAt = System.nanoTime()
                }

                override fun onDone(id: String?) {
                    if (id == utteranceId && continuation.isActive) {
                        val firstAudio = firstAudioAt ?: startedAt
                        continuation.resume(
                            InferenceMeta(
                                providerId = PROVIDER_ID,
                                version = "platform",
                                runtime = "android-offline",
                                latencyMs = (firstAudio - startedAt) / 1_000_000
                            )
                        )
                    }
                }

                @Deprecated("Sobrescrita obrigatória da classe abstrata.")
                override fun onError(id: String?) {
                    if (id == utteranceId && continuation.isActive) {
                        continuation.resume(meta(startedAt, failed = true))
                    }
                }
            })

            continuation.invokeOnCancellation { engine.stop() }
            if (engine.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId) == TextToSpeech.ERROR &&
                continuation.isActive
            ) {
                continuation.resume(meta(startedAt, failed = true))
            }
        }
    }

    fun shutdown() {
        engine.stop()
        engine.shutdown()
    }

    private fun meta(startedAt: Long, failed: Boolean) = InferenceMeta(
        providerId = if (failed) "${PROVIDER_ID}_unavailable" else PROVIDER_ID,
        version = "platform",
        runtime = "android-offline",
        latencyMs = (System.nanoTime() - startedAt) / 1_000_000
    )

    companion object {
        const val PROVIDER_ID = "android_tts_offline"
    }
}
