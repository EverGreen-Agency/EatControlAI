package com.eatcontrolai.glasses

/**
 * Uma cena que o mock "enxerga".
 *
 * O texto é renderizado como uma embalagem real e passa pelo **OCR de verdade** — o mock substitui
 * o hardware, não a inteligência. Isso mantém honesto o que a demo prova: se o ML Kit errar a
 * leitura, a demo erra junto, como erraria com os óculos.
 */
data class MockScene(
    val id: String,
    val title: String,
    val subtitle: String,
    val labelText: String
)

/**
 * Cenários de demonstração, escritos no formato de rotulagem brasileira (RDC 26/2015).
 *
 * Cada um existe para exercitar um estado de decisão diferente — inclusive os desconfortáveis.
 */
object MockScenes {

    val all: List<MockScene> = listOf(
        MockScene(
            id = "biscoito_recheado",
            title = "Biscoito recheado",
            subtitle = "declaração explícita de leite",
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
            labelText = """
                BEBIDA VEGETAL DE AVEIA
                INGREDIENTES: ÁGUA, AVEIA INTEGRAL (12%), ÓLEO DE GIRASSOL E SAL MARINHO.
                ALÉRGICOS: CONTÉM AVEIA. NÃO CONTÉM LEITE.
            """.trimIndent()
        ),
        MockScene(
            id = "barra_proteina",
            title = "Barra de proteína",
            subtitle = "contaminação cruzada possível",
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
            labelText = """
                NOVO!
                LEVE 3 PAGUE 2
                SABOR IRRESISTÍVEL
            """.trimIndent()
        )
    )

    val default: MockScene get() = all.first()
}
