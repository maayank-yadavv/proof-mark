package com.example.data.rules

import com.example.data.local.entities.ComplianceCheckEntity
import com.example.data.local.entities.DeclarationEntity
import com.example.data.local.entities.ProductEntity
import com.example.data.local.entities.RuleEntity
import com.example.data.models.ComplianceStatus
import com.example.data.models.ProductCategory
import com.example.data.models.RuleSeverity
import java.util.UUID

object LegalMetrologyRulesEngine {

    const val ENGINE_VERSION = "LegalMetrology-RulesEngine-v3.4"

    val DEFAULT_RULES = listOf(
        RuleEntity(
            id = "rule_lm_6_1_a",
            ruleCode = "LM-PC-6-1-A",
            title = "Manufacturer / Packer / Importer Details",
            description = "The name and complete physical address of the manufacturer, packer or importer with PIN code must be clearly stated on the label.",
            category = null,
            legalSource = "Legal Metrology (Packaged Commodities) Rules, 2011",
            sectionReference = "Rule 6(1)(a) & Sec 36(1)",
            ruleVersion = "v3.2-2024",
            effectiveDate = "2024-01-01",
            severity = RuleSeverity.CRITICAL,
            isActive = true
        ),
        RuleEntity(
            id = "rule_lm_6_1_b",
            ruleCode = "LM-PC-6-1-B",
            title = "Generic / Common Name of Commodity",
            description = "The common or generic name of the commodity contained in the package must be prominently declared on the Principal Display Panel (PDP).",
            category = null,
            legalSource = "Legal Metrology (Packaged Commodities) Rules, 2011",
            sectionReference = "Rule 6(1)(b)",
            ruleVersion = "v3.2-2024",
            effectiveDate = "2024-01-01",
            severity = RuleSeverity.MAJOR,
            isActive = true
        ),
        RuleEntity(
            id = "rule_lm_6_1_c",
            ruleCode = "LM-PC-6-1-C",
            title = "Net Quantity in Standard SI Units",
            description = "Net quantity must be declared in standard metric units (g, kg, ml, l, m, N). Use of non-standard abbreviations like 'gms', 'kilos', 'cc' is prohibited.",
            category = null,
            legalSource = "Legal Metrology (Packaged Commodities) Rules, 2011",
            sectionReference = "Rule 6(1)(c) & Rule 12",
            ruleVersion = "v3.2-2024",
            effectiveDate = "2024-01-01",
            severity = RuleSeverity.CRITICAL,
            isActive = true
        ),
        RuleEntity(
            id = "rule_lm_6_1_d",
            ruleCode = "LM-PC-6-1-D",
            title = "Month & Year of Manufacture / Packing",
            description = "The month and year in which the commodity is manufactured or pre-packed must be clearly indicated (e.g., '08/2026' or 'Aug 2026').",
            category = null,
            legalSource = "Legal Metrology (Packaged Commodities) Rules, 2011",
            sectionReference = "Rule 6(1)(d)",
            ruleVersion = "v3.2-2024",
            effectiveDate = "2024-01-01",
            severity = RuleSeverity.MAJOR,
            isActive = true
        ),
        RuleEntity(
            id = "rule_lm_6_1_da",
            ruleCode = "LM-PC-6-1-DA",
            title = "Mandatory Unit Sale Price (USP)",
            description = "Declaration of Unit Sale Price in ₹ per gram/ml or ₹ per 100g/100ml is mandatory for all pre-packaged commodities under 2022 amendments.",
            category = null,
            legalSource = "Legal Metrology (Packaged Commodities) Amendment Rules, 2022",
            sectionReference = "Rule 6(1)(da)",
            ruleVersion = "v2.0-2022",
            effectiveDate = "2022-12-01",
            severity = RuleSeverity.MAJOR,
            isActive = true
        ),
        RuleEntity(
            id = "rule_lm_6_1_e",
            ruleCode = "LM-PC-6-1-E",
            title = "Maximum Retail Price (MRP) Format",
            description = "MRP must be declared with unambiguous inclusion of all taxes: 'MRP Rs. XX.XX (incl. of all taxes)' or 'MRP ₹ XX.XX inclusive of all taxes'.",
            category = null,
            legalSource = "Legal Metrology (Packaged Commodities) Rules, 2011",
            sectionReference = "Rule 6(1)(e) & Sec 36(2)",
            ruleVersion = "v3.2-2024",
            effectiveDate = "2024-01-01",
            severity = RuleSeverity.CRITICAL,
            isActive = true
        ),
        RuleEntity(
            id = "rule_lm_6_1_f",
            ruleCode = "LM-PC-6-1-F",
            title = "Consumer Care Contact Information",
            description = "Name, telephone number, email address and complete postal address of the person/office to contact for consumer grievances must be on the label.",
            category = null,
            legalSource = "Legal Metrology (Packaged Commodities) Rules, 2011",
            sectionReference = "Rule 6(1)(f)",
            ruleVersion = "v3.2-2024",
            effectiveDate = "2024-01-01",
            severity = RuleSeverity.CRITICAL,
            isActive = true
        ),
        RuleEntity(
            id = "rule_lm_6_10",
            ruleCode = "LM-PC-6-10",
            title = "Country of Origin Declaration",
            description = "Country of origin / manufacture must be clearly stated on all pre-packaged goods and digital marketplace listings.",
            category = null,
            legalSource = "Legal Metrology (Packaged Commodities) Rules, 2011",
            sectionReference = "Rule 6(10)",
            ruleVersion = "v3.0-2020",
            effectiveDate = "2020-01-01",
            severity = RuleSeverity.MAJOR,
            isActive = true
        ),
        RuleEntity(
            id = "rule_lm_7_pdp",
            ruleCode = "LM-PC-7-PDP",
            title = "Principal Display Panel Minimum Font Height",
            description = "Letter and numeral height on declarations must adhere to minimum statutory height based on PDP area (≥2.0mm for 50-200cm², ≥4.0mm for 200-1000cm²).",
            category = null,
            legalSource = "Legal Metrology (Packaged Commodities) Rules, 2011",
            sectionReference = "Rule 7 & Table 1",
            ruleVersion = "v3.2-2024",
            effectiveDate = "2024-01-01",
            severity = RuleSeverity.MINOR,
            isActive = true
        ),
        RuleEntity(
            id = "rule_lm_ecomm_opt",
            ruleCode = "LM-PC-ECOMM",
            title = "E-Commerce Mandatory Digital Declarations",
            description = "E-commerce entities displaying products must show MRP, Unit Sale Price, Expiry, Net Quantity, Country of Origin and Consumer Care on the listing page.",
            category = ProductCategory.ECOMMERCE_LISTING,
            legalSource = "Legal Metrology (Packaged Commodities) Rules - E-Commerce Provisions",
            sectionReference = "Rule 6(10) & Rule 6(1)(da)",
            ruleVersion = "v2.1-2023",
            effectiveDate = "2023-01-01",
            severity = RuleSeverity.MAJOR,
            isActive = true
        ),
        RuleEntity(
            id = "rule_lm_food_exp",
            ruleCode = "LM-PC-FOOD-18",
            title = "Best Before / Expiry Date on Perishable Commodities",
            description = "All pre-packaged food & beverage commodities must explicitly state Best Before / Expiry Date alongside FSSAI License Number.",
            category = ProductCategory.FOOD_BEVERAGES,
            legalSource = "Legal Metrology (Packaged Commodities) Rules, 2011 & FSSAI 2020",
            sectionReference = "Rule 6(1)(d) Proviso",
            ruleVersion = "v3.4-2024",
            effectiveDate = "2024-01-01",
            severity = RuleSeverity.CRITICAL,
            isActive = true
        ),
        RuleEntity(
            id = "rule_lm_oil_temp",
            ruleCode = "LM-PC-OIL-13",
            title = "Edible Oil Volume at Standard Reference Temperature (30°C)",
            description = "Net volume of edible oils & vanaspati must be declared at standard reference temperature of 30°C alongside net weight equivalent in grams/kg.",
            category = ProductCategory.EDIBLE_OILS_GRAINS,
            legalSource = "Legal Metrology (Packaged Commodities) Rules, 2011",
            sectionReference = "Rule 13(2) & Schedule 2",
            ruleVersion = "v3.1-2022",
            effectiveDate = "2022-06-01",
            severity = RuleSeverity.MAJOR,
            isActive = true
        ),
        RuleEntity(
            id = "rule_lm_cosm_batch",
            ruleCode = "LM-PC-COSM-21",
            title = "Cosmetics Manufacturing Batch Number & Precautions",
            description = "Cosmetics & personal care packages must prominently declare Batch Number, Manufacturing License No., and allergen precautionary statements.",
            category = ProductCategory.COSMETICS_PERSONAL_CARE,
            legalSource = "Legal Metrology (Packaged Commodities) Rules & Drugs Rules",
            sectionReference = "Rule 6(1) & Sec 18",
            ruleVersion = "v3.0-2021",
            effectiveDate = "2021-04-01",
            severity = RuleSeverity.MAJOR,
            isActive = true
        ),
        RuleEntity(
            id = "rule_lm_elec_bee",
            ruleCode = "LM-PC-ELEC-24",
            title = "Electrical Appliance Power Rating & BEE Star Label",
            description = "Pre-packaged electrical appliances must state Operating Voltage, Frequency, Power Rating, and BEE Energy Star Label on the Principal Display Panel.",
            category = ProductCategory.ELECTRONICS_APPLIANCES,
            legalSource = "Legal Metrology Rules & BEE Energy Conservation Regulations",
            sectionReference = "Rule 6(1) & Schedule 4",
            ruleVersion = "v3.3-2023",
            effectiveDate = "2023-09-01",
            severity = RuleSeverity.MAJOR,
            isActive = true
        ),
        RuleEntity(
            id = "rule_lm_chem_pict",
            ruleCode = "LM-PC-CHEM-29",
            title = "Hazardous Chemical Safety Pictograms & Net Mass",
            description = "Paints, pesticides, and household chemicals must carry hazard warning symbols, safety precautions, and net mass/volume in standard metric units.",
            category = ProductCategory.CHEMICALS_PESTICIDES,
            legalSource = "Legal Metrology Rules & Insecticides Act 1968",
            sectionReference = "Rule 6(1) & Sec 36",
            ruleVersion = "v3.2-2022",
            effectiveDate = "2022-01-01",
            severity = RuleSeverity.CRITICAL,
            isActive = true
        ),
        RuleEntity(
            id = "rule_lm_gen_multi",
            ruleCode = "LM-PC-GEN-02",
            title = "Multi-Piece Package Count & Individual Unit Price",
            description = "Packages containing multiple individual items must state total piece count, net quantity of each piece, and per-unit sale price.",
            category = ProductCategory.GENERAL_MERCHANDISE,
            legalSource = "Legal Metrology (Packaged Commodities) Rules, 2011",
            sectionReference = "Rule 6(2) & Rule 17",
            ruleVersion = "v3.2-2024",
            effectiveDate = "2024-01-01",
            severity = RuleSeverity.MAJOR,
            isActive = true
        )
    )

