package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Compare
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Factory
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontFamily
import com.example.ui.theme.AppFontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.*

@Composable
fun ProductIntelligenceReportView(
    report: ProductIntelligenceReport,
    onSelectMatchCandidate: (ProductMatchCandidate) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("product_intelligence_report_view"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Overall Confidence Header Banner & Product Identity
        ConfidenceHeaderCard(report = report)

        // 2. Possible Matches Candidate Selector (if multiple options found)
        if (report.possibleMatches.size > 1) {
            PossibleMatchesSelectorCard(
                candidates = report.possibleMatches,
                onSelectCandidate = onSelectMatchCandidate
            )
        }

        // 3. Scan vs Online Comparison Matrix
        ScanVsOnlineComparisonCard(scanComparison = report.scanComparison)

        // 4. Online Price Intelligence & Selling Intelligence
        if (report.pricing != null) {
            PricingIntelligenceCard(pricing = report.pricing)
        }

        // 5. Complete Warranty Information & Customer Support
        if (report.warranty != null) {
            WarrantyIntelligenceCard(warranty = report.warranty)
        }

        // 6. Manufacturer, Importer, Exporter, Technology & Building Details
        if (report.manufacturer != null) {
            ManufacturerIntelligenceCard(manufacturer = report.manufacturer)
        }

        // 7. Manufacturing & Supply Chain Details
        if (report.supplyChain != null) {
            ManufacturingSupplyCard(supplyChain = report.supplyChain)
        }

        // 8. Packaging & Physical Specifications (with Digital Product awareness)
        PackagingDetailsCard(report = report)

        // 9. Purpose and Intended Usage
        if (report.usagePurpose != null) {
            ProductPurposeCard(usage = report.usagePurpose)
        }

        // 10. Ingredients, Composition & Allergens (if applicable)
        if (report.composition != null && (report.composition.ingredientsList.isNotEmpty() || report.composition.activeMaterials.isNotEmpty() || report.composition.allergens.isNotEmpty())) {
            CompositionAndIngredientsCard(composition = report.composition)
        }

        // 11. Regulatory Certifications Matrix
        CertificationsCard(certifications = report.certifications)

        // 12. Legal Metrology Rule 6 Declarations Checklist
        if (report.legalMetrologyDeclarations.isNotEmpty()) {
            LegalMetrologyChecklistCard(declarations = report.legalMetrologyDeclarations)
        }

        // 13. Source Transparency & Provenance Log
        SourcesTransparencyCard(sources = report.sources)
    }
}

