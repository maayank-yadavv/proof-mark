package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.auth.FirebaseConfig
import com.example.ui.theme.CompliancePass
import com.example.ui.viewmodel.InspectionViewModel

@Composable
fun FirebaseConfigDialog(
    viewModel: InspectionViewModel,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val config by viewModel.firebaseConfig.collectAsStateWithLifecycle()

    var webClientId by remember(config.webClientId) { mutableStateOf(config.webClientId) }
    var apiKey by remember(config.apiKey) { mutableStateOf(config.apiKey) }
    var projectId by remember(config.projectId) { mutableStateOf(config.projectId) }
    var appId by remember(config.appId) { mutableStateOf(config.appId) }

    var testStatusResult by remember { mutableStateOf<String?>(null) }
    var isTesting by remember { mutableStateOf(false) }
    var showInstructions by remember { mutableStateOf(false) }
    var saveSuccessFeedback by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier.testTag("firebase_config_dialog"),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = Color(0xFFFFCA28).copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Cloud,
                            contentDescription = null,
                            tint = Color(0xFFF57C00),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Firebase & Cloud API Setup",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Manage Cloud Identity, Web Client ID & API Keys",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Info banner
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Provide your Firebase Web Client ID and API keys below to enable Google SSO and Firebase cloud sync for this applet.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Success Message
                AnimatedVisibility(visible = saveSuccessFeedback != null) {
                    Surface(
                        color = CompliancePass.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = CompliancePass,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = saveSuccessFeedback ?: "",
                                color = CompliancePass,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                // Web Client ID field (Crucial for Google Sign-In with Credential Manager)
                OutlinedTextField(
                    value = webClientId,
                    onValueChange = {
                        webClientId = it
                        saveSuccessFeedback = null
                    },
                    label = { Text("OAuth 2.0 Web Client ID (Required for Google SSO)") },
                    placeholder = { Text("e.g. 1234567890-abcdef.apps.googleusercontent.com") },
                    leadingIcon = {
                        Icon(Icons.Default.Link, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("firebase_web_client_id_input"),
                    shape = RoundedCornerShape(10.dp)
                )

                // Firebase Web API Key
                OutlinedTextField(
                    value = apiKey,
                    onValueChange = {
                        apiKey = it
                        saveSuccessFeedback = null
                    },
                    label = { Text("Firebase Web API Key (Optional)") },
                    placeholder = { Text("e.g. AIzaSyB...") },
                    leadingIcon = {
                        Icon(Icons.Default.VpnKey, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("firebase_api_key_input"),
                    shape = RoundedCornerShape(10.dp)
                )

                // Project ID & App ID in a row
                OutlinedTextField(
                    value = projectId,
                    onValueChange = {
                        projectId = it
                        saveSuccessFeedback = null
                    },
                    label = { Text("Firebase Project ID") },
                    placeholder = { Text("e.g. proofmark-legal-metrology") },
                    leadingIcon = {
                        Icon(Icons.Default.Cloud, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("firebase_project_id_input"),
                    shape = RoundedCornerShape(10.dp)
                )

                OutlinedTextField(
                    value = appId,
                    onValueChange = {
                        appId = it
                        saveSuccessFeedback = null
                    },
                    label = { Text("Firebase App ID (Mobile SDK)") },
                    placeholder = { Text("e.g. 1:1234567890:android:abc123def") },
                    leadingIcon = {
                        Icon(Icons.Default.Key, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("firebase_app_id_input"),
                    shape = RoundedCornerShape(10.dp)
                )

                // Diagnostic / Test Status Output
                AnimatedVisibility(visible = testStatusResult != null) {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Science,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Diagnostics & Connection Status",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = testStatusResult ?: "",
                                style = MaterialTheme.typography.bodySmall,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }

                // Help Instructions Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = { showInstructions = !showInstructions }) {
                        Icon(Icons.AutoMirrored.Filled.HelpOutline, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (showInstructions) "Hide Setup Guide" else "How to get Firebase Keys?")
                    }

                    OutlinedButton(
                        onClick = {
                            isTesting = true
                            testStatusResult = null
                            val res = viewModel.testFirebaseStatus()
                            res.onSuccess {
                                testStatusResult = it
                                isTesting = false
                            }.onFailure {
                                testStatusResult = "Error: ${it.message}"
                                isTesting = false
                            }
                        },
                        modifier = Modifier.testTag("test_firebase_connection_button")
                    ) {
                        if (isTesting) {
                            CircularProgressIndicator(modifier = Modifier.size(14.dp), strokeWidth = 1.5.dp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Testing...", style = MaterialTheme.typography.labelSmall)
                        } else {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Test Diagnostics", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }

                AnimatedVisibility(visible = showInstructions) {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "Firebase Console Quick Steps:",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "1. Open console.firebase.google.com > Your Project.\n" +
                                        "2. Navigate to Authentication > Sign-in method > Google.\n" +
                                        "3. Expand 'Web SDK configuration' to copy Web Client ID.\n" +
                                        "4. Go to Project Settings > General for Web API Key & Project ID.",
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 11.5.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    viewModel.saveFirebaseConfig(
                        webClientId = webClientId,
                        apiKey = apiKey,
                        projectId = projectId,
                        appId = appId
                    )
                    saveSuccessFeedback = "Firebase & OAuth API credentials saved successfully!"
                },
                modifier = Modifier.testTag("save_firebase_config_button")
            ) {
                Icon(Icons.Default.CloudDone, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Save & Apply")
            }
        },
        dismissButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                TextButton(
                    onClick = {
                        viewModel.resetFirebaseConfig()
                        webClientId = ""
                        apiKey = ""
                        projectId = ""
                        appId = ""
                        saveSuccessFeedback = "Reset to default runtime configuration."
                    }
                ) {
                    Text("Reset Defaults", color = MaterialTheme.colorScheme.error)
                }

                OutlinedButton(onClick = onDismiss) {
                    Text("Close")
                }
            }
        }
    )
}