    fun evaluateCompliance(
        inspectionId: String,
        product: ProductEntity,
        declarations: List<DeclarationEntity>,
        activeRules: List<RuleEntity>
    ): List<ComplianceCheckEntity> {
        val declMap = declarations.associateBy { it.fieldKey.uppercase() }

        return activeRules.map { rule ->
            when (rule.ruleCode) {
                "LM-PC-6-1-A" -> evaluateManufacturerDetails(inspectionId, rule, declMap["MANUFACTURER_ADDRESS"], declMap["MANUFACTURER_NAME"])
                "LM-PC-6-1-B" -> evaluateGenericName(inspectionId, rule, declMap["PRODUCT_NAME"], product)
                "LM-PC-6-1-C" -> evaluateNetQuantity(inspectionId, rule, declMap["NET_QUANTITY"])
                "LM-PC-6-1-D" -> evaluateMfgDate(inspectionId, rule, declMap["DATE_OF_MANUFACTURE"])
                "LM-PC-6-1-DA" -> evaluateUnitSalePrice(inspectionId, rule, declMap["UNIT_SALE_PRICE"], declMap["NET_QUANTITY"], declMap["MRP"])
                "LM-PC-6-1-E" -> evaluateMrp(inspectionId, rule, declMap["MRP"])
                "LM-PC-6-1-F" -> evaluateConsumerCare(inspectionId, rule, declMap["CONSUMER_CARE_CONTACT"])
                "LM-PC-6-10" -> evaluateCountryOfOrigin(inspectionId, rule, declMap["COUNTRY_OF_ORIGIN"])
                "LM-PC-7-PDP" -> evaluatePdpFont(inspectionId, rule, product.pdpAreaCm2, declMap["PDP_FONT_HEIGHT"])
                "LM-PC-ECOMM" -> evaluateEcomm(inspectionId, rule, product.category, declMap)
                else -> evaluateGenericRule(inspectionId, rule, declMap[rule.ruleCode])
            }
        }
    }

