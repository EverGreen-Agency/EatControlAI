package com.eatcontrolai.benchmark

import com.eatcontrolai.core.model.Allergen
import com.eatcontrolai.core.model.DecisionState
import com.eatcontrolai.core.model.UserProfile
import com.eatcontrolai.domain.decision.FoodDecisionEngine
import com.eatcontrolai.domain.evidence.EvidenceBuilder
import java.io.File
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Transforma `benchmark/decision_scenarios.csv` na especificação executável do motor de decisão.
 *
 * É o que faltava para os documentos serem *spec driven*: `FR-008` deixa de ser uma frase e passa a
 * ser um número que o build imprime — o **Decision Success Rate** de `docs/METRICS.md`.
 *
 * Duas regras de aprovação, seguindo a política de segurança do projeto:
 *  - nenhum cenário marcado como `critical` pode falhar;
 *  - o DSR global precisa ficar em 100% enquanto o conjunto for pequeno e curado.
 */
class DecisionScenariosTest {

    private data class Scenario(
        val id: String,
        val restrictions: Set<Allergen>,
        val labelText: String,
        val expected: DecisionState,
        val critical: Boolean,
        val notes: String
    )

    private val engine = FoodDecisionEngine()

    @Test
    fun `decision success rate`() {
        val scenarios = loadScenarios()
        assertTrue("Nenhum cenário carregado", scenarios.isNotEmpty())

        val failures = mutableListOf<String>()

        scenarios.forEach { scenario ->
            val profile = UserProfile(id = scenario.id, restrictions = scenario.restrictions)
            val evidence = EvidenceBuilder.fromLabelOcr(scenario.labelText, "scenario_fixture")
            val actual = engine.decide(profile, evidence).state

            if (actual != scenario.expected) {
                failures += "${scenario.id} [${if (scenario.critical) "CRÍTICO" else "normal"}] " +
                    "esperado=${scenario.expected} obtido=$actual — ${scenario.notes}"
            }
        }

        val passed = scenarios.size - failures.size
        val dsr = 100.0 * passed / scenarios.size
        println("=== Decision Success Rate: %.1f%% (%d/%d) ===".format(dsr, passed, scenarios.size))
        failures.forEach { println("  FALHA: $it") }

        assertTrue(
            "DSR abaixo de 100%%:\n${failures.joinToString("\n")}",
            failures.isEmpty()
        )
    }

    /**
     * O CSV vive em `benchmark/` para o time editar como artefato de especificação, não escondido
     * em resources de teste. Os testes rodam com working dir no módulo `app/`, daí os candidatos.
     */
    private fun loadScenarios(): List<Scenario> {
        val candidates = listOf(
            File("../benchmark/decision_scenarios.csv"),
            File("benchmark/decision_scenarios.csv")
        )
        val file = candidates.firstOrNull { it.exists() }
            ?: error(
                "decision_scenarios.csv não encontrado. Procurei em: " +
                    candidates.joinToString { it.absolutePath }
            )

        return file.readLines()
            .filterNot { it.isBlank() || it.startsWith(">") || it.startsWith("scenario_id|") }
            .map { line ->
                val cols = line.split("|")
                require(cols.size >= 6) { "Linha malformada: $line" }
                Scenario(
                    id = cols[0].trim(),
                    restrictions = cols[1].split(";")
                        .filter { it.isNotBlank() }
                        .map { Allergen.valueOf(it.trim()) }
                        .toSet(),
                    labelText = cols[2],
                    expected = DecisionState.valueOf(cols[3].trim()),
                    critical = cols[4].trim().toBoolean(),
                    notes = cols[5].trim()
                )
            }
    }
}
