package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.ConnectivityBannerEvent
import com.example.data.models.ConnectivityStatus
import com.example.data.models.NetworkConnectivityState

/**
 * Non-intrusive animated Connectivity Banner displayed globally across the application.
 * Communicates connection state transitions cleanly:
 * - Offline: "You're offline. Some features may be unavailable."
 * - Online: "You're back online."
 * - Syncing/Synced indicators
 */
@Composable
fun ConnectivityBanner(
    networkState: NetworkConnectivityState,
    onDismissBanner: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isVisible = networkState.bannerEvent != ConnectivityBannerEvent.NONE ||
            !networkState.isConnected ||
            networkState.status == ConnectivityStatus.SYNCING

    AnimatedVisibility(
        visible = isVisible,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut(),
        modifier = modifier
            .fillMaxWidth()
            .testTag("connectivity_banner")
    ) {
        val (bannerBg, borderColor, icon, title, subtitle, iconTint) = when {
            // 1. Offline Mode
            !networkState.isConnected -> Tuple6(
                Color(0xFF450A0A), // Deep dark red canvas
                Color(0xFFEF4444),
                Icons.Default.WifiOff,
                "You're offline. Some features may be unavailable.",
                if (networkState.pendingSyncCount > 0)
                    "${networkState.pendingSyncCount} changes queued locally for auto-sync"
                else
                    "Local Room database & rules engine active",
                Color(0xFFEF4444)
            )

            // 2. Syncing Queued Changes
            networkState.status == ConnectivityStatus.SYNCING -> Tuple6(
                Color(0xFF451A03), // Deep dark amber canvas
                Color(0xFFF59E0B),
                Icons.Default.Sync,
                "Syncing offline changes with Remote Gateway...",
                "Uploading queued inspection records...",
                Color(0xFFF59E0B)
            )

            // 3. Online Recovery
            networkState.bannerEvent == ConnectivityBannerEvent.ONLINE_RECOVERY -> Tuple6(
                Color(0xFF064E3B), // Deep dark green canvas
                Color(0xFF10B981),
                Icons.Default.CloudDone,
                "You're back online.",
                "Remote Legal Metrology API & ONDC Gateway reconnected",
                Color(0xFF22C55E)
            )

            // 4. Default Synced State
            else -> Tuple6(
                Color(0xFF1E3A8A), // Deep dark blue canvas
                Color(0xFF3B82F6),
                Icons.Default.CheckCircle,
                "All offline operations synchronized.",
                "Remote database state up to date",
                Color(0xFF60A5FA)
            )
        }

        Surface(
            color = bannerBg,
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, borderColor.copy(alpha = 0.5f)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = iconTint.copy(alpha = 0.2f),
                        modifier = Modifier.size(32.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = iconTint,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 13.sp
                        )
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 11.sp
                        )
                    }
                }

                if (networkState.isConnected && networkState.bannerEvent != ConnectivityBannerEvent.NONE) {
                    IconButton(
                        onClick = onDismissBanner,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Dismiss",
                            tint = Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

private data class Tuple6<A, B, C, D, E, F>(
    val a: A,
    val b: B,
    val c: C,
    val d: D,
    val e: E,
    val f: F
)
