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

enum class AppThemeMode(val title: String) {
    SYSTEM("System Default"),
    LIGHT("Light Mode"),
    DARK("Dark Mode")
}

enum class ConnectivityStatus(
    val displayName: String,
    val hexColor: String
) {
    ONLINE("Online", "#10B981"),
    OFFLINE("Offline", "#EF4444"),
    CHECKING("Checking...", "#F59E0B"),
    SYNCING("Syncing", "#F59E0B"),
    SYNCED("Synced", "#3B82F6")
}

enum class ConnectivityBannerEvent {
    NONE,
    OFFLINE_WARNING,
    ONLINE_RECOVERY
}

enum class SyncItemStatus {
    PENDING_SYNC,
    SYNCING,
    SYNCED,
    FAILED
}

enum class NetworkConnectivityMode(val displayName: String) {
    ONLINE_CLOUD("Remote Compliance Cloud Active"),
    PROCESSING_API("Syncing Remote Legal Metrology API"),
    OFFLINE_LOCAL("Offline Local Rules Engine")
}

data class NetworkConnectivityState(
    val status: ConnectivityStatus = ConnectivityStatus.ONLINE,
    val mode: NetworkConnectivityMode = NetworkConnectivityMode.ONLINE_CLOUD,
    val isConnected: Boolean = true,
    val isApiProcessing: Boolean = false,
    val pingMs: Int = 28,
    val activeEndpoint: String = "api.metrology.gov.in",
    val activeApiGateway: String = "National Legal Metrology & ONDC Compliance Gateway",
    val lastSyncTimestamp: Long = System.currentTimeMillis(),
    val pendingSyncCount: Int = 0,
    val syncedCount: Int = 0,
    val bannerEvent: ConnectivityBannerEvent = ConnectivityBannerEvent.NONE,
    val bannerMessage: String? = null,
    val errorMessage: String? = null,
    val isManualOfflineSimulated: Boolean = false
)

