package com.eatcontrolai.data

import com.eatcontrolai.core.model.Allergen
import com.eatcontrolai.core.model.DecisionState
import com.eatcontrolai.core.model.GoalSource
import com.eatcontrolai.core.model.Guideline
import com.eatcontrolai.core.model.MacroGoals
import com.eatcontrolai.core.model.MealRecord
import com.eatcontrolai.core.model.Nutrient
import com.eatcontrolai.core.model.NutrientAmount
import com.eatcontrolai.core.model.NutritionBasis
import com.eatcontrolai.core.model.PrivacySettings
import com.eatcontrolai.core.model.Restriction
import com.eatcontrolai.core.model.RestrictionSeverity
import com.eatcontrolai.core.model.UncertaintyPolicy
import com.eatcontrolai.core.model.UserProfile
import com.eatcontrolai.domain.glp1.PersonalRule
import com.eatcontrolai.domain.glp1.PersonalRuleAction
import com.eatcontrolai.domain.glp1.PersonalRuleOrigin
import com.eatcontrolai.domain.glp1.SymptomKind
import com.eatcontrolai.domain.glp1.SymptomReport
import com.eatcontrolai.domain.plate.PlateFoodClass
import org.json.JSONArray
import org.json.JSONObject

/**
 * Serialização com `org.json`, que já vem no Android.
 *
 * Evita o plugin do kotlinx-serialization por três documentos pequenos. Toda leitura é tolerante a
 * campo ausente e a enum desconhecido: uma versão futura do app não pode travar na inicialização
 * por causa de um dado gravado por uma versão anterior.
 */
object Serialization {

    // ------------------------------------------------------------------ perfil

    fun encode(profile: UserProfile): String = JSONObject().apply {
        put("id", profile.id)
        put("displayName", profile.displayName)
        put("usesGlp1", profile.usesGlp1)
        put("restrictions", JSONArray().apply {
            profile.restrictions.forEach { restriction ->
                put(
                    JSONObject().apply {
                        put("allergen", restriction.allergen.name)
                        put("severity", restriction.severity.name)
                        put("uncertaintyPolicy", restriction.uncertaintyPolicy.name)
                    }
                )
            }
        })
        put("freeTextRestrictions", JSONArray(profile.freeTextRestrictions.toList()))
        put("goals", JSONArray(profile.goals.toList()))
        put("guidelines", JSONArray().apply {
            profile.guidelines.forEach { guideline ->
                put(JSONObject().apply {
                    put("title", guideline.title)
                    put("detail", guideline.detail)
                })
            }
        })
        put("macroGoals", JSONObject().apply {
            profile.macroGoals.energyKcal?.let { put("energyKcal", it) }
            profile.macroGoals.proteinG?.let { put("proteinG", it) }
            profile.macroGoals.carbohydrateG?.let { put("carbohydrateG", it) }
            profile.macroGoals.fatG?.let { put("fatG", it) }
            profile.macroGoals.fiberG?.let { put("fiberG", it) }
            profile.macroGoals.sodiumMg?.let { put("sodiumMg", it) }
            put("definedBy", profile.macroGoals.definedBy.name)
        })
    }.toString()

    fun decodeProfile(json: String, fallback: UserProfile): UserProfile = runCatching {
        val root = JSONObject(json)
        UserProfile(
            id = root.optString("id", fallback.id),
            displayName = root.optString("displayName", fallback.displayName),
            usesGlp1 = root.optBoolean("usesGlp1", fallback.usesGlp1),
            restrictions = root.optJSONArray("restrictions").objects().mapNotNull { item ->
                val allergen = enumOrNull<Allergen>(item.optString("allergen")) ?: return@mapNotNull null
                Restriction(
                    allergen = allergen,
                    severity = enumOrNull<RestrictionSeverity>(item.optString("severity"))
                        ?: RestrictionSeverity.CRITICAL,
                    uncertaintyPolicy = enumOrNull<UncertaintyPolicy>(item.optString("uncertaintyPolicy"))
                        ?: UncertaintyPolicy.ASK_CONFIRMATION
                )
            }.toSet(),
            freeTextRestrictions = root.optJSONArray("freeTextRestrictions").strings().toSet(),
            goals = root.optJSONArray("goals").strings().toSet(),
            guidelines = root.optJSONArray("guidelines").objects().map {
                Guideline(it.optString("title"), it.optString("detail"))
            },
            macroGoals = root.optJSONObject("macroGoals").toMacroGoals(fallback.macroGoals)
        )
    }.getOrDefault(fallback)

