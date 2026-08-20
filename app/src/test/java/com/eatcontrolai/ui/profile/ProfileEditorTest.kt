package com.eatcontrolai.ui.profile

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ProfileEditorTest {

    @Test
    fun `aceita ponto e virgula decimal em meta positiva`() {
        assertEquals(90.5, parsePositiveGoal("90.5")!!, 0.0)
        assertEquals(90.5, parsePositiveGoal(" 90,5 ")!!, 0.0)
    }

    @Test
    fun `campo vazio permanece meta nao configurada`() {
        assertNull(parsePositiveGoal(""))
        assertNull(parsePositiveGoal("   "))
    }

    @Test
    fun `rejeita zero negativos infinitos e texto`() {
        assertNull(parsePositiveGoal("0"))
        assertNull(parsePositiveGoal("-1"))
        assertNull(parsePositiveGoal("Infinity"))
        assertNull(parsePositiveGoal("NaN"))
        assertNull(parsePositiveGoal("cem"))
    }
}
