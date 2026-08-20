package com.eatcontrolai.data

import com.eatcontrolai.core.model.UserProfile
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class ProfilePolicyTest {

    @Test
    fun `perfil vazio exige onboarding`() {
        assertEquals(
            ProfileState.NeedsOnboarding,
            ProfilePolicy.stateFor(UserProfile(id = ""))
        )
    }

    @Test
    fun `perfil nomeado fica pronto sem depender de metas`() {
        val profile = UserProfile(id = "u1", displayName = "Ana")
        val state = ProfilePolicy.stateFor(profile)

        assertTrue(state is ProfileState.Ready)
        assertEquals(profile, (state as ProfileState.Ready).profile)
    }

    @Test
    fun `nome composto apenas por espacos nao conclui onboarding`() {
        assertEquals(
            ProfileState.NeedsOnboarding,
            ProfilePolicy.stateFor(UserProfile(id = "u1", displayName = "   "))
        )
    }

    @Test
    fun `persona legada de demonstracao e descartada`() {
        assertNull(
            ProfilePolicy.normalizeStored(
                UserProfile(id = "demo-joao", displayName = "João")
            )
        )
    }

    @Test
    fun `perfil real armazenado e preservado`() {
        val profile = UserProfile(id = "real", displayName = "Pessoa")
        assertSame(profile, ProfilePolicy.normalizeStored(profile))
    }
}
