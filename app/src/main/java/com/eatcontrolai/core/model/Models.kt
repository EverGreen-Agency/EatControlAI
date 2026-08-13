package com.eatcontrolai.core.model

/**
 * Modelo de domínio do Eat Control.
 *
 * Implementa `docs/SPEC.md`. Qualquer mudança aqui deve ser refletida lá — e vice-versa.
 */

/** Estados de decisão definidos em `docs/SPEC.md`. */
enum class DecisionState {
    COMPATIBLE,
    INCOMPATIBLE,
    NEEDS_CONFIRMATION,
    INSUFFICIENT_INFORMATION
}

/**
 * Tipos de evidência, com a precedência de `docs/SPEC.md` e `contexto-gpt.md` §17.
 * Menor [rank] = mais confiável.
 */
enum class EvidenceType(val rank: Int) {
    /** Orientação explícita de médico/nutricionista cadastrada no perfil. */
    PROFESSIONAL_RULE(1),

    /** Declaração explícita do fabricante já interpretada ("ALÉRGICOS: CONTÉM LEITE"). */
    DECLARED_LABEL(2),

    /** Composição vinda de base estruturada confiável (Open Food Facts, TBCA, USDA). */
    BARCODE_DATABASE(3),

    /** Resposta do próprio usuário a uma pergunta de confirmação. */
    USER_CONFIRMATION(4),

    /** Texto bruto reconhecido, ainda não interpretado. */
    OCR_TEXT(5),

    /** Saída de modelo de visão. Probabilística por definição. */
    VISUAL_INFERENCE(6);

    /**
     * NFR-003 / `docs/SPEC.md`: inferência probabilística isolada nunca pode sustentar
     * uma afirmação de segurança.
     */
    val isProbabilistic: Boolean get() = this == VISUAL_INFERENCE
}

/** Alérgenos de declaração obrigatória (base: RDC 26/2015 da Anvisa). */
enum class Allergen(val displayName: String) {
    MILK("leite"),
    GLUTEN("glúten"),
    SOY("soja"),
    EGG("ovo"),
    PEANUT("amendoim"),
    TREE_NUTS("castanhas"),
    FISH("peixe"),
    CRUSTACEANS("crustáceos"),
    SESAME("gergelim")
}

/** O que o rótulo afirma sobre um alérgeno. */
enum class ClaimPolarity {
    /** "CONTÉM LEITE", ou o alérgeno aparece na lista de ingredientes. */
    CONTAINS,

    /** "PODE CONTER", "TRAÇOS DE" — contaminação cruzada possível. */
    MAY_CONTAIN,

    /** "NÃO CONTÉM", "ZERO LACTOSE", "SEM GLÚTEN". */
    FREE_OF
}

/** Uma afirmação extraída de um rótulo, com o trecho original que a originou. */
data class LabelClaim(
    val allergen: Allergen,
    val polarity: ClaimPolarity,
    val sourceText: String
)

/**
 * Unidade comum de evidência (`docs/SDD.md`, camada Evidence).
 * Toda percepção — OCR, barcode, visão, voz — é normalizada para este formato.
 */
data class Evidence(
    val type: EvidenceType,
    val value: String,
    val source: String,
    val confidence: Float? = null,
    val modelVersion: String? = null,
    val claims: List<LabelClaim> = emptyList()
)

data class UserProfile(
    val id: String,
    val displayName: String = "",
    /** Restrições que o motor determinístico sabe avaliar. */
    val restrictions: Set<Allergen> = emptySet(),
    /** Restrições em texto livre, ainda não cobertas por regra determinística. */
    val freeTextRestrictions: Set<String> = emptySet(),
    val goals: Set<String> = emptySet()
)

/** Por que o sistema chegou ao estado que chegou. Alimenta a UI e a auditoria. */
data class DecisionReason(
    val text: String,
    val evidenceType: EvidenceType,
    val allergen: Allergen? = null
)

data class Decision(
    val state: DecisionState,
    /** Resposta curta, pensada para TTS (`contexto-gpt.md` §21). */
    val shortMessage: String,
    val evidence: List<Evidence>,
    val reasons: List<DecisionReason> = emptyList(),
    val confidence: Float? = null
)
