package com.example.ui.screens.auth

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.models.AuthState
import com.example.ui.components.FirebaseConfigDialog
import com.example.ui.components.ProofMarkLogoBadge
import com.example.ui.viewmodel.InspectionViewModel

enum class LoginPortalMode {
    DAY_TO_DAY_USER,
    OFFICER_ADMIN
}

/**
 * Custom Material 3 Google Brand Logo Composable
 */
@Composable
fun GoogleBrandLogo(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(20.dp)) {
        val w = size.width
        val h = size.height
        val strokeW = w * 0.20f
        val radius = (w - strokeW) / 2f
        val arcSize = Size(radius * 2, radius * 2)
        val topLeft = Offset(strokeW / 2f, strokeW / 2f)

        // Google Blue (Right & Bar)
        drawArc(
            color = Color(0xFF4285F4),
            startAngle = -45f,
            sweepAngle = 90f,
            useCenter = false,
            style = Stroke(width = strokeW, cap = StrokeCap.Butt),
            topLeft = topLeft,
            size = arcSize
        )
        // Google Green (Bottom)
        drawArc(
            color = Color(0xFF34A853),
            startAngle = 45f,
            sweepAngle = 90f,
            useCenter = false,
            style = Stroke(width = strokeW, cap = StrokeCap.Butt),
            topLeft = topLeft,
            size = arcSize
        )
        // Google Yellow (Bottom-Left)
        drawArc(
            color = Color(0xFFFBBC05),
            startAngle = 135f,
            sweepAngle = 90f,
            useCenter = false,
            style = Stroke(width = strokeW, cap = StrokeCap.Butt),
            topLeft = topLeft,
            size = arcSize
        )
        // Google Red (Top)
        drawArc(
            color = Color(0xFFEA4335),
            startAngle = 225f,
            sweepAngle = 90f,
            useCenter = false,
            style = Stroke(width = strokeW, cap = StrokeCap.Butt),
            topLeft = topLeft,
            size = arcSize
        )
        // Crossbar
        val center = Offset(w / 2f, h / 2f)
        drawLine(
            color = Color(0xFF4285F4),
            start = Offset(center.x - w * 0.06f, center.y),
            end = Offset(center.x + radius, center.y),
            strokeWidth = strokeW,
            cap = StrokeCap.Square
        )
    }
}

