package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.models.ComplianceStatus
import com.example.data.models.PackageAngle
import com.example.data.models.ProductCategory
import com.example.data.models.RuleSeverity
import com.example.data.models.UserRole

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val name: String,
    val email: String,
    val badgeNumber: String,
    val role: UserRole,
    val department: String,
    val avatarColorHex: String = "#0284C7",
    val pin: String = "123456",
    val passwordHash: String = "password123",
    val phone: String = "+91 98765 43210",
    val stationJurisdiction: String = "Delhi Central Zone",
    val lastLoginTimestamp: Long = System.currentTimeMillis(),
    val isCurrentSession: Boolean = false,
    val isActive: Boolean = true
)

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: String,
    val name: String,
    val brand: String,
    val category: ProductCategory,
    val barcode: String? = null,
    val batchNumber: String? = null,
    val pdpAreaCm2: Double = 120.0,
    val packageType: String = "Carton/Pouch"
)

@Entity(tableName = "inspections")
data class InspectionEntity(
    @PrimaryKey val id: String,
    val inspectionNumber: String, // e.g. "INSP-2026-0842"
    val productId: String,
    val productName: String,
    val brand: String,
    val category: ProductCategory,
    val officerId: String,
    val officerName: String,
    val status: ComplianceStatus,
    val overallConfidence: Float,
    val complianceScore: Int, // 0..100
    val timestamp: Long = System.currentTimeMillis(),
    val location: String = "Zonal Metrology Testing Lab & Field Station #4",
    val totalRulesChecked: Int = 10,
    val violationsCount: Int = 0,
    val reviewRequiredCount: Int = 0,
    val reviewNotes: String = "",
    val isFinalized: Boolean = false,
    val signedOffBy: String? = null,
    val penaltyAmount: Double = 0.0,
    val noticeNumber: String? = null
)

@Entity(tableName = "inspection_images")
data class InspectionImageEntity(
    @PrimaryKey val id: String,
    val inspectionId: String,
    val imageUri: String,
    val angle: PackageAngle,
    val qualityScore: Int,
    val sharpnessScore: Int,
    val glareScore: Int,
    val readabilityRating: String
)

@Entity(tableName = "ocr_results")
data class OcrResultEntity(
    @PrimaryKey val id: String,
    val inspectionId: String,
    val imageId: String,
    val rawText: String,
    val detectedLanguage: String = "en",
    val confidence: Float,
    val boundingBoxesJson: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "scanned_label_ocr_records")
data class ScannedLabelOcrEntity(
    @PrimaryKey val id: String,
    val imagePath: String,
    val timestamp: Long = System.currentTimeMillis(),
    val extractedRawText: String,
    val linesCount: Int = 0,
    val blocksCount: Int = 0,
    val detectedLanguage: String = "en",
    val confidence: Float = 0.95f,
    val executionTimeMs: Long = 0L,
    val productName: String? = null,
    val brand: String? = null,
    val netQuantity: String? = null,
    val mrp: String? = null,
    val dateOfMfg: String? = null,
    val consumerCare: String? = null,
    val countryOfOrigin: String? = null,
    val unitSalePrice: String? = null,
    val boundingBoxesJson: String = "[]",
    val inspectionId: String? = null,
    val scanSource: String = "CAMERA_CAPTURE"
)

@Entity(tableName = "declarations")
data class DeclarationEntity(
    @PrimaryKey val id: String,
    val inspectionId: String,
    val fieldKey: String, // e.g. "NET_QUANTITY", "MRP", "CONSUMER_CARE"
    val fieldName: String,
    val extractedValue: String,
    val correctedValue: String? = null,
    val confidence: Float = 0.95f,
    val isVerified: Boolean = false,
    val boundingBoxJson: String? = null,
    val imageId: String? = null,
    val sourceRuleCode: String = "LM-PC-6"
)

@Entity(tableName = "compliance_checks")
data class ComplianceCheckEntity(
    @PrimaryKey val id: String,
    val inspectionId: String,
    val ruleId: String,
    val ruleCode: String,
    val ruleTitle: String,
    val ruleVersion: String,
    val severity: RuleSeverity,
    val status: ComplianceStatus,
    val findingMessage: String,
    val legalSection: String,
    val evidenceDeclarationKey: String,
    val evidenceConfidence: Float,
    val manualOverrideStatus: ComplianceStatus? = null,
    val officerComment: String? = null
)

@Entity(tableName = "rules")
data class RuleEntity(
    @PrimaryKey val id: String,
    val ruleCode: String, // e.g. "LM-PC-6-1-E"
    val title: String,
    val description: String,
    val category: ProductCategory?,
    val legalSource: String, // e.g. "Legal Metrology (Packaged Commodities) Rules, 2011"
    val sectionReference: String, // e.g. "Rule 6(1)(e) & Section 36"
    val ruleVersion: String = "v3.2-2024",
    val effectiveDate: String = "2024-01-01",
    val severity: RuleSeverity,
    val isActive: Boolean = true,
    val validationPattern: String? = null
)

@Entity(tableName = "audit_logs")
data class AuditLogEntity(
    @PrimaryKey val id: String,
    val inspectionId: String?,
    val actionType: String, // "CREATED", "OCR_EXTRACTED", "RULE_EVALUATED", "HUMAN_OVERRIDE", "NOTICE_ISSUED"
    val performedBy: String,
    val userRole: String,
    val details: String,
    val timestamp: Long = System.currentTimeMillis()
)