    private fun evaluateManufacturerDetails(
        inspectionId: String,
        rule: RuleEntity,
        addressDecl: DeclarationEntity?,
        nameDecl: DeclarationEntity?
    ): ComplianceCheckEntity {
        val addrVal = addressDecl?.correctedValue ?: addressDecl?.extractedValue ?: ""
        val nameVal = nameDecl?.correctedValue ?: nameDecl?.extractedValue ?: ""

        val combined = "$nameVal $addrVal".trim()
        val conf = addressDecl?.confidence ?: nameDecl?.confidence ?: 0.5f

        if (combined.isBlank() || combined.contains("Not Provided", ignoreCase = true) || combined.length < 5) {
            return ComplianceCheckEntity(
                id = UUID.randomUUID().toString(),
                inspectionId = inspectionId,
                ruleId = rule.id,
                ruleCode = rule.ruleCode,
                ruleTitle = rule.title,
                ruleVersion = rule.ruleVersion,
                severity = rule.severity,
                status = ComplianceStatus.POTENTIAL_NON_COMPLIANCE,
                findingMessage = "Violation: Name or complete physical address of manufacturer/packer/importer is missing from package.",
                legalSection = rule.sectionReference,
                evidenceDeclarationKey = "MANUFACTURER_ADDRESS",
                evidenceConfidence = conf
            )
        }

        val hasPin = combined.contains(Regex("""\b\d{6}\b""")) || combined.contains("PIN", ignoreCase = true)
        val hasCity = combined.contains(Regex("""(Plot|Street|Road|Nagar|Industrial|Phase|Dist|State|City|Delhi|Mumbai|Bengaluru|Chennai|Kolkata|Pune|Hyderabad|Haryana|Gujarat|India)""", RegexOption.IGNORE_CASE))

        return if (hasPin || hasCity) {
            ComplianceCheckEntity(
                id = UUID.randomUUID().toString(),
                inspectionId = inspectionId,
                ruleId = rule.id,
                ruleCode = rule.ruleCode,
                ruleTitle = rule.title,
                ruleVersion = rule.ruleVersion,
                severity = rule.severity,
                status = ComplianceStatus.PASS,
                findingMessage = "Satisfied: Complete manufacturer/packer address with identifier detected ($combined).",
                legalSection = rule.sectionReference,
                evidenceDeclarationKey = "MANUFACTURER_ADDRESS",
                evidenceConfidence = conf
            )
        } else {
            ComplianceCheckEntity(
                id = UUID.randomUUID().toString(),
                inspectionId = inspectionId,
                ruleId = rule.id,
                ruleCode = rule.ruleCode,
                ruleTitle = rule.title,
                ruleVersion = rule.ruleVersion,
                severity = rule.severity,
                status = ComplianceStatus.REQUIRES_REVIEW,
                findingMessage = "Incomplete Address: Missing PIN code or state identifier in address ($combined). Officer verification needed.",
                legalSection = rule.sectionReference,
                evidenceDeclarationKey = "MANUFACTURER_ADDRESS",
                evidenceConfidence = conf
            )
        }
    }

