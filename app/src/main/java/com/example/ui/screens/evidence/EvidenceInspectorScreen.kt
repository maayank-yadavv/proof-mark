package com.example.ui.screens.evidence

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CropFree
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.models.BoundingBox
import com.example.data.models.BoundingBoxJson
import com.example.data.models.ComplianceStatus
import com.example.data.models.PackageAngle
import com.example.data.models.QualityMetrics
import com.example.ui.components.InteractiveBoundingBoxViewer
import com.example.ui.components.OcrConfidenceBadge
import com.example.ui.components.OcrConfidenceOverlay
import com.example.ui.components.QualityScoreCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.ComplianceFail
import com.example.ui.theme.CompliancePass
import com.example.ui.theme.ComplianceReview
import com.example.ui.viewmodel.InspectionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EvidenceInspectorScreen(
    inspectionId: String,
    viewModel: InspectionViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val inspection by viewModel.repository.getInspectionById(inspectionId).collectAsStateWithLifecycle(null)
    val declarations by viewModel.repository.getDeclarations(inspectionId).collectAsStateWithLifecycle(emptyList())
    val images by viewModel.repository.getImages(inspectionId).collectAsStateWithLifecycle(emptyList())
    val ocrResults by viewModel.repository.getOcrResults(inspectionId).collectAsStateWithLifecycle(emptyList())

    // Parse all bounding boxes safely
    val allBoundingBoxes = remember(declarations, ocrResults) {
        val boxes = mutableListOf<BoundingBox>()
        ocrResults.forEach { ocr ->
            val parsed = BoundingBoxJson.fromJson(ocr.boundingBoxesJson)
            if (parsed.isNotEmpty()) boxes.addAll(parsed)
        }
        if (boxes.isEmpty()) {
            declarations.forEach { d ->
                if (!d.boundingBoxJson.isNullOrBlank()) {
                    val parsed = BoundingBoxJson.fromJson(d.boundingBoxJson)
                    if (parsed.isNotEmpty()) boxes.addAll(parsed)
                }
            }
        }
        boxes
    }

    var selectedFieldKey by remember { mutableStateOf<String?>(allBoundingBoxes.firstOrNull()?.fieldKey) }
    var selectedImageIndex by remember { mutableStateOf(0) }

    val currentQuality = remember(images) {
        val img = images.firstOrNull()
        if (img != null) {
            QualityMetrics(
                overallScore = img.qualityScore,
                sharpnessScore = img.sharpnessScore,
                lightingScore = 90,
                glareScore = img.glareScore,
                readabilityRating = img.readabilityRating,
                isAcceptableForLegalEvidence = img.qualityScore >= 65
            )
        } else {
            QualityMetrics(92, 94, 90, 92, "Excellent", true)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Evidence Inspector & Bounding Crops",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = inspection?.inspectionNumber ?: "Evidence Audit",
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
        modifier = modifier.testTag("evidence_inspector_screen")
    ) { innerPadding ->
        LazyColumn(
            contentPadding = innerPadding,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
                // Multi-Angle Selector Chips
                Text(
                    text = "Package Viewing Angles",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    val angles = if (images.isNotEmpty()) images.map { it.angle } else listOf(PackageAngle.FRONT, PackageAngle.BACK, PackageAngle.SIDE)
                    items(angles.size) { index ->
                        val angle = angles[index]
                        val isSelected = selectedImageIndex == index
                        Surface(
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.clickable { selectedImageIndex = index }
                        ) {
                            Text(
                                text = angle.label,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            item {
                // Interactive Bounding Box Viewer Canvas
                InteractiveBoundingBoxViewer(
                    boxes = allBoundingBoxes,
                    selectedBoxKey = selectedFieldKey,
                    onBoxSelected = { box ->
                        selectedFieldKey = box.fieldKey
                    }
                )
            }

            item {
                QualityScoreCard(metrics = currentQuality)
            }

            item {
                Text(
                    text = "Statutory Declarations & Bounding Crops",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            items(declarations, key = { it.id }) { decl ->
                val isSelected = decl.fieldKey.equals(selectedFieldKey, ignoreCase = true)
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surface
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        if (isSelected) 2.dp else 1.dp,
                        if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedFieldKey = decl.fieldKey }
                        .testTag("evidence_decl_${decl.fieldKey}")
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CropFree,
                                    contentDescription = null,
                                    tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = decl.fieldName,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            OcrConfidenceBadge(
                                confidence = decl.confidence,
                                compact = false,
                                showLabel = true
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = if (decl.extractedValue.isNotBlank()) decl.extractedValue else "[Missing / Not Declared]",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (decl.extractedValue.isNotBlank()) MaterialTheme.colorScheme.onSurface else ComplianceFail,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                        )

                        Text(
                            text = "Rule Reference: ${decl.sourceRuleCode ?: "LM Rules 2011"}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}
