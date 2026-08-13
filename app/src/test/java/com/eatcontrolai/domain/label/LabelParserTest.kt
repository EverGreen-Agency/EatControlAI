package com.eatcontrolai.domain.label

import com.eatcontrolai.core.model.Allergen
import com.eatcontrolai.core.model.ClaimPolarity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class LabelParserTest {

    private fun polarityOf(text: String, allergen: Allergen): ClaimPolarity? =
        LabelParser.parse(text).firstOrNull { it.allergen == allergen }?.polarity

    @Test
    fun `declaracao direta vira CONTAINS`() {
        assertEquals(ClaimPolarity.CONTAINS, polarityOf("ALÉRGICOS: CONTÉM LEITE.", Allergen.MILK))
    }

    @Test
    fun `dois pontos depois de CONTEM nao quebra o casamento`() {
        assertEquals(ClaimPolarity.CONTAINS, polarityOf("ALÉRGICOS: CONTÉM: LEITE E SOJA.", Allergen.MILK))
        assertEquals(ClaimPolarity.CONTAINS, polarityOf("ALÉRGICOS: CONTÉM: LEITE E SOJA.", Allergen.SOY))
    }

    @Test
    fun `negacao nao vira presenca`() {
        assertEquals(ClaimPolarity.FREE_OF, polarityOf("NÃO CONTÉM LEITE.", Allergen.MILK))
    }

    @Test
    fun `pode conter vira MAY_CONTAIN`() {
        assertEquals(ClaimPolarity.MAY_CONTAIN, polarityOf("PODE CONTER AMENDOIM.", Allergen.PEANUT))
    }

    @Test
    fun `lista de ingredientes conta como declaracao`() {
        val text = "INGREDIENTES: FARINHA DE TRIGO, AÇÚCAR, LEITE EM PÓ INTEGRAL."
        assertEquals(ClaimPolarity.CONTAINS, polarityOf(text, Allergen.MILK))
        assertEquals(ClaimPolarity.CONTAINS, polarityOf(text, Allergen.GLUTEN))
    }

    @Test
    fun `escopo termina na frase e nao vaza para o proximo alergeno`() {
        val text = "CONTÉM LEITE. FABRICADO EM LINHA QUE TAMBÉM PROCESSA AMENDOIM."
        assertEquals(ClaimPolarity.CONTAINS, polarityOf(text, Allergen.MILK))
        assertEquals(ClaimPolarity.MAY_CONTAIN, polarityOf(text, Allergen.PEANUT))
    }

    @Test
    fun `afirmacao mais restritiva vence quando o rotulo se contradiz`() {
        val text = "IOGURTE ZERO LACTOSE. INGREDIENTES: LEITE DESNATADO. ALÉRGICOS: CONTÉM LEITE."
        assertEquals(ClaimPolarity.CONTAINS, polarityOf(text, Allergen.MILK))
    }

    @Test
    fun `zero lactose nao prova ausencia de proteina do leite`() {
        assertNull(polarityOf("SEM LACTOSE.", Allergen.MILK))
    }

    @Test
    fun `sem leite prova ausencia de leite`() {
        assertEquals(ClaimPolarity.FREE_OF, polarityOf("SEM LEITE.", Allergen.MILK))
    }

    @Test
    fun `texto sem marcador nao gera afirmacao`() {
        assertTrue(LabelParser.parse("NOVO! LEVE 3 PAGUE 2.").isEmpty())
    }

    @Test
    fun `hifenizacao de quebra de linha e recomposta`() {
        assertEquals(ClaimPolarity.CONTAINS, polarityOf("ALÉRGICOS: CONTÉM AMEN-\nDOIM.", Allergen.PEANUT))
    }

    @Test
    fun `fronteira de palavra evita casamento dentro de outra palavra`() {
        // "OVOLACTOVEGETARIANO" não deve disparar o alérgeno OVO.
        assertNull(polarityOf("INGREDIENTES: PRODUTO OVOLACTOVEGETARIANO.", Allergen.EGG))
    }
}
