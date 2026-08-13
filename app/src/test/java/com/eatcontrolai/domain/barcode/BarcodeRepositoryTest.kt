package com.eatcontrolai.domain.barcode

import com.eatcontrolai.core.model.Allergen
import com.eatcontrolai.core.model.EvidenceType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class BarcodeRepositoryTest {

    private val repository = BarcodeRepository()

    @Test
    fun findByEan_returnsProduct_whenEanExists() {
        val product = repository.findByEan("7891000100011")

        assertNotNull(product)
        assertEquals("Iogurte Natural Integral", product?.productName)
        assertEquals("Marca Demo", product?.brand)
    }

    @Test
    fun findByEan_returnsNull_whenEanDoesNotExist() {
        val product = repository.findByEan("9999999999999")

        assertNull(product)
    }

    @Test
    fun toEvidence_createsBarcodeEvidence_withCorrectTypeAndRank() {
        val product = repository.findByEan("7891000200022")!!
        val evidence = repository.toEvidence(product)

        assertEquals(EvidenceType.BARCODE_DATABASE, evidence.type)
        assertEquals(3, evidence.type.rank)
        assertEquals(1, evidence.claims.size)
        assertEquals(Allergen.MILK, evidence.claims.first().allergen)
    }
}
