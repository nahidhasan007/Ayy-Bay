package com.ayybay.app.presentation.screen

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ayybay.app.domain.model.LearningCategory
import com.ayybay.app.domain.model.LearningItem
import com.ayybay.app.presentation.component.AppTopBar
import com.ayybay.app.presentation.component.LanguageToggle
import com.ayybay.app.presentation.language.AppLanguage
import com.ayybay.app.presentation.language.LocalAppLanguage
import com.ayybay.app.presentation.language.tr
import com.ayybay.app.presentation.language.trOf
import com.ayybay.app.presentation.mvi.LearningUiState
import com.ayybay.app.presentation.util.localizeDigits
import com.ayybay.app.ui.theme.BalanceOrange
import com.ayybay.app.ui.theme.IncomeGreen
import com.ayybay.app.ui.theme.InfoBlue
import com.ayybay.app.ui.theme.IslamicGreen

/** Reminder times offered as chips: (hour of day, English label, Bangla label). */
private val ReminderHours = listOf(
    Triple(8, "8 AM", "সকাল ৮টা"),
    Triple(14, "2 PM", "দুপুর ২টা"),
    Triple(20, "8 PM", "রাত ৮টা"),
    Triple(22, "10 PM", "রাত ১০টা")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyLearningScreen(
    uiState: LearningUiState,
    onSetLearned: (itemId: String, learned: Boolean) -> Unit,
    onStartQuiz: () -> Unit,
    onSetReminderEnabled: (Boolean) -> Unit,
    onSetReminderHour: (Int) -> Unit,
    onBack: () -> Unit
) {
    val language = LocalAppLanguage.current

    Scaffold(
        topBar = {
            AppTopBar(
                title = { Text(text = "📚  " + tr("Daily Learning", "দৈনিক শিক্ষা"), fontWeight = FontWeight.Bold) },
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
            item { StreakCard(uiState, language) }

            items(uiState.items, key = { it.id }) { item ->
                LessonCard(
                    item = item,
                    isLearned = item.id in uiState.learnedTodayIds,
                    onSetLearned = { onSetLearned(item.id, it) }
                )
            }

            item { QuizCard(uiState, language, onStartQuiz) }

            item {
                ReminderCard(
                    enabled = uiState.reminderEnabled,
                    hour = uiState.reminderHour,
                    onSetEnabled = onSetReminderEnabled,
                    onSetHour = onSetReminderHour
                )
            }
        }
    }
}

@Composable
private fun StreakCard(uiState: LearningUiState, language: AppLanguage) {
    fun num(value: Int) = localizeDigits(value.toString(), language)
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "🔥", fontSize = 40.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = tr("${uiState.streak}-day streak", "${num(uiState.streak)} দিনের ধারা"),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Text(
                        text = if (uiState.streak == 0) {
                            tr("Learn something today to start one.", "ধারা শুরু করতে আজ কিছু শিখুন।")
                        } else {
                            tr("Learn something every day to keep it going.", "ধারা ধরে রাখতে প্রতিদিন কিছু শিখুন।")
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                StreakStat(
                    value = "${num(uiState.learnedTodayCount)}/${num(uiState.items.size)}",
                    label = tr("Today", "আজ")
                )
                StreakStat(value = num(uiState.longestStreak), label = tr("Best streak", "সেরা ধারা"))
                StreakStat(value = num(uiState.totalLearned), label = tr("Lessons learned", "শেখা পাঠ"))
            }
        }
    }
}

@Composable
private fun StreakStat(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
        )
    }
}