    private fun evaluateGenericName(
        inspectionId: String,
        rule: RuleEntity,
        nameDecl: DeclarationEntity?,
        product: ProductEntity
    ): ComplianceCheckEntity {
        val value = nameDecl?.correctedValue ?: nameDecl?.extractedValue ?: product.name
        val conf = nameDecl?.confidence ?: 0.9f

        return if (value.isNotBlank() && !value.contains("Not Provided", ignoreCase = true) && value.length >= 3) {
            ComplianceCheckEntity(
                id = UUID.randomUUID().toString(),
                inspectionId = inspectionId,
                ruleId = rule.id,
                ruleCode = rule.ruleCode,
                ruleTitle = rule.title,
                ruleVersion = rule.ruleVersion,
                severity = rule.severity,
                status = ComplianceStatus.PASS,
                findingMessage = "Satisfied: Common or generic name '$value' clearly declared on display panel.",
                legalSection = rule.sectionReference,
                evidenceDeclarationKey = "PRODUCT_NAME",
                evidenceConfidence = conf
            )
        } else {
            ComplianceCheckEntity(
                id = UUID.randomUUID().toString(),
                inspectionId = inspectionId,
                ruleId = rule.id,
                ruleCode = rule.ruleCode,
                ruleTitle = rule.title,
                ruleVersion = rule.ruleVersion,
                severity = rule.severity,
                status = ComplianceStatus.POTENTIAL_NON_COMPLIANCE,
                findingMessage = "Violation: Generic/common name of commodity is missing or unreadable.",
                legalSection = rule.sectionReference,
                evidenceDeclarationKey = "PRODUCT_NAME",
                evidenceConfidence = 0.3f
            )
        }
    }

    private fun evaluateNetQuantity(
        inspectionId: String,
        rule: RuleEntity,
        netQtyDecl: DeclarationEntity?
    ): ComplianceCheckEntity {
        val value = netQtyDecl?.correctedValue ?: netQtyDecl?.extractedValue ?: ""
        val conf = netQtyDecl?.confidence ?: 0.5f

        if (value.isBlank() || value.contains("Not Provided", ignoreCase = true)) {
            return ComplianceCheckEntity(
                id = UUID.randomUUID().toString(),
                inspectionId = inspectionId,
                ruleId = rule.id,
                ruleCode = rule.ruleCode,
                ruleTitle = rule.title,
                ruleVersion = rule.ruleVersion,
                severity = rule.severity,
                status = ComplianceStatus.POTENTIAL_NON_COMPLIANCE,
                findingMessage = "Violation: Net Quantity declaration missing on package.",
                legalSection = rule.sectionReference,
                evidenceDeclarationKey = "NET_QUANTITY",
                evidenceConfidence = conf
            )
        }

        // Check for illegal non-standard abbreviations
        val illegalUnits = listOf("gms", "gm", "kilos", "kgs", "cc", "ltrs", "no.")
        val hasIllegal = illegalUnits.any { value.lowercase().contains(Regex("""\b$it\b""")) }

        val validMetricPattern = Regex("""\b(\d+(\.\d+)?)\s*(g|kg|ml|l|L|m|cm|mm|N|units|g\s*\(when packed\))\b""", RegexOption.IGNORE_CASE)
        val matchesValid = validMetricPattern.containsMatchIn(value)

        return if (hasIllegal) {
            ComplianceCheckEntity(
                id = UUID.randomUUID().toString(),
                inspectionId = inspectionId,
                ruleId = rule.id,
                ruleCode = rule.ruleCode,
                ruleTitle = rule.title,
                ruleVersion = rule.ruleVersion,
                severity = rule.severity,
                status = ComplianceStatus.POTENTIAL_NON_COMPLIANCE,
                findingMessage = "Non-Standard Unit: '$value' uses invalid symbol (e.g. 'gms'/'cc'). Metric standard requires 'g', 'kg', 'ml', 'l', or 'N'.",
                legalSection = rule.sectionReference,
                evidenceDeclarationKey = "NET_QUANTITY",
                evidenceConfidence = conf
            )
        } else if (matchesValid) {
            ComplianceCheckEntity(
                id = UUID.randomUUID().toString(),
                inspectionId = inspectionId,
                ruleId = rule.id,
                ruleCode = rule.ruleCode,
                ruleTitle = rule.title,
                ruleVersion = rule.ruleVersion,
                severity = rule.severity,
                status = ComplianceStatus.PASS,
                findingMessage = "Satisfied: Net quantity '$value' compliant with standard SI units.",
                legalSection = rule.sectionReference,
                evidenceDeclarationKey = "NET_QUANTITY",
                evidenceConfidence = conf
            )
        } else {
            ComplianceCheckEntity(
                id = UUID.randomUUID().toString(),
                inspectionId = inspectionId,
                ruleId = rule.id,
                ruleCode = rule.ruleCode,
                ruleTitle = rule.title,
                ruleVersion = rule.ruleVersion,
                severity = rule.severity,
                status = ComplianceStatus.REQUIRES_REVIEW,
                findingMessage = "Uncertain Net Qty format: '$value'. Requires manual check by inspector.",
                legalSection = rule.sectionReference,
                evidenceDeclarationKey = "NET_QUANTITY",
                evidenceConfidence = conf
            )
        }
    }

