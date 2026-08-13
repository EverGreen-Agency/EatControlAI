package com.eatcontrolai.data

import com.eatcontrolai.core.model.Allergen
import com.eatcontrolai.core.model.DecisionState
import com.eatcontrolai.core.model.Guideline
import com.eatcontrolai.core.model.MealRecord
import com.eatcontrolai.core.model.PrivacySettings
import com.eatcontrolai.core.model.Restriction
import com.eatcontrolai.core.model.RestrictionSeverity
import com.eatcontrolai.core.model.UncertaintyPolicy
import com.eatcontrolai.core.model.UserProfile
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
            }
        )
    }.getOrDefault(fallback)

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
                userConfirmed = item.optBoolean("userConfirmed", false)
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
