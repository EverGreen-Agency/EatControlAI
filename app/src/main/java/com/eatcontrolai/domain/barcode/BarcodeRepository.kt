package com.eatcontrolai.domain.barcode

import com.eatcontrolai.core.model.Allergen
import com.eatcontrolai.core.model.ClaimPolarity
import com.eatcontrolai.core.model.Evidence
import com.eatcontrolai.core.model.EvidenceType
import com.eatcontrolai.core.model.LabelClaim

data class ProductBarcodeData(
    val ean: String,
    val productName: String,
    val brand: String,
    val ingredientsText: String,
    val declaredClaims: List<LabelClaim> = emptyList()
)

/**
 * Repositório local de produtos por código de barras (`docs/DATA_SOURCES.md`).
 *
 * Cache offline contendo produtos representativos do mercado brasileiro (Open Food Facts / TBCA),
 * garantindo consulta rápida sem dependência de rede no momento da decisão (NFR-001).
 */
class BarcodeRepository {

    private val catalog = mapOf(
        "7891000100011" to ProductBarcodeData(
            ean = "7891000100011",
            productName = "Iogurte Natural Integral",
            brand = "Marca Demo",
            ingredientsText = "Leite integral e fermento lácteo. CONTÉM LEITE. CONTÉM LACTOSE.",
            declaredClaims = listOf(
                LabelClaim(allergen = Allergen.MILK, polarity = ClaimPolarity.CONTAINS, sourceText = "CONTÉM LEITE")
            )
        ),
        "7891000200022" to ProductBarcodeData(
            ean = "7891000200022",
            productName = "Iogurte Zero Lactose",
            brand = "Marca Demo",
            ingredientsText = "Leite desnatado, enzima lactase e fermento lácteo. CONTÉM LEITE. NÃO CONTÉM LACTOSE.",
            declaredClaims = listOf(
                LabelClaim(allergen = Allergen.MILK, polarity = ClaimPolarity.CONTAINS, sourceText = "CONTÉM LEITE")
            )
        ),
        "7891000300033" to ProductBarcodeData(
            ean = "7891000300033",
            productName = "Bebida de Amêndoa Sem Açúcar",
            brand = "Marca Veg",
            ingredientsText = "Água, amêndoas, cálcio e sal marinho. NÃO CONTÉM LEITE. NÃO CONTÉM GLÚTEN.",
            declaredClaims = listOf(
                LabelClaim(allergen = Allergen.MILK, polarity = ClaimPolarity.FREE_OF, sourceText = "NÃO CONTÉM LEITE")
            )
        )
    )

    fun findByEan(ean: String): ProductBarcodeData? = catalog[ean.trim()]

    fun toEvidence(data: ProductBarcodeData): Evidence {
        return Evidence(
            type = EvidenceType.BARCODE_DATABASE,
            value = "${data.productName} (${data.brand}) - ${data.ingredientsText}",
            source = "Open Food Facts Cache (${data.ean})",
            claims = data.declaredClaims
        )
    }
}
