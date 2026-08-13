package com.eatcontrolai.benchmark

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.eatcontrolai.glasses.Ean13Renderer
import com.eatcontrolai.glasses.MockScenes
import com.eatcontrolai.inference.mlkit.MlKitBarcodeProvider
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Prova que a trilha de código de barras funciona de ponta a ponta.
 *
 * Renderiza as barras EAN-13 e manda para o **ML Kit de verdade**. Se a codificação estivesse
 * errada, ou se as barras estivessem estreitas demais para o decodificador, este teste falharia —
 * que é exatamente o sinal que faltava enquanto o mock desenhava só texto.
 */
@RunWith(AndroidJUnit4::class)
class BarcodeScanInstrumentedTest {

    private val provider = MlKitBarcodeProvider()

    @Test
    fun mlKitDecodificaAsBarrasRenderizadas() = runBlocking {
        MockScenes.barcodes.forEach { scene ->
            val ean = requireNotNull(scene.ean) { "Cena ${scene.id} sem EAN" }
            val frame = Ean13Renderer.render(ean, scene.productName, scene.brand)

            val result = provider.decode(frame)
            Log.i(TAG, "${scene.id}: esperado=$ean lido=${result.rawValue} (${result.meta.latencyMs} ms)")

            assertEquals(
                "ML Kit não leu o código da cena ${scene.id}",
                ean,
                result.rawValue
            )
        }
    }

    private companion object {
        const val TAG = "EC_BENCH"
    }
}
