package com.eatcontrolai.domain.label

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Protege o vocabulário compartilhado entre cardápio e rótulo.
 *
 * O radical existe para cobrir a flexão do português. Se alguém trocar `FRIT` por `FRITO`, a batata
 * "frita" some do texto observado — e o achado de fritura do rule pack some junto, sem nenhum erro
 * aparente. É esse silêncio que estes casos previnem.
 */
class FoodTermsTest {

    @Test
    fun `reconhece flexoes do mesmo radical`() {
        listOf("Batata frita", "Batatas fritas", "Fritura de peixe", "FRITO NA HORA").forEach {
            assertTrue("Deveria observar fritura em '$it'", "frito" in FoodTerms.observedIn(it))
        }
    }

    @Test
    fun `ignora acento e caixa`() {
        assertEquals(listOf("cremoso", "molho"), FoodTerms.observedIn("Môlho cremôso"))
    }

    @Test
    fun `texto em branco nao observa nada`() {
        assertTrue(FoodTerms.observedIn("   ").isEmpty())
    }

    @Test
    fun `nao inventa termo ausente`() {
        assertTrue(FoodTerms.observedIn("Arroz integral com feijão").isEmpty())
    }

    @Test
    fun `ordem e estavel e sem repeticao`() {
        val observed = FoodTerms.observedIn("Frango grelhado, molho, salada e mais molho")
        assertEquals(listOf("grelhado", "molho", "salada"), observed)
    }
}
