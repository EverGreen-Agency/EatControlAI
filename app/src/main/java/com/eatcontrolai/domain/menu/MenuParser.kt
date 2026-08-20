package com.eatcontrolai.domain.menu

import java.text.Normalizer

/** Uma opção observada no cardápio. Nenhum campo nutricional é inferido. */
data class MenuOption(
    val id: String,
    val section: String? = null,
    val name: String,
    val description: String = "",
    /** Preço preservado como texto para nunca ser confundido com nutriente ou porção. */
    val priceText: String? = null,
    val observedTerms: List<String> = emptyList(),
    val sourceLines: List<String> = emptyList()
)

data class MenuAnalysis(
    val options: List<MenuOption>,
    val warnings: List<String> = emptyList(),
    val selectedOptionId: String? = null,
    val registered: Boolean = false
) {
    val selectedOption: MenuOption?
        get() = options.firstOrNull { it.id == selectedOptionId }
}

/**
 * Parser conservador de cardápio: organiza linhas de OCR sem inventar receita, quantidade ou macro.
 *
 * Preço é extraído somente quando aparece como moeda ou decimal de duas casas no fim da linha. O
 * texto original de cada opção permanece em [MenuOption.sourceLines] para revisão na interface.
 */
object MenuParser {

    private val priceAtEnd = Regex(
        pattern = "(?:R\\$\\s*)?([0-9]{1,4}[.,][0-9]{2})\\s*$",
        option = RegexOption.IGNORE_CASE
    )
    private val leadersAtEnd = Regex("[.·•_\\-\\s]{2,}$")
    private val spaces = Regex("\\s+")
    private val diacritics = Regex("\\p{Mn}+")

    private val sectionNames = setOf(
        "ENTRADAS", "ENTRADA", "PETISCOS", "SALADAS", "SOPAS",
        "PRATOS", "PRATOS PRINCIPAIS", "PRINCIPAIS", "MASSAS", "CARNES",
        "AVES", "PEIXES", "VEGETARIANOS", "SANDUICHES", "SANDUICHES E LANCHES",
        "SOBREMESAS", "BEBIDAS", "DRINKS", "MENU EXECUTIVO"
    )

    private val ignoredTitles = setOf("CARDAPIO", "MENU", "RESTAURANTE", "DELIVERY")

    private val factualTerms = linkedMapOf(
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

    /**
     * Quantas linhas terminam em preço.
     *
     * Preço é o sinal estrutural mais forte de cardápio: rótulo de embalagem não tem. Exposto aqui
     * para o [com.eatcontrolai.domain.routing.ContextRouter] reaproveitar a mesma regex em vez de
     * manter uma cópia que pode divergir.
     */
    fun pricedLineCount(rawText: String): Int =
        rawText.lines().count { priceAtEnd.containsMatchIn(it) }

    fun parse(rawText: String): MenuAnalysis {
        val lines = rawText.lineSequence()
            .map { spaces.replace(it.trim(), " ") }
            .filter { it.isNotBlank() }
            .toList()

        if (lines.isEmpty()) {
            return MenuAnalysis(emptyList(), listOf("Nenhum texto legível foi encontrado no cardápio."))
        }

        val drafts = mutableListOf<Draft>()
        var section: String? = null
        var pending: Draft? = null

        fun flushPending() {
            pending?.takeIf { it.name.isNotBlank() }?.let(drafts::add)
            pending = null
        }

        lines.forEach { line ->
            val normalized = normalize(line)
            when {
                normalized in ignoredTitles -> Unit

                normalized in sectionNames -> {
                    flushPending()
                    section = line
                }

                priceAtEnd.containsMatchIn(line) -> {
                    val match = requireNotNull(priceAtEnd.find(line))
                    val price = match.value.trim()
                    val stem = leadersAtEnd.replace(line.substring(0, match.range.first).trim(), "").trim()
                    if (stem.isNotBlank()) {
                        flushPending()
                        pending = Draft(
                            section = section,
                            name = stem,
                            priceText = price,
                            sourceLines = mutableListOf(line)
                        )
                    } else if (pending != null) {
                        pending = pending!!.copy(
                            priceText = price,
                            sourceLines = (pending!!.sourceLines + line).toMutableList()
                        )
                        flushPending()
                    }
                }

                pending == null -> pending = Draft(
                    section = section,
                    name = line,
                    sourceLines = mutableListOf(line)
                )

                looksLikeItemName(line) -> {
                    flushPending()
                    pending = Draft(
                        section = section,
                        name = line,
                        sourceLines = mutableListOf(line)
                    )
                }

                else -> pending = pending!!.copy(
                    description = listOf(pending!!.description, line)
                        .filter { it.isNotBlank() }
                        .joinToString(" "),
                    sourceLines = (pending!!.sourceLines + line).toMutableList()
                )
            }
        }
        flushPending()

        val options = drafts
            .filterNot { normalize(it.name) in ignoredTitles || normalize(it.name) in sectionNames }
            .mapIndexed { index, draft ->
                val searchable = normalize("${draft.name} ${draft.description}")
                MenuOption(
                    id = "menu-option-${index + 1}",
                    section = draft.section,
                    name = draft.name,
                    description = draft.description,
                    priceText = draft.priceText,
                    observedTerms = factualTerms
                        .filterKeys(searchable::contains)
                        .values
                        .distinct(),
                    sourceLines = draft.sourceLines.toList()
                )
            }

        val warnings = buildList {
            if (options.isEmpty()) add("O texto foi lido, mas nenhuma opção pôde ser estruturada.")
            if (options.none { it.priceText != null }) {
                add("Nenhum preço confiável foi identificado; isso não impede revisar as opções.")
            }
        }
        return MenuAnalysis(options = options, warnings = warnings)
    }

    private fun looksLikeItemName(line: String): Boolean {
        val letters = line.filter(Char::isLetter)
        if (letters.isEmpty() || line.length > 80 || line.split(' ').size > 12) return false
        val uppercaseRatio = letters.count(Char::isUpperCase).toDouble() / letters.length
        return uppercaseRatio >= 0.80 && !line.endsWith('.')
    }

    private fun normalize(value: String): String {
        val decomposed = Normalizer.normalize(value, Normalizer.Form.NFD)
        return spaces.replace(diacritics.replace(decomposed, "").uppercase(), " ").trim()
    }

    private data class Draft(
        val section: String?,
        val name: String,
        val description: String = "",
        val priceText: String? = null,
        val sourceLines: MutableList<String>
    )
}
