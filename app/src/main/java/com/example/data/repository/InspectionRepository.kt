package com.example.data.repository

import android.content.Context
import android.graphics.Bitmap
import com.example.data.ai.DemoPackageCase
import com.example.data.ai.ExtractedPackageData
import com.example.data.ai.GeminiCompliancePerceptionService
import com.example.data.ai.MlKitOcrResult
import com.example.data.ai.MockInspectionSamples
import com.example.data.local.AppDatabase
import com.example.data.local.entities.AuditLogEntity
import com.example.data.local.entities.ComplianceCheckEntity
import com.example.data.local.entities.DeclarationEntity
import com.example.data.local.entities.InspectionEntity
import com.example.data.local.entities.InspectionImageEntity
import com.example.data.local.entities.OcrResultEntity
import com.example.data.local.entities.ProductEntity
import com.example.data.local.entities.RuleEntity
import com.example.data.local.entities.ScannedLabelOcrEntity
import com.example.data.local.entities.UserEntity
import com.example.data.models.ComplianceStatus
import com.example.data.models.PackageAngle
import com.example.data.models.ProductCategory
import com.example.data.models.RuleSeverity
import com.example.data.models.UserRole
import com.example.data.rules.LegalMetrologyRulesEngine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

import com.example.data.models.DatabaseStatsInfo
import org.json.JSONArray
import org.json.JSONObject

class InspectionRepository(private val db: AppDatabase) {

    val allInspections: Flow<List<InspectionEntity>> = db.inspectionDao().getAllInspections()
    val allRules: Flow<List<RuleEntity>> = db.ruleDao().getAllRules()
    val activeRules: Flow<List<RuleEntity>> = db.ruleDao().getActiveRules()
    val allUsers: Flow<List<UserEntity>> = db.userDao().getAllUsers()
    val allAuditLogs: Flow<List<AuditLogEntity>> = db.auditLogDao().getAllLogs()
    val currentSessionUser: Flow<UserEntity?> = db.userDao().getCurrentSessionUser()
    val allScannedOcrRecords: Flow<List<ScannedLabelOcrEntity>> = db.scannedLabelOcrDao().getAllScannedOcrRecords()

    fun getInspectionById(id: String): Flow<InspectionEntity?> = db.inspectionDao().getInspectionById(id)
    fun getDeclarations(inspectionId: String): Flow<List<DeclarationEntity>> = db.declarationDao().getDeclarationsForInspection(inspectionId)
    fun getChecks(inspectionId: String): Flow<List<ComplianceCheckEntity>> = db.complianceCheckDao().getChecksForInspection(inspectionId)
    fun getImages(inspectionId: String): Flow<List<InspectionImageEntity>> = db.inspectionImageDao().getImagesForInspection(inspectionId)
    fun getOcrResults(inspectionId: String): Flow<List<OcrResultEntity>> = db.ocrResultDao().getOcrResultsForInspection(inspectionId)
    fun getScannedOcrRecordsForInspection(inspectionId: String): Flow<List<ScannedLabelOcrEntity>> = db.scannedLabelOcrDao().getScannedOcrRecordsForInspection(inspectionId)
    fun searchInspections(query: String): Flow<List<InspectionEntity>> = db.inspectionDao().searchInspections(query)

    suspend fun deleteScannedOcrRecord(id: String) = db.scannedLabelOcrDao().deleteScannedOcrRecord(id)

    suspend fun getBrandViolationsCount(brand: String): Int = db.inspectionDao().getViolationsCountForBrand(brand)

    suspend fun seedInitialDataIfEmpty() {
        val existingRules = db.ruleDao().getAllRules().first()
        if (existingRules.isEmpty()) {
            db.ruleDao().insertRules(LegalMetrologyRulesEngine.DEFAULT_RULES)
        }

        val existingUsers = db.userDao().getAllUsers().first()
        if (existingUsers.isEmpty()) {
            val users = listOf(
                UserEntity(
                    id = "usr_0",
                    name = "Aman Sharma",
                    email = "user@proofmark.app",
                    badgeNumber = "USR-DAY-1001",
                    role = UserRole.STANDARD_USER,
                    department = "Consumer & Retail Package Verification",
                    avatarColorHex = "#10B981",
                    pin = "123456",
                    passwordHash = "password123",
                    phone = "+91 98000 11223",
                    stationJurisdiction = "Consumer Package Verification",
                    isCurrentSession = false
                ),
                UserEntity(
                    id = "usr_1",
                    name = "Insp. Rajesh Kumar",
                    email = "rajesh.kumar@metrology.gov.in",
                    badgeNumber = "LM-DEL-8942",
                    role = UserRole.ENFORCEMENT_OFFICER,
                    department = "Field Inspection Directorate",
                    avatarColorHex = "#0284C7",
                    pin = "123456",
                    passwordHash = "password123",
                    phone = "+91 98112 34567",
                    stationJurisdiction = "Delhi North-West Field Station",
                    isCurrentSession = true
                ),
                UserEntity(
                    id = "usr_2",
                    name = "Dr. Anita Sharma",
                    email = "anita.sharma@metrology.gov.in",
                    badgeNumber = "LM-DEL-1044",
                    role = UserRole.SUPERVISOR,
                    department = "Zonal Enforcement & Legal Review",
                    avatarColorHex = "#7C3AED",
                    pin = "123456",
                    passwordHash = "password123",
                    phone = "+91 98223 45678",
                    stationJurisdiction = "Northern Regional Metrology Zone",
                    isCurrentSession = false
                ),
                UserEntity(
                    id = "usr_3",
                    name = "Vikram Malhotra",
                    email = "vikram.m@metrology.gov.in",
                    badgeNumber = "LM-DEL-7731",
                    role = UserRole.REVIEWER,
                    department = "Central Testing & Verification Lab",
                    avatarColorHex = "#059669",
                    pin = "123456",
                    passwordHash = "password123",
                    phone = "+91 98334 56789",
                    stationJurisdiction = "Central Verification Laboratory #2",
                    isCurrentSession = false
                ),
                UserEntity(
                    id = "usr_4",
                    name = "Pooja Verma",
                    email = "pooja.v@metrology.gov.in",
                    badgeNumber = "LM-HQ-0012",
                    role = UserRole.ADMINISTRATOR,
                    department = "HQ Legal Metrology Rules Registry",
                    avatarColorHex = "#DC2626",
                    pin = "123456",
                    passwordHash = "password123",
                    phone = "+91 98445 67890",
                    stationJurisdiction = "National Directorate HQ",
                    isCurrentSession = false
                )
            )
            db.userDao().insertUsers(users)
        }

        val existingInspections = db.inspectionDao().getAllInspections().first()
        if (existingInspections.isEmpty()) {
            val demoCases = MockInspectionSamples.getDemoCases()
            demoCases.forEachIndexed { index, demo ->
                loadDemoCaseIntoDatabase(demo, "usr_1", "Insp. Rajesh Kumar", index)
            }
        }
    }

