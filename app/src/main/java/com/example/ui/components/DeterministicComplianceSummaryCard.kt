package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.GppBad
import androidx.compose.material.icons.filled.GppGood
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Rule
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import com.example.ui.theme.AppFontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ai.ExtractedPackageData
import com.example.data.local.entities.ComplianceCheckEntity
import com.example.data.local.entities.InspectionEntity
import com.example.data.models.ComplianceStatus
import com.example.data.models.RuleSeverity
import com.example.ui.theme.ComplianceFail
import com.example.ui.theme.CompliancePass
import com.example.ui.theme.ComplianceReview

/**
 * Visual deterministic rule item representation used for instant color-coded feedback.
 */
data class RuleStatusItem(
    val ruleCode: String,
    val shortName: String,
    val status: ComplianceStatus,
    val severity: RuleSeverity,
    val finding: String,
    val legalSection: String
)

/**
 * High-Impact Visual Summary Card highlighting Pass/Fail compliance status
 * based on deterministic Legal Metrology rules applied to extracted package data.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DeterministicComplianceSummaryCard(
    status: ComplianceStatus,
    complianceScore: Int,
    checks: List<RuleStatusItem>,
    productName: String? = null,
    brandName: String? = null,
    modifier: Modifier = Modifier,
    onRuleClick: ((RuleStatusItem) -> Unit)? = null
) {
    var isExpanded by remember { mutableStateOf(false) }

    val passCount = checks.count { it.status == ComplianceStatus.PASS }
    val failCount = checks.count { it.status == ComplianceStatus.POTENTIAL_NON_COMPLIANCE }
    val reviewCount = checks.count { it.status == ComplianceStatus.REQUIRES_REVIEW }

    val isPass = status == ComplianceStatus.PASS && failCount == 0
    val isFail = status == ComplianceStatus.POTENTIAL_NON_COMPLIANCE || failCount > 0

    val primaryStatusColor by animateColorAsState(
        targetValue = when {
            isFail -> Color(0xFFEF4444)
            isPass -> Color(0xFF10B981)
            else -> Color(0xFFF59E0B)
        },
        label = "status_color"
    )

    val containerBgColor = when {
        isFail -> Color(0xFF1C0A0A)
        isPass -> Color(0xFF041E15)
        else -> Color(0xFF1C1505)
    }

    val animatedScore by animateFloatAsState(
        targetValue = (complianceScore.coerceIn(0, 100)) / 100f,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "score_progress"
    )

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerBgColor
        ),
        border = BorderStroke(1.5.dp, primaryStatusColor.copy(alpha = 0.8f)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("deterministic_compliance_summary_card")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Status Badge + Deterministic Rules Engine Tag
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Large High-Contrast Status Pill
                Surface(
                    color = primaryStatusColor,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .testTag("compliance_status_pill")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                    ) {
                        Icon(
                            imageVector = when {
                                isFail -> Icons.Default.GppBad
                                isPass -> Icons.Default.GppGood
                                else -> Icons.Default.Warning
                            },
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = when {
                                isFail -> "NON-COMPLIANT (FAIL)"
                                isPass -> "COMPLIANT (PASS)"
                                else -> "REQUIRES REVIEW"
                            },
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 11.sp,
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Deterministic Verification Badge
                Surface(
                    color = Color.White.copy(alpha = 0.08f),
                    shape = RoundedCornerShape(6.dp),
                    border = BorderStroke(0.5.dp, Color.White.copy(alpha = 0.2f))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Rule,
                            contentDescription = null,
                            tint = Color(0xFF00E5FF),
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "RULE ENGINE v3.4",
                            color = Color(0xFF00E5FF),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = AppFontFamily,
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Product / Brand Title (if available)
            if (!productName.isNullOrBlank()) {
                Text(
                    text = productName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 16.sp,
                    lineHeight = 20.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                if (!brandName.isNullOrBlank()) {
                    Text(
                        text = "Brand: $brandName",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 12.sp
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Compliance Score & Metric Counter Gauge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    Text(
                        text = "DETERMINISTIC COMPLIANCE SCORE",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.6f),
                        letterSpacing = 0.5.sp,
                        fontSize = 9.sp,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "$complianceScore%",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = primaryStatusColor,
                            fontSize = 22.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isPass) "All Passed" else if (failCount > 0) "$failCount Violation${if (failCount > 1) "s" else ""}" else "Under Review",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 12.sp,
                            maxLines = 1
                        )
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Rule Counts Pill Bar
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CounterBadge(
                        count = passCount,
                        label = "PASS",
                        color = Color(0xFF10B981),
                        icon = Icons.Default.Check
                    )
                    CounterBadge(
                        count = failCount,
                        label = "FAIL",
                        color = Color(0xFFEF4444),
                        icon = Icons.Default.Close
                    )
                    if (reviewCount > 0) {
                        CounterBadge(
                            count = reviewCount,
                            label = "REV",
                            color = Color(0xFFF59E0B),
                            icon = Icons.Default.HelpOutline
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Progress Bar Indicator
            LinearProgressIndicator(
                progress = { animatedScore },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = primaryStatusColor,
                trackColor = Color.White.copy(alpha = 0.12f),
                strokeCap = StrokeCap.Round
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Primary Highlight Alert Box
            if (isFail) {
                val topFailures = checks.filter { it.status == ComplianceStatus.POTENTIAL_NON_COMPLIANCE }
                Surface(
                    color = Color(0xFFEF4444).copy(alpha = 0.15f),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = null,
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Legal Metrology Notice / Violation:",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFF8A80),
                                fontSize = 11.sp
                            )
                            topFailures.take(2).forEach { f ->
                                Text(
                                    text = "• ${f.shortName}: ${f.finding}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            } else if (isPass) {
                Surface(
                    color = Color(0xFF10B981).copy(alpha = 0.15f),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "All mandatory statutory declarations under Rule 6(1) & Sec 18 are present and formatted correctly.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFE8F5E9),
                            fontSize = 11.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Quick Color-Coded Statutory Rule Matrix (Interactive Chips)
            Text(
                text = "STATUTORY RULES EVALUATION MATRIX",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 9.sp,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                checks.forEach { ruleItem ->
                    RuleMatrixChip(
                        item = ruleItem,
                        onClick = {
                            if (onRuleClick != null) onRuleClick(ruleItem)
                            else isExpanded = !isExpanded
                        }
                    )
                }
            }

            // Toggle Expand / Collapse Rule Breakdown
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .clickable { isExpanded = !isExpanded }
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isExpanded) "Hide Full Rule Breakdown" else "View All ${checks.size} Deterministic Rules",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00E5FF)
                )
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = Color(0xFF00E5FF),
                    modifier = Modifier.size(18.dp)
                )
            }

            // Expanded Detailed Rules List
            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
                    checks.forEach { item ->
                        DetailedRuleRow(item = item)
                    }
                }
            }
        }
    }
}

@Composable
private fun CounterBadge(
    count: Int,
    label: String,
    color: Color,
    icon: ImageVector
) {
    Surface(
        color = color.copy(alpha = 0.18f),
        shape = RoundedCornerShape(6.dp),
        border = BorderStroke(0.8.dp, color.copy(alpha = 0.6f))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.5.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(11.dp)
            )
            Spacer(modifier = Modifier.width(3.5.dp))
            Text(
                text = "$count $label",
                color = color,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = AppFontFamily,
                maxLines = 1,
                softWrap = false
            )
        }
    }
}

@Composable
private fun RuleMatrixChip(
    item: RuleStatusItem,
    onClick: () -> Unit
) {
    val chipColor = when (item.status) {
        ComplianceStatus.PASS -> Color(0xFF10B981)
        ComplianceStatus.POTENTIAL_NON_COMPLIANCE -> Color(0xFFEF4444)
        ComplianceStatus.REQUIRES_REVIEW -> Color(0xFFF59E0B)
        ComplianceStatus.DRAFT -> Color(0xFF94A3B8)
    }

    Surface(
        color = chipColor.copy(alpha = 0.15f),
        shape = RoundedCornerShape(6.dp),
        border = BorderStroke(0.8.dp, chipColor.copy(alpha = 0.7f)),
        modifier = Modifier
            .clickable { onClick() }
            .testTag("rule_matrix_chip_${item.ruleCode}")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(chipColor, CircleShape)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = item.shortName,
                color = Color.White,
                fontSize = 10.5.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                softWrap = false
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = if (item.status == ComplianceStatus.PASS) "✓" else "✕",
                color = chipColor,
                fontSize = 10.5.sp,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 1,
                softWrap = false
            )
        }
    }
}

@Composable
private fun DetailedRuleRow(item: RuleStatusItem) {
    val color = when (item.status) {
        ComplianceStatus.PASS -> Color(0xFF10B981)
        ComplianceStatus.POTENTIAL_NON_COMPLIANCE -> Color(0xFFEF4444)
        ComplianceStatus.REQUIRES_REVIEW -> Color(0xFFF59E0B)
        ComplianceStatus.DRAFT -> Color(0xFF94A3B8)
    }

    Surface(
        color = Color(0xFF1E293B).copy(alpha = 0.6f),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(0.5.dp, Color.White.copy(alpha = 0.1f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.Top
        ) {
            Surface(
                color = color.copy(alpha = 0.2f),
                shape = RoundedCornerShape(4.dp),
                border = BorderStroke(0.5.dp, color)
            ) {
                Text(
                    text = item.status.shortCode,
                    color = color,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.shortName,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 11.sp
                    )
                    Text(
                        text = item.legalSection,
                        color = Color(0xFF00E5FF),
                        fontSize = 9.sp,
                        fontFamily = AppFontFamily
                    )
                }
                Text(
                    text = item.finding,
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 10.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}

/**
 * Convenience builder function to map Room Database Inspection & ComplianceCheck entities
 * into the deterministic summary card.
 */
