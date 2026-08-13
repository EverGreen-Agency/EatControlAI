package com.eatcontrolai.domain.evidence

import com.eatcontrolai.core.model.Evidence
import com.eatcontrolai.core.model.EvidenceType
import com.eatcontrolai.domain.label.LabelParser

/**
 * Normaliza percepção em evidência (camada Evidence de `docs/SDD.md`).
 *
 * Mora fora do orquestrador de propósito: é Kotlin puro, sem Android, e é exatamente o mesmo código
 * que os testes de `benchmark/decision_scenarios.csv` exercitam. Se o teste passasse por um caminho
 * diferente do app, o Decision Success Rate não valeria nada.
 */
object EvidenceBuilder {

    /**
     * O texto bruto entra como [EvidenceType.OCR_TEXT] (rank 5). Quando o parser reconhece uma
     * declaração explícita do fabricante, o resultado é **promovido** a
     * [EvidenceType.DECLARED_LABEL] (rank 2) — é essa promoção que faz a hierarquia de
     * `docs/SPEC.md` funcionar sem nenhum caso especial dentro do motor de decisão.
     */
    fun fromLabelOcr(
        recognizedText: String,
        providerId: String,
        modelVersion: String? = null
    ): List<Evidence> {
        if (recognizedText.isBlank()) return emptyList()

        val raw = Evidence(
            type = EvidenceType.OCR_TEXT,
            value = recognizedText,
            source = providerId,
            modelVersion = modelVersion
        )

        val claims = LabelParser.parse(recognizedText)
        if (claims.isEmpty()) return listOf(raw)

        val declared = Evidence(
            type = EvidenceType.DECLARED_LABEL,
            value = claims.joinToString("; ") { "${it.polarity} ${it.allergen.displayName}" },
            source = PARSER_ID,
            claims = claims
        )
        return listOf(declared, raw)
    }

    const val PARSER_ID = "label_parser_v1"
}
