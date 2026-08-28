package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.data.local.AppDatabase
import com.example.data.local.entities.DeclarationEntity
import com.example.data.local.entities.InspectionEntity
import com.example.data.local.entities.OcrResultEntity
import com.example.data.local.entities.ScannedLabelOcrEntity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/**
 * Room-to-Firestore Repository Bridge.
 * Synchronizes local Room database records (captured label metadata, OCR results, declarations)
 * with cloud-hosted Firebase Firestore collections ("scanned_labels", "ocr_results", "inspections").
 * Supports offline-first queueing and bi-directional record mapping.
 */
class RoomFirestoreBridge private constructor(private val context: Context) {

    private val db = AppDatabase.getInstance(context)
    private val TAG = "RoomFirestoreBridge"

    private val firestore: FirebaseFirestore?
        get() = try {
            if (FirebaseApp.getApps(context).isNotEmpty()) {
                FirebaseFirestore.getInstance()
            } else {
                null
            }
        } catch (e: Throwable) {
            Log.w(TAG, "FirebaseFirestore instance unavailable: ${e.message}")
            null
        }

    private val currentUserId: String?
        get() = try {
            FirebaseAuth.getInstance().currentUser?.uid
        } catch (e: Throwable) {
            null
        }

    /**
     * Stores a single captured label OCR record from Room to Firebase Firestore.
     */
    suspend fun storeScannedLabelMetadata(record: ScannedLabelOcrEntity): Result<String> = withContext(Dispatchers.IO) {
        val fs = firestore ?: return@withContext Result.failure(Exception("Firestore instance not initialized"))
        val docId = record.id.ifBlank { "scan_${record.timestamp}" }

        val payload = hashMapOf(
            "id" to record.id,
            "imagePath" to record.imagePath,
            "timestamp" to record.timestamp,
            "extractedRawText" to record.extractedRawText,
            "linesCount" to record.linesCount,
            "blocksCount" to record.blocksCount,
            "detectedLanguage" to record.detectedLanguage,
            "confidence" to record.confidence,
            "executionTimeMs" to record.executionTimeMs,
            "productName" to (record.productName ?: ""),
            "brand" to (record.brand ?: ""),
            "netQuantity" to (record.netQuantity ?: ""),
            "mrp" to (record.mrp ?: ""),
            "dateOfMfg" to (record.dateOfMfg ?: ""),
            "consumerCare" to (record.consumerCare ?: ""),
            "countryOfOrigin" to (record.countryOfOrigin ?: ""),
            "unitSalePrice" to (record.unitSalePrice ?: ""),
            "boundingBoxesJson" to record.boundingBoxesJson,
            "inspectionId" to (record.inspectionId ?: ""),
            "scanSource" to record.scanSource,
            "createdByUserId" to (currentUserId ?: "anonymous_inspector"),
            "lastSyncedAt" to System.currentTimeMillis()
        )

        try {
            fs.collection(COLLECTION_SCANNED_LABELS)
                .document(docId)
                .set(payload, SetOptions.merge())
                .await()
            Log.i(TAG, "Successfully synced label OCR record $docId to Firestore collection '$COLLECTION_SCANNED_LABELS'")
            Result.success(docId)
        } catch (e: Throwable) {
            Log.e(TAG, "Failed to push label OCR record $docId to Firestore: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Stores raw OCR result entity from Room to Firebase Firestore.
     */
    suspend fun storeOcrResult(ocrResult: OcrResultEntity): Result<String> = withContext(Dispatchers.IO) {
        val fs = firestore ?: return@withContext Result.failure(Exception("Firestore instance not initialized"))
        val docId = ocrResult.id.ifBlank { "ocr_${ocrResult.timestamp}" }

        val payload = hashMapOf(
            "id" to ocrResult.id,
            "inspectionId" to ocrResult.inspectionId,
            "imageId" to ocrResult.imageId,
            "rawText" to ocrResult.rawText,
            "detectedLanguage" to ocrResult.detectedLanguage,
            "confidence" to ocrResult.confidence,
            "boundingBoxesJson" to ocrResult.boundingBoxesJson,
            "timestamp" to ocrResult.timestamp,
            "createdByUserId" to (currentUserId ?: "anonymous_inspector"),
            "lastSyncedAt" to System.currentTimeMillis()
        )

        try {
            fs.collection(COLLECTION_OCR_RESULTS)
                .document(docId)
                .set(payload, SetOptions.merge())
                .await()
            Log.i(TAG, "Synced OCR result entity $docId to Firestore collection '$COLLECTION_OCR_RESULTS'")
            Result.success(docId)
        } catch (e: Throwable) {
            Log.e(TAG, "Failed to push OCR result $docId to Firestore: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Stores Inspection record and associated declarations from Room to Firebase Firestore.
     */
    suspend fun storeInspectionMetadata(
        inspection: InspectionEntity,
        declarations: List<DeclarationEntity> = emptyList()
    ): Result<String> = withContext(Dispatchers.IO) {
        val fs = firestore ?: return@withContext Result.failure(Exception("Firestore instance not initialized"))
        val docId = inspection.id

        val declarationsList = declarations.map { decl ->
            mapOf(
                "id" to decl.id,
                "fieldKey" to decl.fieldKey,
                "fieldName" to decl.fieldName,
                "extractedValue" to decl.extractedValue,
                "correctedValue" to (decl.correctedValue ?: ""),
                "confidence" to decl.confidence,
                "isVerified" to decl.isVerified,
                "sourceRuleCode" to decl.sourceRuleCode
            )
        }

        val payload = hashMapOf(
            "id" to inspection.id,
            "inspectionNumber" to inspection.inspectionNumber,
            "productId" to inspection.productId,
            "productName" to inspection.productName,
            "brand" to inspection.brand,
            "category" to inspection.category.name,
            "officerId" to inspection.officerId,
            "officerName" to inspection.officerName,
            "status" to inspection.status.name,
            "overallConfidence" to inspection.overallConfidence,
            "complianceScore" to inspection.complianceScore,
            "timestamp" to inspection.timestamp,
            "location" to inspection.location,
            "totalRulesChecked" to inspection.totalRulesChecked,
            "violationsCount" to inspection.violationsCount,
            "reviewRequiredCount" to inspection.reviewRequiredCount,
            "reviewNotes" to inspection.reviewNotes,
            "isFinalized" to inspection.isFinalized,
            "signedOffBy" to (inspection.signedOffBy ?: ""),
            "penaltyAmount" to inspection.penaltyAmount,
            "noticeNumber" to (inspection.noticeNumber ?: ""),
            "declarations" to declarationsList,
            "syncedByUserId" to (currentUserId ?: "anonymous_officer"),
            "lastSyncedAt" to System.currentTimeMillis()
        )

        try {
            fs.collection(COLLECTION_INSPECTIONS)
                .document(docId)
                .set(payload, SetOptions.merge())
                .await()
            Log.i(TAG, "Synced inspection metadata $docId to Firestore collection '$COLLECTION_INSPECTIONS'")
            Result.success(docId)
        } catch (e: Throwable) {
            Log.e(TAG, "Failed to push inspection $docId to Firestore: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Batch bridges all local Room ScannedLabelOcrEntity records to Cloud Firestore.
     */
    suspend fun syncAllRoomRecordsToFirestore(): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val localRecords = db.scannedLabelOcrDao().getAllScannedOcrRecordsDirect()
            if (localRecords.isEmpty()) {
                return@withContext Result.success(0)
            }

            var count = 0
            for (record in localRecords) {
                val res = storeScannedLabelMetadata(record)
                if (res.isSuccess) {
                    count++
                }
            }
            Log.i(TAG, "Batch bridged $count / ${localRecords.size} Room label records to Firestore.")
            Result.success(count)
        } catch (e: Throwable) {
            Log.e(TAG, "Failed batch sync to Firestore: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Fetches remote label metadata records from Firestore and imports any missing into local Room DB.
     */
    suspend fun pullRemoteScannedLabelsToRoom(): Result<Int> = withContext(Dispatchers.IO) {
        val fs = firestore ?: return@withContext Result.failure(Exception("Firestore instance not initialized"))
        try {
            val snapshot = fs.collection(COLLECTION_SCANNED_LABELS)
                .get()
                .await()

            var imported = 0
            for (doc in snapshot.documents) {
                val data = doc.data ?: continue
                val id = doc.id
                val rawText = data["extractedRawText"] as? String ?: continue
                val imagePath = data["imagePath"] as? String ?: ""
                val timestamp = (data["timestamp"] as? Long) ?: System.currentTimeMillis()

                val entity = ScannedLabelOcrEntity(
                    id = id,
                    imagePath = imagePath,
                    timestamp = timestamp,
                    extractedRawText = rawText,
                    linesCount = (data["linesCount"] as? Long)?.toInt() ?: 0,
                    blocksCount = (data["blocksCount"] as? Long)?.toInt() ?: 0,
                    detectedLanguage = data["detectedLanguage"] as? String ?: "en",
                    confidence = (data["confidence"] as? Double)?.toFloat() ?: 0.95f,
                    executionTimeMs = (data["executionTimeMs"] as? Long) ?: 0L,
                    productName = (data["productName"] as? String)?.ifBlank { null },
                    brand = (data["brand"] as? String)?.ifBlank { null },
                    netQuantity = (data["netQuantity"] as? String)?.ifBlank { null },
                    mrp = (data["mrp"] as? String)?.ifBlank { null },
                    dateOfMfg = (data["dateOfMfg"] as? String)?.ifBlank { null },
                    consumerCare = (data["consumerCare"] as? String)?.ifBlank { null },
                    countryOfOrigin = (data["countryOfOrigin"] as? String)?.ifBlank { null },
                    unitSalePrice = (data["unitSalePrice"] as? String)?.ifBlank { null },
                    boundingBoxesJson = data["boundingBoxesJson"] as? String ?: "[]",
                    inspectionId = (data["inspectionId"] as? String)?.ifBlank { null },
                    scanSource = data["scanSource"] as? String ?: "FIRESTORE_SYNC"
                )

                db.scannedLabelOcrDao().insertScannedOcrRecord(entity)
                imported++
            }
            Result.success(imported)
        } catch (e: Throwable) {
            Log.e(TAG, "Error pulling remote scanned labels from Firestore: ${e.message}", e)
            Result.failure(e)
        }
    }

    companion object {
        const val COLLECTION_SCANNED_LABELS = "scanned_labels"
        const val COLLECTION_OCR_RESULTS = "ocr_results"
        const val COLLECTION_INSPECTIONS = "inspections"

        @Volatile
        private var INSTANCE: RoomFirestoreBridge? = null

        fun getInstance(context: Context): RoomFirestoreBridge {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: RoomFirestoreBridge(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}
