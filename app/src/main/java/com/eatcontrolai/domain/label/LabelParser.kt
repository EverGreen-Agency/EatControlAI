package com.eatcontrolai.domain.label

import com.eatcontrolai.core.model.Allergen
import com.eatcontrolai.core.model.ClaimPolarity
import com.eatcontrolai.core.model.LabelClaim

/**
 * Converte o texto bruto de um rótulo em afirmações estruturadas.
 *
 * Este é o passo que **promove** evidência: o OCR entrega `OCR_TEXT` (rank 5); quando o parser
 * reconhece uma declaração explícita do fabricante, o resultado vira `DECLARED_LABEL` (rank 2).
 * É essa promoção que implementa a hierarquia de `docs/SPEC.md`.
 *
 * A estratégia é de marcador + escopo, não de substring solta:
 *  1. localiza marcadores de polaridade ("CONTEM", "NAO CONTEM", "PODE CONTER", ...);
 *  2. cada marcador governa o texto até o próximo marcador **ou** o fim da frase, o que vier antes;
 *  3. procura alérgenos apenas dentro desse escopo.
 *
 * Sem o passo 2, "CONTÉM LEITE. FABRICADO EM LINHA QUE PROCESSA AMENDOIM" faria o amendoim herdar
 * a polaridade do leite.
 */
object LabelParser {

    private data class Marker(val text: String, val polarity: ClaimPolarity)

    private val markers = listOf(
        // Ordem por tamanho decrescente: "NAO CONTEM" precisa vencer "CONTEM".
        Marker("POSSIVEL PRESENCA DE", ClaimPolarity.MAY_CONTAIN),
        Marker("QUE TAMBEM PROCESSA", ClaimPolarity.MAY_CONTAIN),
        Marker("NAO CONTEM", ClaimPolarity.FREE_OF),
        Marker("PODE CONTER", ClaimPolarity.MAY_CONTAIN),
        Marker("INGREDIENTES", ClaimPolarity.CONTAINS),
        Marker("QUE PROCESSA", ClaimPolarity.MAY_CONTAIN),
        Marker("TRACOS DE", ClaimPolarity.MAY_CONTAIN),
        Marker("ISENTO DE", ClaimPolarity.FREE_OF),
        Marker("LIVRE DE", ClaimPolarity.FREE_OF),
        Marker("PROCESSA", ClaimPolarity.MAY_CONTAIN),
        Marker("CONTEM", ClaimPolarity.CONTAINS),
        Marker("ZERO", ClaimPolarity.FREE_OF),
        Marker("SEM", ClaimPolarity.FREE_OF)
    )

    private val markerRegex = Regex(
        markers.joinToString("|") { "\\b${Regex.escape(it.text)}\\b" }
    )

    private val sentenceEnd = charArrayOf('.', ';', '\n')

    /** Prioridade em caso de conflito: a afirmação mais restritiva vence. */
    private val polarityPriority = mapOf(
        ClaimPolarity.CONTAINS to 3,
        ClaimPolarity.MAY_CONTAIN to 2,
        ClaimPolarity.FREE_OF to 1
    )

    /**
     * Extrai afirmações de [rawLabelText]. Devolve no máximo uma afirmação por alérgeno,
     * mantendo a mais restritiva quando o rótulo se contradiz.
     */
    fun parse(rawLabelText: String): List<LabelClaim> {
        val text = TextNormalizer.normalize(rawLabelText)
        if (text.isBlank()) return emptyList()

        val matches = markerRegex.findAll(text).toList()
        if (matches.isEmpty()) return emptyList()

        val best = LinkedHashMap<Allergen, LabelClaim>()

        matches.forEachIndexed { index, match ->
            val polarity = polarityOf(match.value) ?: return@forEachIndexed
            val scopeStart = match.range.last + 1
            val nextMarkerStart = matches.getOrNull(index + 1)?.range?.first ?: text.length
            val scopeEnd = minOf(nextMarkerStart, sentenceEndAfter(text, scopeStart))
            if (scopeEnd <= scopeStart) return@forEachIndexed

            val scope = text.substring(scopeStart, scopeEnd)
            for ((allergen, term) in AllergenDictionary.findIn(scope, polarity)) {
                val claim = LabelClaim(
                    allergen = allergen,
                    polarity = polarity,
                    sourceText = "${match.value} … $term"
                )
                val current = best[allergen]
                if (current == null ||
                    polarityPriority.getValue(polarity) > polarityPriority.getValue(current.polarity)
                ) {
                    best[allergen] = claim
                }
            }
        }

        return best.values.toList()
    }

    private fun polarityOf(matchedText: String): ClaimPolarity? =
        markers.firstOrNull { it.text == matchedText }?.polarity

    private fun sentenceEndAfter(text: String, from: Int): Int {
        val index = text.indexOfAny(sentenceEnd, from)
        return if (index < 0) text.length else index
    }
}
