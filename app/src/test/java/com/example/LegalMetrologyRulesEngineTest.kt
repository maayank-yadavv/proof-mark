package com.example

import com.example.data.local.entities.DeclarationEntity
import com.example.data.local.entities.ProductEntity
import com.example.data.models.ComplianceStatus
import com.example.data.models.ProductCategory
import com.example.data.rules.LegalMetrologyRulesEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class LegalMetrologyRulesEngineTest {

    private val sampleProduct = ProductEntity(
        id = "prod_test_1",
        name = "Royal Shahi Garam Masala",
        brand = "Royal Spices Ltd",
        category = ProductCategory.FOOD_BEVERAGES,
        pdpAreaCm2 = 140.0
    )

    @Test
    fun testMrpMissingTaxesQualificationFails() {
        val declarations = listOf(
            DeclarationEntity(
                id = "d1",
                inspectionId = "insp_1",
                fieldKey = "MRP",
                fieldName = "Maximum Retail Price",
                extractedValue = "MRP Rs. 68.00" // Missing '(incl. of all taxes)'
            )
        )

        val checks = LegalMetrologyRulesEngine.evaluateCompliance(
            inspectionId = "insp_1",
            product = sampleProduct,
            declarations = declarations,
            activeRules = LegalMetrologyRulesEngine.DEFAULT_RULES
        )

        val mrpCheck = checks.find { it.ruleCode == "LM-PC-6-1-E" }
        assertTrue(mrpCheck != null)
        assertEquals(ComplianceStatus.POTENTIAL_NON_COMPLIANCE, mrpCheck?.status)
    }

    @Test
    fun testMrpWithTaxesPasses() {
        val declarations = listOf(
            DeclarationEntity(
                id = "d1",
                inspectionId = "insp_1",
                fieldKey = "MRP",
                fieldName = "Maximum Retail Price",
                extractedValue = "MRP ₹ 68.00 (inclusive of all taxes)"
            )
        )

        val checks = LegalMetrologyRulesEngine.evaluateCompliance(
            inspectionId = "insp_1",
            product = sampleProduct,
            declarations = declarations,
            activeRules = LegalMetrologyRulesEngine.DEFAULT_RULES
        )

        val mrpCheck = checks.find { it.ruleCode == "LM-PC-6-1-E" }
        assertTrue(mrpCheck != null)
        assertEquals(ComplianceStatus.PASS, mrpCheck?.status)
    }

    @Test
    fun testMissingCountryOfOriginFails() {
        val declarations = listOf(
            DeclarationEntity(
                id = "d1",
                inspectionId = "insp_1",
                fieldKey = "COUNTRY_OF_ORIGIN",
                fieldName = "Country of Origin",
                extractedValue = "" // Omitted
            )
        )

        val checks = LegalMetrologyRulesEngine.evaluateCompliance(
            inspectionId = "insp_1",
            product = sampleProduct,
            declarations = declarations,
            activeRules = LegalMetrologyRulesEngine.DEFAULT_RULES
        )

        val originCheck = checks.find { it.ruleCode == "LM-PC-6-10" }
        assertTrue(originCheck != null)
        assertEquals(ComplianceStatus.POTENTIAL_NON_COMPLIANCE, originCheck?.status)
    }

    @Test
    fun testFontHeightCalculationForLargePDP() {
        // Table 1: Area > 200 to <= 360 cm2 requires min 4.0mm
        val largeProduct = sampleProduct.copy(pdpAreaCm2 = 320.0)
        val declarations = listOf(
            DeclarationEntity(
                id = "d1",
                inspectionId = "insp_1",
                fieldKey = "PDP_FONT_HEIGHT",
                fieldName = "PDP Font Height",
                extractedValue = "2.4 mm" // Violates 4.0mm minimum
            )
        )

        val checks = LegalMetrologyRulesEngine.evaluateCompliance(
            inspectionId = "insp_1",
            product = largeProduct,
            declarations = declarations,
            activeRules = LegalMetrologyRulesEngine.DEFAULT_RULES
        )

        val fontCheck = checks.find { it.ruleCode == "LM-PC-7-PDP" }
        assertTrue(fontCheck != null)
        assertEquals(ComplianceStatus.POTENTIAL_NON_COMPLIANCE, fontCheck?.status)
    }
}
