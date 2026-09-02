package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.data.models.NetworkConnectivityMode
import com.example.data.models.NetworkConnectivityState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.InspectionEntity
import com.example.data.models.BoundingBox
import com.example.data.models.ComplianceStatus
import com.example.data.models.QualityMetrics
import com.example.data.models.RuleSeverity
import com.example.ui.theme.ComplianceFail
import com.example.ui.theme.CompliancePass
import com.example.ui.theme.ComplianceReview
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Responsive Container: Centers content on tablets/foldables up to max 720.dp width
 * while taking full width on mobile screens.
 */
@Composable
fun ResponsiveContainer(
    modifier: Modifier = Modifier,
    maxWidth: Dp = 720.dp,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.TopCenter
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = maxWidth)
        ) {
            content()
        }
    }
}

/**
 * Animated Pulse Indicator Dot for live AI processing or status
 */
@Composable
fun AnimatedPulseDot(
    modifier: Modifier = Modifier,
    color: Color = Color(0xFF00E676),
    size: Dp = 10.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(color.copy(alpha = alpha))
    )
}

/**
 * High-polish Skeleton Shimmer Loading Card
 */
@Composable
fun ShimmerLoadingCard(
    modifier: Modifier = Modifier,
    height: Dp = 100.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translate"
    )

    val shimmerColors = listOf(
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(translateAnim - 200f, translateAnim - 200f),
        end = Offset(translateAnim, translateAnim)
    )

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        modifier = modifier
            .fillMaxWidth()
            .height(height)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brush)
        )
    }
}

/**
 * Shimmer-based skeleton loader for camera image preview thumbnails during capture/processing.
 */
@Composable
fun ShimmerImagePreviewSkeleton(
    modifier: Modifier = Modifier,
    size: Dp = 64.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer_img")
    val translateAnim by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_img_translate"
    )

    val shimmerColors = listOf(
        Color.DarkGray.copy(alpha = 0.6f),
        Color.Gray.copy(alpha = 0.3f),
        Color.DarkGray.copy(alpha = 0.6f)
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(translateAnim - 200f, translateAnim - 200f),
        end = Offset(translateAnim, translateAnim)
    )

    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(8.dp))
            .background(brush)
            .border(1.5.dp, Color(0xFF4285F4).copy(alpha = 0.6f), RoundedCornerShape(8.dp))
    )
}

/**
 * Shimmer-based skeleton loading state for text extraction results and statutory declaration parsing.
 */
@Composable
fun ShimmerTextExtractionSkeleton(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer_ocr")
    val translateAnim by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_ocr_translate"
    )

    val shimmerColors = listOf(
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(translateAnim - 200f, translateAnim - 200f),
        end = Offset(translateAnim, translateAnim)
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Header shimmer bar
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(brush)
            )
            Box(
                modifier = Modifier
                    .height(18.dp)
                    .weight(0.7f)
                    .clip(RoundedCornerShape(4.dp))
                    .background(brush)
            )
            Box(
                modifier = Modifier
                    .height(18.dp)
                    .weight(0.3f)
                    .clip(RoundedCornerShape(4.dp))
                    .background(brush)
            )
        }

        // Lines skeleton
        repeat(3) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .height(36.dp)
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(brush)
                )
                Box(
                    modifier = Modifier
                        .height(36.dp)
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(brush)
                )
            }
        }
    }
}

/**
 * Empty State Placeholder with Vector Icon, Title, Subtitle, and Optional Action
 */
@Composable
fun EmptyStateView(
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Default.Inbox,
    actionLabel: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
            shape = CircleShape,
            modifier = Modifier.size(72.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(36.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        if (actionLabel != null && onActionClick != null) {
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = onActionClick,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = actionLabel, fontWeight = FontWeight.Bold)
            }
        }
    }
}