    private fun evaluateMfgDate(
        inspectionId: String,
        rule: RuleEntity,
        dateDecl: DeclarationEntity?
    ): ComplianceCheckEntity {
        val value = dateDecl?.correctedValue ?: dateDecl?.extractedValue ?: ""
        val conf = dateDecl?.confidence ?: 0.5f

        if (value.isBlank() || value.contains("Not Provided", ignoreCase = true)) {
            return ComplianceCheckEntity(
                id = UUID.randomUUID().toString(),
                inspectionId = inspectionId,
                ruleId = rule.id,
                ruleCode = rule.ruleCode,
                ruleTitle = rule.title,
                ruleVersion = rule.ruleVersion,
                severity = rule.severity,
                status = ComplianceStatus.POTENTIAL_NON_COMPLIANCE,
                findingMessage = "Violation: Month and Year of Manufacture/Packing/Import is missing from package.",
                legalSection = rule.sectionReference,
                evidenceDeclarationKey = "DATE_OF_MANUFACTURE",
                evidenceConfidence = conf
            )
        }

        val datePattern = Regex("""(0[1-9]|1[0-2])[\/\-\.\s](20\d\d|\d\d)|(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)[a-z]*[\s\.\,\/\-]*\s*(20\d\d|\d\d)""", RegexOption.IGNORE_CASE)
        val isValid = datePattern.containsMatchIn(value)

        return if (isValid) {
            ComplianceCheckEntity(
                id = UUID.randomUUID().toString(),
                inspectionId = inspectionId,
                ruleId = rule.id,
                ruleCode = rule.ruleCode,
                ruleTitle = rule.title,
                ruleVersion = rule.ruleVersion,
                severity = rule.severity,
                status = ComplianceStatus.PASS,
                findingMessage = "Satisfied: Month & Year of packing verified ($value).",
                legalSection = rule.sectionReference,
                evidenceDeclarationKey = "DATE_OF_MANUFACTURE",
                evidenceConfidence = conf
            )
        } else {
            ComplianceCheckEntity(
                id = UUID.randomUUID().toString(),
                inspectionId = inspectionId,
                ruleId = rule.id,
                ruleCode = rule.ruleCode,
                ruleTitle = rule.title,
                ruleVersion = rule.ruleVersion,
                severity = rule.severity,
                status = ComplianceStatus.REQUIRES_REVIEW,
                findingMessage = "Review Date Format: '$value' may lack explicit month/year notation.",
                legalSection = rule.sectionReference,
                evidenceDeclarationKey = "DATE_OF_MANUFACTURE",
                evidenceConfidence = conf
            )
        }
    }

