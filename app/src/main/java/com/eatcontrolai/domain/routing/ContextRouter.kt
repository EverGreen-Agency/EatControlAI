package com.eatcontrolai.domain.routing

import com.eatcontrolai.domain.label.TextNormalizer
import com.eatcontrolai.domain.menu.MenuParser
import com.eatcontrolai.orchestration.AnalysisTrack

/**
 * Decide qual trilha usar a partir do que a captura entregou.
 *
 * É a **política** do loop descrito na palestra do Ideathon, implementada como cascata por custo:
 * cada estágio barato autoriza — ou dispensa — o seguinte.
 *
 * ```text
 * frame
 *   ├─ há código de barras?        leitor de barras, barato   → PRODUTO  (para aqui)
 *   ├─ há marcador de rotulagem?   OCR, custo médio           → RÓTULO   (para aqui)
 *   ├─ há linhas com preço?                                   → CARDÁPIO (para aqui)
 *   ├─ há texto denso?                                        → RÓTULO
 *   └─ pouco texto                 rotulagem visual, cara     → PRATO
 * ```
 *
 * Determinístico por escolha. A palestra chama o LLM de "especialista caro, não porteiro"; aqui o
 * porteiro não é nem um classificador — é marcador de texto e contagem de caracteres. Um modelo de
 * classificação de cena custaria memória e latência para resolver o que evidências baratas já
 * resolvem.
 *
 * **Precedência de rótulo sobre cardápio, de propósito.** Um cardápio brasileiro às vezes traz nota
 * de alérgeno; um rótulo nunca traz preço. Se o texto tem `INGREDIENTES`, `CONTÉM` ou `ALÉRGICOS`,
 * a trilha de rótulo vem primeiro mesmo havendo preços — perder a análise de alérgeno é o erro
 * perigoso; perder a estrutura de opções é só o erro chato.
 *
 * O roteador **não decide nada sobre alimento**. Ele escolhe qual ferramenta chamar; quem decide
 * continua sendo o motor determinístico com a hierarquia de evidência de `docs/SPEC.md`.
 */
object ContextRouter {

    /**
     * Abaixo disto, o que o OCR devolveu é ruído de embalagem — marca, slogan solto — e não texto
     * analisável. É heurística; `ContextRouterTest` a mantém honesta.
     */
    const val MIN_CHARS_FOR_LABEL = 40

    /** Uma linha com preço pode ser coincidência; duas já desenham um cardápio. */
    const val MIN_PRICED_LINES_FOR_MENU = 2

    private val labelMarkers = listOf(
        "INGREDIENTES", "CONTEM", "ALERGICOS", "PODE CONTER", "NAO CONTEM",
        "TABELA NUTRICIONAL", "INFORMACAO NUTRICIONAL", "VALOR ENERGETICO"
    )

    private val ingredientTerms = listOf(
        "FARINHA", "ACUCAR", "SAL", "OLEO", "GORDURA", "AROMA", "CONSERVANTE",
        "EMULSIFICANTE", "ACIDO", "AMIDO", "ESTABILIZANTE", "CORANTE"
    )

    sealed interface Decision {
        val track: AnalysisTrack?
        val reason: String

        data class Product(val ean: String, override val reason: String) : Decision {
            override val track = AnalysisTrack.BARCODE
        }

        data class Label(override val reason: String) : Decision {
            override val track = AnalysisTrack.LABEL
        }

        data class Menu(override val reason: String) : Decision {
            override val track = AnalysisTrack.MENU
        }

        data class Plate(override val reason: String) : Decision {
            override val track = AnalysisTrack.PLATE
        }

        /** Falta informação para escolher — normalmente porque o OCR ainda não rodou. */
        data class Unsupported(override val reason: String) : Decision {
            override val track: AnalysisTrack? = null
        }
    }

    /**
     * [ean] vem do leitor de código de barras (nulo quando não achou nada) e [recognizedText] do OCR.
     * Passar `null` em [recognizedText] significa "ainda não rodei o OCR" — o roteador então decide
     * só com o barcode, que é o estágio mais barato.
     */
    fun route(ean: String?, recognizedText: String?): Decision {
        if (!ean.isNullOrBlank()) {
            return Decision.Product(ean, "código de barras legível")
        }

        if (recognizedText == null) {
            return Decision.Unsupported("sem código de barras; falta rodar o OCR")
        }

        val normalized = TextNormalizer.normalize(recognizedText)

        labelMarkers.firstOrNull { it in normalized }?.let { marker ->
            return Decision.Label("rótulo reconhecido pelo marcador \"$marker\"")
        }

        val pricedLines = MenuParser.pricedLineCount(recognizedText)
        if (pricedLines >= MIN_PRICED_LINES_FOR_MENU) {
            return Decision.Menu("$pricedLines linhas terminam em preço")
        }

        val density = normalized.count { it.isLetterOrDigit() }
        val hasIngredientStructure = density >= MIN_CHARS_FOR_LABEL && (
            ingredientTerms.any { it in normalized } ||
            (recognizedText.contains(",") && density >= 60) ||
            density >= 140
        )
        if (hasIngredientStructure) {
            return Decision.Label("texto denso de ingredientes ($density caracteres)")
        }

        return Decision.Plate(
            "sem código de barras e sem marcadores de rótulo ($density caracteres): trata como alimento/prato"
        )
    }
}