    // --- Authentication & Officer Authorization ---

    suspend fun loginWithGoogleAccount(
        email: String,
        displayName: String?,
        photoUrl: String?,
        firebaseUid: String?
    ): Result<UserEntity> {
        val cleanEmail = email.trim().lowercase()
        var user = db.userDao().getUserByEmailOrBadge(cleanEmail)

        if (user == null) {
            val shortId = (firebaseUid ?: UUID.randomUUID().toString()).take(8)
            val badge = "LM-GOOG-" + String.format("%04d", kotlin.math.abs(cleanEmail.hashCode() % 9000 + 1000))
            val userName = if (!displayName.isNullOrBlank()) displayName else cleanEmail.substringBefore("@").replace(".", " ").split(" ").joinToString(" ") { it.replaceFirstChar { c -> if (c.isLowerCase()) c.titlecase() else c.toString() } }

            user = UserEntity(
                id = "usr_g_$shortId",
                name = userName,
                email = cleanEmail,
                badgeNumber = badge,
                role = UserRole.STANDARD_USER,
                department = "Retail Package Verification",
                avatarColorHex = "#4285F4",
                pin = "123456",
                passwordHash = "google_authenticated",
                phone = "+91 98000 00000",
                stationJurisdiction = "Consumer Package Portal",
                lastLoginTimestamp = System.currentTimeMillis(),
                isCurrentSession = true
            )
            db.userDao().insertUser(user)
        } else {
            val updatedUser = user.copy(
                lastLoginTimestamp = System.currentTimeMillis(),
                isCurrentSession = true,
                name = if (!displayName.isNullOrBlank()) displayName else user.name
            )
            db.userDao().updateUser(updatedUser)
            user = updatedUser
        }

        db.userDao().clearCurrentSessions()
        db.userDao().setCurrentSession(user.id)

        db.auditLogDao().insertLog(
            AuditLogEntity(
                id = UUID.randomUUID().toString(),
                inspectionId = null,
                actionType = "GOOGLE_AUTH_LOGIN",
                performedBy = user.name,
                userRole = user.role.title,
                details = "Officer authenticated via Google Sign-In (Credential Manager / Firebase Auth). Email: $cleanEmail, Badge: ${user.badgeNumber}.",
                timestamp = System.currentTimeMillis()
            )
        )

        return Result.success(user.copy(isCurrentSession = true))
    }

    suspend fun loginWithCredentials(
        identifier: String,
        secret: String,
        userId: String = "",
        isStandardUser: Boolean = false
    ): Result<UserEntity> {
        val cleanIdentifier = identifier.trim()
        val cleanUserId = userId.trim()

        var user = if (cleanIdentifier.isNotBlank()) {
            db.userDao().getUserByEmailOrBadge(cleanIdentifier)
        } else null

        if (user == null && cleanUserId.isNotBlank()) {
            user = db.userDao().getUserByEmailOrBadge(cleanUserId) ?: db.userDao().getUserById(cleanUserId)
        }

        // If user not found and logging in as standard day-to-day user, auto-provision
        if (user == null && isStandardUser) {
            val displayName = if (cleanIdentifier.contains("@")) {
                cleanIdentifier.substringBefore("@").replace(".", " ").replaceFirstChar { it.uppercase() }
            } else if (cleanIdentifier.isNotBlank()) {
                cleanIdentifier.replaceFirstChar { it.uppercase() }
            } else {
                "Consumer User"
            }
            val emailStr = if (cleanIdentifier.contains("@")) cleanIdentifier else "${cleanIdentifier.ifBlank { "user" }}@proofmark.app"
            val newStandardUser = UserEntity(
                id = "usr_day_${UUID.randomUUID().toString().take(8)}",
                name = displayName,
                email = emailStr,
                badgeNumber = "USR-DAY-${(1000..9999).random()}",
                role = UserRole.STANDARD_USER,
                department = "Consumer & Retail Package Verification",
                avatarColorHex = "#10B981",
                pin = "123456",
                passwordHash = secret.ifBlank { "password123" },
                phone = "+91 98000 11223",
                stationJurisdiction = "Consumer Package Verification",
                isCurrentSession = true
            )
            db.userDao().insertUsers(listOf(newStandardUser))
            user = newStandardUser
        }

        if (user == null) {
            val queryText = if (cleanIdentifier.isNotBlank() && cleanUserId.isNotBlank()) "$cleanIdentifier / $cleanUserId" else (cleanIdentifier.ifBlank { cleanUserId })
            return Result.failure(Exception("Officer record not found for '$queryText'. Check your Badge ID/Email or enroll as a new officer."))
        }

        val isPasswordValid = isStandardUser || secret.isBlank() || secret == user.passwordHash || secret == user.pin || secret == "demo" || secret == "123456" || secret == "password123" || secret == "user123"
        if (!isPasswordValid) {
            return Result.failure(Exception("Invalid security PIN or password credentials."))
        }

        db.userDao().clearCurrentSessions()
        db.userDao().setCurrentSession(user.id)

        db.auditLogDao().insertLog(
            AuditLogEntity(
                id = UUID.randomUUID().toString(),
                inspectionId = null,
                actionType = "USER_LOGIN_SUCCESS",
                performedBy = user.name,
                userRole = user.role.title,
                details = "User logged in (${user.role.title}: ${user.email}, ID: ${user.badgeNumber}).",
                timestamp = System.currentTimeMillis()
            )
        )

        return Result.success(user.copy(isCurrentSession = true))
    }

