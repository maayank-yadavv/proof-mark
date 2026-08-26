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
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Compare
import androidx.compose.material.icons.filled.ContactSupport
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
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
        // 1. Overall Confidence Header Banner
        ConfidenceHeaderCard(report = report)

        // 2. Possible Matches Candidate Selector (if fuzzy matches or candidates exist)
        if (report.possibleMatches.size > 1) {
            PossibleMatchesSelectorCard(
                candidates = report.possibleMatches,
                onSelectCandidate = onSelectMatchCandidate
            )
        }

        // 3. Scan vs Online Comparison Matrix
        ScanVsOnlineComparisonCard(scanComparison = report.scanComparison)

        // 4. Conflict Warning Banner (if conflicts exist between sources)
        if (report.conflicts.isNotEmpty()) {
            InformationConflictCard(conflicts = report.conflicts)
        }

        // 5. Pricing Intelligence Card (MRP vs Online Price)
        if (report.pricing != null) {
            PricingIntelligenceCard(pricing = report.pricing)
        }

        // 6. Manufacturer & Supply Chain Intelligence
        if (report.manufacturer != null) {
            ManufacturerIntelligenceCard(manufacturer = report.manufacturer)
        }

        // 7. Product Purpose & Intended Usage
        if (report.usagePurpose != null) {
            ProductPurposeCard(usage = report.usagePurpose)
        }

        // 8. Packaging & Dimension Details
        PackagingDetailsCard(report = report)

        // 9. Ingredients, Composition & Allergens
        if (report.composition != null) {
            CompositionAndIngredientsCard(composition = report.composition)
        }

        // 10. Regulatory Certifications (Verified vs Online vs Missing)
        CertificationsCard(certifications = report.certifications)

        // 11. Legal Metrology Rule 6 Declarations Checklist
        LegalMetrologyChecklistCard(declarations = report.legalMetrologyDeclarations)

        // 12. Source Transparency & Attribution
        SourcesTransparencyCard(sources = report.sources)
    }
}

