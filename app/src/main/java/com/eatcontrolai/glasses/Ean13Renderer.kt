package com.eatcontrolai.glasses

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import java.io.ByteArrayOutputStream

/**
 * Desenha um código de barras EAN-13 **de verdade** na embalagem simulada.
 *
 * Existe porque o mock anterior desenhava só texto: o `MlKitBarcodeProvider` era chamado, não
 * encontrava barra nenhuma e devolvia `null` sempre. A trilha de barcode ficava marcada como pronta
 * sem nunca ter decodificado nada.
 *
 * Com barras reais, o ML Kit faz a leitura de verdade e uma falha de decodificação aparece como
 * falha — que é o comportamento útil para benchmark.
 *
 * Codificação conforme a especificação EAN-13: 95 módulos (guarda 101 + 6 dígitos + guarda central
 * 01010 + 6 dígitos + guarda 101), com o primeiro dígito codificado na paridade L/G dos seis
 * dígitos da esquerda.
 */
object Ean13Renderer {

    private val L = arrayOf(
        "0001101", "0011001", "0010011", "0111101", "0100011",
        "0110001", "0101111", "0111011", "0110111", "0001011"
    )
    private val G = arrayOf(
        "0100111", "0110011", "0011011", "0100001", "0011101",
        "0111001", "0000101", "0010001", "0001001", "0010111"
    )
    private val R = arrayOf(
        "1110010", "1100110", "1101100", "1000010", "1011100",
        "1001110", "1010000", "1000100", "1001000", "1110100"
    )

    /** Paridade dos seis dígitos da esquerda, determinada pelo primeiro dígito. */
    private val PARITY = arrayOf(
        "LLLLLL", "LLGLGG", "LLGGLG", "LLGGGL", "LGLLGG",
        "LGGLLG", "LGGGLL", "LGLGLG", "LGLGGL", "LGGLGL"
    )

    private const val WIDTH = 1080
    private const val HEIGHT = 1440
    private const val MODULE_WIDTH = 6
    private const val BAR_HEIGHT = 320
    private const val JPEG_QUALITY = 95

    /** Calcula o dígito verificador dos 12 primeiros dígitos. */
    fun checkDigit(first12: String): Int {
        require(first12.length == 12) { "EAN-13 precisa de 12 dígitos antes do verificador" }
        val sum = first12.mapIndexed { index, char ->
            val digit = Character.getNumericValue(char)
            if (index % 2 == 0) digit else digit * 3
        }.sum()
        return (10 - sum % 10) % 10
    }

    fun isValid(ean: String): Boolean =
        ean.length == 13 && ean.all { it.isDigit() } &&
            checkDigit(ean.take(12)) == Character.getNumericValue(ean[12])

    /** Sequência de 95 módulos, `1` = barra escura. */
    fun encode(ean: String): String {
        require(isValid(ean)) { "EAN-13 inválido: $ean" }
        val digits = ean.map { Character.getNumericValue(it) }
        val parity = PARITY[digits[0]]

        val left = buildString {
            for (i in 1..6) {
                append(if (parity[i - 1] == 'L') L[digits[i]] else G[digits[i]])
            }
        }
        val right = buildString {
            for (i in 7..12) append(R[digits[i]])
        }
        return "101" + left + "01010" + right + "101"
    }

    /**
     * Renderiza a embalagem: nome do produto, marca, as barras e o número legível.
     * O texto ao redor também é lido pelo OCR, então a mesma imagem serve às duas trilhas.
     */
    fun render(ean: String, productName: String, brand: String): ByteArray {
        val bitmap = Bitmap.createBitmap(WIDTH, HEIGHT, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE)

        val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(20, 20, 20)
            textSize = 52f
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        }
        val brandPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(90, 90, 90)
            textSize = 36f
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
        }
        canvas.drawText(productName, 64f, 140f, titlePaint)
        canvas.drawText(brand, 64f, 196f, brandPaint)

        val encoded = encode(ean)
        val barsWidth = encoded.length * MODULE_WIDTH
        val startX = (WIDTH - barsWidth) / 2f
        val topY = 420f

        val barPaint = Paint().apply { color = Color.BLACK }
        encoded.forEachIndexed { index, module ->
            if (module == '1') {
                val x = startX + index * MODULE_WIDTH
                canvas.drawRect(x, topY, x + MODULE_WIDTH, topY + BAR_HEIGHT, barPaint)
            }
        }

        val numberPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            textSize = 46f
            typeface = Typeface.MONOSPACE
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText(
            ean.chunked(1).joinToString(" "),
            WIDTH / 2f,
            topY + BAR_HEIGHT + 64f,
            numberPaint
        )

        return ByteArrayOutputStream().use { stream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, stream)
            bitmap.recycle()
            stream.toByteArray()
        }
    }
}
