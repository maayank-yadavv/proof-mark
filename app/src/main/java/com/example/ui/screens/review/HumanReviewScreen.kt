package com.example.ui.screens.review

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.entities.ComplianceCheckEntity
import com.example.data.models.ComplianceStatus
import com.example.ui.components.LegalDisclaimerNotice
import com.example.ui.components.StatusBadge
import com.example.ui.theme.ComplianceFail
import com.example.ui.theme.CompliancePass
import com.example.ui.theme.ComplianceReview
import com.example.ui.viewmodel.InspectionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HumanReviewScreen(
    inspectionId: String,
    viewModel: InspectionViewModel,
    onBack: () -> Unit,
    onReviewCompleted: () -> Unit,
    modifier: Modifier = Modifier
) {
    val inspection by viewModel.repository.getInspectionById(inspectionId).collectAsStateWithLifecycle(null)
    val checks by viewModel.repository.getChecks(inspectionId).collectAsStateWithLifecycle(emptyList())
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()

    var reviewNotes by remember { mutableStateOf("") }
    var penaltyAmount by remember { mutableDoubleStateOf(inspection?.penaltyAmount ?: 10000.0) }
    var showSignSuccessDialog by remember { mutableStateOf(false) }

    // Dialog state for overriding a check
    var checkToOverride by remember { mutableStateOf<ComplianceCheckEntity?>(null) }
    var overrideStatus by remember { mutableStateOf(ComplianceStatus.PASS) }
    var overrideComment by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Enforcement Review & Sign-Off",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Officer: ${currentUser.name} (${currentUser.badgeNumber})",
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        modifier = modifier.testTag("human_review_screen")
    ) { innerPadding ->
        if (inspection == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("Loading inspection findings...", style = MaterialTheme.typography.bodyMedium)
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
                    LegalDisclaimerNotice()
                }

                item {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "1. Findings Verification & Overrides",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                StatusBadge(status = insp.status, isSmall = true)
                            }
                            Text(
                                text = "Review each automated rule finding. Tap 'Override' to record manual inspector justification.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }

                items(checks, key = { it.id }) { check ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "${check.ruleCode} - ${check.ruleTitle}",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = check.legalSection,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                StatusBadge(status = check.status, isSmall = true)
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = check.findingMessage,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (check.status == ComplianceStatus.POTENTIAL_NON_COMPLIANCE) ComplianceFail else MaterialTheme.colorScheme.onSurface
                            )

                            if (check.officerComment != null) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Inspector Note: ${check.officerComment}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedButton(
                                onClick = {
                                    checkToOverride = check
                                    overrideStatus = if (check.status == ComplianceStatus.PASS) ComplianceStatus.POTENTIAL_NON_COMPLIANCE else ComplianceStatus.PASS
                                    overrideComment = ""
                                },
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Text("Change / Override Status", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }

                item {
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
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "2. Compounding Penalty & Enforcement Decision",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Text(
                                text = "Section 36 fine recommendation: ₹ ${penaltyAmount.toInt()}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )

                            Slider(
                                value = penaltyAmount.toFloat(),
                                onValueChange = { penaltyAmount = (it / 1000).toInt() * 1000.0 },
                                valueRange = 0f..50000f,
                                steps = 9
                            )

                            OutlinedTextField(
                                value = reviewNotes,
                                onValueChange = { reviewNotes = it },
                                label = { Text("Final Inspection Notes & Officer Directives") },
                                placeholder = { Text("e.g., Notice served to manufacturer to rectify MRP format within 15 days under Section 36.") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp),
                                maxLines = 4
                            )

                            Button(
                                onClick = {
                                    viewModel.finalizeInspection(
                                        inspectionId = inspectionId,
                                        penaltyAmount = penaltyAmount,
                                        notes = reviewNotes.ifBlank { "Verified and finalized by ${currentUser.name}." }
                                    )
                                    showSignSuccessDialog = true
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .testTag("sign_inspection_button"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Icon(Icons.Default.Gavel, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Digitally Sign & Issue Enforcement Notice")
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

    // Override Check Dialog
    if (checkToOverride != null) {
        val activeCheck = checkToOverride!!
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { checkToOverride = null },
            title = { Text("Override Status: ${activeCheck.ruleCode}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Set final statutory determination:",
                        style = MaterialTheme.typography.bodySmall
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            color = if (overrideStatus == ComplianceStatus.PASS) CompliancePass.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { overrideStatus = ComplianceStatus.PASS }
                        ) {
                            Text(
                                text = "PASS",
                                color = if (overrideStatus == ComplianceStatus.PASS) CompliancePass else MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(8.dp)
                            )
                        }

                        Surface(
                            color = if (overrideStatus == ComplianceStatus.POTENTIAL_NON_COMPLIANCE) ComplianceFail.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { overrideStatus = ComplianceStatus.POTENTIAL_NON_COMPLIANCE }
                        ) {
                            Text(
                                text = "VIOLATION",
                                color = if (overrideStatus == ComplianceStatus.POTENTIAL_NON_COMPLIANCE) ComplianceFail else MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }

                    OutlinedTextField(
                        value = overrideComment,
                        onValueChange = { overrideComment = it },
                        label = { Text("Enforcement Officer Justification (Mandatory)") },
                        placeholder = { Text("e.g., Physical packaging confirmed compliant upon visual re-verification.") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.overrideComplianceCheck(
                            check = activeCheck,
                            newStatus = overrideStatus,
                            comment = overrideComment.ifBlank { "Manually overridden by ${currentUser.name}" }
                        )
                        checkToOverride = null
                    }
                ) {
                    Text("Apply Determination")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { checkToOverride = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Success Dialog
    if (showSignSuccessDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = {
                showSignSuccessDialog = false
                onReviewCompleted()
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = CompliancePass,
                    modifier = Modifier.size(36.dp)
                )
            },
            title = { Text("Notice Signed & Finalized") },
            text = {
                Text("The legal metrology enforcement notice has been recorded in the auditable registry with your digital signature credentials.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSignSuccessDialog = false
                        onReviewCompleted()
                    }
                ) {
                    Text("View Notice & Report")
                }
            }
        )
    }
}
