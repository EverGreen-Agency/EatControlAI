package com.eatcontrolai.glasses

import com.eatcontrolai.core.model.DecisionState

enum class SceneKind { LABEL, BARCODE, MENU, PLATE }

/**
 * Uma cena que o mock "enxerga".
 *
 * A imagem é renderizada de verdade e passa pelo **OCR e pelo leitor de barras reais** — o mock
 * substitui o hardware, não a inteligência. Se o ML Kit errar a leitura, a demo erra junto, como
 * erraria com os óculos.
 */
data class MockScene(
    val id: String,
    val title: String,
    val subtitle: String,
    val kind: SceneKind,
    /** Texto da embalagem, usado nas cenas de rótulo. */
    val labelText: String = "",
    /** EAN-13 válido, usado nas cenas de código de barras. */
    val ean: String? = null,
    val productName: String = "",
    val brand: String = "",
    /** Estado esperado para o perfil de demonstração (restrição crítica a leite). */
    val expectedForMilkProfile: DecisionState
)

/**
 * Cenários de demonstração no formato de rotulagem brasileira (RDC 26/2015).
 *
 * Cada um exercita um estado de decisão diferente — inclusive os desconfortáveis. O campo
 * [MockScene.expectedForMilkProfile] transforma esta lista no gabarito do benchmark: um modelo que
 * lê mal produz decisão errada, e isso aparece como número.
 */
object MockScenes {

    val labels: List<MockScene> = listOf(
        MockScene(
            id = "biscoito_recheado",
            title = "Biscoito recheado",
            subtitle = "declaração explícita de leite",
            kind = SceneKind.LABEL,
            expectedForMilkProfile = DecisionState.INCOMPATIBLE,
            labelText = """
                BISCOITO RECHEADO SABOR CHOCOLATE
                INGREDIENTES: FARINHA DE TRIGO ENRIQUECIDA COM FERRO E ÁCIDO FÓLICO,
                AÇÚCAR, GORDURA VEGETAL, LEITE EM PÓ INTEGRAL, CACAU EM PÓ, SAL,
                FERMENTOS QUÍMICOS E AROMATIZANTE.
                ALÉRGICOS: CONTÉM TRIGO, LEITE E SOJA.
                PODE CONTER AMENDOIM E CASTANHAS.
            """.trimIndent()
        ),
        MockScene(
            id = "iogurte_zero_lactose",
            title = "Iogurte \"zero lactose\"",
            subtitle = "marketing na frente, leite no verso",
            kind = SceneKind.LABEL,
            expectedForMilkProfile = DecisionState.INCOMPATIBLE,
            labelText = """
                IOGURTE INTEGRAL ZERO LACTOSE
                INGREDIENTES: LEITE DESNATADO, PREPARADO DE MORANGO, FERMENTO LÁCTEO,
                ENZIMA LACTASE E ESPESSANTE.
                ALÉRGICOS: CONTÉM LEITE.
            """.trimIndent()
        ),
        MockScene(
            id = "bebida_aveia",
            title = "Bebida vegetal de aveia",
            subtitle = "ausência declarada explicitamente",
            kind = SceneKind.LABEL,
            expectedForMilkProfile = DecisionState.COMPATIBLE,
            labelText = """
                BEBIDA VEGETAL DE AVEIA
                INGREDIENTES: ÁGUA, AVEIA INTEGRAL, ÓLEO DE GIRASSOL E SAL MARINHO.
                ALÉRGICOS: CONTÉM AVEIA. NÃO CONTÉM LEITE.
            """.trimIndent()
        ),
        MockScene(
            id = "barra_proteina",
            title = "Barra de proteína",
            subtitle = "contaminação cruzada possível",
            kind = SceneKind.LABEL,
            expectedForMilkProfile = DecisionState.NEEDS_CONFIRMATION,
            labelText = """
                BARRA DE PROTEÍNA VEGETAL
                INGREDIENTES: PROTEÍNA ISOLADA DE ERVILHA, TÂMARAS, CACAU E ÓLEO DE COCO.
                ALÉRGICOS: PODE CONTER LEITE, SOJA E CASTANHAS.
            """.trimIndent()
        ),
        MockScene(
            id = "embalagem_promocional",
            title = "Frente da embalagem",
            subtitle = "sem informação útil",
            kind = SceneKind.LABEL,
            expectedForMilkProfile = DecisionState.INSUFFICIENT_INFORMATION,
            labelText = """
                NOVO!
                LEVE 3 PAGUE 2
                SABOR IRRESISTÍVEL
            """.trimIndent()
        )
    )

