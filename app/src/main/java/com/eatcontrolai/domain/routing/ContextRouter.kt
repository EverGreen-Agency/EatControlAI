package com.eatcontrolai.domain.routing

import com.eatcontrolai.domain.label.TextNormalizer
import com.eatcontrolai.orchestration.AnalysisTrack

/**
 * Decide qual trilha usar a partir do que a captura entregou.
 *
 * É a **política** do loop descrito na palestra do Ideathon, implementada como cascata por custo:
 * cada estágio barato autoriza — ou dispensa — o seguinte.
 *
 * ```text
 * frame
 *   ├─ há código de barras?   barcode scan, barato   → TRILHA PRODUTO (para aqui)
 *   ├─ há texto denso?        OCR, custo médio       → TRILHA RÓTULO  (para aqui)
 *   └─ nada disso                                    → PRATO (não implementado)
 * ```
 *
 * Determinístico por escolha. A palestra chama o LLM de "especialista caro, não porteiro"; aqui o
 * porteiro não é nem um classificador — é contagem de caracteres. Um modelo de classificação de cena
 * custaria memória e latência para resolver algo que duas evidências baratas já resolvem.
 *
 * O roteador **não decide nada sobre alimento**. Ele só escolhe qual ferramenta chamar; quem decide
 * continua sendo o motor determinístico com a hierarquia de evidência de `docs/SPEC.md`.
 */
object ContextRouter {

    /**
     * Abaixo disto, o que o OCR devolveu é ruído de embalagem — nome de marca, slogan solto — e não
     * uma lista de ingredientes. É heurística; `ContextRouterTest` a mantém honesta.
     */
    const val MIN_CHARS_FOR_LABEL = 40

    /** Marcadores de rotulagem que valem mais que o tamanho do texto. */
    private val labelMarkers = listOf("INGREDIENTES", "CONTEM", "ALERGICOS", "PODE CONTER", "NAO CONTEM")

    sealed interface Decision {
        val track: AnalysisTrack?
        val reason: String

        data class Product(val ean: String, override val reason: String) : Decision {
            override val track = AnalysisTrack.BARCODE
        }

        data class Label(override val reason: String) : Decision {
            override val track = AnalysisTrack.LABEL
        }

        /** Nenhuma trilha implementada dá conta. A UI informa em vez de arriscar. */
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
        val marker = labelMarkers.firstOrNull { it in normalized }
        if (marker != null) {
            return Decision.Label("rótulo reconhecido pelo marcador \"$marker\"")
        }

        val density = normalized.count { it.isLetterOrDigit() }
        if (density >= MIN_CHARS_FOR_LABEL) {
            return Decision.Label("texto denso o suficiente ($density caracteres)")
        }

        return Decision.Unsupported(
            "sem código de barras e sem texto de rótulo ($density caracteres). " +
                "Provavelmente é um prato — trilha ainda não implementada."
        )
    }
}