@Composable
fun StatusBadge(
    status: ComplianceStatus,
    modifier: Modifier = Modifier,
    isSmall: Boolean = false
) {
    val (bgColor, textColor, icon) = when (status) {
        ComplianceStatus.PASS -> Triple(
            CompliancePass.copy(alpha = 0.15f),
            CompliancePass,
            Icons.Default.CheckCircle
        )
        ComplianceStatus.POTENTIAL_NON_COMPLIANCE -> Triple(
            ComplianceFail.copy(alpha = 0.15f),
            ComplianceFail,
            Icons.Default.Error
        )
        ComplianceStatus.REQUIRES_REVIEW -> Triple(
            ComplianceReview.copy(alpha = 0.15f),
            ComplianceReview,
            Icons.Default.Warning
        )
        ComplianceStatus.DRAFT -> Triple(
            Color.Gray.copy(alpha = 0.15f),
            Color.Gray,
            Icons.Default.Info
        )
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, textColor.copy(alpha = 0.4f)),
        modifier = modifier.testTag("status_badge_${status.name.lowercase()}")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(
                horizontal = if (isSmall) 8.dp else 10.dp,
                vertical = if (isSmall) 3.dp else 5.dp
            )
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(if (isSmall) 14.dp else 16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = status.displayName,
                color = textColor,
                style = if (isSmall) MaterialTheme.typography.labelSmall else MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun SeverityBadge(
    severity: RuleSeverity,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor) = when (severity) {
        RuleSeverity.CRITICAL -> Pair(ComplianceFail.copy(alpha = 0.15f), ComplianceFail)
        RuleSeverity.MAJOR -> Pair(ComplianceReview.copy(alpha = 0.15f), ComplianceReview)
        RuleSeverity.MINOR -> Pair(Color(0xFF38BDF8).copy(alpha = 0.15f), Color(0xFF38BDF8))
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(6.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, textColor.copy(alpha = 0.3f)),
        modifier = modifier
    ) {
        Text(
            text = severity.displayName.uppercase(),
            color = textColor,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
fun LegalDisclaimerNotice(
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
        ),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Shield,
                contentDescription = "Legal Protection",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Legal Metrology Enforcement Protocol",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "AI assists with perception & extraction. Deterministic rules engine evaluates compliance. Authorized enforcement officers make final legal determinations.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun QualityScoreCard(
    metrics: QualityMetrics,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Image Quality & Evidentiary Rating",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Surface(
                    color = if (metrics.isAcceptableForLegalEvidence) CompliancePass.copy(alpha = 0.15f) else ComplianceReview.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "${metrics.overallScore}/100 • ${metrics.readabilityRating}",
                        color = if (metrics.isAcceptableForLegalEvidence) CompliancePass else ComplianceReview,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                QualityMetricItem(label = "Sharpness", score = metrics.sharpnessScore, modifier = Modifier.weight(1f))
                QualityMetricItem(label = "Lighting", score = metrics.lightingScore, modifier = Modifier.weight(1f))
                QualityMetricItem(label = "Glare Control", score = metrics.glareScore, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun QualityMetricItem(
    label: String,
    score: Int,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(horizontal = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = "$score%", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { score / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = if (score >= 85) CompliancePass else if (score >= 70) ComplianceReview else ComplianceFail,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

@Composable
fun InspectionItemCard(
    inspection: InspectionEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    val formattedDate = dateFormat.format(Date(inspection.timestamp))

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("inspection_card_${inspection.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = inspection.inspectionNumber,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                StatusBadge(status = inspection.status, isSmall = true)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = inspection.productName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "Brand: ${inspection.brand} • ${inspection.category.label}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "Score: ${inspection.complianceScore}%",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    if (inspection.violationsCount > 0) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            color = ComplianceFail.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "${inspection.violationsCount} Violation${if (inspection.violationsCount > 1) "s" else ""}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = ComplianceFail,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Modern, clear, and decent ProofMark Brand Logo Badge Composable.
 * Renders the official Legal Metrology compliance shield with precision scanner beam,
 * optical calibration corner markers, and emerald verification glyph.
 */
@Composable
fun ProofMarkLogoBadge(
    modifier: Modifier = Modifier,
    size: androidx.compose.ui.unit.Dp = 48.dp,
    showAura: Boolean = true
) {
    Box(
        modifier = modifier
            .size(size)
            .testTag("proofmark_logo_badge"),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = this.size.width
            val h = this.size.height

            // 1. Ambient Glow Aura
            if (showAura) {
                drawCircle(
                    brush = androidx.compose.ui.graphics.Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF22C55E).copy(alpha = 0.30f),
                            Color(0xFF10B981).copy(alpha = 0.12f),
                            Color.Transparent
                        ),
                        center = Offset(w * 0.5f, h * 0.5f),
                        radius = w * 0.48f
                    )
                )
            }

            // 2. Corner Optical Calibration Brackets (Representing Metrology Vision)
            val strokeW = (w * 0.035f).coerceAtLeast(1.5f)
            val cornerLen = w * 0.14f
            val pad = w * 0.06f
            val bracketColor = Color(0xFF22C55E).copy(alpha = 0.85f)

            // Top-Left
            drawLine(bracketColor, Offset(pad, pad + cornerLen), Offset(pad, pad), strokeWidth = strokeW, cap = androidx.compose.ui.graphics.StrokeCap.Round)
            drawLine(bracketColor, Offset(pad, pad), Offset(pad + cornerLen, pad), strokeWidth = strokeW, cap = androidx.compose.ui.graphics.StrokeCap.Round)

            // Top-Right
            drawLine(bracketColor, Offset(w - pad - cornerLen, pad), Offset(w - pad, pad), strokeWidth = strokeW, cap = androidx.compose.ui.graphics.StrokeCap.Round)
            drawLine(bracketColor, Offset(w - pad, pad), Offset(w - pad, pad + cornerLen), strokeWidth = strokeW, cap = androidx.compose.ui.graphics.StrokeCap.Round)

            // Bottom-Left
            drawLine(bracketColor, Offset(pad, h - pad - cornerLen), Offset(pad, h - pad), strokeWidth = strokeW, cap = androidx.compose.ui.graphics.StrokeCap.Round)
            drawLine(bracketColor, Offset(pad, h - pad), Offset(pad + cornerLen, h - pad), strokeWidth = strokeW, cap = androidx.compose.ui.graphics.StrokeCap.Round)

            // Bottom-Right
            drawLine(bracketColor, Offset(w - pad - cornerLen, h - pad), Offset(w - pad, h - pad), strokeWidth = strokeW, cap = androidx.compose.ui.graphics.StrokeCap.Round)
            drawLine(bracketColor, Offset(w - pad, h - pad), Offset(w - pad, h - pad - cornerLen), strokeWidth = strokeW, cap = androidx.compose.ui.graphics.StrokeCap.Round)

            // 3. Outer Shield Path with Modern Light Green Gradient
            val outerShieldPath = androidx.compose.ui.graphics.Path().apply {
                moveTo(w * 0.5f, h * 0.18f)
                cubicTo(w * 0.72f, h * 0.18f, w * 0.82f, h * 0.24f, w * 0.82f, h * 0.38f)
                cubicTo(w * 0.82f, h * 0.62f, w * 0.5f, h * 0.84f, w * 0.5f, h * 0.84f)
                cubicTo(w * 0.5f, h * 0.84f, w * 0.18f, h * 0.62f, w * 0.18f, h * 0.38f)
                cubicTo(w * 0.18f, h * 0.24f, w * 0.28f, h * 0.18f, w * 0.5f, h * 0.18f)
                close()
            }

            drawPath(
                path = outerShieldPath,
                brush = androidx.compose.ui.graphics.Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF4ADE80),
                        Color(0xFF22C55E),
                        Color(0xFF10B981),
                        Color(0xFF059669)
                    ),
                    start = Offset(w * 0.2f, h * 0.18f),
                    end = Offset(w * 0.8f, h * 0.84f)
                )
            )

            // 4. Inner Shield Inset (High-Contrast Pitch AMOLED Black)
            val innerShieldPath = androidx.compose.ui.graphics.Path().apply {
                moveTo(w * 0.5f, h * 0.23f)
                cubicTo(w * 0.68f, h * 0.23f, w * 0.76f, h * 0.28f, w * 0.76f, h * 0.39f)
                cubicTo(w * 0.76f, h * 0.59f, w * 0.5f, h * 0.78f, w * 0.5f, h * 0.78f)
                cubicTo(w * 0.5f, h * 0.78f, w * 0.24f, h * 0.59f, w * 0.24f, h * 0.39f)
                cubicTo(w * 0.24f, h * 0.28f, w * 0.32f, h * 0.23f, w * 0.5f, h * 0.23f)
                close()
            }

            drawPath(
                path = innerShieldPath,
                color = Color(0xFF000000)
            )

            // 5. Horizontal Metrology Scanner Line
            drawLine(
                color = Color(0xFF4ADE80).copy(alpha = 0.60f),
                start = Offset(w * 0.32f, h * 0.48f),
                end = Offset(w * 0.68f, h * 0.48f),
                strokeWidth = (w * 0.025f).coerceAtLeast(1.5f),
                cap = androidx.compose.ui.graphics.StrokeCap.Round
            )

            // 6. Top Metrology Node (Golden Amber Pip)
            drawCircle(
                color = Color(0xFFFBBF24),
                radius = (w * 0.032f).coerceAtLeast(2f),
                center = Offset(w * 0.5f, h * 0.32f)
            )

            // 7. Bold Mint-to-Green Verification Checkmark
            val checkmarkPath = androidx.compose.ui.graphics.Path().apply {
                moveTo(w * 0.37f, h * 0.51f)
                lineTo(w * 0.47f, h * 0.61f)
                lineTo(w * 0.65f, h * 0.41f)
            }

            drawPath(
                path = checkmarkPath,
                brush = androidx.compose.ui.graphics.Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF86EFAC),
                        Color(0xFF22C55E),
                        Color(0xFF10B981)
                    ),
                    start = Offset(w * 0.37f, h * 0.61f),
                    end = Offset(w * 0.65f, h * 0.41f)
                ),
                style = Stroke(
                    width = (w * 0.072f).coerceAtLeast(3f),
                    cap = androidx.compose.ui.graphics.StrokeCap.Round,
                    join = androidx.compose.ui.graphics.StrokeJoin.Round
                )
            )
        }
    }
}

/**
 * Visual Top App Bar Connectivity Indicator showing real-time network status & remote API processing state.
 */
@Composable
fun NetworkConnectivityIndicator(
    networkState: NetworkConnectivityState,
    onToggleConnectivity: () -> Unit = {},
    onTriggerPing: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }

    val (dotColor, statusLabel, bgAlpha) = when {
        !networkState.isConnected -> Triple(
            Color(0xFFEF4444),
            "Offline",
            0.18f
        )
        networkState.status == com.example.data.models.ConnectivityStatus.SYNCING || networkState.isApiProcessing -> Triple(
            Color(0xFFF59E0B),
            if (networkState.pendingSyncCount > 0) "Syncing (${networkState.pendingSyncCount})..." else "Syncing...",
            0.20f
        )
        networkState.status == com.example.data.models.ConnectivityStatus.SYNCED -> Triple(
            Color(0xFF3B82F6),
            "Synced",
            0.20f
        )
        else -> Triple(
            Color(0xFF10B981),
            if (networkState.pendingSyncCount > 0) "Online (${networkState.pendingSyncCount} pending)" else "Online",
            0.18f
        )
    }

    Surface(
        color = dotColor.copy(alpha = bgAlpha),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, dotColor.copy(alpha = 0.45f)),
        modifier = modifier
            .clickable { showDialog = true }
            .testTag("top_bar_network_connectivity_indicator")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
        ) {
            if (networkState.status == com.example.data.models.ConnectivityStatus.SYNCING || networkState.isApiProcessing) {
                AnimatedPulseDot(
                    color = Color(0xFFF59E0B),
                    size = 8.dp
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(dotColor, CircleShape)
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = statusLabel,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = dotColor,
                fontSize = 11.sp
            )
            Spacer(modifier = Modifier.width(5.dp))
            Icon(
                imageVector = if (networkState.isConnected) Icons.Default.Wifi else Icons.Default.WifiOff,
                contentDescription = "Network Status",
                tint = dotColor,
                modifier = Modifier.size(13.dp)
            )
        }
    }

    if (showDialog) {
        NetworkStatusDetailDialog(
            networkState = networkState,
            onDismiss = { showDialog = false },
            onToggleMode = onToggleConnectivity,
            onTriggerPing = onTriggerPing
        )
    }
}

