package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entities.FssaiLicenseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FssaiLicenseDao {
    @Query("SELECT * FROM fssai_licenses ORDER BY companyName ASC")
    fun getAllLicenses(): Flow<List<FssaiLicenseEntity>>

    @Query("SELECT * FROM fssai_licenses WHERE licenseNumber LIKE '%' || :query || '%' OR companyName LIKE '%' || :query || '%' OR brandName LIKE '%' || :query || '%' OR foodCategories LIKE '%' || :query || '%' ORDER BY companyName ASC")
    fun searchLicenses(query: String): Flow<List<FssaiLicenseEntity>>

    @Query("SELECT * FROM fssai_licenses WHERE licenseNumber = :licenseNumber LIMIT 1")
    suspend fun getLicenseByNumberDirect(licenseNumber: String): FssaiLicenseEntity?

    @Query("SELECT * FROM fssai_licenses WHERE status = :status ORDER BY companyName ASC")
    fun getLicensesByStatus(status: String): Flow<List<FssaiLicenseEntity>>

    @Query("SELECT COUNT(*) FROM fssai_licenses")
    suspend fun getLicenseCountDirect(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLicense(license: FssaiLicenseEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(licenses: List<FssaiLicenseEntity>)

    @Update
    suspend fun updateLicense(license: FssaiLicenseEntity)

    @Query("DELETE FROM fssai_licenses WHERE licenseNumber = :licenseNumber")
    suspend fun deleteLicense(licenseNumber: String)

    @Query("DELETE FROM fssai_licenses")
    suspend fun deleteAll()
}
