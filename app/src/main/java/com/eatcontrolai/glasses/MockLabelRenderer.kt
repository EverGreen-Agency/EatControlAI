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

    const val WIDTH = 1080
    const val HEIGHT = 1440
    private const val MARGIN_RATIO = 56f / 1080f
    private const val TEXT_SIZE_RATIO = 38f / 1080f
    private const val LINE_HEIGHT_RATIO = 54f / 1080f
    private const val JPEG_QUALITY = 92

    fun render(text: String): ByteArray = render(text, WIDTH, HEIGHT, JPEG_QUALITY)

    /**
     * Renderiza em resolução e qualidade arbitrárias.
     *
     * A escala é proporcional à largura, então o texto ocupa a mesma fração da imagem em qualquer
     * resolução — é isso que torna a varredura de [com.eatcontrolai.benchmark.CaptureConfigBenchmark]
     * uma comparação justa: muda o tamanho do pixel, não o enquadramento.
     */
    fun render(text: String, width: Int, height: Int, jpegQuality: Int): ByteArray {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE)

        val margin = width * MARGIN_RATIO
        val lineHeight = width * LINE_HEIGHT_RATIO
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(20, 20, 20)
            textSize = width * TEXT_SIZE_RATIO
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
        }

        var y = margin + paint.textSize
        for (line in text.lines()) {
            for (wrapped in wrap(line, paint, width, margin)) {
                canvas.drawText(wrapped, margin, y, paint)
                y += lineHeight
            }
        }

        return ByteArrayOutputStream().use { stream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, jpegQuality, stream)
            bitmap.recycle()
            stream.toByteArray()
        }
    }

    private fun wrap(line: String, paint: Paint, width: Int, margin: Float): List<String> {
        if (line.isBlank()) return listOf("")
        val maxWidth = width - 2 * margin
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
