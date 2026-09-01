package com.ayybay.app.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ayybay.app.domain.model.QuranReadDay
import com.ayybay.app.presentation.component.AppTopBar
import com.ayybay.app.presentation.component.DonutChart
import com.ayybay.app.presentation.component.LanguageToggle
import com.ayybay.app.presentation.component.WeeklyBarChart
import com.ayybay.app.presentation.language.AppLanguage
import com.ayybay.app.presentation.language.LocalAppLanguage
import com.ayybay.app.presentation.language.tr
import com.ayybay.app.presentation.util.banglaWeekdayShort
import com.ayybay.app.presentation.util.toBanglaNumber
import com.ayybay.app.ui.theme.IslamicGreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranProgressScreen(
    completedCount: Int,
    totalSurahs: Int,
    streakDays: Int,
    weeklyReading: List<QuranReadDay>,
    onOpenSurahList: () -> Unit,
    onBack: () -> Unit
) {
    val language = LocalAppLanguage.current
    val percent = if (totalSurahs == 0) 0 else ((completedCount.toFloat() / totalSurahs) * 100).roundToInt()

    Scaffold(
        topBar = {
            AppTopBar(
                title = { Text(text = "📖  " + tr("Quran Progress", "কুরআন অগ্রগতি"), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = tr("Back", "পেছনে"))
                    }
                },
                actions = {
                    LanguageToggle(
                        modifier = Modifier.padding(end = 12.dp),
                        selectedColor = MaterialTheme.colorScheme.onPrimary,
                        unselectedColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                        borderColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.4f),
                        dividerColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f)
                    )
                }
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
                        Box(modifier = Modifier.size(96.dp), contentAlignment = Alignment.Center) {
                            DonutChart(
                                slices = listOf(IslamicGreen to (if (totalSurahs == 0) 0f else completedCount.toFloat() / totalSurahs)),
                                modifier = Modifier.fillMaxSize()
                            )
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = if (language == AppLanguage.EN) "$percent%" else "${toBanglaNumber(percent)}%",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(text = tr("Surahs Completed", "সম্পন্ন সূরা"), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(
                                text = tr("$completedCount of $totalSurahs surahs", "$totalSurahs টির মধ্যে $completedCount টি সূরা"),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            item {
                Card(shape = RoundedCornerShape(16.dp), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = tr("Reading Streak", "পড়ার স্ট্রিক"), fontWeight = FontWeight.SemiBold)
                            Text(
                                text = tr("Keep reading daily to grow your streak", "স্ট্রিক বাড়াতে প্রতিদিন পড়ুন"),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = "🔥 " + tr("$streakDays days", "${toBanglaNumber(streakDays)} দিন"),
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium,
                            color = IslamicGreen
                        )
                    }
                }
            }

            item {
                Text(text = tr("This Week's Reading", "এই সপ্তাহের পড়া"), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }

            item {
                Card(shape = RoundedCornerShape(16.dp), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        val bars = weeklyReading.map { day ->
                            val label = if (language == AppLanguage.EN) {
                                SimpleDateFormat("EEE", Locale.getDefault()).format(Date(day.dateKey))
                            } else {
                                banglaWeekdayShort(Date(day.dateKey))
                            }
                            label to (if (day.surahsOpened > 0) 1f else 0f)
                        }
                        WeeklyBarChart(bars = bars, barColor = IslamicGreen)
                    }
                }
            }

            item {
                Button(
                    onClick = onOpenSurahList,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = tr("Open Surah List", "সূরার তালিকা খুলুন"), fontWeight = FontWeight.Bold)
                }
            }

            item { Spacer(modifier = Modifier.height(8.dp)) }
        }
    }
}
