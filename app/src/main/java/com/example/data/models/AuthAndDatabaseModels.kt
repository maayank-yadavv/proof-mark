package com.example.data.models

data class DatabaseStatsInfo(
    val totalInspections: Int = 0,
    val totalProducts: Int = 0,
    val totalDeclarations: Int = 0,
    val totalComplianceChecks: Int = 0,
    val totalRules: Int = 0,
    val totalAuditLogs: Int = 0,
    val totalUsers: Int = 0,
    val totalImages: Int = 0,
    val totalOcrResults: Int = 0,
    val totalScannedOcrRecords: Int = 0,
    val databaseName: String = "proofmark_database.db",
    val estimatedSizeBytes: Long = 0L,
    val lastBackupTimestamp: Long = System.currentTimeMillis()
)

sealed class AuthState {
    data object Unauthenticated : AuthState()
    data object Loading : AuthState()
    data class Authenticated(val user: com.example.data.local.entities.UserEntity) : AuthState()
    data class Error(val message: String) : AuthState()
}

data class AuthSession(
    val isAuthenticated: Boolean = false,
    val currentUser: com.example.data.local.entities.UserEntity? = null,
    val sessionStartedAt: Long = 0L,
    val authMethod: String = "PASSWORD" // "PASSWORD", "PIN", "BIOMETRIC", "DEMO_SWITCH"
)
