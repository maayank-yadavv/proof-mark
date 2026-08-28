package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext

val LocalAutoFontScale = staticCompositionLocalOf { 1.0f }
val LocalScreenWidthDp = staticCompositionLocalOf { 360 }

private val ProofMarkDarkColorScheme = darkColorScheme(
    primary = LightGreenBright,
    onPrimary = Color.Black,
    primaryContainer = LightGreenContainerDark,
    onPrimaryContainer = LightGreenContainerLight,
    secondary = Color(0xFF34D399),
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF064E3B),
    onSecondaryContainer = Color(0xFFD1FAE5),
    tertiary = ComplianceReviewDark,
    onTertiary = Color.Black,
    tertiaryContainer = Color(0xFF78350F),
    onTertiaryContainer = Color(0xFFFEF3C7),
    background = AmoledDarkBackground,
    onBackground = AmoledTextMain,
    surface = AmoledDarkCardSurface,
    onSurface = AmoledTextMain,
    surfaceVariant = AmoledDarkSurfaceVariant,
    onSurfaceVariant = AmoledTextMuted,
    surfaceContainer = AmoledDarkCardSurface,
    surfaceContainerHigh = AmoledDarkSurfaceVariant,
    outline = AmoledDarkBorder,
    outlineVariant = Color(0xFF3F3F46),
    error = ComplianceFailDark,
    onError = Color.Black,
    errorContainer = Color(0xFF7F1D1D),
    onErrorContainer = Color(0xFFFEE2E2),
    inverseSurface = LightTextMain,
    inverseOnSurface = LightCanvasBackground,
    inversePrimary = LightGreenPrimary
)

private val ProofMarkLightColorScheme = lightColorScheme(
    primary = LightGreenPrimary,
    onPrimary = Color.White,
    primaryContainer = LightGreenContainerLight,
    onPrimaryContainer = Color(0xFF064E3B),
    secondary = Color(0xFF059669),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD1FAE5),
    onSecondaryContainer = Color(0xFF065F46),
    tertiary = ComplianceReview,
    onTertiary = Color.White,
    tertiaryContainer = ComplianceReviewContainer,
    onTertiaryContainer = ComplianceReviewText,
    background = LightCanvasBackground,
    onBackground = LightTextMain,
    surface = LightCardSurface,
    onSurface = LightTextMain,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightTextMuted,
    outline = LightBorderColor,
    outlineVariant = Color(0xFFCBD5E1),
    error = ComplianceFail,
    onError = Color.White,
    errorContainer = ComplianceFailContainer,
    onErrorContainer = ComplianceFailText
)

@Composable
fun ProofMarkTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp
    val screenHeightDp = configuration.screenHeightDp

    val fontScale = remember(screenWidthDp, screenHeightDp) {
        calculateScreenFontScale(screenWidthDp, screenHeightDp)
    }

    val autoAdjustedTypography = remember(screenWidthDp, screenHeightDp) {
        getAutoAdjustedTypography(screenWidthDp, screenHeightDp)
    }

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> ProofMarkDarkColorScheme
        else -> ProofMarkLightColorScheme
    }

    CompositionLocalProvider(
        LocalAutoFontScale provides fontScale,
        LocalScreenWidthDp provides screenWidthDp
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = autoAdjustedTypography,
            shapes = AppShapes,
            content = content
        )
    }
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) = ProofMarkTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)

