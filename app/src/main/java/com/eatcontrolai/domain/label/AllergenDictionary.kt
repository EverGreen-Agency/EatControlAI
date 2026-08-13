package com.eatcontrolai.domain.label

import com.eatcontrolai.core.model.Allergen
import com.eatcontrolai.core.model.ClaimPolarity

/**
 * Dicionário de sinônimos usados em rótulos brasileiros.
 *
 * Deliberadamente determinístico e auditável (`docs/adr/0003`, `contexto-gpt.md` §47): um revisor
 * humano — inclusive um nutricionista — consegue ler esta lista e dizer se está certa. Nenhum
 * modelo generativo participa deste passo.
 *
 * Os termos são escritos já normalizados (caixa alta, sem acento) porque a busca acontece sobre
 * a saída de [TextNormalizer].
 */
object AllergenDictionary {

    private val synonyms: Map<Allergen, List<String>> = mapOf(
        Allergen.MILK to listOf(
            "DERIVADOS DE LEITE", "PROTEINA DO LEITE", "SORO DE LEITE", "LEITE EM PO",
            "CREME DE LEITE", "LEITE", "LACTOSE", "CASEINA", "CASEINATO", "MANTEIGA",
            "QUEIJO", "IOGURTE", "REQUEIJAO", "COALHADA", "LACTOSOROS"
        ),
        Allergen.GLUTEN to listOf(
            "FARINHA DE TRIGO", "GLUTEN", "TRIGO", "CENTEIO", "CEVADA", "AVEIA",
            "TRITICALE", "MALTE", "SEMOLA"
        ),
        Allergen.SOY to listOf(
            "LECITINA DE SOJA", "PROTEINA DE SOJA", "SOJA"
        ),
        Allergen.EGG to listOf(
            "CLARA DE OVO", "GEMA DE OVO", "OVALBUMINA", "ALBUMINA", "OVOS", "OVO"
        ),
        Allergen.PEANUT to listOf(
            "AMENDOIM"
        ),
        Allergen.TREE_NUTS to listOf(
            "CASTANHA DE CAJU", "CASTANHA DO PARA", "CASTANHA", "CASTANHAS",
            "NOZES", "NOZ", "AMENDOA", "AMENDOAS", "AVELA", "AVELAS",
            "PISTACHE", "MACADAMIA", "PECA"
        ),
        Allergen.FISH to listOf(
            "PEIXE", "PESCADO", "ATUM", "SALMAO", "BACALHAU", "SARDINHA"
        ),
        Allergen.CRUSTACEANS to listOf(
            "CRUSTACEOS", "CRUSTACEO", "CAMARAO", "LAGOSTA", "CARANGUEJO", "SIRI"
        ),
        Allergen.SESAME to listOf(
            "GERGELIM", "TAHINE"
        )
    )

    /**
     * Termos ordenados do mais longo para o mais curto, para que "DERIVADOS DE LEITE" case antes
     * de "LEITE" e o trecho reportado ao usuário seja o mais específico possível.
     */
    private val termsByLength: List<Pair<String, Allergen>> =
        synonyms.flatMap { (allergen, terms) -> terms.map { it to allergen } }
            .sortedByDescending { it.first.length }

    /**
     * Termos que provam **presença** do alérgeno mas não provam **ausência** dele.
     *
     * "ZERO LACTOSE" é o caso clássico: o produto teve a lactose quebrada por enzima e continua
     * cheio de proteína do leite. Tratar isso como ausência de leite seria um *false-safe* para
     * alergia à proteína do leite — exatamente a métrica que `docs/METRICS.md` manda tender a zero.
     *
     * Numa afirmação de ausência, estes termos não geram evidência nenhuma: o alérgeno fica sem
     * resposta e o motor cai em "precisa confirmar", que é o comportamento correto.
     */
    private val notProofOfAbsence = setOf("LACTOSE")

    /**
     * Encontra alérgenos citados em [normalizedText], que já deve ter passado por [TextNormalizer].
     * Cada alérgeno aparece uma única vez, associado ao termo mais específico encontrado.
     *
     * [polarity] importa porque nem todo termo sustenta os dois sentidos da afirmação.
     */
    fun findIn(
        normalizedText: String,
        polarity: ClaimPolarity = ClaimPolarity.CONTAINS
    ): List<Pair<Allergen, String>> {
        val found = LinkedHashMap<Allergen, String>()
        for ((term, allergen) in termsByLength) {
            if (allergen in found) continue
            if (polarity == ClaimPolarity.FREE_OF && term in notProofOfAbsence) continue
            if (containsAsWord(normalizedText, term)) found[allergen] = term
        }
        return found.map { it.key to it.value }
    }

    /**
     * Casa o termo respeitando fronteira de palavra. Sem isso, "NOZ" casaria dentro de
     * "NOZES MOSCADA" e "OVO" dentro de "OVOLACTOVEGETARIANO".
     */
    private fun containsAsWord(text: String, term: String): Boolean {
        var index = text.indexOf(term)
        while (index >= 0) {
            val before = index - 1
            val after = index + term.length
            val boundaryBefore = before < 0 || !text[before].isLetterOrDigit()
            val boundaryAfter = after >= text.length || !text[after].isLetterOrDigit()
            if (boundaryBefore && boundaryAfter) return true
            index = text.indexOf(term, index + 1)
        }
        return false
    }
}
