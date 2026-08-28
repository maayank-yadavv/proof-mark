package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.isSpecified
import androidx.compose.ui.unit.sp

val BaseTypography = Typography(
    headlineLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 34.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 19.sp,
        lineHeight = 25.sp,
        letterSpacing = 0.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 17.sp,
        lineHeight = 23.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 15.sp,
        lineHeight = 21.sp,
        letterSpacing = 0.15.sp
    ),
    titleSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 13.5.sp,
        lineHeight = 19.sp,
        letterSpacing = 0.1.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.3.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 13.5.sp,
        lineHeight = 19.sp,
        letterSpacing = 0.2.sp
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 11.5.sp,
        lineHeight = 15.5.sp,
        letterSpacing = 0.3.sp
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 13.5.sp,
        lineHeight = 19.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.5.sp,
        lineHeight = 15.5.sp,
        letterSpacing = 0.4.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 10.sp,
        lineHeight = 13.5.sp,
        letterSpacing = 0.4.sp
    )
)

val Typography = BaseTypography

/**
 * Calculates dynamic font scale factor based on mobile screen width and height.
 */
fun calculateScreenFontScale(screenWidthDp: Int, screenHeightDp: Int): Float {
    return when {
        screenWidthDp < 320 -> 0.86f // Very small screen
        screenWidthDp < 360 -> 0.92f // Compact screen
        screenWidthDp < 400 -> 0.98f // Standard phone screen
        screenWidthDp < 480 -> 1.03f // Large phone screen
        screenWidthDp < 600 -> 1.08f // Phablet / Compact tablet
        else -> 1.14f                // Tablet / Large unfolded screen
    }
}

private fun TextStyle.scale(scaleFactor: Float): TextStyle {
    val newFontSize = (fontSize.value * scaleFactor).sp
    val newLineHeight = if (lineHeight.isSpecified && lineHeight.value > 0) {
        (lineHeight.value * scaleFactor).sp
    } else {
        (fontSize.value * scaleFactor * 1.35f).sp
    }
    return this.copy(
        fontSize = newFontSize,
        lineHeight = newLineHeight
    )
}

/**
 * Auto-adjusts all Material 3 typography styles to fit perfectly on the active mobile screen.
 */
fun getAutoAdjustedTypography(screenWidthDp: Int, screenHeightDp: Int): Typography {
    val scale = calculateScreenFontScale(screenWidthDp, screenHeightDp)
    return Typography(
        headlineLarge = BaseTypography.headlineLarge.scale(scale),
        headlineMedium = BaseTypography.headlineMedium.scale(scale),
        headlineSmall = BaseTypography.headlineSmall.scale(scale),
        titleLarge = BaseTypography.titleLarge.scale(scale),
        titleMedium = BaseTypography.titleMedium.scale(scale),
        titleSmall = BaseTypography.titleSmall.scale(scale),
        bodyLarge = BaseTypography.bodyLarge.scale(scale),
        bodyMedium = BaseTypography.bodyMedium.scale(scale),
        bodySmall = BaseTypography.bodySmall.scale(scale),
        labelLarge = BaseTypography.labelLarge.scale(scale),
        labelMedium = BaseTypography.labelMedium.scale(scale),
        labelSmall = BaseTypography.labelSmall.scale(scale)
    )
}