@Composable
fun NetworkStatusDetailDialog(
    networkState: NetworkConnectivityState,
    onDismiss: () -> Unit,
    onToggleMode: () -> Unit,
    onTriggerPing: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (networkState.isConnected) Icons.Default.CloudDone else Icons.Default.WifiOff,
                        contentDescription = null,
                        tint = if (networkState.isConnected) Color(0xFF10B981) else Color(0xFFEF4444),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Remote API Connectivity",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = networkState.mode.displayName,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Online status banner
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (networkState.isConnected) Color(0xFF10B981).copy(alpha = 0.1f) else Color(0xFFEF4444).copy(alpha = 0.1f)
                    ),
                    border = BorderStroke(1.dp, if (networkState.isConnected) Color(0xFF10B981).copy(alpha = 0.3f) else Color(0xFFEF4444).copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (networkState.isConnected) "LIVE CONNECTIVITY ACTIVE" else "OFFLINE MODE (LOCAL ENGINE)",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (networkState.isConnected) Color(0xFF047857) else Color(0xFFB91C1C)
                            )
                            Text(
                                text = if (networkState.isConnected) "Ping: ${networkState.pingMs}ms • Latency TLS 1.3" else "Using local Room DB & deterministic offline rules",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Switch(
                            checked = networkState.isConnected,
                            onCheckedChange = { onToggleMode() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFF10B981)
                            )
                        )
                    }
                }

                // API Gateway Status Details
                Surface(
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "CONNECTED COMPLIANCE ENDPOINTS",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        RemoteEndpointRow(
                            name = "National Legal Metrology Cloud API",
                            url = "api.metrology.gov.in/v2/rules",
                            status = if (networkState.isConnected) "ONLINE" else "DISCONNECTED",
                            isOnline = networkState.isConnected
                        )

                        RemoteEndpointRow(
                            name = "ONDC E-Commerce Benchmark Feed",
                            url = "ondc.compliance-gateway.org/live",
                            status = if (networkState.isConnected) "ONLINE" else "DISCONNECTED",
                            isOnline = networkState.isConnected
                        )

                        RemoteEndpointRow(
                            name = "Proof AI Vision Perception Engine",
                            url = "generativelanguage.googleapis.com",
                            status = if (networkState.isApiProcessing) "SYNCING..." else if (networkState.isConnected) "READY" else "OFFLINE",
                            isOnline = networkState.isConnected
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "mTLS 256-bit Encrypted",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    OutlinedButton(
                        onClick = onTriggerPing,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Test Ping", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Close")
            }
        }
    )
}