    private fun evaluateUnitSalePrice(
        inspectionId: String,
        rule: RuleEntity,
        uspDecl: DeclarationEntity?,
        netQtyDecl: DeclarationEntity?,
        mrpDecl: DeclarationEntity?
    ): ComplianceCheckEntity {
        val value = uspDecl?.correctedValue ?: uspDecl?.extractedValue ?: ""
        val conf = uspDecl?.confidence ?: 0.6f

        if (value.isNotBlank() && (value.contains("₹") || value.contains("Rs", ignoreCase = true) || value.contains("/"))) {
            return ComplianceCheckEntity(
                id = UUID.randomUUID().toString(),
                inspectionId = inspectionId,
                ruleId = rule.id,
                ruleCode = rule.ruleCode,
                ruleTitle = rule.title,
                ruleVersion = rule.ruleVersion,
                severity = rule.severity,
                status = ComplianceStatus.PASS,
                findingMessage = "Satisfied: Unit Sale Price (USP) declared ($value) per 2022 statutory amendment.",
                legalSection = rule.sectionReference,
                evidenceDeclarationKey = "UNIT_SALE_PRICE",
                evidenceConfidence = conf
            )
        }

        // If missing but package is large
        val netQty = netQtyDecl?.extractedValue ?: ""
        val hasHighQty = netQty.contains("kg", ignoreCase = true) || netQty.contains("l", ignoreCase = true) || (netQty.filter { it.isDigit() }.toIntOrNull() ?: 0) > 100

        return if (hasHighQty) {
            ComplianceCheckEntity(
                id = UUID.randomUUID().toString(),
                inspectionId = inspectionId,
                ruleId = rule.id,
                ruleCode = rule.ruleCode,
                ruleTitle = rule.title,
                ruleVersion = rule.ruleVersion,
                severity = rule.severity,
                status = ComplianceStatus.POTENTIAL_NON_COMPLIANCE,
                findingMessage = "Mandatory USP Missing: Package net quantity exceeds threshold; Unit Sale Price (e.g. ₹/g, ₹/ml) is required.",
                legalSection = rule.sectionReference,
                evidenceDeclarationKey = "UNIT_SALE_PRICE",
                evidenceConfidence = 0.4f
            )
        } else {
            ComplianceCheckEntity(
                id = UUID.randomUUID().toString(),
                inspectionId = inspectionId,
                ruleId = rule.id,
                ruleCode = rule.ruleCode,
                ruleTitle = rule.title,
                ruleVersion = rule.ruleVersion,
                severity = rule.severity,
                status = ComplianceStatus.REQUIRES_REVIEW,
                findingMessage = "USP declaration not explicitly detected. Verify if exempt by package size (<10g or <10ml).",
                legalSection = rule.sectionReference,
                evidenceDeclarationKey = "UNIT_SALE_PRICE",
                evidenceConfidence = 0.5f
            )
        }
    }

    private fun evaluateMrp(
        inspectionId: String,
        rule: RuleEntity,
        mrpDecl: DeclarationEntity?
    ): ComplianceCheckEntity {
        val value = mrpDecl?.correctedValue ?: mrpDecl?.extractedValue ?: ""
        val conf = mrpDecl?.confidence ?: 0.5f

        if (value.isBlank() || value.contains("Not Provided", ignoreCase = true)) {
            return ComplianceCheckEntity(
                id = UUID.randomUUID().toString(),
                inspectionId = inspectionId,
                ruleId = rule.id,
                ruleCode = rule.ruleCode,
                ruleTitle = rule.title,
                ruleVersion = rule.ruleVersion,
                severity = rule.severity,
                status = ComplianceStatus.POTENTIAL_NON_COMPLIANCE,
                findingMessage = "Violation: Maximum Retail Price (MRP) declaration completely missing.",
                legalSection = rule.sectionReference,
                evidenceDeclarationKey = "MRP",
                evidenceConfidence = conf
            )
        }

        val hasTaxesText = value.contains("tax", ignoreCase = true) || value.contains("incl", ignoreCase = true)
        val hasPrice = value.contains(Regex("""\d+"""))

        return if (hasTaxesText && hasPrice) {
            ComplianceCheckEntity(
                id = UUID.randomUUID().toString(),
                inspectionId = inspectionId,
                ruleId = rule.id,
                ruleCode = rule.ruleCode,
                ruleTitle = rule.title,
                ruleVersion = rule.ruleVersion,
                severity = rule.severity,
                status = ComplianceStatus.PASS,
                findingMessage = "Satisfied: MRP '$value' includes statutory tax declaration.",
                legalSection = rule.sectionReference,
                evidenceDeclarationKey = "MRP",
                evidenceConfidence = conf
            )
        } else if (hasPrice && !hasTaxesText) {
            ComplianceCheckEntity(
                id = UUID.randomUUID().toString(),
                inspectionId = inspectionId,
                ruleId = rule.id,
                ruleCode = rule.ruleCode,
                ruleTitle = rule.title,
                ruleVersion = rule.ruleVersion,
                severity = rule.severity,
                status = ComplianceStatus.POTENTIAL_NON_COMPLIANCE,
                findingMessage = "Violation: MRP '$value' is missing mandatory words '(inclusive of all taxes)' or 'incl. of all taxes'.",
                legalSection = rule.sectionReference,
                evidenceDeclarationKey = "MRP",
                evidenceConfidence = conf
            )
        } else {
            ComplianceCheckEntity(
                id = UUID.randomUUID().toString(),
                inspectionId = inspectionId,
                ruleId = rule.id,
                ruleCode = rule.ruleCode,
                ruleTitle = rule.title,
                ruleVersion = rule.ruleVersion,
                severity = rule.severity,
                status = ComplianceStatus.REQUIRES_REVIEW,
                findingMessage = "Review MRP: Extracted text '$value' unclear or smudged.",
                legalSection = rule.sectionReference,
                evidenceDeclarationKey = "MRP",
                evidenceConfidence = conf
            )
        }
    }