@Composable
fun InspectionComplianceSummaryCard(
    inspection: InspectionEntity,
    checks: List<ComplianceCheckEntity>,
    modifier: Modifier = Modifier,
    onRuleClick: ((ComplianceCheckEntity) -> Unit)? = null
) {
    val ruleItems = remember(checks) {
        if (checks.isNotEmpty()) {
            checks.map { c ->
                RuleStatusItem(
                    ruleCode = c.ruleCode,
                    shortName = mapRuleCodeToShortName(c.ruleCode),
                    status = c.status,
                    severity = c.severity,
                    finding = c.findingMessage,
                    legalSection = c.legalSection
                )
            }
        } else {
            // Default 8 Legal Metrology baseline rules if checks list is empty
            generateDefaultRuleItems(inspection.status)
        }
    }

    DeterministicComplianceSummaryCard(
        status = inspection.status,
        complianceScore = inspection.complianceScore,
        checks = ruleItems,
        productName = inspection.productName,
        brandName = inspection.brand,
        modifier = modifier,
        onRuleClick = { item ->
            val orig = checks.find { it.ruleCode == item.ruleCode }
            if (orig != null && onRuleClick != null) onRuleClick(orig)
        }
    )
}

/**
 * Convenience builder for live camera / on-device OCR extracted package data.
 */
