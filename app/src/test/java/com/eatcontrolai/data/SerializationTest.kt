package com.eatcontrolai.data

import com.eatcontrolai.core.model.Allergen
import com.eatcontrolai.core.model.DecisionState
import com.eatcontrolai.core.model.GoalSource
import com.eatcontrolai.core.model.Guideline
import com.eatcontrolai.core.model.MacroGoals
import com.eatcontrolai.core.model.MealRecord
import com.eatcontrolai.core.model.PrivacySettings
import com.eatcontrolai.core.model.Restriction
import com.eatcontrolai.core.model.RestrictionSeverity
import com.eatcontrolai.core.model.UncertaintyPolicy
import com.eatcontrolai.core.model.UserProfile
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * `org.json` está disponível no classpath de teste do Android Gradle Plugin, então a serialização
 * é testável sem aparelho.
 */
class SerializationTest {

    private val profile = UserProfile(
        id = "u1",
        displayName = "João",
        usesGlp1 = true,
        restrictions = setOf(
            Restriction(Allergen.MILK, RestrictionSeverity.CRITICAL, UncertaintyPolicy.ASK_CONFIRMATION),
            Restriction(Allergen.SOY, RestrictionSeverity.PREFERENCE, UncertaintyPolicy.INFORM_ONLY)
        ),
        freeTextRestrictions = setOf("evitar frituras"),
        goals = setOf("Priorizar proteína"),
        guidelines = listOf(Guideline("Título", "Detalhe")),
        macroGoals = MacroGoals(
            energyKcal = 1800.0,
            proteinG = 90.0,
            definedBy = GoalSource.HEALTH_PROFESSIONAL
        )
    )

    @Test
    fun `perfil sobrevive a ida e volta`() {
        val decoded = Serialization.decodeProfile(Serialization.encode(profile), UserProfile(id = "x"))
        assertEquals(profile, decoded)
    }

    @Test
    fun `privacidade sobrevive a ida e volta`() {
        val settings = PrivacySettings(savePhotos = true, shareForImprovement = false, syncHistory = true)
        assertEquals(settings, Serialization.decodePrivacy(Serialization.encode(settings)))
    }

    @Test
    fun `historico sobrevive a ida e volta`() {
        val records = listOf(
            MealRecord(
                id = "r1",
                timestampMillis = 1_700_000_000_000,
                title = "Iogurte",
                decisionState = DecisionState.INCOMPATIBLE,
                shortMessage = "Contém leite.",
                recognizedText = "ALÉRGICOS: CONTÉM LEITE.",
                evidenceLabels = listOf("Declarado no rótulo", "Texto reconhecido"),
                endToEndMs = 812,
                userConfirmed = true,
                confirmedItems = listOf("frango", "arroz"),
                containsVisualEstimate = true
            )
        )
        assertEquals(records, Serialization.decodeHistory(Serialization.encode(records)))
    }

    @Test
    fun `json corrompido volta ao padrao em vez de quebrar`() {
        val fallback = UserProfile(id = "fallback")
        assertEquals(fallback, Serialization.decodeProfile("{{{ não é json", fallback))
        assertEquals(PrivacySettings(), Serialization.decodePrivacy("nada disso"))
        assertTrue(Serialization.decodeHistory("[[[").isEmpty())
    }

    @Test
    fun `historico antigo sem campos assistidos permanece compativel`() {
        val json = """
            [{
              "id":"old","timestampMillis":1,"title":"Antigo",
              "decisionState":"INSUFFICIENT_INFORMATION","shortMessage":"Sem dados",
              "recognizedText":"","evidenceLabels":[],"endToEndMs":2
            }]
        """.trimIndent()

        val record = Serialization.decodeHistory(json).single()
        assertTrue(record.confirmedItems.isEmpty())
        assertEquals(false, record.containsVisualEstimate)
    }

    @Test
    fun `enum desconhecido de versao futura e ignorado sem derrubar o resto`() {
        val json = """
            {"id":"u1","restrictions":[
              {"allergen":"UNOBTANIUM","severity":"CRITICAL"},
              {"allergen":"MILK","severity":"MODERATE","uncertaintyPolicy":"INFORM_ONLY"}
            ]}
        """.trimIndent()
        val decoded = Serialization.decodeProfile(json, UserProfile(id = "x"))
        assertEquals(1, decoded.restrictions.size)
        assertEquals(Allergen.MILK, decoded.restrictions.first().allergen)
        assertEquals(RestrictionSeverity.MODERATE, decoded.restrictions.first().severity)
    }
}