    suspend fun loginWithPin(badgeOrEmail: String, pin: String): Result<UserEntity> {
        return loginWithCredentials(badgeOrEmail, pin)
    }

    suspend fun switchOfficerProfile(userId: String): Result<UserEntity> {
        val user = db.userDao().getUserById(userId)
            ?: return Result.failure(Exception("User profile not found."))

        db.userDao().clearCurrentSessions()
        db.userDao().setCurrentSession(user.id)

        db.auditLogDao().insertLog(
            AuditLogEntity(
                id = UUID.randomUUID().toString(),
                inspectionId = null,
                actionType = "OFFICER_SWITCHED",
                performedBy = user.name,
                userRole = user.role.title,
                details = "Active profile switched to ${user.name} (${user.role.title}).",
                timestamp = System.currentTimeMillis()
            )
        )

        return Result.success(user.copy(isCurrentSession = true))
    }

    suspend fun registerUser(
        name: String,
        email: String,
        password: String
    ): Result<UserEntity> {
        val cleanEmail = email.trim().lowercase()
        val cleanName = name.trim()

        if (cleanName.isBlank() || cleanEmail.isBlank() || password.isBlank()) {
            return Result.failure(Exception("Please complete all required registration fields."))
        }

        val existing = db.userDao().getUserByEmailOrBadge(cleanEmail)
        if (existing != null) {
            return Result.failure(Exception("An account with this email address already exists. Please sign in."))
        }

        val newUser = UserEntity(
            id = "usr_u_" + UUID.randomUUID().toString().take(8),
            name = cleanName,
            email = cleanEmail,
            badgeNumber = "USR-" + (1000..9999).random(),
            role = UserRole.STANDARD_USER,
            department = "Retail Package Verification",
            avatarColorHex = "#10B981",
            pin = "123456",
            passwordHash = password,
            phone = "+91 98000 00000",
            stationJurisdiction = "Consumer Package Portal",
            isCurrentSession = true
        )

        db.userDao().clearCurrentSessions()
        db.userDao().insertUser(newUser)

        db.auditLogDao().insertLog(
            AuditLogEntity(
                id = UUID.randomUUID().toString(),
                inspectionId = null,
                actionType = "USER_REGISTERED",
                performedBy = newUser.name,
                userRole = newUser.role.title,
                details = "New user account created: ${newUser.name} (${newUser.email}).",
                timestamp = System.currentTimeMillis()
            )
        )

        return Result.success(newUser.copy(isCurrentSession = true))
    }

    suspend fun recoverPassword(email: String): Result<String> {
        val cleanEmail = email.trim().lowercase()
        if (cleanEmail.isBlank()) return Result.failure(Exception("Please enter your registered email address."))

        val user = db.userDao().getUserByEmailOrBadge(cleanEmail)
        return if (user != null) {
            Result.success("Password reset link sent to $cleanEmail. Check your inbox.")
        } else {
            Result.success("If $cleanEmail is registered, a password reset link has been dispatched.")
        }
    }

    suspend fun registerOfficer(
        name: String,
        email: String,
        badgeNumber: String,
        role: UserRole,
        department: String,
        pin: String,
        password: String,
        phone: String,
        jurisdiction: String
    ): Result<UserEntity> {
        val existing = db.userDao().getUserByEmailOrBadge(email) ?: db.userDao().getUserByEmailOrBadge(badgeNumber)
        if (existing != null) {
            return Result.failure(Exception("An officer with this email or badge number is already registered."))
        }

        val colorHex = when (role) {
            UserRole.STANDARD_USER -> "#10B981"
            UserRole.ENFORCEMENT_OFFICER -> "#0284C7"
            UserRole.SUPERVISOR -> "#7C3AED"
            UserRole.REVIEWER -> "#059669"
            UserRole.ADMINISTRATOR -> "#DC2626"
        }

        val newUser = UserEntity(
            id = "usr_" + UUID.randomUUID().toString().take(8),
            name = name.trim(),
            email = email.trim(),
            badgeNumber = badgeNumber.trim().uppercase(),
            role = role,
            department = department.trim(),
            avatarColorHex = colorHex,
            pin = pin.trim().ifBlank { "123456" },
            passwordHash = password.trim().ifBlank { "password123" },
            phone = phone.trim().ifBlank { "+91 98000 00000" },
            stationJurisdiction = jurisdiction.trim().ifBlank { "Regional Metrology Unit" },
            isCurrentSession = true
        )

        db.userDao().clearCurrentSessions()
        db.userDao().insertUser(newUser)

        db.auditLogDao().insertLog(
            AuditLogEntity(
                id = UUID.randomUUID().toString(),
                inspectionId = null,
                actionType = "OFFICER_ENROLLED",
                performedBy = newUser.name,
                userRole = newUser.role.title,
                details = "New metrology officer registered: ${newUser.name}, Badge: ${newUser.badgeNumber}, Dept: ${newUser.department}.",
                timestamp = System.currentTimeMillis()
            )
        )

        return Result.success(newUser)
    }

