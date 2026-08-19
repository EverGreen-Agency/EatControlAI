package com.eatcontrolai.domain.routing

import com.eatcontrolai.glasses.MockScenes
import com.eatcontrolai.orchestration.AnalysisTrack
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ContextRouterTest {

    @Test
    fun `codigo de barras vence tudo e para a cascata`() {
        // Mesmo com texto de rótulo presente, o EAN é a evidência mais barata e mais confiável.
        val decision = ContextRouter.route("7891000100103", "ALÉRGICOS: CONTÉM LEITE.")
        assertTrue(decision is ContextRouter.Decision.Product)
        assertEquals(AnalysisTrack.BARCODE, decision.track)
    }

    @Test
    fun `marcador de rotulagem leva para a trilha de rotulo`() {
        val decision = ContextRouter.route(null, "ALÉRGICOS: CONTÉM LEITE.")
        assertEquals(AnalysisTrack.LABEL, decision.track)
        assertTrue("razão deveria citar o marcador", decision.reason.contains("CONTEM"))
    }

    @Test
    fun `texto denso sem marcador ainda e rotulo`() {
        val texto = "FARINHA DE TRIGO ENRIQUECIDA COM FERRO E ACIDO FOLICO, ACUCAR, SAL"
        assertEquals(AnalysisTrack.LABEL, ContextRouter.route(null, texto).track)
    }

    @Test
    fun `frente promocional nao vira rotulo`() {
        val decision = ContextRouter.route(null, "NOVO! LEVE 3 PAGUE 2")
        assertNull("Sem trilha implementada para isto", decision.track)
        assertTrue(decision is ContextRouter.Decision.Unsupported)
    }

    @Test
    fun `sem OCR ainda nao decide entre rotulo e prato`() {
        val decision = ContextRouter.route(null, null)
        assertNull(decision.track)
        assertTrue(decision.reason.contains("OCR"))
    }

    /** O roteamento tem que concordar com o gabarito das cenas — senão a demo roteia errado. */
    @Test
    fun `cenas de rotulo roteiam para rotulo pelo proprio texto`() {
        MockScenes.labels
            .filterNot { it.id == "embalagem_promocional" }
            .forEach { scene ->
                assertEquals(
                    "Cena ${scene.id} deveria rotear para LABEL",
                    AnalysisTrack.LABEL,
                    ContextRouter.route(null, scene.labelText).track
                )
            }
    }

    @Test
    fun `cenas de barcode roteiam para produto pelo EAN`() {
        MockScenes.barcodes.forEach { scene ->
            assertEquals(
                "Cena ${scene.id} deveria rotear para BARCODE",
                AnalysisTrack.BARCODE,
                ContextRouter.route(scene.ean, null).track
            )
        }
    }
}
