package com.eatcontrolai.domain.barcode

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * A composição entre catálogo de demonstração e base remota.
 *
 * O que estes casos protegem é a ordem e o comportamento na falha. Rede indisponível não pode
 * derrubar a análise: ela precisa devolver "não sei quem é este produto" para o orquestrador cair no
 * OCR do rótulo — um caminho pior, porém honesto.
 */
class ProductLookupTest {

    private class FakeSource(
        private val answer: ProductBarcodeData? = null,
        private val failure: Throwable? = null
    ) : ProductDataSource {
        var calls = 0
            private set

        override suspend fun lookup(ean: String): ProductBarcodeData? {
            calls++
            failure?.let { throw it }
            return answer
        }
    }

    private val remoteProduct = ProductBarcodeData(
        ean = "7899999999999",
        productName = "Produto Real",
        brand = "Marca Real",
        ingredientsText = "INGREDIENTES: AVEIA.",
        source = OpenFoodFactsMapper.SOURCE
    )

    @Test
    fun `catalogo de demonstracao responde antes da rede`() = runTest {
        val remote = FakeSource(remoteProduct)
        val repository = BarcodeRepository(remote)

        val product = repository.lookup("7891000100103")

        assertEquals("Iogurte Natural Integral", product?.productName)
        assertEquals("nenhuma chamada de rede deveria ocorrer", 0, remote.calls)
    }

    @Test
    fun `EAN fora do catalogo vai para a base remota`() = runTest {
        val repository = BarcodeRepository(FakeSource(remoteProduct))

        val product = repository.lookup("7899999999999")

        assertEquals("Produto Real", product?.productName)
        assertEquals(OpenFoodFactsMapper.SOURCE, product?.source)
    }

    @Test
    fun `falha de rede devolve nulo em vez de propagar`() = runTest {
        val repository = BarcodeRepository(FakeSource(failure = java.io.IOException("sem rede")))

        assertNull(repository.lookup("7899999999999"))
    }

    @Test
    fun `sem fonte remota o repositorio continua respondendo pelo catalogo`() = runTest {
        val repository = BarcodeRepository()

        assertEquals("Iogurte Natural Integral", repository.lookup("7891000100103")?.productName)
        assertNull(repository.lookup("7899999999999"))
    }

    @Test
    fun `resposta remota e reaproveitada em vez de reconsultada`() = runTest {
        val remote = FakeSource(remoteProduct)
        val repository = BarcodeRepository(remote)

        repository.lookup("7899999999999")
        repository.lookup("7899999999999")

        assertEquals(1, remote.calls)
    }

    @Test
    fun `ausencia tambem e lembrada para nao repetir a consulta`() = runTest {
        val remote = FakeSource(answer = null)
        val repository = BarcodeRepository(remote)

        assertNull(repository.lookup("7899999999999"))
        assertNull(repository.lookup("7899999999999"))

        assertEquals(1, remote.calls)
    }

    @Test
    fun `espaco em volta do codigo nao impede a busca`() = runTest {
        val repository = BarcodeRepository()

        assertTrue(repository.lookup("  7891000100103 ") != null)
    }
}
