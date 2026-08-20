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
 * STT estritamente on-device do Android (`docs/adr/0005`, Fase A).
 *
 * `EXTRA_PREFER_OFFLINE` não é garantia: o reconhecedor padrão pode usar rede. Por isso este provider
 * só fica disponível quando [SpeechRecognizer.isOnDeviceRecognitionAvailable] é verdadeiro e nunca
 * cai para `createSpeechRecognizer`. Sem pacote local pt-BR, a UI mantém o fluxo por toque.
 */
class AndroidSttProvider(context: Context) : SttProvider {

    private val appContext = context.applicationContext

    /** Sempre verdadeiro durante uma transcrição; mantido para diagnóstico da UI. */
    var usingOnDevice: Boolean = isAvailable()
        private set

    override fun isAvailable(): Boolean =
        SpeechRecognizer.isOnDeviceRecognitionAvailable(appContext)

    override suspend fun transcribe(): SttResult = withContext(Dispatchers.Main) {
        check(isAvailable()) {
            "Reconhecimento offline pt-BR indisponível. Baixe o pacote de idioma ou use o botão Analisar."
        }

        val startedAt = System.nanoTime()
        usingOnDevice = true
        val recognizer = SpeechRecognizer.createOnDeviceSpeechRecognizer(appContext)

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
                providerId = PROVIDER_ID,
                version = "platform",
                runtime = "android-on-device",
                latencyMs = (System.nanoTime() - startedAt) / 1_000_000
            )
        )
    }

    companion object {
        const val PROVIDER_ID = "android_stt_on_device"
        private const val LANGUAGE = "pt-BR"
    }
}
