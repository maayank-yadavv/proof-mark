package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entities.PendingSyncEntity
import com.example.data.models.SyncItemStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface PendingSyncDao {
    @Query("SELECT * FROM pending_sync_queue ORDER BY timestamp ASC")
    fun getAllPendingSyncItems(): Flow<List<PendingSyncEntity>>

    @Query("SELECT * FROM pending_sync_queue WHERE syncStatus = 'PENDING_SYNC' OR syncStatus = 'FAILED' ORDER BY timestamp ASC")
    suspend fun getItemsNeedingSyncDirect(): List<PendingSyncEntity>

    @Query("SELECT COUNT(*) FROM pending_sync_queue WHERE syncStatus = 'PENDING_SYNC' OR syncStatus = 'FAILED'")
    fun getPendingCountFlow(): Flow<Int>

    @Query("SELECT COUNT(*) FROM pending_sync_queue WHERE syncStatus = 'PENDING_SYNC' OR syncStatus = 'FAILED'")
    suspend fun getPendingCountDirect(): Int

    @Query("SELECT COUNT(*) FROM pending_sync_queue WHERE syncStatus = 'SYNCED'")
    suspend fun getSyncedCountDirect(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPendingItem(item: PendingSyncEntity)

    @Update
    suspend fun updatePendingItem(item: PendingSyncEntity)

    @Query("UPDATE pending_sync_queue SET syncStatus = :status, lastAttemptTimestamp = :timestamp WHERE id = :id")
    suspend fun updateSyncStatus(id: String, status: SyncItemStatus, timestamp: Long = System.currentTimeMillis())

    @Query("DELETE FROM pending_sync_queue WHERE id = :id")
    suspend fun deletePendingItem(id: String)

    @Query("DELETE FROM pending_sync_queue WHERE syncStatus = 'SYNCED'")
    suspend fun clearSyncedItems()

    @Query("DELETE FROM pending_sync_queue")
    suspend fun deleteAll()
}
