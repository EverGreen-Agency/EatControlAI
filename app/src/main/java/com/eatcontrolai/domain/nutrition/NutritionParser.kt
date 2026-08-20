package com.eatcontrolai.domain.nutrition

import com.eatcontrolai.core.model.NutrientAmount
import com.eatcontrolai.core.model.Nutrient
import com.eatcontrolai.core.model.NutritionBasis
import com.eatcontrolai.core.model.NutritionFacts
import com.eatcontrolai.core.model.PortionInfo
import com.eatcontrolai.core.model.PortionUnit
import com.eatcontrolai.domain.label.TextNormalizer

/**
 * Converte o texto de uma tabela nutricional em números com base declarada.
 *
 * Segue a estrutura da tabela brasileira (IN 75/2020): uma linha por nutriente, colunas por
 * `100 g/ml` e por porção, e uma coluna de `%VD` que **não** é quantidade e precisa ser descartada.
 *
 * Três decisões de projeto sustentam a honestidade do resultado:
 *
 *  1. **Base explícita.** Um número sem base identificada entra como [NutritionBasis.UNKNOWN] e não
 *     participa de nenhuma soma. Adivinhar a base produziria totais errados com aparência correta.
 *  2. **`%VD` descartado.** O valor diário é percentual de referência, não quantidade consumida.
 *  3. **kJ ignorado.** Energia é registrada apenas em kcal, para não somar duas unidades diferentes.
 *
 * O parser é determinístico e sem dependência de Android, então roda igual no app e no teste JVM.
 */
object NutritionParser {

    /** Sinônimos aceitos por nutriente, em texto já normalizado (caixa alta, sem acento). */
    private val synonyms: List<Pair<Nutrient, List<String>>> = listOf(
        // Ordem importa: rótulos mais específicos primeiro para não serem capturados pelo genérico.
        Nutrient.ADDED_SUGARS to listOf("ACUCARES ADICIONADOS", "ACUCAR ADICIONADO"),
        Nutrient.TOTAL_SUGARS to listOf("ACUCARES TOTAIS", "ACUCARES", "ACUCAR"),
        Nutrient.SATURATED_FAT to listOf("GORDURAS SATURADAS", "GORDURA SATURADA"),
        Nutrient.TRANS_FAT to listOf("GORDURAS TRANS", "GORDURA TRANS"),
        Nutrient.TOTAL_FAT to listOf("GORDURAS TOTAIS", "GORDURA TOTAL", "GORDURAS", "LIPIDIOS"),
        Nutrient.FIBER to listOf("FIBRA ALIMENTAR", "FIBRAS ALIMENTARES", "FIBRAS", "FIBRA"),
        Nutrient.PROTEIN to listOf("PROTEINAS", "PROTEINA"),
        Nutrient.CARBOHYDRATE to listOf("CARBOIDRATOS", "CARBOIDRATO"),
        Nutrient.SODIUM to listOf("SODIO"),
        Nutrient.ENERGY to listOf("VALOR ENERGETICO", "ENERGIA", "CALORIAS")
    )

    /** Número seguido, opcionalmente, da unidade. `,` decimal é o padrão brasileiro. */
    private val measurement =
        Regex("(\\d+(?:[.,]\\d+)?)\\s*(KCAL|KJ|MG|G|ML|VD)?", RegexOption.IGNORE_CASE)

    private val portionPatterns = listOf(
        Regex("PORCAO[^0-9\\n]{0,20}(\\d+(?:[.,]\\d+)?)\\s*(G|ML)\\b"),
        Regex("PORCAO DE\\s*(\\d+(?:[.,]\\d+)?)\\s*(G|ML)\\b")
    )

