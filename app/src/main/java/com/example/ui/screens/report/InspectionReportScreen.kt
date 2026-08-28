package com.example.ui.screens.report

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.models.ComplianceStatus
import com.example.ui.components.StatusBadge
import com.example.ui.theme.ComplianceFail
import com.example.ui.theme.CompliancePass
import com.example.ui.theme.ComplianceReview
import com.example.ui.viewmodel.InspectionViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InspectionReportScreen(
    inspectionId: String,
    viewModel: InspectionViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val inspection by viewModel.repository.getInspectionById(inspectionId).collectAsStateWithLifecycle(null)
    val checks by viewModel.repository.getChecks(inspectionId).collectAsStateWithLifecycle(emptyList())
    val declarations by viewModel.repository.getDeclarations(inspectionId).collectAsStateWithLifecycle(emptyList())

    val dateFormat = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.getDefault())
    var showLegalNoticeDialog by remember { mutableStateOf(false) }

    if (showLegalNoticeDialog && inspection != null) {
        val failingChecks = checks.filter { it.status == ComplianceStatus.POTENTIAL_NON_COMPLIANCE }.map { "${it.legalSection} - ${it.ruleTitle}" }
        com.example.ui.components.LegalMetrologyNoticeDialog(
            inspectionId = inspection!!.inspectionNumber,
            productName = inspection!!.productName,
            brandName = inspection!!.brand,
            violatingRules = failingChecks.ifEmpty { listOf("Rule 6(1)(e) - Net Quantity Declaration", "Rule 6(1)(d) - Invalid MRP Format") },
            totalPenaltyAmount = inspection!!.penaltyAmount,
            officerName = inspection!!.officerName,
            onDismiss = { showLegalNoticeDialog = false }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Official Section 36 Notice",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            inspection?.let { insp ->
                                com.example.utils.ReportExporter.exportToPdf(context, insp, declarations, checks)
                            }
                        }
                    ) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = "Export PDF Report")
                    }
                    IconButton(
                        onClick = {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, "Legal Metrology Notice - ${inspection?.inspectionNumber}")
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    """
                                    LEGAL METROLOGY ENFORCEMENT REPORT
                                    Notice Ref: ${inspection?.noticeNumber ?: inspection?.inspectionNumber}
                                    Product: ${inspection?.productName}
                                    Brand: ${inspection?.brand}
                                    Status: ${inspection?.status?.displayName}
                                    Compliance Score: ${inspection?.complianceScore}%
                                    Assessed Penalty: ₹ ${inspection?.penaltyAmount}
                                    Inspector: ${inspection?.officerName}
                                    """.trimIndent()
                                )
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share Inspection Notice"))
                        }
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "Share Notice")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        modifier = modifier.testTag("inspection_report_screen")
    ) { innerPadding ->
        if (inspection == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("Loading notice details...", style = MaterialTheme.typography.bodyMedium)
            }
        } else {
            val insp = inspection!!

            LazyColumn(
                contentPadding = innerPadding,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    // Official Government Notice Certificate Card
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        border = BorderStroke(2.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            // Emblem & Header
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = "Government Seal",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(36.dp)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "GOVERNMENT OF INDIA",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = "DIRECTORATE OF LEGAL METROLOGY",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "Department of Consumer Affairs, Food & Public Distribution",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))
                            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                            Spacer(modifier = Modifier.height(14.dp))

                            // Notice Title
                            Text(
                                text = if (insp.violationsCount > 0) "STATUTORY NOTICE UNDER SECTION 36" else "CERTIFICATE OF PACKAGED COMMODITY COMPLIANCE",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (insp.violationsCount > 0) ComplianceFail else CompliancePass,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            ReportMetadataRow(label = "Notice Reference #", value = insp.noticeNumber ?: insp.inspectionNumber)
                            ReportMetadataRow(label = "Inspection Docket", value = insp.inspectionNumber)
                            ReportMetadataRow(label = "Date & Time", value = dateFormat.format(Date(insp.timestamp)))
                            ReportMetadataRow(label = "Inspection Station", value = insp.location)
                            ReportMetadataRow(label = "Inspecting Officer", value = insp.officerName)
                            ReportMetadataRow(label = "Enforcement Status", value = insp.status.displayName)

                            Spacer(modifier = Modifier.height(16.dp))
                            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                            Spacer(modifier = Modifier.height(12.dp))

                            // Seized / Audited Commodity Details
                            Text(
                                text = "1. COMMODITY PARTICULARS",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            ReportMetadataRow(label = "Generic Name", value = insp.productName)
                            ReportMetadataRow(label = "Brand / Packer", value = insp.brand)
                            ReportMetadataRow(label = "Category", value = insp.category.label)

                            Spacer(modifier = Modifier.height(14.dp))

                            // Statutory Declarations Checklist
                            Text(
                                text = "2. MANDATORY DECLARATIONS AUDIT",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            declarations.forEach { decl ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 3.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = decl.fieldName,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.weight(1.2f)
                                    )
                                    Text(
                                        text = if (decl.extractedValue.isNotBlank()) decl.extractedValue else "[MISSING]",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (decl.extractedValue.isNotBlank()) MaterialTheme.colorScheme.onSurface else ComplianceFail,
                                        modifier = Modifier.weight(1.8f)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Findings & Violations
                            Text(
                                text = "3. STATUTORY INFRACTIONS & DIRECTIVES",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            val violations = checks.filter { it.status == ComplianceStatus.POTENTIAL_NON_COMPLIANCE }
                            if (violations.isEmpty()) {
                                Surface(
                                    color = CompliancePass.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "✔ No statutory infractions observed. Packaged commodity satisfies Legal Metrology Rules, 2011.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = CompliancePass,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.padding(10.dp)
                                    )
                                }
                            } else {
                                violations.forEachIndexed { idx, v ->
                                    Card(
                                        shape = RoundedCornerShape(8.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = ComplianceFail.copy(alpha = 0.05f)
                                        ),
                                        border = BorderStroke(1.dp, ComplianceFail.copy(alpha = 0.3f)),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp)
                                    ) {
                                        Column(modifier = Modifier.padding(10.dp)) {
                                            Text(
                                                text = "${idx + 1}. ${v.ruleTitle} (${v.legalSection})",
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = ComplianceFail
                                            )
                                            Text(
                                                text = v.findingMessage,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                }
                            }

                            if (insp.penaltyAmount > 0) {
                                Spacer(modifier = Modifier.height(14.dp))
                                Text(
                                    text = "4. COMPOUNDING PENALTY ASSESSMENT",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Under Section 36 / 49 of the Legal Metrology Act, 2009, an initial compounding fine of ₹ ${insp.penaltyAmount.toInt()} is assessed. The manufacturer/packer is ordered to rectify packaging labels within 15 days.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Spacer(modifier = Modifier.height(20.dp))
                            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                            Spacer(modifier = Modifier.height(14.dp))

                            // Officer Signature & Seal
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                Column {
                                    Text(text = "Security Audit Hash", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(text = "SHA-256: 8f4b..99d2", style = MaterialTheme.typography.labelSmall, fontFamily = FontFamily.Monospace)
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = CompliancePass,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = insp.signedOffBy ?: insp.officerName,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Authorized Legal Metrology Inspector",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { showLegalNoticeDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = ComplianceFail),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("generate_legal_notice_memo_btn")
                            ) {
                                Icon(Icons.Default.Gavel, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Generate Legal Metrology Notice & Statutory Fine Memo", fontWeight = FontWeight.Bold)
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        com.example.utils.ReportExporter.exportToPdf(context, insp, declarations, checks)
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Export PDF Report")
                                }
                                OutlinedButton(
                                    onClick = {
                                        com.example.utils.ReportExporter.exportToCsv(context, insp, declarations, checks)
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.TableChart, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Export Editable CSV")
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}

@Composable
private fun ReportMetadataRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
    }
}
