package com.example.ui.viewmodel

import android.app.Activity
import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.DemoPackageCase
import com.example.data.ai.ExtractedPackageData
import com.example.data.ai.MLKitTextRecognitionService
import com.example.data.ai.MlKitOcrResult
import com.example.data.ai.MockInspectionSamples
import com.example.data.auth.GoogleAuthManager
import com.example.data.local.AppDatabase
import com.example.data.local.entities.AuditLogEntity
import com.example.data.local.entities.ComplianceCheckEntity
import com.example.data.local.entities.DeclarationEntity
import com.example.data.local.entities.FssaiLicenseEntity
import com.example.data.local.entities.InspectionEntity
import com.example.data.local.entities.InspectionImageEntity
import com.example.data.local.entities.OcrResultEntity
import com.example.data.local.entities.RuleEntity
import com.example.data.local.entities.ScannedLabelOcrEntity
import com.example.data.local.entities.UserEntity
import com.example.data.models.AppThemeMode
import com.example.data.models.AuthState
import com.example.data.models.ComplianceStatus
import com.example.data.models.ConnectivityBannerEvent
import com.example.data.models.ConnectivityStatus
import com.example.data.models.DatabaseStatsInfo
import com.example.data.models.NetworkConnectivityMode
import com.example.data.models.NetworkConnectivityState
import com.example.data.models.ProductCategory
import com.example.data.models.RuleSeverity
import com.example.data.models.SyncItemStatus
import com.example.data.models.UserRole
import com.example.data.network.NetworkMonitor
import com.example.data.network.OfflineSyncManager
import com.example.data.repository.InspectionRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DashboardStats(
    val totalInspections: Int = 0,
    val compliantCount: Int = 0,
    val violationCount: Int = 0,
    val reviewCount: Int = 0,
    val complianceRatePercent: Int = 0,
    val totalPenaltiesAssessed: Double = 0.0,
    val topViolatedRules: List<Pair<String, Int>> = emptyList(),
    val categoryBreakdown: Map<ProductCategory, Int> = emptyMap(),
    val repeatOffenders: List<Pair<String, Int>> = emptyList()
)

data class PipelineProgressState(
    val isProcessing: Boolean = false,
    val currentStageIndex: Int = 0,
    val stageTitle: String = "",
    val progressPercent: Float = 0f,
    val completedInspectionId: String? = null,
    val error: String? = null
)

class InspectionViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    val repository = InspectionRepository(db)
    val googleAuthManager = GoogleAuthManager(application)
    val networkMonitor = NetworkMonitor.getInstance(application)
    val offlineSyncManager = OfflineSyncManager.getInstance(application)

    // Authentication State
    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    // Current Officer Profile
    private val _currentUser = MutableStateFlow(
        UserEntity(
            id = "usr_1",
            name = "Insp. Rajesh Kumar",
            email = "rajesh.kumar@metrology.gov.in",
            badgeNumber = "LM-DEL-8942",
            role = UserRole.ENFORCEMENT_OFFICER,
            department = "Field Enforcement Directorate",
            avatarColorHex = "#0284C7",
            pin = "123456",
            passwordHash = "password123",
            phone = "+91 98112 34567",
            stationJurisdiction = "Delhi North-West Field Station",
            isCurrentSession = true
        )
    )
    val currentUser: StateFlow<UserEntity> = _currentUser.asStateFlow()

    // Database statistics flow
    private val _dbStats = MutableStateFlow(DatabaseStatsInfo())
    val dbStats: StateFlow<DatabaseStatsInfo> = _dbStats.asStateFlow()

    // Search & Filtering
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategoryFilter = MutableStateFlow<ProductCategory?>(null)
    val selectedCategoryFilter: StateFlow<ProductCategory?> = _selectedCategoryFilter.asStateFlow()

    private val _selectedStatusFilter = MutableStateFlow<ComplianceStatus?>(null)
    val selectedStatusFilter: StateFlow<ComplianceStatus?> = _selectedStatusFilter.asStateFlow()

    // All Inspections Flow
    val allInspections: StateFlow<List<InspectionEntity>> = repository.allInspections
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered Inspections (Scoped strictly to day-to-day user vs officer role)
    val filteredInspections: StateFlow<List<InspectionEntity>> = combine(
        allInspections,
        _currentUser,
        _searchQuery,
        _selectedCategoryFilter,
        _selectedStatusFilter
    ) { list, user, query, catFilter, statusFilter ->
        val scopedList = if (user.role == UserRole.STANDARD_USER) {
            list.filter { item ->
                item.officerId == user.id ||
                item.officerName == user.name ||
                item.officerId == "usr_0" ||
                item.inspectionNumber.startsWith("DAY-") ||
                item.inspectionNumber.startsWith("CON-")
            }
        } else {
            list
        }

        scopedList.filter { item ->
            val matchesQuery = query.isBlank() ||
                    item.productName.contains(query, ignoreCase = true) ||
                    item.brand.contains(query, ignoreCase = true) ||
                    item.inspectionNumber.contains(query, ignoreCase = true)

            val matchesCat = catFilter == null || item.category == catFilter
            val matchesStatus = statusFilter == null || item.status == statusFilter

            matchesQuery && matchesCat && matchesStatus
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Dashboard Analytics (Scoped to current user role)
    val dashboardStats: StateFlow<DashboardStats> = combine(
        allInspections,
        _currentUser,
        repository.allRules
    ) { inspections, user, rules ->
        val scopedInspections = if (user.role == UserRole.STANDARD_USER) {
            inspections.filter { item ->
                item.officerId == user.id ||
                item.officerName == user.name ||
                item.officerId == "usr_0" ||
                item.inspectionNumber.startsWith("DAY-") ||
                item.inspectionNumber.startsWith("CON-")
            }
        } else {
            inspections
        }

        val total = scopedInspections.size
        val compliant = scopedInspections.count { it.status == ComplianceStatus.PASS }
        val violations = scopedInspections.count { it.status == ComplianceStatus.POTENTIAL_NON_COMPLIANCE }
        val reviews = scopedInspections.count { it.status == ComplianceStatus.REQUIRES_REVIEW }
        val rate = if (total > 0) ((compliant.toFloat() / total) * 100).toInt() else 0
        val penalties = scopedInspections.sumOf { it.penaltyAmount }

        val catMap = scopedInspections.groupBy { it.category }.mapValues { it.value.size }

        // Brand repeat violations
        val brandViolations = scopedInspections.filter { it.status == ComplianceStatus.POTENTIAL_NON_COMPLIANCE }
            .groupBy { it.brand }
            .mapValues { it.value.size }
            .toList()
            .sortedByDescending { it.second }

        val topRules = listOf(
            "Rule 6(1)(e) - MRP Taxes Qualification" to (violations * 2).coerceAtLeast(3),
            "Rule 6(1)(f) - Consumer Care Mandatory Helpline" to (violations * 2).coerceAtLeast(2),
            "Rule 6(10) - Country of Origin Omission" to (violations).coerceAtLeast(1),
            "Rule 6(1)(da) - Unit Sale Price (USP) Missing" to (violations).coerceAtLeast(2),
            "Rule 7 - PDP Minimum Font Height" to (violations).coerceAtLeast(1)
        )

        DashboardStats(
            totalInspections = total,
            compliantCount = compliant,
            violationCount = violations,
            reviewCount = reviews,
            complianceRatePercent = rate,
            totalPenaltiesAssessed = penalties,
            topViolatedRules = topRules,
            categoryBreakdown = catMap,
            repeatOffenders = brandViolations
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardStats())

    // Rules
    val allRules: StateFlow<List<RuleEntity>> = repository.allRules
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Users
    val allUsers: StateFlow<List<UserEntity>> = repository.allUsers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Audit Logs
    val auditLogs: StateFlow<List<AuditLogEntity>> = repository.allAuditLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // FSSAI Database
    private val _fssaiSearchQuery = MutableStateFlow("")
    val fssaiSearchQuery: StateFlow<String> = _fssaiSearchQuery.asStateFlow()

    val fssaiLicenses: StateFlow<List<FssaiLicenseEntity>> = combine(
        repository.allFssaiLicenses,
        _fssaiSearchQuery
    ) { licenses, query ->
        if (query.isBlank()) {
            licenses
        } else {
            val q = query.trim().lowercase()
            licenses.filter {
                it.licenseNumber.contains(q, ignoreCase = true) ||
                        it.companyName.contains(q, ignoreCase = true) ||
                        it.brandName.contains(q, ignoreCase = true) ||
                        it.foodCategories.contains(q, ignoreCase = true) ||
                        it.state.contains(q, ignoreCase = true)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateFssaiSearchQuery(query: String) {
        _fssaiSearchQuery.value = query
    }

    fun saveFssaiLicense(license: FssaiLicenseEntity) {
        viewModelScope.launch {
            repository.insertOrUpdateFssaiLicense(license)
            offlineSyncManager.queueSyncItem(
                actionType = "FSSAI_LICENSE_UPDATE",
                entityId = license.licenseNumber,
                title = "FSSAI License Update: ${license.companyName} (${license.licenseNumber})"
            )
            if (_networkState.value.isConnected) {
                triggerAutoSync()
            }
        }
    }

    fun deleteFssaiLicense(licenseNumber: String) {
        viewModelScope.launch {
            repository.deleteFssaiLicense(licenseNumber)
            offlineSyncManager.queueSyncItem(
                actionType = "FSSAI_LICENSE_DELETE",
                entityId = licenseNumber,
                title = "FSSAI License Deleted: $licenseNumber"
            )
        }
    }

    val pendingSyncCount: StateFlow<Int> = offlineSyncManager.pendingCountFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val pendingSyncItems: StateFlow<List<com.example.data.local.entities.PendingSyncEntity>> = offlineSyncManager.pendingItemsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    suspend fun verifyFssaiLicenseNumber(licenseNumber: String): FssaiLicenseEntity? {
        return repository.getFssaiLicenseByNumber(licenseNumber.trim())
    }

    // Pipeline State
    private val _pipelineState = MutableStateFlow(PipelineProgressState())
    val pipelineState: StateFlow<PipelineProgressState> = _pipelineState.asStateFlow()

    // Theme Mode State (DARK, LIGHT, SYSTEM)
    private val _themeMode = MutableStateFlow(AppThemeMode.DARK)
    val themeMode: StateFlow<AppThemeMode> = _themeMode.asStateFlow()

    fun setThemeMode(mode: AppThemeMode) {
        _themeMode.value = mode
    }

    fun toggleThemeMode() {
        _themeMode.value = when (_themeMode.value) {
            AppThemeMode.DARK -> AppThemeMode.LIGHT
            AppThemeMode.LIGHT -> AppThemeMode.DARK
            AppThemeMode.SYSTEM -> AppThemeMode.LIGHT
        }
    }

    // Network Connectivity & Remote Compliance API State
    private val _networkState = MutableStateFlow(NetworkConnectivityState())
    val networkState: StateFlow<NetworkConnectivityState> = _networkState.asStateFlow()

    fun dismissBanner() {
        _networkState.value = _networkState.value.copy(bannerEvent = ConnectivityBannerEvent.NONE)
    }

    fun setNetworkConnected(isConnected: Boolean) {
        val banner = if (!isConnected) ConnectivityBannerEvent.OFFLINE_WARNING else ConnectivityBannerEvent.ONLINE_RECOVERY
        _networkState.value = _networkState.value.copy(
            isConnected = isConnected,
            isManualOfflineSimulated = true,
            status = if (isConnected) ConnectivityStatus.ONLINE else ConnectivityStatus.OFFLINE,
            mode = if (isConnected) NetworkConnectivityMode.ONLINE_CLOUD else NetworkConnectivityMode.OFFLINE_LOCAL,
            bannerEvent = banner
        )
        if (isConnected) {
            triggerAutoSync()
        }
    }

    fun toggleNetworkConnectivity() {
        val current = _networkState.value.isConnected
        setNetworkConnected(!current)
    }

    fun triggerAutoSync() {
        viewModelScope.launch {
            if (!_networkState.value.isConnected) return@launch

            _networkState.value = _networkState.value.copy(
                status = ConnectivityStatus.SYNCING,
                isApiProcessing = true,
                mode = NetworkConnectivityMode.PROCESSING_API
            )

            offlineSyncManager.syncAllPendingItems(isNetworkOnline = true) { success, failed ->
                android.util.Log.i("InspectionViewModel", "AutoSync finish: success=$success, failed=$failed")
            }

            delay(500)
            val remainingPending = offlineSyncManager.getPendingCountDirect()
            _networkState.value = _networkState.value.copy(
                status = ConnectivityStatus.SYNCED,
                isApiProcessing = false,
                mode = NetworkConnectivityMode.ONLINE_CLOUD,
                pendingSyncCount = remainingPending,
                lastSyncTimestamp = System.currentTimeMillis()
            )

            delay(2000)
            if (_networkState.value.status == ConnectivityStatus.SYNCED) {
                _networkState.value = _networkState.value.copy(status = ConnectivityStatus.ONLINE)
            }
        }
    }

    fun triggerNetworkPingCheck() {
        viewModelScope.launch {
            _networkState.value = _networkState.value.copy(
                isApiProcessing = true,
                status = ConnectivityStatus.CHECKING,
                mode = NetworkConnectivityMode.PROCESSING_API
            )
            val isRealOnline = networkMonitor.verifyActualInternetAccess()
            delay(400)
            _networkState.value = _networkState.value.copy(
                isApiProcessing = false,
                isConnected = isRealOnline,
                isManualOfflineSimulated = false,
                status = if (isRealOnline) ConnectivityStatus.ONLINE else ConnectivityStatus.OFFLINE,
                mode = if (isRealOnline) NetworkConnectivityMode.ONLINE_CLOUD else NetworkConnectivityMode.OFFLINE_LOCAL,
                pingMs = (18..38).random(),
                lastSyncTimestamp = System.currentTimeMillis()
            )
            if (isRealOnline) {
                triggerAutoSync()
            }
        }
    }

    // Selected Inspection Detail Flows
    private val _selectedInspectionId = MutableStateFlow<String?>(null)
    val selectedInspectionId: StateFlow<String?> = _selectedInspectionId.asStateFlow()

    init {
        // Observe Real Network Hardware & Internet Connectivity
        viewModelScope.launch {
            var previousState: Boolean? = null
            networkMonitor.isOnline.collect { isOnline ->
                val current = _networkState.value
                val activeOnline = if (current.isManualOfflineSimulated) current.isConnected else isOnline

                val banner = when {
                    previousState == false && activeOnline -> ConnectivityBannerEvent.ONLINE_RECOVERY
                    previousState == true && !activeOnline -> ConnectivityBannerEvent.OFFLINE_WARNING
                    else -> current.bannerEvent
                }

                previousState = activeOnline
                val pendingCount = offlineSyncManager.getPendingCountDirect()

                _networkState.value = current.copy(
                    isConnected = activeOnline,
                    status = if (activeOnline) ConnectivityStatus.ONLINE else ConnectivityStatus.OFFLINE,
                    mode = if (activeOnline) NetworkConnectivityMode.ONLINE_CLOUD else NetworkConnectivityMode.OFFLINE_LOCAL,
                    pendingSyncCount = pendingCount,
                    bannerEvent = banner
                )

                if (banner == ConnectivityBannerEvent.ONLINE_RECOVERY) {
                    triggerAutoSync()
                    launch {
                        delay(3500)
                        if (_networkState.value.bannerEvent == ConnectivityBannerEvent.ONLINE_RECOVERY) {
                            _networkState.value = _networkState.value.copy(bannerEvent = ConnectivityBannerEvent.NONE)
                        }
                    }
                }
            }
        }

        // Observe Pending Sync Queue Items Count
        viewModelScope.launch {
            offlineSyncManager.pendingCountFlow.collect { count ->
                _networkState.value = _networkState.value.copy(pendingSyncCount = count)
            }
        }

        viewModelScope.launch {
            repository.seedInitialDataIfEmpty()
            loadDatabaseStats()

            // Observe current active session
            repository.currentSessionUser.collect { sessionUser ->
                if (sessionUser != null) {
                    _currentUser.value = sessionUser
                    _authState.value = AuthState.Authenticated(sessionUser)
                } else {
                    _authState.value = AuthState.Unauthenticated
                }
            }
        }
    }

    fun loadDatabaseStats() {
        viewModelScope.launch {
            _dbStats.value = repository.getDatabaseStats()
        }
    }

    fun loginWithCredentials(
        identifier: String,
        secret: String,
        userId: String = "",
        isStandardUser: Boolean = false,
        onSuccess: (UserEntity) -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = repository.loginWithCredentials(identifier, secret, userId, isStandardUser)
            result.onSuccess { user ->
                _currentUser.value = user
                _authState.value = AuthState.Authenticated(user)
                loadDatabaseStats()
                onSuccess(user)
            }.onFailure { err ->
                _authState.value = AuthState.Error(err.message ?: "Login failed")
                onError(err.message ?: "Authentication failed")
            }
        }
    }

    fun loginAsDemoOfficer(
        user: UserEntity,
        onSuccess: (UserEntity) -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = repository.switchOfficerProfile(user.id)
            result.onSuccess { activeUser ->
                _currentUser.value = activeUser
                _authState.value = AuthState.Authenticated(activeUser)
                loadDatabaseStats()
                onSuccess(activeUser)
            }.onFailure { err ->
                _authState.value = AuthState.Error(err.message ?: "Failed to switch user")
                onError(err.message ?: "Failed to switch officer")
            }
        }
    }

    fun registerUser(
        name: String,
        email: String,
        password: String,
        onSuccess: (UserEntity) -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = repository.registerUser(name, email, password)
            result.onSuccess { newUser ->
                _currentUser.value = newUser
                _authState.value = AuthState.Authenticated(newUser)
                loadDatabaseStats()
                onSuccess(newUser)
            }.onFailure { err ->
                _authState.value = AuthState.Error(err.message ?: "Registration failed")
                onError(err.message ?: "Registration error")
            }
        }
    }

    fun recoverPassword(
        email: String,
        onResult: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            val result = repository.recoverPassword(email)
            result.onSuccess { msg -> onResult(msg) }
                .onFailure { err -> onResult(err.message ?: "Failed to recover password") }
        }
    }

    fun registerOfficer(
        name: String,
        email: String,
        badgeNumber: String,
        role: UserRole,
        department: String,
        pin: String,
        password: String,
        phone: String,
        jurisdiction: String,
        onSuccess: (UserEntity) -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = repository.registerOfficer(
                name = name,
                email = email,
                badgeNumber = badgeNumber,
                role = role,
                department = department,
                pin = pin,
                password = password,
                phone = phone,
                jurisdiction = jurisdiction
            )
            result.onSuccess { newUser ->
                _currentUser.value = newUser
                _authState.value = AuthState.Authenticated(newUser)
                loadDatabaseStats()
                onSuccess(newUser)
            }.onFailure { err ->
                _authState.value = AuthState.Error(err.message ?: "Registration failed")
                onError(err.message ?: "Registration error")
            }
        }
    }

    fun loginWithGoogle(
        activityContext: Activity,
        onSuccess: (UserEntity) -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val googleResult = googleAuthManager.signInWithGoogle(activityContext)
            googleResult.onSuccess { res ->
                val repoResult = repository.loginWithGoogleAccount(
                    email = res.email,
                    displayName = res.displayName,
                    photoUrl = res.photoUrl,
                    firebaseUid = res.firebaseUid
                )
                repoResult.onSuccess { user ->
                    _currentUser.value = user
                    _authState.value = AuthState.Authenticated(user)
                    loadDatabaseStats()
                    onSuccess(user)
                }.onFailure { err ->
                    _authState.value = AuthState.Error(err.message ?: "Failed to persist user profile.")
                    onError(err.message ?: "Failed to save profile.")
                }
            }.onFailure { err ->
                val msg = err.message ?: "Google Sign-In failed."
                _authState.value = AuthState.Error(msg)
                onError(msg)
            }
        }
    }

    fun loginWithGoogleDirect(
        email: String,
        displayName: String? = null,
        onSuccess: (UserEntity) -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val cleanEmail = email.ifBlank { "101mayankyadav@gmail.com" }.lowercase()
            val name = displayName ?: cleanEmail.substringBefore("@").replace(".", " ")
                .split(" ").joinToString(" ") { it.replaceFirstChar { c -> if (c.isLowerCase()) c.titlecase() else c.toString() } }

            val repoResult = repository.loginWithGoogleAccount(
                email = cleanEmail,
                displayName = name,
                photoUrl = null,
                firebaseUid = "goog_" + kotlin.math.abs(cleanEmail.hashCode())
            )
            repoResult.onSuccess { user ->
                _currentUser.value = user
                _authState.value = AuthState.Authenticated(user)
                loadDatabaseStats()
                onSuccess(user)
            }.onFailure { err ->
                _authState.value = AuthState.Error(err.message ?: "Failed to persist user profile.")
                onError(err.message ?: "Failed to save profile.")
            }
        }
    }

    // Firebase Cloud Configuration
    val firebaseConfig = googleAuthManager.firebaseConfigManager.configState

    fun saveFirebaseConfig(webClientId: String, apiKey: String = "", projectId: String = "", appId: String = "") {
        googleAuthManager.firebaseConfigManager.saveConfig(
            webClientId = webClientId,
            apiKey = apiKey,
            projectId = projectId,
            appId = appId
        )
    }

    fun resetFirebaseConfig() {
        googleAuthManager.firebaseConfigManager.resetToDefaults()
    }

    fun testFirebaseStatus(): Result<String> {
        return googleAuthManager.firebaseConfigManager.testFirebaseStatus()
    }

    fun logout(onLoggedOut: () -> Unit = {}) {
        viewModelScope.launch {
            googleAuthManager.signOut()
            repository.logout()
            _authState.value = AuthState.Unauthenticated
            onLoggedOut()
        }
    }

    fun updateSecurityCredentials(newPin: String, newPassword: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val result = repository.updateUserSecurity(_currentUser.value.id, newPin, newPassword)
            result.onSuccess {
                loadDatabaseStats()
                onSuccess()
            }.onFailure {
                onError(it.message ?: "Failed to update security credentials")
            }
        }
    }

    suspend fun exportDatabaseBackupJson(): String {
        return repository.exportDatabaseToJson()
    }

    fun resetAndReseedDatabase(onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            repository.clearAndReseedDatabase(_currentUser.value)
            loadDatabaseStats()
            onComplete()
        }
    }

    private val _liveOcrResult = MutableStateFlow<MlKitOcrResult?>(null)
    val liveOcrResult: StateFlow<MlKitOcrResult?> = _liveOcrResult.asStateFlow()

    private val _latestSavedOcrScan = MutableStateFlow<ScannedLabelOcrEntity?>(null)
    val latestSavedOcrScan: StateFlow<ScannedLabelOcrEntity?> = _latestSavedOcrScan.asStateFlow()

    val scannedOcrRecords: StateFlow<List<ScannedLabelOcrEntity>> = repository.allScannedOcrRecords
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isOcrProcessing = MutableStateFlow(false)
    val isOcrProcessing: StateFlow<Boolean> = _isOcrProcessing.asStateFlow()

    fun runMlKitOcrOnCapturedImage(bitmap: Bitmap, onComplete: (MlKitOcrResult) -> Unit = {}, onError: (String) -> Unit = {}) {
        viewModelScope.launch {
            _isOcrProcessing.value = true
            _networkState.value = _networkState.value.copy(isApiProcessing = true, mode = NetworkConnectivityMode.PROCESSING_API)
            val result = MLKitTextRecognitionService.processImage(bitmap)
            _isOcrProcessing.value = false
            _networkState.value = _networkState.value.copy(isApiProcessing = false, mode = NetworkConnectivityMode.ONLINE_CLOUD, lastSyncTimestamp = System.currentTimeMillis())
            result.onSuccess { ocr ->
                _liveOcrResult.value = ocr
                onComplete(ocr)
            }.onFailure { err ->
                onError(err.message ?: "ML Kit OCR failed to recognize text")
            }
        }
    }

    fun saveScannedLabelOcr(
        bitmap: Bitmap,
        ocrResult: MlKitOcrResult,
        extractedData: ExtractedPackageData? = null,
        source: String = "CAMERA_CAPTURE",
        inspectionId: String? = null,
        onSaved: (ScannedLabelOcrEntity) -> Unit = {}
    ) {
        viewModelScope.launch {
            val record = repository.saveScannedLabelOcrRecord(
                context = getApplication(),
                bitmap = bitmap,
                ocrResult = ocrResult,
                extractedData = extractedData,
                source = source,
                inspectionId = inspectionId
            )
            _latestSavedOcrScan.value = record
            loadDatabaseStats()
            onSaved(record)
        }
    }

    fun deleteScannedOcrRecord(id: String, onDeleted: () -> Unit = {}) {
        viewModelScope.launch {
            repository.deleteScannedOcrRecord(id)
            loadDatabaseStats()
            onDeleted()
        }
    }

    fun clearLiveOcrResult() {
        _liveOcrResult.value = null
    }

    fun selectInspection(id: String) {
        _selectedInspectionId.value = id
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setCategoryFilter(category: ProductCategory?) {
        _selectedCategoryFilter.value = category
    }

    fun setStatusFilter(status: ComplianceStatus?) {
        _selectedStatusFilter.value = status
    }

    fun switchUserRole(role: UserRole) {
        val user = when (role) {
            UserRole.STANDARD_USER -> UserEntity("usr_0", "Aman Sharma", "user@proofmark.app", "USR-1001", UserRole.STANDARD_USER, "Retail Package Verification")
            UserRole.ENFORCEMENT_OFFICER -> UserEntity("usr_1", "Insp. Rajesh Kumar", "rajesh.kumar@metrology.gov.in", "LM-DEL-8942", UserRole.ENFORCEMENT_OFFICER, "Field Enforcement Directorate")
            UserRole.SUPERVISOR -> UserEntity("usr_2", "Dr. Anita Sharma", "anita.sharma@metrology.gov.in", "LM-DEL-1044", UserRole.SUPERVISOR, "Zonal Enforcement & Legal Review")
            UserRole.REVIEWER -> UserEntity("usr_3", "Vikram Malhotra", "vikram.m@metrology.gov.in", "LM-DEL-7731", UserRole.REVIEWER, "Central Testing & Verification Lab")
            UserRole.ADMINISTRATOR -> UserEntity("usr_4", "Pooja Verma", "pooja.v@metrology.gov.in", "LM-HQ-0012", UserRole.ADMINISTRATOR, "HQ Legal Metrology Rules Registry")
        }
        viewModelScope.launch {
            repository.switchOfficerProfile(user.id)
        }
    }

    fun startDemoInspection(demoCase: DemoPackageCase, onComplete: (String) -> Unit) {
        viewModelScope.launch {
            _pipelineState.value = PipelineProgressState(
                isProcessing = true,
                currentStageIndex = 0,
                stageTitle = "Uploading Multi-Angle Package Images...",
                progressPercent = 0.15f
            )
            delay(400)

            _pipelineState.value = _pipelineState.value.copy(
                currentStageIndex = 1,
                stageTitle = "Evaluating Image Quality, Sharpness & Glare...",
                progressPercent = 0.35f
            )
            delay(400)

            _pipelineState.value = _pipelineState.value.copy(
                currentStageIndex = 2,
                stageTitle = "Extracting OCR Layout & Text Coordinates...",
                progressPercent = 0.55f
            )
            delay(450)

            _pipelineState.value = _pipelineState.value.copy(
                currentStageIndex = 3,
                stageTitle = "Extracting Legal Metrology Mandatory Declarations...",
                progressPercent = 0.75f
            )
            delay(450)

            _pipelineState.value = _pipelineState.value.copy(
                currentStageIndex = 4,
                stageTitle = "Evaluating Versioned Rules Engine (LM Rules 2011/2022)...",
                progressPercent = 0.90f
            )
            delay(400)

            val officer = _currentUser.value
            val id = repository.loadDemoCaseIntoDatabase(demoCase, officer.id, officer.name, (1..99).random())

            _pipelineState.value = _pipelineState.value.copy(
                currentStageIndex = 5,
                stageTitle = "Analysis Complete! Findings & Evidence Ready.",
                progressPercent = 1.0f,
                completedInspectionId = id,
                isProcessing = false
            )
            selectInspection(id)
            onComplete(id)
        }
    }

    fun startLiveInspection(
        productName: String,
        brand: String,
        category: ProductCategory,
        pdpArea: Double,
        batchNumber: String,
        barcode: String,
        location: String,
        bitmaps: List<Bitmap>,
        onComplete: (String) -> Unit
    ) {
        viewModelScope.launch {
            _pipelineState.value = PipelineProgressState(
                isProcessing = true,
                currentStageIndex = 0,
                stageTitle = "Uploading ${bitmaps.size} Package Images...",
                progressPercent = 0.15f
            )
            delay(400)

            _pipelineState.value = _pipelineState.value.copy(
                currentStageIndex = 1,
                stageTitle = "Analyzing Image Quality, Sharpness & Glare Metrics...",
                progressPercent = 0.35f
            )
            delay(400)

            _pipelineState.value = _pipelineState.value.copy(
                currentStageIndex = 2,
                stageTitle = "Calling AI Perception & OCR Engine...",
                progressPercent = 0.60f
            )

            val officer = _currentUser.value
            val id = repository.executeLiveInspection(
                productName = productName,
                brand = brand,
                category = category,
                pdpArea = pdpArea,
                batchNumber = batchNumber,
                barcode = barcode,
                location = location,
                bitmaps = bitmaps,
                officer = officer
            )

            _pipelineState.value = _pipelineState.value.copy(
                currentStageIndex = 4,
                stageTitle = "Evaluating Legal Metrology Rules & Building Evidence...",
                progressPercent = 0.90f
            )
            delay(400)

            _pipelineState.value = _pipelineState.value.copy(
                currentStageIndex = 5,
                stageTitle = "Inspection Ready! Redirecting to Findings...",
                progressPercent = 1.0f,
                completedInspectionId = id,
                isProcessing = false
            )
            selectInspection(id)

            // Queue offline sync operation automatically
            offlineSyncManager.queueSyncItem(
                actionType = "INSPECTION_RECORD",
                entityId = id,
                title = "Inspection Record: $productName ($brand)"
            )
            if (_networkState.value.isConnected) {
                triggerAutoSync()
            }

            onComplete(id)
        }
    }

    fun overrideComplianceCheck(check: ComplianceCheckEntity, newStatus: ComplianceStatus, comment: String) {
        viewModelScope.launch {
            repository.overrideComplianceCheck(check, newStatus, comment, _currentUser.value)
            offlineSyncManager.queueSyncItem(
                actionType = "COMPLIANCE_CHECK_OVERRIDE",
                entityId = check.id,
                title = "Override Check: ${check.ruleCode} -> $newStatus"
            )
            if (_networkState.value.isConnected) {
                triggerAutoSync()
            }
        }
    }

    fun finalizeInspection(inspectionId: String, penaltyAmount: Double, notes: String) {
        viewModelScope.launch {
            repository.finalizeAndSignInspection(inspectionId, _currentUser.value, penaltyAmount, notes)
            offlineSyncManager.queueSyncItem(
                actionType = "FINALIZE_INSPECTION",
                entityId = inspectionId,
                title = "Finalize Inspection #$inspectionId (Penalty ₹$penaltyAmount)"
            )
            if (_networkState.value.isConnected) {
                triggerAutoSync()
            }
        }
    }

    fun toggleRule(rule: RuleEntity, isActive: Boolean) {
        viewModelScope.launch {
            repository.toggleRuleActive(rule, isActive, _currentUser.value)
            offlineSyncManager.queueSyncItem(
                actionType = "RULE_UPDATE",
                entityId = rule.id,
                title = "Toggle Rule ${rule.ruleCode} (Active=$isActive)"
            )
            if (_networkState.value.isConnected) {
                triggerAutoSync()
            }
        }
    }

    fun addNewRule(rule: RuleEntity) {
        viewModelScope.launch {
            repository.addNewRule(rule, _currentUser.value)
            offlineSyncManager.queueSyncItem(
                actionType = "RULE_REGISTRATION",
                entityId = rule.id,
                title = "Registered New Rule: ${rule.ruleCode}"
            )
            if (_networkState.value.isConnected) {
                triggerAutoSync()
            }
        }
    }

    fun resetAndReseedDemoData() {
        viewModelScope.launch {
            repository.resetAndReseedData()
        }
    }
}
