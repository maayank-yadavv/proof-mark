package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.models.SyncItemStatus
import java.util.UUID

@Entity(tableName = "pending_sync_queue")
data class PendingSyncEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val actionType: String, // "INSPECTION_RECORD", "RULE_UPDATE", "DECLARATION_OVERRIDE", "AUDIT_LOG_ENTRY"
    val entityId: String,
    val title: String = "Pending Operation",
    val payloadJson: String = "{}",
    val timestamp: Long = System.currentTimeMillis(),
    val syncStatus: SyncItemStatus = SyncItemStatus.PENDING_SYNC,
    val retryCount: Int = 0,
    val errorMessage: String? = null,
    val lastAttemptTimestamp: Long = System.currentTimeMillis()
)
