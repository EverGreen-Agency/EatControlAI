package com.eatcontrolai.glasses

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import java.io.ByteArrayOutputStream

/**
 * Desenha o texto de um rótulo em um JPEG.
 *
 * Vive separado do [MockGlassesGateway] porque o harness de benchmark também precisa gerar frames,
 * e não faz sentido o benchmark depender de TTS ou do ciclo de conexão dos óculos.
 */
object MockLabelRenderer {

    private const val WIDTH = 1080
    private const val HEIGHT = 1440
    private const val MARGIN = 56f
    private const val TEXT_SIZE = 38f
    private const val LINE_HEIGHT = 54f
    private const val JPEG_QUALITY = 92

    fun render(text: String): ByteArray {
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
}
