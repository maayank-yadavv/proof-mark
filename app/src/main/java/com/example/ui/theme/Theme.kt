package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val ProofMarkDarkColorScheme = darkColorScheme(
    primary = ElegantPrimary,
    onPrimary = ElegantOnPrimary,
    primaryContainer = ElegantPrimaryContainer,
    onPrimaryContainer = ElegantOnPrimaryContainer,
    secondary = ElegantSecondary,
    onSecondary = ElegantOnSecondary,
    secondaryContainer = ElegantSecondaryContainer,
    onSecondaryContainer = ElegantOnSecondaryContainer,
    tertiary = ElegantTertiary,
    onTertiary = ElegantOnTertiary,
    tertiaryContainer = ElegantTertiaryContainer,
    onTertiaryContainer = ElegantOnTertiaryContainer,
    background = ElegantDarkBackground,
    onBackground = ElegantTextPrimary,
    surface = ElegantDarkSurface,
    onSurface = ElegantTextPrimary,
    surfaceVariant = ElegantDarkSurfaceVariant,
    onSurfaceVariant = ElegantTextSecondary,
    surfaceContainer = ElegantDarkSurfaceContainer,
    surfaceContainerHigh = ElegantDarkSurfaceContainerHigh,
    outline = ElegantDarkOutline,
    outlineVariant = ElegantDarkOutlineVariant,
    error = ComplianceFail,
    onError = Color(0xFF601410),
    errorContainer = ComplianceFailContainer,
    onErrorContainer = ComplianceFailText,
    inverseSurface = ElegantTextPrimary,
    inverseOnSurface = ElegantDarkBackground,
    inversePrimary = Color(0xFF6750A4)
)

private val ProofMarkLightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD0E4FF),
    onPrimaryContainer = Color(0xFF001D36),
    secondary = PrimaryBlueLight,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD6F0F6),
    onSecondaryContainer = Color(0xFF001F25),
    tertiary = Color(0xFFB45309),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFE088),
    onTertiaryContainer = Color(0xFF261800),
    background = LightBackground,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightTextSecondary,
    outline = LightOutline,
    outlineVariant = Color(0xFFE2E8F0),
    error = ComplianceFail,
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002)
)

@Composable
fun ProofMarkTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Use our dedicated enforcement theme by default
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> ProofMarkDarkColorScheme
        else -> ProofMarkLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// Keep alias for backwards compatibility
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) = ProofMarkTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
