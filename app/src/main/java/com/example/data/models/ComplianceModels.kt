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

enum class CameraScanMode(val title: String, val subtitle: String, val badgeText: String) {
    LIVE_OCR("1. Live OCR Detection", "Camera opens with live bounding box overlay. Detects statutory fields, MRP, Qty, and blur status continuously in real time.", "REAL-TIME"),
    IMAGE_OCR("2. Image OCR Detection", "Take a photo first. Deep Proof ML Kit OCR extracts package details, finds commodity specs, and integrates with online Proof AI for statutory compliance.", "PROOF AI")
}

data class ImageOcrAiResult(
    val image: android.graphics.Bitmap,
    val ocrResult: com.example.data.ai.MlKitOcrResult,
    val extractedPackageData: com.example.data.ai.ExtractedPackageData,
    val productIntelligence: com.example.data.models.ProductIntelligenceReport? = null,
    val isGeminiOnline: Boolean = false,
    val aiConfidence: Float = 0.95f,
    val executionTimeMs: Long = 0L,
    val rawText: String = "",
    val savedRecordId: String? = null
)

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
