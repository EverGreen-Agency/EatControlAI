package com.eatcontrolai.domain.voice

import com.eatcontrolai.core.model.Allergen
import com.eatcontrolai.orchestration.AnalysisTrack
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class VoiceIntentParserTest {

    @Test
    fun `pergunta generica vai para a trilha de rotulo`() {
        val intent = VoiceIntentParser.parse("Posso comer isso?")
        assertEquals(AnalysisTrack.LABEL, intent.track)
        assertNull(intent.focus)
        assertTrue(intent.recognized)
    }

    @Test
    fun `mencao a codigo de barras muda a trilha`() {
        assertEquals(
            AnalysisTrack.BARCODE,
            VoiceIntentParser.parse("Lê o código de barras desse produto").track
        )
    }

    @Test
    fun `alergeno citado vira foco da pergunta`() {
        val intent = VoiceIntentParser.parse("Isso aqui tem leite?")
        assertEquals(Allergen.MILK, intent.focus)
        assertEquals(AnalysisTrack.LABEL, intent.track)
    }

    @Test
    fun `acento e caixa nao atrapalham`() {
        assertEquals(
            AnalysisTrack.BARCODE,
            VoiceIntentParser.parse("CÓDIGO DE BARRAS").track
        )
        assertEquals(Allergen.GLUTEN, VoiceIntentParser.parse("tem glúten?").focus)
    }

    @Test
    fun `transcricao vazia nao e reconhecida`() {
        assertFalse(VoiceIntentParser.parse("   ").recognized)
    }
}
