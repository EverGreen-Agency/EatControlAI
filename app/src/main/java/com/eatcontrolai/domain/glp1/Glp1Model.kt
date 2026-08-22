package com.eatcontrolai.domain.glp1

import com.eatcontrolai.core.model.EvidenceType
import com.eatcontrolai.core.model.Nutrient
import com.eatcontrolai.core.model.NutrientProgress
import com.eatcontrolai.core.model.NutritionFacts
import com.eatcontrolai.core.model.Restriction
import com.eatcontrolai.domain.plate.PlateFoodClass

/**
 * Modelo do rule pack GLP-1 (`docs/RULE_PACK_GLP1.md`), derivado do registro de validação
 * `VAL-GLP1-R1` em `docs/VALIDACAO_CLINICA_2026-08-18.md`.
 *
 * Três invariantes atravessam este arquivo inteiro:
 *
 * 1. nenhum achado existe sem origem declarada;
 * 2. o aplicativo nunca calcula meta clínica — ele compara com meta configurada;
 * 3. imagem sustenta presença de item visível, nunca ausência nem ingrediente oculto.
 */

/** Natureza do achado, usada para ordenar a resposta falada. */
enum class FindingKind { ATTENTION, POSITIVE, INFO }

/**
 * Achados possíveis da v1.
 *
 * [isRegulatory] marca os que derivam do critério de rotulagem frontal. Eles têm uma guarda extra:
 * só existem com composição declarada por 100 g/ml, jamais a partir de estimativa visual.
 */
enum class FindingCode(val kind: FindingKind, val isRegulatory: Boolean = false) {
    HIGH_IN_ADDED_SUGARS(FindingKind.ATTENTION, isRegulatory = true),
    HIGH_IN_SATURATED_FAT(FindingKind.ATTENTION, isRegulatory = true),
    HIGH_IN_SODIUM(FindingKind.ATTENTION, isRegulatory = true),
    PERSONAL_RULE_MATCH(FindingKind.ATTENTION),
    CONFIGURED_LIMIT_EXCEEDED(FindingKind.ATTENTION),
    GOAL_EXCEEDED(FindingKind.ATTENTION),
    PREPARATION_FRIED(FindingKind.ATTENTION),
    CREAMY_SAUCE(FindingKind.ATTENTION),
    PORTION_ABOVE_PLAN(FindingKind.ATTENTION),
    VEGETABLES_PRESENT(FindingKind.POSITIVE),
    PROTEIN_PRESENT(FindingKind.POSITIVE),
    GOAL_REMAINING(FindingKind.INFO)
}

/**
 * Um achado da avaliação.
 *
 * [audioLabel] é o fragmento curto usado na frase falada; [text] é a versão completa mostrada na
 * tela, com a origem explicada.
 */
data class Finding(
    val code: FindingCode,
    val text: String,
    val audioLabel: String,
    val origin: EvidenceType,
    val nutrient: Nutrient? = null
) {
    val kind: FindingKind get() = code.kind
}

/** Nível de encaminhamento aprovado na validação. */
enum class EscalationLevel { NONE, PROFESSIONAL, MEDICAL_EVALUATION }

/**
 * Sinais que o usuário registra explicitamente.
 *
 * Nenhum deles é inferido de imagem ou de histórico alimentar: sintoma é relato, não inferência.
 */
enum class SymptomKind(val level: EscalationLevel) {
    PERSISTENT_GI(EscalationLevel.PROFESSIONAL),
    RAPID_WEIGHT_LOSS(EscalationLevel.PROFESSIONAL),
    DIFFICULTY_EATING(EscalationLevel.PROFESSIONAL),
    MUSCLE_WEAKNESS(EscalationLevel.PROFESSIONAL),
    PREGNANCY(EscalationLevel.PROFESSIONAL),
    RELEVANT_COMORBIDITY(EscalationLevel.PROFESSIONAL),
    EATING_DISORDER_HISTORY(EscalationLevel.PROFESSIONAL),
    MEDICATION_DOUBT(EscalationLevel.PROFESSIONAL),
    SEVERE_ABDOMINAL_PAIN(EscalationLevel.MEDICAL_EVALUATION),
    REPEATED_VOMITING(EscalationLevel.MEDICAL_EVALUATION),
    CANNOT_KEEP_LIQUIDS(EscalationLevel.MEDICAL_EVALUATION),
    DEHYDRATION_SIGNS(EscalationLevel.MEDICAL_EVALUATION),
    ALLERGIC_REACTION(EscalationLevel.MEDICAL_EVALUATION)
}

enum class PersonalRuleAction { AVOID, OBSERVE }

enum class PersonalRuleOrigin(val label: String) {
    REPORTED_DISCOMFORT("desconforto relatado"),
    PREFERENCE("preferência cadastrada"),
    PROFESSIONAL_GUIDANCE("orientação profissional")
}

/**
 * Regra criada pelo próprio usuário, inclusive a partir de desconforto relatado.
 *
 * Ela evita ou observa um item; nunca cria meta clínica nem limite numérico. [possibleTerms] existe
 * para o caso em que a correspondência é apenas plausível — aí o app pergunta, não afirma.
 */
data class PersonalRule(
    val id: String,
    val target: String,
    val action: PersonalRuleAction,
    val origin: PersonalRuleOrigin,
    val note: String = "",
    val matchClasses: Set<PlateFoodClass> = emptySet(),
    val matchTerms: Set<String> = emptySet(),
    val possibleTerms: Set<String> = emptySet()
)

/**
 * Tudo que a avaliação precisa saber, já normalizado pelas camadas de percepção.
 *
 * [confirmedPortions] nulo significa quantidade desconhecida — e quantidade desconhecida nunca é
 * estimada a partir da foto.
 */
data class Glp1Context(
    val facts: NutritionFacts = NutritionFacts(),
    val confirmedPortions: Double? = null,
    val plannedPortions: Double? = null,
    val dailyProgress: List<NutrientProgress> = emptyList(),
    val confirmedComponents: Set<PlateFoodClass> = emptySet(),
    val observedTerms: Set<String> = emptySet(),
    val restrictions: Set<Restriction> = emptySet(),
    val personalRules: List<PersonalRule> = emptyList(),
    val symptomReports: List<SymptomKind> = emptyList(),
    val hasVisualInference: Boolean = false
)

data class Glp1Assessment(
    val packId: String,
    val underReview: Boolean,
    val findings: List<Finding>,
    val questions: List<String>,
    val escalation: EscalationLevel,
    val analysisSuppressed: Boolean,
    val shortMessage: String,
    val detail: List<String>
)

/** Rule pack local e versionado. A versão acompanha toda resposta que o usar. */
interface RulePack {
    val id: String

    /** `true` enquanto o registro formal da validação profissional não existir. */
    val underReview: Boolean

    fun evaluate(context: Glp1Context): Glp1Assessment
}
