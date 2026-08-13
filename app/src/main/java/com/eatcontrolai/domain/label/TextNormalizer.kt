package com.eatcontrolai.domain.label

import java.text.Normalizer

/**
 * Normalização de texto de rótulo antes de qualquer casamento de padrão.
 *
 * OCR de embalagem devolve texto com acento inconsistente, caixa mista, hifenização de quebra de
 * linha e pontuação ruidosa. Casar substring no texto cru é a causa raiz dos falsos negativos
 * descritos em `docs/01_ESTRUTURA_E_ESTADO_ATUAL.md` §5-C.
 */
object TextNormalizer {

    private val diacritics = Regex("\\p{Mn}+")
    private val lineBreakHyphen = Regex("-\\s*\\n\\s*")
    private val noise = Regex("[^A-Z0-9:.;,\\n ]")
    private val extraSpaces = Regex("[ \\t]+")

    /**
     * Devolve o texto em caixa alta, sem acentos, sem hifenização de quebra de linha e com
     * espaçamento colapsado. Preserva `.`, `;`, `,`, `:` e quebras de linha porque são os
     * delimitadores usados para separar afirmações no rótulo.
     */
    fun normalize(raw: String): String {
        val dehyphenated = lineBreakHyphen.replace(raw, "")
        val decomposed = Normalizer.normalize(dehyphenated, Normalizer.Form.NFD)
        val unaccented = diacritics.replace(decomposed, "")
        val upper = unaccented.uppercase()
        val cleaned = noise.replace(upper, " ")
        return extraSpaces.replace(cleaned, " ").trim()
    }
}
