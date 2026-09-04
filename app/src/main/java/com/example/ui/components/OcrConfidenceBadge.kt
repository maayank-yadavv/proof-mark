package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import com.example.ui.theme.AppFontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ComplianceFail
import com.example.ui.theme.CompliancePass
import com.example.ui.theme.ComplianceReview

enum class OcrReliabilityLevel(
    val label: String,
    val shortLabel: String,
    val primaryColor: Color,
    val containerColor: Color,
    val description: String
) {
    HIGH(
        label = "High Reliability",
        shortLabel = "HIGH",
        primaryColor = Color(0xFF00E676),
        containerColor = Color(0xFF00E676).copy(alpha = 0.15f),
        description = "High optical clarity with distinct character edges. Extracted text is highly accurate and legally admissible."
    ),
    MODERATE(
        label = "Adequate",
        shortLabel = "ADEQUATE",
        primaryColor = Color(0xFFFFB300),
        containerColor = Color(0xFFFFB300).copy(alpha = 0.15f),
        description = "Moderate character certainty. Reviewing against the physical package label is recommended."
    ),
    LOW(
        label = "Low Reliability",
        shortLabel = "LOW / REVIEW",
        primaryColor = Color(0xFFFF5252),
        containerColor = Color(0xFFFF5252).copy(alpha = 0.15f),
        description = "Low optical contrast, glare, or motion blur detected. Manual verification required."
    )
}

fun getOcrReliabilityLevel(confidence: Float): OcrReliabilityLevel {
    return when {
        confidence >= 0.85f -> OcrReliabilityLevel.HIGH
        confidence >= 0.65f -> OcrReliabilityLevel.MODERATE
        else -> OcrReliabilityLevel.LOW
    }
}

/**
 * Compact pill badge displaying an OCR confidence score alongside extracted text.
 */
@Composable
fun OcrConfidenceBadge(
    confidence: Float,
    modifier: Modifier = Modifier,
    showLabel: Boolean = true,
    compact: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    val level = getOcrReliabilityLevel(confidence)
    val percentage = (confidence.coerceIn(0f, 1f) * 100).toInt()

    val clickableModifier = if (onClick != null) {
        modifier.clickable { onClick() }
    } else modifier

    Surface(
        color = level.containerColor,
        shape = RoundedCornerShape(if (compact) 4.dp else 6.dp),
        border = BorderStroke(0.8.dp, level.primaryColor.copy(alpha = 0.6f)),
        modifier = clickableModifier.testTag("ocr_confidence_badge")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(
                horizontal = if (compact) 5.dp else 7.dp,
                vertical = if (compact) 2.dp else 3.dp
            )
        ) {
            Box(
                modifier = Modifier
                    .size(if (compact) 5.dp else 6.dp)
                    .background(level.primaryColor, CircleShape)
            )
            Spacer(modifier = Modifier.width(if (compact) 4.dp else 5.dp))
            Text(
                text = if (compact || !showLabel) "$percentage%" else "$percentage% ${level.shortLabel}",
                color = level.primaryColor,
                fontSize = if (compact) 9.sp else 10.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = AppFontFamily
            )
        }
    }
}

/**
 * Comprehensive Confidence Score Overlay banner informing the user about OCR reliability,
 * character clarity, and statutory verification certainty.
 */
