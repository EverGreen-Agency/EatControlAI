package com.eatcontrolai.data

import com.eatcontrolai.domain.glp1.PersonalRule
import com.eatcontrolai.domain.glp1.PersonalRuleAction
import com.eatcontrolai.domain.glp1.PersonalRuleOrigin
import com.eatcontrolai.domain.glp1.SymptomKind
import com.eatcontrolai.domain.glp1.SymptomReport
import com.eatcontrolai.domain.plate.PlateFoodClass
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Persistência das regras pessoais e dos sintomas relatados.
 *
 * São dados de saúde criados pela própria pessoa, então a política vale integralmente: ficam locais,
 * sem backup em nuvem, e um registro corrompido nunca deve derrubar a leitura do resto.
 */
class Glp1SerializationTest {

    private val regra = PersonalRule(
        id = "leite",
        target = "derivados de leite",
        action = PersonalRuleAction.AVOID,
        origin = PersonalRuleOrigin.REPORTED_DISCOMFORT,
        note = "Passei mal depois de um lanche com leite.",
        matchClasses = setOf(PlateFoodClass.CHEESE),
        matchTerms = setOf("queijo"),
        possibleTerms = setOf("molho")
    )

    @Test
    fun `preserva regra pessoal no round trip`() {
        val decodificado = Serialization.decodePersonalRules(Serialization.encodePersonalRules(listOf(regra)))

        assertEquals(listOf(regra), decodificado)
    }

    @Test
    fun `preserva sintoma relatado com refeicao vinculada`() {
        val relato = SymptomReport(
            kind = SymptomKind.PERSISTENT_GI,
            timestampMillis = 1_787_317_200_000L,
            relatedRecordId = "registro-1",
            note = "desconforto após o almoço"
        )

        val decodificado = Serialization.decodeSymptomReports(
            Serialization.encodeSymptomReports(listOf(relato))
        )

        assertEquals(listOf(relato), decodificado)
    }

    @Test
    fun `descarta regra sem alvo e mantem as demais`() {
        val json = """
            [
              {"id":"vazio","target":"","action":"AVOID","origin":"PREFERENCE"},
              {"id":"leite","target":"derivados de leite","action":"AVOID","origin":"PREFERENCE"}
            ]
        """.trimIndent()

        val decodificado = Serialization.decodePersonalRules(json)

        assertEquals(1, decodificado.size)
        assertEquals("leite", decodificado.first().id)
    }

    @Test
    fun `descarta sintoma com tipo desconhecido`() {
        val json = """[{"kind":"NAO_EXISTE","timestampMillis":1}]"""

        assertTrue(Serialization.decodeSymptomReports(json).isEmpty())
    }

    @Test
    fun `json corrompido devolve lista vazia em vez de falhar`() {
        assertTrue(Serialization.decodePersonalRules("{ não é json }").isEmpty())
        assertTrue(Serialization.decodeSymptomReports("{ não é json }").isEmpty())
    }

    @Test
    fun `campos ausentes assumem padroes seguros`() {
        val json = """[{"id":"x","target":"glúten","action":"OBSERVE","origin":"PREFERENCE"}]"""

        val regra = Serialization.decodePersonalRules(json).single()

        assertEquals(PersonalRuleAction.OBSERVE, regra.action)
        assertEquals("", regra.note)
        assertTrue(regra.matchClasses.isEmpty())
        assertTrue(regra.matchTerms.isEmpty())
        assertTrue(regra.possibleTerms.isEmpty())
    }

    @Test
    fun `ignora classe visual desconhecida sem perder a regra`() {
        val json = """
            [{"id":"x","target":"leite","action":"AVOID","origin":"PREFERENCE",
              "matchClasses":["CHEESE","CLASSE_INEXISTENTE"]}]
        """.trimIndent()

        val regra = Serialization.decodePersonalRules(json).single()

        assertEquals(setOf(PlateFoodClass.CHEESE), regra.matchClasses)
    }
}