@Composable
private fun RemoteEndpointRow(
    name: String,
    url: String,
    status: String,
    isOnline: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = name, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
            Text(text = url, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
        }
        Surface(
            color = if (isOnline) Color(0xFF10B981).copy(alpha = 0.15f) else Color.Gray.copy(alpha = 0.15f),
            shape = RoundedCornerShape(4.dp)
        ) {
            Text(
                text = status,
                color = if (isOnline) Color(0xFF047857) else Color.Gray,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                fontSize = 9.sp,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
        }
    }
}

// ============================================================================
// CENTRALIZED DESIGN SYSTEM COMPONENTS
// ============================================================================

/**
 * Standardized High-Polish Primary App Button
 */
@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    icon: ImageVector? = null,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary
) {
    Button(
        onClick = onClick,
        enabled = enabled && !isLoading,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = containerColor.copy(alpha = 0.4f),
            disabledContentColor = contentColor.copy(alpha = 0.6f)
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
    ) {
        if (isLoading) {
            androidx.compose.material3.CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = contentColor,
                strokeWidth = 2.5.dp
            )
            Spacer(modifier = Modifier.width(8.dp))
        } else if (icon != null) {
            Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * Standardized Outlined App Button
 */
@Composable
fun AppOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null,
    borderColor: Color = MaterialTheme.colorScheme.outline
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, if (enabled) borderColor else borderColor.copy(alpha = 0.3f)),
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
    ) {
        if (icon != null) {
            Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

/**
 * Standardized Input Field with Soft Surface & Clear Focus State
 */
@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    leadingIcon: ImageVector? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    errorMessage: String? = null,
    visualTransformation: androidx.compose.ui.text.input.VisualTransformation = androidx.compose.ui.text.input.VisualTransformation.None,
    keyboardOptions: androidx.compose.foundation.text.KeyboardOptions = androidx.compose.foundation.text.KeyboardOptions.Default,
    keyboardActions: androidx.compose.foundation.text.KeyboardActions = androidx.compose.foundation.text.KeyboardActions.Default,
    singleLine: Boolean = true
) {
    Column(modifier = modifier.fillMaxWidth()) {
        androidx.compose.material3.OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            placeholder = if (placeholder.isNotEmpty()) { { Text(placeholder) } } else null,
            leadingIcon = if (leadingIcon != null) { { Icon(leadingIcon, contentDescription = null) } } else null,
            trailingIcon = trailingIcon,
            isError = isError,
            singleLine = singleLine,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            shape = RoundedCornerShape(12.dp),
            colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f),
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            ),
            modifier = Modifier.fillMaxWidth()
        )
        if (isError && !errorMessage.isNullOrEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}

