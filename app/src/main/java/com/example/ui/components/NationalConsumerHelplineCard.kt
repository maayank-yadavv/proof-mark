package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import com.example.ui.theme.AppFontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Interactive National Consumer Helpline (NCH) Action Card
 *
 * Supported Actions:
 * 1. Toll-Free Phone Call to 1915 (redirects to Call Manager / Dialer)
 * 2. SMS Dispatch to 8800001915 (redirects to Default SMS Composer)
 * 3. WhatsApp Direct Chat to +91 88000 01915 (redirects to WhatsApp NCH)
 */
@Composable
fun NationalConsumerHelplineCard(
    modifier: Modifier = Modifier,
    title: String = "National Consumer Helpline",
    description: String = "Report Legal Metrology violations, overcharging above MRP, missing mandatory declarations, or deceptive packaging directly to the Department of Consumer Affairs."
) {
    val context = LocalContext.current

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.4f)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("national_consumer_helpline_card")
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Row with Official Badge
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
                        color = Color(0xFF10B981).copy(alpha = 0.15f),
                        shape = CircleShape,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Phone,
                                contentDescription = "Helpline",
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "Govt. of India · Dept. of Consumer Affairs",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Surface(
                    color = Color(0xFF10B981).copy(alpha = 0.15f),
                    shape = RoundedCornerShape(6.dp),
                    border = BorderStroke(0.75.dp, Color(0xFF10B981).copy(alpha = 0.5f)),
                    modifier = Modifier.wrapContentWidth()
                ) {
                    Text(
                        text = "24x7 TOLL-FREE",
                        color = Color(0xFF10B981),
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1,
                        softWrap = false,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.5.dp)
                    )
                }
            }

            // Descriptive Explanation
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 18.sp
            )

            // Contact Channels Info List (Interactive Direct Chips)
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // 1. Toll Free Call Pill
                HelplineChannelRow(
                    icon = Icons.Default.Call,
                    iconTint = Color(0xFF10B981),
                    bgColor = Color(0xFF10B981).copy(alpha = 0.08f),
                    borderColor = Color(0xFF10B981).copy(alpha = 0.25f),
                    label = "Dial Toll-Free",
                    number = "1915",
                    actionLabel = "CALL",
                    actionColor = Color(0xFF10B981),
                    onClick = { launchCallIntent(context, "1915") },
                    testTag = "helpline_call_chip"
                )

                // 2. SMS Pill
                HelplineChannelRow(
                    icon = Icons.Default.Sms,
                    iconTint = Color(0xFF0D9488),
                    bgColor = Color(0xFF0D9488).copy(alpha = 0.08f),
                    borderColor = Color(0xFF0D9488).copy(alpha = 0.25f),
                    label = "SMS Helpline",
                    number = "8800001915",
                    actionLabel = "SMS",
                    actionColor = Color(0xFF0D9488),
                    onClick = { launchSmsIntent(context, "8800001915") },
                    testTag = "helpline_sms_chip"
                )

                // 3. Official WhatsApp Pill
                HelplineChannelRow(
                    icon = Icons.Default.Chat,
                    iconTint = Color(0xFF22C55E),
                    bgColor = Color(0xFF22C55E).copy(alpha = 0.08f),
                    borderColor = Color(0xFF22C55E).copy(alpha = 0.25f),
                    label = "Official WhatsApp",
                    number = "+91 88000 01915",
                    actionLabel = "CHAT",
                    actionColor = Color(0xFF22C55E),
                    onClick = { launchWhatsAppIntent(context, "918800001915") },
                    testTag = "helpline_whatsapp_chip"
                )
            }

            // Quick 3-Action Bottom Button Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Call 1915 Button
                Button(
                    onClick = { launchCallIntent(context, "1915") },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF10B981),
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("helpline_call_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = "Call",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "1915",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = AppFontFamily,
                        maxLines = 1,
                        softWrap = false
                    )
                }

                // SMS 8800001915 Button
                Button(
                    onClick = { launchSmsIntent(context, "8800001915") },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF0D9488),
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("helpline_sms_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Sms,
                        contentDescription = "SMS",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "SMS",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        softWrap = false
                    )
                }

                // WhatsApp Button
                Button(
                    onClick = { launchWhatsAppIntent(context, "918800001915") },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF22C55E),
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp),
                    modifier = Modifier
                        .weight(1.3f)
                        .height(44.dp)
                        .testTag("helpline_whatsapp_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Chat,
                        contentDescription = "WhatsApp",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "WhatsApp",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }
        }
    }
}

@Composable
private fun HelplineChannelRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    bgColor: Color,
    borderColor: Color,
    label: String,
    number: String,
    actionLabel: String,
    actionColor: Color,
    onClick: () -> Unit,
    testTag: String
) {
    Surface(
        color = bgColor,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, borderColor),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                    Text(
                        text = number,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        fontFamily = AppFontFamily,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Surface(
                color = actionColor,
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.wrapContentWidth()
            ) {
                Text(
                    text = actionLabel,
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 1,
                    softWrap = false,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }
    }
}

/**
 * Launches Phone Dialer with target telephone number (e.g. 1915)
 */
fun launchCallIntent(context: Context, phoneNumber: String) {
    try {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$phoneNumber")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
        Toast.makeText(context, "Redirecting to Call Manager: $phoneNumber", Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        Toast.makeText(context, "Could not open Phone Dialer for $phoneNumber", Toast.LENGTH_SHORT).show()
    }
}

/**
 * Launches Default SMS Messenger with target recipient and prefilled message
 */
fun launchSmsIntent(context: Context, recipientNumber: String, messageText: String? = null) {
    try {
        val defaultMessage = messageText ?: "Reporting Legal Metrology Packaged Commodities violation / overcharging complaint."
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("smsto:$recipientNumber")
            putExtra("sms_body", defaultMessage)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
        Toast.makeText(context, "Opening SMS Messenger to $recipientNumber", Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        Toast.makeText(context, "Could not open SMS application", Toast.LENGTH_SHORT).show()
    }
}

/**
 * Launches Official WhatsApp chat with target phone number
 */
fun launchWhatsAppIntent(context: Context, whatsappNumber: String, messageText: String? = null) {
    try {
        val message = messageText ?: "Hello National Consumer Helpline, I would like to report a Legal Metrology / Packaged Commodities violation."
        val encodedMessage = Uri.encode(message)
        val uri = Uri.parse("https://api.whatsapp.com/send?phone=$whatsappNumber&text=$encodedMessage")
        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
        Toast.makeText(context, "Redirecting to National Consumer Helpline WhatsApp", Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        // Fallback to browser wa.me link
        try {
            val browserUri = Uri.parse("https://wa.me/$whatsappNumber")
            val browserIntent = Intent(Intent.ACTION_VIEW, browserUri).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(browserIntent)
        } catch (ex: Exception) {
            Toast.makeText(context, "Could not launch WhatsApp or browser", Toast.LENGTH_SHORT).show()
        }
    }
}
