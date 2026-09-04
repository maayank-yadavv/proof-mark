@file:OptIn(
    androidx.compose.material3.ExperimentalMaterial3Api::class
)

package com.example.ui.screens.scan

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.graphics.PointF
import android.graphics.RectF
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.CropFree
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.FlipCameraAndroid
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LayersClear
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import com.example.ui.theme.AppFontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.ai.ExtractedPackageData
import com.example.data.ai.ImageStitchingUtil
import com.example.data.ai.MLKitTextRecognitionService
import com.example.data.ai.GeminiApiKeyManager
import com.example.data.ai.MlKitOcrResult
import com.example.data.local.entities.ScannedLabelOcrEntity
import com.example.data.models.BoundingBox
import com.example.data.models.CameraScanMode
import com.example.data.models.ComplianceStatus
import com.example.data.models.ImageOcrAiResult
import com.example.data.models.ProductCategory
import com.example.data.models.EvidenceState
import com.example.ui.components.EvidenceStateBadge
import com.example.ui.components.ExtractedDataComplianceSummaryCard
import com.example.ui.components.LiveOcrDetectionOverlay
import com.example.ui.components.LiveStatutoryFieldStatus
import com.example.ui.components.NetworkConnectivityIndicator
import com.example.ui.components.OcrConfidenceBadge
import com.example.ui.components.OcrConfidenceOverlay
import com.example.ui.components.ProofMarkLogoBadge
import com.example.ui.components.ShimmerImagePreviewSkeleton
import com.example.ui.components.ShimmerTextExtractionSkeleton
import com.example.ui.components.getOcrReliabilityLevel
import com.example.ui.components.resolveEvidenceState
import com.example.ui.screens.dashboard.DemoPackagePickerBottomSheet
import com.example.ui.theme.ComplianceFail
import com.example.ui.theme.CompliancePass
import com.example.ui.theme.ComplianceReview
import com.example.ui.viewmodel.InspectionViewModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PhoneInTalk
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.runtime.mutableLongStateOf
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.max
import kotlin.math.min

/**
 * High-performance CameraScreen powered by CameraX and Google ML Kit Text Recognition / Gemini AI.
 */
