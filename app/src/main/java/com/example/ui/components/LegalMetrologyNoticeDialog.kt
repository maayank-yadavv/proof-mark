package com.example.ui.components

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ComplianceFail

data class ViolationFineItem(
    val sectionRule: String,
    val infractionTitle: String,
    val statutoryFine: Int
)

@Composable
fun LegalMetrologyNoticeDialog(
    inspectionId: String = "INSP-2026-8841",
    productName: String = "Packaged Commodity Sample",
    brandName: String = "Sample Brand",
    violatingRules: List<String> = listOf("Rule 6(1)(e) - Net Quantity Declaration Missing", "Rule 6(1)(d) - Invalid MRP Format"),
    totalPenaltyAmount: Double = 50000.0,
    officerName: String = "Inspector A. K. Sharma (ID: LM-8842)",
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val scrollState = rememberScrollState()

    val noticeRefNo = "LM/NOTICE/2026/08/${(1000..9999).random()}"

    val fineItems = violatingRules.map { rule ->
        when {
            rule.contains("Net Quantity", ignoreCase = true) -> ViolationFineItem("Section 36(1) / Rule 6(1)(e)", "Absence of Net Quantity / Weight Statement", 25000)
            rule.contains("MRP", ignoreCase = true) -> ViolationFineItem("Rule 6(1)(d) / Sec 36", "Non-compliant Maximum Retail Price (MRP) Declaration", 25000)
            rule.contains("Manufacturer", ignoreCase = true) || rule.contains("Packer", ignoreCase = true) -> ViolationFineItem("Rule 6(1)(a)", "Omission of Manufacturer / Packer Name & Address", 25000)
            rule.contains("FSSAI", ignoreCase = true) -> ViolationFineItem("Rule 6(1)(g) / FSS 2.1", "Unverified / Missing FSSAI Registration Number", 25000)
            rule.contains("Import", ignoreCase = true) || rule.contains("Origin", ignoreCase = true) -> ViolationFineItem("Rule 6(3) / Custom Sec 11", "Absence of Country of Origin / Importer Details", 50000)
            else -> ViolationFineItem("Legal Metrology Act Sec 36", rule, 25000)
        }
    }

    val calculatedTotalFine = fineItems.sumOf { it.statutoryFine }

    val fullNoticeText = """
        ====================================================
        GOVERNMENT OF INDIA - DIRECTORATE OF LEGAL METROLOGY
        STATUTORY NOTICE UNDER SECTION 36 & RULE 32
        ====================================================
        Notice Ref #: $noticeRefNo
        Docket #: $inspectionId
        Date: 26 August 2026

        TO:
        The Director / Food Business Operator
        $brandName ($productName)

        WHEREAS an official enforcement inspection of packaged commodity "$productName" (Brand: $brandName) conducted under the Legal Metrology (Packaged Commodities) Rules, 2011 revealed statutory non-compliance.

        OFFENCE & PENALTY CALCULATION BREAKDOWN:
        ${fineItems.joinToString("\n") { " - [${it.sectionRule}] ${it.infractionTitle}: Statutory Fine ₹${it.statutoryFine}" }}

        TOTAL COMPOUNDING FINE ASSESSED: ₹$calculatedTotalFine

        DIRECTIVE:
        You are hereby directed to pay the compounding penalty fee of ₹$calculatedTotalFine within fourteen (14) calendar days of receipt of this notice, failing which prosecution proceedings shall be instituted under Section 36(2) of the Legal Metrology Act, 2009.

        Issued By:
        $officerName
        Legal Metrology Inspector & Authorized Enforcement Officer
        ====================================================
    """.trimIndent()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Gavel,
                    contentDescription = null,
                    tint = ComplianceFail,
                    modifier = Modifier.size(26.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Statutory Notice & Penalty Memo",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = ComplianceFail
                    )
                    Text(
                        text = "Under Rule 32 of Legal Metrology Rules, 2011",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(2.dp, ComplianceFail),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "DIRECTORATE OF LEGAL METROLOGY",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Department of Consumer Affairs • Govt. of India",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = ComplianceFail.copy(alpha = 0.3f))
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Notice Reference #", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(noticeRefNo, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, fontFamily = AppFontFamily)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Offending Product", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("$brandName ($productName)", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Enforcement Officer", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(officerName, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "STATUTORY PENALTY TABLE:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = ComplianceFail
                        )
                        Spacer(modifier = Modifier.height(4.dp))

                        fineItems.forEach { fine ->
                            Surface(
                                color = ComplianceFail.copy(alpha = 0.08f),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(fine.sectionRule, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = ComplianceFail)
                                        Text(fine.infractionTitle, style = MaterialTheme.typography.bodySmall, fontSize = 11.sp)
                                    }
                                    Text("₹${fine.statutoryFine}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.ExtraBold, color = ComplianceFail)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            color = ComplianceFail,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("TOTAL COMPOUNDING FINE:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Color.White)
                                Text("₹$calculatedTotalFine", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold, color = Color.White)
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(fullNoticeText))
                            Toast.makeText(context, "Legal Notice text copied to clipboard!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("copy_legal_notice_btn")
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Copy Text")
                    }

                    Button(
                        onClick = {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, "Legal Metrology Notice - $noticeRefNo")
                                putExtra(Intent.EXTRA_TEXT, fullNoticeText)
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share Legal Notice"))
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ComplianceFail),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("share_legal_notice_btn")
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Share Notice")
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}
