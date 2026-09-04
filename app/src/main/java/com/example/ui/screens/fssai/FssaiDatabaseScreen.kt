package com.example.ui.screens.fssai

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import com.example.ui.theme.AppFontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.entities.FssaiLicenseEntity
import com.example.ui.components.NetworkConnectivityIndicator
import com.example.ui.viewmodel.InspectionViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FssaiDatabaseScreen(
    viewModel: InspectionViewModel,
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val coroutineScope = rememberCoroutineScope()

    val fssaiLicenses by viewModel.fssaiLicenses.collectAsStateWithLifecycle()
    val searchQuery by viewModel.fssaiSearchQuery.collectAsStateWithLifecycle()
    val networkState by viewModel.networkState.collectAsStateWithLifecycle()
    val pendingSyncItems by viewModel.pendingSyncItems.collectAsStateWithLifecycle()

    val fssaiPendingItems = remember(pendingSyncItems) {
        pendingSyncItems.filter { it.actionType.startsWith("FSSAI") }
    }

    var selectedFilter by remember { mutableStateOf("ALL") } // ALL, EXPIRING_SOON, ACTIVE, EXPIRED, FORTIFIED
    var verifyNumberInput by remember { mutableStateOf("") }
    var verificationResult by remember { mutableStateOf<FssaiLicenseEntity?>(null) }
    var hasPerformedVerification by remember { mutableStateOf(false) }

    var showAddEditDialog by remember { mutableStateOf(false) }
    var editingLicense by remember { mutableStateOf<FssaiLicenseEntity?>(null) }
    var licenseToDelete by remember { mutableStateOf<FssaiLicenseEntity?>(null) }
    var showQrScannerDialog by remember { mutableStateOf(false) }
    var showFoscosBridgeDialog by remember { mutableStateOf(false) }
    var selectedFoscosLicenseNo by remember { mutableStateOf("10012021000071") }

    if (showFoscosBridgeDialog) {
        com.example.ui.components.FoscosPortalBridgeDialog(
            initialLicenseNumber = selectedFoscosLicenseNo,
            onDismiss = { showFoscosBridgeDialog = false },
            onSyncImportSuccess = { syncedEntity ->
                viewModel.saveFssaiLicense(syncedEntity)
                viewModel.updateFssaiSearchQuery(syncedEntity.licenseNumber)
            }
        )
    }

    if (showQrScannerDialog) {
        FssaiQrCodeScannerDialog(
            onDismiss = { showQrScannerDialog = false },
            onQrScanned = { scannedNumber ->
                showQrScannerDialog = false
                viewModel.updateFssaiSearchQuery(scannedNumber)
                verifyNumberInput = scannedNumber
                coroutineScope.launch {
                    verificationResult = viewModel.verifyFssaiLicenseNumber(scannedNumber)
                    hasPerformedVerification = true
                }
                Toast.makeText(context, "QR Code Scanned! Redirected to FSSAI License #$scannedNumber", Toast.LENGTH_LONG).show()
            }
        )
    }

    // Expiring soon (< 30 days) background check list
    val expiringSoonList = remember(fssaiLicenses) {
        fssaiLicenses.filter { isExpiringSoonWithin30Days(it.expiryDate, it.status) }
    }
    val expiringSoonCount = expiringSoonList.size

    // Filtered Licenses
    val filteredList = remember(fssaiLicenses, selectedFilter) {
        when (selectedFilter) {
            "EXPIRING_SOON" -> fssaiLicenses.filter { isExpiringSoonWithin30Days(it.expiryDate, it.status) }
            "ACTIVE" -> fssaiLicenses.filter { it.status == "ACTIVE" }
            "EXPIRED" -> fssaiLicenses.filter { it.status == "EXPIRED" || it.status == "SUSPENDED" }
            "FORTIFIED" -> fssaiLicenses.filter { it.isFortifiedCertified }
            else -> fssaiLicenses
        }
    }

    val totalCount = fssaiLicenses.size
    val activeCount = fssaiLicenses.count { it.status == "ACTIVE" }
    val expiredCount = fssaiLicenses.count { it.status == "EXPIRED" || it.status == "SUSPENDED" }
    val fortifiedCount = fssaiLicenses.count { it.isFortifiedCertified }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "FSSAI Food Safety Register",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Food Safety & Standards Authority of India Database",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                    }
                },
                actions = {
                    androidx.compose.material3.IconButton(
                        onClick = {
                            selectedFoscosLicenseNo = "10012021000071"
                            showFoscosBridgeDialog = true
                        },
                        modifier = Modifier.testTag("fssai_topbar_foscos_sync_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudUpload,
                            contentDescription = "FoSCoS Live Sync",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    androidx.compose.material3.IconButton(
                        onClick = { showQrScannerDialog = true },
                        modifier = Modifier.testTag("fssai_topbar_qr_scan_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCode,
                            contentDescription = "Scan License QR Code",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    NetworkConnectivityIndicator(
                        networkState = networkState,
                        onToggleConnectivity = { viewModel.toggleNetworkConnectivity() },
                        onTriggerPing = { viewModel.triggerNetworkPingCheck() }
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    editingLicense = null
                    showAddEditDialog = true
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("add_fssai_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Register New FSSAI License")
            }
        },
        modifier = modifier
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Header Banner & Summary Stats
            item {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "FSSAI License Verification Engine",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Under the FSS Act 2006 & Legal Metrology Rules 2011, all pre-packaged food items must display a valid 14-digit FSSAI License number. Verified locally via Room DB.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Stats Grid
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            StatCard(
                                label = "Total Entries",
                                value = totalCount.toString(),
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            StatCard(
                                label = "Active",
                                value = activeCount.toString(),
                                color = Color(0xFF10B981),
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            StatCard(
                                label = "Non-Compliant",
                                value = expiredCount.toString(),
                                color = Color(0xFFEF4444),
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            StatCard(
                                label = "+F Fortified",
                                value = fortifiedCount.toString(),
                                color = Color(0xFFF59E0B),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // 2. Visual Remote API Synchronization Indicator Card
            item {
                FssaiSyncStatusIndicator(
                    networkState = networkState,
                    pendingFssaiCount = fssaiPendingItems.size,
                    onTriggerSync = { viewModel.triggerAutoSync() }
                )
            }

            // 3. Automated Background Check Alert Banner (Licenses Expiring <30 Days)
            if (expiringSoonCount > 0) {
                item {
                    FssaiExpiringSoonAlertBanner(
                        expiringLicenses = expiringSoonList,
                        onFilterExpiringSoon = { selectedFilter = "EXPIRING_SOON" }
                    )
                }
            }

            // 2. Instant FSSAI Number Verification Card
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("fssai_verification_card")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Instant 14-Digit FSSAI License Lookup",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            androidx.compose.material3.OutlinedButton(
                                onClick = { showQrScannerDialog = true },
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.testTag("fssai_verify_card_qr_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.QrCode,
                                    contentDescription = "Scan QR Code",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Scan QR",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = verifyNumberInput,
                                onValueChange = {
                                    verifyNumberInput = it
                                    hasPerformedVerification = false
                                },
                                label = { Text("Enter 14-Digit FSSAI No.") },
                                placeholder = { Text("e.g. 10012021000071") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                leadingIcon = {
                                    Icon(Icons.Default.QrCode, contentDescription = null)
                                },
                                trailingIcon = {
                                    if (verifyNumberInput.isNotEmpty()) {
                                        IconButton(onClick = {
                                            verifyNumberInput = ""
                                            hasPerformedVerification = false
                                        }) {
                                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("fssai_verify_input")
                            )

                            Spacer(modifier = Modifier.width(10.dp))

                            Button(
                                onClick = {
                                    if (verifyNumberInput.isNotBlank()) {
                                        coroutineScope.launch {
                                            verificationResult = viewModel.verifyFssaiLicenseNumber(verifyNumberInput)
                                            hasPerformedVerification = true
                                        }
                                    }
                                },
                                enabled = verifyNumberInput.isNotBlank(),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .height(56.dp)
                                    .testTag("fssai_verify_button")
                            ) {
                                Text("Verify")
                            }
                        }

                        // Verification Result Box
                        AnimatedVisibility(visible = hasPerformedVerification) {
                            Spacer(modifier = Modifier.height(12.dp))
                            val result = verificationResult
                            if (result != null) {
                                val isOk = result.status == "ACTIVE"
                                Surface(
                                    color = if (isOk) Color(0xFF064E3B) else Color(0xFF450A0A),
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, if (isOk) Color(0xFF10B981) else Color(0xFFEF4444)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = if (isOk) Icons.Default.CheckCircle else Icons.Default.Warning,
                                                contentDescription = null,
                                                tint = if (isOk) Color(0xFF10B981) else Color(0xFFEF4444),
                                                modifier = Modifier.size(22.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = if (isOk) "VALID & ACTIVE FSSAI LICENSE" else "NON-COMPLIANT LICENSE STATUS: ${result.status}",
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(6.dp))

                                        Text(
                                            text = "Company: ${result.companyName}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color.White
                                        )

                                        Text(
                                            text = "Type: ${result.licenseType} | State: ${result.state}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.White.copy(alpha = 0.8f)
                                        )

                                        Text(
                                            text = "Expiry Date: ${result.expiryDate} | Fortified (+F): ${if (result.isFortifiedCertified) "YES" else "NO"}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.White.copy(alpha = 0.8f)
                                        )

                                        Spacer(modifier = Modifier.height(4.dp))

                                        Text(
                                            text = result.remarks,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = if (isOk) Color(0xFFA7F3D0) else Color(0xFFFCA5A5),
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            } else {
                                Surface(
                                    color = Color(0xFF450A0A),
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, Color(0xFFEF4444)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Error,
                                            contentDescription = null,
                                            tint = Color(0xFFEF4444),
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = "UNREGISTERED / INVALID FSSAI NUMBER",
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                            Text(
                                                text = "No FSSAI license entry found for '$verifyNumberInput' in the official database register.",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color.White.copy(alpha = 0.8f),
                                                fontSize = 11.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 3. Search Bar & Filter Chips
            item {
                Column {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.updateFssaiSearchQuery(it) },
                        label = { Text("Search FSSAI Database") },
                        placeholder = { Text("Filter by Business Name or 14-digit License #...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search Icon", tint = MaterialTheme.colorScheme.primary) },
                        trailingIcon = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(
                                        onClick = { viewModel.updateFssaiSearchQuery("") },
                                        modifier = Modifier.testTag("clear_fssai_search_button")
                                    ) {
                                        Icon(Icons.Default.Clear, contentDescription = "Clear search query")
                                    }
                                }
                                IconButton(
                                    onClick = { showQrScannerDialog = true },
                                    modifier = Modifier.testTag("fssai_search_bar_qr_button")
                                ) {
                                    Icon(Icons.Default.QrCode, contentDescription = "Scan License QR Code", tint = MaterialTheme.colorScheme.primary)
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("fssai_search_input")
                    )

                    if (searchQuery.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        ) {
                            Text(
                                text = "Filtered results for \"$searchQuery\" (${filteredList.size} found)",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        FilterChip(
                            selected = selectedFilter == "ALL",
                            onClick = { selectedFilter = "ALL" },
                            label = { Text("All ($totalCount)") },
                            modifier = Modifier.testTag("filter_all")
                        )
                        FilterChip(
                            selected = selectedFilter == "EXPIRING_SOON",
                            onClick = { selectedFilter = "EXPIRING_SOON" },
                            label = { Text("Expiring Soon ($expiringSoonCount)") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF451A03),
                                selectedLabelColor = Color(0xFFF59E0B)
                            ),
                            modifier = Modifier.testTag("filter_expiring_soon")
                        )
                        FilterChip(
                            selected = selectedFilter == "ACTIVE",
                            onClick = { selectedFilter = "ACTIVE" },
                            label = { Text("Active ($activeCount)") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF064E3B),
                                selectedLabelColor = Color(0xFF10B981)
                            ),
                            modifier = Modifier.testTag("filter_active")
                        )
                        FilterChip(
                            selected = selectedFilter == "EXPIRED",
                            onClick = { selectedFilter = "EXPIRED" },
                            label = { Text("Non-Compliant ($expiredCount)") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF450A0A),
                                selectedLabelColor = Color(0xFFEF4444)
                            ),
                            modifier = Modifier.testTag("filter_expired")
                        )
                        FilterChip(
                            selected = selectedFilter == "FORTIFIED",
                            onClick = { selectedFilter = "FORTIFIED" },
                            label = { Text("+F Fortified ($fortifiedCount)") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF451A03),
                                selectedLabelColor = Color(0xFFF59E0B)
                            ),
                            modifier = Modifier.testTag("filter_fortified")
                        )
                    }
                }
            }

            // 4. List Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "FSSAI Registered Licenses (${filteredList.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // 5. Licenses List
            if (filteredList.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No FSSAI license records match your query",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(
                    items = filteredList,
                    key = { it.licenseNumber }
                ) { license ->
                    val isMatch = (searchQuery.isNotBlank() && searchQuery.trim() == license.licenseNumber) ||
                                  (verifyNumberInput.isNotBlank() && verifyNumberInput.trim() == license.licenseNumber)
                    FssaiLicenseCard(
                        license = license,
                        onCopyNumber = {
                            clipboardManager.setText(AnnotatedString(license.licenseNumber))
                            Toast.makeText(context, "Copied FSSAI #${license.licenseNumber}", Toast.LENGTH_SHORT).show()
                        },
                        onEdit = {
                            editingLicense = license
                            showAddEditDialog = true
                        },
                        onDelete = {
                            licenseToDelete = license
                        },
                        forceExpand = isMatch
                    )
                }
            }
        }
    }

    // Add / Edit Dialog
    if (showAddEditDialog) {
        FssaiAddEditDialog(
            existingLicense = editingLicense,
            onDismiss = { showAddEditDialog = false },
            onSave = { updatedLicense ->
                viewModel.saveFssaiLicense(updatedLicense)
                showAddEditDialog = false
                Toast.makeText(context, "FSSAI License Saved Successfully", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // Delete Confirmation Dialog
    if (licenseToDelete != null) {
        AlertDialog(
            onDismissRequest = { licenseToDelete = null },
            title = { Text("Delete FSSAI License?") },
            text = {
                Text("Are you sure you want to remove FSSAI License #${licenseToDelete?.licenseNumber} (${licenseToDelete?.companyName}) from the local Room database?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        licenseToDelete?.let { viewModel.deleteFssaiLicense(it.licenseNumber) }
                        licenseToDelete = null
                        Toast.makeText(context, "License record deleted", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { licenseToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun FssaiLicenseCard(
    license: FssaiLicenseEntity,
    onCopyNumber: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    forceExpand: Boolean = false,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember(forceExpand) { mutableStateOf(forceExpand) }

    val daysRemaining = remember(license.expiryDate) { getDaysRemaining(license.expiryDate) }
    val isExpiringSoon = remember(license.expiryDate, license.status) {
        isExpiringSoonWithin30Days(license.expiryDate, license.status)
    }

    val isOk = license.status == "ACTIVE"
    val isSuspended = license.status == "SUSPENDED"

    val statusBg = when {
        isOk -> Color(0xFF064E3B)
        isSuspended -> Color(0xFF451A03)
        else -> Color(0xFF450A0A)
    }

    val statusText = when {
        isOk -> Color(0xFF10B981)
        isSuspended -> Color(0xFFF59E0B)
        else -> Color(0xFFEF4444)
    }

    val cardBorder = when {
        isExpanded -> MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
        isExpiringSoon -> Color(0xFFF59E0B)
        else -> MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
    }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isExpiringSoon) Color(0xFF451A03).copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(if (isExpiringSoon) 1.5.dp else 1.dp, cardBorder),
        modifier = modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded }
            .testTag("fssai_license_card_${license.licenseNumber}")
    ) {
        Column {
            // Background Check Warning Ribbon for Expiring Soon
            if (isExpiringSoon) {
                Surface(
                    color = Color(0xFFF59E0B).copy(alpha = 0.2f),
                    border = BorderStroke(1.dp, Color(0xFFF59E0B).copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = Color(0xFFF59E0B),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "BACKGROUND CHECK FLAG: Expiring in ${daysRemaining ?: 0} Days (Expiry: ${license.expiryDate})",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFF59E0B),
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(14.dp)) {
                // Header Row: License Number, Copy, Status & Expand Chevron
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // License Number & Copy
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = license.licenseNumber,
                                fontFamily = AppFontFamily,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.titleSmall,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        IconButton(
                            onClick = onCopyNumber,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy License Number",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (isExpiringSoon) {
                            Surface(
                                color = Color(0xFFF59E0B).copy(alpha = 0.2f),
                                shape = RoundedCornerShape(20.dp),
                                border = BorderStroke(1.dp, Color(0xFFF59E0B))
                            ) {
                                Text(
                                    text = "EXPIRING (${daysRemaining}d)",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFF59E0B),
                                    fontSize = 10.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                        }

                        // Status Badge
                        Surface(
                            color = statusBg,
                            shape = RoundedCornerShape(20.dp),
                            border = BorderStroke(1.dp, statusText.copy(alpha = 0.5f))
                        ) {
                            Text(
                                text = license.status,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = statusText,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(
                        onClick = { isExpanded = !isExpanded },
                        modifier = Modifier.size(28.dp).testTag("expand_card_button_${license.licenseNumber}")
                    ) {
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = if (isExpanded) "Collapse Details" else "Expand Details",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Company & Brand Name
            Text(
                text = license.companyName,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (license.brandName.isNotBlank()) {
                Text(
                    text = "Brand: ${license.brandName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Summary Details Line
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Business,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${license.licenseType} • ${license.kindOfBusiness}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = license.premisesAddress,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Badges row: +F Fortified, Expiry, State
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (license.isFortifiedCertified) {
                    Surface(
                        color = Color(0xFF451A03),
                        shape = RoundedCornerShape(6.dp),
                        border = BorderStroke(1.dp, Color(0xFFF59E0B))
                    ) {
                        Text(
                            text = "+F FORTIFIED CERTIFIED",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFF59E0B),
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "Exp: ${license.expiryDate}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = license.state,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            // Expandable Detailed Information View
            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    androidx.compose.material3.HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    if (isExpiringSoon) {
                        Surface(
                            color = Color(0xFF451A03).copy(alpha = 0.35f),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, Color(0xFFF59E0B).copy(alpha = 0.6f)),
                            modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = Color(0xFFF59E0B),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "AUTOMATED BACKGROUND CHECK FLAG",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFF59E0B)
                                    )
                                    Text(
                                        text = "License expires in ${daysRemaining ?: 0} days on ${license.expiryDate}. Pursuant to FSS Rule 2.1.7, Food Business Operators must submit Form B renewal prior to 30 days of expiry on FoSCoS to avoid Rs. 100/day late penalty fee.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }

                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Full FSSAI License & Compliance Dossier",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            // Field: Authorized Categories
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Category,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Authorized Food Categories",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = license.foodCategories.ifBlank { "Uncategorized" },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            // Field: Business Premises Address
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Full Operating Premises Address",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${license.premisesAddress}, ${license.state}, India",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            // Field Grid: License Type & Business Kind
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "License Scope",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = license.licenseType,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Kind of Business",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = license.kindOfBusiness,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }

                            // Field Grid: Expiry Date & Fortified Status
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.DateRange,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Expiry Date",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Text(
                                        text = license.expiryDate,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (isOk) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.error
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "+F Fortified Logo",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = if (license.isFortifiedCertified) "Certified & Authorized" else "Not Applicable",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (license.isFortifiedCertified) Color(0xFFF59E0B) else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            // Field: Registry Remarks
                            if (license.remarks.isNotBlank()) {
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Assignment,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Verification & Audit Remarks",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = license.remarks,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Footer Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isExpanded) "Tap card to collapse" else "Tap card for full dossier",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 10.sp
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedButton(
                        onClick = onEdit,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Edit", fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
}

@Composable
private fun StatCard(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = color.copy(alpha = 0.12f),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, color.copy(alpha = 0.3f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 10.sp
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FssaiAddEditDialog(
    existingLicense: FssaiLicenseEntity?,
    onDismiss: () -> Unit,
    onSave: (FssaiLicenseEntity) -> Unit
) {
    var licenseNumber by remember { mutableStateOf(existingLicense?.licenseNumber ?: "") }
    var companyName by remember { mutableStateOf(existingLicense?.companyName ?: "") }
    var brandName by remember { mutableStateOf(existingLicense?.brandName ?: "") }
    var premisesAddress by remember { mutableStateOf(existingLicense?.premisesAddress ?: "") }
    var licenseType by remember { mutableStateOf(existingLicense?.licenseType ?: "Central License") }
    var kindOfBusiness by remember { mutableStateOf(existingLicense?.kindOfBusiness ?: "Manufacturer & Packer") }
    var state by remember { mutableStateOf(existingLicense?.state ?: "Delhi") }
    var expiryDate by remember { mutableStateOf(existingLicense?.expiryDate ?: "2028-12-31") }
    var status by remember { mutableStateOf(existingLicense?.status ?: "ACTIVE") }
    var foodCategories by remember { mutableStateOf(existingLicense?.foodCategories ?: "01 - Dairy, 05 - Bakery") }
    var isFortified by remember { mutableStateOf(existingLicense?.isFortifiedCertified ?: false) }
    var remarks by remember { mutableStateOf(existingLicense?.remarks ?: "License verified and registered in Room DB.") }

    var showOcrScanner by remember { mutableStateOf(false) }
    var ocrStatusMessage by remember { mutableStateOf<String?>(null) }

    if (showOcrScanner) {
        FssaiOcrScannerDialog(
            onDismiss = { showOcrScanner = false },
            onScanResult = { scannedLicense ->
                licenseNumber = scannedLicense.licenseNumber
                companyName = scannedLicense.companyName
                brandName = scannedLicense.brandName
                premisesAddress = scannedLicense.premisesAddress
                licenseType = scannedLicense.licenseType
                kindOfBusiness = scannedLicense.kindOfBusiness
                state = scannedLicense.state
                expiryDate = scannedLicense.expiryDate
                status = scannedLicense.status
                foodCategories = scannedLicense.foodCategories
                isFortified = scannedLicense.isFortifiedCertified
                remarks = "Auto-populated via OCR Scan of physical FSSAI Form C certificate."
                ocrStatusMessage = "Fields auto-populated via OCR document scan (99.2% confidence)"
                showOcrScanner = false
            }
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (existingLicense != null) "Edit FSSAI License Record" else "Register New FSSAI License",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // OCR Document Scanner Trigger Banner
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showOcrScanner = true }
                        .testTag("ocr_scan_document_button")
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.primary,
                            shape = CircleShape,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.QrCode,
                                    contentDescription = "Scan Document OCR",
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Scan Physical License Document (OCR)",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "Camera / File OCR auto-extracts FSSAI #, business details & expiry",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                if (ocrStatusMessage != null) {
                    Surface(
                        color = Color(0xFF10B981).copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = ocrStatusMessage!!,
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF10B981),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = licenseNumber,
                    onValueChange = { licenseNumber = it },
                    label = { Text("14-Digit FSSAI License No.*") },
                    enabled = existingLicense == null,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = companyName,
                    onValueChange = { companyName = it },
                    label = { Text("Company / Manufacturer Name*") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = brandName,
                    onValueChange = { brandName = it },
                    label = { Text("Brand Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = premisesAddress,
                    onValueChange = { premisesAddress = it },
                    label = { Text("Premises / Unit Address") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = licenseType,
                        onValueChange = { licenseType = it },
                        label = { Text("License Type") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    OutlinedTextField(
                        value = status,
                        onValueChange = { status = it },
                        label = { Text("Status (ACTIVE/EXPIRED)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = state,
                        onValueChange = { state = it },
                        label = { Text("State / UT") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    OutlinedTextField(
                        value = expiryDate,
                        onValueChange = { expiryDate = it },
                        label = { Text("Expiry Date") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = foodCategories,
                    onValueChange = { foodCategories = it },
                    label = { Text("Authorized Food Categories") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isFortified = !isFortified }
                        .padding(vertical = 4.dp)
                ) {
                    androidx.compose.material3.Checkbox(
                        checked = isFortified,
                        onCheckedChange = { isFortified = it }
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Authorized for +F Fortified Logo",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }

                OutlinedTextField(
                    value = remarks,
                    onValueChange = { remarks = it },
                    label = { Text("Verification Remarks") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (licenseNumber.isNotBlank() && companyName.isNotBlank()) {
                        val item = FssaiLicenseEntity(
                            licenseNumber = licenseNumber.trim(),
                            companyName = companyName.trim(),
                            brandName = brandName.trim(),
                            premisesAddress = premisesAddress.trim(),
                            licenseType = licenseType.trim(),
                            kindOfBusiness = kindOfBusiness.trim(),
                            state = state.trim(),
                            expiryDate = expiryDate.trim(),
                            status = status.trim().uppercase(),
                            foodCategories = foodCategories.trim(),
                            isFortifiedCertified = isFortified,
                            remarks = remarks.trim()
                        )
                        onSave(item)
                    }
                },
                enabled = licenseNumber.isNotBlank() && companyName.isNotBlank()
            ) {
                Text("Save to Room DB")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun FssaiOcrScannerDialog(
    onDismiss: () -> Unit,
    onScanResult: (FssaiLicenseEntity) -> Unit
) {
    var selectedPresetIndex by remember { mutableStateOf(0) }
    var isScanning by remember { mutableStateOf(false) }
    var scanCompleted by remember { mutableStateOf(false) }

    val ocrPresets = remember {
        listOf(
            FssaiLicenseEntity(
                licenseNumber = "10014011001890",
                companyName = "Mother Dairy Fruit & Vegetable Pvt. Ltd.",
                brandName = "Mother Dairy",
                premisesAddress = "Patparganj Industrial Area, Phase-I, New Delhi - 110092",
                licenseType = "Central License",
                kindOfBusiness = "Manufacturer & Dairy Processing Plant",
                state = "Delhi",
                issueDate = "2014-06-15",
                expiryDate = "2029-06-14",
                status = "ACTIVE",
                foodCategories = "01 - Dairy products and analogues, 05 - Confectionery",
                isFortifiedCertified = true,
                fssaiLogoVerifiedOnPack = true,
                contactEmail = "quality@motherdairy.com",
                contactPhone = "+91 11 2244 1000",
                remarks = "OCR Extracted: Verified Central License Form C Certificate."
            ),
            FssaiLicenseEntity(
                licenseNumber = "10017051000344",
                companyName = "Haldiram Snacks Private Limited",
                brandName = "Haldiram's Nagpur",
                premisesAddress = "Plot No. 145, Sector 68, IMT Manesar, Gurugram, Haryana - 122050",
                licenseType = "State License",
                kindOfBusiness = "Manufacturer - Sweets & Namkeen Snacks",
                state = "Haryana",
                issueDate = "2017-04-10",
                expiryDate = "2028-04-09",
                status = "ACTIVE",
                foodCategories = "05 - Confectionery & Savoury Snacks",
                isFortifiedCertified = false,
                fssaiLogoVerifiedOnPack = true,
                contactEmail = "qa.manesar@haldiram.com",
                contactPhone = "+91 124 478 9000",
                remarks = "OCR Extracted: Validated Form C State License Certificate."
            ),
            FssaiLicenseEntity(
                licenseNumber = "10019022000811",
                companyName = "Ferrero India Private Limited",
                brandName = "Ferrero Rocher / Nutella",
                premisesAddress = "Plot No. F-1, MIDC Baramati, Pune, Maharashtra - 413133",
                licenseType = "Central License",
                kindOfBusiness = "Importer & Wholesaler",
                state = "Maharashtra",
                issueDate = "2019-11-20",
                expiryDate = "2029-11-19",
                status = "ACTIVE",
                foodCategories = "05 - Chocolate & Cocoa Products",
                isFortifiedCertified = false,
                fssaiLogoVerifiedOnPack = true,
                contactEmail = "compliance.india@ferrero.com",
                contactPhone = "+91 2112 66 1000",
                remarks = "OCR Extracted: Central License for Confectionery Imports."
            )
        )
    }

    val currentPreset = ocrPresets[selectedPresetIndex]

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.QrCode,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "OCR Physical License Scanner",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Select or capture a physical FSSAI license document to perform Optical Character Recognition (OCR) optical text parsing:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Certificate Preset selector
                Column {
                    Text(
                        text = "Physical Document Samples:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    ocrPresets.forEachIndexed { index, preset ->
                        Surface(
                            color = if (selectedPresetIndex == index) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(
                                1.dp,
                                if (selectedPresetIndex == index) MaterialTheme.colorScheme.primary else Color.Transparent
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp)
                                .clickable {
                                    selectedPresetIndex = index
                                    scanCompleted = false
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Assignment,
                                    contentDescription = null,
                                    tint = if (selectedPresetIndex == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = preset.companyName,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "FSSAI #: ${preset.licenseNumber} • ${preset.licenseType}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // Camera Viewfinder Simulation Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.Black),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(12.dp)
                        ) {
                            if (isScanning) {
                                CircularProgressIndicator(
                                    color = Color(0xFF10B981),
                                    strokeWidth = 3.dp,
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Scanning Document OCR Text...",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color(0xFF10B981),
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Parsing License #, FBO Name, Address & Dates",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 10.sp
                                )
                            } else if (scanCompleted) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color(0xFF10B981),
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "OCR Scan Completed (99.2% Confidence)",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color(0xFF10B981),
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${currentPreset.companyName} (${currentPreset.licenseNumber})",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White,
                                    fontSize = 11.sp
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Live Optical Camera Scanner Ready",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Align physical FSSAI Form C certificate within frame",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }

                if (!scanCompleted && !isScanning) {
                    Button(
                        onClick = {
                            isScanning = true
                            // Simulate fast OCR processing delay
                            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                                isScanning = false
                                scanCompleted = true
                            }, 800)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.QrCode, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Start Document OCR Scan")
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onScanResult(currentPreset) },
                enabled = scanCompleted || !isScanning,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
            ) {
                Text("Auto-Populate Entry Form", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun FssaiSyncStatusIndicator(
    networkState: com.example.data.models.NetworkConnectivityState,
    pendingFssaiCount: Int,
    onTriggerSync: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isConnected = networkState.isConnected
    val isSyncing = networkState.status == com.example.data.models.ConnectivityStatus.SYNCING || networkState.isApiProcessing
    val isFullySynced = isConnected && pendingFssaiCount == 0 && !isSyncing

    val cardBg = when {
        isSyncing -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
        isFullySynced -> Color(0xFF064E3B).copy(alpha = 0.25f)
        pendingFssaiCount > 0 -> Color(0xFF451A03).copy(alpha = 0.25f)
        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
    }

    val borderStrokeColor = when {
        isSyncing -> MaterialTheme.colorScheme.primary
        isFullySynced -> Color(0xFF10B981)
        pendingFssaiCount > 0 -> Color(0xFFF59E0B)
        else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
    }

    val statusTitle = when {
        isSyncing -> "Syncing Local Records with Remote API..."
        isFullySynced -> "Local Database Synced with Remote API"
        pendingFssaiCount > 0 -> "$pendingFssaiCount Un-synced Local FSSAI Modifications"
        !isConnected -> "Offline Mode — Local Room Database Active"
        else -> "FSSAI Remote API Sync Engine"
    }

    val statusBadgeText = when {
        isSyncing -> "SYNCING..."
        isFullySynced -> "100% REMOTE SYNCED"
        pendingFssaiCount > 0 -> "PENDING REMOTE SYNC"
        else -> "OFFLINE LOCAL CACHE"
    }

    val statusBadgeColor = when {
        isSyncing -> MaterialTheme.colorScheme.primary
        isFullySynced -> Color(0xFF10B981)
        pendingFssaiCount > 0 -> Color(0xFFF59E0B)
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    val formattedTime = remember(networkState.lastSyncTimestamp) {
        if (networkState.lastSyncTimestamp > 0) {
            val sdf = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault())
            sdf.format(java.util.Date(networkState.lastSyncTimestamp))
        } else {
            "Just now"
        }
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = cardBg),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, borderStrokeColor.copy(alpha = 0.6f)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("fssai_sync_indicator_card")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    if (isSyncing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp).testTag("fssai_sync_spinner"),
                            strokeWidth = 2.5.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    } else if (isFullySynced) {
                        Icon(
                            imageVector = Icons.Default.CloudDone,
                            contentDescription = "Synced Icon",
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(24.dp)
                        )
                    } else if (pendingFssaiCount > 0) {
                        Icon(
                            imageVector = Icons.Default.CloudUpload,
                            contentDescription = "Un-synced Changes Icon",
                            tint = Color(0xFFF59E0B),
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.CloudOff,
                            contentDescription = "Offline Mode Icon",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = statusTitle,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (isFullySynced) {
                                "Remote Gateway (api.fssai.gov.in) • Last sync: $formattedTime"
                            } else if (pendingFssaiCount > 0) {
                                "$pendingFssaiCount local updates queued for remote API sync"
                            } else {
                                "Operating on local Room DB cache. Auto-syncs when online."
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Surface(
                    color = statusBadgeColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, statusBadgeColor.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = statusBadgeText,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = statusBadgeColor,
                        fontSize = 10.sp,
                        modifier = Modifier
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .testTag("fssai_sync_badge")
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Sync Control Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Remote API Endpoint",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 10.sp
                    )
                    Text(
                        text = "https://api.fssai.gov.in/v2/licenses",
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = com.example.ui.theme.AppFontFamily,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 11.sp
                    )
                }

                OutlinedButton(
                    onClick = onTriggerSync,
                    enabled = isConnected && !isSyncing,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.testTag("trigger_remote_fssai_sync_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Sync Now",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isSyncing) "Syncing..." else "Sync Remote API",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun FssaiExpiringSoonAlertBanner(
    expiringLicenses: List<FssaiLicenseEntity>,
    onFilterExpiringSoon: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF451A03).copy(alpha = 0.35f)),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color(0xFFF59E0B)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("fssai_expiring_soon_alert_card")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = Color(0xFFF59E0B),
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Automated Background Check Flag",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFF59E0B)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "${expiringLicenses.size} license record(s) expire within the mandatory 30-day renewal window (FSS Rule 2.1.7). Food business operators must submit Form B on FoSCoS.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Urgent Renewal Compliance Action",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp
                )
                Button(
                    onClick = onFilterExpiringSoon,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("view_expiring_licenses_button")
                ) {
                    Text("Filter Expiring (${expiringLicenses.size})", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
    }
}

private fun getDaysRemaining(expiryDateStr: String): Long? {
    return try {
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        val expiryDate = sdf.parse(expiryDateStr.trim()) ?: return null
        val today = java.util.Date()
        val diffMillis = expiryDate.time - today.time
        java.util.concurrent.TimeUnit.MILLISECONDS.toDays(diffMillis)
    } catch (e: Exception) {
        null
    }
}

private fun isExpiringSoonWithin30Days(expiryDateStr: String, status: String): Boolean {
    if (status == "EXPIRED" || status == "SUSPENDED" || status == "CANCELLED") return false
    val days = getDaysRemaining(expiryDateStr) ?: return false
    return days in 0L..30L
}

@Composable
private fun FssaiQrCodeScannerDialog(
    onDismiss: () -> Unit,
    onQrScanned: (String) -> Unit
) {
    var rawQrInput by remember { mutableStateOf("") }
    var selectedSampleIndex by remember { mutableStateOf<Int?>(0) }
    var isScanning by remember { mutableStateOf(false) }
    var scanSuccess by remember { mutableStateOf(false) }
    var scannedLicenseNo by remember { mutableStateOf("10012021000071") }

    val qrDocumentSamples = remember {
        listOf(
            Triple("Gujarat Cooperative Milk Marketing Federation (Amul)", "10012021000071", "https://foscos.fssai.gov.in/check/10012021000071"),
            Triple("Nestle India Limited (Maggi & KitKat)", "10012011000168", "https://foscos.fssai.gov.in/check/10012011000168"),
            Triple("Britannia Industries Limited", "10015043001124", "https://foscos.fssai.gov.in/check/10015043001124"),
            Triple("Mother Dairy Fruit & Vegetable Pvt. Ltd.", "10014011001890", "https://foscos.fssai.gov.in/check/10014011001890")
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.QrCode,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "FSSAI Document QR Code Scanner",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Scan the official QR validation code printed on physical FSSAI Form C certificates, licenses, or food package labels:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Simulated Viewfinder Box
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.Black),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, if (scanSuccess) Color(0xFF10B981) else MaterialTheme.colorScheme.primary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(12.dp)
                        ) {
                            if (isScanning) {
                                CircularProgressIndicator(
                                    color = Color(0xFF10B981),
                                    strokeWidth = 3.dp,
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Reading QR Matrix & Validating Code...",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color(0xFF10B981),
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Extracting 14-Digit FSSAI License #",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 10.sp
                                )
                            } else if (scanSuccess) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color(0xFF10B981),
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "QR CODE VALIDATED",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color(0xFF10B981),
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "License #: $scannedLicenseNo",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Redirecting to detail view...",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = 10.sp
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.QrCode,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Target Physical FSSAI QR Code in Frame",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Reads FoSCoS portal URLs & raw 14-digit codes",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }

                // Sample physical document QR code presets
                Column {
                    Text(
                        text = "Physical Certificate QR Samples:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    qrDocumentSamples.forEachIndexed { index, (fboName, licNo, qrUrl) ->
                        Surface(
                            color = if (selectedSampleIndex == index) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(
                                1.dp,
                                if (selectedSampleIndex == index) MaterialTheme.colorScheme.primary else Color.Transparent
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp)
                                .clickable {
                                    selectedSampleIndex = index
                                    scannedLicenseNo = licNo
                                    scanSuccess = false
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.QrCode,
                                    contentDescription = null,
                                    tint = if (selectedSampleIndex == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = fboName,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "QR Payload: $qrUrl",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 10.sp,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }

                // Custom QR Code Payload Input
                OutlinedTextField(
                    value = rawQrInput,
                    onValueChange = {
                        rawQrInput = it
                        selectedSampleIndex = null
                        scanSuccess = false
                    },
                    label = { Text("Or enter raw QR URL / payload") },
                    placeholder = { Text("e.g. https://foscos.fssai.gov.in/check/10012021000071") },
                    singleLine = true,
                    trailingIcon = {
                        if (rawQrInput.isNotEmpty()) {
                            IconButton(onClick = { rawQrInput = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("qr_raw_input")
                )

                if (!scanSuccess && !isScanning) {
                    Button(
                        onClick = {
                            isScanning = true
                            val targetNo = if (rawQrInput.isNotBlank()) {
                                val regex = Regex("""\d{14}""")
                                regex.find(rawQrInput)?.value ?: rawQrInput.trim()
                            } else {
                                scannedLicenseNo
                            }
                            scannedLicenseNo = targetNo

                            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                                isScanning = false
                                scanSuccess = true
                            }, 600)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("trigger_qr_scan_action")
                    ) {
                        Icon(Icons.Default.QrCode, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Simulate Camera QR Code Scan")
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onQrScanned(scannedLicenseNo) },
                enabled = scanSuccess || !isScanning,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                modifier = Modifier.testTag("confirm_qr_redirect_button")
            ) {
                Text("Redirect to Detail View", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

