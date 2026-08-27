package com.example.ui.screens.rules

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.entities.RuleEntity
import com.example.data.models.ProductCategory
import com.example.data.models.RuleSeverity
import com.example.ui.components.ProofMarkLogoBadge
import com.example.ui.components.ResponsiveContainer
import com.example.ui.components.SeverityBadge
import com.example.ui.viewmodel.InspectionViewModel

/**
 * Category Group Helper model for rendering category-based lists in Rule Registry.
 */
data class RuleCategoryGroup(
    val title: String,
    val category: ProductCategory?,
    val icon: ImageVector,
    val rules: List<RuleEntity>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RuleManagementScreen(
    viewModel: InspectionViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val allRules by viewModel.allRules.collectAsStateWithLifecycle()
    
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf<String?>("ALL") } // "ALL", "GENERAL", or ProductCategory.name
    var selectedSeverityFilter by remember { mutableStateOf<RuleSeverity?>(null) } // null = All
    var showAddRuleDialog by remember { mutableStateOf(false) }

    // Filter rules based on search query, category, and severity
    val filteredRules = remember(allRules, searchQuery, selectedCategoryFilter, selectedSeverityFilter) {
        allRules.filter { rule ->
            // 1. Search filter
            val matchesQuery = searchQuery.isBlank() ||
                    rule.ruleCode.contains(searchQuery, ignoreCase = true) ||
                    rule.title.contains(searchQuery, ignoreCase = true) ||
                    rule.description.contains(searchQuery, ignoreCase = true) ||
                    rule.sectionReference.contains(searchQuery, ignoreCase = true) ||
                    rule.legalSource.contains(searchQuery, ignoreCase = true) ||
                    (rule.category?.label?.contains(searchQuery, ignoreCase = true) == true)

            // 2. Category filter
            val matchesCategory = when (selectedCategoryFilter) {
                null, "ALL" -> true
                "GENERAL" -> rule.category == null
                else -> rule.category?.name == selectedCategoryFilter
            }

            // 3. Severity filter
            val matchesSeverity = selectedSeverityFilter == null || rule.severity == selectedSeverityFilter

            matchesQuery && matchesCategory && matchesSeverity
        }
    }

    // Group filtered rules by category
    val categoryGroups = remember(filteredRules) {
        val groups = mutableListOf<RuleCategoryGroup>()

        // 1. General Package Declarations (Rule 6)
        val generalRules = filteredRules.filter { it.category == null }
        if (generalRules.isNotEmpty()) {
            groups.add(
                RuleCategoryGroup(
                    title = "General Package Declarations (Rule 6)",
                    category = null,
                    icon = Icons.Default.Gavel,
                    rules = generalRules
                )
            )
        }

        // 2. Group by each ProductCategory
        ProductCategory.values().forEach { cat ->
            val catRules = filteredRules.filter { it.category == cat }
            if (catRules.isNotEmpty()) {
                val icon = when (cat) {
                    ProductCategory.FOOD_BEVERAGES -> Icons.Default.Inventory2
                    ProductCategory.COSMETICS_PERSONAL_CARE -> Icons.Default.Shield
                    ProductCategory.ELECTRONICS_APPLIANCES -> Icons.Default.Inventory2
                    ProductCategory.CHEMICALS_PESTICIDES -> Icons.Default.Warning
                    ProductCategory.EDIBLE_OILS_GRAINS -> Icons.Default.Inventory2
                    ProductCategory.ECOMMERCE_LISTING -> Icons.Default.Category
                    ProductCategory.GENERAL_MERCHANDISE -> Icons.Default.Inventory2
                }
                groups.add(
                    RuleCategoryGroup(
                        title = cat.label,
                        category = cat,
                        icon = icon,
                        rules = catRules
                    )
                )
            }
        }
        groups
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        ProofMarkLogoBadge(size = 36.dp, showAura = true)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Legal Metrology Rule Registry",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "Statutory Rules Engine v3.4 • Packaged Commodities",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF22C55E),
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("rule_registry_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                actions = {
                    Surface(
                        color = Color(0xFF10B981).copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.35f)),
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        Text(
                            text = "${filteredRules.size} Rules",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF22C55E),
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddRuleDialog = true },
                containerColor = Color(0xFF22C55E),
                contentColor = Color.Black,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.testTag("add_new_rule_fab")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Register New Rule")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Register Rule",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        },
        modifier = modifier.testTag("rule_management_screen")
    ) { innerPadding ->
        ResponsiveContainer(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                
                // 1. Search Bar & Instant Query Filtering
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = {
                                Text(
                                    "Search rules by code, section, title, or category...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search",
                                    tint = Color(0xFF22C55E)
                                )
                            },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { searchQuery = "" }) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = "Clear Search",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF22C55E),
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("rule_search_text_field")
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // 2. Category Filter Chips (Horizontal Scrollable)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            FilterChip(
                                selected = selectedCategoryFilter == "ALL",
                                onClick = { selectedCategoryFilter = "ALL" },
                                label = { Text("All Rules (${allRules.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                                leadingIcon = if (selectedCategoryFilter == "ALL") {
                                    { Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(14.dp)) }
                                } else null,
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFF22C55E),
                                    selectedLabelColor = Color.Black,
                                    selectedLeadingIconColor = Color.Black
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("rule_category_chip_all")
                            )

                            FilterChip(
                                selected = selectedCategoryFilter == "GENERAL",
                                onClick = { selectedCategoryFilter = "GENERAL" },
                                label = { Text("General Rule 6", fontSize = 12.sp) },
                                leadingIcon = if (selectedCategoryFilter == "GENERAL") {
                                    { Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(14.dp)) }
                                } else null,
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFF22C55E),
                                    selectedLabelColor = Color.Black,
                                    selectedLeadingIconColor = Color.Black
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("rule_category_chip_general")
                            )

                            ProductCategory.values().forEach { category ->
                                val isSelected = selectedCategoryFilter == category.name
                                val count = allRules.count { it.category == category }
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { selectedCategoryFilter = if (isSelected) "ALL" else category.name },
                                    label = { Text("${category.label} ($count)", fontSize = 12.sp) },
                                    leadingIcon = if (isSelected) {
                                        { Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(14.dp)) }
                                    } else null,
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Color(0xFF22C55E),
                                        selectedLabelColor = Color.Black,
                                        selectedLeadingIconColor = Color.Black
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.testTag("rule_category_chip_${category.name}")
                                )
                            }
                        }

                        // 3. Severity Filter Pill Bar
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Severity:",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            val severities = listOf(
                                null to "All",
                                RuleSeverity.CRITICAL to "Critical",
                                RuleSeverity.MAJOR to "Major",
                                RuleSeverity.MINOR to "Minor"
                            )

                            severities.forEach { (sev, label) ->
                                val isSelected = selectedSeverityFilter == sev
                                Surface(
                                    color = if (isSelected) {
                                        when (sev) {
                                            RuleSeverity.CRITICAL -> Color(0xFFEF4444)
                                            RuleSeverity.MAJOR -> Color(0xFFF59E0B)
                                            RuleSeverity.MINOR -> Color(0xFF3B82F6)
                                            null -> Color(0xFF22C55E)
                                        }
                                    } else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                    contentColor = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant,
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .clickable { selectedSeverityFilter = sev }
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // 4. Category-Based Rules List
                if (filteredRules.isEmpty()) {
                    // Empty state when search or filter returns 0 matches
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                            ),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FilterList,
                                    contentDescription = null,
                                    tint = Color(0xFF22C55E),
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "No Rules Found",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (searchQuery.isNotBlank()) "No statutory rules match query \"$searchQuery\"." else "No rules match the selected category & severity filters.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                OutlinedButton(
                                    onClick = {
                                        searchQuery = ""
                                        selectedCategoryFilter = "ALL"
                                        selectedSeverityFilter = null
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    border = BorderStroke(1.dp, Color(0xFF22C55E))
                                ) {
                                    Text("Reset Search & Filters", color = Color(0xFF22C55E), fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        // Render each Category Group
                        categoryGroups.forEach { group ->
                            item(key = "group_header_${group.title}") {
                                RuleCategoryHeader(
                                    title = group.title,
                                    icon = group.icon,
                                    count = group.rules.size
                                )
                            }

                            items(group.rules, key = { it.id }) { rule ->
                                RuleRegistryCardItem(
                                    rule = rule,
                                    onToggleRule = { isActive ->
                                        viewModel.toggleRule(rule, isActive)
                                    }
                                )
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(88.dp))
                        }
                    }
                }
            }
        }
    }

    // Modal Dialog for registering new rules into engine
    if (showAddRuleDialog) {
        RegisterNewRuleDialog(
            onDismiss = { showAddRuleDialog = false },
            onConfirm = { newRule ->
                viewModel.addNewRule(newRule)
                showAddRuleDialog = false
            }
        )
    }
}

