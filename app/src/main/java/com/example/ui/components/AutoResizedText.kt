package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocalAutoFontScale
import com.example.ui.theme.LocalScreenWidthDp

/**
 * AutoResizedText automatically reduces font size if the text overflows
 * container constraints or max lines on small mobile screens.
 */
@Composable
fun AutoResizedText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
    color: Color = Color.Unspecified,
    minFontSize: TextUnit = 9.sp,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    textAlign: TextAlign? = null,
    fontWeight: FontWeight? = null,
    stepValue: TextUnit = 0.5.sp
) {
    val autoFontScale = LocalAutoFontScale.current
    var resizedTextStyle by remember(text, style, autoFontScale) {
        val baseSize = if (style.fontSize != TextUnit.Unspecified) style.fontSize else 14.sp
        val scaledSize = (baseSize.value * autoFontScale).sp
        mutableStateOf(style.copy(fontSize = scaledSize, fontWeight = fontWeight ?: style.fontWeight))
    }

    var shouldDraw by remember(text, style) { mutableStateOf(false) }

    Text(
        text = text,
        color = color,
        modifier = modifier.drawWithContent {
            if (shouldDraw) {
                drawContent()
            }
        },
        style = resizedTextStyle,
        maxLines = maxLines,
        overflow = overflow,
        softWrap = softWrap,
        textAlign = textAlign,
        onTextLayout = { result ->
            if (result.hasVisualOverflow && resizedTextStyle.fontSize > minFontSize) {
                val newSize = (resizedTextStyle.fontSize.value - stepValue.value).coerceAtLeast(minFontSize.value).sp
                resizedTextStyle = resizedTextStyle.copy(fontSize = newSize)
            } else {
                shouldDraw = true
            }
        }
    )
}

/**
 * AutoText is a standard Text composable wrapper that automatically scales custom inline fontSize
 * by the screen's auto font adjuster scale factor.
 */
@Composable
fun AutoText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    style: TextStyle = LocalTextStyle.current
) {
    val fontScale = LocalAutoFontScale.current
    val adjustedFontSize = if (fontSize != TextUnit.Unspecified) {
        (fontSize.value * fontScale).sp
    } else {
        fontSize
    }

    Text(
        text = text,
        modifier = modifier,
        color = color,
        fontSize = adjustedFontSize,
        fontStyle = fontStyle,
        fontWeight = fontWeight,
        fontFamily = fontFamily,
        letterSpacing = letterSpacing,
        textDecoration = textDecoration,
        textAlign = textAlign,
        lineHeight = lineHeight,
        overflow = overflow,
        softWrap = softWrap,
        maxLines = maxLines,
        style = style
    )
}

/**
 * AutoFontAdjusterBadge displays a sleek status chip showing screen-responsive font scaling status.
 */
@Composable
fun AutoFontAdjusterBadge(
    modifier: Modifier = Modifier,
    showDetails: Boolean = false
) {
    val fontScale = LocalAutoFontScale.current
    val screenWidth = LocalScreenWidthDp.current
    val scalePercent = (fontScale * 100).toInt()

    Row(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.TextFields,
            contentDescription = "Auto Font Adjuster",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(14.dp)
        )
        Text(
            text = if (showDetails) "Auto-Adjusted: ${scalePercent}% (${screenWidth}dp)" else "Auto-Scaled ${scalePercent}%",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(start = 4.dp)
        )
    }
}
