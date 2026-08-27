package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fssai_licenses")
data class FssaiLicenseEntity(
    @PrimaryKey val licenseNumber: String, // 14-digit FSSAI License Number
    val companyName: String,
    val brandName: String = "",
    val premisesAddress: String,
    val licenseType: String, // "Central License", "State License", "Registration"
    val kindOfBusiness: String,
    val state: String = "Gujarat",
    val issueDate: String = "2021-04-12",
    val expiryDate: String = "2028-04-11",
    val status: String = "ACTIVE", // "ACTIVE", "EXPIRED", "SUSPENDED", "CANCELLED", "UNDER_REVIEW"
    val foodCategories: String = "01 - Dairy products, 12 - Salts & Spices",
    val isFortifiedCertified: Boolean = true, // +F Logo
    val fssaiLogoVerifiedOnPack: Boolean = true,
    val contactEmail: String = "compliance@company.com",
    val contactPhone: String = "+91 1800 22 4356",
    val remarks: String = "Valid license issued under FSS Act 2006",
    val lastSyncTimestamp: Long = System.currentTimeMillis()
)