// =========================================================================
// 1. CONFIDENCE HEADER & PRODUCT IDENTITY
// =========================================================================
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ConfidenceHeaderCard(report: ProductIntelligenceReport) {
    val confidenceColor = when (report.overallConfidenceLevel) {
        ReliabilityLevel.HIGH_VERIFIED -> Color(0xFF10B981)
        ReliabilityLevel.MEDIUM_MATCH -> Color(0xFFF59E0B)
        ReliabilityLevel.LOW_UNCERTAIN -> Color(0xFFEF4444)
        ReliabilityLevel.UNAVAILABLE -> Color(0xFF6B7280)
    }

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        border = BorderStroke(1.dp, confidenceColor.copy(alpha = 0.4f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(confidenceColor.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = null,
                            tint = confidenceColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "${report.confidenceScorePercent}% Confidence Match",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = report.overallConfidenceLevel.title,
                            style = MaterialTheme.typography.labelSmall,
                            color = confidenceColor,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (report.isPhysicalProduct) Color(0xFF0284C7).copy(alpha = 0.2f) else Color(0xFF8B5CF6).copy(alpha = 0.2f),
                    border = BorderStroke(1.dp, if (report.isPhysicalProduct) Color(0xFF0284C7).copy(alpha = 0.5f) else Color(0xFF8B5CF6).copy(alpha = 0.5f))
                ) {
                    Text(
                        text = if (report.isPhysicalProduct) "PHYSICAL COMMODITY" else "DIGITAL ASSET",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (report.isPhysicalProduct) Color(0xFF38BDF8) else Color(0xFFA78BFA),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 10.sp
                    )
                }
            }

            // Primary Product Info
            if (report.primaryMatch != null) {
                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = Color(0xFF334155), thickness = 0.5.dp)
                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = report.primaryMatch.productName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00E5FF),
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (report.primaryMatch.brand.isNotBlank() && report.primaryMatch.brand != "Not Provided") {
                        ProductTagChip(label = "Brand", value = report.primaryMatch.brand, icon = Icons.Default.WorkspacePremium)
                    }
                    if (report.primaryMatch.category.isNotBlank() && report.primaryMatch.category != "Not Provided") {
                        ProductTagChip(label = "Category", value = report.primaryMatch.category, icon = Icons.Default.Inventory2)
                    }
                    if (!report.primaryMatch.model.isNullOrBlank() && report.primaryMatch.model != "Not Provided") {
                        ProductTagChip(label = "Model", value = report.primaryMatch.model, icon = Icons.Default.Devices)
                    }
                }

                if (report.primaryMatch.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = report.primaryMatch.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFCBD5E1),
                        lineHeight = 18.sp
                    )
                }
            }

            // Verification signals
            if (report.confidenceReasons.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Verification Signals & Evidence:",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF94A3B8)
                )
                Spacer(modifier = Modifier.height(4.dp))
                report.confidenceReasons.forEach { reason ->
                    Row(
                        verticalAlignment = Alignment.Top,
                        modifier = Modifier.padding(vertical = 1.dp)
                    ) {
                        Text("• ", fontWeight = FontWeight.Bold, color = confidenceColor, fontSize = 12.sp)
                        Text(
                            text = reason,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFE2E8F0),
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductTagChip(label: String, value: String, icon: ImageVector) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = Color(0xFF0F172A),
        border = BorderStroke(1.dp, Color(0xFF334155))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
            Icon(icon, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(12.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "$label: ",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF94A3B8),
                fontSize = 10.sp
            )
            Text(
                text = value,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 10.sp
            )
        }
    }
}

// =========================================================================
// 2. CANDIDATE SELECTOR
// =========================================================================
@Composable
private fun PossibleMatchesSelectorCard(
    candidates: List<ProductMatchCandidate>,
    onSelectCandidate: (ProductMatchCandidate) -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        border = BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Compare, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Multiple Product Matches Identified (${candidates.size})",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            candidates.forEach { candidate ->
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF0F172A),
                    border = BorderStroke(1.dp, Color(0xFF334155)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { onSelectCandidate(candidate) }
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = candidate.product.productName,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = candidate.matchReason,
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF94A3B8),
                                fontSize = 10.sp
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFF10B981).copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "${candidate.matchConfidence}%",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF34D399),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// =========================================================================
// 3. SCAN VS ONLINE COMPARISON MATRIX
// =========================================================================
@Composable
private fun ScanVsOnlineComparisonCard(scanComparison: List<ScanVsOnlineRow>) {
    var isExpanded by remember { mutableStateOf(true) }

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        border = BorderStroke(1.dp, Color(0xFF334155)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF38BDF8).copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Compare, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(16.dp))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Scan vs Reference Comparison",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                IconButton(
                    onClick = { isExpanded = !isExpanded },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "Toggle",
                        tint = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    if (scanComparison.isEmpty()) {
                        Text(
                            text = "No direct comparison rows available.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF94A3B8)
                        )
                    } else {
                        scanComparison.forEachIndexed { index, row ->
                            ComparisonRowCard(row = row)
                            if (index < scanComparison.size - 1) {
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ComparisonRowCard(row: ScanVsOnlineRow) {
    val statusColor = Color(row.status.colorHex)

    Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFF0F172A),
        border = BorderStroke(0.5.dp, Color(0xFF334155)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = row.fieldName,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp,
                    modifier = Modifier.weight(1f, fill = false)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    EvidenceStateBadge(
                        state = row.evidenceState,
                        compact = true
                    )
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = statusColor.copy(alpha = 0.2f),
                        border = BorderStroke(0.5.dp, statusColor.copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = row.status.label.uppercase(java.util.Locale.ROOT),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = statusColor,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            fontSize = 9.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Values
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Scanned Value
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "SCANNED / EVIDENCE",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF64748B),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    val isScannedMissing = row.scannedValue.contains("Not Provided", ignoreCase = true) || row.scannedValue.contains("Not Detected", ignoreCase = true)
                    Text(
                        text = row.scannedValue,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = if (isScannedMissing) FontWeight.Normal else FontWeight.SemiBold,
                        color = if (isScannedMissing) Color(0xFF64748B) else Color.White,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }

                // Reference Value
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "REFERENCE BENCHMARK",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF64748B),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    val isOnlineMissing = row.onlineValue.contains("Not Provided", ignoreCase = true) || row.onlineValue.contains("Not Detected", ignoreCase = true)
                    Text(
                        text = row.onlineValue,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = if (isOnlineMissing) FontWeight.Normal else FontWeight.SemiBold,
                        color = if (isOnlineMissing) Color(0xFF64748B) else Color(0xFF38BDF8),
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
            }

            if (!row.note.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "• ${row.note}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFFCBD5E1),
                    fontSize = 10.sp
                )
            }
        }
    }
}

