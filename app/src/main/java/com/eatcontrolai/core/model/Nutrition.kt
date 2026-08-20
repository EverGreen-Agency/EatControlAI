package com.eatcontrolai.core.model

/**
 * Modelo da camada nutricional (etapa N1 de `docs/INVENTARIO_ENTREGA_2026-08-22.md` §4.4).
 *
 * A fronteira desta camada é deliberada: aqui só existe **aritmética verificável** sobre o que está
 * escrito na embalagem. Nada aqui classifica alimento como bom ou ruim, recomenda troca ou define
 * meta. Definição de meta é do usuário ou do profissional de saúde; recomendação é a etapa N3 e
 * depende de rule pack validado.
 */

/** Nutrientes que a tabela nutricional brasileira declara (base: IN 75/2020 e RDC 429/2020). */
enum class Nutrient(
    val displayName: String,
    val unit: NutrientUnit,
    /** `true` para os campos usados no resumo principal do usuário. */
    val isCore: Boolean = false
) {
    ENERGY("valor energético", NutrientUnit.KCAL, isCore = true),
    CARBOHYDRATE("carboidratos", NutrientUnit.GRAM, isCore = true),
    TOTAL_SUGARS("açúcares totais", NutrientUnit.GRAM),
    ADDED_SUGARS("açúcares adicionados", NutrientUnit.GRAM),
    PROTEIN("proteínas", NutrientUnit.GRAM, isCore = true),
    TOTAL_FAT("gorduras totais", NutrientUnit.GRAM, isCore = true),
    SATURATED_FAT("gorduras saturadas", NutrientUnit.GRAM),
    TRANS_FAT("gorduras trans", NutrientUnit.GRAM),
    FIBER("fibra alimentar", NutrientUnit.GRAM),
    SODIUM("sódio", NutrientUnit.MILLIGRAM)
}

enum class NutrientUnit(val symbol: String) {
    KCAL("kcal"),
    GRAM("g"),
    MILLIGRAM("mg")
}

/**
 * A que quantidade o número declarado se refere.
 *
 * [UNKNOWN] existe porque tabela mal lida é o caso comum, não a exceção: sem saber a base, o número
 * não pode entrar em nenhuma soma. Preferimos declarar insuficiência a inventar a base.
 */
enum class NutritionBasis {
    PER_100,
    PER_PORTION,
    UNKNOWN
}

/** Unidade em que a porção é declarada. Sólido em gramas, líquido em mililitros. */
enum class PortionUnit(val symbol: String) {
    GRAM("g"),
    MILLILITER("ml")
}

data class PortionInfo(
    val amount: Double,
    val unit: PortionUnit,
    /** Texto original, ex.: "porção de 30 g (2 unidades)". */
    val sourceText: String
)

data class NutrientAmount(
    val nutrient: Nutrient,
    val value: Double,
    val basis: NutritionBasis,
    /** Trecho do rótulo que originou o número, para auditoria na UI. */
    val sourceText: String = ""
) {
    val unit: NutrientUnit get() = nutrient.unit
}

/**
 * Tabela nutricional lida de um rótulo ou vinda de base estruturada.
 *
 * [warnings] não é log: é o que a UI mostra ao usuário para justificar por que algum campo não pode
 * ser usado.
 */
data class NutritionFacts(
    val amounts: List<NutrientAmount> = emptyList(),
    val portion: PortionInfo? = null,
    val source: String = "",
    val warnings: List<String> = emptyList()
) {
    val isEmpty: Boolean get() = amounts.isEmpty()

    /** Campos utilizáveis em conta: base conhecida. */
    val usableAmounts: List<NutrientAmount>
        get() = amounts.filter { it.basis != NutritionBasis.UNKNOWN }

    fun amountOf(nutrient: Nutrient, basis: NutritionBasis): NutrientAmount? =
        amounts.firstOrNull { it.nutrient == nutrient && it.basis == basis }

    /**
     * Confiança explícita e reproduzível: proporção dos nutrientes centrais efetivamente lidos com
     * base conhecida, reduzida quando a porção não foi identificada.
     *
     * Não é probabilidade de modelo. É cobertura de leitura, e está documentada como tal.
     */
    val readingCoverage: Float
        get() {
            val core = Nutrient.entries.filter { it.isCore }
            if (core.isEmpty()) return 0f
            val found = core.count { nutrient ->
                usableAmounts.any { it.nutrient == nutrient }
            }
            val base = found.toFloat() / core.size
            return if (portion == null) base * PORTION_UNKNOWN_PENALTY else base
        }

    private companion object {
        const val PORTION_UNKNOWN_PENALTY = 0.75f
    }
}

/**
 * Metas diárias configuradas.
 *
 * Todos os campos são opcionais e nulos por padrão: o aplicativo **não** define meta por conta
 * própria. Sem meta configurada, a camada nutricional apenas informa o consumo.
 */
data class MacroGoals(
    val energyKcal: Double? = null,
    val proteinG: Double? = null,
    val carbohydrateG: Double? = null,
    val fatG: Double? = null,
    val fiberG: Double? = null,
    val sodiumMg: Double? = null,
    /** Quem definiu as metas. Aparece na UI para deixar a responsabilidade explícita. */
    val definedBy: GoalSource = GoalSource.NOT_CONFIGURED
) {
    val isConfigured: Boolean
        get() = listOfNotNull(energyKcal, proteinG, carbohydrateG, fatG, fiberG, sodiumMg).isNotEmpty()

    fun goalFor(nutrient: Nutrient): Double? = when (nutrient) {
        Nutrient.ENERGY -> energyKcal
        Nutrient.PROTEIN -> proteinG
        Nutrient.CARBOHYDRATE -> carbohydrateG
        Nutrient.TOTAL_FAT -> fatG
        Nutrient.FIBER -> fiberG
        Nutrient.SODIUM -> sodiumMg
        else -> null
    }
}

enum class GoalSource(val label: String) {
    NOT_CONFIGURED("não configurado"),
    USER("definido por você"),
    HEALTH_PROFESSIONAL("definido por profissional de saúde")
}

/** Situação de um nutriente frente à meta configurada. Puramente aritmética. */
enum class GoalStatus {
    /** Abaixo da meta configurada. */
    BELOW,

    /** Dentro da faixa de tolerância da meta. */
    MET,

    /** Acima da meta configurada. */
    ABOVE,

    /** Sem meta configurada para este nutriente. */
    NO_GOAL
}

data class NutrientProgress(
    val nutrient: Nutrient,
    val consumed: Double,
    val goal: Double?,
    val status: GoalStatus
) {
    val unit: NutrientUnit get() = nutrient.unit

    /** Quanto falta para a meta. Nulo sem meta; zero quando já alcançada ou excedida. */
    val remaining: Double?
        get() = goal?.let { (it - consumed).coerceAtLeast(0.0) }

    /** Quanto passou da meta. Nulo sem meta; zero quando ainda não excedeu. */
    val excess: Double?
        get() = goal?.let { (consumed - it).coerceAtLeast(0.0) }
}
