package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.BoundingBox
import com.example.data.models.ComplianceStatus
import com.example.ui.theme.ComplianceFail
import com.example.ui.theme.CompliancePass
import com.example.ui.theme.ComplianceReview

@Composable
fun InteractiveBoundingBoxViewer(
    boxes: List<BoundingBox>,
    selectedBoxKey: String?,
    onBoxSelected: (BoundingBox) -> Unit,
    placeholderColorHex: String = "#1E293B",
    modifier: Modifier = Modifier
) {
    val bgColor = try {
        Color(android.graphics.Color.parseColor(placeholderColorHex))
    } catch (e: Exception) {
        Color(0xFF1E293B)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(280.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            .testTag("bounding_box_viewer")
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(boxes) {
                    detectTapGestures { tapOffset ->
                        val canvasW = size.width
                        val canvasH = size.height

                        // Find touched box
                        val hit = boxes.lastOrNull { b ->
                            val left = b.x * canvasW
                            val top = b.y * canvasH
                            val right = left + (b.width * canvasW)
                            val bottom = top + (b.height * canvasH)

                            tapOffset.x in left..right && tapOffset.y in top..bottom
                        }
                        if (hit != null) {
                            onBoxSelected(hit)
                        }
                    }
                }
        ) {
            val w = size.width
            val h = size.height

            // Draw technical crosshairs grid in background
            drawPackageVisualGrid(w, h)

            // Draw bounding boxes
            boxes.forEach { box ->
                val isSelected = box.fieldKey.equals(selectedBoxKey, ignoreCase = true)
                val strokeColor = when (box.status) {
                    ComplianceStatus.PASS -> CompliancePass
                    ComplianceStatus.POTENTIAL_NON_COMPLIANCE -> ComplianceFail
                    ComplianceStatus.REQUIRES_REVIEW -> ComplianceReview
                    ComplianceStatus.DRAFT -> Color.LightGray
                }

                val rectLeft = box.x * w
                val rectTop = box.y * h
                val rectWidth = box.width * w
                val rectHeight = box.height * h

                // Semi-transparent highlight fill
                drawRoundRect(
                    color = strokeColor.copy(alpha = if (isSelected) 0.35f else 0.12f),
                    topLeft = Offset(rectLeft, rectTop),
                    size = Size(rectWidth, rectHeight),
                    cornerRadius = CornerRadius(4f, 4f),
                    style = Fill
                )

                // Outline
                drawRoundRect(
                    color = if (isSelected) Color.White else strokeColor,
                    topLeft = Offset(rectLeft, rectTop),
                    size = Size(rectWidth, rectHeight),
                    cornerRadius = CornerRadius(4f, 4f),
                    style = Stroke(
                        width = if (isSelected) 6f else 3f,
                        pathEffect = if (!isSelected) PathEffect.dashPathEffect(floatArrayOf(12f, 6f), 0f) else null
                    )
                )
            }
        }

        // Overlay status header for active box
        if (selectedBoxKey != null) {
            val activeBox = boxes.find { it.fieldKey.equals(selectedBoxKey, ignoreCase = true) }
            if (activeBox != null) {
                Surface(
                    color = Color.Black.copy(alpha = 0.8f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(
                                    when (activeBox.status) {
                                        ComplianceStatus.PASS -> CompliancePass
                                        ComplianceStatus.POTENTIAL_NON_COMPLIANCE -> ComplianceFail
                                        ComplianceStatus.REQUIRES_REVIEW -> ComplianceReview
                                        ComplianceStatus.DRAFT -> Color.Gray
                                    },
                                    RoundedCornerShape(4.dp)
                                )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${activeBox.fieldKey}: \"${activeBox.text.take(32)}\" (${(activeBox.confidence * 100).toInt()}%)",
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

private fun DrawScope.drawPackageVisualGrid(w: Float, h: Float) {
    val gridColor = Color.White.copy(alpha = 0.05f)
    val step = 40f
    var x = 0f
    while (x < w) {
        drawLine(gridColor, Offset(x, 0f), Offset(x, h), 1f)
        x += step
    }
    var y = 0f
    while (y < h) {
        drawLine(gridColor, Offset(0f, y), Offset(w, y), 1f)
        y += step
    }
}