// =========================================================================
// 4. ONLINE PRICE & SELLING INTELLIGENCE
// =========================================================================
@Composable
private fun PricingIntelligenceCard(pricing: OnlinePriceInfo) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        border = BorderStroke(1.dp, Color(0xFF334155)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF10B981).copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.CurrencyRupee, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(16.dp))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Online Price & Selling Intelligence",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                if (pricing.discountPercent != null && pricing.discountPercent > 0) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFF10B981).copy(alpha = 0.2f),
                        border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = "${pricing.discountPercent}% OFF",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF34D399),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Pricing Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Printed MRP
                PriceMetricTile(
                    label = "PRINTED / DECLARED MRP",
                    value = pricing.printedMrp ?: (if (pricing.onlineMrp != null) "₹%.2f".format(pricing.onlineMrp) else "Not Provided"),
                    highlightColor = Color.White,
                    modifier = Modifier.weight(1f)
                )

                // Current Online Selling Price
                PriceMetricTile(
                    label = "ONLINE SELLING PRICE",
                    value = if (pricing.currentOnlinePrice != null) "₹%.2f".format(pricing.currentOnlinePrice) else "Not Provided",
                    highlightColor = Color(0xFF00E5FF),
                    modifier = Modifier.weight(1f)
                )
            }

            if (pricing.priceDifference != null && pricing.priceDifference > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF0F172A),
                    border = BorderStroke(0.5.dp, Color(0xFF334155)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Estimated Consumer Savings:",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF94A3B8)
                        )
                        Text(
                            text = "₹%.2f below Printed MRP".format(pricing.priceDifference),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF34D399)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = Color(0xFF334155), thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(8.dp))

            // Metadata info
            MetadataRow(label = "Price Source / Feeds", value = pricing.priceSource)
            MetadataRow(label = "Unit Sale Price Benchmark", value = pricing.pricePerUnit ?: "Not Provided")
            MetadataRow(label = "Market Price Range", value = pricing.priceRange ?: "Not Provided")
            MetadataRow(label = "Last Verified Timestamp", value = pricing.lastCheckedTimestamp)
        }
    }
}

@Composable
private fun PriceMetricTile(
    label: String,
    value: String,
    highlightColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Color(0xFF0F172A),
        border = BorderStroke(0.5.dp, Color(0xFF334155)),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF64748B),
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = if (value.contains("Not Provided", ignoreCase = true)) Color(0xFF64748B) else highlightColor,
                fontSize = 13.sp
            )
        }
    }
}