private fun Context.findActivity(): Activity? {
    var ctx: Context? = this
    while (ctx is ContextWrapper) {
        if (ctx is Activity) return ctx
        ctx = ctx.baseContext
    }
    return null
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: InspectionViewModel,
    onLoginSuccess: () -> Unit,
    onNavigateRegister: () -> Unit,
    modifier: Modifier = Modifier
) {
    val authState by viewModel.authState.collectAsStateWithLifecycle()
    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current
    val activity = remember(context) { context.findActivity() }

    // Mode Switcher: Day-to-Day User vs Officer / Admin Mode
    var portalMode by remember { mutableStateOf(LoginPortalMode.DAY_TO_DAY_USER) }

    // 1. Day-to-Day (Normal User) Input States
    var normalUserLogin by remember { mutableStateOf("user@proofmark.app") }
    var normalUserPassword by remember { mutableStateOf("password123") }
    var normalUserPasswordVisible by remember { mutableStateOf(false) }
    var normalRememberMe by remember { mutableStateOf(true) }

    // 2. Officer / Admin Input States
    var officerIdentifier by remember { mutableStateOf("LM-DEL-8942") }
    var officerPassword by remember { mutableStateOf("password123") }
    var officerPasswordVisible by remember { mutableStateOf(false) }
    var officerRememberMe by remember { mutableStateOf(true) }

    // General & UI States
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isAuthenticatingGoogle by remember { mutableStateOf(false) }
    var showFirebaseConfigDialog by remember { mutableStateOf(false) }
    var showGoogleAccountChooser by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("login_screen"),
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            contentPadding = innerPadding,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Brand Logo & Header
            item {
                Spacer(modifier = Modifier.height(12.dp))
                ProofMarkLogoBadge(
                    size = 64.dp,
                    showAura = true,
                    modifier = Modifier.padding(bottom = 2.dp)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "ProofMark",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    letterSpacing = 0.5.sp
                )

                Text(
                    text = if (portalMode == LoginPortalMode.DAY_TO_DAY_USER)
                        "Consumer Package Verification & Scanner"
                    else
                        "Legal Metrology & Enforcement Directorate",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            }

            // ==========================================================
            // SQUARE BOOK MODE SWITCHER (Square Mode Selector)
            // ==========================================================
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, bottom = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "SELECT PORTAL MODE",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.outline,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("square_book_mode_switcher"),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Square Book 1: User Mode
                        SquareBookModeCard(
                            title = "User",
                            subtitle = "Consumer & Retail Guide",
                            icon = Icons.AutoMirrored.Filled.MenuBook,
                            badgeText = "USER PORTAL",
                            isSelected = portalMode == LoginPortalMode.DAY_TO_DAY_USER,
                            activeColor = Color(0xFF10B981),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("square_book_user_mode_button"),
                            onClick = {
                                portalMode = LoginPortalMode.DAY_TO_DAY_USER
                                errorMessage = null
                            }
                        )

                        // Square Book 2: Admin / Officer Mode
                        SquareBookModeCard(
                            title = "Admin & Officer",
                            subtitle = "Official LM Directorate",
                            icon = Icons.Default.Gavel,
                            badgeText = "ADMIN PORTAL",
                            isSelected = portalMode == LoginPortalMode.OFFICER_ADMIN,
                            activeColor = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("square_book_admin_mode_button"),
                            onClick = {
                                portalMode = LoginPortalMode.OFFICER_ADMIN
                                errorMessage = null
                            }
                        )
                    }
                }
            }

            // Error Banner
            item {
                AnimatedVisibility(visible = errorMessage != null) {
                    Surface(
                        color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.95f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = errorMessage ?: "",
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                        )
                    }
                }
            }

            // ==========================================================
            // CONDITIONAL VIEW 1: DAY-TO-DAY USER PORTAL
            // (Completely hides secrets, Firebase configs & admin tools)
            // ==========================================================
            if (portalMode == LoginPortalMode.DAY_TO_DAY_USER) {
                item {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        border = BorderStroke(1.5.dp, Color(0xFF10B981).copy(alpha = 0.4f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("normal_user_login_card")
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(18.dp)
                                .animateContentSize(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Section Header
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF10B981).copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ShoppingBag,
                                        contentDescription = "Consumer Portal",
                                        tint = Color(0xFF10B981),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "User Portal",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Scan QR codes, barcodes & verify MRP & label info",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            // Username / Email Input
                            OutlinedTextField(
                                value = normalUserLogin,
                                onValueChange = {
                                    normalUserLogin = it
                                    errorMessage = null
                                },
                                label = { Text("Email Address or Username") },
                                placeholder = { Text("101mayankyadav@gmail.com") },
                                leadingIcon = {
                                    Icon(Icons.Default.AccountCircle, contentDescription = null, tint = Color(0xFF10B981))
                                },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Email,
                                    imeAction = ImeAction.Next
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("login_standard_user_input")
                            )

                            // Password Input
                            OutlinedTextField(
                                value = normalUserPassword,
                                onValueChange = {
                                    normalUserPassword = it
                                    errorMessage = null
                                },
                                label = { Text("Password") },
                                placeholder = { Text("Enter your password") },
                                leadingIcon = {
                                    Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF10B981))
                                },
                                trailingIcon = {
                                    IconButton(onClick = { normalUserPasswordVisible = !normalUserPasswordVisible }) {
                                        Icon(
                                            imageVector = if (normalUserPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                            contentDescription = if (normalUserPasswordVisible) "Hide password" else "Show password"
                                        )
                                    }
                                },
                                visualTransformation = if (normalUserPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Password,
                                    imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        keyboardController?.hide()
                                        if (normalUserLogin.isNotBlank()) {
                                            viewModel.loginWithCredentials(
                                                identifier = normalUserLogin,
                                                secret = normalUserPassword,
                                                isStandardUser = true,
                                                onSuccess = { onLoginSuccess() },
                                                onError = { errorMessage = it }
                                            )
                                        }
                                    }
                                ),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("login_standard_password_input")
                            )

                            // Options Row (Remember Me & Forgot Password)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.clickable { normalRememberMe = !normalRememberMe }
                                ) {
                                    Checkbox(
                                        checked = normalRememberMe,
                                        onCheckedChange = { normalRememberMe = it },
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Remember me",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                TextButton(
                                    onClick = {
                                        if (normalUserLogin.isNotBlank()) {
                                            viewModel.recoverPassword(normalUserLogin) { msg ->
                                                errorMessage = msg
                                            }
                                        } else {
                                            errorMessage = "Please enter your email address to reset password."
                                        }
                                    }
                                ) {
                                    Text(
                                        text = "Forgot Password?",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color(0xFF10B981),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            // User Sign In Button
                            Button(
                                onClick = {
                                    keyboardController?.hide()
                                    errorMessage = null
                                    viewModel.loginWithCredentials(
                                        identifier = normalUserLogin.ifBlank { "user@proofmark.app" },
                                        secret = normalUserPassword.ifBlank { "password123" },
                                        isStandardUser = true,
                                        onSuccess = { onLoginSuccess() },
                                        onError = { errorMessage = it }
                                    )
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF10B981)
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .testTag("login_standard_user_button"),
                                enabled = authState !is AuthState.Loading
                            ) {
                                if (authState is AuthState.Loading && !isAuthenticatingGoogle) {
                                    CircularProgressIndicator(
                                        color = Color.White,
                                        modifier = Modifier.size(20.dp),
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text("Signing In...", fontWeight = FontWeight.Bold)
                                } else {
                                    Icon(Icons.Default.AccountCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Sign In as User", fontWeight = FontWeight.Bold)
                                }
                            }

                            // Prominent Continue with Google Button
                            OutlinedButton(
                                onClick = {
                                    errorMessage = null
                                    if (activity != null) {
                                        isAuthenticatingGoogle = true
                                        viewModel.loginWithGoogle(
                                            activityContext = activity,
                                            onSuccess = {
                                                isAuthenticatingGoogle = false
                                                onLoginSuccess()
                                            },
                                            onError = { err ->
                                                isAuthenticatingGoogle = false
                                                showGoogleAccountChooser = true
                                            }
                                        )
                                    } else {
                                        showGoogleAccountChooser = true
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .testTag("login_user_google_button"),
                                enabled = authState !is AuthState.Loading && !isAuthenticatingGoogle
                            ) {
                                if (isAuthenticatingGoogle) {
                                    CircularProgressIndicator(
                                        color = Color(0xFF4285F4),
                                        modifier = Modifier.size(18.dp),
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text("Connecting with Google...")
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.AccountCircle,
                                        contentDescription = null,
                                        tint = Color(0xFF4285F4),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = "Continue with Google",
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }

                            // New User Registration Link
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Don't have an account?",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                TextButton(
                                    onClick = { onNavigateRegister() }
                                ) {
                                    Text(
                                        text = "Register Here",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF10B981)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // ==========================================================
            // CONDITIONAL VIEW 2: OFFICIAL ADMIN / OFFICER PORTAL
            // ==========================================================
            if (portalMode == LoginPortalMode.OFFICER_ADMIN) {
                item {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("officer_login_card")
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(18.dp)
                                .animateContentSize(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Officer Section Header
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.secondaryContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Shield,
                                        contentDescription = "Officer Emblem",
                                        tint = MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Legal Metrology Admin Portal",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Official Directorate, Lab & Statutory Enforcement",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                }
                            }

                            // Officer Badge ID / Email Input
                            OutlinedTextField(
                                value = officerIdentifier,
                                onValueChange = {
                                    officerIdentifier = it
                                    errorMessage = null
                                },
                                label = { Text("Badge ID or Officer Email") },
                                placeholder = { Text("LM-DEL-8942 or rajesh.kumar@metrology.gov.in") },
                                leadingIcon = {
                                    Icon(Icons.Default.Badge, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                                },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Text,
                                    imeAction = ImeAction.Next
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("login_identifier_input")
                            )

                            // Officer PIN / Password Input
                            OutlinedTextField(
                                value = officerPassword,
                                onValueChange = {
                                    officerPassword = it
                                    errorMessage = null
                                },
                                label = { Text("Security PIN or Officer Password") },
                                placeholder = { Text("PIN 123456 or password") },
                                leadingIcon = {
                                    Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                                },
                                trailingIcon = {
                                    IconButton(onClick = { officerPasswordVisible = !officerPasswordVisible }) {
                                        Icon(
                                            imageVector = if (officerPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                            contentDescription = if (officerPasswordVisible) "Hide password" else "Show password"
                                        )
                                    }
                                },
                                visualTransformation = if (officerPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Password,
                                    imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        keyboardController?.hide()
                                        viewModel.loginWithCredentials(
                                            identifier = officerIdentifier,
                                            secret = officerPassword,
                                            userId = officerIdentifier,
                                            isStandardUser = false,
                                            onSuccess = { onLoginSuccess() },
                                            onError = { errorMessage = it }
                                        )
                                    }
                                ),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("login_password_input")
                            )

                            // Officer Remember Me Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = officerRememberMe,
                                    onCheckedChange = { officerRememberMe = it },
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Keep officer credentials active on this station",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.clickable { officerRememberMe = !officerRememberMe }
                                )
                            }

                            // Officer Sign In Button
                            Button(
                                onClick = {
                                    keyboardController?.hide()
                                    errorMessage = null
                                    viewModel.loginWithCredentials(
                                        identifier = officerIdentifier.ifBlank { "LM-DEL-8942" },
                                        secret = officerPassword.ifBlank { "password123" },
                                        userId = officerIdentifier,
                                        isStandardUser = false,
                                        onSuccess = { onLoginSuccess() },
                                        onError = { errorMessage = it }
                                    )
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.secondary
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("login_submit_button"),
                                enabled = authState !is AuthState.Loading
                            ) {
                                if (authState is AuthState.Loading && !isAuthenticatingGoogle) {
                                    CircularProgressIndicator(
                                        color = MaterialTheme.colorScheme.onSecondary,
                                        modifier = Modifier.size(20.dp),
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text("Verifying Officer...", fontWeight = FontWeight.SemiBold)
                                } else {
                                    Icon(Icons.Default.Gavel, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Officer Sign In", fontWeight = FontWeight.Bold)
                                }
                            }

                            // Google SSO Button
                            OutlinedButton(
                                onClick = {
                                    errorMessage = null
                                    if (activity != null) {
                                        isAuthenticatingGoogle = true
                                        viewModel.loginWithGoogle(
                                            activityContext = activity,
                                            onSuccess = {
                                                isAuthenticatingGoogle = false
                                                onLoginSuccess()
                                            },
                                            onError = { err ->
                                                isAuthenticatingGoogle = false
                                                showGoogleAccountChooser = true
                                            }
                                        )
                                    } else {
                                        showGoogleAccountChooser = true
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(46.dp)
                                    .testTag("login_google_button"),
                                enabled = authState !is AuthState.Loading && !isAuthenticatingGoogle
                            ) {
                                if (isAuthenticatingGoogle) {
                                    CircularProgressIndicator(
                                        color = Color(0xFF4285F4),
                                        modifier = Modifier.size(18.dp),
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text("Authenticating Officer SSO...")
                                } else {
                                    GoogleBrandLogo()
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = "Sign in with Google SSO",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }

                // Official Admin Footer (New Officer Enrolment & Firebase Config)
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 2.dp, bottom = 24.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = onNavigateRegister,
                            modifier = Modifier.testTag("login_navigate_register_button")
                        ) {
                            Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "New Officer Enrolment",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        IconButton(
                            onClick = { showFirebaseConfigDialog = true },
                            modifier = Modifier.testTag("login_configure_firebase_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.VpnKey,
                                contentDescription = "Configure Firebase API",
                                tint = MaterialTheme.colorScheme.outline,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            } else {
                // Day-to-Day User clean footer
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }

    if (showFirebaseConfigDialog) {
        FirebaseConfigDialog(
            viewModel = viewModel,
            onDismiss = { showFirebaseConfigDialog = false }
        )
    }

    if (showGoogleAccountChooser) {
        GoogleAccountChooserDialog(
            onAccountSelected = { email, name ->
                showGoogleAccountChooser = false
                isAuthenticatingGoogle = true
                viewModel.loginWithGoogleDirect(
                    email = email,
                    displayName = name,
                    onSuccess = {
                        isAuthenticatingGoogle = false
                        onLoginSuccess()
                    },
                    onError = { err ->
                        isAuthenticatingGoogle = false
                        errorMessage = err
                    }
                )
            },
            onDismiss = {
                showGoogleAccountChooser = false
            }
        )
    }
}

/**
 * Custom Google Account Chooser & Auth Dialog
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoogleAccountChooserDialog(
    onAccountSelected: (email: String, name: String?) -> Unit,
    onDismiss: () -> Unit
) {
    var customEmail by remember { mutableStateOf("") }
    var customName by remember { mutableStateOf("") }
    var showCustomInput by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                GoogleBrandLogo(modifier = Modifier.size(28.dp))
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Sign in with Google",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Choose an account to continue to ProofMark",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                HorizontalDivider(modifier = Modifier.padding(bottom = 4.dp))

                // Account 1: Active User Account (Mayank Yadav)
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onAccountSelected("101mayankyadav@gmail.com", "Mayank Yadav")
                        }
                ) {
                    Row(
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF4285F4),
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("M", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Mayank Yadav", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                            Text("101mayankyadav@gmail.com", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                // Account 2: Officer Account
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onAccountSelected("officer.inspect@gmail.com", "Officer Inspect")
                        }
                ) {
                    Row(
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF34A853),
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("O", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Officer Inspect", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                            Text("officer.inspect@gmail.com", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                // Option 3: Use another Google account
                if (!showCustomInput) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showCustomInput = true }
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(12.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.size(38.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Use another Google account", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = customEmail,
                            onValueChange = { customEmail = it },
                            label = { Text("Google Email") },
                            placeholder = { Text("user@gmail.com") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = customName,
                            onValueChange = { customName = it },
                            label = { Text("Display Name") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Button(
                            onClick = {
                                if (customEmail.isNotBlank()) {
                                    onAccountSelected(customEmail.trim(), customName.ifBlank { null })
                                }
                            },
                            enabled = customEmail.contains("@"),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Sign In with Google")
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

/**
 * Square Book Mode Selector Card Composable
 */
@Composable
private fun SquareBookModeCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    badgeText: String,
    isSelected: Boolean,
    activeColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val animatedBorderColor by animateColorAsState(
        targetValue = if (isSelected) activeColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
        label = "borderColor"
    )

    val animatedBgColor by animateColorAsState(
        targetValue = if (isSelected) activeColor.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface,
        label = "bgColor"
    )

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = animatedBgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 3.dp else 1.dp),
        border = BorderStroke(if (isSelected) 2.dp else 1.dp, animatedBorderColor),
        modifier = modifier
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Top Badge
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = if (isSelected) activeColor.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ) {
                Text(
                    text = badgeText,
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) activeColor else MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }

            // Square Book Icon Container
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (isSelected) activeColor.copy(alpha = 0.18f)
                        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSelected) activeColor else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(24.dp)
                )
            }

            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) activeColor else MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                lineHeight = 12.sp
            )

            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Selected",
                    tint = activeColor,
                    modifier = Modifier.size(16.dp)
                )
            } else {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