@Composable
fun OcrConfidenceOverlay(
    confidence: Float,
    modifier: Modifier = Modifier,
    extractedFieldCount: Int? = null,
    latencyMs: Long? = null,
    onInfoClick: (() -> Unit)? = null
) {
    var showDialog by remember { mutableStateOf(false) }
    val level = getOcrReliabilityLevel(confidence)
    val percentage = (confidence.coerceIn(0f, 1f) * 100).toInt()

    val animatedProgress by animateFloatAsState(
        targetValue = confidence.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "confidence_progress"
    )

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0F172A)
        ),
        border = BorderStroke(1.dp, level.primaryColor.copy(alpha = 0.45f)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("ocr_confidence_overlay")
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = when (level) {
                            OcrReliabilityLevel.HIGH -> Icons.Default.Verified
                            OcrReliabilityLevel.MODERATE -> Icons.Default.Info
                            OcrReliabilityLevel.LOW -> Icons.Default.Warning
                        },
                        contentDescription = null,
                        tint = level.primaryColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "OCR RELIABILITY SCORE",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White.copy(alpha = 0.7f),
                            letterSpacing = 0.5.sp,
                            fontSize = 9.sp
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "$percentage% Confidence",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = level.containerColor,
                                shape = RoundedCornerShape(4.dp),
                                border = BorderStroke(0.5.dp, level.primaryColor.copy(alpha = 0.7f))
                            ) {
                                Text(
                                    text = level.label.uppercase(),
                                    color = level.primaryColor,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                )
                            }
                        }
                    }
                }

                TextButton(
                    onClick = {
                        if (onInfoClick != null) onInfoClick() else showDialog = true
                    },
                    modifier = Modifier.height(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.HelpOutline,
                        contentDescription = "Reliability Breakdown",
                        tint = Color.White.copy(alpha = 0.75f),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Details",
                        fontSize = 10.sp,
                        color = Color.White.copy(alpha = 0.85f),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Progress Bar Gauge
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = level.primaryColor,
                trackColor = Color.White.copy(alpha = 0.12f),
                strokeCap = StrokeCap.Round
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Sub-metrics and Anti-Hallucination Assurance
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = level.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 11.sp,
                    modifier = Modifier.weight(1f)
                )

                if (latencyMs != null || extractedFieldCount != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.background(Color.White.copy(alpha = 0.06f), RoundedCornerShape(4.dp)).padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        if (extractedFieldCount != null) {
                            Text(
                                text = "$extractedFieldCount Fields",
                                fontSize = 10.sp,
                                color = Color.White.copy(alpha = 0.7f),
                                fontWeight = FontWeight.Medium
                            )
                        }
                        if (extractedFieldCount != null && latencyMs != null) {
                            Text(" • ", fontSize = 10.sp, color = Color.White.copy(alpha = 0.4f))
                        }
                        if (latencyMs != null) {
                            Text(
                                text = "${latencyMs}ms",
                                fontSize = 10.sp,
                                color = Color(0xFF00E5FF),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        OcrConfidenceExplanationDialog(
            confidence = confidence,
            onDismiss = { showDialog = false }
        )
    }
}

/**
 * Educational breakdown dialog detailing OCR reliability criteria under Legal Metrology standards.
 */
@Composable
fun OcrConfidenceExplanationDialog(
    confidence: Float,
    onDismiss: () -> Unit
) {
    val level = getOcrReliabilityLevel(confidence)
    val percentage = (confidence.coerceIn(0f, 1f) * 100).toInt()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Speed,
                    contentDescription = null,
                    tint = level.primaryColor
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("OCR Confidence & Reliability")
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Surface(
                    color = level.containerColor,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, level.primaryColor.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "Score: $percentage% — ${level.label}",
                            fontWeight = FontWeight.Bold,
                            color = level.primaryColor,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = level.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Text(
                    text = "How Proof Mark measures optical certainty:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )

                ConfidenceFactorRow(
                    title = "Character Edge Sharpness",
                    desc = "Evaluates gradient contrast along letter boundaries to rule out motion blur or out-of-focus camera angles."
                )
                ConfidenceFactorRow(
                    title = "Bounding Box Geometry",
                    desc = "Validates height/width aspect ratios against Legal Metrology Rule 6 minimum font dimension mandates."
                )
                ConfidenceFactorRow(
                    title = "Statutory Syntax Match",
                    desc = "Cross-references detected numbers and units against strict Legal Metrology dictionaries (e.g. 'g', 'kg', 'ml', '₹')."
                )
                ConfidenceFactorRow(
                    title = "Zero-Hallucination Policy",
                    desc = "Missing or unreadable text is strictly flagged as 'Not Detected' rather than estimated by AI."
                )
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Got It")
            }
        }
    )
}

@Composable
private fun ConfidenceFactorRow(title: String, desc: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .padding(top = 4.dp)
                .size(6.dp)
                .background(MaterialTheme.colorScheme.primary, CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = desc,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp
            )
        }
    }
}