// =========================================================================
// 5. WARRANTY & CUSTOMER SUPPORT
// =========================================================================
@Composable
private fun WarrantyIntelligenceCard(warranty: WarrantyIntel) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        border = BorderStroke(1.dp, Color(0xFF334155)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF8B5CF6).copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = Color(0xFFA78BFA), modifier = Modifier.size(16.dp))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Warranty & Support Information",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (warranty.isProvided && warranty.duration != "Not Provided") Color(0xFF8B5CF6).copy(alpha = 0.2f) else Color(0xFF64748B).copy(alpha = 0.2f)
                ) {
                    Text(
                        text = warranty.duration,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (warranty.isProvided && warranty.duration != "Not Provided") Color(0xFFA78BFA) else Color(0xFF94A3B8),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        fontSize = 10.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Full terms
            Text(
                text = "Full Warranty Policy & Terms:",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF94A3B8)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = warranty.fullTerms,
                style = MaterialTheme.typography.bodySmall,
                color = if (warranty.fullTerms.contains("Not Provided", ignoreCase = true)) Color(0xFF64748B) else Color(0xFFE2E8F0),
                lineHeight = 18.sp
            )

            // Conditions & Exclusions if available
            if (warranty.conditions.isNotEmpty() || warranty.exclusions.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isExpanded = !isExpanded },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (isExpanded) "Hide Detailed Terms & Exclusions" else "View Coverage Conditions & Exclusions",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF38BDF8),
                        fontWeight = FontWeight.SemiBold
                    )
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = Color(0xFF38BDF8),
                        modifier = Modifier.size(16.dp)
                    )
                }

                AnimatedVisibility(visible = isExpanded) {
                    Column(modifier = Modifier.padding(top = 8.dp)) {
                        if (warranty.conditions.isNotEmpty()) {
                            Text(
                                text = "Coverage Conditions:",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF34D399)
                            )
                            warranty.conditions.forEach { cond ->
                                Text("• $cond", style = MaterialTheme.typography.bodySmall, color = Color(0xFFCBD5E1), fontSize = 11.sp, modifier = Modifier.padding(top = 2.dp))
                            }
                        }

                        if (warranty.exclusions.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Explicit Exclusions:",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFEF4444)
                            )
                            warranty.exclusions.forEach { excl ->
                                Text("• $excl", style = MaterialTheme.typography.bodySmall, color = Color(0xFFCBD5E1), fontSize = 11.sp, modifier = Modifier.padding(top = 2.dp))
                            }
                        }
                    }
                }
            }

            // Support contact
            if (!warranty.supportPhone.isNullOrBlank() || !warranty.supportEmail.isNullOrBlank() || !warranty.supportWebsite.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = Color(0xFF334155), thickness = 0.5.dp)
                Spacer(modifier = Modifier.height(8.dp))

                if (!warranty.supportPhone.isNullOrBlank()) {
                    MetadataRow(label = "Official Helpline", value = warranty.supportPhone)
                }
                if (!warranty.supportEmail.isNullOrBlank()) {
                    MetadataRow(label = "Support Email", value = warranty.supportEmail)
                }
                if (!warranty.supportWebsite.isNullOrBlank()) {
                    MetadataRow(label = "Service Portal", value = warranty.supportWebsite)
                }
            }
        }
    }
}

// =========================================================================
// 6. MANUFACTURER, IMPORTER, EXPORTER & BUILDING DETAILS
// =========================================================================
@Composable
private fun ManufacturerIntelligenceCard(manufacturer: ManufacturerIntel) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        border = BorderStroke(1.dp, Color(0xFF334155)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF0284C7).copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Factory, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(16.dp))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Manufacturer, Importer & Origin",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            MetadataRow(label = "Manufacturer Name", value = manufacturer.name)
            MetadataRow(label = "Registered Address", value = manufacturer.address ?: "Not Provided")
            MetadataRow(label = "Country of Origin", value = manufacturer.countryOfOrigin)
            MetadataRow(label = "Importer Details", value = manufacturer.importerName?.let { if (manufacturer.importerAddress != null) "$it (${manufacturer.importerAddress})" else it } ?: "Not Applicable / Domestic")
            MetadataRow(label = "Exporter Details", value = manufacturer.exporterName ?: "Not Applicable")
            MetadataRow(label = "Manufacturing Plant Location", value = manufacturer.manufacturingLocation ?: "Not Provided")
            MetadataRow(label = "Building & Tech Process", value = manufacturer.buildingAndTechDetails ?: "Not Provided")
            MetadataRow(label = "Statutory License Number", value = manufacturer.licenseNumber ?: "Not Provided")
            MetadataRow(label = "LMPC Registration Number", value = manufacturer.registrationNumber ?: "Not Provided")
            MetadataRow(label = "Consumer Care Helpline", value = manufacturer.customerCarePhone ?: "Not Provided")
        }
    }
}

