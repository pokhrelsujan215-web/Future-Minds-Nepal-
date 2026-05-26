package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.*
import com.example.ui.ChatMessage
import com.example.ui.NgoViewModel
import com.example.ui.components.*
import com.example.ui.theme.*
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import java.util.Locale

// ==========================================
// 1. HOME SCREEN
// ==========================================

@Composable
fun HomeScreen(
    viewModel: NgoViewModel,
    onNavigateTo: (String) -> Unit
) {
    val lang by viewModel.appLanguage.collectAsStateWithLifecycle()
    val role by viewModel.currentUserRole.collectAsStateWithLifecycle()
    val name by viewModel.currentUserName.collectAsStateWithLifecycle()
    val projects by viewModel.projects.collectAsStateWithLifecycle()
    val donations by viewModel.donations.collectAsStateWithLifecycle()
    val volunteers by viewModel.volunteers.collectAsStateWithLifecycle()

    val totalVolunteerHours = volunteers.filter { it.isApproved }.sumOf { it.hoursTracked }
    val totalDonatedAmount = donations.sumOf { it.amount }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp)
    ) {
        // Welcome and profile line
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "${LanguageBundle.get("welcome", lang)} $name",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Role: $role",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
                
                // Language Switcher Indicator Ripple
                IconButton(
                    onClick = { viewModel.toggleLanguage() },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Text(
                        text = if (lang == "English") "ने" else "EN",
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 14.sp
                    )
                }
            }
        }

        // Hero Banner Display
        item {
            MainHeroBanner(
                title = LanguageBundle.get("welcome_title", lang),
                subtitle = LanguageBundle.get("welcome_sub", lang)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { onNavigateTo("Donate") },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NavyBlue,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(Icons.Default.VolunteerActivism, contentDescription = "Donate")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = LanguageBundle.get("quick_donate", lang), fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = { onNavigateTo("Volunteers") },
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.onSurface
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.border(
                            1.dp,
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f),
                            RoundedCornerShape(16.dp)
                        )
                    ) {
                        Text(text = LanguageBundle.get("volunteer_join", lang), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Emergency Flood Alerter
        item {
            EmergencyBulletinBoard(
                headline = LanguageBundle.get("emergency_headline", lang),
                bulletin = LanguageBundle.get("emergency_bulletin", lang)
            )
        }

        // Metrics Rollout
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Public,
                    label = "Projects",
                    value = "${projects.size}",
                    color = NavyBlue
                )
                MetricCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.VolunteerActivism,
                    label = "Raised",
                    value = "Rs.${"%,.0f".format(totalDonatedAmount)}",
                    color = SkyBlue
                )
                MetricCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.AccessTime,
                    label = "Srv. Hours",
                    value = "$totalVolunteerHours",
                    color = CrimsonRed
                )
            }
        }

        // Vision & Mission
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = LanguageBundle.get("mission_title", lang),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = NavyBlue
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = LanguageBundle.get("mission_desc", lang),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // Success Stories slider
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Success Impact Stories \uD83D\uDCD4",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            val stories = projects.filter { it.impactStory.isNotBlank() }
            if (stories.isEmpty()) {
                Text("Stories being verified...", style = MaterialTheme.typography.bodySmall)
            } else {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(stories) { project ->
                        Card(
                            modifier = Modifier
                                .width(280.dp)
                                .height(160.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxSize(),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = "Impact", tint = SkyBlue, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(project.title, fontWeight = FontWeight.Bold, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                }
                                Text(
                                    text = project.impactStory,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                    lineHeight = 15.sp,
                                    maxLines = 4,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "Location: ${project.location}",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = CrimsonRed
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MetricCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = label, tint = color, modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

// ==========================================
// 2. DONATION SCREEN
// ==========================================

@Composable
fun DonateScreen(viewModel: NgoViewModel) {
    val lang by viewModel.appLanguage.collectAsStateWithLifecycle()
    val donations by viewModel.donations.collectAsStateWithLifecycle()
    val totalDonation = donations.sumOf { it.amount }
    val fundGoal = 2000000.0 // target
    val progress = (totalDonation / fundGoal).toFloat().coerceIn(0f, 1f)

    var showDonateDialog by remember { mutableStateOf(false) }
    var selectedQrMethod by remember { mutableStateOf("eSewa") } // eSewa, Khalti, Fonepay, Bank

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp)
    ) {
        item {
            MainHeroBanner(
                title = LanguageBundle.get("donate", lang),
                subtitle = "Empower marginalized Nepalese children and youth campaigns through safe, 100% transparent donations."
            )
        }

        // Live Donation Goal Tracker Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Campaign Funding Tracker 🏔️",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "${(progress * 100).toInt()}% Met",
                            color = SkyBlue,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp)),
                        color = NavyBlue,
                        trackColor = NavyBlue.copy(alpha = 0.15f)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Currently Collected", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                            Text(text = "Rs.${"%,.0f".format(totalDonation)}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = NavyBlue)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "Strategic Goal", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                            Text(text = "Rs.2,000,000", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }
            }
        }

        // eSewa, Khalti, Fonepay Quick QR Selection Panels
        item {
            Text(
                text = "Nepal Standard Instant Payment Gateways",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 10.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QrOptionTab(name = "eSewa", selected = selectedQrMethod == "eSewa", color = Color(0xFF60BB46), onClick = { selectedQrMethod = "eSewa" })
                QrOptionTab(name = "Khalti", selected = selectedQrMethod == "Khalti", color = Color(0xFF5C2D91), onClick = { selectedQrMethod = "Khalti" })
                QrOptionTab(name = "Fonepay", selected = selectedQrMethod == "Fonepay", color = Color(0xFFE53935), onClick = { selectedQrMethod = "Fonepay" })
                QrOptionTab(name = "Bank", selected = selectedQrMethod == "Bank", color = NavyBlue, onClick = { selectedQrMethod = "Bank" })
            }
        }

        // Custom QR / Account Info display card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (selectedQrMethod != "Bank") {
                        Text(
                            text = "Scan Future Minds $selectedQrMethod QR Log",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        // Custom drawn adaptive simulated QR Code using Canvas to avoid static images!
                        Canvas(
                            modifier = Modifier
                                .size(140.dp)
                                .background(Color.White, RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        ) {
                            // Draw QR Finder Patterns in matching gateway colors
                            val themeColor = when (selectedQrMethod) {
                                "eSewa" -> Color(0xFF60BB46)
                                "Khalti" -> Color(0xFF5C2D91)
                                else -> Color(0xFFE53935)
                            }
                            // Top Left anchor
                            drawRect(color = themeColor, topLeft = Offset(0f, 0f), size = Size(35f, 35f))
                            drawRect(color = Color.White, topLeft = Offset(10f, 10f), size = Size(15f, 15f))
                            // Top Right anchor
                            drawRect(color = themeColor, topLeft = Offset(this.size.width - 35f, 0f), size = Size(35f, 35f))
                            drawRect(color = Color.White, topLeft = Offset(this.size.width - 25f, 10f), size = Size(15f, 15f))
                            // Bottom Left anchor
                            drawRect(color = themeColor, topLeft = Offset(0f, this.size.height - 35f), size = Size(35f, 35f))
                            drawRect(color = Color.White, topLeft = Offset(10f, this.size.height - 25f), size = Size(15f, 15f))

                            // Simulated hash blocks
                            drawRect(color = Color.Black, topLeft = Offset(50f, 50f), size = Size(20f, 15f))
                            drawRect(color = Color.Black, topLeft = Offset(80f, 30f), size = Size(10f, 25f))
                            drawRect(color = Color.Black, topLeft = Offset(45f, 85f), size = Size(30f, 10f))
                            drawRect(color = Color.Black, topLeft = Offset(90f, 90f), size = Size(25f, 25f))
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Fast Merchant Account ID: fmn-nepal@esewa",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        // Direct Bank Credentials
                        Text(
                            text = "Direct Bank Transfer Credentials",
                            fontWeight = FontWeight.Bold,
                            color = NavyBlue,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        BankDetailRow("Beneficiary Name", "Future Minds Nepal NGO")
                        BankDetailRow("Bank Name", "Rashtriya Banijya Bank Ltd.")
                        BankDetailRow("Account Number", "1020304050607008")
                        BankDetailRow("Branch Route", "Singhadurbar, Kathmandu")
                        BankDetailRow("SWIFT/IBAN", "RBBNNPKAXXX")
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { showDonateDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = NavyBlue),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.CreditCard, contentDescription = "Log Donation")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Log Safe Payment Success Detail")
                    }
                }
            }
        }

        // Ledger list
        item {
            Text(
                text = LanguageBundle.get("ledger", lang),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }
        
        items(donations) { item ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(item.donorName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = item.category,
                                fontSize = 10.sp,
                                color = CrimsonRed,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "• via ${item.paymentMethod}",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        }
                        if (item.memo.isNotBlank()) {
                            Text(
                                text = "\"${item.memo}\"",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                    Text(
                        text = "+Rs.${"%,.0f".format(item.amount)}",
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF4CAF50),
                        fontSize = 15.sp
                    )
                }
            }
        }
    }

    if (showDonateDialog) {
        DonateFormDialog(
            method = selectedQrMethod,
            onDismiss = { showDonateDialog = false },
            onSave = { name, amt, cat, memo ->
                viewModel.makeDonation(name, amt, cat, selectedQrMethod, memo) {}
                showDonateDialog = false
            }
        )
    }
}

