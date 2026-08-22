package com.eatcontrolai.domain.glp1

import java.text.Normalizer

/**
 * Léxico proibido, aprovado em `VAL-GLP1-R1`.
 *
 * Existe como código, e não como recomendação em documento, para que uma frase nova com termo
 * proibido quebre o build em vez de chegar ao usuário. A comparação ignora acentos e caixa, e usa
 * fronteira de palavra: "tratamento" não deve casar com "trata".
 */
object ForbiddenLexicon {

    /**
     * Termos e expressões bloqueados.
     *
     * A ordem é estável de propósito: o resultado de [violations] é determinístico e testável.
     */
    val terms: List<String> = listOf(
        "seguro",
        "diagnostico",
        "evita sintomas",
        "vai causar",
        "nao vai causar",
        "garantido",
        "sem risco",
        "cura",
        "trata",
        "previne",
        "indicado",
        "contraindicado",
        "proibido",
        "liberado",
        "ideal para quem usa glp-1",
        "isso preserva musculo",
        "evita perda muscular"
    )

    private val diacritics = Regex("\\p{Mn}+")
    private val spaces = Regex("\\s+")

    private val patterns: List<Pair<String, Regex>> = terms.map { term ->
        term to Regex("(?<![\\p{L}\\p{N}])${Regex.escape(term)}(?![\\p{L}\\p{N}])")
    }

    /** Termos proibidos presentes no texto, na ordem declarada em [terms]. */
    fun violations(text: String): List<String> {
        val normalized = normalize(text)
        return patterns.filter { (_, pattern) -> pattern.containsMatchIn(normalized) }
            .map { (term, _) -> term }
    }

    fun isClean(text: String): Boolean = violations(text).isEmpty()

    private fun normalize(value: String): String {
        val decomposed = Normalizer.normalize(value, Normalizer.Form.NFD)
        return spaces.replace(diacritics.replace(decomposed, "").lowercase(), " ").trim()
    }
}
