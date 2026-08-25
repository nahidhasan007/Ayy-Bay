package com.ayybay.app.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ayybay.app.presentation.component.LanguageToggle
import com.ayybay.app.presentation.language.tr
import com.ayybay.app.ui.theme.BalanceOrange
import com.ayybay.app.ui.theme.ExpenseRed
import com.ayybay.app.ui.theme.IncomeGreen
import com.ayybay.app.ui.theme.InfoBlue
import com.ayybay.app.ui.theme.IslamicGreen

private data class MoreItem(
    val labelEn: String,
    val labelBn: String,
    val subtitleEn: String,
    val subtitleBn: String,
    val icon: ImageVector,
    val color: Color,
    val onClick: () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreScreen(
    onNavigateNotes: () -> Unit,
    onNavigateSalahTracker: () -> Unit,
    onNavigateQuranProgress: () -> Unit,
    onNavigateAgeCalculator: () -> Unit,
    onNavigateBmiCalculator: () -> Unit,
    onNavigateFitnessAdvice: () -> Unit,
    onNavigateWebsites: () -> Unit
) {
    val items = listOf(
        MoreItem("Notes", "নোট", "Personal notes and reminders", "ব্যক্তিগত নোট ও রিমাইন্ডার", Icons.Default.Description, IslamicGreen, onNavigateNotes),
        MoreItem("Salah Tracker", "নামাজ ট্র্যাকার", "Track daily prayers and weekly progress", "দৈনিক নামাজ ও সাপ্তাহিক অগ্রগতি ট্র্যাক করুন", Icons.Default.Mosque, IslamicGreen, onNavigateSalahTracker),
        MoreItem("Quran Progress", "কুরআন অগ্রগতি", "Track Quran reading completion", "কুরআন পড়ার অগ্রগতি ট্র্যাক করুন", Icons.AutoMirrored.Filled.MenuBook, IslamicGreen, onNavigateQuranProgress),
        MoreItem("Age Calculator", "বয়স ক্যালকুলেটর", "Calculate your exact age", "আপনার সঠিক বয়স হিসাব করুন", Icons.Default.Cake, IncomeGreen, onNavigateAgeCalculator),
        MoreItem("BMI Calculator", "বিএমআই ক্যালকুলেটর", "Check your body mass index", "আপনার বডি মাস ইনডেক্স যাচাই করুন", Icons.Default.MonitorWeight, BalanceOrange, onNavigateBmiCalculator),
        MoreItem("Fitness Advice", "ফিটনেস পরামর্শ", "Personalized diet and exercise tips", "ব্যক্তিগত ডায়েট ও ব্যায়ামের পরামর্শ", Icons.Default.FitnessCenter, ExpenseRed, onNavigateFitnessAdvice),
        MoreItem("Useful Websites", "গুরুত্বপূর্ণ ওয়েবসাইট", "Government, news, jobs and more", "সরকারি, সংবাদ, চাকরি ও আরও অনেক কিছু", Icons.Default.Public, InfoBlue, onNavigateWebsites)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = tr("More", "আরও"), fontWeight = FontWeight.Bold) },
                actions = { LanguageToggle(modifier = Modifier.padding(end = 12.dp)) }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(items) { item -> MoreRow(item) }
        }
    }
}

@Composable
private fun MoreRow(item: MoreItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = item.onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(item.color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(item.icon, contentDescription = null, tint = item.color, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = tr(item.labelEn, item.labelBn), fontWeight = FontWeight.SemiBold)
                Text(
                    text = tr(item.subtitleEn, item.subtitleBn),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