@Composable
fun ExtractedDataComplianceSummaryCard(
    data: ExtractedPackageData,
    modifier: Modifier = Modifier
) {
    val ruleItems = remember(data) {
        evaluateExtractedDataToRuleItems(data)
    }

    val failCount = ruleItems.count { it.status == ComplianceStatus.POTENTIAL_NON_COMPLIANCE }
    val overallStatus = if (failCount == 0) ComplianceStatus.PASS else ComplianceStatus.POTENTIAL_NON_COMPLIANCE
    val score = (((8 - failCount).toFloat() / 8f) * 100).toInt()

    DeterministicComplianceSummaryCard(
        status = overallStatus,
        complianceScore = score,
        checks = ruleItems,
        productName = data.productName.takeIf { it.isNotBlank() && !it.contains("Not Provided", ignoreCase = true) },
        brandName = data.manufacturerName.takeIf { it.isNotBlank() && !it.contains("Not Provided", ignoreCase = true) },
        modifier = modifier
    )
}

private fun mapRuleCodeToShortName(code: String): String {
    return when (code.uppercase()) {
        "LM-PC-6-1-A" -> "Rule 6(1)(a) Manufacturer"
        "LM-PC-6-1-B" -> "Rule 6(1)(b) Commodity Name"
        "LM-PC-6-1-C" -> "Rule 6(1)(c) Net Qty SI Units"
        "LM-PC-6-1-D" -> "Rule 6(1)(d) Mfg / Pkg Date"
        "LM-PC-6-1-DA" -> "Rule 6(1)(da) Unit Sale Price"
        "LM-PC-6-1-E" -> "Rule 6(1)(e) MRP & Taxes"
        "LM-PC-6-1-F" -> "Rule 6(1)(f) Consumer Care"
        "LM-PC-6-10" -> "Rule 6(10) Origin"
        "LM-PC-7-PDP" -> "Rule 7 PDP Font"
        else -> code
    }
}

