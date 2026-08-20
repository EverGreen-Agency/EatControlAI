package com.eatcontrolai.domain.routing

import com.eatcontrolai.glasses.MockScenes
import com.eatcontrolai.glasses.SceneKind
import com.eatcontrolai.orchestration.AnalysisTrack
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
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
    fun `frente promocional cai para prato`() {
        // Pouco texto e sem código de barras: a cascata trata como prato, que é o degrau mais caro.
        val decision = ContextRouter.route(null, "NOVO! LEVE 3 PAGUE 2")
        assertEquals(AnalysisTrack.PLATE, decision.track)
        assertTrue(decision is ContextRouter.Decision.Plate)
    }

    @Test
    fun `linhas com preco levam para cardapio`() {
        val cardapio = """
            PRATOS PRINCIPAIS
            FRANGO GRELHADO ........ R$ 42,00
            MASSA AO MOLHO CREMOSO .. R$ 38,00
        """.trimIndent()
        val decision = ContextRouter.route(null, cardapio)
        assertEquals(AnalysisTrack.MENU, decision.track)
        assertTrue("razão deveria citar preço", decision.reason.contains("preço"))
    }

    /**
     * Um cardápio brasileiro às vezes traz nota de alérgeno. Perder a análise de alérgeno é o erro
     * perigoso; perder a estrutura de opções é só o erro chato — por isso rótulo tem precedência.
     */
    @Test
    fun `marcador de alergeno vence preco`() {
        val misto = """
            FRANGO GRELHADO ........ R$ 42,00
            MASSA CREMOSA ........... R$ 38,00
            ALÉRGICOS: CONTÉM LEITE.
        """.trimIndent()
        assertEquals(AnalysisTrack.LABEL, ContextRouter.route(null, misto).track)
    }

    @Test
    fun `uma linha com preco nao basta para cardapio`() {
        val decision = ContextRouter.route(null, "SANDUÍCHE NATURAL ... R$ 15,00")
        assertNotEquals(AnalysisTrack.MENU, decision.track)
    }

    /** Toda cena do modo automático precisa cair na trilha que o gabarito espera. */
    @Test
    fun `cenas do modo automatico roteiam para a trilha certa`() {
        MockScenes.automatic.forEach { scene ->
            val ean = scene.ean
            val texto = scene.labelText.ifBlank { null }
            val esperado = when (scene.kind) {
                SceneKind.BARCODE -> AnalysisTrack.BARCODE
                SceneKind.LABEL -> if (scene.id == "embalagem_promocional") AnalysisTrack.PLATE
                else AnalysisTrack.LABEL
                SceneKind.MENU -> AnalysisTrack.MENU
                SceneKind.PLATE -> AnalysisTrack.PLATE
            }
            assertEquals(
                "Cena ${scene.id} roteou errado",
                esperado,
                ContextRouter.route(ean, texto ?: "").track
            )
        }
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