// =========================================================================
// 7. MANUFACTURING & SUPPLY CHAIN DETAILS
// =========================================================================
@Composable
private fun ManufacturingSupplyCard(supplyChain: ManufacturingSupplyIntel) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        border = BorderStroke(1.dp, Color(0xFF334155)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF59E0B).copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.LocalShipping, contentDescription = null, tint = Color(0xFFFBBF24), modifier = Modifier.size(16.dp))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Manufacturing & Supply Chain Identification",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            MetadataRow(label = "Model Number", value = supplyChain.modelNumber ?: "Not Provided")
            MetadataRow(label = "Product Variant", value = supplyChain.variant ?: "Not Provided")
            MetadataRow(label = "SKU / Part Number", value = supplyChain.skuOrPartNumber ?: "Not Provided")
            MetadataRow(label = "Batch / Lot Number", value = supplyChain.batchOrLotNumber ?: "Not Provided")
            MetadataRow(label = "Date of Manufacture", value = supplyChain.manufacturingDate ?: "Not Provided")
            MetadataRow(label = "Best Before / Expiry Date", value = supplyChain.expiryDate ?: "Not Applicable / Not Provided")
            MetadataRow(label = "Serial / Unit Identification", value = supplyChain.serialOrIdentification ?: "Not Provided")
            MetadataRow(label = "Distribution & Supply Chain", value = supplyChain.distributorOrSupplyChain ?: "Not Provided")
            MetadataRow(label = "Manufacturing Standards Compliance", value = supplyChain.manufacturingCompliance ?: "Not Provided")
        }
    }
}

// =========================================================================
// 8. PACKAGING & PHYSICAL SPECIFICATIONS (With Digital Product Awareness)
// =========================================================================
@Composable
private fun PackagingDetailsCard(report: ProductIntelligenceReport) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        border = BorderStroke(1.dp, Color(0xFF334155)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF059669).copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Inventory2, contentDescription = null, tint = Color(0xFF34D399), modifier = Modifier.size(16.dp))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Packaging & Physical Specifications",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (!report.isPhysicalProduct) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF6366F1).copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, Color(0xFF6366F1).copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Devices, contentDescription = null, tint = Color(0xFFA5B4FC), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Digital Non-Physical Commodity",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFA5B4FC)
                            )
                            Text(
                                text = "Physical dimensions, net quantity in grams, packaging materials, and physical packer declarations are Not Applicable.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFE0E7FF),
                                fontSize = 11.sp
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            val comp = report.composition
            MetadataRow(label = "Net Quantity", value = comp?.netQuantity ?: (if (report.isPhysicalProduct) "Not Provided" else "No Physical Product"))
            MetadataRow(label = "Gross Weight", value = comp?.grossQuantity ?: (if (report.isPhysicalProduct) "Not Provided" else "No Physical Product"))
            MetadataRow(label = "Packaging Format", value = comp?.packagingType ?: (if (report.isPhysicalProduct) "Not Provided" else "No Physical Product"))
            MetadataRow(label = "Package Dimensions", value = comp?.dimensions ?: (if (report.isPhysicalProduct) "Not Provided" else "No Physical Product"))
            MetadataRow(label = "Packaging Material", value = comp?.material ?: (if (report.isPhysicalProduct) "Not Provided" else "No Physical Product"))
        }
    }
}

