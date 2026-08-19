package com.eatcontrolai.benchmark

import android.os.Build
import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.eatcontrolai.core.model.Allergen
import com.eatcontrolai.core.model.Restriction
import com.eatcontrolai.core.model.UserProfile
import com.eatcontrolai.inference.mlkit.MlKitOcrProvider
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Varre resolução × compressão e diz qual configuração de captura usar.
 *
 * O material do curso (13.5.3) recomenda MEDIUM a 15 FPS em vez de HIGH a 30, com o argumento de que
 * imagem menos comprimida ajuda mais a acurácia do que resolução extra. **Isso é hipótese.** Este
 * teste a transforma em número no aparelho-alvo, que é o que o `MODEL_BENCHMARK.md` exige antes de
 * qualquer escolha entrar no app.
 *
 * Como rodar, com o celular conectado por USB:
 *
 * ```
 * ./scripts/benchmark.sh          # roda esta varredura junto com a de providers
 * ```
 */
@RunWith(AndroidJUnit4::class)
class CaptureConfigBenchmarkTest {

    private val profile = UserProfile(
        id = "capture-benchmark",
        restrictions = setOf(Restriction(Allergen.MILK))
    )

    @Test
    fun varreResolucaoECompressao() = runBlocking {
        val benchmark = CaptureConfigBenchmark()

        log("=".repeat(72))
        log("Eat Control — varredura de configuração de captura")
        log("aparelho: ${Build.MANUFACTURER} ${Build.MODEL} · Android ${Build.VERSION.RELEASE}")
        log("=".repeat(72))

        val results = benchmark.run(provider = MlKitOcrProvider(), profile = profile)
        benchmark.report(results).forEach { log(it) }

        results.filter { it.divergences.isNotEmpty() }.forEach { result ->
            log("")
            log("divergências em ${result.config.id}:")
            result.divergences.forEach { log("  $it") }
        }

        log("")
        log("Regra de escolha: entre as configurações com DSR 100%, a mais leve em bytes.")
        log("Acurácia é restrição, não termo de um score.")

        assertTrue("Nenhuma configuração avaliada", results.isNotEmpty())
        assertTrue(
            "Nenhuma configuração acertou uma decisão sequer — algo está errado no harness",
            results.any { it.correctDecisions > 0 }
        )
    }

    private fun log(message: String) = Log.i(TAG, message)

    private companion object {
        const val TAG = "EC_BENCH"
    }
}