@Composable
private fun LessonCard(item: LearningItem, isLearned: Boolean, onSetLearned: (Boolean) -> Unit) {
    val language = LocalAppLanguage.current
    val context = LocalContext.current
    val accent = categoryColor(item.category)
    // GK answers stay hidden until tapped, so it works as a mini self-test.
    var answerRevealed by remember(item.id) { mutableStateOf(false) }

    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp)) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(accent.copy(alpha = 0.14f))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = categoryEmoji(item.category) + "  " + categoryLabel(item.category, language) +
                            " · " + trOf(language, item.tagEn, item.tagBn),
                        style = MaterialTheme.typography.labelMedium,
                        color = accent,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = {
                    val send = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, shareText(item, language))
                    }
                    context.startActivity(Intent.createChooser(send, null))
                }) {
                    Icon(Icons.Default.Share, contentDescription = tr("Share", "শেয়ার"), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            when (item.category) {
                LearningCategory.ISLAMIC -> {
                    Text(
                        text = item.arabic.orEmpty(),
                        modifier = Modifier.fillMaxWidth(),
                        fontSize = 26.sp,
                        lineHeight = 44.sp,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium
                    )
                    item.subline?.let {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = it,
                            modifier = Modifier.fillMaxWidth(),
                            style = MaterialTheme.typography.bodyMedium,
                            fontStyle = FontStyle.Italic,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = trOf(language, item.meaningEn, item.meaningBn), style = MaterialTheme.typography.bodyLarge)
                    item.reference?.let {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = it, style = MaterialTheme.typography.labelMedium, color = accent, fontWeight = FontWeight.SemiBold)
                    }
                }

                LearningCategory.VOCABULARY -> {
                    Text(text = item.titleEn, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                    item.subline?.let {
                        Text(text = "/$it/", style = MaterialTheme.typography.bodyMedium, fontStyle = FontStyle.Italic, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = trOf(language, item.meaningEn, item.meaningBn), style = MaterialTheme.typography.bodyLarge)
                    if (language == AppLanguage.BN) {
                        // The Bangla meaning is the point of the lesson; keep the English definition for context.
                        Text(text = item.meaningEn, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    item.noteEn?.let { example ->
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(text = "“$example”", style = MaterialTheme.typography.bodyMedium, fontStyle = FontStyle.Italic)
                        if (language == AppLanguage.BN) {
                            item.noteBn?.let { Text(text = it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                        }
                    }
                }

                LearningCategory.GENERAL_KNOWLEDGE -> {
                    Text(text = trOf(language, item.titleEn, item.titleBn), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(10.dp))
                    if (answerRevealed) {
                        Text(text = trOf(language, item.meaningEn, item.meaningBn), style = MaterialTheme.typography.titleMedium, color = accent, fontWeight = FontWeight.SemiBold)
                        val fact = trOf(language, item.noteEn.orEmpty(), item.noteBn.orEmpty())
                        if (fact.isNotBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = fact, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    } else {
                        OutlinedButton(onClick = { answerRevealed = true }, shape = RoundedCornerShape(12.dp)) {
                            Text(tr("Show answer", "উত্তর দেখুন"))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            if (isLearned) {
                OutlinedButton(
                    onClick = { onSetLearned(false) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp), tint = IncomeGreen)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(tr("Learned", "শেখা হয়েছে"), color = IncomeGreen)
                }
            } else {
                Button(
                    onClick = { onSetLearned(true) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                ) { Text(tr("Mark as learned", "শেখা হয়েছে চিহ্নিত করুন")) }
            }
        }
    }
}

@Composable
private fun QuizCard(uiState: LearningUiState, language: AppLanguage, onStartQuiz: () -> Unit) {
    val result = uiState.quizResultToday
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp)) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(text = "🧠  " + tr("Daily Quiz", "দৈনিক কুইজ"), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (result != null) {
                    tr(
                        "Today's best score: ${result.score}/${result.total}",
                        "আজকের সেরা স্কোর: ${localizeDigits("${result.score}/${result.total}", language)}"
                    )
                } else {
                    tr("5 quick questions on Islam, vocabulary and general knowledge.", "ইসলাম, শব্দভান্ডার ও সাধারণ জ্ঞানের ৫টি দ্রুত প্রশ্ন।")
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(onClick = onStartQuiz, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) {
                Text(if (result != null) tr("Retake quiz", "আবার কুইজ দিন") else tr("Start quiz", "কুইজ শুরু করুন"))
            }
        }
    }
}

@Composable
private fun ReminderCard(
    enabled: Boolean,
    hour: Int,
    onSetEnabled: (Boolean) -> Unit,
    onSetHour: (Int) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp)) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "⏰  " + tr("Daily reminder", "দৈনিক রিমাইন্ডার"), fontWeight = FontWeight.SemiBold)
                    Text(
                        text = tr(
                            "A gentle nudge if you haven't learned anything yet today.",
                            "আজ এখনো কিছু না শিখে থাকলে একটি ছোট্ট মনে করিয়ে দেওয়া।"
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Switch(checked = enabled, onCheckedChange = onSetEnabled)
            }
            if (enabled) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ReminderHours.forEach { (optionHour, labelEn, labelBn) ->
                        FilterChip(
                            selected = hour == optionHour,
                            onClick = { onSetHour(optionHour) },
                            label = { Text(tr(labelEn, labelBn)) }
                        )
                    }
                }
            }
        }
    }
}

private fun categoryColor(category: LearningCategory): Color = when (category) {
    LearningCategory.ISLAMIC -> IslamicGreen
    LearningCategory.VOCABULARY -> InfoBlue
    LearningCategory.GENERAL_KNOWLEDGE -> BalanceOrange
}

private fun categoryEmoji(category: LearningCategory): String = when (category) {
    LearningCategory.ISLAMIC -> "🕌"
    LearningCategory.VOCABULARY -> "🔤"
    LearningCategory.GENERAL_KNOWLEDGE -> "🌍"
}

private fun categoryLabel(category: LearningCategory, language: AppLanguage): String = when (category) {
    LearningCategory.ISLAMIC -> trOf(language, "Islamic", "ইসলামিক")
    LearningCategory.VOCABULARY -> trOf(language, "Vocabulary", "শব্দভান্ডার")
    LearningCategory.GENERAL_KNOWLEDGE -> trOf(language, "General Knowledge", "সাধারণ জ্ঞান")
}

private fun shareText(item: LearningItem, language: AppLanguage): String = buildString {
    when (item.category) {
        LearningCategory.ISLAMIC -> {
            appendLine(item.arabic)
            item.subline?.let { appendLine(it) }
            appendLine()
            appendLine(trOf(language, item.meaningEn, item.meaningBn))
            item.reference?.let { append("— $it") }
        }
        LearningCategory.VOCABULARY -> {
            appendLine("${item.titleEn} (${item.subline})")
            appendLine(trOf(language, item.meaningEn, item.meaningBn))
            item.noteEn?.let { append("“$it”") }
        }
        LearningCategory.GENERAL_KNOWLEDGE -> {
            appendLine(trOf(language, item.titleEn, item.titleBn))
            append(trOf(language, item.meaningEn, item.meaningBn))
        }
    }
    append("\n\n— " + trOf(language, "Shared from Jibon", "জীবন অ্যাপ থেকে শেয়ার করা"))
}
