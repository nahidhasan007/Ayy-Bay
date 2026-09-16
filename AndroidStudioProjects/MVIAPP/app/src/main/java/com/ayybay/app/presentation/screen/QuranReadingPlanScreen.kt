package com.ayybay.app.presentation.screen

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.ayybay.app.domain.model.QuranReadingPlanStatus
import com.ayybay.app.presentation.component.AppTopBar
import com.ayybay.app.presentation.component.DonutChart
import com.ayybay.app.presentation.component.LanguageToggle
import com.ayybay.app.presentation.language.AppLanguage
import com.ayybay.app.presentation.language.LocalAppLanguage
import com.ayybay.app.presentation.language.tr
import com.ayybay.app.presentation.util.formatBanglaDate
import com.ayybay.app.presentation.util.toBanglaNumber
import com.ayybay.app.ui.theme.BalanceOrange
import com.ayybay.app.ui.theme.ExpenseRed
import com.ayybay.app.ui.theme.IslamicGreen
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

private data class DurationPreset(val days: Int, val labelEn: String, val labelBn: String)

private val durationPresets = listOf(
    DurationPreset(15, "15 Days", "১৫ দিন"),
    DurationPreset(30, "1 Month", "১ মাস"),
    DurationPreset(60, "2 Months", "২ মাস"),
    DurationPreset(90, "3 Months", "৩ মাস")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranReadingPlanScreen(
    status: QuranReadingPlanStatus?,
    onStartPlan: (durationDays: Int, hour: Int, minute: Int) -> Unit,
    onUpdateReminderTime: (hour: Int, minute: Int) -> Unit,
    onCancelPlan: () -> Unit,
    onOpenSurahList: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            AppTopBar(
                title = { Text(text = "🗓️  " + tr("Quran Reading Plan", "কুরআন পড়ার পরিকল্পনা"), fontWeight = FontWeight.Bold) },
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
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            if (status == null) {
                PlanSetupContent(onStartPlan = onStartPlan)
            } else {
                ActivePlanContent(
                    status = status,
                    onUpdateReminderTime = onUpdateReminderTime,
                    onCancelPlan = onCancelPlan,
                    onOpenSurahList = onOpenSurahList
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlanSetupContent(onStartPlan: (durationDays: Int, hour: Int, minute: Int) -> Unit) {
    var selectedPreset by remember { mutableStateOf<Int?>(30) }
    var customDaysText by remember { mutableStateOf("") }
    val timePickerState = rememberTimePickerState(initialHour = 20, initialMinute = 0, is24Hour = false)

    val isCustom = selectedPreset == null
    val effectiveDays = if (isCustom) customDaysText.toIntOrNull() else selectedPreset
    val isValid = effectiveDays != null && effectiveDays > 0

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(shape = RoundedCornerShape(20.dp), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "📖 " + tr("Complete the Quran on your schedule", "আপনার সময়সূচি অনুযায়ী কুরআন সম্পন্ন করুন"),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = tr(
                            "Pick how many days you want to take. We'll split the surahs into a daily target and remind you if you fall behind.",
                            "আপনি কত দিনে শেষ করতে চান তা বেছে নিন। আমরা সূরাগুলো দৈনিক লক্ষ্যে ভাগ করে দেব এবং পিছিয়ে পড়লে মনে করিয়ে দেব।"
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        item {
            Text(text = tr("Choose a duration", "সময়কাল নির্বাচন করুন"), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                durationPresets.forEach { preset ->
                    FilterChip(
                        selected = selectedPreset == preset.days,
                        onClick = { selectedPreset = preset.days },
                        label = { Text(tr(preset.labelEn, preset.labelBn)) }
                    )
                }
                FilterChip(
                    selected = isCustom,
                    onClick = { selectedPreset = null },
                    label = { Text(tr("Custom", "কাস্টম")) }
                )
            }
        }

        if (isCustom) {
            item {
                OutlinedTextField(
                    value = customDaysText,
                    onValueChange = { value -> customDaysText = value.filter { it.isDigit() }.take(4) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(tr("Number of days", "দিনের সংখ্যা")) },
                    placeholder = { Text(tr("e.g. 45", "যেমন: ৪৫")) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        }

        item {
            Text(text = tr("Daily reminder time", "দৈনিক রিমাইন্ডার সময়"), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }

        item {
            Card(shape = RoundedCornerShape(16.dp), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
                Box(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), contentAlignment = Alignment.Center) {
                    TimePicker(state = timePickerState)
                }
            }
        }

        item {
            Button(
                onClick = {
                    val days = effectiveDays ?: return@Button
                    onStartPlan(days, timePickerState.hour, timePickerState.minute)
                },
                enabled = isValid,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(text = tr("Start Plan", "পরিকল্পনা শুরু করুন"), fontWeight = FontWeight.Bold)
            }
        }

        item { Spacer(modifier = Modifier.height(8.dp)) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ActivePlanContent(
    status: QuranReadingPlanStatus,
    onUpdateReminderTime: (hour: Int, minute: Int) -> Unit,
    onCancelPlan: () -> Unit,
    onOpenSurahList: () -> Unit
) {
    val language = LocalAppLanguage.current
    var showTimeDialog by remember { mutableStateOf(false) }
    var showCancelDialog by remember { mutableStateOf(false) }

    val percent = if (status.totalSurahs == 0) 0 else ((status.completedSurahs.toFloat() / status.totalSurahs) * 100).roundToInt()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(shape = RoundedCornerShape(20.dp), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.size(96.dp), contentAlignment = Alignment.Center) {
                        DonutChart(
                            slices = listOf(IslamicGreen to (if (status.totalSurahs == 0) 0f else status.completedSurahs.toFloat() / status.totalSurahs)),
                            modifier = Modifier.fillMaxSize()
                        )
                        Text(
                            text = if (language == AppLanguage.EN) "$percent%" else "${toBanglaNumber(percent)}%",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = tr(
                                "Day ${status.daysElapsed} of ${status.plan.durationDays}",
                                "${status.plan.durationDays} দিনের মধ্যে ${status.daysElapsed} দিন"
                            ),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = tr(
                                "${status.completedSurahs} of ${status.totalSurahs} surahs completed",
                                "${status.totalSurahs} টির মধ্যে ${status.completedSurahs} টি সূরা সম্পন্ন"
                            ),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        item {
            val (accent, icon, message) = when {
                status.isCompleted -> Triple(
                    IslamicGreen, "🎉",
                    tr("Khatam complete! May Allah accept it.", "খতম সম্পন্ন! আল্লাহ কবুল করুন।")
                )
                status.isBehind -> Triple(
                    BalanceOrange, "⚠️",
                    tr(
                        "You're ${status.surahsBehind} surah(s) behind today's target -- catch up!",
                        "আজকের লক্ষ্য থেকে আপনি ${status.surahsBehind} টি সূরা পিছিয়ে আছেন -- এগিয়ে যান!"
                    )
                )
                else -> Triple(
                    IslamicGreen, "✅",
                    tr("You're on track. Keep it up!", "আপনি ঠিক পথে আছেন। চালিয়ে যান!")
                )
            }
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = accent.copy(alpha = 0.12f))
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = icon, style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = message, fontWeight = FontWeight.SemiBold, color = accent)
                }
            }
        }

        item {
            Card(shape = RoundedCornerShape(16.dp), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    StatRow(
                        label = tr("Today's target", "আজকের লক্ষ্য"),
                        value = if (language == AppLanguage.EN) "${status.expectedByToday} / ${status.totalSurahs}"
                        else "${toBanglaNumber(status.expectedByToday)} / ${toBanglaNumber(status.totalSurahs)}"
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    StatRow(
                        label = tr("Days remaining", "বাকি দিন"),
                        value = if (language == AppLanguage.EN) "${status.daysRemaining}" else toBanglaNumber(status.daysRemaining)
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    StatRow(
                        label = tr("Target finish", "সমাপ্তির লক্ষ্য"),
                        value = formatTargetDate(status.targetEndDateKey, language)
                    )
                }
            }
        }

        item {
            Card(shape = RoundedCornerShape(16.dp), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = tr("Daily reminder", "দৈনিক রিমাইন্ডার"), fontWeight = FontWeight.SemiBold)
                        Text(
                            text = formatReminderTime(status.plan.reminderHour, status.plan.reminderMinute, language),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    TextButton(onClick = { showTimeDialog = true }) {
                        Text(tr("Change", "পরিবর্তন"))
                    }
                }
            }
        }

        item {
            Button(
                onClick = onOpenSurahList,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = tr("Open Surah List", "সূরার তালিকা খুলুন"), fontWeight = FontWeight.Bold)
            }
        }

        item {
            OutlinedButton(
                onClick = { showCancelDialog = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = ExpenseRed)
            ) {
                Text(tr("Cancel Plan", "পরিকল্পনা বাতিল করুন"))
            }
        }

        item { Spacer(modifier = Modifier.height(8.dp)) }
    }

    if (showTimeDialog) {
        ReminderTimeDialog(
            initialHour = status.plan.reminderHour,
            initialMinute = status.plan.reminderMinute,
            onConfirm = { hour, minute ->
                onUpdateReminderTime(hour, minute)
                showTimeDialog = false
            },
            onDismiss = { showTimeDialog = false }
        )
    }

    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = { Text(tr("Cancel plan?", "পরিকল্পনা বাতিল করবেন?")) },
            text = {
                Text(
                    tr(
                        "Your Surah completion progress stays saved -- only the plan schedule and reminders are removed.",
                        "আপনার সূরা সম্পন্ন করার অগ্রগতি সংরক্ষিত থাকবে -- শুধু পরিকল্পনার সময়সূচি ও রিমাইন্ডার মুছে যাবে।"
                    )
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    onCancelPlan()
                    showCancelDialog = false
                }) { Text(tr("Cancel Plan", "বাতিল করুন"), color = ExpenseRed) }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = false }) { Text(tr("Keep Plan", "রাখুন")) }
            }
        )
    }
}

@Composable
private fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, fontWeight = FontWeight.Bold)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReminderTimeDialog(
    initialHour: Int,
    initialMinute: Int,
    onConfirm: (hour: Int, minute: Int) -> Unit,
    onDismiss: () -> Unit
) {
    val timePickerState = rememberTimePickerState(initialHour = initialHour, initialMinute = initialMinute, is24Hour = false)
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(tr("Daily reminder time", "দৈনিক রিমাইন্ডার সময়")) },
        text = { TimePicker(state = timePickerState) },
        confirmButton = {
            TextButton(onClick = { onConfirm(timePickerState.hour, timePickerState.minute) }) {
                Text(tr("Save", "সংরক্ষণ"))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(tr("Cancel", "বাতিল")) }
        }
    )
}

private fun formatTargetDate(dateKey: Long, language: AppLanguage): String =
    if (language == AppLanguage.EN) SimpleDateFormat("d MMM yyyy", Locale.US).format(Date(dateKey))
    else formatBanglaDate(Date(dateKey))

private fun formatReminderTime(hour: Int, minute: Int, language: AppLanguage): String {
    val hourPart = if (hour % 12 == 0) 12 else hour % 12
    return if (language == AppLanguage.EN) {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
        }
        SimpleDateFormat("hh:mm a", Locale.US).format(calendar.time)
    } else {
        val minuteBn = if (minute < 10) "০${toBanglaNumber(minute)}" else toBanglaNumber(minute)
        val suffixBn = if (hour < 12) "পূর্বাহ্ণ" else "অপরাহ্ণ"
        "${toBanglaNumber(hourPart)}:$minuteBn $suffixBn"
    }
}