private fun evaluateExtractedDataToRuleItems(data: ExtractedPackageData): List<RuleStatusItem> {
    val items = mutableListOf<RuleStatusItem>()

    // 1. Manufacturer
    val mfgValid = (data.manufacturerName.isNotBlank() && !data.manufacturerName.contains("Not Provided", ignoreCase = true)) ||
                   (data.manufacturerAddress.isNotBlank() && !data.manufacturerAddress.contains("Not Provided", ignoreCase = true))
    items.add(
        RuleStatusItem(
            ruleCode = "LM-PC-6-1-A",
            shortName = "Rule 6(1)(a) Mfr/Packer",
            status = if (mfgValid) ComplianceStatus.PASS else ComplianceStatus.POTENTIAL_NON_COMPLIANCE,
            severity = RuleSeverity.CRITICAL,
            finding = if (mfgValid) "Manufacturer details detected: ${data.manufacturerName}" else "Missing manufacturer/packer name and address",
            legalSection = "Rule 6(1)(a)"
        )
    )

    // 2. Generic Name
    val nameValid = data.productName.isNotBlank() && !data.productName.contains("Not Provided", ignoreCase = true)
    items.add(
        RuleStatusItem(
            ruleCode = "LM-PC-6-1-B",
            shortName = "Rule 6(1)(b) Generic Name",
            status = if (nameValid) ComplianceStatus.PASS else ComplianceStatus.POTENTIAL_NON_COMPLIANCE,
            severity = RuleSeverity.MAJOR,
            finding = if (nameValid) "Generic commodity name declared: ${data.productName}" else "Missing common or generic commodity name on PDP",
            legalSection = "Rule 6(1)(b)"
        )
    )

    // 3. Net Quantity (SI Units)
    val qtyValid = data.netQuantity.isNotBlank() && !data.netQuantity.contains("Not Provided", ignoreCase = true) &&
                   (data.netQuantity.contains(Regex("""\b(g|kg|ml|l|m|N)\b""", RegexOption.IGNORE_CASE)))
    items.add(
        RuleStatusItem(
            ruleCode = "LM-PC-6-1-C",
            shortName = "Rule 6(1)(c) Net Qty",
            status = if (qtyValid) ComplianceStatus.PASS else ComplianceStatus.POTENTIAL_NON_COMPLIANCE,
            severity = RuleSeverity.CRITICAL,
            finding = if (qtyValid) "Standard SI metric net quantity declared: ${data.netQuantity}" else "Missing or non-standard metric net quantity",
            legalSection = "Rule 6(1)(c)"
        )
    )

    // 4. Mfg Date
    val dateValid = data.dateOfMfg.isNotBlank() && !data.dateOfMfg.contains("Not Provided", ignoreCase = true)
    items.add(
        RuleStatusItem(
            ruleCode = "LM-PC-6-1-D",
            shortName = "Rule 6(1)(d) Mfg Date",
            status = if (dateValid) ComplianceStatus.PASS else ComplianceStatus.POTENTIAL_NON_COMPLIANCE,
            severity = RuleSeverity.MAJOR,
            finding = if (dateValid) "Month & year of manufacture stated: ${data.dateOfMfg}" else "Month and year of manufacture missing from label",
            legalSection = "Rule 6(1)(d)"
        )
    )

    // 5. Unit Sale Price
    val uspValid = data.unitSalePrice.isNotBlank() && !data.unitSalePrice.contains("Not Provided", ignoreCase = true)
    items.add(
        RuleStatusItem(
            ruleCode = "LM-PC-6-1-DA",
            shortName = "Rule 6(1)(da) USP",
            status = if (uspValid) ComplianceStatus.PASS else ComplianceStatus.POTENTIAL_NON_COMPLIANCE,
            severity = RuleSeverity.MAJOR,
            finding = if (uspValid) "Mandatory Unit Sale Price declared: ${data.unitSalePrice}" else "Unit Sale Price (USP in ₹/g or ₹/ml) missing",
            legalSection = "Rule 6(1)(da)"
        )
    )

    // 6. MRP
    val mrpValid = data.mrp.isNotBlank() && !data.mrp.contains("Not Provided", ignoreCase = true)
    items.add(
        RuleStatusItem(
            ruleCode = "LM-PC-6-1-E",
            shortName = "Rule 6(1)(e) MRP",
            status = if (mrpValid) ComplianceStatus.PASS else ComplianceStatus.POTENTIAL_NON_COMPLIANCE,
            severity = RuleSeverity.CRITICAL,
            finding = if (mrpValid) "Maximum Retail Price declared: ${data.mrp}" else "MRP declaration missing from package",
            legalSection = "Rule 6(1)(e)"
        )
    )

    // 7. Consumer Care
    val careValid = data.consumerCare.isNotBlank() && !data.consumerCare.contains("Not Provided", ignoreCase = true)
    items.add(
        RuleStatusItem(
            ruleCode = "LM-PC-6-1-F",
            shortName = "Rule 6(1)(f) Consumer Care",
            status = if (careValid) ComplianceStatus.PASS else ComplianceStatus.POTENTIAL_NON_COMPLIANCE,
            severity = RuleSeverity.CRITICAL,
            finding = if (careValid) "Consumer grievance contact details present: ${data.consumerCare}" else "Consumer care phone/email contact missing",
            legalSection = "Rule 6(1)(f)"
        )
    )

    // 8. Origin
    val originValid = data.countryOfOrigin.isNotBlank() && !data.countryOfOrigin.contains("Not Provided", ignoreCase = true)
    items.add(
        RuleStatusItem(
            ruleCode = "LM-PC-6-10",
            shortName = "Rule 6(10) Origin",
            status = if (originValid) ComplianceStatus.PASS else ComplianceStatus.POTENTIAL_NON_COMPLIANCE,
            severity = RuleSeverity.MAJOR,
            finding = if (originValid) "Country of origin declared: ${data.countryOfOrigin}" else "Country of origin missing from packaging",
            legalSection = "Rule 6(10)"
        )
    )

    return items
}

