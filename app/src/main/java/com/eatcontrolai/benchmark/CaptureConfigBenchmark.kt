package com.eatcontrolai.benchmark

import com.eatcontrolai.core.model.UserProfile
import com.eatcontrolai.domain.decision.FoodDecisionEngine
import com.eatcontrolai.domain.evidence.EvidenceBuilder
import com.eatcontrolai.glasses.MockLabelRenderer
import com.eatcontrolai.glasses.MockScene
import com.eatcontrolai.glasses.MockScenes
import com.eatcontrolai.inference.OcrProvider

/**
 * Varredura de configuração de captura.
 *
 * O material do curso (13.5.3) afirma que **imagem menos comprimida tende a melhorar mais a acurácia
 * do que resolução extra melhoraria**, e recomenda MEDIUM a 15 FPS em vez de HIGH a 30. Isso é uma
 * hipótese testável, não uma verdade a ser aceita — este harness a transforma em número no
 * aparelho-alvo.
 *
 * A régua é a mesma do [ProviderBenchmark]: qualidade medida por **decisão acertada**, não por texto
 * idêntico. Um OCR que lê "CONTEM LEITF" tem erro de caractere baixo e ainda assim manda a decisão
 * errada para o usuário.
 *
 * Sobre o que se aplica onde:
 *
 * | Parâmetro | Onde vale |
 * | :--- | :--- |
 * | resolução e qualidade JPEG | mock e câmera do celular — é o que chega ao OCR |
 * | `VideoQuality` × `frameRate` | só na trilha do DAT, e determina a resolução do frame |
 *
 * A varredura abaixo cobre o primeiro caso, que roda em qualquer aparelho. A grade do DAT precisa
 * dos óculos (reais ou pelo Mock Device Kit) e usa as mesmas resoluções: HIGH 720×1280,
 * MEDIUM 504×896, LOW 360×640.
 */
class CaptureConfigBenchmark(
    private val engine: FoodDecisionEngine = FoodDecisionEngine()
) {

    data class Config(
        val label: String,
        val width: Int,
        val height: Int,
        val jpegQuality: Int
    ) {
        val id: String get() = "${label}_q$jpegQuality"
    }

    data class Result(
        val config: Config,
        val avgBytes: Int,
        val p50Ms: Long,
        val p95Ms: Long,
        val correctDecisions: Int,
        val totalDecisions: Int,
        val divergences: List<String>
    ) {
        val decisionSuccessRate: Double
            get() = if (totalDecisions == 0) 0.0 else 100.0 * correctDecisions / totalDecisions
    }

    /** Grade padrão: as três resoluções do DAT cruzadas com três níveis de compressão. */
    val defaultGrid: List<Config> = listOf(
        Config("HIGH_720x1280", 720, 1280, 60),
        Config("HIGH_720x1280", 720, 1280, 80),
        Config("HIGH_720x1280", 720, 1280, 95),
        Config("MEDIUM_504x896", 504, 896, 60),
        Config("MEDIUM_504x896", 504, 896, 80),
        Config("MEDIUM_504x896", 504, 896, 95),
        Config("LOW_360x640", 360, 640, 80),
        Config("LOW_360x640", 360, 640, 95)
    )

    suspend fun run(
        provider: OcrProvider,
        profile: UserProfile,
        grid: List<Config> = defaultGrid,
        scenes: List<MockScene> = MockScenes.labels,
        repetitions: Int = 3,
        warmup: Int = 1
    ): List<Result> {
        // Warm-up fora da medição: a primeira inferência do ML Kit carrega modelo e infla a latência.
        repeat(warmup) {
            scenes.forEach { provider.recognize(MockLabelRenderer.render(it.labelText)) }
        }

        return grid.map { config ->
            val frames = scenes.associate { scene ->
                scene.id to MockLabelRenderer.render(
                    text = scene.labelText,
                    width = config.width,
                    height = config.height,
                    jpegQuality = config.jpegQuality
                )
            }

            val latencies = mutableListOf<Long>()
            val divergences = mutableListOf<String>()
            var correct = 0
            var total = 0

            repeat(repetitions) {
                for (scene in scenes) {
                    val frame = frames.getValue(scene.id)
                    val ocr = provider.recognize(frame)
                    latencies += ocr.meta.latencyMs

                    val evidence = EvidenceBuilder.fromLabelOcr(
                        ocr.text, ocr.meta.providerId, ocr.meta.version
                    )
                    val state = engine.decide(profile, evidence).state
                    total++
                    if (state == scene.expectedForMilkProfile) {
                        correct++
                    } else {
                        divergences += "${scene.id}: esperado=${scene.expectedForMilkProfile} obtido=$state"
                    }
                }
            }

            val sorted = latencies.sorted()
            Result(
                config = config,
                avgBytes = frames.values.map { it.size }.average().toInt(),
                p50Ms = sorted.percentile(50),
                p95Ms = sorted.percentile(95),
                correctDecisions = correct,
                totalDecisions = total,
                divergences = divergences.distinct()
            )
        }
    }

    /**
     * Escolhe a configuração recomendada: entre as que **não erram nenhuma decisão**, a mais barata
     * em bytes. Sem candidata perfeita, devolve a de maior DSR.
     *
     * A ordem importa: acurácia é restrição, não termo de um score. Uma configuração mais leve que
     * erra uma decisão a mais não é um bom negócio num app que diz o que a pessoa pode comer.
     */
    fun recommend(results: List<Result>): Result? =
        results.filter { it.decisionSuccessRate >= 100.0 }.minByOrNull { it.avgBytes }
            ?: results.maxByOrNull { it.decisionSuccessRate }

    fun report(results: List<Result>): List<String> = buildList {
        add("%-18s %5s %9s %8s %8s %8s".format("config", "qual", "bytes", "p50", "p95", "DSR"))
        results.forEach {
            add(
                "%-18s %5d %8dB %6dms %6dms %7.1f%%".format(
                    it.config.label,
                    it.config.jpegQuality,
                    it.avgBytes,
                    it.p50Ms,
                    it.p95Ms,
                    it.decisionSuccessRate
                )
            )
        }
        recommend(results)?.let {
            add("")
            add("→ recomendada: ${it.config.id} (${it.avgBytes} B, p95 ${it.p95Ms} ms, DSR %.1f%%)".format(it.decisionSuccessRate))
        }
    }

    private fun List<Long>.percentile(p: Int): Long {
        if (isEmpty()) return 0L
        return this[((p / 100.0) * (size - 1)).toInt().coerceIn(0, size - 1)]
    }
}
