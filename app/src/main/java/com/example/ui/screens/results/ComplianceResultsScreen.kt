package com.example.ui.screens.results

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.Crop
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.entities.ComplianceCheckEntity
import com.example.data.local.entities.DeclarationEntity
import com.example.data.local.entities.InspectionEntity
import com.example.data.models.ComplianceStatus
import com.example.data.models.UserRole
import com.example.ui.components.LegalDisclaimerNotice
import com.example.ui.components.SeverityBadge
import com.example.ui.components.StatusBadge
import com.example.ui.theme.ComplianceFail
import com.example.ui.theme.CompliancePass
import com.example.ui.theme.ComplianceReview
import com.example.ui.viewmodel.InspectionViewModel
import com.example.data.ai.ProductIntelligenceService
import com.example.ui.components.ProductIntelligenceReportView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComplianceResultsScreen(
    inspectionId: String,
    viewModel: InspectionViewModel,
    onBack: () -> Unit,
    onNavigateEvidence: (String) -> Unit,
    onNavigateReview: (String) -> Unit,
    onNavigateReport: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val isStandardUser = currentUser.role == UserRole.STANDARD_USER

    val inspection by viewModel.repository.getInspectionById(inspectionId).collectAsStateWithLifecycle(null)
    val checks by viewModel.repository.getChecks(inspectionId).collectAsStateWithLifecycle(emptyList())
    val declarations by viewModel.repository.getDeclarations(inspectionId).collectAsStateWithLifecycle(emptyList())

    val productIntelReport = remember(inspection, declarations) {
        val currentInsp = inspection
        if (currentInsp != null) {
            ProductIntelligenceService.resolveProductIntelligence(
                queryOrBarcode = currentInsp.productName,
                scannedDeclarations = declarations
            )
        } else null
    }

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = if (isStandardUser) {
        listOf("Product Intelligence", "Declarations (${declarations.size})", "Verification (${checks.size})", "Summary")
    } else {
        listOf("Product Intelligence", "Rule Findings (${checks.size})", "Declarations (${declarations.size})", "Summary")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (isStandardUser) "Product Package Details" else (inspection?.inspectionNumber ?: "Inspection Results"),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = inspection?.productName ?: "Commodity Audit",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { onNavigateReport(inspectionId) }) {
                        Icon(Icons.Default.Print, contentDescription = "Print Notice / Report")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isStandardUser) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = onBack,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Done")
                        }
                        Button(
                            onClick = { onNavigateReport(inspectionId) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                            modifier = Modifier.weight(1.3f)
                        ) {
                            Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("View Product Report")
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { onNavigateEvidence(inspectionId) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("inspect_evidence_button")
                        ) {
                            Icon(Icons.Default.Crop, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Evidence")
                        }

                        Button(
                            onClick = { onNavigateReview(inspectionId) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("human_review_button")
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Review")
                        }

                        FilledTonalButton(
                            onClick = { onNavigateReport(inspectionId) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("view_notice_report_button")
                        ) {
                            Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Notice")
                        }
                    }
                }
            }
        },
        modifier = modifier
    ) { innerPadding ->
        if (inspection == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("Loading product findings...", style = MaterialTheme.typography.bodyMedium)
            }
        } else {
            val insp = inspection!!

            LazyColumn(
                contentPadding = innerPadding,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    InspectionSummaryHeaderCard(inspection = insp)
                }

                item {
                    LegalDisclaimerNotice()
                }

                item {
                    TabRow(
                        selectedTabIndex = selectedTabIndex,
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.primary
                    ) {
                        tabs.forEachIndexed { index, title ->
                            Tab(
                                selected = selectedTabIndex == index,
                                onClick = { selectedTabIndex = index },
                                text = {
                                    Text(
                                        text = title,
                                        fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                }
                            )
                        }
                    }
                }

                if (isStandardUser) {
                    when (selectedTabIndex) {
                        0 -> {
                            // Product Intelligence Tab
                            item {
                                if (productIntelReport != null) {
                                    ProductIntelligenceReportView(report = productIntelReport)
                                } else {
                                    Text("Generating Product Intelligence...", style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        }
                        1 -> {
                            // Declarations Tab
                            items(declarations) { decl ->
                                DeclarationChecklistItem(declaration = decl)
                            }
                        }
                        2 -> {
                            // Verification Findings
                            items(checks) { check ->
                                RuleFindingCard(
                                    check = check,
                                    onInspectEvidence = { onNavigateEvidence(inspectionId) }
                                )
                            }
                        }
                        3 -> {
                            // Product Summary Tab
                            item {
                                InspectionMetaDetailCard(
                                    inspection = insp,
                                    onNavigateReview = { onNavigateReview(inspectionId) }
                                )
                            }
                        }
                    }
                } else {
                    when (selectedTabIndex) {
                        0 -> {
                            // Product Intelligence Tab
                            item {
                                if (productIntelReport != null) {
                                    ProductIntelligenceReportView(report = productIntelReport)
                                } else {
                                    Text("Generating Product Intelligence...", style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        }
                        1 -> {
                            // Rule Findings Tab
                            items(checks) { check ->
                                RuleFindingCard(
                                    check = check,
                                    onInspectEvidence = { onNavigateEvidence(inspectionId) }
                                )
                            }
                        }
                        2 -> {
                            // Declarations Checklist Tab
                            items(declarations) { decl ->
                                DeclarationChecklistItem(declaration = decl)
                            }
                        }
                        3 -> {
                            // Summary & Meta Tab
                            item {
                                InspectionMetaDetailCard(
                                    inspection = insp,
                                    onNavigateReview = { onNavigateReview(inspectionId) }
                                )
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}

@Composable
private fun InspectionSummaryHeaderCard(inspection: InspectionEntity) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatusBadge(status = inspection.status)
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Score: ${inspection.complianceScore}%",
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = inspection.productName,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "Brand: ${inspection.brand} • Category: ${inspection.category.label}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "Officer", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = inspection.officerName, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "Date & Station", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = dateFormat.format(Date(inspection.timestamp)), style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
private fun RuleFindingCard(
    check: ComplianceCheckEntity,
    onInspectEvidence: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = when (check.status) {
                ComplianceStatus.POTENTIAL_NON_COMPLIANCE -> ComplianceFail.copy(alpha = 0.05f)
                ComplianceStatus.REQUIRES_REVIEW -> ComplianceReview.copy(alpha = 0.05f)
                ComplianceStatus.PASS -> MaterialTheme.colorScheme.surface
                ComplianceStatus.DRAFT -> MaterialTheme.colorScheme.surface
            }
        ),
        border = BorderStroke(
            1.dp,
            when (check.status) {
                ComplianceStatus.POTENTIAL_NON_COMPLIANCE -> ComplianceFail.copy(alpha = 0.4f)
                ComplianceStatus.REQUIRES_REVIEW -> ComplianceReview.copy(alpha = 0.4f)
                ComplianceStatus.PASS -> MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                ComplianceStatus.DRAFT -> MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
            }
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("rule_finding_${check.ruleCode}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = check.ruleCode,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    SeverityBadge(severity = check.severity)
                }
                StatusBadge(status = check.status, isSmall = true)
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = check.ruleTitle,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Statutory Citation: ${check.legalSection}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = check.findingMessage,
                style = MaterialTheme.typography.bodySmall,
                color = if (check.status == ComplianceStatus.POTENTIAL_NON_COMPLIANCE) ComplianceFail else MaterialTheme.colorScheme.onSurface
            )

            if (check.status == ComplianceStatus.POTENTIAL_NON_COMPLIANCE || check.status == ComplianceStatus.REQUIRES_REVIEW) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = ComplianceFail.copy(alpha = 0.1f),
                    border = BorderStroke(1.dp, ComplianceFail.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "⚠️ MISSING / NON-COMPLIANT DECLARATION",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = ComplianceFail
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Field Status: Not Detected / Missing on Package",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Statutory Citation: ${check.legalSection}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Reason Missing: ${check.findingMessage}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        val correctiveAction = when (check.ruleCode) {
                            "LM-PC-6-1-A" -> "Corrective Action: Manufacturer must reprint full physical address with PIN code on package PDP label."
                            "LM-PC-6-1-B" -> "Corrective Action: Prominently declare generic/common name on PDP."
                            "LM-PC-6-1-C" -> "Corrective Action: Declare Net Quantity in metric units (g, kg, ml, l, N) without non-standard abbreviations."
                            "LM-PC-6-1-D" -> "Corrective Action: Print Month & Year of Packing/Manufacture (e.g., MM/YYYY)."
                            "LM-PC-6-1-DA" -> "Corrective Action: State Unit Sale Price in ₹/g or ₹/ml for pre-packed items."
                            "LM-PC-6-1-E" -> "Corrective Action: Print MRP with mandatory tax clause 'MRP ₹ XX.XX (incl. of all taxes)'."
                            "LM-PC-6-1-F" -> "Corrective Action: Provide contact name, phone number, email, and postal address for consumer grievances."
                            else -> "Corrective Action: Relabel or reprint package to include missing statutory declaration prior to retail release."
                        }
                        Text(
                            text = correctiveAction,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            if (check.officerComment != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "Officer Note: ${check.officerComment}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Evidence Confidence: ${(check.evidenceConfidence * 100).toInt()}% • Verified Evidence Only",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "View Bounding Crop →",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { onInspectEvidence() }
                )
            }
        }
    }
}

@Composable
private fun DeclarationChecklistItem(declaration: DeclarationEntity) {
    val isMissing = declaration.extractedValue.isBlank() && declaration.correctedValue == null

    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isMissing) ComplianceFail.copy(alpha = 0.05f) else MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            1.dp,
            if (isMissing) ComplianceFail.copy(alpha = 0.4f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = declaration.fieldName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Surface(
                    color = if (isMissing) ComplianceFail.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = if (isMissing) "NOT DETECTED" else "${(declaration.confidence * 100).toInt()}% OCR",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (isMissing) ComplianceFail else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = when {
                    declaration.correctedValue != null -> declaration.correctedValue
                    declaration.extractedValue.isNotBlank() -> declaration.extractedValue
                    else -> "Not Detected / Not Provided"
                },
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isMissing) FontWeight.Bold else FontWeight.Normal,
                color = if (isMissing) ComplianceFail else MaterialTheme.colorScheme.onSurface
            )

            if (isMissing) {
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(6.dp),
                    border = BorderStroke(1.dp, ComplianceFail.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(
                            text = "Why Missing: OCR camera scan detected no legible value for '${declaration.fieldName}' on package panels.",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Statutory Rule: Mandatory declaration under Rule 6 of Legal Metrology (Packaged Commodities) Rules, 2011.",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Suggested Corrective Action: Re-capture high-resolution photo of package panel or manually verify during Officer Review. Value is never fabricated.",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            if (declaration.correctedValue != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Corrected by Inspector: ${declaration.correctedValue}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun InspectionMetaDetailCard(
    inspection: InspectionEntity,
    onNavigateReview: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Inspection Case Information",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            DetailRow(label = "Inspection Number", value = inspection.inspectionNumber)
            DetailRow(label = "Commodity Name", value = inspection.productName)
            DetailRow(label = "Brand / Manufacturer", value = inspection.brand)
            DetailRow(label = "Statutory Category", value = inspection.category.label)
            DetailRow(label = "Station & Checkpoint", value = inspection.location)
            DetailRow(label = "Rules Engine Version", value = "Legal Metrology Rules Engine v3.4 (2024)")
            DetailRow(label = "Total Rules Evaluated", value = "${inspection.totalRulesChecked}")
            DetailRow(label = "Statutory Violations", value = "${inspection.violationsCount}")
            DetailRow(label = "Compound Fine Assessed", value = "₹ ${inspection.penaltyAmount}")

            if (inspection.noticeNumber != null) {
                DetailRow(label = "Section 36 Notice #", value = inspection.noticeNumber)
            }

            if (inspection.signedOffBy != null) {
                DetailRow(label = "Legal Sign-off", value = inspection.signedOffBy)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onNavigateReview,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Edit, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Open Review & Sign-Off Portal")
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
    }
}