// =========================================================================
// 9. PURPOSE & USAGE
// =========================================================================
@Composable
private fun ProductPurposeCard(usage: ProductUsagePurpose) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        border = BorderStroke(1.dp, Color(0xFF334155)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF3B82F6).copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF60A5FA), modifier = Modifier.size(16.dp))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Product Purpose & Intended Usage",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            MetadataRow(label = "Commodity Category", value = usage.category)
            MetadataRow(label = "Intended Purpose", value = usage.purposeSummary)
            if (!usage.targetAudience.isNullOrBlank()) {
                MetadataRow(label = "Target Audience", value = usage.targetAudience)
            }
            if (!usage.storageInstructions.isNullOrBlank()) {
                MetadataRow(label = "Storage Instructions", value = usage.storageInstructions)
            }
            if (!usage.directionsForUse.isNullOrBlank()) {
                MetadataRow(label = "Directions for Use", value = usage.directionsForUse)
            }
        }
    }
}

// =========================================================================
// 10. INGREDIENTS & COMPOSITION
// =========================================================================
@Composable
private fun CompositionAndIngredientsCard(composition: ProductCompositionIntel) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        border = BorderStroke(1.dp, Color(0xFF334155)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF10B981).copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Restaurant, contentDescription = null, tint = Color(0xFF34D399), modifier = Modifier.size(16.dp))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Ingredients & Material Composition",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (composition.ingredientsList.isNotEmpty()) {
                Text(
                    text = "Declared Ingredients List:",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF94A3B8)
                )
                Spacer(modifier = Modifier.height(4.dp))
                composition.ingredientsList.forEach { ing ->
                    Text("• $ing", style = MaterialTheme.typography.bodySmall, color = Color(0xFFE2E8F0), fontSize = 12.sp, modifier = Modifier.padding(vertical = 1.dp))
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (composition.activeMaterials.isNotEmpty()) {
                Text(
                    text = "Active Materials & Construction:",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF94A3B8)
                )
                Spacer(modifier = Modifier.height(4.dp))
                composition.activeMaterials.forEach { mat ->
                    Text("• $mat", style = MaterialTheme.typography.bodySmall, color = Color(0xFFE2E8F0), fontSize = 12.sp, modifier = Modifier.padding(vertical = 1.dp))
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (composition.allergens.isNotEmpty()) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFEF4444).copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(
                            text = "Allergen Declarations:",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFF87171)
                        )
                        composition.allergens.forEach { allergen ->
                            Text("⚠️ $allergen", style = MaterialTheme.typography.bodySmall, color = Color(0xFFFECACA), fontSize = 11.sp, modifier = Modifier.padding(top = 2.dp))
                        }
                    }
                }
            }
        }
    }
}

