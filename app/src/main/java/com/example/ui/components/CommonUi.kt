package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

// ==========================================
// BILINGUAL LANGUAGE LOCALIZATION BUNDLE
// ==========================================

object LanguageBundle {
    private val englishMap = mapOf(
        "app_title" to "Future Minds Nepal",
        "tagline" to "Youth. Education. Transparency.",
        "welcome" to "Namaste,",
        "home" to "Home",
        "donate" to "Donate",
        "projects" to "Projects",
        "financials" to "Transparency",
        "vacancies" to "Vacancies",
        "volunteers" to "Volunteers",
        "scholarships" to "Scholarships",
        "community" to "Community",
        "admin" to "Admin",
        "chatbot" to "Ask Sagar AI",
        "mission_title" to "Our Vision & Mission",
        "mission_desc" to "To foster a resilient, digitally literate, and highly skilled generation of young Nepalese leaders by maintaining 100% financial transparency and running high-impact community programs.",
        "emergency_headline" to "PRE-MONSOON FLOOD ALERTS",
        "emergency_bulletin" to "Alert: Siren sensors are active across Rautahat river-beds. Stay connected for live warnings and safety guides from our Disaster Relief caravan.",
        "featured_projects" to "Featured Projects",
        "learn_more" to "Learn More",
        "top_supporters" to "Top Monthly Supporters",
        "ledger" to "Public Donation Ledger",
        "total_raised" to "Total Raised Offline & Online",
        "submit" to "Submit Application",
        "apply_success" to "Success! Your application has been submitted to the NGO board.",
        "current_status" to "Review Status",
        "id_card" to "NGO Youth Member Card",
        "hours" to "Service Hours Tracked",
        "code" to "Verification Code"
    )

    private val nepaliMap = mapOf(
        "app_title" to "फ्युचर माइन्ड्स नेपाल",
        "tagline" to "युवा सचेतना | शिक्षा | पारदर्शिता",
        "welcome" to "नमस्ते,",
        "home" to "गृहपृष्ठ",
        "donate" to "सहयोग",
        "projects" to "योजनाहरू",
        "financials" to "पारदर्शिता",
        "vacancies" to "अवसरहरू",
        "volunteers" to "स्वयंसेवक",
        "scholarships" to "छात्रवृत्ति",
        "community" to "समुदाय",
        "admin" to "प्रशासक",
        "chatbot" to "सागर एआई",
        "mission_title" to "हाम्रो दृष्टिकोण र लक्ष्य",
        "mission_desc" to "शतप्रतिशत वित्तीय पारदर्शिता र उच्च प्रभावकारी सामुदायिक कार्यक्रमहरू सञ्‍चालन गर्दै नेपालका युवाहरूलाई डिजिटल रूपमा साक्षर, प्रतिभावान र सक्षम नेतृत्वको रूपमा विकास गर्नु।",
        "emergency_headline" to "मनसुन-पूर्व बाढी सतर्कता",
        "emergency_bulletin" to "सूचना: रौतहट नदी क्षेत्रहरूमा बाढी पूर्व-सूचना साइरन प्रणाली सक्रिय छ। हाम्रो विपद् व्यवस्थापन टोलीको प्रत्यक्ष सूचनामा रहनुहोस्।",
        "featured_projects" to "विशेष परियोजनाहरू",
        "learn_more" to "विस्तृत विवरण",
        "top_supporters" to "शीर्ष सहयोगीहरू",
        "ledger" to "सार्वजनिक दान बहीखाता",
        "total_raised" to "जम्मा संकलित सहयोग",
        "submit" to "फारम बुझाउनुहोस्",
        "apply_success" to "सफल! तपाईंको फारम एनजीओ बोर्डमा दर्ता भएको छ।",
        "current_status" to "मूल्याङ्कन स्थिति",
        "id_card" to "एनजीओ स्वयंसेवक प्रमाणपत्र",
        "hours" to "योगदान स्वयंसेवा घण्टा",
        "code" to "प्रमाणीकरण कोड"
    )

