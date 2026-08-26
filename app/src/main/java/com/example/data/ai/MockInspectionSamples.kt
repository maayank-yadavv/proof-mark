package com.example.data.ai

import com.example.data.local.entities.DeclarationEntity
import com.example.data.local.entities.InspectionEntity
import com.example.data.local.entities.InspectionImageEntity
import com.example.data.local.entities.ProductEntity
import com.example.data.models.BoundingBox
import com.example.data.models.BoundingBoxJson
import com.example.data.models.ComplianceStatus
import com.example.data.models.PackageAngle
import com.example.data.models.ProductCategory
import com.example.data.models.QualityMetrics
import java.util.UUID

data class DemoPackageCase(
    val product: ProductEntity,
    val sampleDescription: String,
    val expectedStatus: ComplianceStatus,
    val imageAngles: List<DemoImageDetail>,
    val declarations: List<DeclarationData>,
    val inspectionNotes: String,
    val location: String = "Zonal Metrology Testing Lab & Field Station #4"
)

data class DemoImageDetail(
    val angle: PackageAngle,
    val quality: QualityMetrics,
    val previewLabel: String,
    val visualPlaceholderColor: String
)

data class DeclarationData(
    val fieldKey: String,
    val fieldName: String,
    val extractedValue: String,
    val confidence: Float,
    val sourceRuleCode: String,
    val boundingBox: BoundingBox
)

object MockInspectionSamples {

    fun boxesToJson(boxes: List<BoundingBox>): String {
        return BoundingBoxJson.toJson(boxes)
    }

