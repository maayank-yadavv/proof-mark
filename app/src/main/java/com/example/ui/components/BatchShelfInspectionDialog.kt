package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ComplianceFail
import com.example.ui.theme.CompliancePass

data class BatchShelfPackage(
    val id: String,
    val brandName: String,
    val productName: String,
    val mrp: String,
    val netQuantity: String,
    val fssaiLicNo: String,
    val isCompliant: Boolean,
    val nonComplianceReason: String? = null
)

@Composable
fun BatchShelfInspectionDialog(
    initialStoreName: String = "Reliance Smart Superstore - Shelf B4",
    onDismiss: () -> Unit,
    onGenerateReport: (List<BatchShelfPackage>) -> Unit
) {
    val context = LocalContext.current
    var storeName by remember { mutableStateOf(initialStoreName) }

    val batchItems = remember {
        mutableStateListOf(
            BatchShelfPackage("SH-101", "Amul Taza", "Toned Milk 1L", "₹72.00", "1 Litre", "10012021000071", true),
            BatchShelfPackage("SH-102", "Britannia NutriChoice", "Digestive Biscuits 250g", "₹65.00", "250 g", "10019011000543", true),
            BatchShelfPackage("SH-103", "Local Dairy Premium", "Full Cream Paneer", "₹140.00", "200 g", "UNREGISTERED", false, "Missing FSSAI License & Net Weight Statement"),
            BatchShelfPackage("SH-104", "Maggi 2-Min Noodles", "Masala Instant Noodles 420g", "₹60.00", "420 g", "10014042000210", true)
        )
    }

    val compliantCount = batchItems.count { it.isCompliant }
    val nonCompliantCount = batchItems.count { !it.isCompliant }
    val batchComplianceRate = if (batchItems.isNotEmpty()) (compliantCount * 100) / batchItems.size else 100

    val scrollState = rememberScrollState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Storefront,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(26.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Batch Shelf Inspection Mode",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Continuous Multi-Package Store Audit Queue",
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
                // Shelf Summary Bar
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(storeName, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                            Text("${batchItems.size} Commodities in Shelf Queue", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
                        }
                        Surface(
                            color = if (batchComplianceRate >= 75) CompliancePass else ComplianceFail,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "$batchComplianceRate% Pass",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                // Add simulated shelf item button
                Button(
                    onClick = {
                        val newId = "SH-${(105..999).random()}"
                        val isPass = (1..10).random() > 3
                        batchItems.add(
                            BatchShelfPackage(
                                id = newId,
                                brandName = if (isPass) "Haldiram's" else "Unbranded Snack",
                                productName = if (isPass) "Bhujia Sev 200g" else "Local Fried Chips",
                                mrp = if (isPass) "₹55.00" else "₹30.00",
                                netQuantity = "200 g",
                                fssaiLicNo = if (isPass) "10017051000344" else "INVALID",
                                isCompliant = isPass,
                                nonComplianceReason = if (isPass) null else "Missing MRP and Declaration Language"
                            )
                        )
                        Toast.makeText(context, "Added scanned commodity to Shelf Queue!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("add_batch_shelf_item_btn")
                ) {
                    Icon(Icons.Default.QrCodeScanner, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Simulate Continuous Camera Package Scan (+1)")
                }

                Text("Scanned Shelf Items Breakdown:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)

                batchItems.forEach { item ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, if (item.isCompliant) CompliancePass.copy(alpha = 0.5f) else ComplianceFail.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Icon(
                                    imageVector = if (item.isCompliant) Icons.Default.CheckCircle else Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = if (item.isCompliant) CompliancePass else ComplianceFail,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(item.brandName + " - " + item.productName, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                    Text("MRP: ${item.mrp} • Net: ${item.netQuantity} • FSSAI: ${item.fssaiLicNo}", style = MaterialTheme.typography.bodySmall, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    if (!item.isCompliant && item.nonComplianceReason != null) {
                                        Text(item.nonComplianceReason, style = MaterialTheme.typography.labelSmall, fontSize = 10.sp, color = ComplianceFail, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                            IconButton(onClick = { batchItems.remove(item) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Remove item", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onGenerateReport(batchItems)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier.testTag("generate_shelf_audit_report_btn")
            ) {
                Icon(Icons.Default.Assignment, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Generate Store Audit Report (${batchItems.size})")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
