package com.eatcontrolai.glasses

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import com.eatcontrolai.inference.InferenceMeta
import com.eatcontrolai.inference.TtsProvider
import java.io.ByteArrayOutputStream
import kotlinx.coroutines.delay

/**
 * Óculos simulados — o "Mock Device Kit" da nossa camada.
 *
 * Vale distinguir de duas outras coisas:
 *  - o **Mock Device Kit da Meta** simula o dispositivo para a API do DAT, e depende do ADR-0006;
 *  - este mock simula o dispositivo para o **nosso app**, e funciona hoje.
 *
 * O edital deixa isso mais importante do que parece: os óculos e o smartphone só ficam disponíveis
 * no dia do hackathon presencial (18/09) e são fornecidos pela organização. Desenvolver contra este
 * mock não é plano B — é o caminho principal.
 *
 * A imagem é renderizada como uma embalagem, não injetada como texto: o frame passa por OCR real.
 */
class MockGlassesGateway(
    private val tts: TtsProvider,
    private val scenes: List<MockScene> = MockScenes.all
) : GlassesGateway {

    override val sourceId: String = "mock_glasses"

    override var isConnected: Boolean = false
        private set

    var currentScene: MockScene = scenes.first()

    fun selectScene(sceneId: String) {
        currentScene = scenes.firstOrNull { it.id == sceneId } ?: currentScene
    }

    override suspend fun connect() {
        delay(SIMULATED_CONNECT_MS)
        isConnected = true
    }

    override suspend fun disconnect() {
        isConnected = false
    }

    override suspend fun capturePhoto(): ByteArray {
        // Latência simulada da captura + transporte dos óculos para o telefone.
        delay(SIMULATED_CAPTURE_MS)
        return renderLabel(currentScene.labelText)
    }

    override suspend fun startVoiceCapture(): ByteArray {
        throw UnsupportedOperationException("Trilha de STT ainda não implementada")
    }

    override suspend fun playSpeech(text: String): InferenceMeta = tts.speak(text)

    /** Desenha o texto do rótulo em um bitmap para alimentar o OCR real. */
    private fun renderLabel(text: String): ByteArray {
        val bitmap = Bitmap.createBitmap(WIDTH, HEIGHT, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE)

        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(20, 20, 20)
            textSize = TEXT_SIZE
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
        }

        var y = MARGIN + TEXT_SIZE
        for (line in text.lines()) {
            for (wrapped in wrap(line, paint)) {
                canvas.drawText(wrapped, MARGIN, y, paint)
                y += LINE_HEIGHT
            }
        }

        return ByteArrayOutputStream().use { stream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, stream)
            bitmap.recycle()
            stream.toByteArray()
        }
    }

    private fun wrap(line: String, paint: Paint): List<String> {
        if (line.isBlank()) return listOf("")
        val maxWidth = WIDTH - 2 * MARGIN
        val out = mutableListOf<String>()
        var current = StringBuilder()
        for (word in line.split(" ")) {
            val candidate = if (current.isEmpty()) word else "$current $word"
            if (paint.measureText(candidate) <= maxWidth) {
                current = StringBuilder(candidate)
            } else {
                if (current.isNotEmpty()) out += current.toString()
                current = StringBuilder(word)
            }
        }
        if (current.isNotEmpty()) out += current.toString()
        return out
    }

    private companion object {
        const val WIDTH = 1080
        const val HEIGHT = 1440
        const val MARGIN = 56f
        const val TEXT_SIZE = 38f
        const val LINE_HEIGHT = 54f
        const val JPEG_QUALITY = 92
        const val SIMULATED_CONNECT_MS = 300L
        const val SIMULATED_CAPTURE_MS = 120L
    }
}
