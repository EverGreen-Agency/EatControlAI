package com.eatcontrolai.domain.menu

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MenuParserTest {

    @Test
    fun `estrutura opcoes secoes descricoes e precos sem inventar nutrientes`() {
        val result = MenuParser.parse(
            """
                CARDÁPIO
                PRATOS PRINCIPAIS
                FRANGO GRELHADO ........ R$ 42,00
                Arroz, feijão e salada da casa
                MASSA AO MOLHO CREMOSO .. R$ 38,00
                Penne e queijo
                SOBREMESAS
                BOLO DE CHOCOLATE ....... R$ 18,00
            """.trimIndent()
        )

        assertEquals(3, result.options.size)
        val chicken = result.options[0]
        assertEquals("PRATOS PRINCIPAIS", chicken.section)
        assertEquals("FRANGO GRELHADO", chicken.name)
        assertEquals("R$ 42,00", chicken.priceText)
        assertEquals("Arroz, feijão e salada da casa", chicken.description)
        assertEquals(listOf("grelhado", "salada"), chicken.observedTerms)
        assertEquals(2, chicken.sourceLines.size)
        assertTrue(result.warnings.isEmpty())
    }

    @Test
    fun `preco em linha separada continua sendo preco textual`() {
        val result = MenuParser.parse(
            """
                ENTRADAS
                BRUSCHETTA
                Tomate e manjericão
                R$ 24,00
            """.trimIndent()
        )

        val option = result.options.single()
        assertEquals("BRUSCHETTA", option.name)
        assertEquals("Tomate e manjericão", option.description)
        assertEquals("R$ 24,00", option.priceText)
        assertTrue(option.sourceLines.last().startsWith("R$"))
    }

    @Test
    fun `opcoes sem preco permanecem revisaveis e geram aviso`() {
        val result = MenuParser.parse(
            """
                SALADAS
                SALADA VERDE
                Folhas e tomate
                SALADA DE LEGUMES
                Legumes da estação
            """.trimIndent()
        )

        assertEquals(2, result.options.size)
        assertTrue(result.options.all { it.priceText == null })
        assertTrue(result.warnings.any { it.contains("preço") })
    }

    @Test
    fun `texto vazio nao cria opcao`() {
        val result = MenuParser.parse("  \n\t")

        assertTrue(result.options.isEmpty())
        assertEquals(1, result.warnings.size)
        assertNull(result.selectedOption)
        assertFalse(result.registered)
    }

    @Test
    fun `somente titulos genericos nao viram comida`() {
        val result = MenuParser.parse("CARDÁPIO\nMENU\nRESTAURANTE")

        assertTrue(result.options.isEmpty())
        assertTrue(result.warnings.any { it.contains("nenhuma opção", ignoreCase = true) })
    }

    @Test
    fun `selecao resolve apenas id existente`() {
        val parsed = MenuParser.parse("PRATOS\nPEIXE ASSADO R$ 45,00")
        val selected = parsed.copy(selectedOptionId = parsed.options.single().id)

        assertEquals("PEIXE ASSADO", selected.selectedOption?.name)
        assertNull(parsed.copy(selectedOptionId = "inexistente").selectedOption)
    }
}
