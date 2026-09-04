package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PhoneInTalk
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import com.example.ui.theme.AppFontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.BoundingBox

/**
 * Represents the live detection status of a mandatory Rule 6 statutory field in the camera stream.
 */
data class LiveStatutoryFieldStatus(
    val key: String,
    val displayName: String,
    val icon: ImageVector,
    val isDetected: Boolean,
    val extractedValue: String = "",
    val confidence: Float = 0.9f,
    val ruleReference: String = "Rule 6(1)"
)

/**
 * LiveOcrDetectionOverlay provides high-contrast, real-time live OCR visual feedback:
 * 1. Live bounding boxes scaled directly over the camera feed.
 * 2. Real-time Detection Status Matrix showing what is DETECTED vs NOT DETECTED.
 * 3. Live compliance score calculator (e.g. 5/7 Statutory Declarations Found).
 * 4. Streaming text ticker and active laser sweep line.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LiveOcrDetectionOverlay(
    detectedBoxes: List<BoundingBox>,
    statutoryFields: List<LiveStatutoryFieldStatus>,
    rawTextPreview: String,
    detectedCount: Int,
    totalCount: Int,
    modifier: Modifier = Modifier,
    onFreezeAndInspect: () -> Unit = {}
) {
    var isExpandedMatrix by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "live_stream_sweep")
    val sweepPosition by infiniteTransition.animateFloat(
        initialValue = 0.05f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sweep_pos"
    )

    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(750, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .testTag("live_ocr_detection_overlay")
    ) {
        val screenWidth = maxWidth.value
        val screenHeight = maxHeight.value

        // 1. Live Bounding Box Canvas Layer
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasW = size.width
            val canvasH = size.height

            // Sweep laser beam across viewfinder
            val laserY = canvasH * sweepPosition
            drawLine(
                brush = Brush.horizontalGradient(
                    listOf(
                        Color.Transparent,
                        Color(0xFF00E5FF).copy(alpha = 0.4f),
                        Color(0xFF00E676),
                        Color.White,
                        Color(0xFF00E676),
                        Color(0xFF00E5FF).copy(alpha = 0.4f),
                        Color.Transparent
                    )
                ),
                start = Offset(0f, laserY),
                end = Offset(canvasW, laserY),
                strokeWidth = 2.dp.toPx(),
                cap = StrokeCap.Round
            )

            // Render live bounding boxes from ML Kit stream
            detectedBoxes.forEach { box ->
                val x = box.x * canvasW
                val y = box.y * canvasH
                val w = (box.width * canvasW).coerceAtLeast(20.dp.toPx())
                val h = (box.height * canvasH).coerceAtLeast(16.dp.toPx())

                val boxColor = when {
                    box.fieldKey.contains("MRP", ignoreCase = true) || box.fieldKey.contains("PRICE", ignoreCase = true) -> Color(0xFF00E676)
                    box.fieldKey.contains("QUANTITY", ignoreCase = true) || box.fieldKey.contains("NET", ignoreCase = true) -> Color(0xFF00E5FF)
                    box.fieldKey.contains("DATE", ignoreCase = true) || box.fieldKey.contains("MFG", ignoreCase = true) -> Color(0xFFFFB300)
                    box.fieldKey.contains("MANUFACTURER", ignoreCase = true) || box.fieldKey.contains("BRAND", ignoreCase = true) -> Color(0xFF818CF8)
                    box.fieldKey.contains("CARE", ignoreCase = true) || box.fieldKey.contains("PHONE", ignoreCase = true) -> Color(0xFFE879F9)
                    else -> Color(0xFF38BDF8)
                }

                // Box border
                drawRoundRect(
                    color = boxColor.copy(alpha = pulseAlpha),
                    topLeft = Offset(x, y),
                    size = Size(w, h),
                    cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx()),
                    style = Stroke(
                        width = 1.8.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 6f), 0f)
                    )
                )

                // Fill highlight
                drawRoundRect(
                    color = boxColor.copy(alpha = 0.08f),
                    topLeft = Offset(x, y),
                    size = Size(w, h),
                    cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                )

                // Corner brackets
                val cornerLen = 8.dp.toPx()
                drawLine(boxColor, Offset(x, y), Offset(x + cornerLen, y), strokeWidth = 3.dp.toPx())
                drawLine(boxColor, Offset(x, y), Offset(x, y + cornerLen), strokeWidth = 3.dp.toPx())
                drawLine(boxColor, Offset(x + w, y + h), Offset(x + w - cornerLen, y + h), strokeWidth = 3.dp.toPx())
                drawLine(boxColor, Offset(x + w, y + h), Offset(x + w, y + h - cornerLen), strokeWidth = 3.dp.toPx())
            }
        }

        // 2. Minimal Center Pill indicator
        Surface(
            color = Color.Black.copy(alpha = 0.5f),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 140.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.DocumentScanner,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                val complianceRatio = if (totalCount > 0) detectedCount.toFloat() / totalCount else 0f
                val statusText = if (complianceRatio >= 0.7f) "Ready to capture" else "Scanning product..."
                Text(
                    text = statusText,
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
