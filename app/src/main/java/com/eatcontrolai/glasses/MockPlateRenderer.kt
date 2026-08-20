package com.eatcontrolai.glasses

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import java.io.ByteArrayOutputStream

/** Desenha um prato sintético; não injeta classes no provider visual. */
object MockPlateRenderer {

    fun render(): ByteArray {
        val bitmap = Bitmap.createBitmap(WIDTH, HEIGHT, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.rgb(205, 188, 160))
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        paint.color = Color.rgb(247, 247, 242)
        canvas.drawCircle(WIDTH / 2f, HEIGHT / 2f, 455f, paint)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 18f
        paint.color = Color.rgb(210, 210, 202)
        canvas.drawCircle(WIDTH / 2f, HEIGHT / 2f, 430f, paint)
        paint.style = Paint.Style.FILL

        // Arroz, feijão, frango e salada estilizados apenas para exercitar o frame real.
        paint.color = Color.rgb(238, 225, 185)
        canvas.drawOval(RectF(170f, 390f, 575f, 865f), paint)
        paint.color = Color.rgb(92, 48, 32)
        canvas.drawOval(RectF(540f, 430f, 885f, 805f), paint)
        paint.color = Color.rgb(190, 125, 72)
        canvas.drawRoundRect(RectF(360f, 805f, 780f, 1050f), 70f, 70f, paint)
        paint.color = Color.rgb(57, 135, 68)
        repeat(12) { index ->
            val x = 190f + (index % 6) * 100f
            val y = 940f + (index / 6) * 95f
            canvas.drawCircle(x, y, 55f, paint)
        }

        return ByteArrayOutputStream().use { stream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, stream)
            bitmap.recycle()
            stream.toByteArray()
        }
    }

    private const val WIDTH = 1080
    private const val HEIGHT = 1440
    private const val JPEG_QUALITY = 92
}