    fun get(key: String, language: String): String {
        return if (language == "English") {
            englishMap[key] ?: key
        } else {
            nepaliMap[key] ?: key
        }
    }
}

// ==========================================
// REUSABLE ELEVATED Gradient UI CARD HEROES
// ==========================================

@Composable
fun MainHeroBanner(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    trailingContent: @Composable (() -> Unit)? = null
) {
    Card(
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)),
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "CURRENT FOCUS",
                    color = CrimsonRed,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )
                
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(NavyBlue.copy(alpha = 0.1f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "NEPAL",
                        color = NavyBlue,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    lineHeight = 32.sp
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = subtitle,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 22.sp
            )
            
            if (trailingContent != null) {
                Spacer(modifier = Modifier.height(20.dp))
                trailingContent()
            }
        }
    }
}

// ==========================================
// EMERGENCY HUD FLOURISH
// ==========================================

@Composable
fun EmergencyBulletinBoard(
    headline: String,
    bulletin: String,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = CrimsonRed.copy(alpha = 0.08f)
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
            .fillMaxWidth()
            .border(1.2.dp, CrimsonRed.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            .padding(bottom = 16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(CrimsonRed),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "SOS Alert",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "\uD83D\uDEA8 $headline",
                    color = CrimsonRed,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = bulletin,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                    style = MaterialTheme.typography.bodySmall,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

// ==========================================
// COMPOSABLE CANVAS GRAPH VISUALIZER
// ==========================================

@Composable
fun FinancialExpensePieChart(
    educationAmt: Double,
    disasterAmt: Double,
    skillsAmt: Double,
    healthAmt: Double,
    modifier: Modifier = Modifier
) {
    val total = (educationAmt + disasterAmt + skillsAmt + healthAmt).coerceAtLeast(1.0)
    val colorEducation = NavyBlue
    val colorDisaster = SkyBlue
    val colorSkills = CrimsonRed
    val colorHealth = Color(0xFFFFB300)

    val sweepEducation = ((educationAmt / total) * 360f).toFloat()
    val sweepDisaster = ((disasterAmt / total) * 360f).toFloat()
    val sweepSkills = ((skillsAmt / total) * 360f).toFloat()
    val sweepHealth = ((healthAmt / total) * 360f).toFloat()

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(20.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Project-wise Spending Analytics",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier.size(160.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(140.dp)) {
                    var startAngle = 0f
                    drawArc(
                        color = colorEducation,
                        startAngle = startAngle,
                        sweepAngle = sweepEducation,
                        useCenter = false,
                        style = Stroke(width = 30f, cap = StrokeCap.Round)
                    )
                    startAngle += sweepEducation

                    drawArc(
                        color = colorDisaster,
                        startAngle = startAngle,
                        sweepAngle = sweepDisaster,
                        useCenter = false,
                        style = Stroke(width = 30f, cap = StrokeCap.Round)
                    )
                    startAngle += sweepDisaster

                    drawArc(
                        color = colorSkills,
                        startAngle = startAngle,
                        sweepAngle = sweepSkills,
                        useCenter = false,
                        style = Stroke(width = 30f, cap = StrokeCap.Round)
                    )
                    startAngle += sweepSkills

                    drawArc(
                        color = colorHealth,
                        startAngle = startAngle,
                        sweepAngle = sweepHealth,
                        useCenter = false,
                        style = Stroke(width = 30f, cap = StrokeCap.Round)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Total Spend",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                    Text(
                        text = "Rs.${"%,.0f".format(total)}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Legends Panel
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                LegendIndicator(color = colorEducation, label = "Education", percent = (educationAmt / total * 100).toInt())
                LegendIndicator(color = colorDisaster, label = "Disaster", percent = (disasterAmt / total * 100).toInt())
                LegendIndicator(color = colorSkills, label = "Skills", percent = (skillsAmt / total * 100).toInt())
                LegendIndicator(color = colorHealth, label = "Health", percent = (healthAmt / total * 100).toInt())
            }
        }
    }
}

@Composable
private fun LegendIndicator(color: Color, label: String, percent: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = "$percent%", fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
        Text(
            text = label,
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}