/**
 * Standardized App Card Surface
 */
@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    borderColor: Color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
    content: @Composable ColumnScope.() -> Unit
) {
    if (onClick != null) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = containerColor),
            border = BorderStroke(1.dp, borderColor),
            modifier = modifier
                .fillMaxWidth()
                .clickable { onClick() }
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                content()
            }
        }
    } else {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = containerColor),
            border = BorderStroke(1.dp, borderColor),
            modifier = modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                content()
            }
        }
    }
}

/**
 * Modern Metric Card for Dashboards & Analytics
 */
@Composable
fun MetricCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    icon: ImageVector? = null,
    iconTint: Color = MaterialTheme.colorScheme.primary,
    accentBg: Color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
    onClick: (() -> Unit)? = null
) {
    AppCard(
        modifier = modifier,
        onClick = onClick,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (!subtitle.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            if (icon != null) {
                Surface(
                    color = accentBg,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(22.dp))
                    }
                }
            }
        }
    }
}

/**
 * Standardized Section Header Composable
 */
@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    actionLabel: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (!subtitle.isNullOrEmpty()) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        if (actionLabel != null && onActionClick != null) {
            androidx.compose.material3.TextButton(onClick = onActionClick) {
                Text(text = actionLabel, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

/**
 * Standardized Loading State Composable
 */
@Composable
fun LoadingState(
    message: String = "Loading compliance records...",
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        androidx.compose.material3.CircularProgressIndicator(
            color = MaterialTheme.colorScheme.primary,
            strokeWidth = 3.dp,
            modifier = Modifier.size(36.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Standardized Error State Box
 */
@Composable
fun ErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    AppCard(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f),
        borderColor = MaterialTheme.colorScheme.error.copy(alpha = 0.3f)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Error, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Authentication Error", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                Text(text = message, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onErrorContainer)
            }
            Spacer(modifier = Modifier.width(8.dp))
            AppOutlinedButton(
                text = "Retry",
                onClick = onRetry,
                borderColor = MaterialTheme.colorScheme.error,
                modifier = Modifier.width(90.dp).height(36.dp)
            )
        }
    }
}