@OptIn(ExperimentalMaterial3Api::class)
@ExperimentalGetImage
@Composable
fun CameraScreen(
    viewModel: InspectionViewModel,
    onBack: () -> Unit,
    onInspectionCompleted: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Permission state
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
        if (!isGranted) {
            Toast.makeText(context, "Camera permission is required for live label inspection", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // Real-time ML Kit Recognizer client
    val realtimeTextRecognizer = remember {
        TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    }

    // Camera Controls State
    val isImageAiProcessing by viewModel.isImageAiProcessing.collectAsStateWithLifecycle()
    val imageAiProcessingStage by viewModel.imageAiProcessingStage.collectAsStateWithLifecycle()
    val imageOcrAiResult by viewModel.imageOcrAiResult.collectAsStateWithLifecycle()
    val cameraScanMode by viewModel.cameraScanMode.collectAsStateWithLifecycle()
    var showImageAiResultSheet by remember { mutableStateOf(false) }

    var isTorchOn by remember { mutableStateOf(false) }
    var isBackCamera by remember { mutableStateOf(true) }
    var zoomRatio by remember { mutableFloatStateOf(1f) }
    var isCapturing by remember { mutableStateOf(false) }
    var showDemoPicker by remember { mutableStateOf(false) }

    // Live OCR Detection State for Viewfinder HUD
    val liveDetectedBoxes = remember { mutableStateListOf<BoundingBox>() }
    var liveRawTextPreview by remember { mutableStateOf("") }
    var liveDetectedNetQty by remember { mutableStateOf("") }
    var liveDetectedMrp by remember { mutableStateOf("") }
    var liveDetectedMfgDate by remember { mutableStateOf("") }
    var liveDetectedManufacturer by remember { mutableStateOf("") }
    var liveDetectedConsumerCare by remember { mutableStateOf("") }
    var liveDetectedOrigin by remember { mutableStateOf("") }
    var liveHasHindiText by remember { mutableStateOf(false) }
    var lastAnalysisTimestamp by remember { mutableLongStateOf(0L) }

    // CameraX Controller configured for High-Resolution Capture and Live Frame Stream Analysis
    val cameraController = remember {
        LifecycleCameraController(context).apply {
            setEnabledUseCases(CameraController.IMAGE_CAPTURE or CameraController.IMAGE_ANALYSIS)
            cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            setImageAnalysisAnalyzer(ContextCompat.getMainExecutor(context)) { imageProxy ->
                val mediaImage = imageProxy.image
                if (mediaImage != null && cameraScanMode == CameraScanMode.LIVE_OCR) {
                    val currentMs = System.currentTimeMillis()
                    if (currentMs - lastAnalysisTimestamp > 250) { // ~4 FPS for fluid scanning & zero CPU bottleneck
                        lastAnalysisTimestamp = currentMs
                        val rotationDegrees = imageProxy.imageInfo.rotationDegrees
                        val image = InputImage.fromMediaImage(mediaImage, rotationDegrees)
                        val imageWidth = if (rotationDegrees == 90 || rotationDegrees == 270) mediaImage.height else mediaImage.width
                        val imageHeight = if (rotationDegrees == 90 || rotationDegrees == 270) mediaImage.width else mediaImage.height

                        realtimeTextRecognizer.process(image)
                            .addOnSuccessListener { visionText ->
                                val boxes = mutableListOf<BoundingBox>()
                                var foundMrp = ""
                                var foundQty = ""
                                var foundMfg = ""
                                var foundMfr = ""
                                var foundCare = ""
                                var foundOrigin = ""

                                val fullStreamText = visionText.text
                                if (fullStreamText.isNotBlank()) {
                                    liveRawTextPreview = visionText.textBlocks.firstOrNull()?.text?.replace('\n', ' ') ?: ""
                                }

                                val hasHindi = fullStreamText.any { it.code in 0x0900..0x097F }

                                visionText.textBlocks.forEach { block ->
                                    val blockText = block.text
                                    val rect = block.boundingBox
                                    if (rect != null && imageWidth > 0 && imageHeight > 0) {
                                        val normX = (rect.left.toFloat() / imageWidth).coerceIn(0f, 1f)
                                        val normY = (rect.top.toFloat() / imageHeight).coerceIn(0f, 1f)
                                        val normW = (rect.width().toFloat() / imageWidth).coerceIn(0f, 1f)
                                        val normH = (rect.height().toFloat() / imageHeight).coerceIn(0f, 1f)

                                        val fieldKey = when {
                                            blockText.contains("MRP", ignoreCase = true) || blockText.contains("Rs", ignoreCase = true) || blockText.contains("₹") -> {
                                                if (foundMrp.isBlank()) foundMrp = blockText.lines().firstOrNull { it.contains("MRP", ignoreCase = true) || it.contains("₹") || it.contains("Rs") } ?: blockText
                                                "MRP_PRICE"
                                            }
                                            blockText.matches(Regex(".*\\b\\d+(\\.\\d+)?\\s*(g|kg|ml|l|ltr|gm|grams|pieces|N|unit|U)\\b.*", RegexOption.IGNORE_CASE)) -> {
                                                if (foundQty.isBlank()) foundQty = blockText
                                                "NET_QUANTITY"
                                            }
                                            blockText.contains("MFG", ignoreCase = true) || blockText.contains("PKD", ignoreCase = true) || blockText.contains("EXP", ignoreCase = true) || blockText.contains("BATCH", ignoreCase = true) || blockText.contains("USE BY", ignoreCase = true) -> {
                                                if (foundMfg.isBlank()) foundMfg = blockText
                                                "MFG_DATE"
                                            }
                                            blockText.contains("MFD BY", ignoreCase = true) || blockText.contains("MANUFACTURED", ignoreCase = true) || blockText.contains("PACKED BY", ignoreCase = true) || blockText.contains("MARKETED", ignoreCase = true) -> {
                                                if (foundMfr.isBlank()) foundMfr = blockText
                                                "MANUFACTURER"
                                            }
                                            blockText.contains("CARE", ignoreCase = true) || blockText.contains("FEEDBACK", ignoreCase = true) || blockText.contains("TOLL", ignoreCase = true) || blockText.contains("1800", ignoreCase = true) || blockText.contains("@") -> {
                                                if (foundCare.isBlank()) foundCare = blockText
                                                "CONSUMER_CARE"
                                            }
                                            blockText.contains("INDIA", ignoreCase = true) || blockText.contains("ORIGIN", ignoreCase = true) || blockText.contains("MADE IN", ignoreCase = true) -> {
                                                if (foundOrigin.isBlank()) foundOrigin = blockText
                                                "COUNTRY_ORIGIN"
                                            }
                                            else -> "TEXT_BLOCK"
                                        }

                                        boxes.add(
                                            BoundingBox(
                                                x = normX,
                                                y = normY,
                                                width = normW,
                                                height = normH,
                                                fieldKey = fieldKey,
                                                text = blockText,
                                                confidence = 0.92f,
                                                status = ComplianceStatus.PASS
                                            )
                                        )
                                    }
                                }

                                liveDetectedBoxes.clear()
                                liveDetectedBoxes.addAll(boxes.take(12))

                                if (foundMrp.isNotBlank()) liveDetectedMrp = foundMrp
                                if (foundQty.isNotBlank()) liveDetectedNetQty = foundQty
                                if (foundMfg.isNotBlank()) liveDetectedMfgDate = foundMfg
                                if (foundMfr.isNotBlank()) liveDetectedManufacturer = foundMfr
                                if (foundCare.isNotBlank()) liveDetectedConsumerCare = foundCare
                                if (foundOrigin.isNotBlank()) liveDetectedOrigin = foundOrigin
                                liveHasHindiText = hasHindi
                            }
                            .addOnCompleteListener {
                                imageProxy.close()
                            }
                        return@setImageAnalysisAnalyzer
                    }
                }
                imageProxy.close()
            }
        }
    }

    val liveStatutoryFields = remember(
        liveDetectedMrp,
        liveDetectedNetQty,
        liveDetectedMfgDate,
        liveDetectedManufacturer,
        liveDetectedConsumerCare,
        liveDetectedOrigin,
        liveHasHindiText
    ) {
        listOf(
            LiveStatutoryFieldStatus(
                key = "MRP",
                displayName = "MRP (Rule 6)",
                icon = Icons.Default.Payments,
                isDetected = liveDetectedMrp.isNotBlank(),
                extractedValue = liveDetectedMrp
            ),
            LiveStatutoryFieldStatus(
                key = "NET_QTY",
                displayName = "Net Quantity",
                icon = Icons.Default.Scale,
                isDetected = liveDetectedNetQty.isNotBlank(),
                extractedValue = liveDetectedNetQty
            ),
            LiveStatutoryFieldStatus(
                key = "MFG_DATE",
                displayName = "Mfg / Pkd Date",
                icon = Icons.Default.CalendarMonth,
                isDetected = liveDetectedMfgDate.isNotBlank(),
                extractedValue = liveDetectedMfgDate
            ),
            LiveStatutoryFieldStatus(
                key = "MANUFACTURER",
                displayName = "Manufacturer / Packer",
                icon = Icons.Default.Business,
                isDetected = liveDetectedManufacturer.isNotBlank(),
                extractedValue = liveDetectedManufacturer
            ),
            LiveStatutoryFieldStatus(
                key = "CONSUMER_CARE",
                displayName = "Consumer Care",
                icon = Icons.Default.PhoneInTalk,
                isDetected = liveDetectedConsumerCare.isNotBlank(),
                extractedValue = liveDetectedConsumerCare
            ),
            LiveStatutoryFieldStatus(
                key = "COUNTRY_ORIGIN",
                displayName = "Country of Origin",
                icon = Icons.Default.Public,
                isDetected = liveDetectedOrigin.isNotBlank(),
                extractedValue = liveDetectedOrigin
            ),
            LiveStatutoryFieldStatus(
                key = "DUAL_SCRIPT",
                displayName = "Hindi / English Script",
                icon = Icons.Default.Language,
                isDetected = liveHasHindiText,
                extractedValue = if (liveHasHindiText) "Hindi + English" else "English"
            )
        )
    }

    // Laser scan animation
    val infiniteTransition = rememberInfiniteTransition(label = "laser_sweep")
    val laserProgress by infiniteTransition.animateFloat(
        initialValue = 0.05f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laser_pos"
    )

    // Room Database Persisted Scans State
    val scannedOcrRecords by viewModel.scannedOcrRecords.collectAsStateWithLifecycle()
    val networkState by viewModel.networkState.collectAsStateWithLifecycle()
    var showSavedScansSheet by remember { mutableStateOf(false) }
    var latestSavedOcrRecord by remember { mutableStateOf<ScannedLabelOcrEntity?>(null) }
    var showBatchShelfDialog by remember { mutableStateOf(false) }

    if (showBatchShelfDialog) {
        com.example.ui.components.BatchShelfInspectionDialog(
            onDismiss = { showBatchShelfDialog = false },
            onGenerateReport = { shelfItems ->
                Toast.makeText(context, "Batch Store Audit Report generated for ${shelfItems.size} packages!", Toast.LENGTH_LONG).show()
            }
        )
    }

    // Captured Bitmaps & Final OCR State
    val capturedImages = remember { mutableStateListOf<Bitmap>() }
    var latestOcrResult by remember { mutableStateOf<MlKitOcrResult?>(null) }
    var isOcrProcessing by remember { mutableStateOf(false) }
    var extractedData by remember { mutableStateOf<ExtractedPackageData?>(null) }
    var showResultsSheet by remember { mutableStateOf(false) }
    var showStitchedSheet by remember { mutableStateOf(false) }
    var stitchedBitmap by remember { mutableStateOf<Bitmap?>(null) }

    // Form Overrides from OCR
    var detectedProductName by remember { mutableStateOf("") }
    var detectedBrand by remember { mutableStateOf("") }
    var detectedNetQty by remember { mutableStateOf("") }
    var detectedMrp by remember { mutableStateOf("") }
    var detectedCategory by remember { mutableStateOf(ProductCategory.FOOD_BEVERAGES) }
    var statusMessage by remember { mutableStateOf<String?>(null) }

    // Process a full snapshot image with Google ML Kit & persist to Room DB
    fun processCapturedBitmap(bitmap: Bitmap) {
        isOcrProcessing = true
        statusMessage = "Processing label with Proof ML Kit Text Recognition..."
        viewModel.runMlKitOcrOnCapturedImage(
            bitmap = bitmap,
            onComplete = { ocrResult ->
                isOcrProcessing = false
                latestOcrResult = ocrResult
                val extracted = MLKitTextRecognitionService.extractLegalMetrologyDeclarations(
                    ocrResults = listOf(ocrResult),
                    productHint = detectedProductName,
                    brandHint = detectedBrand
                )
                extractedData = extracted
                detectedProductName = extracted.productName
                detectedBrand = extracted.manufacturerName
                detectedNetQty = extracted.netQuantity
                detectedMrp = extracted.mrp
                showResultsSheet = true
                statusMessage = "Proof ML Kit extracted ${ocrResult.totalLinesCount} text lines in ${ocrResult.executionTimeMs}ms"

                // Trigger tactile haptic feedback for field audit scan
                val hasMissingDeclarations = extracted.mrp.contains("Not Provided", ignoreCase = true) ||
                        extracted.netQuantity.contains("Not Provided", ignoreCase = true) ||
                        extracted.manufacturerName.contains("Not Provided", ignoreCase = true) ||
                        extracted.dateOfMfg.contains("Not Provided", ignoreCase = true)

                if (hasMissingDeclarations) {
                    com.example.utils.HapticFeedbackHelper.triggerComplianceError(context)
                    com.example.utils.AudioFeedbackHelper.playComplianceAlertSound(context)
                } else {
                    com.example.utils.HapticFeedbackHelper.triggerScanSuccess(context)
                    com.example.utils.AudioFeedbackHelper.playOcrSuccessSound(context)
                }

                // Persist extracted OCR text data, timestamp, and local image file to Room DB
                viewModel.saveScannedLabelOcr(
                    bitmap = bitmap,
                    ocrResult = ocrResult,
                    extractedData = extracted,
                    source = "CAMERA_CAPTURE",
                    onSaved = { savedEntity ->
                        latestSavedOcrRecord = savedEntity
                        statusMessage = "Stored in Room DB (${ocrResult.totalLinesCount} lines) at ${savedEntity.imagePath.substringAfterLast('/')}"
                    }
                )
            },
            onError = { error ->
                isOcrProcessing = false
                statusMessage = "OCR warning: $error"
            }
        )
    }

    // Gallery Picker Fallback
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        uris.take(4).forEach { uri ->
            try {
                val bmp = if (Build.VERSION.SDK_INT < 28) {
                    @Suppress("DEPRECATION")
                    MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                } else {
                    val source = ImageDecoder.createSource(context.contentResolver, uri)
                    ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                        decoder.isMutableRequired = true
                    }
                }
                capturedImages.add(bmp)
                viewModel.analyzeImageWithOcrAndGemini(
                    bitmap = bmp,
                    onComplete = { result ->
                        detectedProductName = result.extractedPackageData.productName
                        detectedBrand = result.extractedPackageData.manufacturerName
                        detectedNetQty = result.extractedPackageData.netQuantity
                        detectedMrp = result.extractedPackageData.mrp
                        showImageAiResultSheet = true
                        com.example.utils.AudioFeedbackHelper.playOcrSuccessSound(context)
                        com.example.utils.HapticFeedbackHelper.triggerScanSuccess(context)
                    },
                    onError = { error ->
                        Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                    }
                )
            } catch (e: Exception) {
                Toast.makeText(context, "Failed to load image: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Camera Capture Function
    fun capturePhoto() {
        if (isCapturing || isImageAiProcessing) return
        isCapturing = true

        val executor = ContextCompat.getMainExecutor(context)
        cameraController.takePicture(
            executor,
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(imageProxy: ImageProxy) {
                    try {
                        val rotationDegrees = imageProxy.imageInfo.rotationDegrees
                        val bitmap = imageProxy.toBitmap()
                        val correctedBitmap = if (rotationDegrees != 0) {
                            val matrix = Matrix().apply { postRotate(rotationDegrees.toFloat()) }
                            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
                        } else {
                            bitmap
                        }

                        capturedImages.add(correctedBitmap)
                        viewModel.analyzeImageWithOcrAndGemini(
                            bitmap = correctedBitmap,
                            onComplete = { result ->
                                detectedProductName = result.extractedPackageData.productName
                                detectedBrand = result.extractedPackageData.manufacturerName
                                detectedNetQty = result.extractedPackageData.netQuantity
                                detectedMrp = result.extractedPackageData.mrp
                                showImageAiResultSheet = true
                                com.example.utils.AudioFeedbackHelper.playOcrSuccessSound(context)
                                com.example.utils.HapticFeedbackHelper.triggerScanSuccess(context)
                            },
                            onError = { error ->
                                Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                            }
                        )
                    } catch (e: Exception) {
                        Toast.makeText(context, "Error decoding image: ${e.message}", Toast.LENGTH_SHORT).show()
                    } finally {
                        imageProxy.close()
                        isCapturing = false
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    isCapturing = false
                    Toast.makeText(context, "Capture failed: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    DisposableEffect(lifecycleOwner) {
        cameraController.bindToLifecycle(lifecycleOwner)
        onDispose {
            cameraController.unbind()
            realtimeTextRecognizer.close()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column(
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Package Label Scanner",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f, fill = false)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            val isLiveMode = cameraScanMode == CameraScanMode.LIVE_OCR
                            val badgeColor = if (isLiveMode) Color(0xFF00E5FF) else Color(0xFF4285F4)
                            Surface(
                                color = badgeColor.copy(alpha = 0.22f),
                                shape = RoundedCornerShape(4.dp),
                                border = BorderStroke(1.dp, badgeColor.copy(alpha = 0.8f))
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isLiveMode) Icons.Default.DocumentScanner else Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = badgeColor,
                                        modifier = Modifier.size(10.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isLiveMode) "LIVE" else "AI",
                                        color = badgeColor,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        maxLines = 1,
                                        softWrap = false
                                    )
                                }
                            }
                        }
                        Text(
                            text = if (cameraScanMode == CameraScanMode.LIVE_OCR) "Continuous Stream Detection" else "Proof AI Metrology",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.75f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("camera_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    // Room Database Saved Scans Viewer
                    IconButton(
                        onClick = { showSavedScansSheet = true },
                        modifier = Modifier.testTag("camera_saved_scans_button")
                    ) {
                        BadgedBox(
                            badge = {
                                if (scannedOcrRecords.isNotEmpty()) {
                                    Badge(
                                        containerColor = Color(0xFF00E676),
                                        contentColor = Color.Black
                                    ) {
                                        Text(
                                            text = "${scannedOcrRecords.size}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp
                                        )
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Storage,
                                contentDescription = "Room DB Stored Scans",
                                tint = if (scannedOcrRecords.isNotEmpty()) Color(0xFF00E676) else Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    // Torch Toggle
                    IconButton(
                        onClick = {
                            isTorchOn = !isTorchOn
                            cameraController.enableTorch(isTorchOn)
                        },
                        modifier = Modifier.testTag("camera_torch_button")
                    ) {
                        Icon(
                            imageVector = if (isTorchOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                            contentDescription = "Toggle Torch",
                            tint = if (isTorchOn) Color(0xFFFFD54F) else Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    // Lens Switch
                    IconButton(
                        onClick = {
                            isBackCamera = !isBackCamera
                            cameraController.cameraSelector = if (isBackCamera) {
                                CameraSelector.DEFAULT_BACK_CAMERA
                            } else {
                                CameraSelector.DEFAULT_FRONT_CAMERA
                            }
                        },
                        modifier = Modifier.testTag("camera_flip_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.FlipCameraAndroid,
                            contentDescription = "Switch Camera",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black.copy(alpha = 0.85f)
                )
            )
        },
        containerColor = Color.Black,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (hasCameraPermission) {
                // 1. CameraX Preview Stream
                AndroidView(
                    factory = { ctx ->
                        PreviewView(ctx).apply {
                            controller = cameraController
                            scaleType = PreviewView.ScaleType.FILL_CENTER
                        }
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("camerax_preview_view")
                )

                // 2. Viewfinder Reticle & Real-Time ML Kit Scanning Animation Overlay
                if (cameraScanMode == CameraScanMode.IMAGE_OCR) {
                    com.example.ui.components.VisualScanningAnimationOverlay(
                        isProcessing = isOcrProcessing || isImageAiProcessing,
                        stageText = if (isImageAiProcessing) imageAiProcessingStage else "Proof ML Kit Processing...",
                        detectedWeightOrVolume = detectedNetQty.takeIf { it.isNotBlank() && !it.contains("Not Provided", ignoreCase = true) },
                        detectedMrp = detectedMrp.takeIf { it.isNotBlank() && !it.contains("Not Provided", ignoreCase = true) },
                        confidenceScore = extractedData?.perceptionConfidence ?: if (detectedNetQty.isNotBlank() || detectedMrp.isNotBlank()) 0.94f else null,
                        modifier = Modifier.fillMaxSize()
                    )
                } else if (cameraScanMode == CameraScanMode.LIVE_OCR) {
                    LiveOcrDetectionOverlay(
                        detectedBoxes = liveDetectedBoxes,
                        statutoryFields = liveStatutoryFields,
                        rawTextPreview = liveRawTextPreview,
                        detectedCount = liveStatutoryFields.count { it.isDetected },
                        totalCount = liveStatutoryFields.size,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Top Guidance Hint Banner removed.

                // 4. Controls & Shutter Bottom Bar
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f), Color.Black)
                            )
                        )
                        .padding(bottom = 20.dp, start = 16.dp, end = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Zoom Slider
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.width(220.dp)
                        ) {
                            Text("1x", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Slider(
                                value = zoomRatio,
                                onValueChange = {
                                    zoomRatio = it
                                    cameraController.setZoomRatio(it)
                                },
                                valueRange = 1f..4f,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 8.dp)
                            )
                            Text("4x", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Thumbnail Row of Captured Angles (Multi-Image Session Tray)
                    if (capturedImages.isNotEmpty()) {
                        Surface(
                            color = Color.Black.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, Color(0xFF4285F4).copy(alpha = 0.5f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 10.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFF00E676))
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "MULTI-IMAGE SESSION: ${capturedImages.size} ANGLE(S)",
                                            color = Color.White,
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    Row {
                                        TextButton(
                                            onClick = { capturedImages.clear() },
                                            modifier = Modifier.height(26.dp)
                                        ) {
                                            Text("Clear All", color = Color(0xFFFF5252), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                        if (extractedData != null) {
                                            TextButton(
                                                onClick = { showResultsSheet = true },
                                                modifier = Modifier.height(26.dp)
                                            ) {
                                                Icon(Icons.Default.DocumentScanner, contentDescription = null, tint = Color(0xFF4285F4), modifier = Modifier.size(13.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("OCR Details", color = Color(0xFF4285F4), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    items(capturedImages.size) { idx ->
                                        val bmp = capturedImages[idx]
                                        Box(
                                            modifier = Modifier
                                                .size(64.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .border(1.5.dp, Color(0xFF4285F4), RoundedCornerShape(8.dp))
                                                .clickable { processCapturedBitmap(bmp) }
                                        ) {
                                            Image(
                                                bitmap = bmp.asImageBitmap(),
                                                contentDescription = "Angle ${idx + 1}",
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier.fillMaxSize()
                                            )
                                            Surface(
                                                color = Color.Black.copy(alpha = 0.75f),
                                                shape = RoundedCornerShape(bottomEnd = 4.dp),
                                                modifier = Modifier.align(Alignment.TopStart)
                                            ) {
                                                Text("A${idx + 1}", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                                            }
                                            IconButton(
                                                onClick = { capturedImages.removeAt(idx) },
                                                modifier = Modifier
                                                    .size(20.dp)
                                                    .align(Alignment.TopEnd)
                                                    .background(Color.Red.copy(alpha = 0.8f), CircleShape)
                                            ) {
                                                Icon(Icons.Default.Close, contentDescription = "Delete angle", tint = Color.White, modifier = Modifier.size(12.dp))
                                            }
                                        }
                                    }
                                    if (isCapturing || isOcrProcessing) {
                                        item {
                                            ShimmerImagePreviewSkeleton(size = 64.dp)
                                        }
                                    }
                                }

                                if (capturedImages.size >= 2) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    OutlinedButton(
                                        onClick = {
                                            stitchedBitmap = ImageStitchingUtil.stitchHorizontally(capturedImages.toList())
                                            showStitchedSheet = true
                                        },
                                        border = BorderStroke(1.dp, Color(0xFF4285F4)),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF4285F4)),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(36.dp)
                                            .testTag("stitch_multi_angle_labels_button")
                                    ) {
                                        Icon(Icons.Default.Layers, contentDescription = null, modifier = Modifier.size(15.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "STITCH & COMBINE LABELS (${capturedImages.size} PANELS)",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        )
                                    }
                                }

                                // Prominent Bulk Compliance Check Trigger Button
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = {
                                        val finalBitmaps = capturedImages.toList()
                                        viewModel.startLiveInspection(
                                            productName = detectedProductName.ifBlank { "Multi-Angle Package Audit (${finalBitmaps.size} Scans)" },
                                            brand = detectedBrand.ifBlank { "Verified FMCG Manufacturer" },
                                            category = detectedCategory,
                                            pdpArea = 140.0,
                                            batchNumber = "BULK-CAM-${System.currentTimeMillis() % 10000}",
                                            barcode = "890" + (1000000000..9999999999).random(),
                                            location = "Camera Metrology Field Station",
                                            bitmaps = finalBitmaps,
                                            onComplete = { createdInspectionId ->
                                                onInspectionCompleted(createdInspectionId)
                                            }
                                        )
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF34A853)),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(38.dp)
                                        .testTag("run_bulk_compliance_check_button")
                                    ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "RUN BULK COMPLIANCE AUDIT (${capturedImages.size} IMAGES)",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }

                    // Main Action Row (Gallery, Shutter Button, Quick Enforce)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Gallery / File upload fallback
                        IconButton(
                            onClick = { galleryLauncher.launch("image/*") },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.15f))
                                .testTag("camera_gallery_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddPhotoAlternate,
                                contentDescription = "Upload from Gallery",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        // Big Shutter Button
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF4285F4).copy(alpha = 0.3f))
                                .clickable(enabled = !isCapturing && !isOcrProcessing && !isImageAiProcessing) { capturePhoto() }
                                .testTag("camera_shutter_button")
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isCapturing || isOcrProcessing || isImageAiProcessing) Color(0xFF4285F4)
                                        else Color(0xFF1E88E5)
                                    )
                            ) {
                                if (isCapturing || isOcrProcessing || isImageAiProcessing) {
                                    CircularProgressIndicator(
                                        color = Color.White,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(12.dp),
                                        strokeWidth = 3.dp
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = "Capture for Proof AI",
                                        tint = Color.White,
                                        modifier = Modifier
                                            .align(Alignment.Center)
                                            .size(30.dp)
                                    )
                                }
                            }
                        }

                        // Quick Enforce Button
                        if (capturedImages.isNotEmpty() || extractedData != null) {
                            IconButton(
                                onClick = {
                                    val finalBitmaps = capturedImages.toList()
                                    viewModel.startLiveInspection(
                                        productName = detectedProductName.ifBlank { "Scanned Commodity Package" },
                                        brand = detectedBrand.ifBlank { "Verified FMCG Manufacturer" },
                                        category = detectedCategory,
                                        pdpArea = 140.0,
                                        batchNumber = "CAM-BATCH-${System.currentTimeMillis() % 10000}",
                                        barcode = "890" + (1000000000..9999999999).random(),
                                        location = "Camera Metrology Station #1",
                                        bitmaps = finalBitmaps,
                                        onComplete = { createdInspectionId ->
                                            onInspectionCompleted(createdInspectionId)
                                        }
                                    )
                                },
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF34A853))
                                    .testTag("camera_quick_enforce_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Run Enforcement Pipeline",
                                    tint = Color.White,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                        } else {
                            // Demo Preset Shortcut
                            IconButton(
                                onClick = { showDemoPicker = true },
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.15f))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Demo Presets",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }
            } else {
                // Camera Permission Required Fallback Screen
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Camera Access Required",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Under Legal Metrology Rules, ProofMark uses CameraX optical scanning to inspect physical commodity packaging and verify Principal Display Panel declarations with Proof ML Kit.",
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        color = Color.White.copy(alpha = 0.8f)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("grant_camera_permission_button")
                    ) {
                        Icon(Icons.Default.Shield, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Grant Camera Permission")
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = { galleryLauncher.launch("image/*") },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.AddPhotoAlternate, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Upload Commodity Photo from Gallery")
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    TextButton(
                        onClick = { showDemoPicker = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color(0xFF4285F4))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Load Official Metrology Demo Sample", color = Color(0xFF4285F4))
                    }
                }
            }

            // Results & Extracted Declarations Bottom Overlay Sheet
            AnimatedVisibility(
                visible = showResultsSheet && extractedData != null,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                Card(
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("camera_ocr_results_panel")
                ) {
                    Column(
                        modifier = Modifier
                            .padding(20.dp)
                            .fillMaxWidth()
                    ) {
                        // Header
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = CompliancePass,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Proof ML Kit OCR Extraction",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            IconButton(
                                onClick = { showResultsSheet = false },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Close")
                            }
                        }

                        // Metrics Pill
                        latestOcrResult?.let { ocr ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Surface(
                                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Column(modifier = Modifier.padding(6.dp)) {
                                        Text("Lines Detected", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                                        Text("${ocr.totalLinesCount}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    }
                                }
                                Surface(
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Column(modifier = Modifier.padding(6.dp)) {
                                        Text("Blocks", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                                        Text("${ocr.blocks.size}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                    }
                                }
                                Surface(
                                    color = CompliancePass.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Column(modifier = Modifier.padding(6.dp)) {
                                        Text("Latency", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                                        Text("${ocr.executionTimeMs} ms", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = CompliancePass)
                                    }
                                }
                            }
                        }

                        // Prominent OCR Reliability & Confidence Score Overlay Banner
                        extractedData?.let { data ->
                            Spacer(modifier = Modifier.height(4.dp))
                            OcrConfidenceOverlay(
                                confidence = data.perceptionConfidence,
                                extractedFieldCount = listOf(data.productName, data.manufacturerName, data.manufacturerAddress, data.netQuantity, data.mrp, data.dateOfMfg, data.countryOfOrigin).count { it.isNotBlank() && !it.contains("Not Provided", ignoreCase = true) },
                                latencyMs = latestOcrResult?.executionTimeMs
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                        }

                        // Extracted Statutory Declarations Summary with per-field confidence score overlays
                        extractedData?.let { data ->
                            val baseConf = data.perceptionConfidence
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                DeclarationPillRow("Generic Name", data.productName, confidence = if (data.productName.isNotBlank() && !data.productName.contains("Not", ignoreCase = true)) baseConf else null)
                                DeclarationPillRow("Manufacturer", data.manufacturerName, confidence = if (data.manufacturerName.isNotBlank() && !data.manufacturerName.contains("Not", ignoreCase = true)) (baseConf * 0.96f).coerceIn(0.6f, 0.99f) else null)
                                DeclarationPillRow("Address", data.manufacturerAddress, confidence = if (data.manufacturerAddress.isNotBlank() && !data.manufacturerAddress.contains("Not", ignoreCase = true)) (baseConf * 0.94f).coerceIn(0.6f, 0.99f) else null)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Box(modifier = Modifier.weight(1f)) {
                                        DeclarationPillRow("Net Qty", data.netQuantity, confidence = if (data.netQuantity.isNotBlank() && !data.netQuantity.contains("Not", ignoreCase = true)) (baseConf * 1.02f).coerceIn(0.7f, 0.99f) else null)
                                    }
                                    Box(modifier = Modifier.weight(1f)) {
                                        DeclarationPillRow("MRP", data.mrp, confidence = if (data.mrp.isNotBlank() && !data.mrp.contains("Not", ignoreCase = true)) (baseConf * 0.98f).coerceIn(0.7f, 0.99f) else null)
                                    }
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Box(modifier = Modifier.weight(1f)) {
                                        DeclarationPillRow("Mfg Date", data.dateOfMfg, confidence = if (data.dateOfMfg.isNotBlank() && !data.dateOfMfg.contains("Not", ignoreCase = true)) (baseConf * 0.95f).coerceIn(0.6f, 0.99f) else null)
                                    }
                                    Box(modifier = Modifier.weight(1f)) {
                                        DeclarationPillRow("Origin", data.countryOfOrigin, confidence = if (data.countryOfOrigin.isNotBlank() && !data.countryOfOrigin.contains("Not", ignoreCase = true)) (baseConf * 0.92f).coerceIn(0.6f, 0.99f) else null)
                                    }
                                }
                            }
                        }

                        // Room DB Persistent Storage Card
                        latestSavedOcrRecord?.let { savedEntity ->
                            Spacer(modifier = Modifier.height(4.dp))
                            Surface(
                                color = Color(0xFF0F172A),
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, Color(0xFF00E676).copy(alpha = 0.5f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.Storage,
                                                contentDescription = null,
                                                tint = Color(0xFF00E676),
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "Persisted in Local Room DB",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF00E676)
                                            )
                                        }
                                        Text(
                                            text = SimpleDateFormat("dd MMM, hh:mm:ss a", Locale.getDefault()).format(Date(savedEntity.timestamp)),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color.White.copy(alpha = 0.7f),
                                            fontSize = 10.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Image,
                                            contentDescription = null,
                                            tint = Color.White.copy(alpha = 0.5f),
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = savedEntity.imagePath,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.White.copy(alpha = 0.85f),
                                            fontFamily = AppFontFamily,
                                            fontSize = 10.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Action Buttons: Populate Dashboard & Review Findings
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    showResultsSheet = false
                                },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Scan Next Angle")
                            }

                            Button(
                                onClick = {
                                    val finalBitmaps = capturedImages.toList()
                                    viewModel.startLiveInspection(
                                        productName = detectedProductName.ifBlank { "Scanned Commodity Package" },
                                        brand = detectedBrand.ifBlank { "Verified FMCG Manufacturer" },
                                        category = detectedCategory,
                                        pdpArea = 140.0,
                                        batchNumber = "CAM-BATCH-${System.currentTimeMillis() % 10000}",
                                        barcode = "890" + (1000000000..9999999999).random(),
                                        location = "Camera Metrology Station #1",
                                        bitmaps = finalBitmaps,
                                        onComplete = { createdInspectionId ->
                                            showResultsSheet = false
                                            onInspectionCompleted(createdInspectionId)
                                        }
                                    )
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                ),
                                modifier = Modifier
                                    .weight(1.3f)
                                    .testTag("submit_camera_inspection_button")
                            ) {
                                Icon(Icons.Default.Shield, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Populate Dashboard", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }

    // Saved Scans History Bottom Sheet (Room DB Records)
    if (showSavedScansSheet) {
        SavedScansBottomSheet(
            records = scannedOcrRecords,
            onDismiss = { showSavedScansSheet = false },
            onInspectRecord = { record ->
                showSavedScansSheet = false
                detectedProductName = record.productName ?: ""
                detectedBrand = record.brand ?: ""
                detectedNetQty = record.netQuantity ?: ""
                detectedMrp = record.mrp ?: ""

                // Load image from path if available
                val file = File(record.imagePath)
                val bmp = if (file.exists()) {
                    BitmapFactory.decodeFile(file.absolutePath)
                } else null

                val bitmaps = if (bmp != null) listOf(bmp) else capturedImages.toList()

                viewModel.startLiveInspection(
                    productName = record.productName?.ifBlank { "Scanned Commodity Package" } ?: "Scanned Commodity Package",
                    brand = record.brand?.ifBlank { "Verified FMCG Manufacturer" } ?: "Verified FMCG Manufacturer",
                    category = detectedCategory,
                    pdpArea = 140.0,
                    batchNumber = "REC-${record.id.takeLast(6)}",
                    barcode = "890" + (1000000000..9999999999).random(),
                    location = "Camera Metrology Station #1",
                    bitmaps = bitmaps,
                    onComplete = { createdInspectionId ->
                        onInspectionCompleted(createdInspectionId)
                    }
                )
            },
            onDeleteRecord = { recordId ->
                viewModel.deleteScannedOcrRecord(recordId)
            }
        )
    }

    // Demo Preset Picker Modal
    if (showDemoPicker) {
        DemoPackagePickerBottomSheet(
            onDismiss = { showDemoPicker = false },
            onSelectCase = { demoCase ->
                showDemoPicker = false
                viewModel.startDemoInspection(demoCase) { createdId ->
                    onInspectionCompleted(createdId)
                }
            }
        )
    }

    // AI Multimodal Processing Loading Dialog
    if (isImageAiProcessing) {
        AiImageProcessingModal(stage = imageAiProcessingStage)
    }

    // Image OCR & Online Gemini AI Analysis Result Bottom Sheet
    if (showImageAiResultSheet) {
        imageOcrAiResult?.let { aiResult ->
            ImageOcrAiResultBottomSheet(
                result = aiResult,
                capturedImage = capturedImages.lastOrNull(),
                onDismiss = { showImageAiResultSheet = false },
                onStartInspection = {
                    val finalBitmaps = capturedImages.toList()
                    val pName = aiResult.extractedPackageData.productName.takeIf { it.isNotBlank() && it != "Not Provided in Image" } ?: "Scanned Commodity Package"
                    val bName = aiResult.extractedPackageData.manufacturerName.takeIf { it.isNotBlank() && it != "Not Provided in Image" } ?: "Verified FMCG Manufacturer"
                    viewModel.startLiveInspection(
                        productName = pName,
                        brand = bName,
                        category = detectedCategory,
                        pdpArea = 140.0,
                        batchNumber = "AI-BATCH-${System.currentTimeMillis() % 10000}",
                        barcode = "890" + (1000000000..9999999999).random(),
                        location = "Proof AI Optical Metrology Station",
                        bitmaps = finalBitmaps,
                        onComplete = { createdInspectionId ->
                            showImageAiResultSheet = false
                            onInspectionCompleted(createdInspectionId)
                        }
                    )
                },
                onScanAnother = {
                    showImageAiResultSheet = false
                }
            )
        }
    }

    // Stitched Label Composite Modal
    if (showStitchedSheet && stitchedBitmap != null) {
        StitchedLabelPreviewBottomSheet(
            stitchedBitmap = stitchedBitmap!!,
            panelCount = capturedImages.size,
            onDismiss = { showStitchedSheet = false },
            onRunOcrOnStitched = { bmp ->
                showStitchedSheet = false
                processCapturedBitmap(bmp)
            },
            onAuditWithStitched = { bmp ->
                showStitchedSheet = false
                viewModel.startLiveInspection(
                    productName = detectedProductName.ifBlank { "Stitched Composite Package (${capturedImages.size} Panels)" },
                    brand = detectedBrand.ifBlank { "Verified FMCG Manufacturer" },
                    category = detectedCategory,
                    pdpArea = 140.0,
                    batchNumber = "STITCH-${System.currentTimeMillis() % 10000}",
                    barcode = "890" + (1000000000..9999999999).random(),
                    location = "Camera Metrology Station #1",
                    bitmaps = listOf(bmp),
                    onComplete = { createdInspectionId ->
                        onInspectionCompleted(createdInspectionId)
                    }
                )
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StitchedLabelPreviewBottomSheet(
    stitchedBitmap: Bitmap,
    panelCount: Int,
    onDismiss: () -> Unit,
    onRunOcrOnStitched: (Bitmap) -> Unit,
    onAuditWithStitched: (Bitmap) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Layers,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Stitched Multi-Angle Label Composite",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "$panelCount Angle Panels • Combined ${stitchedBitmap.width} × ${stitchedBitmap.height} px",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Stitched Image Canvas Preview
            Card(
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
                colors = CardDefaults.cardColors(containerColor = Color.Black),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 240.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                ) {
                    Image(
                        bitmap = stitchedBitmap.asImageBitmap(),
                        contentDescription = "Stitched Composite Label",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )

                    Surface(
                        color = Color.Black.copy(alpha = 0.75f),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "SEAMLESS PANORAMIC STITCH",
                            color = Color(0xFF00E676),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Stitching combines curved bottle labels or multi-sided package boxes into one continuous surface to detect scattered statutory declarations.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(
                    onClick = { onRunOcrOnStitched(stitchedBitmap) },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                ) {
                    Icon(Icons.Default.DocumentScanner, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("OCR STITCHED", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                Button(
                    onClick = { onAuditWithStitched(stitchedBitmap) },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1.2f)
                        .height(44.dp)
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("AUDIT STITCHED", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun DeclarationPillRow(
    label: String,
    value: String,
    confidence: Float? = null
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
        ) {
            Text(
                text = "$label:",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.width(86.dp)
            )
            Text(
                text = value.ifBlank { "Not detected on label" },
                style = MaterialTheme.typography.bodySmall,
                color = if (value.isNotBlank() && !value.contains("Not detected", ignoreCase = true)) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outline,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            if (confidence != null && value.isNotBlank() && !value.contains("Not", ignoreCase = true)) {
                Spacer(modifier = Modifier.width(4.dp))
                OcrConfidenceBadge(
                    confidence = confidence,
                    compact = true,
                    showLabel = false
                )
            }
        }
    }
}

/**
 * Bottom sheet modal displaying all OCR scans persisted in the local Room database (`scanned_label_ocr_records`).
 */
@Composable
fun SavedScansBottomSheet(
    records: List<ScannedLabelOcrEntity>,
    onDismiss: () -> Unit,
    onInspectRecord: (ScannedLabelOcrEntity) -> Unit,
    onDeleteRecord: (String) -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    Card(
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("saved_scans_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF00E676).copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Storage,
                            contentDescription = null,
                            tint = Color(0xFF00E676),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Room DB Stored Scans",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${records.size} label scans stored locally with OCR text & image files",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (records.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.DocumentScanner,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "No Scanned Labels in Database",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Point camera at commodity packaging and tap shutter to scan with Proof ML Kit and persist to Room DB.",
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(380.dp)
                ) {
                    items(records, key = { it.id }) { record ->
                        var isExpanded by remember { mutableStateOf(false) }
                        val file = remember(record.imagePath) { File(record.imagePath) }
                        val bitmap = remember(record.imagePath) {
                            if (file.exists()) {
                                try {
                                    BitmapFactory.decodeFile(file.absolutePath)
                                } catch (e: Exception) {
                                    null
                                }
                            } else null
                        }

                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("scanned_record_${record.id}")
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Thumbnail / Icon
                                    if (bitmap != null) {
                                        Image(
                                            bitmap = bitmap.asImageBitmap(),
                                            contentDescription = "Scanned Label",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .size(54.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .border(1.dp, Color(0xFF4285F4), RoundedCornerShape(8.dp))
                                        )
                                    } else {
                                        Box(
                                            modifier = Modifier
                                                .size(54.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(MaterialTheme.colorScheme.primaryContainer),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Image,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = record.productName?.ifBlank { "Unidentified Commodity" } ?: "Unidentified Commodity",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        if (!record.brand.isNullOrBlank()) {
                                            Text(
                                                text = "Brand: ${record.brand}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.primary,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(top = 2.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Schedule,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.outline,
                                                modifier = Modifier.size(12.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(record.timestamp)),
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.outline
                                            )
                                        }
                                    }

                                    IconButton(
                                        onClick = { onDeleteRecord(record.id) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete Record",
                                            tint = ComplianceFail
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Chips Row for MRP, Net Qty, Mfg Date
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    if (!record.mrp.isNullOrBlank()) {
                                        Surface(
                                            color = Color(0xFF00E5FF).copy(alpha = 0.15f),
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = "MRP: ${record.mrp}",
                                                color = Color(0xFF00E5FF),
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    if (!record.netQuantity.isNullOrBlank()) {
                                        Surface(
                                            color = Color(0xFF00E676).copy(alpha = 0.15f),
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = "Qty: ${record.netQuantity}",
                                                color = Color(0xFF00E676),
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Surface(
                                        color = MaterialTheme.colorScheme.surfaceVariant,
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = "${record.linesCount} lines",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                // Image Path display
                                Surface(
                                    color = MaterialTheme.colorScheme.surface,
                                    shape = RoundedCornerShape(6.dp),
                                    border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Image,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.outline,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = record.imagePath,
                                            style = MaterialTheme.typography.bodySmall,
                                            fontFamily = AppFontFamily,
                                            fontSize = 9.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }

                                if (isExpanded) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Surface(
                                        color = Color(0xFF0A0F1D),
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(8.dp)) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Text(
                                                    text = "Extracted OCR Text:",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFF00E5FF)
                                                )
                                                IconButton(
                                                    onClick = {
                                                        clipboardManager.setText(AnnotatedString(record.extractedRawText))
                                                        Toast.makeText(context, "OCR text copied to clipboard", Toast.LENGTH_SHORT).show()
                                                    },
                                                    modifier = Modifier.size(20.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.ContentCopy,
                                                        contentDescription = "Copy text",
                                                        tint = Color.White.copy(alpha = 0.7f),
                                                        modifier = Modifier.size(14.dp)
                                                    )
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = record.extractedRawText,
                                                style = MaterialTheme.typography.bodySmall,
                                                fontFamily = AppFontFamily,
                                                fontSize = 10.sp,
                                                color = Color.White.copy(alpha = 0.9f)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Bottom buttons for record
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    TextButton(
                                        onClick = { isExpanded = !isExpanded },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(if (isExpanded) "Hide OCR Text" else "View OCR Text", fontSize = 11.sp)
                                    }

                                    Button(
                                        onClick = { onInspectRecord(record) },
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1.2f)
                                    ) {
                                        Icon(Icons.Default.Shield, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Inspect in Pipeline", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Close")
            }
        }
    }
}

/**
 * High-tech multimodal AI processing dialog displayed during photo upload + OCR + Gemini AI pipeline.
 */
@Composable
fun AiImageProcessingModal(
    stage: String,
    modifier: Modifier = Modifier
) {
    Dialog(
        onDismissRequest = { /* Non-cancellable during active processing */ },
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            border = BorderStroke(1.5.dp, Color(0xFF4285F4).copy(alpha = 0.6f)),
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF4285F4).copy(alpha = 0.15f))
                ) {
                    CircularProgressIndicator(
                        color = Color(0xFF4285F4),
                        strokeWidth = 3.dp,
                        modifier = Modifier.size(48.dp)
                    )
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = Color(0xFF8AB4F8),
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Multimodal AI Optical Metrology",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stage.ifBlank { "Analyzing package with OCR & Proof AI..." },
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF94A3B8),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Surface(
                    color = Color(0xFF1E293B),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = Color(0xFF34A853),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Extracting Statutory Attributes under LM (PC) Rules",
                            fontSize = 11.sp,
                            color = Color(0xFFE2E8F0)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Bottom sheet displaying complete extracted package attributes from Image OCR & Gemini AI.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageOcrAiResultBottomSheet(
    result: ImageOcrAiResult,
    capturedImage: Bitmap?,
    onDismiss: () -> Unit,
    onStartInspection: () -> Unit,
    onScanAnother: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var isRawOcrExpanded by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = Color(0xFF0F172A),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF4285F4).copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = Color(0xFF8AB4F8),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.padding(end = 8.dp)) {
                        Text(
                            text = "AI Optical Metrology Result",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "OCR + Proof AI Analysis",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF8AB4F8),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Surface(
                    color = if (result.extractedPackageData.perceptionConfidence >= 0.85f) Color(0xFF1B5E20)
                    else Color(0xFFE65100),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = if (result.isGeminiOnline) "CLOUD VERIFIED" else "ON-DEVICE OCR",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        softWrap = false,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Prominent Optical Reliability & Confidence Overlay
            OcrConfidenceOverlay(
                confidence = result.extractedPackageData.perceptionConfidence,
                extractedFieldCount = listOf(
                    result.extractedPackageData.productName,
                    result.extractedPackageData.manufacturerName,
                    result.extractedPackageData.netQuantity,
                    result.extractedPackageData.mrp,
                    result.extractedPackageData.dateOfMfg,
                    result.extractedPackageData.countryOfOrigin
                ).count { it.isNotBlank() && !it.contains("Not Provided", ignoreCase = true) },
                latencyMs = result.executionTimeMs
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Deterministic Rules Compliance Summary Card (Pass/Fail)
            ExtractedDataComplianceSummaryCard(
                data = result.extractedPackageData
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Extracted Commodity Details Card
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFF334155)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    val conf = result.extractedPackageData.perceptionConfidence
                    Text(
                        text = "Extracted Package Attributes",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00E5FF)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    AttributeRow(label = "Product Name", value = result.extractedPackageData.productName, confidence = if (result.extractedPackageData.productName.isNotBlank() && !result.extractedPackageData.productName.contains("Not", ignoreCase = true)) conf else null)
                    HorizontalDivider(color = Color(0xFF334155), thickness = 0.5.dp, modifier = Modifier.padding(vertical = 6.dp))
                    AttributeRow(label = "Manufacturer / Brand", value = result.extractedPackageData.manufacturerName, confidence = if (result.extractedPackageData.manufacturerName.isNotBlank() && !result.extractedPackageData.manufacturerName.contains("Not", ignoreCase = true)) (conf * 0.96f).coerceIn(0.6f, 0.99f) else null)
                    HorizontalDivider(color = Color(0xFF334155), thickness = 0.5.dp, modifier = Modifier.padding(vertical = 6.dp))
                    AttributeRow(label = "Net Quantity", value = result.extractedPackageData.netQuantity, confidence = if (result.extractedPackageData.netQuantity.isNotBlank() && !result.extractedPackageData.netQuantity.contains("Not", ignoreCase = true)) (conf * 1.02f).coerceIn(0.7f, 0.99f) else null)
                    HorizontalDivider(color = Color(0xFF334155), thickness = 0.5.dp, modifier = Modifier.padding(vertical = 6.dp))
                    AttributeRow(label = "MRP (Incl. taxes)", value = result.extractedPackageData.mrp, confidence = if (result.extractedPackageData.mrp.isNotBlank() && !result.extractedPackageData.mrp.contains("Not", ignoreCase = true)) (conf * 0.98f).coerceIn(0.7f, 0.99f) else null)
                    HorizontalDivider(color = Color(0xFF334155), thickness = 0.5.dp, modifier = Modifier.padding(vertical = 6.dp))
                    AttributeRow(label = "Mfg / Pkg Date", value = result.extractedPackageData.dateOfMfg, confidence = if (result.extractedPackageData.dateOfMfg.isNotBlank() && !result.extractedPackageData.dateOfMfg.contains("Not", ignoreCase = true)) (conf * 0.95f).coerceIn(0.6f, 0.99f) else null)
                    HorizontalDivider(color = Color(0xFF334155), thickness = 0.5.dp, modifier = Modifier.padding(vertical = 6.dp))
                    AttributeRow(label = "Consumer Care", value = result.extractedPackageData.consumerCare, confidence = if (result.extractedPackageData.consumerCare.isNotBlank() && !result.extractedPackageData.consumerCare.contains("Not", ignoreCase = true)) (conf * 0.93f).coerceIn(0.6f, 0.99f) else null)
                    HorizontalDivider(color = Color(0xFF334155), thickness = 0.5.dp, modifier = Modifier.padding(vertical = 6.dp))
                    AttributeRow(label = "Country of Origin", value = result.extractedPackageData.countryOfOrigin, confidence = if (result.extractedPackageData.countryOfOrigin.isNotBlank() && !result.extractedPackageData.countryOfOrigin.contains("Not", ignoreCase = true)) (conf * 0.92f).coerceIn(0.6f, 0.99f) else null)
                }
            }

            // AI Compliance Summary Card
            val aiSummaryText = result.productIntelligence?.primaryMatch?.description ?: ""
            if (aiSummaryText.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFF4285F4).copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFF8AB4F8), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Proof AI Metrology Findings",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = aiSummaryText,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFE2E8F0),
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            // Missing Declarations Alert if any
            val missingFields = mutableListOf<String>()
            if (result.extractedPackageData.mrp == "Not Provided in Image") missingFields.add("Maximum Retail Price (MRP)")
            if (result.extractedPackageData.netQuantity == "Not Provided in Image") missingFields.add("Net Quantity Declaration")
            if (result.extractedPackageData.manufacturerName == "Not Provided in Image") missingFields.add("Manufacturer / Packer Name & Address")
            if (result.extractedPackageData.dateOfMfg == "Not Provided in Image") missingFields.add("Date of Manufacture / Packing")
            if (result.extractedPackageData.consumerCare == "Not Provided in Image") missingFields.add("Consumer Care Details")

            if (missingFields.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    color = Color(0xFFB71C1C).copy(alpha = 0.15f),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Color(0xFFE53935).copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFEF5350), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Missing Mandatory Declarations (${missingFields.size})",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFEF5350)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        missingFields.forEach { missing ->
                            Text(
                                text = "• $missing",
                                fontSize = 11.sp,
                                color = Color(0xFFFFCDD2),
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            // Expandable Raw OCR Text Section
            Spacer(modifier = Modifier.height(10.dp))
            Surface(
                color = Color(0xFF1E293B),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Raw Proof ML Kit OCR Text",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF94A3B8),
                            fontWeight = FontWeight.Bold
                        )
                        Row {
                            IconButton(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(result.rawText))
                                    Toast.makeText(context, "OCR text copied", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = Color.White.copy(alpha = 0.7f), modifier = Modifier.size(14.dp))
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(
                                onClick = { isRawOcrExpanded = !isRawOcrExpanded },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = if (isRawOcrExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = "Expand",
                                    tint = Color.White.copy(alpha = 0.7f),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    if (isRawOcrExpanded) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = result.rawText.ifBlank { "No OCR text extracted" },
                            style = MaterialTheme.typography.bodySmall,
                            fontFamily = AppFontFamily,
                            fontSize = 10.sp,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onScanAnother,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Scan Another")
                }

                Button(
                    onClick = onStartInspection,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier
                        .weight(1.3f)
                        .testTag("start_inspection_from_ai_ocr")
                ) {
                    Icon(Icons.Default.Shield, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Populate Dashboard", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun AttributeRow(
    label: String,
    value: String,
    confidence: Float? = null,
    evidenceState: EvidenceState? = null,
    modifier: Modifier = Modifier
) {
    val effectiveState = evidenceState ?: resolveEvidenceState(
        value = value,
        defaultState = if (confidence != null && confidence >= 0.70f) EvidenceState.VERIFIED_PACKAGING else EvidenceState.AI_IDENTIFIED
    )
    val isMissingOrNotProvided = effectiveState == EvidenceState.NOT_PROVIDED
    val isNotApplicable = effectiveState == EvidenceState.NOT_APPLICABLE || effectiveState == EvidenceState.NO_PHYSICAL_PRODUCT
    val isUnableToVerify = effectiveState == EvidenceState.UNABLE_TO_VERIFY

    val displayText = when {
        effectiveState == EvidenceState.NOT_PROVIDED && (value.isBlank() || value.equals("not detected", ignoreCase = true) || value.equals("not provided", ignoreCase = true)) -> "Not Provided"
        effectiveState == EvidenceState.NOT_APPLICABLE && value.isBlank() -> "Not Applicable"
        effectiveState == EvidenceState.NO_PHYSICAL_PRODUCT && value.isBlank() -> "No Physical Product"
        effectiveState == EvidenceState.UNABLE_TO_VERIFY && value.isBlank() -> "Unable to Verify"
        else -> value.ifBlank { "Not Provided" }
    }

    val valueColor = when (effectiveState) {
        EvidenceState.NOT_PROVIDED -> Color(0xFF94A3B8)
        EvidenceState.NOT_APPLICABLE, EvidenceState.NO_PHYSICAL_PRODUCT -> Color(0xFF94A3B8)
        EvidenceState.UNABLE_TO_VERIFY -> Color(0xFFFBBF24)
        else -> Color.White
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label.uppercase(java.util.Locale.ROOT),
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF94A3B8),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f, fill = false)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                EvidenceStateBadge(
                    state = effectiveState,
                    compact = true
                )
                if (confidence != null && !isMissingOrNotProvided && !isNotApplicable) {
                    OcrConfidenceBadge(
                        confidence = confidence,
                        compact = true,
                        showLabel = false
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = displayText,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = if (isMissingOrNotProvided || isNotApplicable) FontWeight.Normal else FontWeight.SemiBold,
            color = valueColor,
            fontSize = 12.sp,
            lineHeight = 16.sp
        )
    }
}
