package com.example.data.models

import androidx.compose.ui.graphics.Color

/**
 * Reliability & Confidence Scale for Product Intelligence Retrieval
 */
enum class ReliabilityLevel(
    val title: String,
    val description: String,
    val colorHex: Long
) {
    HIGH_VERIFIED("High Confidence (Verified)", "Multiple official cross-references match scanned product.", 0xFF10B981),
    MEDIUM_MATCH("Medium Confidence (Likely Match)", "Scanned info matches general catalog specs with minor variance.", 0xFFF59E0B),
    LOW_UNCERTAIN("Low Confidence (Unverified)", "Limited online records found; user verification strongly advised.", 0xFFEF4444),
    UNAVAILABLE("Data Unavailable", "No authoritative online record available for this product or barcode.", 0xFF6B7280)
}

/**
 * Status of scan vs online comparison fields
 */
enum class ComparisonStatus(val label: String, val colorHex: Long) {
    MATCH("Match", 0xFF10B981),
    MISMATCH("Mismatch", 0xFFEF4444),
    SCAN_ONLY("Scan Only", 0xFF3B82F6),
    ONLINE_ONLY("Online Only", 0xFF8B5CF6),
    NOT_AVAILABLE("N/A", 0xFF6B7280)
}

/**
 * Single field comparison between scanned physical package and online benchmark
 */
data class ScanVsOnlineRow(
    val fieldName: String,
    val scannedValue: String,
    val onlineValue: String,
    val status: ComparisonStatus,
    val note: String? = null
)

/**
 * Online Pricing Intelligence
 */
data class OnlinePriceInfo(
    val mrp: Double?,
    val currentOnlinePrice: Double?,
    val pricePerUnit: String?,
    val priceRange: String?,
    val priceSource: String,
    val isOverpriced: Boolean = false
)

/**
 * Manufacturer and Brand Intelligence
 */
data class ManufacturerIntel(
    val name: String,
    val address: String?,
    val packerNameAddress: String? = null,
    val importerNameAddress: String? = null,
    val countryOfOrigin: String,
    val customerCarePhone: String? = null,
    val customerCareEmail: String? = null
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
 * Composition, Ingredients and Materials
 */
data class ProductCompositionIntel(
    val ingredientsList: List<String> = emptyList(),
    val activeMaterials: List<String> = emptyList(),
    val allergens: List<String> = emptyList(),
    val netQuantity: String,
    val grossQuantity: String? = null,
    val packagingType: String? = null,
    val dimensions: String? = null
)

/**
 * Certifications and Statutory Compliance Indicators
 */
enum class VerificationStatus { VERIFIED, PENDING, EXPIRED, NOT_FOUND }

data class CertificationItem(
    val title: String,
    val identifierNumber: String?,
    val issuingBody: String,
    val status: VerificationStatus
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
    val sourceType: String, // e.g. "Official Brand Catalog", "Govt BIS Database", "E-Commerce Aggregator"
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
    val manufacturer: String,
    val category: String,
    val description: String,
    val sourceName: String,
    val imageUrl: String? = null
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
    val manufacturer: ManufacturerIntel?,
    val usagePurpose: ProductUsagePurpose?,
    val composition: ProductCompositionIntel?,
    val certifications: List<CertificationItem> = emptyList(),
    val legalMetrologyDeclarations: List<LegalMetrologyDeclarationItem> = emptyList(),
    val sources: List<IntelSource> = emptyList(),
    val conflicts: List<IntelConflict> = emptyList()
)
