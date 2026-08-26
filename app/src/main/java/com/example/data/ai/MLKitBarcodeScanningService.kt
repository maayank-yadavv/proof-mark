package com.example.data.ai

import android.graphics.Bitmap
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

data class ScannedBarcodeResult(
    val rawValue: String,
    val displayValue: String,
    val format: Int,
    val formatName: String,
    val valueType: Int,
    val url: String? = null
)

object MLKitBarcodeScanningService {

    private val options = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(
            Barcode.FORMAT_QR_CODE,
            Barcode.FORMAT_EAN_13,
            Barcode.FORMAT_EAN_8,
            Barcode.FORMAT_UPC_A,
            Barcode.FORMAT_UPC_E,
            Barcode.FORMAT_CODE_128,
            Barcode.FORMAT_CODE_39,
            Barcode.FORMAT_DATA_MATRIX,
            Barcode.FORMAT_PDF417
        )
        .build()

    private val scanner = BarcodeScanning.getClient(options)

    suspend fun scanBarcodes(bitmap: Bitmap): Result<List<ScannedBarcodeResult>> =
        suspendCancellableCoroutine { continuation ->
            try {
                val inputImage = InputImage.fromBitmap(bitmap, 0)
                scanner.process(inputImage)
                    .addOnSuccessListener { barcodes ->
                        val results = barcodes.map { barcode ->
                            ScannedBarcodeResult(
                                rawValue = barcode.rawValue ?: "",
                                displayValue = barcode.displayValue ?: barcode.rawValue ?: "",
                                format = barcode.format,
                                formatName = getFormatName(barcode.format),
                                valueType = barcode.valueType,
                                url = barcode.url?.url
                            )
                        }
                        if (continuation.isActive) {
                            continuation.resume(Result.success(results))
                        }
                    }
                    .addOnFailureListener { exception ->
                        if (continuation.isActive) {
                            continuation.resume(Result.failure(exception))
                        }
                    }
            } catch (e: Exception) {
                if (continuation.isActive) {
                    continuation.resume(Result.failure(e))
                }
            }
        }

    private fun getFormatName(format: Int): String {
        return when (format) {
            Barcode.FORMAT_QR_CODE -> "QR Code"
            Barcode.FORMAT_EAN_13 -> "EAN-13 Barcode"
            Barcode.FORMAT_EAN_8 -> "EAN-8 Barcode"
            Barcode.FORMAT_UPC_A -> "UPC-A Barcode"
            Barcode.FORMAT_UPC_E -> "UPC-E Barcode"
            Barcode.FORMAT_CODE_128 -> "Code 128"
            Barcode.FORMAT_CODE_39 -> "Code 39"
            Barcode.FORMAT_DATA_MATRIX -> "Data Matrix"
            Barcode.FORMAT_PDF417 -> "PDF-417"
            else -> "Barcode"
        }
    }
}