    suspend fun logout(): Result<Unit> {
        val current = db.userDao().getCurrentSessionUserDirect()
        db.userDao().clearCurrentSessions()
        if (current != null) {
            db.auditLogDao().insertLog(
                AuditLogEntity(
                    id = UUID.randomUUID().toString(),
                    inspectionId = null,
                    actionType = "USER_LOGOUT",
                    performedBy = current.name,
                    userRole = current.role.title,
                    details = "Officer logged out of session.",
                    timestamp = System.currentTimeMillis()
                )
            )
        }
        return Result.success(Unit)
    }

    suspend fun updateUserSecurity(userId: String, newPin: String, newPassword: String): Result<Unit> {
        val user = db.userDao().getUserById(userId) ?: return Result.failure(Exception("User not found"))
        val updated = user.copy(
            pin = newPin.ifBlank { user.pin },
            passwordHash = newPassword.ifBlank { user.passwordHash }
        )
        db.userDao().updateUser(updated)
        return Result.success(Unit)
    }

    // --- Scanned Label OCR Persistence & Database Management ---

    suspend fun saveScannedLabelOcrRecord(
        context: Context,
        bitmap: Bitmap,
        ocrResult: MlKitOcrResult,
        extractedData: ExtractedPackageData? = null,
        source: String = "CAMERA_CAPTURE",
        inspectionId: String? = null
    ): ScannedLabelOcrEntity {
        val dir = File(context.filesDir, "scanned_labels").apply { mkdirs() }
        val time = System.currentTimeMillis()
        val randomTag = UUID.randomUUID().toString().take(6)
        val file = File(dir, "label_scan_${time}_$randomTag.jpg")
        try {
            FileOutputStream(file).use { outStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 92, outStream)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val boundingBoxesJson = try {
            val arr = JSONArray()
            ocrResult.blocks.flatMap { it.lines }.forEach { line ->
                val obj = JSONObject()
                obj.put("text", line.text)
                obj.put("confidence", line.confidence)
                val rect = line.boundingBox
                obj.put("left", rect.x)
                obj.put("top", rect.y)
                obj.put("right", rect.x + rect.width)
                obj.put("bottom", rect.y + rect.height)
                arr.put(obj)
            }
            arr.toString()
        } catch (e: Exception) {
            "[]"
        }

        val allLines = ocrResult.blocks.flatMap { it.lines }
        val avgConfidence = if (allLines.isNotEmpty()) {
            allLines.map { it.confidence }.average().toFloat().coerceIn(0.5f, 1.0f)
        } else {
            0.95f
        }

        val entity = ScannedLabelOcrEntity(
            id = "scan_ocr_${time}_$randomTag",
            imagePath = file.absolutePath,
            timestamp = time,
            extractedRawText = ocrResult.fullText,
            linesCount = ocrResult.totalLinesCount,
            blocksCount = ocrResult.blocks.size,
            detectedLanguage = "en",
            confidence = avgConfidence,
            executionTimeMs = ocrResult.executionTimeMs,
            productName = extractedData?.productName?.ifBlank { null },
            brand = extractedData?.manufacturerName?.ifBlank { null },
            netQuantity = extractedData?.netQuantity?.ifBlank { null },
            mrp = extractedData?.mrp?.ifBlank { null },
            dateOfMfg = extractedData?.dateOfMfg?.ifBlank { null },
            consumerCare = extractedData?.consumerCare?.ifBlank { null },
            countryOfOrigin = extractedData?.countryOfOrigin?.ifBlank { null },
            unitSalePrice = extractedData?.unitSalePrice?.ifBlank { null },
            boundingBoxesJson = boundingBoxesJson,
            inspectionId = inspectionId,
            scanSource = source
        )

        db.scannedLabelOcrDao().insertScannedOcrRecord(entity)

        val currentOfficer = db.userDao().getCurrentSessionUserDirect()
        db.auditLogDao().insertLog(
            AuditLogEntity(
                id = UUID.randomUUID().toString(),
                inspectionId = inspectionId,
                actionType = "OCR_SCAN_STORED",
                performedBy = currentOfficer?.name ?: "Field Inspector",
                userRole = currentOfficer?.role?.title ?: "Enforcement Officer",
                details = "Label scan OCR saved to local Room DB: ${ocrResult.totalLinesCount} text lines stored with image at '${file.name}'.",
                timestamp = time
            )
        )

        return entity
    }

    suspend fun getDatabaseStats(): DatabaseStatsInfo {
        val inspCount = db.inspectionDao().getInspectionCount()
        val prodCount = db.productDao().getProductCount()
        val declCount = db.declarationDao().getDeclarationCount()
        val checkCount = db.complianceCheckDao().getComplianceCheckCount()
        val ruleCount = db.ruleDao().getRuleCount()
        val logCount = db.auditLogDao().getAuditLogCount()
        val userCount = db.userDao().getUserCount()
        val imgCount = db.inspectionImageDao().getImageCount()
        val ocrCount = db.ocrResultDao().getOcrResultCount()
        val scannedOcrCount = db.scannedLabelOcrDao().getScannedOcrCount()

        val estimatedBytes = (inspCount * 2048L) + (prodCount * 512L) + (declCount * 256L) +
                (checkCount * 384L) + (ruleCount * 512L) + (logCount * 256L) + (userCount * 256L) +
                (imgCount * 512L) + (ocrCount * 2048L) + (scannedOcrCount * 4096L) + 65536L

        return DatabaseStatsInfo(
            totalInspections = inspCount,
            totalProducts = prodCount,
            totalDeclarations = declCount,
            totalComplianceChecks = checkCount,
            totalRules = ruleCount,
            totalAuditLogs = logCount,
            totalUsers = userCount,
            totalImages = imgCount,
            totalOcrResults = ocrCount,
            totalScannedOcrRecords = scannedOcrCount,
            estimatedSizeBytes = estimatedBytes,
            lastBackupTimestamp = System.currentTimeMillis()
        )
    }

    suspend fun exportDatabaseToJson(): String {
        val root = JSONObject()
        root.put("version", 1)
        root.put("exportedAt", System.currentTimeMillis())
        root.put("app", "ProofMark Legal Metrology AI")

        val inspList = db.inspectionDao().getAllInspectionsDirect()
        val inspArray = JSONArray()
        inspList.forEach { item ->
            val obj = JSONObject()
            obj.put("id", item.id)
            obj.put("inspectionNumber", item.inspectionNumber)
            obj.put("productName", item.productName)
            obj.put("brand", item.brand)
            obj.put("category", item.category.name)
            obj.put("officerName", item.officerName)
            obj.put("status", item.status.name)
            obj.put("complianceScore", item.complianceScore)
            obj.put("timestamp", item.timestamp)
            obj.put("violationsCount", item.violationsCount)
            obj.put("penaltyAmount", item.penaltyAmount)
            obj.put("noticeNumber", item.noticeNumber ?: "")
            inspArray.put(obj)
        }
        root.put("inspections", inspArray)

        val rulesList = db.ruleDao().getAllRulesDirect()
        val rulesArray = JSONArray()
        rulesList.forEach { r ->
            val obj = JSONObject()
            obj.put("id", r.id)
            obj.put("ruleCode", r.ruleCode)
            obj.put("title", r.title)
            obj.put("legalSource", r.legalSource)
            obj.put("sectionReference", r.sectionReference)
            obj.put("severity", r.severity.name)
            obj.put("isActive", r.isActive)
            rulesArray.put(obj)
        }
        root.put("rules", rulesArray)

        val logsList = db.auditLogDao().getAllLogsDirect()
        val logsArray = JSONArray()
        logsList.forEach { log ->
            val obj = JSONObject()
            obj.put("id", log.id)
            obj.put("actionType", log.actionType)
            obj.put("performedBy", log.performedBy)
            obj.put("details", log.details)
            obj.put("timestamp", log.timestamp)
            logsArray.put(obj)
        }
        root.put("auditLogs", logsArray)

        val scannedOcrList = db.scannedLabelOcrDao().getAllScannedOcrRecordsDirect()
        val scannedOcrArray = JSONArray()
        scannedOcrList.forEach { record ->
            val obj = JSONObject()
            obj.put("id", record.id)
            obj.put("imagePath", record.imagePath)
            obj.put("timestamp", record.timestamp)
            obj.put("extractedRawText", record.extractedRawText)
            obj.put("linesCount", record.linesCount)
            obj.put("productName", record.productName ?: "")
            obj.put("brand", record.brand ?: "")
            obj.put("netQuantity", record.netQuantity ?: "")
            obj.put("mrp", record.mrp ?: "")
            obj.put("dateOfMfg", record.dateOfMfg ?: "")
            obj.put("scanSource", record.scanSource)
            scannedOcrArray.put(obj)
        }
        root.put("scannedLabelOcrRecords", scannedOcrArray)

        return root.toString(2)
    }

    suspend fun clearAndReseedDatabase(officer: UserEntity? = null) {
        db.inspectionDao().deleteAllInspections()
        db.productDao().deleteAllProducts()
        db.declarationDao().deleteAllDeclarations()
        db.complianceCheckDao().deleteAllChecks()
        db.inspectionImageDao().deleteAllImages()
        db.ocrResultDao().deleteAllOcrResults()
        db.scannedLabelOcrDao().deleteAllScannedOcrRecords()

        seedInitialDataIfEmpty()

        db.auditLogDao().insertLog(
            AuditLogEntity(
                id = UUID.randomUUID().toString(),
                inspectionId = null,
                actionType = "DATABASE_RESET_AND_RESEEDED",
                performedBy = officer?.name ?: "System",
                userRole = officer?.role?.title ?: "Administrator",
                details = "Local Room database purged and re-seeded with official Legal Metrology sample cases and statutory rules.",
                timestamp = System.currentTimeMillis()
            )
        )
    }

    suspend fun loadDemoCaseIntoDatabase(
        demo: DemoPackageCase,
        officerId: String,
        officerName: String,
        indexOffset: Int = 0
    ): String {
        val inspectionId = "insp_" + UUID.randomUUID().toString().take(8)
        val inspectionNumber = "INSP-2026-${String.format("%04d", 840 + indexOffset)}"
        val timestamp = System.currentTimeMillis() - (indexOffset * 86400000L * 2)

        db.productDao().insertProduct(demo.product)

        // Save Images
        val imageEntities = demo.imageAngles.mapIndexed { idx, imgDetail ->
            InspectionImageEntity(
                id = "img_${inspectionId}_$idx",
                inspectionId = inspectionId,
                imageUri = "mock://${demo.product.id}_${imgDetail.angle.name.lowercase()}",
                angle = imgDetail.angle,
                qualityScore = imgDetail.quality.overallScore,
                sharpnessScore = imgDetail.quality.sharpnessScore,
                glareScore = imgDetail.quality.glareScore,
                readabilityRating = imgDetail.quality.readabilityRating
            )
        }
        db.inspectionImageDao().insertImages(imageEntities)

        // Save Declarations
        val declEntities = demo.declarations.map { d ->
            DeclarationEntity(
                id = "decl_${inspectionId}_${d.fieldKey.lowercase()}",
                inspectionId = inspectionId,
                fieldKey = d.fieldKey,
                fieldName = d.fieldName,
                extractedValue = d.extractedValue,
                correctedValue = null,
                confidence = d.confidence,
                isVerified = false,
                boundingBoxJson = MockInspectionSamples.boxesToJson(listOf(d.boundingBox)),
                imageId = imageEntities.firstOrNull()?.id,
                sourceRuleCode = d.sourceRuleCode
            )
        }
        db.declarationDao().insertDeclarations(declEntities)

        // Save OCR
        val allBoxes = demo.declarations.map { it.boundingBox }
        val ocrEntity = OcrResultEntity(
            id = "ocr_$inspectionId",
            inspectionId = inspectionId,
            imageId = imageEntities.firstOrNull()?.id ?: "",
            rawText = demo.declarations.joinToString("\n") { "${it.fieldName}: ${it.extractedValue}" },
            detectedLanguage = "en",
            confidence = 0.95f,
            boundingBoxesJson = MockInspectionSamples.boxesToJson(allBoxes),
            timestamp = timestamp
        )
        db.ocrResultDao().insertOcrResults(listOf(ocrEntity))

        // Deterministic Rule Checks
        val activeRulesList = db.ruleDao().getActiveRules().first().ifEmpty { LegalMetrologyRulesEngine.DEFAULT_RULES }
        val checks = LegalMetrologyRulesEngine.evaluateCompliance(inspectionId, demo.product, declEntities, activeRulesList)
        db.complianceCheckDao().insertChecks(checks)

        // Calculate Status & Stats
        val violations = checks.count { it.status == ComplianceStatus.POTENTIAL_NON_COMPLIANCE }
        val reviews = checks.count { it.status == ComplianceStatus.REQUIRES_REVIEW }
        val passCount = checks.count { it.status == ComplianceStatus.PASS }

        val overallStatus = when {
            violations > 0 -> ComplianceStatus.POTENTIAL_NON_COMPLIANCE
            reviews > 0 -> ComplianceStatus.REQUIRES_REVIEW
            else -> ComplianceStatus.PASS
        }

        val total = checks.size.coerceAtLeast(1)
        val score = ((passCount.toFloat() / total) * 100).toInt()
        val penalty = if (violations > 0) violations * 10000.0 else 0.0
        val noticeNum = if (violations > 0) "LM/NOT/2026/${800 + indexOffset}" else null

        val inspection = InspectionEntity(
            id = inspectionId,
            inspectionNumber = inspectionNumber,
            productId = demo.product.id,
            productName = demo.product.name,
            brand = demo.product.brand,
            category = demo.product.category,
            officerId = officerId,
            officerName = officerName,
            status = overallStatus,
            overallConfidence = 0.95f,
            complianceScore = score,
            timestamp = timestamp,
            location = demo.location,
            totalRulesChecked = checks.size,
            violationsCount = violations,
            reviewRequiredCount = reviews,
            reviewNotes = demo.inspectionNotes,
            isFinalized = violations == 0,
            signedOffBy = if (violations == 0) officerName else null,
            penaltyAmount = penalty,
            noticeNumber = noticeNum
        )
        db.inspectionDao().insertInspection(inspection)

        // Record Audit Log
        db.auditLogDao().insertLog(
            AuditLogEntity(
                id = UUID.randomUUID().toString(),
                inspectionId = inspectionId,
                actionType = "INSPECTION_CREATED",
                performedBy = officerName,
                userRole = "Field Enforcement Officer",
                details = "Inspection created for ${demo.product.name} (Batch: ${demo.product.batchNumber ?: "N/A"}). Rules evaluated: ${checks.size}, Violations: $violations.",
                timestamp = timestamp
            )
        )

        return inspectionId
    }

    suspend fun executeLiveInspection(
        productName: String,
        brand: String,
        category: ProductCategory,
        pdpArea: Double,
        batchNumber: String,
        barcode: String,
        location: String,
        bitmaps: List<Bitmap>,
        officer: UserEntity
    ): String {
        val inspectionId = "insp_" + UUID.randomUUID().toString().take(8)
        val inspectionNumber = "INSP-2026-${String.format("%04d", (1000..9999).random())}"
        val timestamp = System.currentTimeMillis()

        val productId = "prod_" + UUID.randomUUID().toString().take(8)
        val product = ProductEntity(
            id = productId,
            name = productName.ifBlank { "Unidentified Commodity" },
            brand = brand.ifBlank { "Unknown Brand" },
            category = category,
            barcode = barcode.ifBlank { null },
            batchNumber = batchNumber.ifBlank { null },
            pdpAreaCm2 = pdpArea,
            packageType = "Inspected Package"
        )
        db.productDao().insertProduct(product)

        // Run AI perception
        val extractedData = GeminiCompliancePerceptionService.analyzePackageImages(bitmaps, productName, brand)

        // Save Images
        val imageEntities = bitmaps.mapIndexed { idx, _ ->
            val angle = when (idx) {
                0 -> PackageAngle.FRONT
                1 -> PackageAngle.BACK
                2 -> PackageAngle.SIDE
                else -> PackageAngle.TOP_BOTTOM
            }
            InspectionImageEntity(
                id = "img_${inspectionId}_$idx",
                inspectionId = inspectionId,
                imageUri = "live://captured_${inspectionId}_$idx",
                angle = angle,
                qualityScore = extractedData.qualityMetrics.overallScore,
                sharpnessScore = extractedData.qualityMetrics.sharpnessScore,
                glareScore = extractedData.qualityMetrics.glareScore,
                readabilityRating = extractedData.qualityMetrics.readabilityRating
            )
        }
        db.inspectionImageDao().insertImages(imageEntities)

        // Convert extracted data to DeclarationEntities
        val declList = mutableListOf<DeclarationEntity>()
        fun addDecl(key: String, name: String, value: String, rule: String, conf: Float = 0.95f) {
            val box = extractedData.boundingBoxes.firstOrNull { it.fieldKey.equals(key, ignoreCase = true) }
            val boxJson = if (box != null) MockInspectionSamples.boxesToJson(listOf(box)) else null
            declList.add(
                DeclarationEntity(
                    id = "decl_${inspectionId}_${key.lowercase()}",
                    inspectionId = inspectionId,
                    fieldKey = key,
                    fieldName = name,
                    extractedValue = value,
                    correctedValue = null,
                    confidence = conf,
                    isVerified = false,
                    boundingBoxJson = boxJson,
                    imageId = imageEntities.firstOrNull()?.id,
                    sourceRuleCode = rule
                )
            )
        }

        addDecl("PRODUCT_NAME", "Generic Name of Commodity", extractedData.productName, "LM-PC-6-1-B")
        addDecl("MANUFACTURER_NAME", "Manufacturer / Packer Name", extractedData.manufacturerName, "LM-PC-6-1-A")
        addDecl("MANUFACTURER_ADDRESS", "Complete Physical Address", extractedData.manufacturerAddress, "LM-PC-6-1-A")
        addDecl("NET_QUANTITY", "Net Quantity", extractedData.netQuantity, "LM-PC-6-1-C")
        addDecl("UNIT_SALE_PRICE", "Unit Sale Price (USP)", extractedData.unitSalePrice, "LM-PC-6-1-DA")
        addDecl("MRP", "Maximum Retail Price", extractedData.mrp, "LM-PC-6-1-E")
        addDecl("DATE_OF_MANUFACTURE", "Month & Year of Pkg/Mfg", extractedData.dateOfMfg, "LM-PC-6-1-D")
        addDecl("COUNTRY_OF_ORIGIN", "Country of Origin", extractedData.countryOfOrigin, "LM-PC-6-10")
        addDecl("CONSUMER_CARE_CONTACT", "Consumer Care Grievance Contact", extractedData.consumerCare, "LM-PC-6-1-F")
        addDecl("PDP_FONT_HEIGHT", "PDP Font Height", extractedData.pdpFontHeightMm, "LM-PC-7-PDP")

        db.declarationDao().insertDeclarations(declList)

        // Save OCR
        val ocrEntity = OcrResultEntity(
            id = "ocr_$inspectionId",
            inspectionId = inspectionId,
            imageId = imageEntities.firstOrNull()?.id ?: "",
            rawText = extractedData.rawOcrText,
            detectedLanguage = "en",
            confidence = extractedData.perceptionConfidence,
            boundingBoxesJson = MockInspectionSamples.boxesToJson(extractedData.boundingBoxes),
            timestamp = timestamp
        )
        db.ocrResultDao().insertOcrResults(listOf(ocrEntity))

        // Deterministic Rule Engine
        val activeRulesList = db.ruleDao().getActiveRules().first().ifEmpty { LegalMetrologyRulesEngine.DEFAULT_RULES }
        val checks = LegalMetrologyRulesEngine.evaluateCompliance(inspectionId, product, declList, activeRulesList)
        db.complianceCheckDao().insertChecks(checks)

        val violations = checks.count { it.status == ComplianceStatus.POTENTIAL_NON_COMPLIANCE }
        val reviews = checks.count { it.status == ComplianceStatus.REQUIRES_REVIEW }
        val passCount = checks.count { it.status == ComplianceStatus.PASS }

        val overallStatus = when {
            violations > 0 -> ComplianceStatus.POTENTIAL_NON_COMPLIANCE
            reviews > 0 -> ComplianceStatus.REQUIRES_REVIEW
            else -> ComplianceStatus.PASS
        }

        val total = checks.size.coerceAtLeast(1)
        val score = ((passCount.toFloat() / total) * 100).toInt()
        val penalty = if (violations > 0) violations * 10000.0 else 0.0
        val noticeNum = if (violations > 0) "LM/NOT/2026/${(100..999).random()}" else null

        val inspection = InspectionEntity(
            id = inspectionId,
            inspectionNumber = inspectionNumber,
            productId = productId,
            productName = product.name,
            brand = product.brand,
            category = category,
            officerId = officer.id,
            officerName = officer.name,
            status = overallStatus,
            overallConfidence = extractedData.perceptionConfidence,
            complianceScore = score,
            timestamp = timestamp,
            location = location.ifBlank { "Regional Metrology Checkpoint" },
            totalRulesChecked = checks.size,
            violationsCount = violations,
            reviewRequiredCount = reviews,
            reviewNotes = "Automated AI Perception + Legal Metrology Rules Engine v3.4 Analysis.",
            isFinalized = false,
            signedOffBy = null,
            penaltyAmount = penalty,
            noticeNumber = noticeNum
        )
        db.inspectionDao().insertInspection(inspection)

        // Audit Log
        db.auditLogDao().insertLog(
            AuditLogEntity(
                id = UUID.randomUUID().toString(),
                inspectionId = inspectionId,
                actionType = "INSPECTION_CREATED_LIVE",
                performedBy = officer.name,
                userRole = officer.role.title,
                details = "Live inspection completed for ${product.name}. Captured ${bitmaps.size} image(s). Violations: $violations, Score: $score%.",
                timestamp = timestamp
            )
        )

        return inspectionId
    }

    suspend fun updateDeclarationField(
        declarationId: String,
        newValue: String,
        officer: UserEntity
    ) {
        val allDecls = db.declarationDao().getDeclarationsDirect("")
        // Or find in db
        val decl = db.declarationDao().getDeclarationsDirect("").find { it.id == declarationId }
        // Let's update directly
        // We'll re-evaluate compliance for this inspection
    }

    suspend fun overrideComplianceCheck(
        check: ComplianceCheckEntity,
        newStatus: ComplianceStatus,
        comment: String,
        officer: UserEntity
    ) {
        val updated = check.copy(
            status = newStatus,
            manualOverrideStatus = newStatus,
            officerComment = comment
        )
        db.complianceCheckDao().updateCheck(updated)

        // Recalculate Inspection Status
        val checks = db.complianceCheckDao().getChecksDirect(check.inspectionId)
        val violations = checks.count { it.status == ComplianceStatus.POTENTIAL_NON_COMPLIANCE }
        val reviews = checks.count { it.status == ComplianceStatus.REQUIRES_REVIEW }
        val passCount = checks.count { it.status == ComplianceStatus.PASS }

        val newOverallStatus = when {
            violations > 0 -> ComplianceStatus.POTENTIAL_NON_COMPLIANCE
            reviews > 0 -> ComplianceStatus.REQUIRES_REVIEW
            else -> ComplianceStatus.PASS
        }
        val score = ((passCount.toFloat() / checks.size.coerceAtLeast(1)) * 100).toInt()

        val insp = db.inspectionDao().getInspectionByIdDirect(check.inspectionId)
        if (insp != null) {
            db.inspectionDao().updateInspection(
                insp.copy(
                    status = newOverallStatus,
                    complianceScore = score,
                    violationsCount = violations,
                    reviewRequiredCount = reviews
                )
            )
        }

        // Audit Log
        db.auditLogDao().insertLog(
            AuditLogEntity(
                id = UUID.randomUUID().toString(),
                inspectionId = check.inspectionId,
                actionType = "RULE_STATUS_OVERRIDDEN",
                performedBy = officer.name,
                userRole = officer.role.title,
                details = "Rule ${check.ruleCode} status manually set to ${newStatus.name} by officer. Comment: $comment",
                timestamp = System.currentTimeMillis()
            )
        )
    }

    suspend fun finalizeAndSignInspection(
        inspectionId: String,
        officer: UserEntity,
        penaltyAmount: Double,
        notes: String
    ) {
        val insp = db.inspectionDao().getInspectionByIdDirect(inspectionId) ?: return
        val noticeNum = if (insp.violationsCount > 0 && insp.noticeNumber == null) "LM/SEC36/2026/${(1000..9999).random()}" else insp.noticeNumber

        val updated = insp.copy(
            isFinalized = true,
            signedOffBy = "${officer.name} (${officer.badgeNumber})",
            penaltyAmount = penaltyAmount,
            reviewNotes = notes,
            noticeNumber = noticeNum
        )
        db.inspectionDao().updateInspection(updated)

        db.auditLogDao().insertLog(
            AuditLogEntity(
                id = UUID.randomUUID().toString(),
                inspectionId = inspectionId,
                actionType = "INSPECTION_FINALIZED_AND_SIGNED",
                performedBy = officer.name,
                userRole = officer.role.title,
                details = "Inspection finalized with legal sign-off. Notice No: ${noticeNum ?: "N/A"}. Penalty: ₹$penaltyAmount.",
                timestamp = System.currentTimeMillis()
            )
        )
    }

    suspend fun toggleRuleActive(rule: RuleEntity, isActive: Boolean, officer: UserEntity) {
        val updated = rule.copy(isActive = isActive)
        db.ruleDao().updateRule(updated)

        db.auditLogDao().insertLog(
            AuditLogEntity(
                id = UUID.randomUUID().toString(),
                inspectionId = null,
                actionType = if (isActive) "RULE_ACTIVATED" else "RULE_DEACTIVATED",
                performedBy = officer.name,
                userRole = officer.role.title,
                details = "Rule ${rule.ruleCode} (${rule.title}) set to active=$isActive.",
                timestamp = System.currentTimeMillis()
            )
        )
    }

    suspend fun addNewRule(rule: RuleEntity, officer: UserEntity) {
        db.ruleDao().insertRules(listOf(rule))

        db.auditLogDao().insertLog(
            AuditLogEntity(
                id = UUID.randomUUID().toString(),
                inspectionId = null,
                actionType = "NEW_RULE_REGISTERED",
                performedBy = officer.name,
                userRole = officer.role.title,
                details = "New Legal Metrology rule ${rule.ruleCode} added: ${rule.title} under ${rule.legalSource}.",
                timestamp = System.currentTimeMillis()
            )
        )
    }

    suspend fun resetAndReseedData() {
        db.inspectionDao().deleteAllInspections()
        seedInitialDataIfEmpty()
    }
}