    /**
     * Extrai a tabela de [rawText].
     *
     * Devolve [NutritionFacts.isEmpty] quando o texto não contém tabela reconhecível — nesse caso a
     * UI deve dizer que não há informação nutricional utilizável, não exibir zeros.
     */
    fun parse(rawText: String, source: String = PARSER_ID): NutritionFacts {
        val normalized = TextNormalizer.normalize(rawText)
        if (normalized.isBlank()) return NutritionFacts(source = source)

        val warnings = mutableListOf<String>()
        val portion = findPortion(normalized)
        var columns = detectColumns(normalized)

        if (columns.isEmpty() && portion != null) {
            // Rótulo de coluna única: a própria declaração de porção define a base. A suposição fica
            // registrada em warnings para o usuário poder desconfiar do número.
            columns = listOf(NutritionBasis.PER_PORTION)
            warnings += "A tabela não tem cabeçalho legível; assumi que os valores são da porção declarada."
        }
        if (columns.isEmpty()) {
            warnings += "Não identifiquei se os valores são por porção ou por 100 g/ml."
        }

        val amounts = LinkedHashMap<Pair<Nutrient, NutritionBasis>, NutrientAmount>()

        normalized.lines().forEach { line ->
            val nutrient = matchNutrient(line) ?: return@forEach
            val readings = readMeasurements(line, nutrient)
            if (readings.isEmpty()) return@forEach

            readings.forEachIndexed { index, value ->
                val basis = columns.getOrElse(index) { NutritionBasis.UNKNOWN }
                val amount = NutrientAmount(
                    nutrient = nutrient,
                    value = value,
                    basis = basis,
                    sourceText = line.trim()
                )
                // Primeira leitura de cada combinação vence: colunas extras da tabela costumam ser
                // repetição de referência, não um novo valor.
                amounts.putIfAbsent(nutrient to basis, amount)
            }
        }

        if (amounts.isEmpty()) {
            warnings += "Não encontrei valores numéricos associados aos nutrientes."
        }
        if (portion == null && amounts.values.any { it.basis == NutritionBasis.PER_100 }) {
            warnings += "A porção não foi declarada de forma legível; não consigo converter de 100 g/ml para porção."
        }

        return NutritionFacts(
            amounts = amounts.values.toList(),
            portion = portion,
            source = source,
            warnings = warnings
        )
    }

    /**
     * Descobre a ordem das colunas de quantidade.
     *
     * Procura o cabeçalho que menciona `100 G`/`100 ML` e/ou `PORCAO` e devolve as bases na ordem em
     * que aparecem. `%VD` não entra porque não é quantidade.
     */
    private fun detectColumns(text: String): List<NutritionBasis> {
        val candidates = text.lines().filter { line ->
            val mentionsPer100 = line.contains("100 G") || line.contains("100 ML")
            val mentionsPortion = line.contains("PORCAO")
            // "Porção de 30 g" declara o tamanho da porção, não o cabeçalho das colunas. Tratar essa
            // linha como cabeçalho inverteria as bases e produziria totais errados.
            val isPortionDeclaration = portionPatterns.any { it.containsMatchIn(line) }
            (mentionsPer100 || mentionsPortion) && (mentionsPer100 || !isPortionDeclaration)
        }

        val header = candidates.firstOrNull { line ->
            (line.contains("100 G") || line.contains("100 ML")) && line.contains("PORCAO")
        } ?: candidates.firstOrNull() ?: return emptyList()

        val per100 = listOf(header.indexOf("100 G"), header.indexOf("100 ML"))
            .filter { it >= 0 }
            .minOrNull()
        val perPortion = header.indexOf("PORCAO").takeIf { it >= 0 }

        return listOfNotNull(
            per100?.let { it to NutritionBasis.PER_100 },
            perPortion?.let { it to NutritionBasis.PER_PORTION }
        ).sortedBy { it.first }.map { it.second }
    }

    private fun findPortion(text: String): PortionInfo? {
        portionPatterns.forEach { pattern ->
            val match = pattern.find(text) ?: return@forEach
            val amount = match.groupValues[1].toDoubleOrNullBr() ?: return@forEach
            if (amount <= 0.0) return@forEach
            val unit = if (match.groupValues[2] == "ML") PortionUnit.MILLILITER else PortionUnit.GRAM
            return PortionInfo(amount = amount, unit = unit, sourceText = match.value.trim())
        }
        return null
    }

    private fun matchNutrient(line: String): Nutrient? {
        synonyms.forEach { (nutrient, terms) ->
            if (terms.any { line.contains(it) }) return nutrient
        }
        return null
    }

    /**
     * Lê as quantidades de uma linha, descartando `%VD` e unidades incompatíveis com o nutriente.
     */
    private fun readMeasurements(line: String, nutrient: Nutrient): List<Double> {
        val values = mutableListOf<Double>()

        measurement.findAll(line).forEach { match ->
            val rawValue = match.groupValues[1]
            val unit = match.groupValues[2].uppercase()

            // O número da própria descrição da porção não é quantidade de nutriente.
            if (unit == "VD") return@forEach

            val accepted = when (nutrient) {
                // Energia só em kcal: kJ duplicaria o mesmo dado em outra unidade.
                Nutrient.ENERGY -> unit == "KCAL" || unit.isEmpty()
                Nutrient.SODIUM -> unit == "MG" || unit.isEmpty()
                else -> unit == "G" || unit.isEmpty()
            }
            if (!accepted) return@forEach

            rawValue.toDoubleOrNullBr()?.let { values += it }
        }

        return values
    }

    /** `5,2` e `5.2` são o mesmo número em rótulo brasileiro. */
    private fun String.toDoubleOrNullBr(): Double? = replace(',', '.').toDoubleOrNull()

    const val PARSER_ID = "nutrition_parser_v1"
}