    private fun evaluateConsumerCare(
        inspectionId: String,
        rule: RuleEntity,
        careDecl: DeclarationEntity?
    ): ComplianceCheckEntity {
        val value = careDecl?.correctedValue ?: careDecl?.extractedValue ?: ""
        val conf = careDecl?.confidence ?: 0.5f

        if (value.isBlank() || value.contains("Not Provided", ignoreCase = true)) {
            return ComplianceCheckEntity(
                id = UUID.randomUUID().toString(),
                inspectionId = inspectionId,
                ruleId = rule.id,
                ruleCode = rule.ruleCode,
                ruleTitle = rule.title,
                ruleVersion = rule.ruleVersion,
                severity = rule.severity,
                status = ComplianceStatus.POTENTIAL_NON_COMPLIANCE,
                findingMessage = "Violation: Consumer care helpline / email details missing under Rule 6(1)(f).",
                legalSection = rule.sectionReference,
                evidenceDeclarationKey = "CONSUMER_CARE_CONTACT",
                evidenceConfidence = conf
            )
        }

        val hasEmail = value.contains(Regex("""[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}"""))
        val hasPhone = value.contains(Regex("""(\+?91[\-\s]?)?[6-9]\d{9}|1800[\-\s]?\d{3,4}[\-\s]?\d{3,4}"""))

        return if (hasEmail && hasPhone) {
            ComplianceCheckEntity(
                id = UUID.randomUUID().toString(),
                inspectionId = inspectionId,
                ruleId = rule.id,
                ruleCode = rule.ruleCode,
                ruleTitle = rule.title,
                ruleVersion = rule.ruleVersion,
                severity = rule.severity,
                status = ComplianceStatus.PASS,
                findingMessage = "Satisfied: Consumer care phone and email contact verified ($value).",
                legalSection = rule.sectionReference,
                evidenceDeclarationKey = "CONSUMER_CARE_CONTACT",
                evidenceConfidence = conf
            )
        } else if (hasEmail || hasPhone) {
            ComplianceCheckEntity(
                id = UUID.randomUUID().toString(),
                inspectionId = inspectionId,
                ruleId = rule.id,
                ruleCode = rule.ruleCode,
                ruleTitle = rule.title,
                ruleVersion = rule.ruleVersion,
                severity = rule.severity,
                status = ComplianceStatus.REQUIRES_REVIEW,
                findingMessage = "Partial Consumer Care: Found ${if (hasEmail) "email" else "telephone"}. Verify designation & physical grievance address.",
                legalSection = rule.sectionReference,
                evidenceDeclarationKey = "CONSUMER_CARE_CONTACT",
                evidenceConfidence = conf
            )
        } else {
            ComplianceCheckEntity(
                id = UUID.randomUUID().toString(),
                inspectionId = inspectionId,
                ruleId = rule.id,
                ruleCode = rule.ruleCode,
                ruleTitle = rule.title,
                ruleVersion = rule.ruleVersion,
                severity = rule.severity,
                status = ComplianceStatus.POTENTIAL_NON_COMPLIANCE,
                findingMessage = "Inadequate Consumer Contact: '$value' lacks valid 10-digit phone/toll-free or email.",
                legalSection = rule.sectionReference,
                evidenceDeclarationKey = "CONSUMER_CARE_CONTACT",
                evidenceConfidence = conf
            )
        }
    }

    private fun evaluateCountryOfOrigin(
        inspectionId: String,
        rule: RuleEntity,
        originDecl: DeclarationEntity?
    ): ComplianceCheckEntity {
        val value = originDecl?.correctedValue ?: originDecl?.extractedValue ?: ""
        val conf = originDecl?.confidence ?: 0.5f

        if (value.isNotBlank() && !value.contains("Not Provided", ignoreCase = true) && (value.contains("India", ignoreCase = true) || value.contains("Made in", ignoreCase = true) || value.contains("Country of Origin", ignoreCase = true) || value.contains("Product of", ignoreCase = true))) {
            return ComplianceCheckEntity(
                id = UUID.randomUUID().toString(),
                inspectionId = inspectionId,
                ruleId = rule.id,
                ruleCode = rule.ruleCode,
                ruleTitle = rule.title,
                ruleVersion = rule.ruleVersion,
                severity = rule.severity,
                status = ComplianceStatus.PASS,
                findingMessage = "Satisfied: Country of origin clearly stated as '$value'.",
                legalSection = rule.sectionReference,
                evidenceDeclarationKey = "COUNTRY_OF_ORIGIN",
                evidenceConfidence = conf
            )
        }

        return ComplianceCheckEntity(
            id = UUID.randomUUID().toString(),
            inspectionId = inspectionId,
            ruleId = rule.id,
            ruleCode = rule.ruleCode,
            ruleTitle = rule.title,
            ruleVersion = rule.ruleVersion,
            severity = rule.severity,
            status = ComplianceStatus.POTENTIAL_NON_COMPLIANCE,
            findingMessage = "Violation: Mandatory Country of Origin declaration not found.",
            legalSection = rule.sectionReference,
            evidenceDeclarationKey = "COUNTRY_OF_ORIGIN",
            evidenceConfidence = conf
        )
    }