    /** Meta ausente permanece nula: zero significaria "meta de zero", que é outra coisa. */
    private fun JSONObject?.toMacroGoals(fallback: MacroGoals): MacroGoals {
        if (this == null) return fallback
        return MacroGoals(
            energyKcal = optionalDouble("energyKcal"),
            proteinG = optionalDouble("proteinG"),
            carbohydrateG = optionalDouble("carbohydrateG"),
            fatG = optionalDouble("fatG"),
            fiberG = optionalDouble("fiberG"),
            sodiumMg = optionalDouble("sodiumMg"),
            definedBy = enumOrNull<GoalSource>(optString("definedBy")) ?: GoalSource.NOT_CONFIGURED
        )
    }

    private fun JSONObject.optionalDouble(key: String): Double? =
        if (has(key) && !isNull(key)) optDouble(key).takeIf { !it.isNaN() } else null

    // ------------------------------------------------------------- privacidade

    fun encode(settings: PrivacySettings): String = JSONObject().apply {
        put("savePhotos", settings.savePhotos)
        put("shareForImprovement", settings.shareForImprovement)
        put("syncHistory", settings.syncHistory)
    }.toString()

    fun decodePrivacy(json: String): PrivacySettings = runCatching {
        val root = JSONObject(json)
        PrivacySettings(
            savePhotos = root.optBoolean("savePhotos", false),
            shareForImprovement = root.optBoolean("shareForImprovement", false),
            syncHistory = root.optBoolean("syncHistory", false)
        )
    }.getOrDefault(PrivacySettings())

    // ---------------------------------------------------------------- histórico

    fun encode(records: List<MealRecord>): String = JSONArray().apply {
        records.forEach { record ->
            put(
                JSONObject().apply {
                    put("id", record.id)
                    put("timestampMillis", record.timestampMillis)
                    put("title", record.title)
                    put("decisionState", record.decisionState.name)
                    put("shortMessage", record.shortMessage)
                    put("recognizedText", record.recognizedText)
                    put("evidenceLabels", JSONArray(record.evidenceLabels))
                    put("endToEndMs", record.endToEndMs)
                    put("userConfirmed", record.userConfirmed)
                    put("consumedNutrients", JSONArray().apply {
                        record.consumedNutrients.forEach { amount ->
                            put(JSONObject().apply {
                                put("nutrient", amount.nutrient.name)
                                put("value", amount.value)
                                put("basis", amount.basis.name)
                            })
                        }
                    })
                    put("confirmedItems", JSONArray(record.confirmedItems))
                    put("containsVisualEstimate", record.containsVisualEstimate)
                }
            )
        }
    }.toString()

    fun decodeHistory(json: String): List<MealRecord> = runCatching {
        JSONArray(json).objects().mapNotNull { item ->
            val state = enumOrNull<DecisionState>(item.optString("decisionState")) ?: return@mapNotNull null
            MealRecord(
                id = item.optString("id"),
                timestampMillis = item.optLong("timestampMillis"),
                title = item.optString("title"),
                decisionState = state,
                shortMessage = item.optString("shortMessage"),
                recognizedText = item.optString("recognizedText"),
                evidenceLabels = item.optJSONArray("evidenceLabels").strings(),
                endToEndMs = item.optLong("endToEndMs"),
                userConfirmed = item.optBoolean("userConfirmed", false),
                consumedNutrients = item.optJSONArray("consumedNutrients").objects()
                    .mapNotNull { entry ->
                        val nutrient = enumOrNull<Nutrient>(entry.optString("nutrient"))
                            ?: return@mapNotNull null
                        NutrientAmount(
                            nutrient = nutrient,
                            value = entry.optDouble("value").takeIf { !it.isNaN() }
                                ?: return@mapNotNull null,
                            basis = enumOrNull<NutritionBasis>(entry.optString("basis"))
                                ?: NutritionBasis.PER_PORTION
                        )
                    },
                confirmedItems = item.optJSONArray("confirmedItems").strings(),
                containsVisualEstimate = item.optBoolean("containsVisualEstimate", false)
            )
        }
    }.getOrDefault(emptyList())

