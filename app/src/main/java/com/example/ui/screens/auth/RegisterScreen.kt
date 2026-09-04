package com.example.ui.screens.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import com.example.ui.theme.AppFontFamily
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
import com.example.data.models.UserRole
import com.example.ui.viewmodel.InspectionViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    viewModel: InspectionViewModel,
    onBack: () -> Unit,
    onRegistrationSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val authState by viewModel.authState.collectAsStateWithLifecycle()
    val keyboardController = LocalSoftwareKeyboardController.current

    var selectedTabIndex by remember { mutableIntStateOf(0) } // 0 = User, 1 = Officer

    // User Fields & OTP Verification State
    var fullName by remember { mutableStateOf("") }
    var userEmail by remember { mutableStateOf("") }
    var userPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var isOtpSent by remember { mutableStateOf(false) }
    var sentOtpCode by remember { mutableStateOf("") }
    var enteredOtp by remember { mutableStateOf("") }
    var isGmailVerified by remember { mutableStateOf(false) }
    var isSendingOtp by remember { mutableStateOf(false) }
    var isVerifyingOtp by remember { mutableStateOf(false) }
    var otpCooldownSeconds by remember { mutableIntStateOf(0) }
    var otpSuccessNotice by remember { mutableStateOf<String?>(null) }
    var generatedPasswordNotice by remember { mutableStateOf<String?>(null) }

    // Countdown Timer for OTP Resend
    LaunchedEffect(otpCooldownSeconds) {
        if (otpCooldownSeconds > 0) {
            delay(1000L)
            otpCooldownSeconds -= 1
        }
    }

    // Helper to generate a random strong password
    fun generateSecurePassword(): String {
        val uppercase = "ABCDEFGHJKLMNPQRSTUVWXYZ"
        val lowercase = "abcdefghjkmnpqrstuvwxyz"
        val numbers = "23456789"
        val special = "@#$*!"
        val seed = buildString {
            append(uppercase.random())
            append(lowercase.random())
            append(numbers.random())
            append(special.random())
            append(uppercase.random())
            append(lowercase.random())
            append(numbers.random())
            append(special.random())
        }.toList().shuffled().joinToString("")
        return "Proof#$seed"
    }

    // Officer Fields
    var officerName by remember { mutableStateOf("") }
    var officerEmail by remember { mutableStateOf("") }
    var badgeNumber by remember { mutableStateOf("") }
    var department by remember { mutableStateOf("Zonal Enforcement Directorate") }
    var jurisdiction by remember { mutableStateOf("State Enforcement Directorate Zone #3") }
    var phone by remember { mutableStateOf("+91 ") }
    var pin by remember { mutableStateOf("") }
    var officerPassword by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf(UserRole.ENFORCEMENT_OFFICER) }

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (selectedTabIndex == 0) "Create User Account" else "Enrol Metrology Officer",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
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
        modifier = modifier
            .fillMaxSize()
            .testTag("register_screen")
    ) { innerPadding ->
        LazyColumn(
            contentPadding = innerPadding,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    contentColor = MaterialTheme.colorScheme.primary
                ) {
                    Tab(
                        selected = selectedTabIndex == 0,
                        onClick = { selectedTabIndex = 0; errorMessage = null },
                        text = {
                            Text(
                                text = "User Registration",
                                fontWeight = if (selectedTabIndex == 0) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                    Tab(
                        selected = selectedTabIndex == 1,
                        onClick = { selectedTabIndex = 1; errorMessage = null },
                        text = {
                            Text(
                                text = "Officer Enrolment",
                                fontWeight = if (selectedTabIndex == 1) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            item {
                AnimatedVisibility(visible = errorMessage != null) {
                    Surface(
                        color = MaterialTheme.colorScheme.errorContainer,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = errorMessage ?: "",
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }

            if (selectedTabIndex == 0) {
                // --- Standard User Registration Form with Gmail OTP Verification ---
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Step 1: Identity & Gmail Verification",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF10B981)
                                )
                                if (isGmailVerified) {
                                    Surface(
                                        color = Color(0xFF10B981).copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(6.dp),
                                        border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.5f))
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = null,
                                                tint = Color(0xFF10B981),
                                                modifier = Modifier.size(12.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "GMAIL VERIFIED",
                                                color = Color(0xFF10B981),
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }

                            Text(
                                text = "Enter your full name and Gmail address. An OTP will be dispatched to verify your identity prior to setting your account password.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 16.sp
                            )

                            // Full Name Input
                            OutlinedTextField(
                                value = fullName,
                                onValueChange = {
                                    fullName = it
                                    errorMessage = null
                                },
                                label = { Text("Full Name *") },
                                placeholder = { Text("e.g. Mayank Yadav") },
                                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF10B981)) },
                                singleLine = true,
                                enabled = !isGmailVerified,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("register_user_fullname")
                            )

                            // Gmail Address Input
                            OutlinedTextField(
                                value = userEmail,
                                onValueChange = {
                                    userEmail = it.trim()
                                    errorMessage = null
                                    if (isGmailVerified) {
                                        isGmailVerified = false
                                        isOtpSent = false
                                        enteredOtp = ""
                                    }
                                },
                                label = { Text("Gmail Address *") },
                                placeholder = { Text("e.g. 101mayankyadav@gmail.com") },
                                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = Color(0xFF10B981)) },
                                trailingIcon = {
                                    if (isGmailVerified) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "Verified",
                                            tint = Color(0xFF10B981)
                                        )
                                    }
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Done),
                                singleLine = true,
                                enabled = !isGmailVerified,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("register_user_email")
                            )

                            // Send OTP Action Button (Before Gmail is verified)
                            if (!isGmailVerified) {
                                Button(
                                    onClick = {
                                        keyboardController?.hide()
                                        if (fullName.isBlank()) {
                                            errorMessage = "Please enter your full name first."
                                            return@Button
                                        }
                                        if (userEmail.isBlank() || !userEmail.contains("@")) {
                                            errorMessage = "Please enter a valid Gmail address (e.g. user@gmail.com)."
                                            return@Button
                                        }

                                        errorMessage = null
                                        isSendingOtp = true
                                        // Generate 6-digit OTP
                                        val generatedOtp = (100000..999999).random().toString()
                                        sentOtpCode = generatedOtp
                                        isOtpSent = true
                                        isSendingOtp = false
                                        otpCooldownSeconds = 60
                                        otpSuccessNotice = "Verification OTP dispatched to $userEmail"
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D9488)),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(46.dp)
                                        .testTag("send_otp_button"),
                                    enabled = !isSendingOtp && fullName.isNotBlank() && userEmail.isNotBlank() && userEmail.contains("@")
                                ) {
                                    if (isSendingOtp) {
                                        CircularProgressIndicator(
                                            color = Color.White,
                                            modifier = Modifier.size(16.dp),
                                            strokeWidth = 2.dp
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Sending OTP...", fontSize = 13.sp)
                                    } else {
                                        Icon(
                                            imageVector = if (isOtpSent) Icons.Default.Refresh else Icons.Default.Send,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = if (isOtpSent) "Resend OTP to Gmail" else "Send OTP to Gmail Address",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // OTP Verification Section
                if (isOtpSent && !isGmailVerified) {
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
                            border = BorderStroke(1.dp, Color(0xFF0D9488).copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(18.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.MarkEmailRead,
                                        contentDescription = null,
                                        tint = Color(0xFF0D9488),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = "Enter 6-Digit OTP",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                Text(
                                    text = "We have sent a 6-digit verification code to $userEmail. Please enter it below to verify your email.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 12.sp
                                )

                                // Simulated / Real Delivery Helper Pill
                                Surface(
                                    color = Color(0xFF0D9488).copy(alpha = 0.12f),
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(0.5.dp, Color(0xFF0D9488).copy(alpha = 0.3f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 12.dp, vertical = 8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Verification Code Sent:",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color(0xFF0D9488),
                                            fontWeight = FontWeight.Bold
                                        )
                                        Surface(
                                            color = Color(0xFF0D9488),
                                            shape = RoundedCornerShape(4.dp),
                                            modifier = Modifier.clickable {
                                                enteredOtp = sentOtpCode
                                                errorMessage = null
                                            }
                                        ) {
                                            Text(
                                                text = sentOtpCode,
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = FontWeight.ExtraBold,
                                                fontFamily = AppFontFamily,
                                                color = Color.White,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }

                                // OTP Input TextField
                                OutlinedTextField(
                                    value = enteredOtp,
                                    onValueChange = {
                                        if (it.length <= 6 && it.all { char -> char.isDigit() }) {
                                            enteredOtp = it
                                            errorMessage = null
                                        }
                                    },
                                    label = { Text("6-Digit OTP") },
                                    placeholder = { Text("• • • • • •") },
                                    leadingIcon = { Icon(Icons.Default.Pin, contentDescription = null, tint = Color(0xFF0D9488)) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                                    singleLine = true,
                                    textStyle = MaterialTheme.typography.titleMedium.copy(
                                        textAlign = TextAlign.Center,
                                        fontFamily = AppFontFamily,
                                        letterSpacing = 4.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("otp_input_field")
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    // Verify OTP Button
                                    Button(
                                        onClick = {
                                            keyboardController?.hide()
                                            if (enteredOtp.length != 6) {
                                                errorMessage = "Please enter the complete 6-digit OTP code."
                                                return@Button
                                            }
                                            if (enteredOtp == sentOtpCode) {
                                                isGmailVerified = true
                                                errorMessage = null
                                                otpSuccessNotice = "Gmail address $userEmail successfully verified!"
                                            } else {
                                                errorMessage = "Invalid OTP code. Please check and try again."
                                            }
                                        },
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(46.dp)
                                            .testTag("verify_otp_button"),
                                        enabled = enteredOtp.length == 6
                                    ) {
                                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Verify OTP", fontWeight = FontWeight.Bold)
                                    }

                                    // Resend OTP / Change Email
                                    OutlinedButton(
                                        onClick = {
                                            if (otpCooldownSeconds == 0) {
                                                val newOtp = (100000..999999).random().toString()
                                                sentOtpCode = newOtp
                                                enteredOtp = ""
                                                otpCooldownSeconds = 60
                                                errorMessage = null
                                                otpSuccessNotice = "New OTP sent to $userEmail"
                                            }
                                        },
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.height(46.dp),
                                        enabled = otpCooldownSeconds == 0
                                    ) {
                                        Text(
                                            text = if (otpCooldownSeconds > 0) "${otpCooldownSeconds}s" else "Resend",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Step 2: Password Setting & Generation (Unlocked ONLY after Gmail is verified)
                if (isGmailVerified) {
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(18.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Step 2: Set Account Password",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF38BDF8)
                                    )

                                    // Auto-Generate Password Pill / Action
                                    Surface(
                                        color = Color(0xFF38BDF8).copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(8.dp),
                                        border = BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.4f)),
                                        modifier = Modifier.clickable {
                                            val generated = generateSecurePassword()
                                            userPassword = generated
                                            confirmPassword = generated
                                            passwordVisible = true
                                            confirmPasswordVisible = true
                                            generatedPasswordNotice = "Secure password generated and applied!"
                                            errorMessage = null
                                        }
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.AutoAwesome,
                                                contentDescription = null,
                                                tint = Color(0xFF38BDF8),
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "Generate Password",
                                                color = Color(0xFF38BDF8),
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }

                                Text(
                                    text = "Your Gmail is verified. You can now choose a custom password or tap 'Generate Password' to create a high-security key automatically.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 16.sp
                                )

                                if (generatedPasswordNotice != null) {
                                    Surface(
                                        color = Color(0xFF38BDF8).copy(alpha = 0.1f),
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(8.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.VpnKey,
                                                contentDescription = null,
                                                tint = Color(0xFF38BDF8),
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = generatedPasswordNotice ?: "",
                                                color = Color(0xFF38BDF8),
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }
                                }

                                // Password Field
                                OutlinedTextField(
                                    value = userPassword,
                                    onValueChange = {
                                        userPassword = it
                                        errorMessage = null
                                        generatedPasswordNotice = null
                                    },
                                    label = { Text("Password *") },
                                    placeholder = { Text("Minimum 6 characters") },
                                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF38BDF8)) },
                                    trailingIcon = {
                                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                            Icon(
                                                imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                                contentDescription = null
                                            )
                                        }
                                    },
                                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
                                    singleLine = true,
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("register_user_password")
                                )

                                // Confirm Password Field
                                OutlinedTextField(
                                    value = confirmPassword,
                                    onValueChange = {
                                        confirmPassword = it
                                        errorMessage = null
                                        generatedPasswordNotice = null
                                    },
                                    label = { Text("Confirm Password *") },
                                    placeholder = { Text("Re-enter your password") },
                                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF38BDF8)) },
                                    trailingIcon = {
                                        IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                            Icon(
                                                imageVector = if (confirmPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                                contentDescription = null
                                            )
                                        }
                                    },
                                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                                    singleLine = true,
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("register_user_confirm_password")
                                )
                            }
                        }
                    }

                    // Final Create Account Button
                    item {
                        Button(
                            onClick = {
                                keyboardController?.hide()
                                if (!isGmailVerified) {
                                    errorMessage = "Please verify your Gmail address with OTP first."
                                    return@Button
                                }
                                if (userPassword.length < 6) {
                                    errorMessage = "Password must be at least 6 characters long."
                                    return@Button
                                }
                                if (userPassword != confirmPassword) {
                                    errorMessage = "Passwords do not match. Please check and try again."
                                    return@Button
                                }

                                errorMessage = null
                                viewModel.registerUser(
                                    name = fullName,
                                    email = userEmail,
                                    password = userPassword,
                                    onSuccess = { onRegistrationSuccess() },
                                    onError = { errorMessage = it }
                                )
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("register_user_submit_button"),
                            enabled = authState !is AuthState.Loading
                        ) {
                            if (authState is AuthState.Loading) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Creating Verified Account...")
                            } else {
                                Icon(Icons.Default.CheckCircle, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Create Account & Sign In", fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            } else {
                // --- Officer Enrolment Form ---
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "1. Officer Details",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )

                            OutlinedTextField(
                                value = officerName,
                                onValueChange = { officerName = it; errorMessage = null },
                                label = { Text("Full Legal Name") },
                                placeholder = { Text("e.g. Insp. Deepak Verma") },
                                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = officerEmail,
                                onValueChange = { officerEmail = it; errorMessage = null },
                                label = { Text("Official Email ID") },
                                placeholder = { Text("deepak.v@metrology.gov.in") },
                                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = badgeNumber,
                                onValueChange = { badgeNumber = it.uppercase(); errorMessage = null },
                                label = { Text("Official Badge / Inspector ID") },
                                placeholder = { Text("LM-MUM-4512") },
                                leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) },
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = department,
                                onValueChange = { department = it },
                                label = { Text("Zonal Unit / Directorate") },
                                leadingIcon = { Icon(Icons.Default.Business, contentDescription = null) },
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = jurisdiction,
                                onValueChange = { jurisdiction = it },
                                label = { Text("Station Jurisdiction") },
                                leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = phone,
                                onValueChange = { phone = it },
                                label = { Text("Official Contact Number") },
                                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "2. Assign Role & Security",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )

                            UserRole.entries.filter { it != UserRole.STANDARD_USER }.forEach { role ->
                                val isSelected = selectedRole == role
                                Surface(
                                    color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                    shape = RoundedCornerShape(10.dp),
                                    border = BorderStroke(
                                        if (isSelected) 1.5.dp else 1.dp,
                                        if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { selectedRole = role }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = role.title,
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = role.permissions,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        if (isSelected) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            OutlinedTextField(
                                value = pin,
                                onValueChange = { if (it.length <= 6) pin = it; errorMessage = null },
                                label = { Text("6-Digit Security PIN") },
                                placeholder = { Text("849201") },
                                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = officerPassword,
                                onValueChange = { officerPassword = it; errorMessage = null },
                                label = { Text("Password") },
                                placeholder = { Text("Minimum 6 characters") },
                                leadingIcon = { Icon(Icons.Default.Security, contentDescription = null) },
                                visualTransformation = PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                item {
                    Button(
                        onClick = {
                            keyboardController?.hide()
                            if (officerName.isBlank()) {
                                errorMessage = "Please enter officer full legal name."
                                return@Button
                            }
                            if (officerEmail.isBlank() || !officerEmail.contains("@")) {
                                errorMessage = "Please enter a valid email address."
                                return@Button
                            }
                            if (badgeNumber.isBlank()) {
                                errorMessage = "Please enter official badge number."
                                return@Button
                            }
                            if (pin.length < 4) {
                                errorMessage = "Security PIN must be at least 4 digits."
                                return@Button
                            }
                            if (officerPassword.length < 6) {
                                errorMessage = "Password must be at least 6 characters."
                                return@Button
                            }

                            errorMessage = null
                            viewModel.registerOfficer(
                                name = officerName,
                                email = officerEmail,
                                badgeNumber = badgeNumber,
                                role = selectedRole,
                                department = department,
                                pin = pin,
                                password = officerPassword,
                                phone = phone,
                                jurisdiction = jurisdiction,
                                onSuccess = { onRegistrationSuccess() },
                                onError = { errorMessage = it }
                            )
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("register_officer_submit_button"),
                        enabled = authState !is AuthState.Loading
                    ) {
                        if (authState is AuthState.Loading) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Enrolling Officer...")
                        } else {
                            Icon(Icons.Default.Security, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Enrol & Authenticate Officer", fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}
