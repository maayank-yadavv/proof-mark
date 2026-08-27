package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// ============================================================================
// AMOLED DARK BLACK & LIGHT GREEN PALETTE
// ============================================================================

// Primary Brand Colors (Light Green System)
val LightGreenPrimary = Color(0xFF10B981)     // Vibrant Emerald / Light Green
val LightGreenBright = Color(0xFF22C55E)      // Bright Mint Green (AMOLED Accent)
val LightGreenContainerDark = Color(0xFF022C22) // Deep Emerald AMOLED Container
val LightGreenContainerLight = Color(0xFFD1FAE5)// Soft Light Mint Container

// Neutral Scale - AMOLED Dark Theme
val AmoledDarkBackground = Color(0xFF000000)  // True AMOLED Pitch Black
val AmoledDarkCardSurface = Color(0xFF121212)  // Sleek Dark Black Surface
val AmoledDarkSurfaceVariant = Color(0xFF1C1C1E)// Dark Gray Card Container
val AmoledDarkBorder = Color(0xFF2C2C2E)        // Subtle Dark Border
val AmoledTextMain = Color(0xFFFFFFFF)          // High Contrast Pure White Text
val AmoledTextMuted = Color(0xFFA1A1AA)         // Crisp Muted Gray Text

// Neutral Scale - Light Theme
val LightCanvasBackground = Color(0xFFF8FAFC) // Crisp Off-White Canvas
val LightCardSurface = Color(0xFFFFFFFF)       // Pure White Cards
val LightSurfaceVariant = Color(0xFFF1F5F9)    // Light Slate Container
val LightBorderColor = Color(0xFFE2E8F0)       // Clean Light Gray Border
val LightTextMain = Color(0xFF0F172A)          // Dark High Contrast Text
val LightTextMuted = Color(0xFF64748B)         // Slate Secondary Text

// Standard Status Indicators
val CompliancePass = Color(0xFF10B981)          // Light Green Pass
val CompliancePassDark = Color(0xFF22C55E)
val CompliancePassContainer = Color(0xFFD1FAE5)
val CompliancePassText = Color(0xFF065F46)

val ComplianceFail = Color(0xFFEF4444)          // Crisp Red Fail
val ComplianceFailDark = Color(0xFFF87171)
val ComplianceFailContainer = Color(0xFFFEE2E2)
val ComplianceFailText = Color(0xFF7F1D1D)

val ComplianceReview = Color(0xFFF59E0B)        // Clear Amber Review
val ComplianceReviewDark = Color(0xFFFBBF24)
val ComplianceReviewContainer = Color(0xFFFEF3C7)
val ComplianceReviewText = Color(0xFF78350F)

val ComplianceDraft = Color(0xFF71717A)

// Color Mappings & Legacy References
val PrimaryBlue = LightGreenPrimary
val PrimaryBlueDark = LightGreenBright
val PrimaryBlueContainerLight = LightGreenContainerLight
val PrimaryBlueContainerDark = LightGreenContainerDark

val Navy900 = LightTextMain
val Navy800 = LightGreenPrimary
val Navy700 = LightGreenBright
val Navy600 = LightTextMuted
val Navy500 = AmoledTextMuted
val Navy200 = LightBorderColor
val Navy100 = LightSurfaceVariant
val Navy50  = LightCanvasBackground

val Teal600 = LightGreenPrimary
val Teal500 = LightGreenBright
val Teal100 = LightGreenContainerLight
val Teal900 = LightGreenContainerDark

val DarkBackground = AmoledDarkBackground
val DarkSurface = AmoledDarkCardSurface
val DarkSurfaceVariant_Legacy = AmoledDarkSurfaceVariant
val DarkOutline = AmoledDarkBorder
val DarkTextPrimary = AmoledTextMain
val DarkTextSecondary = AmoledTextMuted
val DarkTextMuted_Legacy = AmoledTextMuted

val LightBackground = LightCanvasBackground
val LightSurface = LightCardSurface
val LightSurfaceVariant_Legacy = LightSurfaceVariant
val LightOutline = LightBorderColor
val LightTextPrimary = LightTextMain
val LightTextSecondary = LightTextMuted
val LightTextMuted_Legacy = LightTextMuted

val PrimaryBlueLight = LightGreenBright
val PrimaryCyan = LightGreenBright
val AccentTeal = LightGreenPrimary
val AccentGold = ComplianceReview

val ElegantDarkBackground = AmoledDarkBackground
val ElegantDarkSurface = AmoledDarkCardSurface
val ElegantDarkSurfaceVariant = AmoledDarkSurfaceVariant
val ElegantDarkSurfaceContainer = AmoledDarkSurfaceVariant
val ElegantDarkSurfaceContainerHigh = AmoledDarkSurfaceVariant
val ElegantDarkSurfaceContainerHighest = AmoledDarkSurfaceVariant
val ElegantDarkCard = AmoledDarkCardSurface
val ElegantDarkBorder = AmoledDarkBorder
val ElegantDarkOutline = AmoledDarkBorder
val ElegantDarkOutlineVariant = AmoledDarkBorder

val ElegantPrimary = LightGreenBright
val ElegantOnPrimary = Color.Black
val ElegantPrimaryContainer = LightGreenContainerDark
val ElegantOnPrimaryContainer = LightGreenContainerLight

val ElegantSecondary = LightGreenPrimary
val ElegantOnSecondary = Color.Black
val ElegantSecondaryContainer = LightGreenContainerLight
val ElegantOnSecondaryContainer = LightGreenContainerDark

val ElegantTertiary = AmoledTextMuted
val ElegantOnTertiary = LightTextMain
val ElegantTertiaryContainer = LightSurfaceVariant
val ElegantOnTertiaryContainer = LightTextMain

val ElegantTextPrimary = AmoledTextMain
val ElegantTextSecondary = AmoledTextMuted
val ElegantTextMuted = AmoledTextMuted
val ElegantTextInverse = LightTextMain

val ProofNavyDark = AmoledDarkBackground
val ProofNavySurface = AmoledDarkCardSurface
val ProofNavyCard = AmoledDarkCardSurface
val ProofNavyBorder = AmoledDarkBorder