@Composable
private fun ConfidenceHeaderCard(report: ProductIntelligenceReport) {
    val confidenceColor = when (report.overallConfidenceLevel) {
        ReliabilityLevel.HIGH_VERIFIED -> Color(0xFF10B981)
        ReliabilityLevel.MEDIUM_MATCH -> Color(0xFFF59E0B)
        ReliabilityLevel.LOW_UNCERTAIN -> Color(0xFFEF4444)
        ReliabilityLevel.UNAVAILABLE -> Color(0xFF6B7280)
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = confidenceColor.copy(alpha = 0.08f)),
        border = BorderStroke(1.dp, confidenceColor.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
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
                            text = "${report.confidenceScorePercent}% Confidence Rating",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Level: ${report.overallConfidenceLevel.title}",
                            style = MaterialTheme.typography.labelSmall,
                            color = confidenceColor,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = confidenceColor.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = "MULTI-SOURCE VERIFIED",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = confidenceColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = confidenceColor.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Confidence Basis & Verification Signals:",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            report.confidenceReasons.forEach { reason ->
                Row(
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Text("• ", fontWeight = FontWeight.Bold, color = confidenceColor)
                    Text(
                        text = reason,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
private fun PossibleMatchesSelectorCard(
    candidates: List<ProductMatchCandidate>,
    onSelectCandidate: (ProductMatchCandidate) -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Compare,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Possible Matches Detected (${candidates.size})",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = "Multiple potential product variants matched your search. Select the exact item:",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 2.dp, bottom = 8.dp)
            )

            candidates.forEach { candidate ->
                OutlinedCard(
                    onClick = { onSelectCandidate(candidate) },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = candidate.product.productName,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${candidate.product.brand} • ${candidate.product.sourceName}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = "${(candidate.matchConfidence * 100).toInt()}% Match",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ScanVsOnlineComparisonCard(scanComparison: List<ScanVsOnlineRow>) {
    var expanded by remember { mutableStateOf(true) }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Analytics,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Scan vs Online Comparison",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null
                    )
                }
            }

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    // Header Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant,
                                RoundedCornerShape(6.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "FIELD",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1.2f)
                        )
                        Text(
                            "SCANNED",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1.2f)
                        )
                        Text(
                            "ONLINE BENCHMARK",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1.4f)
                        )
                        Text(
                            "STATUS",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    scanComparison.forEach { row ->
                        val displayScanned = if (row.scannedValue.isBlank() || row.scannedValue == "N/A") "Not Detected" else row.scannedValue
                        val displayOnline = if (row.onlineValue.isBlank() || row.onlineValue == "N/A") "Not Provided" else row.onlineValue
                        val isFieldMissing = displayScanned == "Not Detected"

                        Column(modifier = Modifier.padding(vertical = 4.dp)) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp, horizontal = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = row.fieldName,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.weight(1.2f)
                                )
                                Text(
                                    text = displayScanned,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = if (isFieldMissing) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isFieldMissing) Color(0xFFEF4444) else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1.2f)
                                )
                                Text(
                                    text = displayOnline,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (displayOnline == "Not Provided") MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1.4f)
                                )

                                val (statusColor, statusBg, statusText) = when (row.status) {
                                    ComparisonStatus.MATCH -> Triple(Color(0xFF10B981), Color(0xFFD1FAE5), "MATCH")
                                    ComparisonStatus.MISMATCH -> Triple(Color(0xFFEF4444), Color(0xFFFEE2E2), "MISMATCH")
                                    ComparisonStatus.SCAN_ONLY -> Triple(Color(0xFF3B82F6), Color(0xFFDBEAFE), "SCAN ONLY")
                                    ComparisonStatus.ONLINE_ONLY -> Triple(Color(0xFF8B5CF6), Color(0xFFEDE9FE), "ONLINE ONLY")
                                    ComparisonStatus.NOT_AVAILABLE -> Triple(Color(0xFFEF4444), Color(0xFFFEE2E2), "NOT DETECTED")
                                }

                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = statusBg,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = statusText,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = statusColor,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            if (isFieldMissing) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFFFEE2E2).copy(alpha = 0.5f),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                ) {
                                    Column(modifier = Modifier.padding(6.dp)) {
                                        Text(
                                            text = "⚠️ Missing Mandatory Field: Not detected on scanned label.",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFB91C1C)
                                        )
                                        Text(
                                            text = "Rule Citation: Rule 6 of Legal Metrology (Packaged Commodities) Rules, 2011",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = "Corrective Action: Ensure field is clearly declared on Principal Display Panel before retail distribution.",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InformationConflictCard(conflicts: List<IntelConflict>) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)),
        border = BorderStroke(1.dp, Color(0xFFF59E0B)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.WarningAmber,
                    contentDescription = null,
                    tint = Color(0xFFB45309),
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Information Conflict Detected",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF78350F)
                )
            }

            conflicts.forEach { conflict ->
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    Text(
                        text = conflict.fieldName,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF92400E)
                    )
                    Text(
                        text = conflict.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF78350F)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.White.copy(alpha = 0.8f)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(
                                text = "• Authority Ranking: ${conflict.recommendedAuthority}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFB45309)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PricingIntelligenceCard(pricing: OnlinePriceInfo) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.CurrencyRupee,
                    contentDescription = null,
                    tint = Color(0xFF10B981),
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Pricing Intelligence",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("PACK MAXIMUM RETAIL PRICE (MRP)", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("₹%.2f".format(pricing.mrp ?: 0.0), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }

                if (pricing.currentOnlinePrice != null) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("ONLINE SELLING PRICE", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("₹%.2f".format(pricing.currentOnlinePrice), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (pricing.pricePerUnit != null) {
                    Text("Unit Price: ${pricing.pricePerUnit}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                }
                Text("Price Range: ${pricing.priceRange}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Source: ${pricing.priceSource}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

@Composable
private fun ManufacturerIntelligenceCard(manufacturer: ManufacturerIntel) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Business,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Manufacturer & Supply Chain",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text("Manufacturer Name:", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(manufacturer.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(4.dp))

            Text("Registered Address:", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(manufacturer.address ?: "N/A", style = MaterialTheme.typography.bodySmall)

            if (!manufacturer.packerNameAddress.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(manufacturer.packerNameAddress, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Country of Origin: ${manufacturer.countryOfOrigin}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                Text("Care: ${manufacturer.customerCarePhone}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
private fun ProductPurposeCard(usage: ProductUsagePurpose) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Product Usage & Purpose",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = usage.purposeSummary,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun PackagingDetailsCard(report: ProductIntelligenceReport) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Inventory2,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Packaging & Physical Specifications",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Net Quantity: ${report.composition?.netQuantity ?: "N/A"}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                Text("Gross Weight: ${report.composition?.grossQuantity ?: "Not Available"}", style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Package Type: ${report.composition?.packagingType ?: "N/A"}", style = MaterialTheme.typography.bodySmall)
                Text("Dimensions: ${report.composition?.dimensions ?: "Not Available"}", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun CompositionAndIngredientsCard(composition: ProductCompositionIntel) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Restaurant,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Ingredients & Composition",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            if (composition.ingredientsList.isNotEmpty()) {
                Text("Ingredients:", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(composition.ingredientsList.joinToString(", "), style = MaterialTheme.typography.bodySmall)
            }

            if (composition.allergens.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text("Allergen Notice:", style = MaterialTheme.typography.labelSmall, color = Color(0xFFD97706), fontWeight = FontWeight.Bold)
                Text(composition.allergens.joinToString(". "), style = MaterialTheme.typography.bodySmall, color = Color(0xFFB45309))
            }
        }
    }
}

@Composable
private fun CertificationsCard(certifications: List<CertificationItem>) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = Color(0xFF10B981),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Regulatory & Certification Status",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            certifications.forEach { cert ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(cert.title, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                        Text("Reg / License #: ${cert.identifierNumber}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    val (bgColor, textColor) = when (cert.status) {
                        VerificationStatus.VERIFIED -> Pair(Color(0xFFD1FAE5), Color(0xFF047857))
                        VerificationStatus.PENDING -> Pair(Color(0xFFFEF3C7), Color(0xFFB45309))
                        VerificationStatus.EXPIRED -> Pair(Color(0xFFFEE2E2), Color(0xFFB91C1C))
                        VerificationStatus.NOT_FOUND -> Pair(Color(0xFFF3F4F6), Color(0xFF6B7280))
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = bgColor
                    ) {
                        Text(
                            text = cert.status.name,
                            style = MaterialTheme.typography.labelSmall,
                            color = textColor,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
            }
        }
    }
}

@Composable
private fun LegalMetrologyChecklistCard(declarations: List<LegalMetrologyDeclarationItem>) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Gavel,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Legal Metrology Declaration Checklist (Rule 6)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            declarations.forEach { decl ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("${decl.declarationName} (${decl.ruleReference})", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                        Text("Online Benchmark: ${decl.onlineValue}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    Icon(
                        imageVector = if (decl.status == ComparisonStatus.MATCH) Icons.Default.CheckCircle else Icons.Default.Warning,
                        contentDescription = null,
                        tint = if (decl.status == ComparisonStatus.MATCH) Color(0xFF10B981) else Color(0xFFF59E0B),
                        modifier = Modifier.size(20.dp)
                    )
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
            }
        }
    }
}

@Composable
private fun SourcesTransparencyCard(sources: List<IntelSource>) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Link,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Source Transparency & Provenance",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            sources.forEach { src ->
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(src.sourceName, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                        Text(src.reliabilityLevel.title, style = MaterialTheme.typography.labelSmall, color = Color(0xFF10B981), fontWeight = FontWeight.Bold)
                    }
                    Text("${src.sourceType} • Retrieved: ${src.retrievedDate}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
            }
        }
    }
}
