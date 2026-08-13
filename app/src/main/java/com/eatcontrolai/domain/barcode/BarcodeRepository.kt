package com.eatcontrolai.domain.barcode

import com.eatcontrolai.core.model.Evidence
import com.eatcontrolai.core.model.EvidenceType
import com.eatcontrolai.core.model.LabelClaim
import com.eatcontrolai.domain.label.LabelParser

data class ProductBarcodeData(
    val ean: String,
    val productName: String,
    val brand: String,
    val ingredientsText: String
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
 * Catálogo local de produtos por código de barras.
 *
 * **Este é um catálogo de demonstração, não um espelho do Open Food Facts.** Os EANs e marcas são
 * fictícios e existem para tornar o fluxo de barcode demonstrável sem rede (NFR-001). A integração
 * real com Open Food Facts / TBCA está em `docs/DATA_SOURCES.md` e é trabalho seguinte — quando
 * existir, entra atrás desta mesma interface, com `source` apontando para a base de verdade.
 */
class BarcodeRepository {

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

    fun findByEan(ean: String): ProductBarcodeData? = catalog[ean.trim()]

    fun all(): List<ProductBarcodeData> = catalog.values.toList()

    fun toEvidence(data: ProductBarcodeData): Evidence = Evidence(
        type = EvidenceType.BARCODE_DATABASE,
        value = "${data.productName} — ${data.brand}",
        source = "catálogo local de demonstração (EAN ${data.ean})",
        claims = data.claims
    )
}
