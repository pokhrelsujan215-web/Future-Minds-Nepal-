package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.ui.geometry.Offset
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.animation.core.spring
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.data.NgoDatabase
import com.example.data.NgoRepository
import com.example.ui.NgoViewModel
import com.example.ui.NgoViewModelFactory
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Core initialization of Room Database & Repository
        val database = NgoDatabase.getDatabase(applicationContext, lifecycleScope)
        val repository = NgoRepository(database.ngoDao())
        
        // Instantiate the View Model using Factory provider
        val viewModelFactory = NgoViewModelFactory(application, repository)
        val viewModel = ViewModelProvider(this, viewModelFactory)[NgoViewModel::class.java]

        setContent {
            MyApplicationTheme {
                NgoMainLayout(viewModel)
            }
        }
    }
}

// Helper model to encapsulate Hub items
data class HubServiceItem(
    val title: String,
    val desc: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val route: String,
    val color: Color
)

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun NgoMainLayout(viewModel: NgoViewModel) {
    var currentScreen by remember { mutableStateOf("Home") }
    
    // Track past navigation states to allow popping back nicely from deeper Hub screens!
    var navigationHistory by remember { mutableStateOf(listOf("Home")) }

    fun navigateTo(screen: String) {
        if (screen != currentScreen) {
            navigationHistory = navigationHistory + currentScreen
            currentScreen = screen
        }
    }

    fun navigateBack() {
        if (navigationHistory.isNotEmpty()) {
            val previous = navigationHistory.last()
            navigationHistory = navigationHistory.dropLast(1)
            currentScreen = previous
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            // Standard Navigation Bar (compact width smartphones)
            // Show Home, Projects, Donate, Transparency, and More Hub tabs
            NavigationBar(
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .drawBehind {
                        drawLine(
                            color = Color.LightGray.copy(alpha = 0.35f),
                            start = Offset(0f, 0f),
                            end = Offset(size.width, 0f),
                            strokeWidth = 1.dp.toPx()
                        )
                    },
                tonalElevation = 0.dp
            ) {
                NavigationBarItem(
                    selected = currentScreen == "Home",
                    onClick = { currentScreen = "Home" },
                    icon = { Icon(Icons.Default.Home, "Home") },
                    label = { Text("Home", fontSize = 10.sp, fontWeight = FontWeight.Bold) }
                )
                NavigationBarItem(
                    selected = currentScreen == "Projects",
                    onClick = { currentScreen = "Projects" },
                    icon = { Icon(Icons.Default.Public, "Projects") },
                    label = { Text("Projects", fontSize = 10.sp, fontWeight = FontWeight.Bold) }
                )
                NavigationBarItem(
                    selected = currentScreen == "Donate",
                    onClick = { currentScreen = "Donate" },
                    icon = { Icon(Icons.Default.VolunteerActivism, "Donate") },
                    label = { Text("Donate", fontSize = 10.sp, fontWeight = FontWeight.Bold) }
                )
                NavigationBarItem(
                    selected = currentScreen == "Transparency",
                    onClick = { currentScreen = "Transparency" },
                    icon = { Icon(Icons.Default.AccountBalance, "Financials") },
                    label = { Text("Metrics", fontSize = 10.sp, fontWeight = FontWeight.Bold) }
                )
                NavigationBarItem(
                    selected = listOf("Hub", "Vacancies", "Volunteers", "Scholarships", "Community", "Admin", "Chatbot").contains(currentScreen),
                    onClick = { navigateTo("Hub") },
                    icon = { Icon(Icons.Default.Widgets, "More") },
                    label = { Text("More Hub", fontSize = 10.sp, fontWeight = FontWeight.Bold) }
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
        ) {
            // Custom Action bar with Nepali accent borders
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .drawBehind {
                        // Drawing custom thin red and blue flag lines beneath action bar
                        drawLine(
                            color = NavyBlue,
                            start = androidx.compose.ui.geometry.Offset(0f, size.height),
                            end = androidx.compose.ui.geometry.Offset(size.width * 0.7f, size.height),
                            strokeWidth = 6f
                        )
                        drawLine(
                            color = CrimsonRed,
                            start = androidx.compose.ui.geometry.Offset(size.width * 0.7f, size.height),
                            end = androidx.compose.ui.geometry.Offset(size.width, size.height),
                            strokeWidth = 6f
                        )
                    }
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (currentScreen != "Home" && currentScreen != "Projects" && currentScreen != "Donate" && currentScreen != "Transparency" && currentScreen != "Hub") {
                            IconButton(onClick = { navigateBack() }) {
                                Icon(Icons.Default.ArrowBack, "Back", tint = NavyBlue)
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        
                        Column {
                            Text(
                                text = "FUTURE MINDS NEPAL",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = NavyBlue,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Youth Empowerment NGO Initiative",
                                style = MaterialTheme.typography.labelSmall,
                                color = CrimsonRed,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Multi-role selector dropdown mock badge
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        var expanded by remember { mutableStateOf(false) }
                        IconButton(onClick = { expanded = true }) {
                            Icon(Icons.Default.ManageAccounts, "Account Settings", tint = SkyBlue)
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Standard Member (User)") },
                                onClick = { viewModel.switchRole("Member"); expanded = false }
                            )
                            DropdownMenuItem(
                                text = { Text("NGO Donor") },
                                onClick = { viewModel.switchRole("Donor"); expanded = false }
                            )
                            DropdownMenuItem(
                                text = { Text("Active Volunteer") },
                                onClick = { viewModel.switchRole("Volunteer"); expanded = false }
                            )
                            DropdownMenuItem(
                                text = { Text("Administrator (Admin)") },
                                onClick = { viewModel.switchRole("Admin"); expanded = false }
                            )
                        }
                    }
                }
            }

            // Crossfade content transition for ultra-premium feel
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = {
                    fadeIn(animationSpec = spring()) with fadeOut(animationSpec = spring())
                },
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) { target ->
                when (target) {
                    "Home" -> HomeScreen(viewModel, onNavigateTo = { navigateTo(it) })
                    "Projects" -> ProjectsScreen(viewModel)
                    "Donate" -> DonateScreen(viewModel)
                    "Transparency" -> TransparencyScreen(viewModel)
                    "Hub" -> Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        ServicesHubScreen(onNavigate = { navigateTo(it) })
                    }
                    "Vacancies" -> VacanciesScreen(viewModel)
                    "Volunteers" -> VolunteerScreen(viewModel)
                    "Scholarships" -> ScholarshipScreen(viewModel)
                    "Community" -> CommunityScreen(viewModel)
                    "Admin" -> AdminPanelScreen(viewModel)
                    "Chatbot" -> ChatbotScreen(viewModel)
                }
            }
        }
    }
}

