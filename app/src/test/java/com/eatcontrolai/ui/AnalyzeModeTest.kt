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

    @Test
    fun `automatico nao finge rotear menu ou prato`() {
        assertFalse(MockScenes.automatic.any { it.kind == SceneKind.MENU })
        assertFalse(MockScenes.automatic.any { it.kind == SceneKind.PLATE })
    }
}
