package com.eatcontrolai.domain.voice

import com.eatcontrolai.core.model.Allergen
import com.eatcontrolai.domain.label.AllergenDictionary
import com.eatcontrolai.domain.label.TextNormalizer
import com.eatcontrolai.orchestration.AnalysisTrack

/**
 * Interpreta o comando falado.
 *
 * Determinístico por escolha, não por limitação: mandar "posso comer isso?" para um LLM não
 * acrescenta nada que casar padrão não resolva, e acrescenta latência e dependência de rede no
 * caminho crítico (`contexto-gpt.md` §48). Um LLM entra quando houver pergunta que exija
 * interpretação de verdade.
 */
object VoiceIntentParser {

    data class Intent(
        val track: AnalysisTrack,
        /** Alérgeno citado explicitamente na pergunta, quando houver. */
        val focus: Allergen?,
        val transcript: String
    ) {
        val recognized: Boolean get() = transcript.isNotBlank()
    }

    private val barcodeHints = listOf(
        "CODIGO DE BARRAS", "CODIGO", "BARRAS", "EAN", "PRODUTO EMBALADO", "ESCANEAR PRODUTO"
    )

    fun parse(transcript: String): Intent {
        val normalized = TextNormalizer.normalize(transcript)

        val track = if (barcodeHints.any { it in normalized }) AnalysisTrack.BARCODE
        else AnalysisTrack.LABEL

        val focus = AllergenDictionary.findIn(normalized).firstOrNull()?.first

        return Intent(track = track, focus = focus, transcript = transcript.trim())
    }
}