    fun getDemoCases(): List<DemoPackageCase> {
        return listOf(
            // Case 1: Royal Spice Mix - Missing Customer Care & MRP missing taxes
            DemoPackageCase(
                product = ProductEntity(
                    id = "prod_royal_spice_100g",
                    name = "Royal Shahi Garam Masala",
                    brand = "Royal Spices India Ltd",
                    category = ProductCategory.FOOD_BEVERAGES,
                    barcode = "8901234567890",
                    batchNumber = "RSM-2026-B94",
                    pdpAreaCm2 = 140.0,
                    packageType = "Foil Sealed Pouch"
                ),
                sampleDescription = "Packaged spice mix pouch inspected at retail supermarket in Central Market.",
                expectedStatus = ComplianceStatus.POTENTIAL_NON_COMPLIANCE,
                imageAngles = listOf(
                    DemoImageDetail(
                        angle = PackageAngle.FRONT,
                        quality = QualityMetrics(94, 95, 92, 95, "Excellent", true),
                        previewLabel = "Front Display Panel (Royal Shahi Garam Masala 100g)",
                        visualPlaceholderColor = "#B45309"
                    ),
                    DemoImageDetail(
                        angle = PackageAngle.BACK,
                        quality = QualityMetrics(88, 90, 85, 90, "Adequate", true),
                        previewLabel = "Back Panel (Declarations, Mfg Details, Ingredients)",
                        visualPlaceholderColor = "#78350F"
                    ),
                    DemoImageDetail(
                        angle = PackageAngle.SIDE,
                        quality = QualityMetrics(89, 91, 88, 89, "Adequate", true),
                        previewLabel = "Side Panel (MRP & Batch Stamp)",
                        visualPlaceholderColor = "#92400E"
                    )
                ),
                declarations = listOf(
                    DeclarationData(
                        fieldKey = "PRODUCT_NAME",
                        fieldName = "Generic Name of Commodity",
                        extractedValue = "Royal Shahi Garam Masala (Ground Mixed Spice Blend)",
                        confidence = 0.98f,
                        sourceRuleCode = "LM-PC-6-1-B",
                        boundingBox = BoundingBox(0.12f, 0.18f, 0.76f, 0.12f, "PRODUCT_NAME", "Royal Shahi Garam Masala", 0.98f, ComplianceStatus.PASS)
                    ),
                    DeclarationData(
                        fieldKey = "MANUFACTURER_NAME",
                        fieldName = "Manufacturer Name",
                        extractedValue = "Royal Spices & Condiments Private Limited",
                        confidence = 0.95f,
                        sourceRuleCode = "LM-PC-6-1-A",
                        boundingBox = BoundingBox(0.08f, 0.42f, 0.84f, 0.08f, "MANUFACTURER_NAME", "Mfd by: Royal Spices & Condiments Pvt Ltd", 0.95f, ComplianceStatus.PASS)
                    ),
                    DeclarationData(
                        fieldKey = "MANUFACTURER_ADDRESS",
                        fieldName = "Complete Manufacturer Address",
                        extractedValue = "Plot No. 42-A, Sector 18 Industrial Area, Gurugram, Haryana - 122015, India",
                        confidence = 0.94f,
                        sourceRuleCode = "LM-PC-6-1-A",
                        boundingBox = BoundingBox(0.08f, 0.50f, 0.84f, 0.10f, "MANUFACTURER_ADDRESS", "Plot 42-A, Sec 18 Ind Area Gurugram PIN 122015", 0.94f, ComplianceStatus.PASS)
                    ),
                    DeclarationData(
                        fieldKey = "NET_QUANTITY",
                        fieldName = "Net Quantity",
                        extractedValue = "100 g (when packed)",
                        confidence = 0.97f,
                        sourceRuleCode = "LM-PC-6-1-C",
                        boundingBox = BoundingBox(0.15f, 0.62f, 0.40f, 0.08f, "NET_QUANTITY", "Net Wt: 100 g", 0.97f, ComplianceStatus.PASS)
                    ),
                    DeclarationData(
                        fieldKey = "UNIT_SALE_PRICE",
                        fieldName = "Unit Sale Price (USP)",
                        extractedValue = "₹ 0.68 / g",
                        confidence = 0.93f,
                        sourceRuleCode = "LM-PC-6-1-DA",
                        boundingBox = BoundingBox(0.58f, 0.62f, 0.36f, 0.08f, "UNIT_SALE_PRICE", "USP: ₹ 0.68 / g", 0.93f, ComplianceStatus.PASS)
                    ),
                    DeclarationData(
                        fieldKey = "MRP",
                        fieldName = "Maximum Retail Price",
                        extractedValue = "MRP: Rs. 68.00",
                        confidence = 0.96f,
                        sourceRuleCode = "LM-PC-6-1-E",
                        boundingBox = BoundingBox(0.10f, 0.72f, 0.80f, 0.09f, "MRP", "MRP: Rs. 68.00", 0.96f, ComplianceStatus.POTENTIAL_NON_COMPLIANCE)
                    ),
                    DeclarationData(
                        fieldKey = "DATE_OF_MANUFACTURE",
                        fieldName = "Date of Packing / Mfg",
                        extractedValue = "07/2026",
                        confidence = 0.92f,
                        sourceRuleCode = "LM-PC-6-1-D",
                        boundingBox = BoundingBox(0.10f, 0.82f, 0.40f, 0.07f, "DATE_OF_MANUFACTURE", "Pkd: 07/2026", 0.92f, ComplianceStatus.PASS)
                    ),
                    DeclarationData(
                        fieldKey = "COUNTRY_OF_ORIGIN",
                        fieldName = "Country of Origin",
                        extractedValue = "Country of Origin: India",
                        confidence = 0.97f,
                        sourceRuleCode = "LM-PC-6-10",
                        boundingBox = BoundingBox(0.55f, 0.82f, 0.38f, 0.07f, "COUNTRY_OF_ORIGIN", "Country of Origin: India", 0.97f, ComplianceStatus.PASS)
                    ),
                    DeclarationData(
                        fieldKey = "CONSUMER_CARE_CONTACT",
                        fieldName = "Consumer Care Grievance Details",
                        extractedValue = "Write to: Manager, Customer Relations at mfg address.",
                        confidence = 0.89f,
                        sourceRuleCode = "LM-PC-6-1-F",
                        boundingBox = BoundingBox(0.08f, 0.90f, 0.84f, 0.08f, "CONSUMER_CARE_CONTACT", "Contact Manager Customer Relations", 0.89f, ComplianceStatus.POTENTIAL_NON_COMPLIANCE)
                    ),
                    DeclarationData(
                        fieldKey = "PDP_FONT_HEIGHT",
                        fieldName = "PDP Font Height",
                        extractedValue = "2.8 mm",
                        confidence = 0.91f,
                        sourceRuleCode = "LM-PC-7-PDP",
                        boundingBox = BoundingBox(0.15f, 0.62f, 0.40f, 0.08f, "PDP_FONT_HEIGHT", "Height: 2.8mm", 0.91f, ComplianceStatus.PASS)
                    )
                ),
                inspectionNotes = "Officer Note: MRP lacks mandatory tax qualification '(inclusive of all taxes)' and Consumer Care does not provide active telephone number or official email under Rule 6(1)(f)."
            ),

            // Case 2: GlowPure Face Serum - Missing Importer Address & Country of Origin
            DemoPackageCase(
                product = ProductEntity(
                    id = "prod_glowpure_serum_30ml",
                    name = "GlowPure Hydrating Face Serum 30ml",
                    brand = "GlowPure Paris",
                    category = ProductCategory.COSMETICS_PERSONAL_CARE,
                    barcode = "3700123456789",
                    batchNumber = "GP-FR-982",
                    pdpAreaCm2 = 85.0,
                    packageType = "Glass Dropper Bottle in Mono-Carton"
                ),
                sampleDescription = "Imported cosmetic serum sample seized during surprise customs warehouse inspection.",
                expectedStatus = ComplianceStatus.POTENTIAL_NON_COMPLIANCE,
                imageAngles = listOf(
                    DemoImageDetail(
                        angle = PackageAngle.FRONT,
                        quality = QualityMetrics(92, 94, 90, 92, "Excellent", true),
                        previewLabel = "Front Box (GlowPure Hydrating Face Serum)",
                        visualPlaceholderColor = "#0284C7"
                    ),
                    DemoImageDetail(
                        angle = PackageAngle.BACK,
                        quality = QualityMetrics(89, 90, 88, 89, "Adequate", true),
                        previewLabel = "Back Panel (Importer & Regulatory Label)",
                        visualPlaceholderColor = "#0369A1"
                    )
                ),
                declarations = listOf(
                    DeclarationData(
                        fieldKey = "PRODUCT_NAME",
                        fieldName = "Generic Name of Commodity",
                        extractedValue = "Hydrating Facial Serum (Cosmetic)",
                        confidence = 0.97f,
                        sourceRuleCode = "LM-PC-6-1-B",
                        boundingBox = BoundingBox(0.10f, 0.15f, 0.80f, 0.10f, "PRODUCT_NAME", "GlowPure Hydrating Face Serum", 0.97f, ComplianceStatus.PASS)
                    ),
                    DeclarationData(
                        fieldKey = "MANUFACTURER_NAME",
                        fieldName = "Manufacturer / Importer Name",
                        extractedValue = "Imported and Marketed by GlowPure India Pvt Ltd",
                        confidence = 0.92f,
                        sourceRuleCode = "LM-PC-6-1-A",
                        boundingBox = BoundingBox(0.08f, 0.38f, 0.84f, 0.08f, "MANUFACTURER_NAME", "GlowPure India Pvt Ltd", 0.92f, ComplianceStatus.PASS)
                    ),
                    DeclarationData(
                        fieldKey = "MANUFACTURER_ADDRESS",
                        fieldName = "Complete Importer Address",
                        extractedValue = "GlowPure India, Mumbai, India",
                        confidence = 0.88f,
                        sourceRuleCode = "LM-PC-6-1-A",
                        boundingBox = BoundingBox(0.08f, 0.46f, 0.84f, 0.08f, "MANUFACTURER_ADDRESS", "Mumbai, India", 0.88f, ComplianceStatus.REQUIRES_REVIEW)
                    ),
                    DeclarationData(
                        fieldKey = "NET_QUANTITY",
                        fieldName = "Net Quantity",
                        extractedValue = "30 ml",
                        confidence = 0.98f,
                        sourceRuleCode = "LM-PC-6-1-C",
                        boundingBox = BoundingBox(0.12f, 0.56f, 0.35f, 0.08f, "NET_QUANTITY", "Net Vol: 30 ml", 0.98f, ComplianceStatus.PASS)
                    ),
                    DeclarationData(
                        fieldKey = "UNIT_SALE_PRICE",
                        fieldName = "Unit Sale Price (USP)",
                        extractedValue = "₹ 49.67 / ml",
                        confidence = 0.94f,
                        sourceRuleCode = "LM-PC-6-1-DA",
                        boundingBox = BoundingBox(0.52f, 0.56f, 0.40f, 0.08f, "UNIT_SALE_PRICE", "USP: ₹ 49.67 / ml", 0.94f, ComplianceStatus.PASS)
                    ),
                    DeclarationData(
                        fieldKey = "MRP",
                        fieldName = "Maximum Retail Price",
                        extractedValue = "₹ 1,490.00 (inclusive of all taxes)",
                        confidence = 0.99f,
                        sourceRuleCode = "LM-PC-6-1-E",
                        boundingBox = BoundingBox(0.10f, 0.66f, 0.80f, 0.09f, "MRP", "₹ 1,490 (incl. of all taxes)", 0.99f, ComplianceStatus.PASS)
                    ),
                    DeclarationData(
                        fieldKey = "DATE_OF_MANUFACTURE",
                        fieldName = "Date of Import / Mfg",
                        extractedValue = "05/2026",
                        confidence = 0.93f,
                        sourceRuleCode = "LM-PC-6-1-D",
                        boundingBox = BoundingBox(0.10f, 0.76f, 0.40f, 0.07f, "DATE_OF_MANUFACTURE", "Imported: 05/2026", 0.93f, ComplianceStatus.PASS)
                    ),
                    DeclarationData(
                        fieldKey = "COUNTRY_OF_ORIGIN",
                        fieldName = "Country of Origin",
                        extractedValue = "", // Completely missing on label
                        confidence = 0.30f,
                        sourceRuleCode = "LM-PC-6-10",
                        boundingBox = BoundingBox(0.55f, 0.76f, 0.38f, 0.07f, "COUNTRY_OF_ORIGIN", "NOT FOUND", 0.30f, ComplianceStatus.POTENTIAL_NON_COMPLIANCE)
                    ),
                    DeclarationData(
                        fieldKey = "CONSUMER_CARE_CONTACT",
                        fieldName = "Consumer Care Grievance Details",
                        extractedValue = "Consumer Care Exec: care@glowpure.in, Tel: +91 22 4567 8900",
                        confidence = 0.96f,
                        sourceRuleCode = "LM-PC-6-1-F",
                        boundingBox = BoundingBox(0.08f, 0.85f, 0.84f, 0.09f, "CONSUMER_CARE_CONTACT", "care@glowpure.in / +91 22 4567 8900", 0.96f, ComplianceStatus.PASS)
                    ),
                    DeclarationData(
                        fieldKey = "PDP_FONT_HEIGHT",
                        fieldName = "PDP Font Height",
                        extractedValue = "2.1 mm",
                        confidence = 0.90f,
                        sourceRuleCode = "LM-PC-7-PDP",
                        boundingBox = BoundingBox(0.12f, 0.56f, 0.35f, 0.08f, "PDP_FONT_HEIGHT", "Height: 2.1mm", 0.90f, ComplianceStatus.PASS)
                    )
                ),
                inspectionNotes = "Critical Violation: Mandatory Country of Origin completely omitted from outer carton. Importer address lacks street and PIN code."
            ),

            // Case 3: NutriCrunch Biscuits - 100% Fully Compliant PASS
            DemoPackageCase(
                product = ProductEntity(
                    id = "prod_nutricrunch_250g",
                    name = "NutriCrunch Wholewheat Digestive Biscuits",
                    brand = "NutriCrunch Organics Ltd",
                    category = ProductCategory.FOOD_BEVERAGES,
                    barcode = "8909876543210",
                    batchNumber = "NC-DIG-AUG26",
                    pdpAreaCm2 = 180.0,
                    packageType = "Flow-Wrap Pillow Pack"
                ),
                sampleDescription = "Standard commercial packaged biscuit pack evaluated for standard size and label declarations.",
                expectedStatus = ComplianceStatus.PASS,
                imageAngles = listOf(
                    DemoImageDetail(
                        angle = PackageAngle.FRONT,
                        quality = QualityMetrics(98, 97, 98, 99, "Excellent", true),
                        previewLabel = "Front Packaging (NutriCrunch Wholewheat)",
                        visualPlaceholderColor = "#059669"
                    ),
                    DemoImageDetail(
                        angle = PackageAngle.BACK,
                        quality = QualityMetrics(96, 95, 96, 97, "Excellent", true),
                        previewLabel = "Back Declarations & Nutrition Matrix",
                        visualPlaceholderColor = "#047857"
                    )
                ),
                declarations = listOf(
                    DeclarationData(
                        fieldKey = "PRODUCT_NAME",
                        fieldName = "Generic Name of Commodity",
                        extractedValue = "Wholewheat Digestive Biscuits (High Fibre)",
                        confidence = 0.99f,
                        sourceRuleCode = "LM-PC-6-1-B",
                        boundingBox = BoundingBox(0.10f, 0.15f, 0.80f, 0.12f, "PRODUCT_NAME", "NutriCrunch Wholewheat Biscuits", 0.99f, ComplianceStatus.PASS)
                    ),
                    DeclarationData(
                        fieldKey = "MANUFACTURER_NAME",
                        fieldName = "Manufacturer Name",
                        extractedValue = "NutriCrunch Foods Private Limited",
                        confidence = 0.98f,
                        sourceRuleCode = "LM-PC-6-1-A",
                        boundingBox = BoundingBox(0.08f, 0.35f, 0.84f, 0.08f, "MANUFACTURER_NAME", "NutriCrunch Foods Pvt Ltd", 0.98f, ComplianceStatus.PASS)
                    ),
                    DeclarationData(
                        fieldKey = "MANUFACTURER_ADDRESS",
                        fieldName = "Complete Manufacturer Address",
                        extractedValue = "Sy. No. 108/2, Food Tech Park, Peenya Industrial Area, Bengaluru, Karnataka - 560058, India",
                        confidence = 0.97f,
                        sourceRuleCode = "LM-PC-6-1-A",
                        boundingBox = BoundingBox(0.08f, 0.44f, 0.84f, 0.10f, "MANUFACTURER_ADDRESS", "Peenya Indl Area, Bengaluru - 560058", 0.97f, ComplianceStatus.PASS)
                    ),
                    DeclarationData(
                        fieldKey = "NET_QUANTITY",
                        fieldName = "Net Quantity",
                        extractedValue = "250 g",
                        confidence = 0.99f,
                        sourceRuleCode = "LM-PC-6-1-C",
                        boundingBox = BoundingBox(0.12f, 0.56f, 0.35f, 0.08f, "NET_QUANTITY", "Net Weight: 250 g", 0.99f, ComplianceStatus.PASS)
                    ),
                    DeclarationData(
                        fieldKey = "UNIT_SALE_PRICE",
                        fieldName = "Unit Sale Price (USP)",
                        extractedValue = "₹ 0.18 / g (₹ 18.00 / 100g)",
                        confidence = 0.97f,
                        sourceRuleCode = "LM-PC-6-1-DA",
                        boundingBox = BoundingBox(0.52f, 0.56f, 0.40f, 0.08f, "UNIT_SALE_PRICE", "USP: ₹ 0.18 / g", 0.97f, ComplianceStatus.PASS)
                    ),
                    DeclarationData(
                        fieldKey = "MRP",
                        fieldName = "Maximum Retail Price",
                        extractedValue = "MRP ₹ 45.00 (inclusive of all taxes)",
                        confidence = 0.99f,
                        sourceRuleCode = "LM-PC-6-1-E",
                        boundingBox = BoundingBox(0.10f, 0.66f, 0.80f, 0.09f, "MRP", "MRP ₹ 45.00 (incl. of all taxes)", 0.99f, ComplianceStatus.PASS)
                    ),
                    DeclarationData(
                        fieldKey = "DATE_OF_MANUFACTURE",
                        fieldName = "Date of Manufacture",
                        extractedValue = "08/2026",
                        confidence = 0.98f,
                        sourceRuleCode = "LM-PC-6-1-D",
                        boundingBox = BoundingBox(0.10f, 0.77f, 0.40f, 0.07f, "DATE_OF_MANUFACTURE", "Mfg: 08/2026", 0.98f, ComplianceStatus.PASS)
                    ),
                    DeclarationData(
                        fieldKey = "COUNTRY_OF_ORIGIN",
                        fieldName = "Country of Origin",
                        extractedValue = "Country of Origin: India",
                        confidence = 0.99f,
                        sourceRuleCode = "LM-PC-6-10",
                        boundingBox = BoundingBox(0.55f, 0.77f, 0.38f, 0.07f, "COUNTRY_OF_ORIGIN", "Country of Origin: India", 0.99f, ComplianceStatus.PASS)
                    ),
                    DeclarationData(
                        fieldKey = "CONSUMER_CARE_CONTACT",
                        fieldName = "Consumer Care Grievance Details",
                        extractedValue = "Customer Service Cell: 1800-425-9988, feedback@nutricrunch.com, Address: Same as Mfg.",
                        confidence = 0.98f,
                        sourceRuleCode = "LM-PC-6-1-F",
                        boundingBox = BoundingBox(0.08f, 0.86f, 0.84f, 0.09f, "CONSUMER_CARE_CONTACT", "1800-425-9988 / feedback@nutricrunch.com", 0.98f, ComplianceStatus.PASS)
                    ),
                    DeclarationData(
                        fieldKey = "PDP_FONT_HEIGHT",
                        fieldName = "PDP Font Height",
                        extractedValue = "3.2 mm",
                        confidence = 0.95f,
                        sourceRuleCode = "LM-PC-7-PDP",
                        boundingBox = BoundingBox(0.12f, 0.56f, 0.35f, 0.08f, "PDP_FONT_HEIGHT", "Height: 3.2mm", 0.95f, ComplianceStatus.PASS)
                    )
                ),
                inspectionNotes = "Exemplary Package: Complies with all mandatory declarations under Legal Metrology Rules, 2011 and 2022 Unit Sale Price amendments."
            ),

            // Case 4: FreshPulse Cold-Pressed Oil 1L - Missing Packing Date & Under-sized Font
            DemoPackageCase(
                product = ProductEntity(
                    id = "prod_freshpulse_oil_1l",
                    name = "FreshPulse Cold-Pressed Kachi Ghani Mustard Oil",
                    brand = "FreshPulse Agritech Ltd",
                    category = ProductCategory.EDIBLE_OILS_GRAINS,
                    barcode = "8903332221110",
                    batchNumber = "FPO-MO-AUG26",
                    pdpAreaCm2 = 320.0,
                    packageType = "PET Bottle 1 Litre"
                ),
                sampleDescription = "Edible oil bottle inspected at regional wholesale mandi distribution center.",
                expectedStatus = ComplianceStatus.POTENTIAL_NON_COMPLIANCE,
                imageAngles = listOf(
                    DemoImageDetail(
                        angle = PackageAngle.FRONT,
                        quality = QualityMetrics(91, 93, 89, 90, "Adequate", true),
                        previewLabel = "Front Label (FreshPulse Mustard Oil 1L)",
                        visualPlaceholderColor = "#D97706"
                    ),
                    DemoImageDetail(
                        angle = PackageAngle.BACK,
                        quality = QualityMetrics(87, 88, 86, 88, "Adequate", true),
                        previewLabel = "Back Label (Declarations & FSSAI Details)",
                        visualPlaceholderColor = "#B45309"
                    )
                ),
                declarations = listOf(
                    DeclarationData(
                        fieldKey = "PRODUCT_NAME",
                        fieldName = "Generic Name of Commodity",
                        extractedValue = "Cold Pressed Mustard Oil (Edible Vegetable Oil)",
                        confidence = 0.96f,
                        sourceRuleCode = "LM-PC-6-1-B",
                        boundingBox = BoundingBox(0.10f, 0.15f, 0.80f, 0.12f, "PRODUCT_NAME", "FreshPulse Mustard Oil", 0.96f, ComplianceStatus.PASS)
                    ),
                    DeclarationData(
                        fieldKey = "MANUFACTURER_NAME",
                        fieldName = "Manufacturer Name",
                        extractedValue = "FreshPulse Agritech Processing Pvt Ltd",
                        confidence = 0.95f,
                        sourceRuleCode = "LM-PC-6-1-A",
                        boundingBox = BoundingBox(0.08f, 0.35f, 0.84f, 0.08f, "MANUFACTURER_NAME", "FreshPulse Agritech Pvt Ltd", 0.95f, ComplianceStatus.PASS)
                    ),
                    DeclarationData(
                        fieldKey = "MANUFACTURER_ADDRESS",
                        fieldName = "Complete Manufacturer Address",
                        extractedValue = "Village Rampur, GT Road, Alwar, Rajasthan - 301001, India",
                        confidence = 0.94f,
                        sourceRuleCode = "LM-PC-6-1-A",
                        boundingBox = BoundingBox(0.08f, 0.44f, 0.84f, 0.09f, "MANUFACTURER_ADDRESS", "Alwar, Rajasthan - 301001", 0.94f, ComplianceStatus.PASS)
                    ),
                    DeclarationData(
                        fieldKey = "NET_QUANTITY",
                        fieldName = "Net Quantity",
                        extractedValue = "1 L (1 Litre)",
                        confidence = 0.98f,
                        sourceRuleCode = "LM-PC-6-1-C",
                        boundingBox = BoundingBox(0.12f, 0.55f, 0.35f, 0.08f, "NET_QUANTITY", "Net Qty: 1 L", 0.98f, ComplianceStatus.PASS)
                    ),
                    DeclarationData(
                        fieldKey = "UNIT_SALE_PRICE",
                        fieldName = "Unit Sale Price (USP)",
                        extractedValue = "₹ 195.00 / L (₹ 0.195 / ml)",
                        confidence = 0.92f,
                        sourceRuleCode = "LM-PC-6-1-DA",
                        boundingBox = BoundingBox(0.52f, 0.55f, 0.40f, 0.08f, "UNIT_SALE_PRICE", "USP: ₹ 195.00 / L", 0.92f, ComplianceStatus.PASS)
                    ),
                    DeclarationData(
                        fieldKey = "MRP",
                        fieldName = "Maximum Retail Price",
                        extractedValue = "MRP ₹ 195.00 (inclusive of all taxes)",
                        confidence = 0.98f,
                        sourceRuleCode = "LM-PC-6-1-E",
                        boundingBox = BoundingBox(0.10f, 0.65f, 0.80f, 0.09f, "MRP", "MRP ₹ 195.00 (incl. of all taxes)", 0.98f, ComplianceStatus.PASS)
                    ),
                    DeclarationData(
                        fieldKey = "DATE_OF_MANUFACTURE",
                        fieldName = "Date of Packing / Mfg",
                        extractedValue = "", // Missing on bottle
                        confidence = 0.25f,
                        sourceRuleCode = "LM-PC-6-1-D",
                        boundingBox = BoundingBox(0.10f, 0.76f, 0.40f, 0.07f, "DATE_OF_MANUFACTURE", "MISSING", 0.25f, ComplianceStatus.POTENTIAL_NON_COMPLIANCE)
                    ),
                    DeclarationData(
                        fieldKey = "COUNTRY_OF_ORIGIN",
                        fieldName = "Country of Origin",
                        extractedValue = "Product of India",
                        confidence = 0.96f,
                        sourceRuleCode = "LM-PC-6-10",
                        boundingBox = BoundingBox(0.55f, 0.76f, 0.38f, 0.07f, "COUNTRY_OF_ORIGIN", "Product of India", 0.96f, ComplianceStatus.PASS)
                    ),
                    DeclarationData(
                        fieldKey = "CONSUMER_CARE_CONTACT",
                        fieldName = "Consumer Care Grievance Details",
                        extractedValue = "Care Manager: 1800-200-1122, info@freshpulseagro.com",
                        confidence = 0.95f,
                        sourceRuleCode = "LM-PC-6-1-F",
                        boundingBox = BoundingBox(0.08f, 0.85f, 0.84f, 0.09f, "CONSUMER_CARE_CONTACT", "1800-200-1122 / info@freshpulseagro.com", 0.95f, ComplianceStatus.PASS)
                    ),
                    DeclarationData(
                        fieldKey = "PDP_FONT_HEIGHT",
                        fieldName = "PDP Font Height",
                        extractedValue = "2.4 mm", // Violates 4.0mm requirement for 320cm2 PDP
                        confidence = 0.93f,
                        sourceRuleCode = "LM-PC-7-PDP",
                        boundingBox = BoundingBox(0.12f, 0.55f, 0.35f, 0.08f, "PDP_FONT_HEIGHT", "Height: 2.4mm", 0.93f, ComplianceStatus.POTENTIAL_NON_COMPLIANCE)
                    )
                ),
                inspectionNotes = "Double Non-Compliance: Missing Month & Year of packing on bottle body, and font height (2.4mm) violates Table-1 statutory minimum (4.0mm) for PDP area > 200 cm²."
            ),

            // Case 5: ProSound Wireless Earbuds - E-Commerce Listing Check
            DemoPackageCase(
                product = ProductEntity(
                    id = "prod_prosound_earbuds_ecomm",
                    name = "ProSound AirSync Active Noise Cancelling Earbuds",
                    brand = "ProSound Acoustics",
                    category = ProductCategory.ECOMMERCE_LISTING,
                    barcode = "8905544332211",
                    batchNumber = "PS-TWS-2026-X",
                    pdpAreaCm2 = 110.0,
                    packageType = "E-Commerce Product Page Listing"
                ),
                sampleDescription = "Digital compliance audit of e-commerce marketplace listing for consumer electronics.",
                expectedStatus = ComplianceStatus.POTENTIAL_NON_COMPLIANCE,
                imageAngles = listOf(
                    DemoImageDetail(
                        angle = PackageAngle.ECOMM_LISTING,
                        quality = QualityMetrics(95, 96, 95, 96, "Excellent", true),
                        previewLabel = "E-Commerce Marketplace Listing Screenshot",
                        visualPlaceholderColor = "#4338CA"
                    )
                ),
                declarations = listOf(
                    DeclarationData(
                        fieldKey = "PRODUCT_NAME",
                        fieldName = "Generic Name of Commodity",
                        extractedValue = "Wireless Bluetooth Earphones with Charging Case",
                        confidence = 0.98f,
                        sourceRuleCode = "LM-PC-6-1-B",
                        boundingBox = BoundingBox(0.10f, 0.15f, 0.80f, 0.10f, "PRODUCT_NAME", "ProSound Wireless Earbuds", 0.98f, ComplianceStatus.PASS)
                    ),
                    DeclarationData(
                        fieldKey = "MANUFACTURER_NAME",
                        fieldName = "Manufacturer / Importer Name",
                        extractedValue = "Sold by: FastTrack Electronics Private Limited",
                        confidence = 0.92f,
                        sourceRuleCode = "LM-PC-6-1-A",
                        boundingBox = BoundingBox(0.08f, 0.35f, 0.84f, 0.08f, "MANUFACTURER_NAME", "FastTrack Electronics Pvt Ltd", 0.92f, ComplianceStatus.PASS)
                    ),
                    DeclarationData(
                        fieldKey = "MANUFACTURER_ADDRESS",
                        fieldName = "Complete Seller/Importer Address",
                        extractedValue = "FastTrack Hub, Okhla Phase III, New Delhi - 110020",
                        confidence = 0.93f,
                        sourceRuleCode = "LM-PC-6-1-A",
                        boundingBox = BoundingBox(0.08f, 0.44f, 0.84f, 0.08f, "MANUFACTURER_ADDRESS", "Okhla Phase III, New Delhi - 110020", 0.93f, ComplianceStatus.PASS)
                    ),
                    DeclarationData(
                        fieldKey = "NET_QUANTITY",
                        fieldName = "Net Quantity",
                        extractedValue = "1 N (Contains: 1 Pair Earbuds, 1 Case, 1 Cable)",
                        confidence = 0.97f,
                        sourceRuleCode = "LM-PC-6-1-C",
                        boundingBox = BoundingBox(0.12f, 0.54f, 0.35f, 0.08f, "NET_QUANTITY", "Net Qty: 1 N", 0.97f, ComplianceStatus.PASS)
                    ),
                    DeclarationData(
                        fieldKey = "UNIT_SALE_PRICE",
                        fieldName = "Unit Sale Price (USP)",
                        extractedValue = "", // Omitted on marketplace page
                        confidence = 0.35f,
                        sourceRuleCode = "LM-PC-6-1-DA",
                        boundingBox = BoundingBox(0.52f, 0.54f, 0.40f, 0.08f, "UNIT_SALE_PRICE", "MISSING ON LISTING", 0.35f, ComplianceStatus.POTENTIAL_NON_COMPLIANCE)
                    ),
                    DeclarationData(
                        fieldKey = "MRP",
                        fieldName = "Maximum Retail Price",
                        extractedValue = "₹ 2,999.00 (incl. of all taxes)",
                        confidence = 0.98f,
                        sourceRuleCode = "LM-PC-6-1-E",
                        boundingBox = BoundingBox(0.10f, 0.64f, 0.80f, 0.09f, "MRP", "₹ 2,999 (incl. all taxes)", 0.98f, ComplianceStatus.PASS)
                    ),
                    DeclarationData(
                        fieldKey = "DATE_OF_MANUFACTURE",
                        fieldName = "Date of Import / Mfg",
                        extractedValue = "06/2026",
                        confidence = 0.92f,
                        sourceRuleCode = "LM-PC-6-1-D",
                        boundingBox = BoundingBox(0.10f, 0.75f, 0.40f, 0.07f, "DATE_OF_MANUFACTURE", "Imported: 06/2026", 0.92f, ComplianceStatus.PASS)
                    ),
                    DeclarationData(
                        fieldKey = "COUNTRY_OF_ORIGIN",
                        fieldName = "Country of Origin",
                        extractedValue = "Country of Origin: Vietnam",
                        confidence = 0.97f,
                        sourceRuleCode = "LM-PC-6-10",
                        boundingBox = BoundingBox(0.55f, 0.75f, 0.38f, 0.07f, "COUNTRY_OF_ORIGIN", "Country of Origin: Vietnam", 0.97f, ComplianceStatus.PASS)
                    ),
                    DeclarationData(
                        fieldKey = "CONSUMER_CARE_CONTACT",
                        fieldName = "Consumer Care Grievance Details",
                        extractedValue = "support@prosound.co.in | Helpline: 1800-889-2211",
                        confidence = 0.96f,
                        sourceRuleCode = "LM-PC-6-1-F",
                        boundingBox = BoundingBox(0.08f, 0.84f, 0.84f, 0.09f, "CONSUMER_CARE_CONTACT", "support@prosound.co.in / 1800-889-2211", 0.96f, ComplianceStatus.PASS)
                    ),
                    DeclarationData(
                        fieldKey = "ECOMMERCE_LISTING",
                        fieldName = "E-Commerce Digital Compliance",
                        extractedValue = "Digital Listing Check",
                        confidence = 0.90f,
                        sourceRuleCode = "LM-PC-ECOMM",
                        boundingBox = BoundingBox(0.10f, 0.10f, 0.80f, 0.80f, "ECOMMERCE_LISTING", "Digital Listing Analysis", 0.90f, ComplianceStatus.POTENTIAL_NON_COMPLIANCE)
                    )
                ),
                inspectionNotes = "E-Commerce Marketplace Non-Compliance: Seller page displays MRP and Country of Origin, but fails to display mandatory Unit Sale Price (USP) per Rule 6(1)(da) & E-Commerce guidelines."
            ),

            // Case 6: AgroShield Plant Booster - Requires Review (Smudged text)
            DemoPackageCase(
                product = ProductEntity(
                    id = "prod_agroshield_500g",
                    name = "AgroShield Bio-Enzyme Organic Plant Booster",
                    brand = "AgroShield BioTech Ltd",
                    category = ProductCategory.CHEMICALS_PESTICIDES,
                    barcode = "8907766554433",
                    batchNumber = "AS-ENZ-???",
                    pdpAreaCm2 = 190.0,
                    packageType = "HDPE Jar 500g"
                ),
                sampleDescription = "Agricultural nutrient container with partially degraded print inspected at rural depot.",
                expectedStatus = ComplianceStatus.REQUIRES_REVIEW,
                imageAngles = listOf(
                    DemoImageDetail(
                        angle = PackageAngle.FRONT,
                        quality = QualityMetrics(74, 68, 75, 78, "Marginal", true),
                        previewLabel = "Front Label (AgroShield Bio-Enzyme Jar)",
                        visualPlaceholderColor = "#065F46"
                    ),
                    DemoImageDetail(
                        angle = PackageAngle.BACK,
                        quality = QualityMetrics(69, 62, 70, 74, "Marginal", false),
                        previewLabel = "Back Panel (Smudged Batch Stamp & Declarations)",
                        visualPlaceholderColor = "#047857"
                    )
                ),
                declarations = listOf(
                    DeclarationData(
                        fieldKey = "PRODUCT_NAME",
                        fieldName = "Generic Name of Commodity",
                        extractedValue = "Organic Bio-Enzyme Fertilizer & Soil Conditioner",
                        confidence = 0.88f,
                        sourceRuleCode = "LM-PC-6-1-B",
                        boundingBox = BoundingBox(0.10f, 0.15f, 0.80f, 0.10f, "PRODUCT_NAME", "AgroShield Organic Booster", 0.88f, ComplianceStatus.PASS)
                    ),
                    DeclarationData(
                        fieldKey = "MANUFACTURER_NAME",
                        fieldName = "Manufacturer Name",
                        extractedValue = "AgroShield BioTech Innovations Ltd",
                        confidence = 0.85f,
                        sourceRuleCode = "LM-PC-6-1-A",
                        boundingBox = BoundingBox(0.08f, 0.35f, 0.84f, 0.08f, "MANUFACTURER_NAME", "AgroShield BioTech Ltd", 0.85f, ComplianceStatus.PASS)
                    ),
                    DeclarationData(
                        fieldKey = "MANUFACTURER_ADDRESS",
                        fieldName = "Complete Manufacturer Address",
                        extractedValue = "Industrial Growth Centre, Phase II, Samba, Jammu - 184121",
                        confidence = 0.84f,
                        sourceRuleCode = "LM-PC-6-1-A",
                        boundingBox = BoundingBox(0.08f, 0.44f, 0.84f, 0.09f, "MANUFACTURER_ADDRESS", "Samba, Jammu - 184121", 0.84f, ComplianceStatus.PASS)
                    ),
                    DeclarationData(
                        fieldKey = "NET_QUANTITY",
                        fieldName = "Net Quantity",
                        extractedValue = "500 g... [ink smudged]",
                        confidence = 0.62f, // Low confidence
                        sourceRuleCode = "LM-PC-6-1-C",
                        boundingBox = BoundingBox(0.12f, 0.55f, 0.35f, 0.08f, "NET_QUANTITY", "500 g... [smudge]", 0.62f, ComplianceStatus.REQUIRES_REVIEW)
                    ),
                    DeclarationData(
                        fieldKey = "UNIT_SALE_PRICE",
                        fieldName = "Unit Sale Price (USP)",
                        extractedValue = "₹ 0.70 / g",
                        confidence = 0.76f,
                        sourceRuleCode = "LM-PC-6-1-DA",
                        boundingBox = BoundingBox(0.52f, 0.55f, 0.40f, 0.08f, "UNIT_SALE_PRICE", "USP: ₹ 0.70 / g", 0.76f, ComplianceStatus.PASS)
                    ),
                    DeclarationData(
                        fieldKey = "MRP",
                        fieldName = "Maximum Retail Price",
                        extractedValue = "₹ 350.00 (incl. of all taxes)",
                        confidence = 0.88f,
                        sourceRuleCode = "LM-PC-6-1-E",
                        boundingBox = BoundingBox(0.10f, 0.65f, 0.80f, 0.09f, "MRP", "₹ 350 (incl. of all taxes)", 0.88f, ComplianceStatus.PASS)
                    ),
                    DeclarationData(
                        fieldKey = "DATE_OF_MANUFACTURE",
                        fieldName = "Date of Packing / Mfg",
                        extractedValue = "??/2026 [partially illegible]",
                        confidence = 0.58f,
                        sourceRuleCode = "LM-PC-6-1-D",
                        boundingBox = BoundingBox(0.10f, 0.76f, 0.40f, 0.07f, "DATE_OF_MANUFACTURE", "Mfg: ??/2026", 0.58f, ComplianceStatus.REQUIRES_REVIEW)
                    ),
                    DeclarationData(
                        fieldKey = "COUNTRY_OF_ORIGIN",
                        fieldName = "Country of Origin",
                        extractedValue = "Made in India",
                        confidence = 0.91f,
                        sourceRuleCode = "LM-PC-6-10",
                        boundingBox = BoundingBox(0.55f, 0.76f, 0.38f, 0.07f, "COUNTRY_OF_ORIGIN", "Made in India", 0.91f, ComplianceStatus.PASS)
                    ),
                    DeclarationData(
                        fieldKey = "CONSUMER_CARE_CONTACT",
                        fieldName = "Consumer Care Grievance Details",
                        extractedValue = "Email: support@agroshield.in (Phone stamp faded)",
                        confidence = 0.72f,
                        sourceRuleCode = "LM-PC-6-1-F",
                        boundingBox = BoundingBox(0.08f, 0.85f, 0.84f, 0.09f, "CONSUMER_CARE_CONTACT", "support@agroshield.in", 0.72f, ComplianceStatus.REQUIRES_REVIEW)
                    ),
                    DeclarationData(
                        fieldKey = "PDP_FONT_HEIGHT",
                        fieldName = "PDP Font Height",
                        extractedValue = "2.2 mm",
                        confidence = 0.82f,
                        sourceRuleCode = "LM-PC-7-PDP",
                        boundingBox = BoundingBox(0.12f, 0.55f, 0.35f, 0.08f, "PDP_FONT_HEIGHT", "Height: 2.2mm", 0.82f, ComplianceStatus.PASS)
                    )
                ),
                inspectionNotes = "Inspection Alert: Physical label print degradation observed. Net quantity and manufacturing month digits smudged. Flagged for Manual Enforcement Verification."
            )
        )
    }
}