// =========================================================================
// 11. REGULATIONS & CERTIFICATIONS MATRIX
// =========================================================================
@Composable
private fun CertificationsCard(certifications: List<CertificationItem>) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        border = BorderStroke(1.dp, Color(0xFF334155)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF38BDF8).copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Policy, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(16.dp))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Regulations & Quality Certifications",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (certifications.isEmpty()) {
                Text(
                    text = "No certifications provided.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF94A3B8)
                )
            } else {
                certifications.forEachIndexed { index, cert ->
                    CertificationRow(cert = cert)
                    if (index < certifications.size - 1) {
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun CertificationRow(cert: CertificationItem) {
    val statusColor = Color(cert.status.colorHex)

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Color(0xFF0F172A),
        border = BorderStroke(0.5.dp, Color(0xFF334155)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = cert.title,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                if (!cert.identifierNumber.isNullOrBlank() && cert.identifierNumber != "Not Provided" && cert.identifierNumber != "N/A") {
                    Text(
                        text = "Reg ID: ${cert.identifierNumber}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF00E5FF),
                        fontSize = 10.sp
                    )
                }
                Text(
                    text = "Issuing Body: ${cert.issuingBody}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF94A3B8),
                    fontSize = 10.sp
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Surface(
                shape = RoundedCornerShape(4.dp),
                color = statusColor.copy(alpha = 0.2f),
                border = BorderStroke(0.5.dp, statusColor.copy(alpha = 0.5f))
            ) {
                Text(
                    text = cert.status.label,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = statusColor,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                    fontSize = 9.sp
                )
            }
        }
    }
}

// =========================================================================
// 12. LEGAL METROLOGY DECLARATIONS CHECKLIST
// =========================================================================
@Composable
private fun LegalMetrologyChecklistCard(declarations: List<LegalMetrologyDeclarationItem>) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        border = BorderStroke(1.dp, Color(0xFF334155)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF38BDF8).copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Gavel, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(16.dp))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Legal Metrology Rule 6 Declarations",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                IconButton(
                    onClick = { isExpanded = !isExpanded },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "Toggle",
                        tint = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    declarations.forEachIndexed { index, item ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF0F172A),
                            border = BorderStroke(0.5.dp, Color(0xFF334155)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = item.declarationName,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = item.ruleReference,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color(0xFF38BDF8),
                                        fontSize = 9.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = item.onlineValue,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFFCBD5E1),
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

// =========================================================================
// 13. SOURCE TRANSPARENCY
// =========================================================================
@Composable
private fun SourcesTransparencyCard(sources: List<IntelSource>) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        border = BorderStroke(1.dp, Color(0xFF334155)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF64748B).copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Public, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(16.dp))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Source Provenance & Transparency",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (sources.isEmpty()) {
                Text(
                    text = "Verified through on-device perception & OCR evidence.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF94A3B8)
                )
            } else {
                sources.forEach { src ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF0F172A),
                        border = BorderStroke(0.5.dp, Color(0xFF334155)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = src.sourceName,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Type: ${src.sourceType} • Retrieved: ${src.retrievedDate}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF64748B),
                                    fontSize = 9.sp
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(src.reliabilityLevel.colorHex).copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = src.reliabilityLevel.title.take(15),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(src.reliabilityLevel.colorHex),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    fontSize = 8.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// =========================================================================
// HELPER METADATA ROW (Clean, high-contrast, responsive, EvidenceState-driven)
// =========================================================================
@Composable
private fun MetadataRow(
    label: String,
    value: String,
    evidenceState: EvidenceState? = null,
    modifier: Modifier = Modifier
) {
    val effectiveState = evidenceState ?: resolveEvidenceState(value)
    val isMissingOrNotProvided = effectiveState == EvidenceState.NOT_PROVIDED
    val isNotApplicable = effectiveState == EvidenceState.NOT_APPLICABLE || effectiveState == EvidenceState.NO_PHYSICAL_PRODUCT
    val isUnableToVerify = effectiveState == EvidenceState.UNABLE_TO_VERIFY

    val displayText = when {
        effectiveState == EvidenceState.NOT_PROVIDED && (value.isBlank() || value.equals("not detected", ignoreCase = true) || value.equals("not provided", ignoreCase = true)) -> "Not Provided"
        effectiveState == EvidenceState.NOT_APPLICABLE && value.isBlank() -> "Not Applicable"
        effectiveState == EvidenceState.NO_PHYSICAL_PRODUCT && value.isBlank() -> "No Physical Product"
        effectiveState == EvidenceState.UNABLE_TO_VERIFY && value.isBlank() -> "Unable to Verify"
        else -> value
    }

    val valueColor = when (effectiveState) {
        EvidenceState.NOT_PROVIDED -> Color(0xFF94A3B8)
        EvidenceState.NOT_APPLICABLE, EvidenceState.NO_PHYSICAL_PRODUCT -> Color(0xFF94A3B8)
        EvidenceState.UNABLE_TO_VERIFY -> Color(0xFFFBBF24)
        else -> Color(0xFFF1F5F9)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label.uppercase(java.util.Locale.ROOT),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF64748B),
                fontSize = 9.sp,
                letterSpacing = 0.5.sp,
                modifier = Modifier.weight(1f, fill = false)
            )
            Spacer(modifier = Modifier.width(6.dp))
            EvidenceStateBadge(
                state = effectiveState,
                compact = true
            )
        }
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = displayText,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = if (isMissingOrNotProvided || isNotApplicable) FontWeight.Normal else FontWeight.SemiBold,
            color = valueColor,
            fontSize = 12.sp,
            lineHeight = 16.sp
        )
        HorizontalDivider(color = Color(0xFF334155).copy(alpha = 0.5f), thickness = 0.5.dp, modifier = Modifier.padding(top = 4.dp))
    }
}
