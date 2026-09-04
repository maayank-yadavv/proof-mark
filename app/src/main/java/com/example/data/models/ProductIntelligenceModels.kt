package com.example.data.models

import androidx.compose.ui.graphics.Color

/**
 * Product Classification Type
 */
enum class ProductType(val label: String, val isPhysical: Boolean) {
    PHYSICAL("Physical Packaged Commodity", true),
    DIGITAL_SOFTWARE("Digital Software / Application", false),
    DIGITAL_SUBSCRIPTION("Digital Subscription / SaaS", false),
    DIGITAL_LICENSE("Digital License Key / Certificate", false),
    DIGITAL_CONTENT("Digital Media / E-Book / Audio", false),
    SERVICE("Online / Professional Service", false)
}

/**
 * Reliability & Confidence Scale for Product Intelligence Retrieval
 */
enum class ReliabilityLevel(
    val title: String,
    val description: String,
    val colorHex: Long
) {
    HIGH_VERIFIED("High Confidence (Verified)", "Multiple official cross-references match scanned product.", 0xFF10B981),
    MEDIUM_MATCH("Medium Confidence (Likely Match)", "Scanned info matches verified catalog specs with minor variance.", 0xFFF59E0B),
    LOW_UNCERTAIN("Low Confidence (Unverified)", "Limited records found; packaging evidence verification required.", 0xFFEF4444),
    UNAVAILABLE("Data Unavailable", "No authoritative online record available for this product.", 0xFF6B7280)
}

/**
 * Evidence & Provenance State for individual product attributes
 */
enum class EvidenceState(val label: String, val colorHex: Long) {
    VERIFIED_PACKAGING("Verified from Packaging", 0xFF10B981),
    VERIFIED_TRUSTED_SOURCE("Verified from Trusted Source", 0xFF059669),
    AI_IDENTIFIED("AI Identified (Package)", 0xFF3B82F6),
    ONLINE_SOURCE_FOUND("Online Benchmark Found", 0xFF8B5CF6),
    NOT_PROVIDED("Not Provided", 0xFF6B7280),
    NOT_APPLICABLE("Not Applicable", 0xFF9CA3AF),
    UNABLE_TO_VERIFY("Unable to Verify", 0xFFF59E0B),
    NO_PHYSICAL_PRODUCT("No Physical Product", 0xFF6366F1)
}

/**
 * Status of scan vs online comparison fields
 */
enum class ComparisonStatus(val label: String, val colorHex: Long) {
    MATCH("Match", 0xFF10B981),
    MISMATCH("Mismatch", 0xFFEF4444),
    SCAN_ONLY("Scan Only", 0xFF3B82F6),
    ONLINE_ONLY("Online Only", 0xFF8B5CF6),
    NOT_DETECTED("Not Detected", 0xFFEF4444),
    NOT_PROVIDED("Not Provided", 0xFF6B7280),
    NOT_APPLICABLE("Not Applicable", 0xFF9CA3AF)
}

/**
 * Single field comparison between scanned physical package and online benchmark
 */
data class ScanVsOnlineRow(
    val fieldName: String,
    val scannedValue: String,
    val onlineValue: String,
    val status: ComparisonStatus,
    val note: String? = null,
    val evidenceState: EvidenceState = EvidenceState.VERIFIED_PACKAGING
)

/**
 * Online Pricing Intelligence & Selling Intelligence
 */
data class OnlinePriceInfo(
    val printedMrp: String? = null,
    val onlineMrp: Double? = null,
    val currentOnlinePrice: Double? = null,
    val discountPercent: Int? = null,
    val priceDifference: Double? = null,
    val pricePerUnit: String? = null,
    val priceRange: String? = null,
    val priceSource: String = "Not Provided",
    val lastCheckedTimestamp: String = "Recently Verified",
    val availableSellers: List<String> = emptyList(),
    val isAvailableOnline: Boolean = true,
    val isOverpriced: Boolean = false
)

/**
 * Complete Warranty Information
 */
data class WarrantyIntel(
    val duration: String,
    val fullTerms: String,
    val conditions: List<String> = emptyList(),
    val exclusions: List<String> = emptyList(),
    val supportPhone: String? = null,
    val supportEmail: String? = null,
    val supportWebsite: String? = null,
    val isProvided: Boolean = true
)

/**
 * Manufacturer, Importer, Exporter, Building & Technology Intelligence
 */
data class ManufacturerIntel(
    val name: String,
    val address: String? = null,
    val packerNameAddress: String? = null,
    val importerName: String? = null,
    val importerAddress: String? = null,
    val exporterName: String? = null,
    val exporterAddress: String? = null,
    val countryOfOrigin: String,
    val manufacturingLocation: String? = null,
    val buildingAndTechDetails: String? = null,
    val licenseNumber: String? = null,
    val registrationNumber: String? = null,
    val customerCarePhone: String? = null,
    val customerCareEmail: String? = null,
    val customerCareWebsite: String? = null
)

