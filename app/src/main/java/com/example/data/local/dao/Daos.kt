package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
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
import kotlinx.coroutines.flow.Flow

@Dao
interface InspectionDao {
    @Query("SELECT * FROM inspections ORDER BY timestamp DESC")
    fun getAllInspections(): Flow<List<InspectionEntity>>

    @Query("SELECT * FROM inspections ORDER BY timestamp DESC")
    suspend fun getAllInspectionsDirect(): List<InspectionEntity>

    @Query("SELECT * FROM inspections WHERE id = :id")
    fun getInspectionById(id: String): Flow<InspectionEntity?>

    @Query("SELECT * FROM inspections WHERE id = :id")
    suspend fun getInspectionByIdDirect(id: String): InspectionEntity?

    @Query("SELECT * FROM inspections WHERE status = :status ORDER BY timestamp DESC")
    fun getInspectionsByStatus(status: ComplianceStatus): Flow<List<InspectionEntity>>

    @Query("SELECT * FROM inspections WHERE productName LIKE '%' || :query || '%' OR brand LIKE '%' || :query || '%' OR inspectionNumber LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    fun searchInspections(query: String): Flow<List<InspectionEntity>>

    @Query("SELECT COUNT(*) FROM inspections")
    suspend fun getInspectionCount(): Int

    @Query("SELECT COUNT(*) FROM inspections WHERE brand = :brand AND status = 'POTENTIAL_NON_COMPLIANCE'")
    suspend fun getViolationsCountForBrand(brand: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInspection(inspection: InspectionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInspections(inspections: List<InspectionEntity>)

    @Update
    suspend fun updateInspection(inspection: InspectionEntity)

    @Query("DELETE FROM inspections WHERE id = :id")
    suspend fun deleteInspection(id: String)

    @Query("DELETE FROM inspections")
    suspend fun deleteAllInspections()
}

@Dao
interface ProductDao {
    @Query("SELECT * FROM products ORDER BY name ASC")
    fun getAllProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products ORDER BY name ASC")
    suspend fun getAllProductsDirect(): List<ProductEntity>

    @Query("SELECT * FROM products WHERE id = :id")
    suspend fun getProductById(id: String): ProductEntity?

    @Query("SELECT COUNT(*) FROM products")
    suspend fun getProductCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<ProductEntity>)

    @Query("DELETE FROM products")
    suspend fun deleteAllProducts()
}

@Dao
interface DeclarationDao {
    @Query("SELECT * FROM declarations WHERE inspectionId = :inspectionId")
    fun getDeclarationsForInspection(inspectionId: String): Flow<List<DeclarationEntity>>

    @Query("SELECT * FROM declarations WHERE inspectionId = :inspectionId")
    suspend fun getDeclarationsDirect(inspectionId: String): List<DeclarationEntity>

    @Query("SELECT * FROM declarations")
    suspend fun getAllDeclarationsDirect(): List<DeclarationEntity>

    @Query("SELECT COUNT(*) FROM declarations")
    suspend fun getDeclarationCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeclarations(declarations: List<DeclarationEntity>)

    @Update
    suspend fun updateDeclaration(declaration: DeclarationEntity)

    @Query("DELETE FROM declarations")
    suspend fun deleteAllDeclarations()
}

@Dao
interface ComplianceCheckDao {
    @Query("SELECT * FROM compliance_checks WHERE inspectionId = :inspectionId")
    fun getChecksForInspection(inspectionId: String): Flow<List<ComplianceCheckEntity>>

    @Query("SELECT * FROM compliance_checks WHERE inspectionId = :inspectionId")
    suspend fun getChecksDirect(inspectionId: String): List<ComplianceCheckEntity>

    @Query("SELECT * FROM compliance_checks")
    suspend fun getAllChecksDirect(): List<ComplianceCheckEntity>

    @Query("SELECT COUNT(*) FROM compliance_checks")
    suspend fun getComplianceCheckCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChecks(checks: List<ComplianceCheckEntity>)

    @Update
    suspend fun updateCheck(check: ComplianceCheckEntity)

    @Query("DELETE FROM compliance_checks")
    suspend fun deleteAllChecks()
}

@Dao
interface InspectionImageDao {
    @Query("SELECT * FROM inspection_images WHERE inspectionId = :inspectionId")
    fun getImagesForInspection(inspectionId: String): Flow<List<InspectionImageEntity>>

    @Query("SELECT * FROM inspection_images WHERE inspectionId = :inspectionId")
    suspend fun getImagesDirect(inspectionId: String): List<InspectionImageEntity>

    @Query("SELECT * FROM inspection_images")
    suspend fun getAllImagesDirect(): List<InspectionImageEntity>

    @Query("SELECT COUNT(*) FROM inspection_images")
    suspend fun getImageCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImages(images: List<InspectionImageEntity>)

    @Query("DELETE FROM inspection_images")
    suspend fun deleteAllImages()
}

@Dao
interface OcrResultDao {
    @Query("SELECT * FROM ocr_results WHERE inspectionId = :inspectionId")
    fun getOcrResultsForInspection(inspectionId: String): Flow<List<OcrResultEntity>>

    @Query("SELECT * FROM ocr_results WHERE inspectionId = :inspectionId")
    suspend fun getOcrResultsDirect(inspectionId: String): List<OcrResultEntity>

    @Query("SELECT * FROM ocr_results")
    suspend fun getAllOcrResultsDirect(): List<OcrResultEntity>

    @Query("SELECT COUNT(*) FROM ocr_results")
    suspend fun getOcrResultCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOcrResults(results: List<OcrResultEntity>)

    @Query("DELETE FROM ocr_results")
    suspend fun deleteAllOcrResults()
}

@Dao
interface RuleDao {
    @Query("SELECT * FROM rules WHERE isActive = 1 ORDER BY ruleCode ASC")
    fun getActiveRules(): Flow<List<RuleEntity>>

    @Query("SELECT * FROM rules ORDER BY ruleCode ASC")
    fun getAllRules(): Flow<List<RuleEntity>>

    @Query("SELECT * FROM rules ORDER BY ruleCode ASC")
    suspend fun getAllRulesDirect(): List<RuleEntity>

    @Query("SELECT * FROM rules WHERE id = :id")
    suspend fun getRuleById(id: String): RuleEntity?

    @Query("SELECT COUNT(*) FROM rules")
    suspend fun getRuleCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRules(rules: List<RuleEntity>)

    @Update
    suspend fun updateRule(rule: RuleEntity)

    @Query("DELETE FROM rules")
    suspend fun deleteAllRules()
}

@Dao
interface AuditLogDao {
    @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC")
    fun getAllLogs(): Flow<List<AuditLogEntity>>

    @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC")
    suspend fun getAllLogsDirect(): List<AuditLogEntity>

    @Query("SELECT * FROM audit_logs WHERE inspectionId = :inspectionId ORDER BY timestamp DESC")
    fun getLogsForInspection(inspectionId: String): Flow<List<AuditLogEntity>>

    @Query("SELECT COUNT(*) FROM audit_logs")
    suspend fun getAuditLogCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: AuditLogEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLogs(logs: List<AuditLogEntity>)

    @Query("DELETE FROM audit_logs")
    suspend fun deleteAllLogs()
}

@Dao
interface UserDao {
    @Query("SELECT * FROM users ORDER BY name ASC")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users ORDER BY name ASC")
    suspend fun getAllUsersDirect(): List<UserEntity>

    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getUserById(id: String): UserEntity?

    @Query("SELECT * FROM users WHERE email = :email OR badgeNumber = :email LIMIT 1")
    suspend fun getUserByEmailOrBadge(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE isCurrentSession = 1 LIMIT 1")
    fun getCurrentSessionUser(): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE isCurrentSession = 1 LIMIT 1")
    suspend fun getCurrentSessionUserDirect(): UserEntity?

    @Query("UPDATE users SET isCurrentSession = 0")
    suspend fun clearCurrentSessions()

    @Query("UPDATE users SET isCurrentSession = 1, lastLoginTimestamp = :timestamp WHERE id = :userId")
    suspend fun setCurrentSession(userId: String, timestamp: Long = System.currentTimeMillis())

    @Query("SELECT COUNT(*) FROM users")
    suspend fun getUserCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserEntity>)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("DELETE FROM users WHERE id = :id")
    suspend fun deleteUser(id: String)
}

@Dao
interface ScannedLabelOcrDao {
    @Query("SELECT * FROM scanned_label_ocr_records ORDER BY timestamp DESC")
    fun getAllScannedOcrRecords(): Flow<List<ScannedLabelOcrEntity>>

    @Query("SELECT * FROM scanned_label_ocr_records ORDER BY timestamp DESC")
    suspend fun getAllScannedOcrRecordsDirect(): List<ScannedLabelOcrEntity>

    @Query("SELECT * FROM scanned_label_ocr_records WHERE id = :id")
    fun getScannedOcrRecordById(id: String): Flow<ScannedLabelOcrEntity?>

    @Query("SELECT * FROM scanned_label_ocr_records WHERE id = :id")
    suspend fun getScannedOcrRecordByIdDirect(id: String): ScannedLabelOcrEntity?

    @Query("SELECT * FROM scanned_label_ocr_records WHERE inspectionId = :inspectionId ORDER BY timestamp DESC")
    fun getScannedOcrRecordsForInspection(inspectionId: String): Flow<List<ScannedLabelOcrEntity>>

    @Query("SELECT COUNT(*) FROM scanned_label_ocr_records")
    suspend fun getScannedOcrCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScannedOcrRecord(record: ScannedLabelOcrEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScannedOcrRecords(records: List<ScannedLabelOcrEntity>)

    @Query("DELETE FROM scanned_label_ocr_records WHERE id = :id")
    suspend fun deleteScannedOcrRecord(id: String)

    @Query("DELETE FROM scanned_label_ocr_records")
    suspend fun deleteAllScannedOcrRecords()
}