    private fun evaluatePdpFont(
        inspectionId: String,
        rule: RuleEntity,
        pdpAreaCm2: Double,
        fontDecl: DeclarationEntity?
    ): ComplianceCheckEntity {
        val requiredMm = when {
            pdpAreaCm2 <= 50.0 -> 1.0
            pdpAreaCm2 <= 200.0 -> 2.0
            pdpAreaCm2 <= 1000.0 -> 4.0
            else -> 6.0
        }

        val extractedHeight = fontDecl?.extractedValue?.filter { it.isDigit() || it == '.' }?.toDoubleOrNull() ?: requiredMm
        val conf = fontDecl?.confidence ?: 0.85f

        return if (extractedHeight >= requiredMm) {
            ComplianceCheckEntity(
                id = UUID.randomUUID().toString(),
                inspectionId = inspectionId,
                ruleId = rule.id,
                ruleCode = rule.ruleCode,
                ruleTitle = rule.title,
                ruleVersion = rule.ruleVersion,
                severity = rule.severity,
                status = ComplianceStatus.PASS,
                findingMessage = "Satisfied: Declaration font height (~${extractedHeight}mm) meets Table-1 requirement (min ${requiredMm}mm for PDP area ${pdpAreaCm2}cm²).",
                legalSection = rule.sectionReference,
                evidenceDeclarationKey = "PDP_FONT_HEIGHT",
                evidenceConfidence = conf
            )
        } else {
            ComplianceCheckEntity(
                id = UUID.randomUUID().toString(),
                inspectionId = inspectionId,
                ruleId = rule.id,
                ruleCode = rule.ruleCode,
                ruleTitle = rule.title,
                ruleVersion = rule.ruleVersion,
                severity = rule.severity,
                status = ComplianceStatus.POTENTIAL_NON_COMPLIANCE,
                findingMessage = "Under-Sized Font: Font height (~${extractedHeight}mm) is below required ${requiredMm}mm for PDP ${pdpAreaCm2}cm² under Rule 7.",
                legalSection = rule.sectionReference,
                evidenceDeclarationKey = "PDP_FONT_HEIGHT",
                evidenceConfidence = conf
            )
        }
    }

    private fun evaluateEcomm(
        inspectionId: String,
        rule: RuleEntity,
        category: ProductCategory,
        declMap: Map<String, DeclarationEntity>
    ): ComplianceCheckEntity {
        if (category != ProductCategory.ECOMMERCE_LISTING) {
            return ComplianceCheckEntity(
                id = UUID.randomUUID().toString(),
                inspectionId = inspectionId,
                ruleId = rule.id,
                ruleCode = rule.ruleCode,
                ruleTitle = rule.title,
                ruleVersion = rule.ruleVersion,
                severity = rule.severity,
                status = ComplianceStatus.PASS,
                findingMessage = "Not Applicable: Physical packaged commodity inspection.",
                legalSection = rule.sectionReference,
                evidenceDeclarationKey = "ECOMMERCE_LISTING",
                evidenceConfidence = 1.0f
            )
        }

        val hasMrp = declMap.containsKey("MRP")
        val hasOrigin = declMap.containsKey("COUNTRY_OF_ORIGIN")
        val hasUsp = declMap.containsKey("UNIT_SALE_PRICE")

        return if (hasMrp && hasOrigin && hasUsp) {
            ComplianceCheckEntity(
                id = UUID.randomUUID().toString(),
                inspectionId = inspectionId,
                ruleId = rule.id,
                ruleCode = rule.ruleCode,
                ruleTitle = rule.title,
                ruleVersion = rule.ruleVersion,
                severity = rule.severity,
                status = ComplianceStatus.PASS,
                findingMessage = "Satisfied: All mandatory e-commerce declarations (MRP, USP, Origin) present on listing.",
                legalSection = rule.sectionReference,
                evidenceDeclarationKey = "ECOMMERCE_LISTING",
                evidenceConfidence = 0.92f
            )
        } else {
            ComplianceCheckEntity(
                id = UUID.randomUUID().toString(),
                inspectionId = inspectionId,
                ruleId = rule.id,
                ruleCode = rule.ruleCode,
                ruleTitle = rule.title,
                ruleVersion = rule.ruleVersion,
                severity = rule.severity,
                status = ComplianceStatus.POTENTIAL_NON_COMPLIANCE,
                findingMessage = "E-Commerce Violation: Missing mandatory digital listing declarations under E-Commerce amendment.",
                legalSection = rule.sectionReference,
                evidenceDeclarationKey = "ECOMMERCE_LISTING",
                evidenceConfidence = 0.88f
            )
        }
    }

    private fun evaluateGenericRule(
        inspectionId: String,
        rule: RuleEntity,
        decl: DeclarationEntity?
    ): ComplianceCheckEntity {
        return ComplianceCheckEntity(
            id = UUID.randomUUID().toString(),
            inspectionId = inspectionId,
            ruleId = rule.id,
            ruleCode = rule.ruleCode,
            ruleTitle = rule.title,
            ruleVersion = rule.ruleVersion,
            severity = rule.severity,
            status = if (decl != null && decl.extractedValue.isNotBlank()) ComplianceStatus.PASS else ComplianceStatus.REQUIRES_REVIEW,
            findingMessage = if (decl != null) "Verified: ${decl.extractedValue}" else "Declaration required for ${rule.title}",
            legalSection = rule.sectionReference,
            evidenceDeclarationKey = decl?.fieldKey ?: rule.ruleCode,
            evidenceConfidence = decl?.confidence ?: 0.5f
        )
    }
}
