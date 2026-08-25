package com.ayybay.app.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ayybay.app.data.local.FitnessTip
import com.ayybay.app.data.local.FitnessTipData
import com.ayybay.app.data.local.TipGroup
import com.ayybay.app.presentation.component.LanguageToggle
import com.ayybay.app.presentation.language.label
import com.ayybay.app.presentation.language.tr
import com.ayybay.app.presentation.util.bmiOf
import com.ayybay.app.presentation.util.calculateAge
import com.ayybay.app.presentation.util.categoryOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FitnessAdviceScreen(
    dateOfBirth: Long?,
    heightCm: Double?,
    weightKg: Double?,
    onNavigateAge: () -> Unit,
    onNavigateBmi: () -> Unit,
    onBack: () -> Unit
) {
    val now = remember { System.currentTimeMillis() }
    val ageYears = dateOfBirth?.let { calculateAge(it, now).years }
    val bmi = if (heightCm != null && heightCm > 0 && weightKg != null && weightKg > 0) bmiOf(heightCm, weightKg) else null
    val category = bmi?.let { categoryOf(it) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "💪  " + tr("Fitness Advice", "ফিটনেস পরামর্শ"), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = tr("Back", "পেছনে"))
                    }
                },
                actions = { LanguageToggle(modifier = Modifier.padding(end = 12.dp)) }
            )
        }
    ) { paddingValues ->
        if (category == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(shape = RoundedCornerShape(20.dp), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
                    Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = tr("Set your height and weight to get personalized advice", "ব্যক্তিগত পরামর্শ পেতে আপনার উচ্চতা ও ওজন দিন"),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = onNavigateBmi, shape = RoundedCornerShape(14.dp)) {
                            Text(tr("Open BMI Calculator", "বিএমআই ক্যালকুলেটর খুলুন"))
                        }
                    }
                }
            }
            return@Scaffold
        }

        val tips = remember(category, ageYears) { FitnessTipData.tipsFor(category, ageYears) }
        val grouped = remember(tips) { tips.groupBy { it.group } }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Card(shape = RoundedCornerShape(16.dp), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = tr("Based on your profile", "আপনার প্রোফাইল অনুসারে"),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = tr(
                                "BMI ${String.format("%.1f", bmi)} (${category.displayName})" + (ageYears?.let { " · Age $it" } ?: ""),
                                "বিএমআই ${String.format("%.1f", bmi)} (${category.label()})" + (ageYears?.let { " · বয়স $it" } ?: "")
                            ),
                            fontWeight = FontWeight.SemiBold
                        )
                        if (ageYears == null) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = tr("Add your date of birth for age-tailored tips  >", "বয়স-উপযোগী পরামর্শের জন্য জন্ম তারিখ যোগ করুন  >"),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.clickable(onClick = onNavigateAge)
                            )
                        }
                    }
                }
            }

            TipGroup.entries.forEach { group ->
                val groupTips = grouped[group].orEmpty()
                if (groupTips.isNotEmpty()) {
                    item {
                        Text(
                            text = groupTitle(group),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    items(groupTips) { tip -> TipCard(tip) }
                }
            }

            item {
                Text(
                    text = tr(
                        "General wellness guidance only — not medical advice.",
                        "শুধুমাত্র সাধারণ স্বাস্থ্য পরামর্শ — চিকিৎসা পরামর্শ নয়।"
                    ),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            item { Spacer(modifier = Modifier.height(8.dp)) }
        }
    }
}

@Composable
private fun groupTitle(group: TipGroup): String = when (group) {
    TipGroup.DIET -> tr("Diet", "খাদ্যাভ্যাস")
    TipGroup.EXERCISE -> tr("Exercise", "ব্যায়াম")
    TipGroup.SLEEP -> tr("Sleep", "ঘুম")
    TipGroup.HABIT -> tr("Daily Habits", "দৈনন্দিন অভ্যাস")
}

@Composable
private fun TipCard(tip: FitnessTip) {
    Card(shape = RoundedCornerShape(14.dp), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.Top) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(tip.icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = tr(tip.titleEn, tip.titleBn), fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = tr(tip.bodyEn, tip.bodyBn),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