// ==========================================
// CENTRAL HUB / MORE SERVICES DIRECTORY
// ==========================================

@Composable
fun ServicesHubScreen(onNavigate: (String) -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Youth Activity Directory \uD83D\uDDE3",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        // Hub items
        val services = listOf(
            HubServiceItem("Opportunities & Jobs", "Apply for vacancies/internships", Icons.Default.Work, "Vacancies", SkyBlue),
            HubServiceItem("Volunteer Hub", "Track hours & Digital ID Cards", Icons.Default.Badge, "Volunteers", NavyBlue),
            HubServiceItem("Scholarships Program", "Aid applications and tracking", Icons.Default.School, "Scholarships", CrimsonRed),
            HubServiceItem("Discussion Forum", "Share ideas with Nepalese youth", Icons.Default.Forum, "Community", Color(0xFF4CAF50)),
            HubServiceItem("Consult Sagar AI", "Polite, supportive chatbot", Icons.Default.SmartButton, "Chatbot", Color(0xFFFF9800)),
            HubServiceItem("Admin Board", "Manage NGOs logs (Dev toggle)", Icons.Default.Settings, "Admin", Color(0xFF673AB7))
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(services.chunked(2)) { rowList ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    rowList.forEach { item ->
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .height(130.dp)
                                .clickable { onNavigate(item.route) },
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.04f)
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.03f)),
                            shape = RoundedCornerShape(28.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxSize(),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(MaterialTheme.colorScheme.surface)
                                        .border(
                                            width = 1.dp,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f),
                                            shape = RoundedCornerShape(14.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = item.title,
                                        tint = item.color,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = item.title,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = item.desc,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
