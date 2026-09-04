package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R

/**
 * Proof Mark Official Brand Logo Composable.
 * Renders the brand logo with the scanner bracket viewfinder, verified green shield,
 * amber verification dot, and high-contrast checkmark.
 */
@Composable
fun ProofMarkBrandLogo(
    modifier: Modifier = Modifier,
    size: Dp = 36.dp,
    withGlow: Boolean = true,
    shapeCornerRadius: Dp = 10.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(shapeCornerRadius))
            .background(
                if (withGlow) {
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF00E676).copy(alpha = 0.22f),
                            Color(0xFF06180E).copy(alpha = 0.85f)
                        )
                    )
                } else {
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF06180E), Color(0xFF020D07))
                    )
                }
            )
            .border(
                width = 1.dp,
                color = Color(0xFF00E676).copy(alpha = 0.4f),
                shape = RoundedCornerShape(shapeCornerRadius)
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_proofmark_logo),
            contentDescription = "Proof Mark Logo",
            tint = Color.Unspecified, // Retains vector colors: neon green, amber, dark fill
            modifier = Modifier
                .size(size * 0.85f)
                .testTag("proofmark_brand_logo_icon")
        )
    }
}

/**
 * Top App Bar Brand Header with Proof Mark Logo and Portal designation.
 */
@Composable
fun ProofMarkTopBarBrand(
    modifier: Modifier = Modifier,
    title: String = "Legal Metrology",
    subtitle: String = "Consumer Portal",
    isConsumerMode: Boolean = true,
    onClick: (() -> Unit)? = null
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(vertical = 4.dp, horizontal = 2.dp)
            .testTag("proofmark_top_bar_brand")
    ) {
        ProofMarkBrandLogo(
            size = 38.dp,
            withGlow = true
        )

        Spacer(modifier = Modifier.width(10.dp))

        Column(verticalArrangement = Arrangement.Center) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.width(6.dp))
                AnimatedPulseDot(
                    color = if (isConsumerMode) Color(0xFF00E676) else Color(0xFF3B82F6),
                    size = 8.dp
                )
            }

            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = if (isConsumerMode) Color(0xFF00E676) else MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
