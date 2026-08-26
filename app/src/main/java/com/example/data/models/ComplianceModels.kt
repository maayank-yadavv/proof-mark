package com.example.data.models

enum class ComplianceStatus(val displayName: String, val shortCode: String) {
    PASS("Compliant", "PASS"),
    POTENTIAL_NON_COMPLIANCE("Potential Non-Compliance", "FAIL"),
    REQUIRES_REVIEW("Requires Review", "REVIEW"),
    DRAFT("Draft", "DRAFT")
}

enum class RuleSeverity(val displayName: String, val fineGuideline: String) {
    CRITICAL("Critical", "Seizure / Mandatory Compound Fine ₹25,000+"),
    MAJOR("Major", "Compound Fine ₹5,000 - ₹25,000"),
    MINOR("Minor", "Rectification Notice / Fine up to ₹5,000")
}

enum class UserRole(val title: String, val permissions: String) {
    STANDARD_USER("User", "Scan Packages, Verify Declarations, Check MRP"),
    ENFORCEMENT_OFFICER("Field Enforcement Officer", "Create, Capture, Inspect, Submit"),
    SUPERVISOR("Metrology Supervisor", "Review, Approve, Issue Notice, Override"),
    REVIEWER("Compliance Reviewer", "Verify OCR, Annotate Evidence, Audit"),
    ADMINISTRATOR("System Administrator", "Rule Management, User RBAC, System Audit")
}

enum class ProductCategory(val label: String, val iconName: String) {
    FOOD_BEVERAGES("Food & Beverages", "Restaurant"),
    COSMETICS_PERSONAL_CARE("Cosmetics & Personal Care", "Spa"),
    ELECTRONICS_APPLIANCES("Electronics & Gadgets", "Devices"),
    CHEMICALS_PESTICIDES("Chemicals & Paints", "Science"),
    EDIBLE_OILS_GRAINS("Edible Oils & Commodities", "LocalDrink"),
    ECOMMERCE_LISTING("E-Commerce Listing", "ShoppingBag"),
    GENERAL_MERCHANDISE("General Packaged Goods", "Inventory2")
}

enum class PackageAngle(val label: String) {
    FRONT("Front PDP"),
    BACK("Back Declarations"),
    SIDE("Side Panel"),
    TOP_BOTTOM("Top/Base Stamp"),
    ECOMM_LISTING("Digital Listing")
}

data class BoundingBox(
    val x: Float, // Normalized 0..1
    val y: Float,
    val width: Float,
    val height: Float,
    val fieldKey: String,
    val text: String,
    val confidence: Float,
    val status: ComplianceStatus = ComplianceStatus.PASS
)

data class QualityMetrics(
    val overallScore: Int, // 0..100
    val sharpnessScore: Int,
    val lightingScore: Int,
    val glareScore: Int,
    val readabilityRating: String, // "Excellent", "Adequate", "Marginal", "Blurry"
    val isAcceptableForLegalEvidence: Boolean
)
