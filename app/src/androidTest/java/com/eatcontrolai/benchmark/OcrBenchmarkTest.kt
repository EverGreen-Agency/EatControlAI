package com.eatcontrolai.benchmark

import android.os.Build
import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.eatcontrolai.core.model.Allergen
import com.eatcontrolai.core.model.Restriction
import com.eatcontrolai.core.model.UserProfile
import com.eatcontrolai.inference.OcrProvider
import com.eatcontrolai.inference.mlkit.MlKitOcrProvider
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Benchmark de OCR em aparelho real — o que `docs/SRS.md` NFR-006 exige e o emulador não entrega.
 *
 * Como rodar, com o celular conectado por USB e depuração USB ativa:
 *
 * ```
 * ./scripts/benchmark.sh
 * ```
 *
 * ou, direto:
 *
 * ```
 * adb logcat -c
 * ./gradlew :app:connectedDebugAndroidTest
 * adb logcat -d -s EC_BENCH:I
 * ```
 *
 * Para comparar dois modelos, acrescente o candidato à lista [providers] e rode de novo. É assim
 * que a troca de provider vira número em vez de opinião.
 */
@RunWith(AndroidJUnit4::class)
class OcrBenchmarkTest {

    private val profile = UserProfile(
        id = "benchmark",
        restrictions = setOf(Restriction(Allergen.MILK))
    )

    /** Candidatos a comparar. Espelha a seção `ocr` de `benchmark/candidates.yaml`. */
    private val providers: List<Pair<String, () -> OcrProvider>> = listOf(
        "mlkit_text_v2" to { MlKitOcrProvider() }
        // Novo candidato entra aqui, ex.: "tesseract_pt" to { TesseractOcrProvider(ctx) }
    )

    @Test
    fun compararProvidersDeOcr() = runBlocking {
        val benchmark = ProviderBenchmark()

        log("=".repeat(64))
        log("Eat Control — benchmark de OCR")
        log("aparelho: ${Build.MANUFACTURER} ${Build.MODEL} · Android ${Build.VERSION.RELEASE}")
        log("=".repeat(64))

        val results = providers.map { (name, factory) ->
            log("")
            log("→ $name")
            val result = benchmark.run(provider = factory(), profile = profile)
            result.toReportLines().forEach { log(it) }
            result
        }

        log("")
        log("-".repeat(64))
        log("%-22s %7s %7s %7s %9s".format("provider", "p50", "p90", "p95", "DSR"))
        results.forEach {
            log(
                "%-22s %6dms %6dms %6dms %8.1f%%".format(
                    it.providerId, it.p50Ms, it.p90Ms, it.p95Ms, it.decisionSuccessRate
                )
            )
        }
        log("-".repeat(64))
        log("Alvo NFR-002: p95 do fluxo de rótulo abaixo de 2500 ms.")

        val best = results.maxByOrNull { it.decisionSuccessRate }
        assertTrue("Nenhum provider produziu resultado", best != null)
        assertTrue(
            "Nenhum cenário foi avaliado — o dataset do benchmark está vazio",
            best!!.totalDecisions > 0
        )
    }

    private fun log(message: String) = Log.i(TAG, message)

    private companion object {
        const val TAG = "EC_BENCH"
    }
}
