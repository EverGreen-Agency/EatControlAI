package com.eatcontrolai.ui

import com.eatcontrolai.glasses.MockScenes
import com.eatcontrolai.glasses.SceneKind
import com.eatcontrolai.orchestration.AnalysisTrack
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AnalyzeModeTest {

    @Test
    fun `todos os modos expostos possuem trilha implementada`() {
        assertTrue(AnalyzeMode.entries.all(AnalyzeMode::ready))
        assertEquals(AnalysisTrack.MENU, AnalyzeMode.MENU.track)
        assertEquals(AnalysisTrack.PLATE, AnalyzeMode.PLATE.track)
    }

    @Test
    fun `mock oferece cenas proprias para menu e prato`() {
        assertTrue(MockScenes.forKind(SceneKind.MENU).isNotEmpty())
        assertTrue(MockScenes.forKind(SceneKind.PLATE).isNotEmpty())
    }

    /**
     * O modo automático cobre as quatro trilhas.
     *
     * Antes, `automatic` era só rótulo e código de barras — restringir as cenas escondia o
     * comportamento que o modo existe para demonstrar. Agora a cascata decide, e cada trilha tem a
     * sua régua: rótulo e produto passam pelo motor determinístico; cardápio e prato passam por
     * guardrails assistivos, que nunca afirmam compatibilidade.
     */
    @Test
    fun `automatico cobre as quatro trilhas`() {
        SceneKind.entries.forEach { kind ->
            assertTrue(
                "Modo automático deveria enxergar cenas de $kind",
                MockScenes.automatic.any { it.kind == kind }
            )
        }
    }
}
