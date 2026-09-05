package com.eatcontrolai.domain.label

/**
 * Vocabulário de termos factuais observáveis em texto de alimento.
 *
 * É o mesmo vocabulário para cardápio e para rótulo de propósito: o rule pack GLP-1 reage a
 * "fritura" e "molho cremoso" independentemente de a frase ter vindo da descrição de um prato ou
 * da embalagem. Duas listas paralelas divergiriam na primeira vez que alguém acrescentasse um termo
 * em um lugar só.
 *
 * O que este objeto **não** faz: inferir composição. "Molho" observado é a palavra "molho" no texto,
 * nada além disso — quem decide o que fazer com o achado é `domain/glp1`.
 */
object FoodTerms {

    /**
     * Radical → termo canônico.
     *
     * Radical, e não palavra inteira, porque o português flexiona o que interessa aqui: `FRIT`
     * cobre frito, frita, fritas e fritura sem manter quatro entradas que podem sair de sincronia.
     * A ordem é estável para que a saída seja determinística e testável.
     */
    private val stems = linkedMapOf(
        "GRELHAD" to "grelhado",
        "FRIT" to "frito",
        "ASSAD" to "assado",
        "EMPANAD" to "empanado",
        "CREMOS" to "cremoso",
        "MOLHO" to "molho",
        "QUEIJO" to "queijo",
        "SALADA" to "salada",
        "LEGUME" to "legumes"
    )

    /** Termos observados no texto, na ordem declarada em [stems]. */
    fun observedIn(rawText: String): List<String> {
        if (rawText.isBlank()) return emptyList()
        val normalized = TextNormalizer.normalize(rawText)
        return stems.filterKeys(normalized::contains).values.distinct()
    }
}