/**
 * Manufacturing & Supply Chain Identification
 */
data class ManufacturingSupplyIntel(
    val modelNumber: String? = null,
    val variant: String? = null,
    val skuOrPartNumber: String? = null,
    val batchOrLotNumber: String? = null,
    val manufacturingDate: String? = null,
    val expiryDate: String? = null,
    val serialOrIdentification: String? = null,
    val distributorOrSupplyChain: String? = null,
    val manufacturingCompliance: String? = null
)

/**
 * Purpose and Usage Details
 */
data class ProductUsagePurpose(
    val category: String,
    val purposeSummary: String,
    val targetAudience: String? = null,
    val storageInstructions: String? = null,
    val directionsForUse: String? = null
)

/**
 * Composition, Ingredients, Materials and Packaging
 */
data class ProductCompositionIntel(
    val ingredientsList: List<String> = emptyList(),
    val activeMaterials: List<String> = emptyList(),
    val allergens: List<String> = emptyList(),
    val netQuantity: String,
    val grossQuantity: String? = null,
    val packagingType: String? = null,
    val dimensions: String? = null,
    val material: String? = null,
    val isPhysicalProduct: Boolean = true
)

/**
 * Certifications and Statutory Compliance Indicators
 */
enum class VerificationStatus(val label: String, val colorHex: Long) {
    VERIFIED("Verified", 0xFF10B981),
    FOUND_ON_PACKAGING("Found on Packaging", 0xFF059669),
    VERIFIED_ONLINE("Verified Online", 0xFF3B82F6),
    PENDING("Verification Pending", 0xFFF59E0B),
    EXPIRED("Expired", 0xFFEF4444),
    NOT_PROVIDED("Not Provided", 0xFF6B7280),
    NOT_APPLICABLE("Not Applicable", 0xFF9CA3AF)
}

data class CertificationItem(
    val title: String,
    val identifierNumber: String?,
    val issuingBody: String,
    val status: VerificationStatus,
    val evidenceNote: String? = null
)

/**
 * Statutory Legal Metrology Declarations Benchmark
 */
data class LegalMetrologyDeclarationItem(
    val declarationName: String,
    val requiredByLaw: Boolean,
    val onlineValue: String,
    val ruleReference: String,
    val status: ComparisonStatus
)

/**
 * Source Transparency Record
 */
data class IntelSource(
    val sourceName: String,
    val sourceType: String,
    val retrievedDate: String,
    val reliabilityLevel: ReliabilityLevel,
    val urlOrReference: String? = null
)

/**
 * Detected Discrepancy / Conflict
 */
data class IntelConflict(
    val fieldName: String,
    val description: String,
    val recommendedAuthority: String
)

/**
 * Candidate match when search yields multiple potential products
 */
data class ProductMatchCandidate(
    val product: OnlineProductModel,
    val matchConfidence: Int,
    val matchReason: String
)

/**
 * Online Product Representation
 */
data class OnlineProductModel(
    val barcode: String?,
    val productName: String,
    val brand: String,
    val model: String? = null,
    val variant: String? = null,
    val sku: String? = null,
    val manufacturer: String,
    val category: String,
    val productType: ProductType = ProductType.PHYSICAL,
    val description: String,
    val sourceName: String,
    val imageUrl: String? = null,
    val specifications: Map<String, String> = emptyMap()
)

/**
 * Master Aggregated Product Intelligence Report
 */
data class ProductIntelligenceReport(
    val query: String,
    val primaryMatch: OnlineProductModel?,
    val possibleMatches: List<ProductMatchCandidate> = emptyList(),
    val overallConfidenceLevel: ReliabilityLevel,
    val confidenceScorePercent: Int,
    val confidenceReasons: List<String> = emptyList(),
    val scanComparison: List<ScanVsOnlineRow> = emptyList(),
    val pricing: OnlinePriceInfo?,
    val warranty: WarrantyIntel?,
    val manufacturer: ManufacturerIntel?,
    val supplyChain: ManufacturingSupplyIntel?,
    val usagePurpose: ProductUsagePurpose?,
    val composition: ProductCompositionIntel?,
    val certifications: List<CertificationItem> = emptyList(),
    val legalMetrologyDeclarations: List<LegalMetrologyDeclarationItem> = emptyList(),
    val sources: List<IntelSource> = emptyList(),
    val conflicts: List<IntelConflict> = emptyList(),
    val productType: ProductType = ProductType.PHYSICAL,
    val isPhysicalProduct: Boolean = true,
    val lastUpdated: String = "Live Verification"
)