    /** EANs válidos (dígito verificador conferido) que existem no catálogo local. */
    val barcodes: List<MockScene> = listOf(
        MockScene(
            id = "ean_iogurte_natural",
            title = "EAN · Iogurte natural",
            subtitle = "produto no catálogo, declara leite",
            kind = SceneKind.BARCODE,
            ean = "7891000100103",
            productName = "Iogurte Natural Integral",
            brand = "Marca Demo",
            expectedForMilkProfile = DecisionState.INCOMPATIBLE
        ),
        MockScene(
            id = "ean_bebida_amendoa",
            title = "EAN · Bebida de amêndoa",
            subtitle = "ausência de leite declarada",
            kind = SceneKind.BARCODE,
            ean = "7891000300107",
            productName = "Bebida de Amêndoa Sem Açúcar",
            brand = "Marca Veg",
            expectedForMilkProfile = DecisionState.COMPATIBLE
        ),
        MockScene(
            id = "ean_biscoito_integral",
            title = "EAN · Biscoito integral",
            subtitle = "traços de leite",
            kind = SceneKind.BARCODE,
            ean = "7891000400104",
            productName = "Biscoito Integral",
            brand = "Marca Demo",
            expectedForMilkProfile = DecisionState.NEEDS_CONFIRMATION
        ),
        MockScene(
            id = "ean_desconhecido",
            title = "EAN · fora do catálogo",
            subtitle = "produto não encontrado",
            kind = SceneKind.BARCODE,
            ean = "7899999999999",
            productName = "Produto Desconhecido",
            brand = "Sem marca",
            expectedForMilkProfile = DecisionState.INSUFFICIENT_INFORMATION
        )
    )

    val menus: List<MockScene> = listOf(
        MockScene(
            id = "menu_bistro",
            title = "Cardápio de bistrô",
            subtitle = "OCR real, opções e preços revisáveis",
            kind = SceneKind.MENU,
            labelText = """
                CARDÁPIO
                PRATOS PRINCIPAIS
                FRANGO GRELHADO ........ R$ 42,00
                Arroz, feijão e salada da casa
                MASSA AO MOLHO CREMOSO .. R$ 38,00
                Penne, molho cremoso e queijo
                PEIXE ASSADO ............ R$ 49,00
                Legumes e arroz
                SOBREMESAS
                BOLO DE CHOCOLATE ....... R$ 18,00
            """.trimIndent(),
            expectedForMilkProfile = DecisionState.INSUFFICIENT_INFORMATION
        )
    )

    val plates: List<MockScene> = listOf(
        MockScene(
            id = "prato_brasileiro",
            title = "Prato brasileiro sintético",
            subtitle = "candidatos visuais; componentes exigem confirmação",
            kind = SceneKind.PLATE,
            expectedForMilkProfile = DecisionState.INSUFFICIENT_INFORMATION
        )
    )

    /**
     * O modo automático enxerga tudo: a cascata é que decide a trilha. Restringir as cenas aqui
     * esconderia justamente o comportamento que o modo existe para demonstrar.
     */
    val automatic: List<MockScene> = labels + barcodes + menus + plates

    val all: List<MockScene> = automatic

    val default: MockScene get() = labels.first()

    fun forKind(kind: SceneKind): List<MockScene> = all.filter { it.kind == kind }
}
