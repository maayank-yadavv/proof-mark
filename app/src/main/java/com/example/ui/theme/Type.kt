package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import com.example.ui.theme.AppFontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.isSpecified
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.text.googlefonts.Font
import com.example.R

val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

val fontName = GoogleFont("Plus Jakarta Sans")

val AppFontFamily = FontFamily(
    Font(googleFont = fontName, fontProvider = provider, weight = FontWeight.Normal),
    Font(googleFont = fontName, fontProvider = provider, weight = FontWeight.Medium),
    Font(googleFont = fontName, fontProvider = provider, weight = FontWeight.SemiBold),
    Font(googleFont = fontName, fontProvider = provider, weight = FontWeight.Bold)
)

val BaseTypography = Typography(
    headlineLarge = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 34.sp,
        letterSpacing = (-0.5).sp
    ),
    headlineMedium = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 30.sp,
        letterSpacing = (-0.5).sp
    ),
    headlineSmall = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 26.sp,
        letterSpacing = (-0.5).sp
    ),
    titleLarge = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = (-0.25).sp
    ),
    titleMedium = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.sp
    ),
    titleSmall = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),
    labelLarge = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
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
