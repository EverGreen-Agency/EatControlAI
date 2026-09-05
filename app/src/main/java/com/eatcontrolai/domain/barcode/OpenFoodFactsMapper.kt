package com.eatcontrolai.domain.barcode

import com.eatcontrolai.core.model.Nutrient
import com.eatcontrolai.core.model.NutrientAmount
import com.eatcontrolai.core.model.NutritionBasis
import com.eatcontrolai.core.model.NutritionFacts
import org.json.JSONObject

/**
 * Traduz a resposta da API do Open Food Facts para o modelo do produto.
 *
 * Puro e separado do cliente HTTP de propósito: é aqui que mora a chance real de errar — unidade
 * trocada, campo ausente virando zero, alérgeno perdido — e nada disso precisa de rede para ser
 * testado.
 *
 * Duas regras atravessam o arquivo:
 *
 * 1. **campo ausente é ausente, nunca zero.** Um produto sem sódio declarado não tem "0 mg de
 *    sódio"; ele não tem informação de sódio, e o motor precisa saber a diferença;
 * 2. **os alérgenos declarados voltam para texto** e passam pelo mesmo `LabelParser` do rótulo
 *    fotografado, em vez de virarem um segundo caminho de afirmação que pode divergir do primeiro.
 */
object OpenFoodFactsMapper {

    const val SOURCE = "Open Food Facts"

    /**
     * Devolve nulo quando a base não conhece o produto ou não tem sequer um nome.
     *
     * Produto sem nome é registro incompleto na base colaborativa. Mostrar "produto sem nome" com
     * uma composição ao lado passaria uma confiança que o registro não sustenta.
     */
    fun map(json: String, ean: String): ProductBarcodeData? = runCatching {
        val root = JSONObject(json)
        if (root.optInt("status", 0) != 1) return null
        val product = root.optJSONObject("product") ?: return null

        val name = firstNonBlank(
            product.optString("product_name_pt"),
            product.optString("product_name")
        ) ?: return null

        ProductBarcodeData(
            ean = ean,
            productName = name,
            brand = firstNonBlank(product.optString("brands")).orEmpty(),
            ingredientsText = ingredientsText(product),
            nutrition = nutrition(product),
            source = SOURCE
        )
    }.getOrNull()

    /**
     * Junta a lista de ingredientes e os alérgenos declarados num texto no formato do rótulo
     * brasileiro, para que o `LabelParser` continue sendo o único lugar que interpreta afirmação.
     */
    private fun ingredientsText(product: JSONObject): String {
        val ingredients = firstNonBlank(
            product.optString("ingredients_text_pt"),
            product.optString("ingredients_text")
        )
        val allergens = tagNames(product, "allergens_tags")
        val traces = tagNames(product, "traces_tags")

        return buildList {
            ingredients?.let { add("INGREDIENTES: $it") }
            if (allergens.isNotEmpty()) add("ALÉRGICOS: CONTÉM ${allergens.joinToString(", ")}.")
            if (traces.isNotEmpty()) add("PODE CONTER ${traces.joinToString(", ")}.")
        }.joinToString(separator = " ")
    }

    /**
     * Tags vêm prefixadas por idioma, como `en:milk` ou `pt:leite`.
     *
     * O prefixo é descartado e o restante é traduzido pelo dicionário abaixo quando conhecido. Termo
     * desconhecido é mantido como veio: perder o alérgeno seria pior que exibi-lo em inglês.
     */
    private fun tagNames(product: JSONObject, field: String): List<String> {
        val array = product.optJSONArray(field) ?: return emptyList()
        return (0 until array.length())
            .mapNotNull { array.optString(it).takeIf(String::isNotBlank) }
            .map { tag -> tag.substringAfter(':').replace('-', ' ').trim() }
            .filter { it.isNotBlank() }
            .map { ALLERGEN_TRANSLATIONS[it.lowercase()] ?: it }
            .distinct()
    }

    /**
     * Nutrientes por 100 g/ml.
     *
     * O Open Food Facts publica sódio **em gramas**; o modelo do produto usa miligramas. Essa é a
     * conversão que, se passar despercebida, transforma 0,4 g de sódio em "0,4 mg" e faz um produto
     * salgado parecer inofensivo.
     */
    private fun nutrition(product: JSONObject): NutritionFacts {
        val nutriments = product.optJSONObject("nutriments") ?: return NutritionFacts()

        val amounts = NUTRIENT_FIELDS.mapNotNull { (nutrient, field) ->
            val value = numberOrNull(nutriments, "${field}_100g") ?: return@mapNotNull null
            NutrientAmount(
                nutrient = nutrient,
                value = if (nutrient == Nutrient.SODIUM) value * GRAMS_TO_MILLIGRAMS else value,
                basis = NutritionBasis.PER_100,
                sourceText = "$field por 100 g/ml · $SOURCE"
            )
        }

        if (amounts.isEmpty()) return NutritionFacts()

        return NutritionFacts(
            amounts = amounts,
            source = SOURCE,
            warnings = listOf(
                "Composição declarada pela base colaborativa Open Food Facts; confira a embalagem."
            )
        )
    }

    /** `optDouble` devolve `NaN` para ausente e `0.0` para string vazia; nenhum dos dois é um valor. */
    private fun numberOrNull(json: JSONObject, key: String): Double? {
        if (!json.has(key) || json.isNull(key)) return null
        val value = json.optDouble(key, Double.NaN)
        return value.takeIf { !it.isNaN() }
    }

    private fun firstNonBlank(vararg candidates: String?): String? =
        candidates.firstOrNull { !it.isNullOrBlank() && it != "null" }?.trim()

    private const val GRAMS_TO_MILLIGRAMS = 1000.0

    /** Campo do Open Food Facts, sem o sufixo de base. */
    private val NUTRIENT_FIELDS = listOf(
        Nutrient.ENERGY to "energy-kcal",
        Nutrient.CARBOHYDRATE to "carbohydrates",
        Nutrient.TOTAL_SUGARS to "sugars",
        Nutrient.ADDED_SUGARS to "added-sugars",
        Nutrient.PROTEIN to "proteins",
        Nutrient.TOTAL_FAT to "fat",
        Nutrient.SATURATED_FAT to "saturated-fat",
        Nutrient.TRANS_FAT to "trans-fat",
        Nutrient.FIBER to "fiber",
        Nutrient.SODIUM to "sodium"
    )

    private val ALLERGEN_TRANSLATIONS = mapOf(
        "milk" to "leite",
        "gluten" to "glúten",
        "soybeans" to "soja",
        "soya" to "soja",
        "eggs" to "ovo",
        "peanuts" to "amendoim",
        "nuts" to "castanhas",
        "fish" to "peixe",
        "crustaceans" to "crustáceos",
        "sesame seeds" to "gergelim",
        "molluscs" to "moluscos",
        "celery" to "aipo",
        "mustard" to "mostarda",
        "sulphur dioxide and sulphites" to "sulfitos"
    )
}
