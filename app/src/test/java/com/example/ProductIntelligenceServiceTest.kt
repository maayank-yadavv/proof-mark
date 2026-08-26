package com.example

import com.example.data.ai.ProductIntelligenceService
import com.example.data.local.entities.DeclarationEntity
import com.example.data.local.entities.ProductEntity
import com.example.data.models.ProductCategory
import com.example.data.models.ReliabilityLevel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ProductIntelligenceServiceTest {

    private val sampleProduct = ProductEntity(
        id = "prod_fortune_1",
        name = "Fortune Refined Mustard Oil 1L",
        brand = "Fortune Oils",
        category = ProductCategory.FOOD_BEVERAGES,
        barcode = "8901030800012",
        pdpAreaCm2 = 180.0
    )

    @Test
    fun testProductIntelligenceResolutionForBarcode() {
        val declarations = listOf(
            DeclarationEntity(
                id = "d1",
                inspectionId = "insp_test_101",
                fieldKey = "MRP",
                fieldName = "Maximum Retail Price",
                extractedValue = "MRP ₹185.00 (inclusive of all taxes)"
            ),
            DeclarationEntity(
                id = "d2",
                inspectionId = "insp_test_101",
                fieldKey = "NET_QTY",
                fieldName = "Net Quantity",
                extractedValue = "1 Litre"
            )
        )

        val report = ProductIntelligenceService.resolveProductIntelligence(
            queryOrBarcode = "8901030800012",
            knownProducts = listOf(sampleProduct),
            scannedDeclarations = declarations
        )

        assertNotNull(report)
        assertEquals("8901030800012", report.query)
        assertEquals(ReliabilityLevel.HIGH_VERIFIED, report.overallConfidenceLevel)
        assertTrue(report.confidenceScorePercent >= 90)
        assertNotNull(report.pricing)
        assertNotNull(report.manufacturer)
        assertNotNull(report.composition)
        assertTrue(report.certifications.isNotEmpty())
        assertTrue(report.sources.isNotEmpty())
    }

    @Test
    fun testProductIntelligenceResolutionWithMissingDeclarations() {
        val report = ProductIntelligenceService.resolveProductIntelligence(
            queryOrBarcode = "Garam Masala 100g",
            knownProducts = emptyList(),
            scannedDeclarations = emptyList()
        )

        assertNotNull(report)
        assertNotNull(report.primaryMatch)
        assertTrue(report.scanComparison.isNotEmpty())
    }
}
