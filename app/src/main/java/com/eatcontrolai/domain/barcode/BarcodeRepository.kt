package com.eatcontrolai.domain.barcode

import com.eatcontrolai.core.model.Evidence
import com.eatcontrolai.core.model.EvidenceType
import com.eatcontrolai.core.model.LabelClaim
import com.eatcontrolai.core.model.NutritionFacts
import com.eatcontrolai.domain.label.LabelParser

data class ProductBarcodeData(
    val ean: String,
    val productName: String,
    val brand: String,
    val ingredientsText: String,
    /** Composição declarada, quando a base traz. Vazia não significa ausência: significa não sei. */
    val nutrition: NutritionFacts = NutritionFacts(),
    /** De onde veio. Aparece na evidência, porque base de verdade e demonstração não são a mesma coisa. */
    val source: String = "catálogo local de demonstração"
) {
    /**
     * As afirmações vêm do **mesmo** [LabelParser] que interpreta rótulo fotografado.
     *
     * Escrevê-las à mão ao lado do texto abriria espaço para divergência: o catálogo diria uma coisa
     * e o parser outra, sobre exatamente o mesmo produto.
     */
    val claims: List<LabelClaim> get() = LabelParser.parse(ingredientsText)
}

/**
 * Uma base de produtos consultável por código de barras.
 *
 * `suspend` porque a implementação real vai à rede; nulo significa "não encontrei este produto",
 * nunca "este produto não tem nada". A diferença importa: composição desconhecida leva o motor a
 * `INFORMAÇÃO_INSUFICIENTE`, não a `COMPATÍVEL`.
 */
interface ProductDataSource {
    suspend fun lookup(ean: String): ProductBarcodeData?
}

/**
 * Catálogo de produtos por código de barras.
 *
 * São duas camadas com papéis diferentes. O catálogo local tem quatro EANs fictícios e existe para
 * a demonstração funcionar sem rede (NFR-001) — é ele que sustenta as cenas do mock e o gabarito do
 * benchmark. A base remota é a verdade para produto real, e por isso o `source` da evidência diz
 * qual das duas respondeu: apresentar demonstração como base de verdade seria exatamente o tipo de
 * falsa certeza que o produto existe para evitar.
 *
 * O local é consultado primeiro porque só ele conhece os EANs de demonstração, e porque responder
 * sem rede é mais rápido do que responder com.
 */
class BarcodeRepository(private val remote: ProductDataSource? = null) {

    private val remoteCache = mutableMapOf<String, ProductBarcodeData?>()

    private val catalog = listOf(
        ProductBarcodeData(
            ean = "7891000100103",
            productName = "Iogurte Natural Integral",
            brand = "Marca Demo",
            ingredientsText = "INGREDIENTES: LEITE INTEGRAL E FERMENTO LÁCTEO. " +
                "ALÉRGICOS: CONTÉM LEITE."
        ),
        ProductBarcodeData(
            ean = "7891000200100",
            productName = "Iogurte Zero Lactose",
            brand = "Marca Demo",
            ingredientsText = "IOGURTE ZERO LACTOSE. INGREDIENTES: LEITE DESNATADO, ENZIMA LACTASE " +
                "E FERMENTO LÁCTEO. ALÉRGICOS: CONTÉM LEITE."
        ),
        ProductBarcodeData(
            ean = "7891000300107",
            productName = "Bebida de Amêndoa Sem Açúcar",
            brand = "Marca Veg",
            ingredientsText = "INGREDIENTES: ÁGUA, AMÊNDOAS, CÁLCIO E SAL MARINHO. " +
                "ALÉRGICOS: CONTÉM AMÊNDOA. NÃO CONTÉM LEITE."
        ),
        ProductBarcodeData(
            ean = "7891000400104",
            productName = "Biscoito Integral",
            brand = "Marca Demo",
            ingredientsText = "INGREDIENTES: FARINHA DE TRIGO INTEGRAL, AÇÚCAR E GORDURA VEGETAL. " +
                "ALÉRGICOS: CONTÉM TRIGO. PODE CONTER LEITE, SOJA E OVO."
        )
    ).associateBy { it.ean }

    /** Só o catálogo de demonstração. Sem rede, sem suspensão. */
    fun findByEan(ean: String): ProductBarcodeData? = catalog[ean.trim()]

    /**
     * Demonstração primeiro, base remota depois.
     *
     * Falha de rede devolve nulo em vez de propagar: sem internet, o app cai para o OCR do rótulo,
     * que é um caminho pior porém honesto. Derrubar a análise inteira porque a consulta falhou
     * seria transformar indisponibilidade de rede em erro do usuário.
     */
    suspend fun lookup(ean: String): ProductBarcodeData? {
        val normalized = ean.trim()
        findByEan(normalized)?.let { return it }
        val source = remote ?: return null
        if (remoteCache.containsKey(normalized)) return remoteCache[normalized]

        val found = runCatching { source.lookup(normalized) }.getOrNull()
        remoteCache[normalized] = found
        return found
    }

    fun all(): List<ProductBarcodeData> = catalog.values.toList()

    fun toEvidence(data: ProductBarcodeData): Evidence = Evidence(
        type = EvidenceType.BARCODE_DATABASE,
        value = "${data.productName} — ${data.brand}",
        source = "${data.source} (EAN ${data.ean})",
        claims = data.claims
    )
}
