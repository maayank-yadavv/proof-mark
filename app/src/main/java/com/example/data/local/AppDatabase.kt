package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.data.local.dao.AuditLogDao
import com.example.data.local.dao.ComplianceCheckDao
import com.example.data.local.dao.DeclarationDao
import com.example.data.local.dao.FssaiLicenseDao
import com.example.data.local.dao.InspectionDao
import com.example.data.local.dao.InspectionImageDao
import com.example.data.local.dao.OcrResultDao
import com.example.data.local.dao.PendingSyncDao
import com.example.data.local.dao.ProductDao
import com.example.data.local.dao.RuleDao
import com.example.data.local.dao.ScannedLabelOcrDao
import com.example.data.local.dao.UserDao
import com.example.data.local.entities.AuditLogEntity
import com.example.data.local.entities.ComplianceCheckEntity
import com.example.data.local.entities.DeclarationEntity
import com.example.data.local.entities.FssaiLicenseEntity
import com.example.data.local.entities.InspectionEntity
import com.example.data.local.entities.InspectionImageEntity
import com.example.data.local.entities.OcrResultEntity
import com.example.data.local.entities.PendingSyncEntity
import com.example.data.local.entities.ProductEntity
import com.example.data.local.entities.RuleEntity
import com.example.data.local.entities.ScannedLabelOcrEntity
import com.example.data.local.entities.UserEntity

@Database(
    entities = [
        UserEntity::class,
        ProductEntity::class,
        InspectionEntity::class,
        InspectionImageEntity::class,
        OcrResultEntity::class,
        ScannedLabelOcrEntity::class,
        DeclarationEntity::class,
        ComplianceCheckEntity::class,
        RuleEntity::class,
        AuditLogEntity::class,
        PendingSyncEntity::class,
        FssaiLicenseEntity::class
    ],
    version = 4,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun inspectionDao(): InspectionDao
    abstract fun productDao(): ProductDao
    abstract fun declarationDao(): DeclarationDao
    abstract fun complianceCheckDao(): ComplianceCheckDao
    abstract fun inspectionImageDao(): InspectionImageDao
    abstract fun ocrResultDao(): OcrResultDao
    abstract fun scannedLabelOcrDao(): ScannedLabelOcrDao
    abstract fun ruleDao(): RuleDao
    abstract fun auditLogDao(): AuditLogDao
    abstract fun userDao(): UserDao
    abstract fun pendingSyncDao(): PendingSyncDao
    abstract fun fssaiLicenseDao(): FssaiLicenseDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "proofmark_database.db"
                ).fallbackToDestructiveMigration(true).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
