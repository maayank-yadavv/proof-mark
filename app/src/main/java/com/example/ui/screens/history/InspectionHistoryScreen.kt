package com.example.ui.screens.history

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.models.ComplianceStatus
import com.example.data.models.ProductCategory
import com.example.data.models.UserRole
import com.example.ui.components.EmptyStateView
import com.example.ui.components.InspectionItemCard
import com.example.ui.components.ResponsiveContainer
import com.example.ui.viewmodel.InspectionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InspectionHistoryScreen(
    viewModel: InspectionViewModel,
    onBack: () -> Unit,
    onNavigateInspectionDetail: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val isStandardUser = currentUser.role == UserRole.STANDARD_USER

    val inspections by viewModel.filteredInspections.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategoryFilter.collectAsStateWithLifecycle()
    val selectedStatus by viewModel.selectedStatusFilter.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isStandardUser) "My Scanned Products & History" else "Inspection Records & Audit Log",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        modifier = modifier.testTag("inspection_history_screen")
    ) { innerPadding ->
        ResponsiveContainer(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    placeholder = {
                        Text(if (isStandardUser) "Search by product name, brand, or code" else "Search by commodity, brand, or notice #")
                    },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .testTag("history_search_input")
                )

                // Category Filter Chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 6.dp)
                ) {
                    item {
                        FilterChip(
                            selected = selectedCategory == null,
                            onClick = { viewModel.setCategoryFilter(null) },
                            label = { Text("All Categories") }
                        )
                    }
                    items(ProductCategory.entries) { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { viewModel.setCategoryFilter(if (selectedCategory == cat) null else cat) },
                            label = { Text(cat.label) }
                        )
                    }
                }

                // Status Filter Chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = selectedStatus == null,
                            onClick = { viewModel.setStatusFilter(null) },
                            label = { Text("All Statuses") }
                        )
                    }
                    items(ComplianceStatus.entries) { st ->
                        FilterChip(
                            selected = selectedStatus == st,
                            onClick = { viewModel.setStatusFilter(if (selectedStatus == st) null else st) },
                            label = {
                                Text(
                                    if (isStandardUser) {
                                        when (st) {
                                            ComplianceStatus.PASS -> "Verified Authentic"
                                            ComplianceStatus.POTENTIAL_NON_COMPLIANCE -> "Missing Details"
                                            ComplianceStatus.REQUIRES_REVIEW -> "Needs Check"
                                            ComplianceStatus.DRAFT -> "Draft Scan"
                                        }
                                    } else {
                                        st.displayName
                                    }
                                )
                            }
                        )
                    }
                }

                // Results count
                Text(
                    text = "${inspections.size} record${if (inspections.size == 1) "" else "s"} found",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // List
                if (inspections.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        EmptyStateView(
                            title = if (isStandardUser) "No Scanned Products Found" else "No matching inspections",
                            description = if (isStandardUser)
                                "Scan any package QR code, barcode, or label to see your verified history here."
                            else
                                "Try adjusting your search query or filter chips above.",
                            icon = Icons.Default.History
                        )
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(inspections, key = { it.id }) { inspection ->
                            InspectionItemCard(
                                inspection = inspection,
                                onClick = { onNavigateInspectionDetail(inspection.id) },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
        }
    }
}

