package com.eatcontrolai.benchmark

import com.eatcontrolai.core.model.UserProfile
import com.eatcontrolai.domain.decision.FoodDecisionEngine
import com.eatcontrolai.domain.evidence.EvidenceBuilder
import com.eatcontrolai.glasses.MockLabelRenderer
import com.eatcontrolai.glasses.MockScene
import com.eatcontrolai.glasses.MockScenes
import com.eatcontrolai.inference.OcrProvider

/**
 * Harness de benchmark de providers.
 *
 * Implementa o protocolo de `docs/MODEL_BENCHMARK.md`: warm-up, N repetições, medição de latência e
 * de qualidade no **mesmo** dataset, em aparelho real. Roda em dois lugares, com o mesmo código:
 *
 *  - `./gradlew :app:connectedDebugAndroidTest` (teste instrumentado, saída no logcat);
 *  - tela de Laboratório do app em build de debug.
 *
 * A métrica de qualidade não é "acertou o texto", é **acertou a decisão**. Um OCR que lê
 * "CONTEM LEITF" pode até ter CER baixo e ainda assim produzir a decisão errada — e é a decisão que
 * chega ao usuário.
 */
class ProviderBenchmark(
    private val engine: FoodDecisionEngine = FoodDecisionEngine()
) {

    data class SceneOutcome(
        val sceneId: String,
        val expected: String,
        val actual: String,
        val latencyMs: Long,
        val recognizedText: String
    ) {
        val correct: Boolean get() = expected == actual
    }

    data class Result(
        val providerId: String,
        val runtime: String,
        val modelVersion: String,
        val samples: Int,
        val p50Ms: Long,
        val p90Ms: Long,
        val p95Ms: Long,
        val maxMs: Long,
        val correctDecisions: Int,
        val totalDecisions: Int,
        val outcomes: List<SceneOutcome>
    ) {
        val decisionSuccessRate: Double
            get() = if (totalDecisions == 0) 0.0 else 100.0 * correctDecisions / totalDecisions

        fun toReportLines(): List<String> = buildList {
            add("provider=$providerId runtime=$runtime version=$modelVersion")
            add("  amostras=$samples  p50=${p50Ms}ms  p90=${p90Ms}ms  p95=${p95Ms}ms  max=${maxMs}ms")
            add("  DSR=%.1f%% (%d/%d)".format(decisionSuccessRate, correctDecisions, totalDecisions))
            outcomes.filterNot { it.correct }.forEach {
                add("  DIVERGÊNCIA ${it.sceneId}: esperado=${it.expected} obtido=${it.actual}")
            }
        }
    }

    /**
     * Roda [provider] sobre [scenes], [repetitions] vezes cada, depois de [warmup] execuções
     * descartadas. As execuções de warm-up existem porque a primeira inferência do ML Kit carrega
     * modelo e infla a latência.
     */
    suspend fun run(
        provider: OcrProvider,
        profile: UserProfile,
        /** Só cenas de rótulo: é OCR que está sendo medido, não leitura de barras. */
        scenes: List<MockScene> = MockScenes.labels,
        repetitions: Int = 5,
        warmup: Int = 2
    ): Result {
        val frames = scenes.associate { it.id to MockLabelRenderer.render(it.labelText) }

        repeat(warmup) {
            scenes.forEach { provider.recognize(frames.getValue(it.id)) }
        }

        val latencies = mutableListOf<Long>()
        val outcomes = mutableListOf<SceneOutcome>()
        var providerId = "?"
        var runtime = "?"
        var version = "?"

        repeat(repetitions) {
            for (scene in scenes) {
                val ocr = provider.recognize(frames.getValue(scene.id))
                providerId = ocr.meta.providerId
                runtime = ocr.meta.runtime
                version = ocr.meta.version
                latencies += ocr.meta.latencyMs

                val evidence = EvidenceBuilder.fromLabelOcr(ocr.text, ocr.meta.providerId, ocr.meta.version)
                val decision = engine.decide(profile, evidence)

                outcomes += SceneOutcome(
                    sceneId = scene.id,
                    expected = scene.expectedForMilkProfile.name,
                    actual = decision.state.name,
                    latencyMs = ocr.meta.latencyMs,
                    recognizedText = ocr.text
                )
            }
        }

        val sorted = latencies.sorted()
        return Result(
            providerId = providerId,
            runtime = runtime,
            modelVersion = version,
            samples = sorted.size,
            p50Ms = sorted.percentile(50),
            p90Ms = sorted.percentile(90),
            p95Ms = sorted.percentile(95),
            maxMs = sorted.lastOrNull() ?: 0L,
            correctDecisions = outcomes.count { it.correct },
            totalDecisions = outcomes.size,
            outcomes = outcomes
        )
    }

    private fun List<Long>.percentile(p: Int): Long {
        if (isEmpty()) return 0L
        val index = ((p / 100.0) * (size - 1)).toInt().coerceIn(0, size - 1)
        return this[index]
    }
}