/**
 * Visual Header for Category Groups in the Rule Registry
 */
@Composable
fun RuleCategoryHeader(
    title: String,
    icon: ImageVector,
    count: Int,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color(0xFF10B981).copy(alpha = 0.10f),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.25f)),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFF22C55E).copy(alpha = 0.2f),
                    modifier = Modifier.size(28.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = Color(0xFF22C55E),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = title.uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF22C55E),
                    letterSpacing = 0.5.sp
                )
            }

            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = "$count Rules",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}

/**
 * Individual Rule Card in the Registry with Expandable Statutory Details
 */
@Composable
fun RuleRegistryCardItem(
    rule: RuleEntity,
    onToggleRule: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            width = 1.dp,
            color = if (rule.isActive) MaterialTheme.colorScheme.outline.copy(alpha = 0.25f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
        ),
        modifier = modifier
            .fillMaxWidth()
            .testTag("rule_item_card_${rule.ruleCode}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Row 1: Code, Severity, Category, & Active Switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        color = Color(0xFF10B981).copy(alpha = 0.15f),
                        shape = RoundedCornerShape(6.dp),
                        border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = rule.ruleCode,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF22C55E),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))
                    SeverityBadge(severity = rule.severity)

                    rule.category?.let { cat ->
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = cat.label,
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                Switch(
                    checked = rule.isActive,
                    onCheckedChange = onToggleRule,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.Black,
                        checkedTrackColor = Color(0xFF22C55E)
                    ),
                    modifier = Modifier.testTag("toggle_switch_${rule.ruleCode}")
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Row 2: Rule Title
            Text(
                text = rule.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (rule.isActive) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )

            // Row 3: Statutory Citation & Section Reference
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 2.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.MenuBook,
                    contentDescription = null,
                    tint = Color(0xFF22C55E),
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${rule.legalSource} • ${rule.sectionReference}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF22C55E),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Row 4: Rule Description
            Text(
                text = rule.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Expandable Statutory Details Accordion Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { expanded = !expanded }
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (expanded) "Hide Statutory Enforcement Details" else "View Statutory Enforcement & Penalties",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF22C55E),
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = Color(0xFF22C55E),
                    modifier = Modifier.size(18.dp)
                )
            }

            // Expanded Statutory Panel
            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    DetailItemRow(
                        label = "Statutory Authority",
                        value = rule.legalSource
                    )
                    DetailItemRow(
                        label = "Section Reference",
                        value = rule.sectionReference
                    )
                    DetailItemRow(
                        label = "Statutory Penalty",
                        value = when (rule.severity) {
                            RuleSeverity.CRITICAL -> "Sec 36(1) / 36(2): Fine up to ₹25,000 (1st offence), ₹50,000 & 1 yr imprisonment (repeat)."
                            RuleSeverity.MAJOR -> "Sec 36(1): Seizure notice & compoundable fine up to ₹10,000."
                            RuleSeverity.MINOR -> "Rule 7 Warning notice with 15-day packaging rectification deadline."
                        }
                    )
                    DetailItemRow(
                        label = "Engine Version",
                        value = "${rule.ruleVersion} (Effective ${rule.effectiveDate})"
                    )
                    DetailItemRow(
                        label = "Verification Status",
                        value = if (rule.isActive) "ACTIVE IN DETERMINISTIC RULES ENGINE" else "INACTIVE / DISABLED BY OFFICER"
                    )
                }
            }
        }
    }
}

