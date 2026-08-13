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
enum class EvidenceType(val rank: Int, val label: String) {
    /** Orientação explícita de médico/nutricionista cadastrada no perfil. */
    PROFESSIONAL_RULE(1, "Orientação profissional"),

    /** Declaração explícita do fabricante já interpretada ("ALÉRGICOS: CONTÉM LEITE"). */
    DECLARED_LABEL(2, "Declarado no rótulo"),

    /** Composição vinda de base estruturada confiável (Open Food Facts, TBCA, USDA). */
    BARCODE_DATABASE(3, "Base de produto"),

    /** Resposta do próprio usuário a uma pergunta de confirmação. */
    USER_CONFIRMATION(4, "Confirmado por você"),

    /** Texto bruto reconhecido, ainda não interpretado. */
    OCR_TEXT(5, "Texto reconhecido"),

    /** Saída de modelo de visão. Probabilística por definição. */
    VISUAL_INFERENCE(6, "Inferência visual");

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
 * Quanto pesa uma restrição na decisão.
 *
 * Não é enfeite de UI: muda o comportamento do motor. Uma preferência não resolvida não trava a
 * refeição inteira; uma restrição crítica trava.
 */
enum class RestrictionSeverity(val label: String) {
    /** Alergia, doença celíaca, orientação profissional firme. Nunca relaxa. */
    CRITICAL("Alta prioridade"),

    /** Intolerância, desconforto conhecido, meta comportamental firme. */
    MODERATE("Atenção"),

    /** Gosto pessoal. Informa, não bloqueia. */
    PREFERENCE("Preferência")
}

/** O que fazer quando a evidência não resolve a restrição. */
enum class UncertaintyPolicy(val label: String) {
    ASK_CONFIRMATION("Pedir confirmação / declarar incerteza"),
    INFORM_ONLY("Apenas informar"),
    IGNORE_VISUAL("Não usar em análise visual")
}

data class Restriction(
    val allergen: Allergen,
    val severity: RestrictionSeverity = RestrictionSeverity.CRITICAL,
    val uncertaintyPolicy: UncertaintyPolicy = UncertaintyPolicy.ASK_CONFIRMATION
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
    val restrictions: Set<Restriction> = emptySet(),
    /** Restrições em texto livre, ainda não cobertas por regra determinística. */
    val freeTextRestrictions: Set<String> = emptySet(),
    val goals: Set<String> = emptySet(),
    /** Orientações declaradas por profissional de saúde, exibidas na tela Meu plano. */
    val guidelines: List<Guideline> = emptyList(),
    val usesGlp1: Boolean = false
) {
    fun restrictionFor(allergen: Allergen): Restriction? =
        restrictions.firstOrNull { it.allergen == allergen }
}

data class Guideline(
    val title: String,
    val detail: String
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
    val confidence: Float? = null,
    /** Alérgenos que ficaram sem resposta — alimentam a pergunta de confirmação. */
    val unresolved: List<Allergen> = emptyList()
)

/** Configurações de privacidade. Padrões conservadores por decisão (NFR-005). */
data class PrivacySettings(
    val savePhotos: Boolean = false,
    val shareForImprovement: Boolean = false,
    val syncHistory: Boolean = false
)

/** Um registro no histórico. Guarda a decisão, não a imagem (a menos que [PrivacySettings]). */
data class MealRecord(
    val id: String,
    val timestampMillis: Long,
    val title: String,
    val decisionState: DecisionState,
    val shortMessage: String,
    val recognizedText: String,
    val evidenceLabels: List<String>,
    val endToEndMs: Long,
    val userConfirmed: Boolean = false
)
