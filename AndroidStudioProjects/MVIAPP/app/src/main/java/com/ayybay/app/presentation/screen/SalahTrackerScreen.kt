package com.ayybay.app.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ayybay.app.domain.model.DayPrayerProgress
import com.ayybay.app.domain.model.PrayerName
import com.ayybay.app.domain.model.PrayerTime
import com.ayybay.app.presentation.component.DonutChart
import com.ayybay.app.presentation.component.LanguageToggle
import com.ayybay.app.presentation.component.WeeklyBarChart
import com.ayybay.app.presentation.language.AppLanguage
import com.ayybay.app.presentation.language.LocalAppLanguage
import com.ayybay.app.presentation.language.label
import com.ayybay.app.presentation.language.tr
import com.ayybay.app.presentation.util.banglaWeekdayShort
import com.ayybay.app.ui.theme.IslamicGreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalahTrackerScreen(
    prayerTimes: List<PrayerTime>,
    todayPrayerLogs: Map<PrayerName, Boolean>,
    weeklyProgress: List<DayPrayerProgress>,
    onTogglePrayer: (PrayerName, Boolean) -> Unit,
    onBack: () -> Unit
) {
    val language = LocalAppLanguage.current
    val timeFormat = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }
    val prayedCount = todayPrayerLogs.values.count { it }
    val total = PrayerName.entries.size
    val sortedPrayers = remember(prayerTimes) { prayerTimes.sortedBy { it.prayerName.ordinal } }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "🕌  " + tr("Salah Tracker", "নামাজ ট্র্যাকার"), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = tr("Back", "পেছনে"))
                    }
                },
                actions = { LanguageToggle(modifier = Modifier.padding(end = 12.dp)) }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(shape = RoundedCornerShape(20.dp), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.size(90.dp), contentAlignment = Alignment.Center) {
                            DonutChart(
                                slices = listOf(IslamicGreen to (prayedCount.toFloat() / total)),
                                modifier = Modifier.fillMaxSize()
                            )
                            Text(text = "$prayedCount/$total", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(text = tr("Today's Salah", "আজকের নামাজ"), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(
                                text = if (prayedCount == total) {
                                    tr("Alhamdulillah, all done!", "আলহামদুলিল্লাহ, সবগুলো সম্পন্ন!")
                                } else {
                                    tr("$prayedCount of $total prayed", "$total টির মধ্যে $prayedCount টি পড়া হয়েছে")
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            items(sortedPrayers, key = { it.prayerName }) { prayer ->
                val isPrayed = todayPrayerLogs[prayer.prayerName] ?: false
                PrayerLogRow(
                    prayer = prayer,
                    isPrayed = isPrayed,
                    timeText = timeFormat.format(prayer.time),
                    onToggle = { onTogglePrayer(prayer.prayerName, !isPrayed) }
                )
            }

            item {
                Text(
                    text = tr("This Week", "এই সপ্তাহে"),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Card(shape = RoundedCornerShape(16.dp), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        val bars = weeklyProgress.map { day ->
                            val label = if (language == AppLanguage.EN) {
                                SimpleDateFormat("EEE", Locale.getDefault()).format(Date(day.dateKey))
                            } else {
                                banglaWeekdayShort(Date(day.dateKey))
                            }
                            label to (day.prayedCount.toFloat() / day.total)
                        }
                        WeeklyBarChart(bars = bars, barColor = IslamicGreen)
                        Spacer(modifier = Modifier.height(8.dp))
                        val weekTotal = weeklyProgress.sumOf { it.prayedCount }
                        Text(
                            text = tr(
                                "This week: $weekTotal/${weeklyProgress.size * total}",
                                "এই সপ্তাহে: $weekTotal/${weeklyProgress.size * total}"
                            ),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(8.dp)) }
        }
    }
}

@Composable
private fun PrayerLogRow(
    prayer: PrayerTime,
    isPrayed: Boolean,
    timeText: String,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = prayer.prayerName.label(), fontWeight = FontWeight.SemiBold)
                Text(text = timeText, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(if (isPrayed) IslamicGreen else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                if (isPrayed) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}