@Composable
fun DetailItemRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(0.4f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(0.6f)
        )
    }
}

/**
 * Registration Modal Dialog for Officers to insert new rules into the system database
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterNewRuleDialog(
    onDismiss: () -> Unit,
    onConfirm: (RuleEntity) -> Unit
) {
    var ruleCode by remember { mutableStateOf("LM-PC-CUSTOM-01") }
    var title by remember { mutableStateOf("") }
    var section by remember { mutableStateOf("Rule 6(1)") }
    var description by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<ProductCategory?>(null) }
    var selectedSeverity by remember { mutableStateOf(RuleSeverity.MAJOR) }
    var legalSource by remember { mutableStateOf("Legal Metrology (Packaged Commodities) Rules, 2011") }

    var categoryDropdownExpanded by remember { mutableStateOf(false) }
    var severityDropdownExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                ProofMarkLogoBadge(size = 32.dp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Register Statutory Rule",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = ruleCode,
                    onValueChange = { ruleCode = it },
                    label = { Text("Rule Identifier Code") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Rule Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = section,
                    onValueChange = { section = it },
                    label = { Text("Statutory Section Reference") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Category Selection Dropdown
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = selectedCategory?.label ?: "General Packaging (Rule 6)",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Target Commodity Category") },
                        trailingIcon = { Icon(Icons.Default.ExpandMore, contentDescription = null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { categoryDropdownExpanded = true }
                    )
                    DropdownMenu(
                        expanded = categoryDropdownExpanded,
                        onDismissRequest = { categoryDropdownExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("General Packaging (Rule 6)") },
                            onClick = {
                                selectedCategory = null
                                categoryDropdownExpanded = false
                            }
                        )
                        ProductCategory.values().forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat.label) },
                                onClick = {
                                    selectedCategory = cat
                                    categoryDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                // Severity Dropdown
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = selectedSeverity.name,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Statutory Infraction Severity") },
                        trailingIcon = { Icon(Icons.Default.ExpandMore, contentDescription = null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { severityDropdownExpanded = true }
                    )
                    DropdownMenu(
                        expanded = severityDropdownExpanded,
                        onDismissRequest = { severityDropdownExpanded = false }
                    ) {
                        RuleSeverity.values().forEach { sev ->
                            DropdownMenuItem(
                                text = { Text(sev.name) },
                                onClick = {
                                    selectedSeverity = sev
                                    severityDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = legalSource,
                    onValueChange = { legalSource = it },
                    label = { Text("Legal Act / Rules Source") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Statutory Requirement Description") },
                    minLines = 2,
                    maxLines = 4,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && ruleCode.isNotBlank()) {
                        onConfirm(
                            RuleEntity(
                                id = "rule_${System.currentTimeMillis()}",
                                ruleCode = ruleCode,
                                title = title,
                                description = description,
                                category = selectedCategory,
                                legalSource = legalSource,
                                sectionReference = section,
                                ruleVersion = "v3.4-2026",
                                effectiveDate = "2026-01-01",
                                severity = selectedSeverity,
                                isActive = true
                            )
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF22C55E), contentColor = Color.Black),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Register Rule", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Cancel")
            }
        }
    )
}
