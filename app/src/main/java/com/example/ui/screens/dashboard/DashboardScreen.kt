package com.example.ui.screens.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.ai.MockInspectionSamples
import com.example.data.models.ComplianceStatus
import com.example.data.models.UserRole
import com.example.ui.components.InspectionItemCard
import com.example.ui.components.ProofMarkLogoBadge
import com.example.ui.components.ResponsiveContainer
import com.example.ui.components.StatusBadge
import com.example.ui.theme.ComplianceFail
import com.example.ui.theme.CompliancePass
import com.example.ui.theme.ComplianceReview
import com.example.ui.viewmodel.DashboardStats
import com.example.ui.viewmodel.InspectionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: InspectionViewModel,
    onNavigateNewInspection: () -> Unit,
    onNavigateInspectionDetail: (String) -> Unit,
    onNavigateHistory: () -> Unit,
    onNavigateRules: () -> Unit,
    onNavigateSettings: () -> Unit,
    onNavigateCamera: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val stats by viewModel.dashboardStats.collectAsStateWithLifecycle()
    val inspections by viewModel.filteredInspections.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()

    var showDemoPicker by remember { mutableStateOf(false) }
    val isStandardUser = currentUser.role == UserRole.STANDARD_USER

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ProofMarkLogoBadge(
                            size = 38.dp,
                            showAura = false,
                            modifier = Modifier.padding(end = 4.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "ProofMark",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (isStandardUser) "Consumer Package Portal" else "Legal Metrology Enforcement",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isStandardUser) Color(0xFF10B981) else MaterialTheme.colorScheme.outline,
                                fontWeight = if (isStandardUser) FontWeight.SemiBold else FontWeight.Normal
                            )
                        }
                    }
                },
                actions = {
                    Surface(
                        color = if (isStandardUser) Color(0xFF10B981).copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .clickable { onNavigateSettings() }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(if (isStandardUser) Color(0xFF10B981) else CompliancePass, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isStandardUser) "User" else currentUser.role.title.take(10),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isStandardUser) Color(0xFF047857) else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    IconButton(
                        onClick = onNavigateSettings,
                        modifier = Modifier.testTag("dashboard_settings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateCamera,
                icon = {
                    Icon(
                        imageVector = if (isStandardUser) Icons.Default.QrCodeScanner else Icons.Default.CameraAlt,
                        contentDescription = "Scan Label"
                    )
                },
                text = {
                    Text(
                        text = if (isStandardUser) "Scan QR / Label" else "Scan Label",
                        fontWeight = FontWeight.Bold
                    )
                },
                containerColor = if (isStandardUser) Color(0xFF10B981) else MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                modifier = Modifier.testTag("dashboard_camera_fab")
            )
        },
        modifier = modifier
    ) { innerPadding ->
        ResponsiveContainer(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    // Clean User / Officer Hero Card
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isStandardUser)
                            Color(0xFF10B981).copy(alpha = 0.10f)
                        else
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                    ),
                    border = BorderStroke(
                        1.dp,
                        if (isStandardUser) Color(0xFF10B981).copy(alpha = 0.3f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = currentUser.name,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (isStandardUser)
                                        "User Portal • Retail Verifier"
                                    else
                                        "${currentUser.stationJurisdiction} • Badge #${currentUser.badgeNumber}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isStandardUser) Color(0xFF047857) else MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(if (isStandardUser) Color(0xFF10B981) else MaterialTheme.colorScheme.primary),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = currentUser.name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString(""),
                                    color = Color.White,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Primary Action Buttons Row
                        if (isStandardUser) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = onNavigateCamera,
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                    modifier = Modifier
                                        .weight(1.3f)
                                        .height(46.dp)
                                        .testTag("dashboard_scan_qr_button")
                                ) {
                                    Icon(Icons.Default.QrCodeScanner, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Scan QR / Label", fontWeight = FontWeight.Bold)
                                }

                                FilledTonalButton(
                                    onClick = onNavigateNewInspection,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(46.dp)
                                        .testTag("new_inspection_button")
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Verify Product")
                                }
                            }
                        } else {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = onNavigateCamera,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .weight(1.2f)
                                        .height(46.dp)
                                        .testTag("dashboard_live_camera_button")
                                ) {
                                    Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Live Camera", fontWeight = FontWeight.Bold)
                                }

                                FilledTonalButton(
                                    onClick = onNavigateNewInspection,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(46.dp)
                                        .testTag("new_inspection_button")
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Manual Form")
                                }

                                OutlinedButton(
                                    onClick = { showDemoPicker = true },
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .weight(0.9f)
                                        .height(46.dp)
                                        .testTag("load_demo_button")
                                ) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text("Presets")
                                }
                            }
                        }
                    }
                }
            }

            // Clean 3-Metric Overview Grid
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CleanStatCard(
                        title = if (isStandardUser) "My Scans" else "Total Scans",
                        value = "${if (isStandardUser) inspections.size else stats.totalInspections}",
                        accentColor = if (isStandardUser) Color(0xFF10B981) else MaterialTheme.colorScheme.primary,
                        icon = Icons.Default.AssignmentTurnedIn,
                        modifier = Modifier.weight(1f)
                    )
                    CleanStatCard(
                        title = if (isStandardUser) "Verified" else "Pass Rate",
                        value = if (isStandardUser) "${inspections.count { it.status == ComplianceStatus.PASS }}" else "${stats.complianceRatePercent}%",
                        accentColor = CompliancePass,
                        icon = Icons.Default.CheckCircle,
                        modifier = Modifier.weight(1f)
                    )
                    CleanStatCard(
                        title = if (isStandardUser) "Incomplete" else "Violations",
                        value = if (isStandardUser) "${inspections.count { it.status != ComplianceStatus.PASS }}" else "${stats.violationCount}",
                        accentColor = if (isStandardUser) ComplianceReview else ComplianceFail,
                        icon = Icons.Default.ErrorOutline,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Quick Navigation Chips (Admin/Officer only for Rules)
            if (!isStandardUser) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        QuickToolChip(
                            label = "Audit Records",
                            icon = Icons.Default.History,
                            onClick = onNavigateHistory,
                            modifier = Modifier.weight(1f)
                        )
                        QuickToolChip(
                            label = "Rules Engine",
                            icon = Icons.AutoMirrored.Filled.MenuBook,
                            onClick = onNavigateRules,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Day-to-Day Consumer Guidance Card
            if (isStandardUser) {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = Color(0xFF10B981),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Consumer Legal Metrology Checklist",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Text(
                                text = "Ensure packages display MRP (inclusive of all taxes), Net Quantity, Unit Sale Price (USP), Date of Manufacture/Packing, and Manufacturer Customer Care Helpline.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }

            // Repeat Offender Alert (Officer & Admin only)
            if (!isStandardUser && stats.repeatOffenders.isNotEmpty()) {
                val topRepeat = stats.repeatOffenders.firstOrNull { it.second >= 1 }
                if (topRepeat != null) {
                    item {
                        Surface(
                            color = ComplianceFail.copy(alpha = 0.08f),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, ComplianceFail.copy(alpha = 0.25f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.WarningAmber,
                                    contentDescription = null,
                                    tint = ComplianceFail,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "High Infraction Brand: ${topRepeat.first}",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = ComplianceFail
                                    )
                                    Text(
                                        text = "${topRepeat.second} recorded non-compliance notices under LM Act.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                TextButton(onClick = onNavigateHistory) {
                                    Text("View", color = ComplianceFail, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // Recent Inspections / Scans Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isStandardUser) "My Scanned Products" else "Recent Inspections",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    TextButton(
                        onClick = onNavigateHistory,
                        modifier = Modifier.testTag("dashboard_view_all_records")
                    ) {
                        Text("View All (${inspections.size})", fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Inspections List or Empty State
            if (inspections.isEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = if (isStandardUser) Icons.Default.QrCodeScanner else Icons.Default.DocumentScanner,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.outline,
                                modifier = Modifier.size(48.dp)
                            )
                            Text(
                                text = if (isStandardUser) "No Scanned Products Yet" else "No Inspection Records",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (isStandardUser)
                                    "Tap the button below to scan a QR code, barcode, or product package label to get statutory details."
                                else
                                    "Capture a label with Live Camera or load a preset to start statutory verification.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Button(
                                onClick = onNavigateCamera,
                                shape = RoundedCornerShape(10.dp),
                                colors = if (isStandardUser) ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)) else ButtonDefaults.buttonColors()
                            ) {
                                Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(if (isStandardUser) "Scan Product Now" else "Start Live Scan")
                            }
                        }
                    }
                }
            } else {
                items(inspections.take(5)) { item ->
                    InspectionItemCard(
                        inspection = item,
                        onClick = { onNavigateInspectionDetail(item.id) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(60.dp))
            }
        }
    }
}

    // Preset Cases Bottom Sheet
    if (showDemoPicker) {
        DemoPackagePickerBottomSheet(
            onDismiss = { showDemoPicker = false },
            onSelectCase = { demoCase ->
                showDemoPicker = false
                viewModel.startDemoInspection(demoCase) { createdId ->
                    onNavigateInspectionDetail(createdId)
                }
            }
        )
    }
}

@Composable
fun CleanStatCard(
    title: String,
    value: String,
    accentColor: Color,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(accentColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(18.dp)
                )
            }
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun QuickToolChip(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DemoPackagePickerBottomSheet(
    onDismiss: () -> Unit,
    onSelectCase: (com.example.data.ai.DemoPackageCase) -> Unit = {},
    onSelectSample: (com.example.data.ai.DemoPackageCase) -> Unit = {}
) {
    val sheetState = rememberModalBottomSheetState()
    val demoCases = remember { MockInspectionSamples.getDemoCases() }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp)
                .padding(bottom = 30.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Load Legal Metrology Test Presets",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = "Select a predefined packaged commodity case from the SIH evaluation dataset:",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            demoCases.forEach { demo ->
                Card(
                    onClick = {
                        onSelectCase(demo)
                        onSelectSample(demo)
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = demo.product.name,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                StatusBadge(status = demo.expectedStatus)
                            }
                            Text(
                                text = "${demo.product.brand} • ${demo.product.category.label}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = demo.sampleDescription,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.outline,
                                maxLines = 1
                            )
                        }
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
