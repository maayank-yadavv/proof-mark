@file:OptIn(
    androidx.compose.material3.ExperimentalMaterial3Api::class,
    androidx.camera.core.ExperimentalGetImage::class
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.ai.ExtractedPackageData
import com.example.data.ai.MLKitTextRecognitionService
import com.example.data.ai.MlKitOcrResult
import com.example.data.local.entities.ScannedLabelOcrEntity
import com.example.data.models.ProductCategory
import com.example.ui.components.ProofMarkLogoBadge
import com.example.ui.components.ShimmerImagePreviewSkeleton
import com.example.ui.components.ShimmerTextExtractionSkeleton
import com.example.ui.screens.dashboard.DemoPackagePickerBottomSheet
import com.example.ui.theme.ComplianceFail
import com.example.ui.theme.CompliancePass
import com.example.ui.theme.ComplianceReview
import com.example.ui.viewmodel.InspectionViewModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.max
import kotlin.math.min

/**
 * Statutory declaration categories identified in real-time by ML Kit on commodity labels.
 */
enum class StatutoryFieldType(
    val displayName: String,
    val shortBadge: String,
    val colorHex: Long,
    val bgHex: Long
) {
    MRP("MRP / Price", "MRP", 0xFFFFB74D, 0x33FFB74D), // Amber / Gold
    NET_QTY("Net Quantity", "QTY", 0xFF00E676, 0x3300E676), // Emerald Green
    MFG_DATE("Mfg / Date", "DATE", 0xFF00E5FF, 0x3300E5FF), // Cyan / Sky Blue
    MANUFACTURER("Manufacturer", "MFD", 0xFFBA68C8, 0x33BA68C8), // Purple
    CONSUMER_CARE("Customer Care", "CARE", 0xFFFF7043, 0x33FF7043), // Vivid Orange
    COUNTRY_ORIGIN("Country of Origin", "ORIGIN", 0xFF26A69A, 0x3326A69A), // Teal
    GENERAL("Statutory Text", "TXT", 0xFF4285F4, 0x264285F4) // Google Blue
}

/**
 * Data holder for real-time bounding boxes detected by ML Kit on camera frames.
 */
data class RealtimeDetectedBox(
    val normalizedRect: RectF, // 0..1 in frame coordinates
    val text: String,
    val fieldType: StatutoryFieldType,
    val lineIndex: Int = 0
)

/**
 * Real-time blur status categories for live camera frame quality assessment.
 */
enum class BlurStatus(
    val label: String,
    val shortTag: String,
    val colorHex: Long,
    val bgHex: Long,
    val isBlurry: Boolean
) {
    SHARP("Sharp Focus", "SHARP", 0xFF00E676, 0x3300E676, false),
    ACCEPTABLE("Good Focus", "GOOD", 0xFF00E5FF, 0x3300E5FF, false),
    SLIGHT_BLUR("Slight Blur", "HOLD STEADY", 0xFFFFB74D, 0x33FFB74D, true),
    BLURRY("Blurry Image", "BLURRY", 0xFFFF5252, 0x33FF5252, true)
}

/**
 * Data holder for real-time blur and image quality analysis metrics.
 */
data class RealtimeBlurMetrics(
    val blurScore: Float,
    val status: BlurStatus,
    val isLowLight: Boolean = false
)

/**
 * Computes high-speed spatial gradient variance on the Y (luminance) plane of CameraX ImageProxy
 * to detect motion blur or camera defocus in real time before image capture.
 */
fun computeRealtimeBlurMetrics(imageProxy: ImageProxy): RealtimeBlurMetrics {
    return try {
        val plane = imageProxy.planes.getOrNull(0) ?: return RealtimeBlurMetrics(120f, BlurStatus.ACCEPTABLE)
        val buffer = plane.buffer.duplicate()
        val width = imageProxy.width
        val height = imageProxy.height
        val rowStride = plane.rowStride
        val pixelStride = plane.pixelStride

        if (width < 32 || height < 32) return RealtimeBlurMetrics(120f, BlurStatus.ACCEPTABLE)

        val startX = width / 4
        val endX = (width * 3) / 4
        val startY = height / 4
        val endY = (height * 3) / 4
        val step = 8

        var totalVarianceSum = 0.0
        var totalLuminanceSum = 0L
        var count = 0

        for (y in startY until endY - step step step) {
            val rowOffset1 = y * rowStride
            val rowOffset2 = (y + step) * rowStride
            for (x in startX until endX - step step step) {
                val idx = rowOffset1 + x * pixelStride
                val idxRight = rowOffset1 + (x + step) * pixelStride
                val idxDown = rowOffset2 + x * pixelStride

                if (idx < buffer.capacity() && idxRight < buffer.capacity() && idxDown < buffer.capacity()) {
                    val p = buffer.get(idx).toInt() and 0xFF
                    val pRight = buffer.get(idxRight).toInt() and 0xFF
                    val pDown = buffer.get(idxDown).toInt() and 0xFF

                    totalLuminanceSum += p
                    val gx = (pRight - p).toDouble()
                    val gy = (pDown - p).toDouble()
                    totalVarianceSum += (gx * gx + gy * gy)
                    count++
                }
            }
        }

        if (count == 0) return RealtimeBlurMetrics(120f, BlurStatus.ACCEPTABLE)

        val avgGradSq = (totalVarianceSum / count).toFloat()
        val avgLuminance = (totalLuminanceSum / count).toFloat()
        val isLowLight = avgLuminance < 35f

        val status = when {
            avgGradSq >= 130f -> BlurStatus.SHARP
            avgGradSq >= 70f -> BlurStatus.ACCEPTABLE
            avgGradSq >= 35f -> BlurStatus.SLIGHT_BLUR
            else -> BlurStatus.BLURRY
        }

        RealtimeBlurMetrics(
            blurScore = avgGradSq,
            status = status,
            isLowLight = isLowLight
        )
    } catch (e: Exception) {
        RealtimeBlurMetrics(120f, BlurStatus.ACCEPTABLE)
    }
}

/**
 * High-performance CameraScreen powered by CameraX and Google ML Kit Text Recognition.
 * Highlights detected text bounding boxes in real-time directly on the camera preview
 * before the final image is captured.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalGetImage::class)
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
    var isTorchOn by remember { mutableStateOf(false) }
    var isBackCamera by remember { mutableStateOf(true) }
    var zoomRatio by remember { mutableFloatStateOf(1f) }
    var isCapturing by remember { mutableStateOf(false) }
    var showDemoPicker by remember { mutableStateOf(false) }
    var isRealtimeOverlayEnabled by remember { mutableStateOf(true) }
    var showLegendSheet by remember { mutableStateOf(false) }

    // Real-time Bounding Boxes, Blur Metrics & Live Frame Dimensions
    var realtimeBoxes by remember { mutableStateOf<List<RealtimeDetectedBox>>(emptyList()) }
    var realtimeBlurMetrics by remember { mutableStateOf(RealtimeBlurMetrics(120f, BlurStatus.ACCEPTABLE)) }
    var showBlurWarningDialog by remember { mutableStateOf(false) }
    var liveFrameWidth by remember { mutableFloatStateOf(1080f) }
    var liveFrameHeight by remember { mutableFloatStateOf(1920f) }
    var liveDetectedLinesCount by remember { mutableIntStateOf(0) }
    var liveDetectedFields by remember { mutableStateOf<Set<StatutoryFieldType>>(emptySet()) }

    // CameraX Controller with both Capture and Real-time Image Analysis
    val cameraController = remember {
        LifecycleCameraController(context).apply {
            setEnabledUseCases(CameraController.IMAGE_CAPTURE or CameraController.IMAGE_ANALYSIS)
            imageAnalysisBackpressureStrategy = ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
            cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        }
    }

    // Set up real-time ML Kit Analyzer
    LaunchedEffect(isRealtimeOverlayEnabled) {
        if (isRealtimeOverlayEnabled) {
            val mainExecutor = ContextCompat.getMainExecutor(context)
            cameraController.setImageAnalysisAnalyzer(mainExecutor) { imageProxy ->
                // Continuous Real-Time Frame Quality & Blur Analysis
                val blurMetrics = computeRealtimeBlurMetrics(imageProxy)
                realtimeBlurMetrics = blurMetrics

                val mediaImage = imageProxy.image
                if (mediaImage != null) {
                    val rotationDegrees = imageProxy.imageInfo.rotationDegrees
                    val inputImage = InputImage.fromMediaImage(mediaImage, rotationDegrees)

                    val isRotated = (rotationDegrees == 90 || rotationDegrees == 270)
                    val orientedWidth = if (isRotated) imageProxy.height.toFloat() else imageProxy.width.toFloat()
                    val orientedHeight = if (isRotated) imageProxy.width.toFloat() else imageProxy.height.toFloat()

                    liveFrameWidth = orientedWidth.coerceAtLeast(1f)
                    liveFrameHeight = orientedHeight.coerceAtLeast(1f)

                    realtimeTextRecognizer.process(inputImage)
                        .addOnSuccessListener { visionText ->
                            val detectedList = mutableListOf<RealtimeDetectedBox>()
                            val activeFields = mutableSetOf<StatutoryFieldType>()
                            var lineCounter = 0

                            for (block in visionText.textBlocks) {
                                for (line in block.lines) {
                                    val rect = line.boundingBox
                                    if (rect != null && orientedWidth > 0 && orientedHeight > 0) {
                                        val normLeft = (rect.left.toFloat() / orientedWidth).coerceIn(0f, 1f)
                                        val normTop = (rect.top.toFloat() / orientedHeight).coerceIn(0f, 1f)
                                        val normRight = (rect.right.toFloat() / orientedWidth).coerceIn(0f, 1f)
                                        val normBottom = (rect.bottom.toFloat() / orientedHeight).coerceIn(0f, 1f)

                                        val fieldType = classifyStatutorySnippet(line.text)
                                        if (fieldType != StatutoryFieldType.GENERAL) {
                                            activeFields.add(fieldType)
                                        }

                                        detectedList.add(
                                            RealtimeDetectedBox(
                                                normalizedRect = RectF(normLeft, normTop, normRight, normBottom),
                                                text = line.text,
                                                fieldType = fieldType,
                                                lineIndex = lineCounter++
                                            )
                                        )
                                    }
                                }
                            }
                            realtimeBoxes = detectedList
                            liveDetectedLinesCount = detectedList.size
                            liveDetectedFields = activeFields
                        }
                        .addOnFailureListener {
                            // Non-fatal, analyzer will retry on next frame
                        }
                        .addOnCompleteListener {
                            imageProxy.close()
                        }
                } else {
                    imageProxy.close()
                }
            }
        } else {
            cameraController.clearImageAnalysisAnalyzer()
            realtimeBoxes = emptyList()
            liveDetectedLinesCount = 0
            liveDetectedFields = emptySet()
        }
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
    var showSavedScansSheet by remember { mutableStateOf(false) }
    var latestSavedOcrRecord by remember { mutableStateOf<ScannedLabelOcrEntity?>(null) }

    // Captured Bitmaps & Final OCR State
    val capturedImages = remember { mutableStateListOf<Bitmap>() }
    var latestOcrResult by remember { mutableStateOf<MlKitOcrResult?>(null) }
    var isOcrProcessing by remember { mutableStateOf(false) }
    var extractedData by remember { mutableStateOf<ExtractedPackageData?>(null) }
    var showResultsSheet by remember { mutableStateOf(false) }

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
        statusMessage = "Processing label with Google ML Kit Text Recognition..."
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
                statusMessage = "ML Kit extracted ${ocrResult.totalLinesCount} text lines in ${ocrResult.executionTimeMs}ms"

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
                processCapturedBitmap(bmp)
            } catch (e: Exception) {
                Toast.makeText(context, "Failed to load image: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Camera Capture Function with Real-Time Blur Detection Pre-Check
    fun capturePhoto(ignoreBlurWarning: Boolean = false) {
        if (isCapturing) return
        if (!ignoreBlurWarning && realtimeBlurMetrics.status.isBlurry) {
            showBlurWarningDialog = true
            return
        }
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
                        processCapturedBitmap(correctedBitmap)
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
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Live Label Scanner",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                color = if (isRealtimeOverlayEnabled) Color(0xFF00E676).copy(alpha = 0.25f) else Color.White.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(4.dp),
                                border = BorderStroke(1.dp, if (isRealtimeOverlayEnabled) Color(0xFF00E676) else Color.White.copy(alpha = 0.4f))
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(if (isRealtimeOverlayEnabled) Color(0xFF00E676) else Color.Gray)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isRealtimeOverlayEnabled) "LIVE ML OCR" else "OCR PAUSED",
                                        color = if (isRealtimeOverlayEnabled) Color(0xFF00E676) else Color.White,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                        Text(
                            text = if (liveDetectedLinesCount > 0) "$liveDetectedLinesCount text boxes tracking in real-time" else "CameraX Real-Time Text Tracking",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.75f)
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
                                tint = if (scannedOcrRecords.isNotEmpty()) Color(0xFF00E676) else Color.White
                            )
                        }
                    }

                    // Toggle Real-Time Bounding Box Overlay
                    IconButton(
                        onClick = { isRealtimeOverlayEnabled = !isRealtimeOverlayEnabled },
                        modifier = Modifier.testTag("toggle_realtime_overlay_button")
                    ) {
                        Icon(
                            imageVector = if (isRealtimeOverlayEnabled) Icons.Default.Layers else Icons.Default.LayersClear,
                            contentDescription = "Toggle Real-Time Overlay",
                            tint = if (isRealtimeOverlayEnabled) Color(0xFF00E5FF) else Color.White.copy(alpha = 0.5f)
                        )
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
                            tint = if (isTorchOn) Color(0xFFFFD54F) else Color.White
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
                            tint = Color.White
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

                // 2. REAL-TIME BOUNDING BOX OVERLAY CANVAS
                if (isRealtimeOverlayEnabled && realtimeBoxes.isNotEmpty()) {
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("realtime_bounding_box_canvas")
                    ) {
                        drawRealtimeBoundingBoxes(
                            boxes = realtimeBoxes,
                            frameWidth = liveFrameWidth,
                            frameHeight = liveFrameHeight
                        )
                    }
                }

                // 3. Viewfinder Reticle & Laser Sweep
                BoxWithConstraints(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    val frameWidth = maxWidth * 0.86f
                    val frameHeight = maxHeight * 0.52f

                    // Viewfinder Guidelines with Dynamic Blur Status Color Border
                    Box(
                        modifier = Modifier
                            .size(width = frameWidth, height = frameHeight)
                            .border(
                                width = 2.5.dp,
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        Color(realtimeBlurMetrics.status.colorHex),
                                        Color(realtimeBlurMetrics.status.colorHex).copy(alpha = 0.6f),
                                        if (realtimeBlurMetrics.status.isBlurry) Color(0xFFFF5252) else Color(0xFF00E676)
                                    )
                                ),
                                shape = RoundedCornerShape(16.dp)
                            )
                    ) {
                        // Animated Scanning Laser Line
                        if (isRealtimeOverlayEnabled) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(2.dp)
                                    .offset(y = frameHeight * laserProgress)
                                    .background(
                                        Brush.horizontalGradient(
                                            colors = listOf(
                                                Color.Transparent,
                                                Color(0xFF00E5FF),
                                                Color(0xFF00E676),
                                                Color(0xFF00E5FF),
                                                Color.Transparent
                                            )
                                        )
                                    )
                            )
                        }

                        // Corner Guidelines Banner
                        Surface(
                            color = Color.Black.copy(alpha = 0.65f),
                            shape = RoundedCornerShape(bottomEnd = 8.dp),
                            modifier = Modifier.align(Alignment.TopStart)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CropFree,
                                    contentDescription = null,
                                    tint = Color(0xFF00E5FF),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Principal Display Panel (PDP)",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Bottom Guideline
                        Surface(
                            color = Color.Black.copy(alpha = 0.65f),
                            shape = RoundedCornerShape(topStart = 8.dp),
                            modifier = Modifier.align(Alignment.BottomEnd)
                        ) {
                            Text(
                                text = "Rule 6 / Rule 9 Declarations",
                                color = Color(0xFFE2E8F0),
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }

                // 4. Live Real-Time Statutory Detection Badges HUD (Top)
                Column(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .fillMaxWidth()
                        .padding(top = 10.dp, start = 12.dp, end = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Top Guidance Hint Banner
                    Surface(
                        color = Color.Black.copy(alpha = 0.75f),
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(0.5.dp, Color.White.copy(alpha = 0.2f))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = if (isRealtimeOverlayEnabled) Icons.Default.AutoAwesome else Icons.Default.DocumentScanner,
                                contentDescription = null,
                                tint = if (isRealtimeOverlayEnabled) Color(0xFF00E5FF) else Color(0xFF4285F4),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isOcrProcessing) "Running High-Precision OCR..." else if (liveDetectedLinesCount > 0) "Tracking $liveDetectedLinesCount statutory text areas in real-time" else "Align commodity label & tap shutter",
                                color = Color.White,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // Live Real-Time Blur & Quality Assessment Pill
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        color = Color(realtimeBlurMetrics.status.bgHex),
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.dp, Color(realtimeBlurMetrics.status.colorHex))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Color(realtimeBlurMetrics.status.colorHex))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = if (realtimeBlurMetrics.status.isBlurry) Icons.Default.Warning else Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(realtimeBlurMetrics.status.colorHex),
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "FOCUS: ${realtimeBlurMetrics.status.label.uppercase()} (${realtimeBlurMetrics.blurScore.toInt()})",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (realtimeBlurMetrics.isLowLight) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "• LOW LIGHT (USE FLASH)",
                                    color = Color(0xFFFFD54F),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Live Detected Statutory Badges Row
                    if (liveDetectedFields.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(liveDetectedFields.toList()) { field ->
                                Surface(
                                    color = Color(field.bgHex),
                                    shape = RoundedCornerShape(6.dp),
                                    border = BorderStroke(1.dp, Color(field.colorHex))
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .clip(CircleShape)
                                                .background(Color(field.colorHex))
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "${field.shortBadge} DETECTED",
                                            color = Color.White,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 5. Controls & Shutter Bottom Bar
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
                    // Zoom Slider & Realtime Box Legend Button
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TextButton(
                            onClick = { showLegendSheet = !showLegendSheet },
                            modifier = Modifier.height(28.dp)
                        ) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Box Color Key", color = Color(0xFF00E5FF), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        // Zoom Slider
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.width(180.dp)
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
                                    .padding(horizontal = 6.dp)
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
                                            // Delete Angle X Badge
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
                                .background(Color.White.copy(alpha = 0.25f))
                                .clickable(enabled = !isCapturing && !isOcrProcessing) { capturePhoto() }
                                .testTag("camera_shutter_button")
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(if (isCapturing || isOcrProcessing) Color(0xFF4285F4) else Color.White)
                            ) {
                                if (isCapturing || isOcrProcessing) {
                                    CircularProgressIndicator(
                                        color = Color.White,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(12.dp),
                                        strokeWidth = 3.dp
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.CameraAlt,
                                        contentDescription = "Capture Label",
                                        tint = MaterialTheme.colorScheme.primary,
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
                        text = "Under Legal Metrology Rules, ProofMark uses CameraX optical scanning to inspect physical commodity packaging and verify Principal Display Panel declarations with Google ML Kit.",
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

            // Live Camera Blur Warning Alert Dialog
            if (showBlurWarningDialog) {
                AlertDialog(
                    onDismissRequest = { showBlurWarningDialog = false },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = Color(0xFFFF5252),
                            modifier = Modifier.size(32.dp)
                        )
                    },
                    title = {
                        Text(
                            text = "Camera Frame Blur Detected",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "The live camera preview indicates potential motion blur or defocus (Sharpness score: ${realtimeBlurMetrics.blurScore.toInt()}).",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Surface(
                                color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "💡 Tip: Hold the camera steady 15-20cm from the package label and tap the preview to focus before capturing.",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                showBlurWarningDialog = false
                                capturePhoto(ignoreBlurWarning = true)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252))
                        ) {
                            Text("Capture Anyway")
                        }
                    },
                    dismissButton = {
                        OutlinedButton(
                            onClick = {
                                showBlurWarningDialog = false
                            }
                        ) {
                            Text("Refocus & Hold Steady")
                        }
                    }
                )
            }

            // Real-Time Color Legend Card Modal
            AnimatedVisibility(
                visible = showLegendSheet,
                enter = fadeIn() + slideInVertically { it / 2 },
                exit = fadeOut() + slideOutVertically { it / 2 },
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(24.dp)
            ) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Real-Time Statutory Highlights Key",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            IconButton(
                                onClick = { showLegendSheet = false },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                            }
                        }

                        StatutoryFieldType.entries.forEach { type ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(14.dp)
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(Color(type.colorHex))
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = type.displayName,
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                Text(
                                    text = "[${type.shortBadge}]",
                                    color = Color(type.colorHex),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
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
                                    text = "Google ML Kit OCR Extraction",
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
                                    .padding(vertical = 8.dp),
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

                        // Extracted Statutory Declarations Summary
                        extractedData?.let { data ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                DeclarationPillRow("Generic Name", data.productName)
                                DeclarationPillRow("Manufacturer", data.manufacturerName)
                                DeclarationPillRow("Address", data.manufacturerAddress)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Box(modifier = Modifier.weight(1f)) {
                                        DeclarationPillRow("Net Qty", data.netQuantity)
                                    }
                                    Box(modifier = Modifier.weight(1f)) {
                                        DeclarationPillRow("MRP", data.mrp)
                                    }
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Box(modifier = Modifier.weight(1f)) {
                                        DeclarationPillRow("Mfg Date", data.dateOfMfg)
                                    }
                                    Box(modifier = Modifier.weight(1f)) {
                                        DeclarationPillRow("Origin", data.countryOfOrigin)
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
                                            fontFamily = FontFamily.Monospace,
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
}

/**
 * Draws high-precision real-time bounding boxes and HUD brackets on the Compose Canvas.
 */
private fun DrawScope.drawRealtimeBoundingBoxes(
    boxes: List<RealtimeDetectedBox>,
    frameWidth: Float,
    frameHeight: Float
) {
    val canvasW = size.width
    val canvasH = size.height

    val imgW = frameWidth.coerceAtLeast(1f)
    val imgH = frameHeight.coerceAtLeast(1f)

    val imgAspect = imgW / imgH
    val canvasAspect = canvasW / canvasH

    // PreviewView uses FILL_CENTER mapping
    val scale = if (canvasAspect > imgAspect) {
        canvasW / imgW
    } else {
        canvasH / imgH
    }

    val offsetX = (canvasW - imgW * scale) / 2f
    val offsetY = (canvasH - imgH * scale) / 2f

    for (item in boxes) {
        val rect = item.normalizedRect
        val left = rect.left * imgW * scale + offsetX
        val top = rect.top * imgH * scale + offsetY
        val right = rect.right * imgW * scale + offsetX
        val bottom = rect.bottom * imgH * scale + offsetY

        val width = (right - left).coerceAtLeast(6f)
        val height = (bottom - top).coerceAtLeast(6f)

        val boxColor = Color(item.fieldType.colorHex)
        val bgColor = Color(item.fieldType.bgHex)

        // 1. Draw Translucent Background Fill
        drawRoundRect(
            color = bgColor,
            topLeft = Offset(left, top),
            size = Size(width, height),
            cornerRadius = CornerRadius(4f, 4f)
        )

        // 2. Draw Bounding Stroke
        drawRoundRect(
            color = boxColor.copy(alpha = 0.85f),
            topLeft = Offset(left, top),
            size = Size(width, height),
            cornerRadius = CornerRadius(4f, 4f),
            style = Stroke(width = if (item.fieldType != StatutoryFieldType.GENERAL) 2.5f else 1.2f)
        )

        // 3. Draw Corner HUD Brackets for high-tech aesthetic
        val bracketLen = min(width, height) * 0.35f
        val bracketColor = if (item.fieldType != StatutoryFieldType.GENERAL) boxColor else Color.White

        // Top-Left
        drawLine(color = bracketColor, start = Offset(left, top), end = Offset(left + bracketLen, top), strokeWidth = 3f)
        drawLine(color = bracketColor, start = Offset(left, top), end = Offset(left, top + bracketLen), strokeWidth = 3f)

        // Top-Right
        drawLine(color = bracketColor, start = Offset(right, top), end = Offset(right - bracketLen, top), strokeWidth = 3f)
        drawLine(color = bracketColor, start = Offset(right, top), end = Offset(right, top + bracketLen), strokeWidth = 3f)

        // Bottom-Left
        drawLine(color = bracketColor, start = Offset(left, bottom), end = Offset(left + bracketLen, bottom), strokeWidth = 3f)
        drawLine(color = bracketColor, start = Offset(left, bottom), end = Offset(left, bottom - bracketLen), strokeWidth = 3f)

        // Bottom-Right
        drawLine(color = bracketColor, start = Offset(right, bottom), end = Offset(right - bracketLen, bottom), strokeWidth = 3f)
        drawLine(color = bracketColor, start = Offset(right, bottom), end = Offset(right, bottom - bracketLen), strokeWidth = 3f)
    }
}

/**
 * Classifies text snippets into Legal Metrology statutory declaration categories.
 */
private fun classifyStatutorySnippet(text: String): StatutoryFieldType {
    val upper = text.uppercase()
    return when {
        upper.contains("MRP") || upper.contains("₹") || upper.contains("RS.") || upper.contains("INCL.") || upper.contains("MAX RETAIL") || upper.contains("UNIT SALE") || upper.contains("USP") -> StatutoryFieldType.MRP
        upper.contains("NET Q") || upper.contains("NET WT") || upper.contains("NET VOL") || upper.contains("WEIGHT") || upper.contains("VOLUME") || upper.matches(Regex(".*\\b\\d+(\\.\\d+)?\\s*(G|KG|ML|L|GM|GMS|LTR|PCS|N|UNITS)\\b.*")) -> StatutoryFieldType.NET_QTY
        upper.contains("MFD") || upper.contains("PKD") || upper.contains("DATE OF") || upper.contains("EXP") || upper.contains("USE BY") || upper.contains("BEST BEFORE") || upper.contains("BATCH") || upper.contains("LOT") -> StatutoryFieldType.MFG_DATE
        upper.contains("MFD BY") || upper.contains("PKD BY") || upper.contains("MANUFACTURED") || upper.contains("PACKED BY") || upper.contains("MARKETED BY") || upper.contains("PVT") || upper.contains("LTD") || upper.contains("LIMITED") || upper.contains("CORP") || upper.contains("INDUSTRIES") -> StatutoryFieldType.MANUFACTURER
        upper.contains("CARE") || upper.contains("FEEDBACK") || upper.contains("TOLL") || upper.contains("EMAIL") || upper.contains("CONSUMER") || upper.contains("COMPLAINT") || upper.contains("GRIEVANCE") || upper.contains("HELPLINE") -> StatutoryFieldType.CONSUMER_CARE
        upper.contains("ORIGIN") || upper.contains("MADE IN") || upper.contains("PRODUCT OF") || upper.contains("COUNTRY") || upper.contains("INDIA") -> StatutoryFieldType.COUNTRY_ORIGIN
        else -> StatutoryFieldType.GENERAL
    }
}

@Composable
private fun DeclarationPillRow(label: String, value: String) {
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
                modifier = Modifier.width(90.dp)
            )
            Text(
                text = value.ifBlank { "Not detected on label" },
                style = MaterialTheme.typography.bodySmall,
                color = if (value.isNotBlank()) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outline,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
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
                        text = "Point camera at commodity packaging and tap shutter to scan with ML Kit and persist to Room DB.",
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
                                            fontFamily = FontFamily.Monospace,
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
                                                fontFamily = FontFamily.Monospace,
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
