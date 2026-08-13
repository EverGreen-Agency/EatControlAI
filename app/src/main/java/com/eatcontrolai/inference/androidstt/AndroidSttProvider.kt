package com.eatcontrolai.inference.androidstt

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import com.eatcontrolai.inference.InferenceMeta
import com.eatcontrolai.inference.SttProvider
import com.eatcontrolai.inference.SttResult
import kotlin.coroutines.resume
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext

/**
 * STT on-device do Android (`docs/adr/0005`, Fase A).
 *
 * `minSdk` 33 permite [SpeechRecognizer.createOnDeviceSpeechRecognizer], que roda **sem rede** e
 * portanto sustenta o ADR-0002: o caminho crítico da interação não depende de nuvem. Quando o
 * aparelho não tem o reconhecedor local, cai para o reconhecedor padrão do sistema e a UI mostra
 * isso — a diferença importa para a conversa de Edge AI com a banca.
 *
 * Requer `RECORD_AUDIO`, pedida na tela antes do primeiro uso.
 */
class AndroidSttProvider(context: Context) : SttProvider {

    private val appContext = context.applicationContext

    /** `true` quando o reconhecimento roda no próprio aparelho, sem rede. */
    var usingOnDevice: Boolean = false
        private set

    override fun isAvailable(): Boolean =
        SpeechRecognizer.isRecognitionAvailable(appContext) ||
            SpeechRecognizer.isOnDeviceRecognitionAvailable(appContext)

    override suspend fun transcribe(): SttResult = withContext(Dispatchers.Main) {
        val startedAt = System.nanoTime()

        val onDevice = SpeechRecognizer.isOnDeviceRecognitionAvailable(appContext)
        usingOnDevice = onDevice

        val recognizer = if (onDevice) {
            SpeechRecognizer.createOnDeviceSpeechRecognizer(appContext)
        } else {
            SpeechRecognizer.createSpeechRecognizer(appContext)
        }

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, LANGUAGE)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
            putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true)
        }

        val text = suspendCancellableCoroutine { continuation ->
            recognizer.setRecognitionListener(object : RecognitionListener {
                override fun onResults(results: Bundle?) {
                    val best = results
                        ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        ?.firstOrNull()
                        .orEmpty()
                    if (continuation.isActive) continuation.resume(best)
                }

                override fun onError(error: Int) {
                    if (continuation.isActive) continuation.resume("")
                }

                override fun onReadyForSpeech(params: Bundle?) = Unit
                override fun onBeginningOfSpeech() = Unit
                override fun onRmsChanged(rmsdB: Float) = Unit
                override fun onBufferReceived(buffer: ByteArray?) = Unit
                override fun onEndOfSpeech() = Unit
                override fun onPartialResults(partialResults: Bundle?) = Unit
                override fun onEvent(eventType: Int, params: Bundle?) = Unit
            })

            continuation.invokeOnCancellation {
                recognizer.cancel()
                recognizer.destroy()
            }

            recognizer.startListening(intent)
        }

        recognizer.destroy()

        SttResult(
            text = text,
            meta = InferenceMeta(
                providerId = if (onDevice) PROVIDER_ID_ON_DEVICE else PROVIDER_ID_SYSTEM,
                version = "platform",
                runtime = if (onDevice) "android-on-device" else "android-system",
                latencyMs = (System.nanoTime() - startedAt) / 1_000_000
            )
        )
    }

    companion object {
        const val PROVIDER_ID_ON_DEVICE = "android_stt_on_device"
        const val PROVIDER_ID_SYSTEM = "android_stt_system"
        private const val LANGUAGE = "pt-BR"
    }
}