    // ------------------------------------------- regras pessoais e sintomas

    fun encodePersonalRules(rules: List<PersonalRule>): String = JSONArray().apply {
        rules.forEach { rule ->
            put(
                JSONObject().apply {
                    put("id", rule.id)
                    put("target", rule.target)
                    put("action", rule.action.name)
                    put("origin", rule.origin.name)
                    put("note", rule.note)
                    put("matchClasses", JSONArray(rule.matchClasses.map { it.name }))
                    put("matchTerms", JSONArray(rule.matchTerms.toList()))
                    put("possibleTerms", JSONArray(rule.possibleTerms.toList()))
                }
            )
        }
    }.toString()

    /** Regra sem alvo, ação ou origem válidos é descartada: melhor perder a regra que aplicá-la errado. */
    fun decodePersonalRules(json: String): List<PersonalRule> = runCatching {
        JSONArray(json).objects().mapNotNull { item ->
            val target = item.optString("target").takeIf { it.isNotBlank() }
                ?: return@mapNotNull null
            val action = enumOrNull<PersonalRuleAction>(item.optString("action"))
                ?: return@mapNotNull null
            val origin = enumOrNull<PersonalRuleOrigin>(item.optString("origin"))
                ?: return@mapNotNull null

            PersonalRule(
                id = item.optString("id"),
                target = target,
                action = action,
                origin = origin,
                note = item.optString("note"),
                matchClasses = item.optJSONArray("matchClasses").strings()
                    .mapNotNull { enumOrNull<PlateFoodClass>(it) }
                    .toSet(),
                matchTerms = item.optJSONArray("matchTerms").strings().toSet(),
                possibleTerms = item.optJSONArray("possibleTerms").strings().toSet()
            )
        }
    }.getOrDefault(emptyList())

    fun encodeSymptomReports(reports: List<SymptomReport>): String = JSONArray().apply {
        reports.forEach { report ->
            put(
                JSONObject().apply {
                    put("kind", report.kind.name)
                    put("timestampMillis", report.timestampMillis)
                    report.relatedRecordId?.let { put("relatedRecordId", it) }
                    put("note", report.note)
                }
            )
        }
    }.toString()

    fun decodeSymptomReports(json: String): List<SymptomReport> = runCatching {
        JSONArray(json).objects().mapNotNull { item ->
            val kind = enumOrNull<SymptomKind>(item.optString("kind")) ?: return@mapNotNull null
            SymptomReport(
                kind = kind,
                timestampMillis = item.optLong("timestampMillis"),
                relatedRecordId = item.optString("relatedRecordId").takeIf { it.isNotBlank() },
                note = item.optString("note")
            )
        }
    }.getOrDefault(emptyList())

    // ------------------------------------------------------------------ helpers

    private inline fun <reified T : Enum<T>> enumOrNull(name: String?): T? =
        name?.takeIf { it.isNotBlank() }?.let { value ->
            enumValues<T>().firstOrNull { it.name == value }
        }

    private fun JSONArray?.objects(): List<JSONObject> =
        (0 until (this?.length() ?: 0)).mapNotNull { this?.optJSONObject(it) }

    private fun JSONArray?.strings(): List<String> =
        (0 until (this?.length() ?: 0)).mapNotNull { this?.optString(it) }.filter { it.isNotBlank() }
}