@Composable
fun QrOptionTab(name: String, selected: Boolean, color: Color, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .border(
                width = 1.2.dp,
                color = if (selected) color else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                shape = RoundedCornerShape(10.dp)
            )
            .clip(RoundedCornerShape(10.dp))
            .background(if (selected) color.copy(alpha = 0.08f) else Color.Transparent)
            .clickable { onClick() }
            .padding(vertical = 8.dp, horizontal = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = name, fontWeight = FontWeight.Bold, color = if (selected) color else MaterialTheme.colorScheme.onSurface, fontSize = 11.sp)
    }
}

@Composable
fun BankDetailRow(label: String, valStr: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
        Text(text = valStr, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun DonateFormDialog(
    method: String,
    onDismiss: () -> Unit,
    onSave: (String, Double, String, String) -> Unit
) {
    var donorName by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var memo by remember { mutableStateOf("") }
    val categories = listOf("Education Support", "Health Support", "Disaster Relief", "Youth Programs", "Women Empowerment")
    var selectedCategory by remember { mutableStateOf(categories[0]) }

    Dialog(onDismissRequest = { onDismiss() }) {
        Card(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Verify Offline Donation Ledger Form",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = NavyBlue
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = donorName,
                    onValueChange = { donorName = it },
                    label = { Text("Your Dynamic Name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Donation Amount (Rs)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Selector for category
                Text("Select Support Category", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    items(categories) { category ->
                        Surface(
                            color = if (selectedCategory == category) SkyBlue else MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.clickable { selectedCategory = category }
                        ) {
                            Text(
                                text = category,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selectedCategory == category) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = memo,
                    onValueChange = { memo = it },
                    label = { Text("Blessing Message / Memo Note (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = { onDismiss() }) { Text("Dismiss") }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        onClick = {
                            val amtVal = amount.toDoubleOrNull() ?: 1000.0
                            onSave(donorName, amtVal, selectedCategory, memo)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed)
                    ) {
                        Text("Add to Ledger")
                    }
                }
            }
        }
    }
}

// ==========================================
// 3. TRANSPARENCY SCREEN
// ==========================================

@Composable
fun TransparencyScreen(viewModel: NgoViewModel) {
    val financials by viewModel.financials.collectAsStateWithLifecycle()
    
    val totalIncome = financials.filter { it.type == "Income" }.sumOf { it.amount }
    val totalExpense = financials.filter { it.type == "Expense" }.sumOf { it.amount }
    val reserveBalance = totalIncome - totalExpense

    // Budget allocation sums dynamically computed for pie chart
    val educationSum = financials.filter { it.projectCategory.contains("Education", ignoreCase = true) }.sumOf { it.amount }
    val disasterSum = financials.filter { it.projectCategory.contains("Disaster", ignoreCase = true) || it.projectCategory.contains("Floods", ignoreCase = true) }.sumOf { it.amount }
    val skillsSum = financials.filter { it.projectCategory.contains("Skills", ignoreCase = true) || it.projectCategory.contains("Women", ignoreCase = true) }.sumOf { it.amount }
    val healthSum = financials.filter { it.projectCategory.contains("Health", ignoreCase = true) || it.projectCategory.contains("Individual", ignoreCase = true) }.sumOf { it.amount }

    var selectedTab by remember { mutableStateOf("Overview") } // Overview, Ledger

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp)
    ) {
        item {
            MainHeroBanner(
                title = "Financial Transparency",
                subtitle = "Read our audit trail. Every single Nepali Rupee is accounted for and matched with concrete visual impact."
            )
        }

        // Sub tab selectors
        item {
            TabRow(
                selectedTabIndex = if (selectedTab == "Overview") 0 else 1,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Tab(selected = selectedTab == "Overview", onClick = { selectedTab = "Overview" }) {
                    Text("Overview & Charts", modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold)
                }
                Tab(selected = selectedTab == "Ledger", onClick = { selectedTab = "Ledger" }) {
                    Text("Income/Expense ledger", modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold)
                }
            }
        }

        if (selectedTab == "Overview") {
            // General Ledger Balances
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(text = "NGO Strategic Balance Sheet", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            TransparentBoxMetric("Total Inflow (Grants+Donations)", totalIncome, Color(0xFF4CAF50))
                            TransparentBoxMetric("Total Outflow (Spending)", totalExpense, CrimsonRed)
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(thickness = 0.8.dp)
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Active Reserves", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            Text(
                                "Rs.${"%,.2f".format(reserveBalance)}",
                                fontWeight = FontWeight.ExtraBold,
                                color = NavyBlue,
                                fontSize = 18.sp
                            )
                        }
                    }
                }
            }

            // Pie Chart Section
            item {
                FinancialExpensePieChart(
                    educationAmt = if (educationSum == 0.0) 250000.0 else educationSum,
                    disasterAmt = if (disasterSum == 0.0) 150000.0 else disasterSum,
                    skillsAmt = if (skillsSum == 0.0) 180000.0 else skillsSum,
                    healthAmt = if (healthSum == 0.0) 100000.0 else healthSum,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // Audit Section Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.VerifiedUser, "Verified", tint = Color(0xFF4CAF50), modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("FY 2025/2026 Audit Complete", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(
                                "Certified by Pokhrel & Co Chartered Accountants. Public audit reports and receipts conform to Nepal Government Social Welfare Council regulations.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        } else {
            // Ledger Log Details
            items(financials) { record ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (record.type == "Income") Color(0xFF4CAF50).copy(alpha = 0.12f)
                                        else CrimsonRed.copy(alpha = 0.12f)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (record.type == "Income") Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                                    contentDescription = record.type,
                                    tint = if (record.type == "Income") Color(0xFF4CAF50) else CrimsonRed,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(record.title, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Row {
                                    Text(record.projectCategory, fontSize = 10.sp, color = SkyBlue, fontWeight = FontWeight.SemiBold)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("• ${record.date}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
                                }
                            }
                        }
                        Text(
                            text = "${if (record.type == "Income") "+" else "-"}Rs.${"%,.0f".format(record.amount)}",
                            fontWeight = FontWeight.ExtraBold,
                            color = if (record.type == "Income") Color(0xFF4CAF50) else CrimsonRed,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TransparentBoxMetric(label: String, valAmt: Double, color: Color) {
    Column {
        Text(text = label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
        Text(
            "Rs.${"%,.2f".format(valAmt)}",
            color = color,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp
        )
    }
}

// ==========================================
// 4. PROJECTS SCREEN
// ==========================================

@Composable
fun ProjectsScreen(viewModel: NgoViewModel) {
    val projects by viewModel.projects.collectAsStateWithLifecycle()
    val selectedProj by viewModel.selectedProject.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp)
    ) {
        item {
            MainHeroBanner(
                title = "NGO Social Projects",
                subtitle = "Empower communities through long-term strategic projects spanning rural districts to the urban core."
            )
        }

        // Active projects lists
        items(projects) { project ->
            val spendingProgress = (project.currentSpent / project.budget).toFloat().coerceIn(0f, 1f)
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .clickable { viewModel.selectProject(project) }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Headline status row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = NavyBlue.copy(alpha = 0.08f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Active Program",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = NavyBlue,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        
                        Text(
                            text = "\uD83D\uDCCD ${project.location}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = CrimsonRed
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = project.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = project.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        lineHeight = 16.sp,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(thickness = 0.5.dp)
                    Spacer(modifier = Modifier.height(12.dp))

                    // Progress indicators
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Assigned Budget", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                            Text("Rs.${"%,.0f".format(project.budget)}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Actual Spent", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                            Text("Rs.${"%,.0f".format(project.currentSpent)} ( ${(spendingProgress * 100).toInt()}% )", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = SkyBlue)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { spendingProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = SkyBlue,
                        trackColor = SkyBlue.copy(alpha = 0.15f)
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Beneficiaries Covered: ${"%,d".format(project.beneficiaryCount)}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Read Stories", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NavyBlue)
                            Icon(Icons.Default.ChevronRight, "More", tint = NavyBlue, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }

    // Modal Details Dialog for Project Story
    if (selectedProj != null) {
        val proj = selectedProj!!
        Dialog(onDismissRequest = { viewModel.selectProject(null) }) {
            Card(
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = proj.title,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge,
                        color = NavyBlue
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Location: ${proj.location} • Beneficiaries: ${proj.beneficiaryCount}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = CrimsonRed
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Text("Campaign Narrative", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text(
                        text = proj.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        lineHeight = 16.sp
                    )

                    if (proj.impactStory.isNotBlank()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Card(
                            colors = CardDefaults.cardColors(containerColor = SkyBlue.copy(alpha = 0.08f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("\uD83D\uDCD4 Local Impact Diary:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = SkyBlue)
                                Text(
                                    text = proj.impactStory,
                                    fontSize = 11.sp,
                                    lineHeight = 16.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Assigned Officers:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text(proj.teamMembers, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))

                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = { viewModel.selectProject(null) },
                        modifier = Modifier.align(Alignment.End),
                        colors = ButtonDefaults.buttonColors(containerColor = NavyBlue)
                    ) {
                        Text("Dismiss")
                    }
                }
            }
        }
    }
}

// ==========================================
// 5. JOBS & VACANCIES SCREEN
// ==========================================

@Composable
fun VacanciesScreen(viewModel: NgoViewModel) {
    val vacancies by viewModel.vacancies.collectAsStateWithLifecycle()
    var selectedCategory by remember { mutableStateOf("All") } // All, Job, Volunteer Opening, Internship
    val context = LocalContext.current

    val filtered = if (selectedCategory == "All") vacancies
    else vacancies.filter { it.type == selectedCategory }

    var appliedId by remember { mutableStateOf<Int?>(null) } // Tracking click

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp)
    ) {
        item {
            MainHeroBanner(
                title = "Opportunities & Vacancies",
                subtitle = "Empower Nepal yourself by working directly in the social development sector. Apply for jobs, fellowships, or internships!"
            )
        }

        // Job/Vacancy filters row
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val filters = listOf("All", "Vacancy", "Volunteer Opening", "Internship")
                filters.forEach { filter ->
                    Surface(
                        color = if (selectedCategory == filter) NavyBlue else MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.clickable { selectedCategory = filter }
                    ) {
                        Text(
                            text = filter,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedCategory == filter) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        if (filtered.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No openings currently posted. Check back soon, sathi!", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                }
            }
        } else {
            items(filtered) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = SkyBlue.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = item.type,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SkyBlue,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            Text(
                                text = "Deadline: ${item.deadline}",
                                color = CrimsonRed,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(item.title, fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = item.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Active Applicants: ${item.appliedCount}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                            Button(
                                onClick = {
                                    viewModel.applyVacancy(item.id)
                                    appliedId = item.id
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = NavyBlue)
                            ) {
                                Text("Apply Directly")
                            }
                        }
                    }
                }
            }
        }
    }

    if (appliedId != null) {
        Dialog(onDismissRequest = { appliedId = null }) {
            Card(shape = RoundedCornerShape(20.dp)) {
                Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.CheckCircle, "Applied", tint = Color(0xFF4CAF50), modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Dynamic CV Application Sent!", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "We have processed your profile CV parameters. Our recruitment team will examine credentials and coordinate soon. Keep following notifications! Dhanyabad.",
                        textAlign = TextAlign.Center,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { appliedId = null }, colors = ButtonDefaults.buttonColors(containerColor = NavyBlue)) {
                        Text("Dismiss")
                    }
                }
            }
        }
    }
}

// ==========================================
// 6. VOLUNTEER MANAGEMENT SCREEN & CUSTOM ID CARD
// ==========================================

@Composable
fun VolunteerScreen(viewModel: NgoViewModel) {
    val lang by viewModel.appLanguage.collectAsStateWithLifecycle()
    val volunteers by viewModel.volunteers.collectAsStateWithLifecycle()
    val userEmail by viewModel.currentUserEmail.collectAsStateWithLifecycle()

    // Find if the user has registered email in our database
    val myProfile = volunteers.find { it.email.lowercase() == userEmail.lowercase() }

    // Forms
    var fullName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var skills by remember { mutableStateOf("") }
    var availability by remember { mutableStateOf("Weekends (Sat - Sun)") }
    
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            MainHeroBanner(
                title = LanguageBundle.get("volunteers", lang),
                subtitle = "Earn certificate units, track social action indexes, and retrieve custom Digital ID Badges."
            )
        }

        if (myProfile != null) {
            // Volunteer is Registered! Display custom floating ID card
            item {
                Text(
                    text = "Your Future Minds Volunteer Badge \uD83E\uDEAA",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(bottom = 12.dp).fillMaxWidth()
                )

                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(NavyBlue.copy(alpha = 0.05f), AppLightBg)
                                )
                            )
                            .padding(24.dp)
                            .fillMaxWidth()
                    ) {
                        // Badge Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "FUTURE MINDS NEPAL",
                                    fontWeight = FontWeight.ExtraBold,
                                    color = NavyBlue,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "YOUTH MOBILIZATION DEPT",
                                    fontSize = 9.sp,
                                    color = CrimsonRed,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            // Small Flag element placeholder
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(CrimsonRed),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("FM", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Avatar & credentials row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Avatar Placeholder box
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(SkyBlue.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Person, "Avatar", tint = SkyBlue, modifier = Modifier.size(40.dp))
                            }
                            Spacer(modifier = Modifier.width(16.dp))

                            Column {
                                Text(
                                    text = myProfile.fullName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = myProfile.email,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                )
                                
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Status: ", fontSize = 11.sp)
                                    val statusText = if (myProfile.isApproved) "APPROVED MEMBER" else "PENDING COMPLIANCE"
                                    Text(
                                        text = statusText,
                                        fontWeight = FontWeight.Bold,
                                        color = if (myProfile.isApproved) Color(0xFF4CAF50) else Color(0xFFFF9800),
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(thickness = 0.5.dp)
                        Spacer(modifier = Modifier.height(16.dp))

                        // Detailed stats block
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Assigned Skills", fontSize = 10.sp, color = Color.Gray)
                                Text(myProfile.skills, fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Impact Hours", fontSize = 10.sp, color = Color.Gray)
                                Text("${myProfile.hoursTracked} Hrs", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SkyBlue)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Drawn Barcode for ID design
                        Canvas(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(30.dp)
                                .background(Color.White)
                        ) {
                            // Draw randomized barcode lines
                            var startX = 20f
                            while (startX < this.size.width - 20f) {
                                val barWidth = if (startX % 3 == 0f) 6f else 2f
                                drawRect(
                                    color = Color.Black,
                                    topLeft = Offset(startX, 0f),
                                    size = Size(barWidth, this.size.height)
                                )
                                startX += barWidth + (2..5).random().toFloat()
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Reference code ID: ${myProfile.refCode}",
                            fontSize = 9.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                            fontFamily = FontFamily.Monospace,
                            color = Color.Gray
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = {
                            Toast.makeText(context, "Certificate Generated! Simulated PDF download initiated.", Toast.LENGTH_LONG).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NavyBlue),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Download, "Download")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Service Certificate")
                    }
                    
                    OutlinedButton(
                        onClick = {
                            // Logout volunteer simulation by switching role or clearing
                            viewModel.updateProfile("Bikram Nepal", "differentemail@example.com")
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Sign Out Badge")
                    }
                }
            }
        } else {
            // Display Register Form
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "Register for Youth Social Volunteering",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium,
                            color = NavyBlue
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = fullName,
                            onValueChange = { fullName = it },
                            label = { Text("Enter Your Full Name") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text("Phone Number (98xxxxxxxx)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = skills,
                            onValueChange = { skills = it },
                            label = { Text("E.g., Teaching, Tailoring, Coding, First Aid") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Text("Select Availability Profile", fontSize = 11.sp, color = Color.Gray)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            val slots = listOf("Weekends (Sat - Sun)", "Weekdays Only", "Flexible Hours")
                            slots.forEach { slot ->
                                Surface(
                                    color = if (availability == slot) SkyBlue else MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.clickable { availability = slot }
                                ) {
                                    Text(
                                        text = slot,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (availability == slot) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = {
                                if (fullName.isNotBlank() && phone.isNotBlank()) {
                                    // Update visual profile to match register so myProfile shows up directly!
                                    viewModel.updateProfile(fullName, userEmail)
                                    viewModel.registerVolunteer(fullName, userEmail, phone, skills, availability)
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Submit Volunteer Signup Profile")
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 7. SCHOLARSHIP SCREEN
// ==========================================

@Composable
fun ScholarshipScreen(viewModel: NgoViewModel) {
    val scholarships by viewModel.scholarships.collectAsStateWithLifecycle()
    val userEmail by viewModel.currentUserEmail.collectAsStateWithLifecycle()

    var applicantName by remember { mutableStateOf("") }
    var school by remember { mutableStateOf("") }
    var program by remember { mutableStateOf("") }
    var bracket by remember { mutableStateOf("< Rs.200,000") } // Household income
    var description by remember { mutableStateOf("") }

    val context = LocalContext.current

    val myApplications = scholarships.filter { it.email.lowercase() == userEmail.lowercase() }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp)
    ) {
        item {
            MainHeroBanner(
                title = "Scholarship Assistance",
                subtitle = "Apply for educational assistance, emergency school supplies, or microdevelopment academic grants."
            )
        }

        // Active Trackers List
        if (myApplications.isNotEmpty()) {
            item {
                Text(
                    text = "Track Class Applications Status",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }
            items(myApplications) { app ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(app.targetProgram, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            
                            val stateColor = when (app.status) {
                                "Approved" -> Color(0xFF4CAF50)
                                "Rejected" -> CrimsonRed
                                else -> Color(0xFFFF9800)
                            }
                            Surface(
                                color = stateColor.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = app.status,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = stateColor,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Current School: ${app.currentSchool}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                        Text("Income Bracket: ${app.incomeBracket}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                        Text("Date Lodged: ${app.dateApplied}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                    }
                }
            }
        }

        // Application Card Form
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "New Student Support Request",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        color = NavyBlue
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = applicantName,
                        onValueChange = { applicantName = it },
                        label = { Text("Applicant Full Name") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = school,
                        onValueChange = { school = it },
                        label = { Text("School / College Name") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = program,
                        onValueChange = { program = it },
                        label = { Text("Target Course / Scholarship Tier") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Family Annual Income Class (Rs)", fontSize = 11.sp, color = Color.Gray)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        val incomeSlots = listOf("< Rs.200k", "Rs.200k - 400k", "Rs.400k - 600k")
                        incomeSlots.forEach { slot ->
                            Surface(
                                color = if (bracket == slot) SkyBlue else MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.clickable { bracket = slot }
                            ) {
                                Text(
                                    text = slot,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (bracket == slot) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("State narrative of academic needs") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Upload files simulation
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.2.dp, SkyBlue.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                            .background(SkyBlue.copy(alpha = 0.05f))
                            .clickable {
                                Toast.makeText(context, "Citizen/Income credentials upload simulated successfully!", Toast.LENGTH_SHORT).show()
                            }
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.CloudUpload, "Upload", tint = SkyBlue)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Attach Supporting Files (Citizenship / Income docs)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SkyBlue)
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            if (applicantName.isNotBlank() && program.isNotBlank()) {
                                viewModel.applyScholarship(
                                    applicantName = applicantName,
                                    email = userEmail,
                                    targetProgram = program,
                                    incomeBracket = bracket,
                                    currentSchool = school,
                                    whyNeeded = description,
                                    documentUploaded = "Citizenship_Verified.pdf"
                                )
                                applicantName = ""
                                school = ""
                                program = ""
                                description = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Submit Support Request")
                    }
                }
            }
        }
    }
}

// ==========================================
// 8. COMMUNITY SECTION
// ==========================================

@Composable
fun CommunityScreen(viewModel: NgoViewModel) {
    val forumPosts by viewModel.forumPosts.collectAsStateWithLifecycle()
    var postTitle by remember { mutableStateOf("") }
    var postContent by remember { mutableStateOf("") }
    val categories = listOf("General", "Discussion", "Youth Ideas", "Feedback")
    var selectedCategory by remember { mutableStateOf(categories[0]) }

    var selectedFilter by remember { mutableStateOf("All") }

    val filteredList = if (selectedFilter == "All") forumPosts
    else forumPosts.filter { it.category == selectedFilter }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp)
    ) {
        item {
            MainHeroBanner(
                title = "Youth Idea Platform",
                subtitle = "Engage in progressive discussion forums, exchange ideas, and cast survey feedback."
            )
        }

        // Post Creator Card Form
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Express Your Voice \uD83D\uDCE3", fontWeight = FontWeight.Bold, color = NavyBlue)
                    Spacer(modifier = Modifier.height(10.dp))
                    
                    OutlinedTextField(
                        value = postTitle,
                        onValueChange = { postTitle = it },
                        label = { Text("Catchy Title") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = postContent,
                        onValueChange = { postContent = it },
                        label = { Text("What idea or comment do you have for Nepal?") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    // Categories selector for post
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        categories.forEach { category ->
                            Surface(
                                color = if (selectedCategory == category) SkyBlue else MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.clickable { selectedCategory = category }
                            ) {
                                Text(
                                    text = category,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (selectedCategory == category) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            if (postTitle.isNotBlank() && postContent.isNotBlank()) {
                                viewModel.postIdea(postTitle, postContent, selectedCategory)
                                postTitle = ""
                                postContent = ""
                            }
                        },
                        modifier = Modifier.align(Alignment.End),
                        colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed)
                    ) {
                        Text("Post Dynamic Voice")
                    }
                }
            }
        }

        // Filters row
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf("All", "Discussion", "Youth Ideas", "Feedback").forEach { filter ->
                    Surface(
                        color = if (selectedFilter == filter) NavyBlue else MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.clickable { selectedFilter = filter }
                    ) {
                        Text(
                            text = filter,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedFilter == filter) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // Active posts feed
        items(filteredList) { post ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "By ${post.authorName}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Gray
                        )
                        Surface(
                            color = NavyBlue.copy(alpha = 0.08f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = post.category,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = NavyBlue,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(post.title, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = post.content,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        lineHeight = 16.sp
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(thickness = 0.5.dp)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(post.timestamp, fontSize = 10.sp, color = Color.Gray)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { viewModel.likePost(post.id) }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(Icons.Default.Favorite, "Like", tint = CrimsonRed, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "${post.likes} Hearts", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 9. ADMIN PANEL SCREEN
// ==========================================

@Composable
fun AdminPanelScreen(viewModel: NgoViewModel) {
    var adminMode by remember { mutableStateOf("Volunteers") } // Volunteers, Scholarships, Ledger, Projects
    val volunteers by viewModel.volunteers.collectAsStateWithLifecycle()
    val scholarships by viewModel.scholarships.collectAsStateWithLifecycle()
    val projects by viewModel.projects.collectAsStateWithLifecycle()

    // Form states
    var projTitle by remember { mutableStateOf("") }
    var projDesc by remember { mutableStateOf("") }
    var projBudget by remember { mutableStateOf("") }
    var projLocation by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp)
    ) {
        item {
            MainHeroBanner(
                title = "NGO Administrative Console",
                subtitle = "Approve volunteer hours, manage grant distribution states, view analytical budgets."
            )
        }

        // Sub control tabs row
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val screens = listOf("Volunteers", "Scholarships", "Projects")
                screens.forEach { screen ->
                    Surface(
                        color = if (adminMode == screen) CrimsonRed else MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.clickable { adminMode = screen }
                    ) {
                        Text(
                            text = screen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (adminMode == screen) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        if (adminMode == "Volunteers") {
            // Manage volunteer credentials and hours
            if (volunteers.isEmpty()) {
                item { Text("No current volunteer signups.") }
            } else {
                items(volunteers) { vol ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(vol.fullName, fontWeight = FontWeight.Bold)
                            Text("Email: ${vol.email} • Skills: ${vol.skills}", fontSize = 11.sp)
                            
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Impact hours: ", fontSize = 11.sp)
                                    // Add hour controller button
                                    Button(
                                        onClick = {
                                            viewModel.adminApproveVolunteer(vol.id, vol.isApproved, vol.hoursTracked + 5)
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = SkyBlue)
                                    ) {
                                        Text("+5 Hrs", fontSize = 10.sp)
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("${vol.hoursTracked} Hrs", fontWeight = FontWeight.Bold)
                                }
                                
                                Button(
                                    onClick = { viewModel.adminApproveVolunteer(vol.id, vol.isApproved, vol.hoursTracked) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (vol.isApproved) Color(0xFF4CAF50) else Color(0xFFFF9800)
                                    )
                                ) {
                                    Text(if (vol.isApproved) "Approved ✔" else "Approve ✖", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        } else if (adminMode == "Scholarships") {
            // Manage grant approvals
            if (scholarships.isEmpty()) {
                item { Text("No scholarships lodged yet.") }
            } else {
                items(scholarships) { sch ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(sch.applicantName, fontWeight = FontWeight.Bold)
                            Text("Program: ${sch.targetProgram} • School: ${sch.currentSchool}", fontSize = 11.sp)
                            Text("Narrative: ${sch.whyNeeded}", fontSize = 11.sp, color = Color.Gray)
                            
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                OutlinedButton(
                                    onClick = { viewModel.adminUpdateScholarshipStatus(sch.id, "Rejected") },
                                    modifier = Modifier.padding(end = 8.dp)
                                ) {
                                    Text("Deny", color = CrimsonRed)
                                }
                                Button(
                                    onClick = { viewModel.adminUpdateScholarshipStatus(sch.id, "Approved") },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                                ) {
                                    Text("Approve")
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Project adding board
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Add New NGO Project", fontWeight = FontWeight.ExtraBold, color = NavyBlue)
                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = projTitle,
                            onValueChange = { projTitle = it },
                            label = { Text("Project Title") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = projDesc,
                            onValueChange = { projDesc = it },
                            label = { Text("叙述 Description") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = projBudget,
                            onValueChange = { projBudget = it },
                            label = { Text("Budget Allocation (Rs)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = projLocation,
                            onValueChange = { projLocation = it },
                            label = { Text("Location District") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                if (projTitle.isNotBlank() && projBudget.isNotBlank()) {
                                    viewModel.adminAddProject(
                                        title = projTitle,
                                        description = projDesc,
                                        budget = projBudget.toDoubleOrNull() ?: 100000.0,
                                        location = projLocation,
                                        beneficiaries = (200..1000).random(),
                                        teamLeads = "Admin board"
                                    )
                                    projTitle = ""
                                    projDesc = ""
                                    projBudget = ""
                                    projLocation = ""
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed)
                        ) {
                            Text("Post Project Globally")
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 10. CHATBOT SCREEN
// ==========================================

@Composable
fun ChatbotScreen(viewModel: NgoViewModel) {
    val messages by viewModel.chatMessages.collectAsStateWithLifecycle()
    val isGenerating by viewModel.isChatLoading.collectAsStateWithLifecycle()
    
    var textInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Chat screen header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(NavyBlue, NavyBlue.copy(alpha = 0.85f))
                    )
                )
                .padding(vertical = 16.dp, horizontal = 20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Sagar avatar indicator pulsing ripple effect
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SmartButton,
                        contentDescription = "Sagar Bot",
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Sagar • Future Minds AI",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 15.sp
                    )
                    Text(
                        text = if (isGenerating) "Generating response..." else "Online support adviser",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }
        }

        // Active chat thread logs
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(messages) { msg ->
                val bubbleAlign = if (msg.isUser) Alignment.End else Alignment.Start
                val containerColor = if (msg.isUser) SkyBlue else MaterialTheme.colorScheme.surfaceVariant
                val textColor = if (msg.isUser) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = bubbleAlign
                ) {
                    Card(
                        shape = RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (msg.isUser) 16.dp else 0.dp,
                            bottomEnd = if (msg.isUser) 0.dp else 16.dp
                        ),
                        colors = CardDefaults.cardColors(containerColor = containerColor),
                        modifier = Modifier.widthIn(max = 280.dp)
                    ) {
                        Text(
                            text = msg.text,
                            color = textColor,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(12.dp),
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }

        // Inputs panels
        Surface(
            tonalElevation = 6.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .navigationBarsPadding()
                    .imePadding()
                    .padding(12.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = textInput,
                    onValueChange = { textInput = it },
                    placeholder = { Text("Ask Sagar about projects, audits, or eSewa...", fontSize = 13.sp) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    maxLines = 3,
                    trailingIcon = {
                        if (isGenerating) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp,
                                color = SkyBlue
                            )
                        }
                    }
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (textInput.isNotBlank()) {
                            viewModel.sendChatMessage(textInput)
                            textInput = ""
                        }
                    },
                    colors = IconButtonDefaults.iconButtonColors(containerColor = CrimsonRed),
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                ) {
                    Icon(Icons.Default.Send, "Send", tint = Color.White)
                }
            }
        }
    }
}
