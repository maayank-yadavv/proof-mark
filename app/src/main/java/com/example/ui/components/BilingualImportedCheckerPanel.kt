package com.example.ui.components

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import com.example.ui.theme.AppFontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ComplianceFail
import com.example.ui.theme.CompliancePass

data class ImportedComplianceItem(
    val title: String,
    val ruleReference: String,
    val isDeclared: Boolean,
    val extractedValue: String
)

/**
 * Clean, high-legibility audit panel for Imported Commodities & Bilingual Declarations.
 * Strictly adheres to Anti-Hallucination rules: if information was not detected in
 * the scan, it explicitly renders "Not Provided" without guessing.
 */
@Composable
fun BilingualImportedCheckerPanel(
    countryOfOrigin: String? = null,
    importerNameAddress: String? = null,
    importerIecCode: String? = null,
    bilingualEnglishFound: Boolean? = null,
    bilingualHindiFound: Boolean? = null,
    fssaiImportLicense: String? = null,
    modifier: Modifier = Modifier
) {
    fun cleanVal(v: String?): Pair<Boolean, String> {
        val trimmed = v?.trim() ?: ""
        val isInvalid = trimmed.isBlank() ||
                trimmed.equals("Not Provided", ignoreCase = true) ||
                trimmed.equals("Not Detected", ignoreCase = true) ||
                trimmed.equals("Detail Not Provided", ignoreCase = true) ||
                trimmed.equals("Not Applicable", ignoreCase = true)
        return if (isInvalid) {
            Pair(false, "Detail Not Provided on Package")
        } else {
            Pair(true, trimmed)
        }
    }

    val (originDeclared, originVal) = cleanVal(countryOfOrigin)
    val (importerDeclared, importerVal) = cleanVal(importerNameAddress)
    val (iecDeclared, iecVal) = cleanVal(importerIecCode)
    val (fssaiDeclared, fssaiVal) = cleanVal(fssaiImportLicense)

    val (bilingualDeclared, bilingualVal) = when {
        bilingualEnglishFound == true && bilingualHindiFound == true ->
            Pair(true, "Compliant (English & Hindi Text Present on Label)")
        bilingualEnglishFound == true && bilingualHindiFound == false ->
            Pair(false, "English Detected · Hindi Script Not Provided")
        bilingualEnglishFound == false && bilingualHindiFound == true ->
            Pair(false, "Hindi Detected · English Script Not Provided")
        else ->
            Pair(false, "Bilingual Declaration Not Detected")
    }

    val checks = remember(originVal, importerVal, iecVal, bilingualVal, fssaiVal) {
        listOf(
            ImportedComplianceItem(
                title = "Country of Origin Declaration",
                ruleReference = "Rule 6(3) LM Rules & Customs Act Sec 11",
                isDeclared = originDeclared,
                extractedValue = originVal
            ),
            ImportedComplianceItem(
                title = "Importer Name & Address",
                ruleReference = "Rule 6(1)(a) LM Packaged Commodities Rules",
                isDeclared = importerDeclared,
                extractedValue = importerVal
            ),
            ImportedComplianceItem(
                title = "Importer Exporter Code (IEC)",
                ruleReference = "DGFT / Customs Regulation 2019",
                isDeclared = iecDeclared,
                extractedValue = iecVal
            ),
            ImportedComplianceItem(
                title = "Dual Language (English + Hindi)",
                ruleReference = "Rule 6(3) Principal Display Panel Directives",
                isDeclared = bilingualDeclared,
                extractedValue = bilingualVal
            ),
            ImportedComplianceItem(
                title = "FSSAI Import License Verification",
                ruleReference = "FSSAI Import Clearance Regulations 2017",
                isDeclared = fssaiDeclared,
                extractedValue = fssaiVal
            )
        )
    }

    val passCount = checks.count { it.isDeclared }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("bilingual_imported_checker_panel")
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Row with No-Wrap Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
                        shape = CircleShape,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Public,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Imported Commodity & Bilingual Audit",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Legal Metrology Rule 6(3) & DGFT Directives",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Pass Count Status Badge (Clean non-wrapping pill)
                Surface(
                    color = if (passCount == checks.size) CompliancePass else if (passCount > 0) Color(0xFF0284C7) else Color(0xFF64748B),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.wrapContentWidth()
                ) {
                    Text(
                        text = "$passCount / ${checks.size} DECLARED",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        maxLines = 1,
                        softWrap = false,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

            // Audit Items
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                checks.forEach { check ->
                    Surface(
                        color = if (check.isDeclared) Color(0xFF10B981).copy(alpha = 0.05f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(
                            0.75.dp,
                            if (check.isDeclared) Color(0xFF10B981).copy(alpha = 0.3f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            // Title Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = if (check.isDeclared) Icons.Default.CheckCircle else Icons.AutoMirrored.Filled.HelpOutline,
                                        contentDescription = null,
                                        tint = if (check.isDeclared) CompliancePass else Color(0xFF94A3B8),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = check.title,
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = check.ruleReference,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontSize = 10.sp
                                        )
                                    }
                                }

                                Surface(
                                    color = if (check.isDeclared) Color(0xFF10B981).copy(alpha = 0.15f) else Color(0xFF64748B).copy(alpha = 0.12f),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = if (check.isDeclared) "DECLARED" else "NOT PROVIDED",
                                        color = if (check.isDeclared) Color(0xFF047857) else Color(0xFF64748B),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            // Extracted Value Box
                            Surface(
                                color = if (check.isDeclared) MaterialTheme.colorScheme.surface else Color.Transparent,
                                shape = RoundedCornerShape(6.dp),
                                border = if (check.isDeclared) BorderStroke(0.5.dp, Color(0xFF10B981).copy(alpha = 0.2f)) else null,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = check.extractedValue,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 11.5.sp,
                                        lineHeight = 16.sp
                                    ),
                                    fontWeight = if (check.isDeclared) FontWeight.Medium else FontWeight.Normal,
                                    fontStyle = if (check.isDeclared) FontStyle.Normal else FontStyle.Italic,
                                    color = if (check.isDeclared) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                                    modifier = Modifier.padding(
                                        horizontal = if (check.isDeclared) 8.dp else 4.dp,
                                        vertical = if (check.isDeclared) 5.dp else 2.dp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