private fun generateDefaultRuleItems(status: ComplianceStatus): List<RuleStatusItem> {
    val isPass = status == ComplianceStatus.PASS
    return listOf(
        RuleStatusItem("LM-PC-6-1-A", "Rule 6(1)(a) Mfr/Packer", if (isPass) ComplianceStatus.PASS else ComplianceStatus.POTENTIAL_NON_COMPLIANCE, RuleSeverity.CRITICAL, "Manufacturer & Packer address", "Rule 6(1)(a)"),
        RuleStatusItem("LM-PC-6-1-B", "Rule 6(1)(b) Commodity Name", ComplianceStatus.PASS, RuleSeverity.MAJOR, "Generic Commodity Name on PDP", "Rule 6(1)(b)"),
        RuleStatusItem("LM-PC-6-1-C", "Rule 6(1)(c) Net Qty", if (isPass) ComplianceStatus.PASS else ComplianceStatus.POTENTIAL_NON_COMPLIANCE, RuleSeverity.CRITICAL, "Net quantity in standard SI metric units", "Rule 6(1)(c)"),
        RuleStatusItem("LM-PC-6-1-D", "Rule 6(1)(d) Mfg Date", ComplianceStatus.PASS, RuleSeverity.MAJOR, "Month & Year of Manufacture", "Rule 6(1)(d)"),
        RuleStatusItem("LM-PC-6-1-DA", "Rule 6(1)(da) USP", if (isPass) ComplianceStatus.PASS else ComplianceStatus.POTENTIAL_NON_COMPLIANCE, RuleSeverity.MAJOR, "Unit Sale Price per g/ml", "Rule 6(1)(da)"),
        RuleStatusItem("LM-PC-6-1-E", "Rule 6(1)(e) MRP", ComplianceStatus.PASS, RuleSeverity.CRITICAL, "Maximum Retail Price inclusive of all taxes", "Rule 6(1)(e)"),
        RuleStatusItem("LM-PC-6-1-F", "Rule 6(1)(f) Consumer Care", ComplianceStatus.PASS, RuleSeverity.CRITICAL, "Consumer Grievance Contact", "Rule 6(1)(f)"),
        RuleStatusItem("LM-PC-6-10", "Rule 6(10) Origin", ComplianceStatus.PASS, RuleSeverity.MAJOR, "Country of Origin", "Rule 6(10)")
    )
}
