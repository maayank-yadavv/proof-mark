package com.example.ui.components

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.ui.theme.ComplianceFail
import com.example.ui.theme.CompliancePass

data class ImportedComplianceItem(
    val title: String,
    val ruleReference: String,
    val isDeclared: Boolean,
    val extractedValue: String
)

@Composable
fun BilingualImportedCheckerPanel(
    countryOfOrigin: String = "Thailand (Product of Thailand)",
    importerNameAddress: String = "Ferrero India Pvt Ltd, Baramati, MH - 413133",
    importerIecCode: String = "IEC-0309014820",
    bilingualEnglishFound: Boolean = true,
    bilingualHindiFound: Boolean = true,
    fssaiImportLicense: String = "10013022001928",
    modifier: Modifier = Modifier
) {
    var isImportedModeEnabled by remember { mutableStateOf(true) }

    val checks = remember(countryOfOrigin, importerIecCode, bilingualEnglishFound, bilingualHindiFound) {
        listOf(
            ImportedComplianceItem("Country of Origin Declaration", "Rule 6(3) LM Rules & Custom Sec 11", countryOfOrigin.isNotBlank(), countryOfOrigin),
            ImportedComplianceItem("Importer Name & Address", "Rule 6(1)(a) LM Rules 2011", importerNameAddress.isNotBlank(), importerNameAddress),
            ImportedComplianceItem("Importer Exporter Code (IEC)", "DGFT / Custom Regulation 2019", importerIecCode.isNotBlank(), importerIecCode),
            ImportedComplianceItem("Dual Language (English + Hindi)", "Rule 6(3) Principal Display Panel", bilingualEnglishFound && bilingualHindiFound, if (bilingualEnglishFound && bilingualHindiFound) "Compliant (English & Hindi Text Present)" else "Non-Compliant (Hindi Declaration Missing)"),
            ImportedComplianceItem("FSSAI Import License Verification", "FSSAI Import Clearance Reg 2017", fssaiImportLicense.isNotBlank(), fssaiImportLicense)
        )
    }

    val passCount = checks.count { it.isDeclared }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("bilingual_imported_checker_panel")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Public,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Imported Commodity & Bilingual Audit",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Legal Metrology Rule 6(3) & DGFT Directives",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                    }
                }

                Surface(
                    color = if (passCount == checks.size) CompliancePass else MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "$passCount / ${checks.size} PASSED",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(10.dp))

            checks.forEach { check ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Icon(
                            imageVector = if (check.isDeclared) Icons.Default.CheckCircle else Icons.Default.Warning,
                            contentDescription = null,
                            tint = if (check.isDeclared) CompliancePass else ComplianceFail,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = check.title,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = check.ruleReference,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 10.sp
                            )
                        }
                    }

                    Text(
                        text = check.extractedValue,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = if (check.isDeclared) MaterialTheme.colorScheme.onSurface else ComplianceFail,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}
