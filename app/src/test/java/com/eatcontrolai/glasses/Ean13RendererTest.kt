package com.eatcontrolai.glasses

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * A codificação é Kotlin puro, então dá para testar sem aparelho. A prova de que o ML Kit consegue
 * ler as barras desenhadas está no teste instrumentado `BarcodeScanInstrumentedTest`.
 */
class Ean13RendererTest {

    @Test
    fun `digito verificador segue a especificacao`() {
        // Exemplo canônico da especificação EAN-13: 4006381333931.
        assertEquals(1, Ean13Renderer.checkDigit("400638133393"))
        assertEquals(3, Ean13Renderer.checkDigit("789100010010"))
    }

    @Test
    fun `valida EAN completo`() {
        assertTrue(Ean13Renderer.isValid("7891000100103"))
        assertFalse(Ean13Renderer.isValid("7891000100104"))
        assertFalse(Ean13Renderer.isValid("789100010010"))
        assertFalse(Ean13Renderer.isValid("789100010010X"))
    }

    @Test
    fun `codificacao tem 95 modulos com guardas nas posicoes certas`() {
        val encoded = Ean13Renderer.encode("7891000100103")
        assertEquals(95, encoded.length)
        assertEquals("101", encoded.take(3))
        assertEquals("01010", encoded.substring(45, 50))
        assertEquals("101", encoded.takeLast(3))
    }

    @Test
    fun `primeiro digito e codificado na paridade dos digitos da esquerda`() {
        // Dois EANs que só diferem no primeiro dígito precisam gerar sequências diferentes,
        // porque o primeiro dígito não tem barras próprias — vive na paridade.
        val a = Ean13Renderer.encode("0123456789012")
        val b = Ean13Renderer.encode("1123456789011")
        assertEquals(95, a.length)
        assertEquals(95, b.length)
        assertTrue("A paridade deveria diferenciar os dois códigos", a != b)
    }

    @Test
    fun `EAN invalido e rejeitado na codificacao`() {
        val error = runCatching { Ean13Renderer.encode("1234567890123") }.exceptionOrNull()
        assertTrue(error is IllegalArgumentException)
    }
}
