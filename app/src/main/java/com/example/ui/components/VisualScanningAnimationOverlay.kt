package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import com.example.ui.theme.AppFontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.cos
import kotlin.math.sin

/**
 * VisualScanningAnimationOverlay provides high-contrast, real-time visual feedback
 * during Proof ML Kit OCR processing of package labels, weight, volume, and statutory markings.
 *
 * Features:
 * 1. Ultra-smooth vertical laser sweep with luminous gradient aura and optical trailing glow.
 * 2. High-precision corner reticle HUD brackets with animated neon pulse.
 * 3. Matrix grid overlay with real-time target acquisition crosshairs.
 * 4. Dynamic simulated OCR detection target nodes highlighting label key regions.
 * 5. Audio-visual spectrum frequency indicator bars communicating active OCR throughput.
 * 6. Live HUD status pills showing detected weight, MRP, and confidence rating.
 */
@Composable
fun VisualScanningAnimationOverlay(
    isProcessing: Boolean,
    modifier: Modifier = Modifier,
    stageText: String = "Scanning Package Labels...",
    detectedWeightOrVolume: String? = null,
    detectedMrp: String? = null,
    confidenceScore: Float? = null,
    showGrid: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "scanning_anim_transition")

    // Primary laser sweep position (0.02f to 0.98f)
    val laserProgress by infiniteTransition.animateFloat(
        initialValue = 0.04f,
        targetValue = 0.96f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = if (isProcessing) 1200 else 2200,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laser_progress"
    )

    // Secondary harmonic laser line for depth and dual-wavelength effect
    val secondaryLaserProgress by infiniteTransition.animateFloat(
        initialValue = 0.15f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = if (isProcessing) 1800 else 2800,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "secondary_laser_progress"
    )

    // Glowing pulse for corners and reticle
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.40f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 750, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    // Radar rotation angle
    val radarRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "radar_rotation"
    )

    // Holographic particle phase shift
    val particlePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "particle_phase"
    )

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .testTag("visual_scanning_animation_overlay"),
        contentAlignment = Alignment.Center
    ) {
        val frameWidth = maxWidth * 0.88f
        val frameHeight = maxHeight * 0.54f

        // 1. Canvas Layer: Grid lines, targeting brackets, radar sweep, laser beam & holographic nodes
        Canvas(
            modifier = Modifier
                .size(width = frameWidth, height = frameHeight)
                .clip(RoundedCornerShape(16.dp))
        ) {
            val width = size.width
            val height = size.height
            val currentLaserY = height * laserProgress
            val secondaryLaserY = height * secondaryLaserProgress

            // 1a. Background subtle matrix grid
            if (showGrid) {
                val gridSpacing = 22.dp.toPx()
                val gridColor = if (isProcessing) {
                    Color(0xFF00E5FF).copy(alpha = 0.10f)
                } else {
                    Color.White.copy(alpha = 0.05f)
                }
                val strokeWidth = 0.8.dp.toPx()

                var x = gridSpacing
                while (x < width) {
                    drawLine(
                        color = gridColor,
                        start = Offset(x, 0f),
                        end = Offset(x, height),
                        strokeWidth = strokeWidth
                    )
                    x += gridSpacing
                }

                var y = gridSpacing
                while (y < height) {
                    drawLine(
                        color = gridColor,
                        start = Offset(0f, y),
                        end = Offset(width, y),
                        strokeWidth = strokeWidth
                    )
                    y += gridSpacing
                }
            }

            // 1b. Optical Scanning Zone Detection Nodes (Holographic live OCR targets)
            if (isProcessing) {
                val targetZones = listOf(
                    // MRP & Date Zone (Top Right)
                    Offset(width * 0.72f, height * 0.22f) to Size(width * 0.22f, height * 0.12f),
                    // Net Quantity & Measurement Zone (Center Left)
                    Offset(width * 0.08f, height * 0.48f) to Size(width * 0.35f, height * 0.14f),
                    // Manufacturer & Address Zone (Bottom Center)
                    Offset(width * 0.15f, height * 0.74f) to Size(width * 0.70f, height * 0.16f)
                )

                targetZones.forEachIndexed { index, (pos, boxSize) ->
                    val isLaserNearby = Math.abs(currentLaserY - (pos.y + boxSize.height / 2)) < (height * 0.18f)
                    val nodeAlpha = if (isLaserNearby) 0.85f * pulseAlpha else 0.25f
                    val nodeColor = if (isLaserNearby) Color(0xFF00E676) else Color(0xFF00E5FF)

                    // Dashed node bounding box
                    drawRoundRect(
                        color = nodeColor.copy(alpha = nodeAlpha),
                        topLeft = pos,
                        size = boxSize,
                        cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx()),
                        style = Stroke(
                            width = 1.2.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f), 0f)
                        )
                    )

                    // Corner tick marks for target node
                    val tickLen = 6.dp.toPx()
                    drawLine(
                        color = nodeColor.copy(alpha = nodeAlpha),
                        start = Offset(pos.x, pos.y),
                        end = Offset(pos.x + tickLen, pos.y),
                        strokeWidth = 2.dp.toPx()
                    )
                    drawLine(
                        color = nodeColor.copy(alpha = nodeAlpha),
                        start = Offset(pos.x, pos.y),
                        end = Offset(pos.x, pos.y + tickLen),
                        strokeWidth = 2.dp.toPx()
                    )
                }
            }

            // 1c. Primary Laser Beam Glow Aura (Trailing vertical gradient)
            val glowHeight = if (isProcessing) 56.dp.toPx() else 40.dp.toPx()
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        if (isProcessing) Color(0xFF00E5FF).copy(alpha = 0.22f) else Color(0xFF4285F4).copy(alpha = 0.12f),
                        if (isProcessing) Color(0xFF00E676).copy(alpha = 0.45f) else Color(0xFF00E5FF).copy(alpha = 0.25f)
                    ),
                    startY = (currentLaserY - glowHeight).coerceAtLeast(0f),
                    endY = currentLaserY
                ),
                topLeft = Offset(0f, (currentLaserY - glowHeight).coerceAtLeast(0f)),
                size = Size(width, glowHeight)
            )

            // Secondary subtle forward glow aura
            val forwardGlowHeight = 20.dp.toPx()
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        if (isProcessing) Color(0xFF00E676).copy(alpha = 0.30f) else Color(0xFF00E5FF).copy(alpha = 0.15f),
                        Color.Transparent
                    ),
                    startY = currentLaserY,
                    endY = (currentLaserY + forwardGlowHeight).coerceAtMost(height)
                ),
                topLeft = Offset(0f, currentLaserY),
                size = Size(width, forwardGlowHeight)
            )

            // 1d. Sharp High-Energy Laser Beam Line
            drawLine(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color.Transparent,
                        if (isProcessing) Color(0xFF00E5FF) else Color(0xFF00E5FF).copy(alpha = 0.6f),
                        if (isProcessing) Color(0xFF00E676) else Color(0xFF00E5FF),
                        Color.White,
                        if (isProcessing) Color(0xFF00E676) else Color(0xFF00E5FF),
                        if (isProcessing) Color(0xFF00E5FF) else Color(0xFF00E5FF).copy(alpha = 0.6f),
                        Color.Transparent
                    )
                ),
                start = Offset(0f, currentLaserY),
                end = Offset(width, currentLaserY),
                strokeWidth = if (isProcessing) 3.5.dp.toPx() else 2.2.dp.toPx(),
                cap = StrokeCap.Round
            )

            // Dynamic Scanning Light Particles along laser beam
            if (isProcessing) {
                val particleCount = 6
                for (i in 0 until particleCount) {
                    val pX = (width * ((i.toFloat() / particleCount + particlePhase) % 1f))
                    val pRadius = (2.5f + (i % 2) * 1.5f).dp.toPx()
                    drawCircle(
                        color = Color.White.copy(alpha = 0.85f),
                        radius = pRadius,
                        center = Offset(pX, currentLaserY)
                    )
                }
            }

            // 1e. Corner Targeting Brackets with High-Tech Bevels
            val cornerLength = 32.dp.toPx()
            val cornerStroke = 3.5.dp.toPx()
            val bracketColor = if (isProcessing) {
                Color(0xFF00E676).copy(alpha = pulseAlpha)
            } else {
                Color(0xFF00E5FF).copy(alpha = pulseAlpha)
            }

            // Top-Left
            drawPath(
                path = Path().apply {
                    moveTo(0f, cornerLength)
                    lineTo(0f, 0f)
                    lineTo(cornerLength, 0f)
                },
                color = bracketColor,
                style = Stroke(width = cornerStroke, cap = StrokeCap.Round)
            )

            // Top-Right
            drawPath(
                path = Path().apply {
                    moveTo(width - cornerLength, 0f)
                    lineTo(width, 0f)
                    lineTo(width, cornerLength)
                },
                color = bracketColor,
                style = Stroke(width = cornerStroke, cap = StrokeCap.Round)
            )

            // Bottom-Left
            drawPath(
                path = Path().apply {
                    moveTo(0f, height - cornerLength)
                    lineTo(0f, height)
                    lineTo(cornerLength, height)
                },
                color = bracketColor,
                style = Stroke(width = cornerStroke, cap = StrokeCap.Round)
            )

            // Bottom-Right
            drawPath(
                path = Path().apply {
                    moveTo(width - cornerLength, height)
                    lineTo(width, height)
                    lineTo(width, height - cornerLength)
                },
                color = bracketColor,
                style = Stroke(width = cornerStroke, cap = StrokeCap.Round)
            )

            // 1f. Center Reticle & Rotating Radar Arc (when actively processing)
            if (isProcessing) {
                val center = Offset(width / 2f, height / 2f)
                val reticleRadius = 40.dp.toPx()

                // Dashed outer reticle ring
                drawCircle(
                    color = Color(0xFF00E5FF).copy(alpha = 0.55f),
                    radius = reticleRadius,
                    center = center,
                    style = Stroke(
                        width = 1.5.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)
                    )
                )

                // Rotating Radar Beam line
                val rad = Math.toRadians(radarRotation.toDouble())
                val rayEnd = Offset(
                    x = center.x + (reticleRadius * cos(rad)).toFloat(),
                    y = center.y + (reticleRadius * sin(rad)).toFloat()
                )
                drawLine(
                    color = Color(0xFF00E676),
                    start = center,
                    end = rayEnd,
                    strokeWidth = 2.2.dp.toPx(),
                    cap = StrokeCap.Round
                )

                // Center crosshair
                val crossLength = 10.dp.toPx()
                drawLine(
                    color = Color.White.copy(alpha = 0.9f),
                    start = Offset(center.x - crossLength, center.y),
                    end = Offset(center.x + crossLength, center.y),
                    strokeWidth = 1.5.dp.toPx()
                )
                drawLine(
                    color = Color.White.copy(alpha = 0.9f),
                    start = Offset(center.x, center.y - crossLength),
                    end = Offset(center.x, center.y + crossLength),
                    strokeWidth = 1.5.dp.toPx()
                )
            }
        }

        // 2. Viewfinder Outer Border Container
        Box(
            modifier = Modifier
                .size(width = frameWidth, height = frameHeight)
                .border(
                    width = 1.5.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            if (isProcessing) Color(0xFF00E676) else Color(0xFF00E5FF),
                            Color(0xFF4285F4).copy(alpha = 0.6f),
                            if (isProcessing) Color(0xFF00E5FF) else Color(0xFF00E676)
                        )
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
        )
        
        // 3. Simple Processing Indicator (Clean & Minimal)
        if (isProcessing) {
            Surface(
                color = Color.Black.copy(alpha = 0.6f),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = (-16).dp)
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
                    Text(
                        text = "Analyzing...",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

private fun BorderStroke(width: androidx.compose.ui.unit.Dp, color: Color) = androidx.compose.foundation.BorderStroke(width, color)
private fun BorderStroke(width: androidx.compose.ui.unit.Dp, brush: Brush) = androidx.compose.foundation.BorderStroke(width, brush)
