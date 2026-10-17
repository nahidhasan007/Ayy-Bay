package com.ayybay.app.presentation.screen

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.ayybay.app.domain.model.StepDay
import com.ayybay.app.presentation.component.AppTopBar
import com.ayybay.app.presentation.component.DonutChart
import com.ayybay.app.presentation.component.LanguageToggle
import com.ayybay.app.presentation.component.WeeklyBarChart
import com.ayybay.app.presentation.language.AppLanguage
import com.ayybay.app.presentation.language.LocalAppLanguage
import com.ayybay.app.presentation.language.tr
import com.ayybay.app.presentation.mvi.StepUiState
import com.ayybay.app.presentation.util.banglaWeekdayShort
import com.ayybay.app.presentation.util.formatBanglaMonthYear
import com.ayybay.app.presentation.util.toBanglaNumber
import com.ayybay.app.ui.theme.IslamicGreen
import com.ayybay.app.util.StepMath
import com.ayybay.app.util.StepPermission
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StepTrackerScreen(
    uiState: StepUiState,
    heightCm: Double?,
    onPermissionResult: (Boolean) -> Unit,
    onStartTracking: () -> Unit,
    onStopTracking: () -> Unit,
    onSetGoal: (Int) -> Unit,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var showGoalDialog by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        onPermissionResult(granted)
    }

    // Re-check on every resume: the user may have flipped the permission in system settings.
    // Live tracking only runs while this screen is visible; the background worker covers the rest.
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    onPermissionResult(StepPermission.isGranted(context))
                    onStartTracking()
                }
                Lifecycle.Event.ON_PAUSE -> onStopTracking()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            onStopTracking()
        }
    }
    // Permission granted while visible: begin streaming without waiting for the next resume.
    LaunchedEffect(uiState.hasPermission) {
        if (uiState.hasPermission) onStartTracking()
    }

    if (showGoalDialog) {
        GoalDialog(
            currentGoal = uiState.dailyGoal,
            onConfirm = { onSetGoal(it); showGoalDialog = false },
            onDismiss = { showGoalDialog = false }
        )
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = { Text(text = "👣  " + tr("Step Tracker", "স্টেপ ট্র্যাকার"), fontWeight = FontWeight.Bold) },
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
            if (!uiState.isSensorAvailable) {
                item {
                    NoticeCard(
                        message = tr(
                            "This device has no step counter sensor, so steps can't be tracked.",
                            "এই ডিভাইসে স্টেপ কাউন্টার সেন্সর নেই, তাই পদক্ষেপ গণনা করা যাচ্ছে না।"
                        )
                    )
                }
            } else if (!uiState.hasPermission) {
                item {
                    NoticeCard(
                        message = tr(
                            "Allow physical activity access so Jibon can count your steps.",
                            "জীবন যেন আপনার পদক্ষেপ গুনতে পারে, সে জন্য শারীরিক কার্যকলাপের অনুমতি দিন।"
                        )
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { permissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION) },
                                shape = RoundedCornerShape(14.dp)
                            ) { Text(tr("Allow", "অনুমতি দিন")) }
                            TextButton(onClick = {
                                context.startActivity(
                                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", context.packageName, null))
                                )
                            }) { Text(tr("App settings", "অ্যাপ সেটিংস")) }
                        }
                    }
                }
            }

            item { TodayCard(uiState, heightCm, onEditGoal = { showGoalDialog = true }) }

            item { SectionTitle(tr("Last 7 Days", "গত ৭ দিন")) }
            item { WeekCard(uiState, heightCm) }

            item { SectionTitle(tr("Monthly Steps", "মাসিক পদক্ষেপ")) }
            item { MonthCard(uiState, heightCm, onPreviousMonth, onNextMonth) }

            val listedDays = uiState.monthDays.filter { it.steps > 0 }.sortedByDescending { it.dateKey }
            if (listedDays.isNotEmpty()) {
                items(listedDays, key = { it.dateKey }) { day ->
                    DayRow(day, uiState.dailyGoal, heightCm)
                }
            }

            item {
                Text(
                    text = tr(
                        "Distance is estimated from your step count" +
                            (if (heightCm != null) " and height." else ". Add your height in the BMI Calculator for a closer estimate."),
                        "দূরত্ব আপনার পদক্ষেপ" +
                            (if (heightCm != null) " ও উচ্চতা থেকে আনুমানিকভাবে হিসাব করা।" else " থেকে আনুমানিকভাবে হিসাব করা। আরও সঠিক হিসাবের জন্য বিএমআই ক্যালকুলেটরে উচ্চতা দিন।")
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
private fun SectionTitle(text: String) {
    Text(text = text, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
}

@Composable
private fun NoticeCard(message: String, actions: @Composable (() -> Unit)? = null) {
    Card(shape = RoundedCornerShape(16.dp), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = message)
            if (actions != null) {
                Spacer(modifier = Modifier.height(12.dp))
                actions()
            }
        }
    }
}

@Composable
private fun TodayCard(uiState: StepUiState, heightCm: Double?, onEditGoal: () -> Unit) {
    val goal = uiState.dailyGoal
    val progress = if (goal > 0) uiState.todaySteps.toFloat() / goal else 0f
    Card(shape = RoundedCornerShape(20.dp), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(120.dp), contentAlignment = Alignment.Center) {
                DonutChart(
                    slices = listOf(IslamicGreen to progress.coerceIn(0f, 1f)),
                    modifier = Modifier.fillMaxSize(),
                    strokeWidth = 14.dp
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = formatNumber(uiState.todaySteps.toLong()),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = tr("steps", "পদক্ষেপ"),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(modifier = Modifier.width(20.dp))
            Column {
                Text(text = tr("Today", "আজ"), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(
                    text = formatKm(StepMath.kilometers(uiState.todaySteps.toLong(), heightCm)),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = IslamicGreen
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (uiState.todaySteps >= goal) {
                        tr("Goal reached!", "লক্ষ্য পূরণ হয়েছে!")
                    } else {
                        tr(
                            "${formatNumber((goal - uiState.todaySteps).toLong())} to goal",
                            "লক্ষ্যে পৌঁছাতে ${formatNumber((goal - uiState.todaySteps).toLong())} বাকি"
                        )
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                TextButton(onClick = onEditGoal, contentPadding = PaddingValues(0.dp)) {
                    Text(tr("Goal: ${formatNumber(goal.toLong())}  ›", "লক্ষ্য: ${formatNumber(goal.toLong())}  ›"))
                }
            }
        }
    }
}

@Composable
private fun WeekCard(uiState: StepUiState, heightCm: Double?) {
    val language = LocalAppLanguage.current
    val goal = uiState.dailyGoal
    Card(shape = RoundedCornerShape(16.dp), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            val bars = uiState.week.map { day ->
                val label = if (language == AppLanguage.EN) {
                    SimpleDateFormat("EEE", Locale.US).format(Date(day.dateKey))
                } else {
                    banglaWeekdayShort(Date(day.dateKey))
                }
                label to (if (goal > 0) day.steps.toFloat() / goal else 0f)
            }
            WeeklyBarChart(bars = bars, barColor = IslamicGreen)
            Spacer(modifier = Modifier.height(8.dp))
            val total = uiState.weekTotalSteps
            Text(
                text = tr(
                    "This week: ${formatNumber(total)} steps · ${formatKm(StepMath.kilometers(total, heightCm))}",
                    "এই সপ্তাহে: ${formatNumber(total)} পদক্ষেপ · ${formatKm(StepMath.kilometers(total, heightCm))}"
                ),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun MonthCard(
    uiState: StepUiState,
    heightCm: Double?,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    val language = LocalAppLanguage.current
    val monthDate = Date(uiState.monthStartKey)
    val monthLabel = if (language == AppLanguage.EN) {
        SimpleDateFormat("MMMM yyyy", Locale.US).format(monthDate)
    } else {
        formatBanglaMonthYear(monthDate)
    }
    // For the running month, average over the days that have happened so far.
    val elapsedDays = if (uiState.isCurrentMonth) {
        Calendar.getInstance().apply { timeInMillis = uiState.todayKey }.get(Calendar.DAY_OF_MONTH)
    } else {
        Calendar.getInstance().apply { timeInMillis = uiState.monthStartKey }.getActualMaximum(Calendar.DAY_OF_MONTH)
    }
    val total = uiState.monthTotalSteps
    val best = uiState.monthBestDay

    Card(shape = RoundedCornerShape(16.dp), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onPreviousMonth) {
                    Icon(Icons.Default.KeyboardArrowLeft, contentDescription = tr("Previous month", "আগের মাস"))
                }
                Text(
                    text = monthLabel,
                    modifier = Modifier.weight(1f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    fontWeight = FontWeight.SemiBold
                )
                IconButton(onClick = onNextMonth, enabled = !uiState.isCurrentMonth) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = tr("Next month", "পরের মাস"))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatTile(tr("Total steps", "মোট পদক্ষেপ"), formatNumber(total), Modifier.weight(1f))
                StatTile(tr("Total distance", "মোট দূরত্ব"), formatKm(StepMath.kilometers(total, heightCm)), Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatTile(tr("Daily average", "দৈনিক গড়"), formatNumber(uiState.monthDailyAverage(elapsedDays)), Modifier.weight(1f))
                StatTile(
                    tr("Best day", "সেরা দিন"),
                    best?.let { formatNumber(it.steps.toLong()) } ?: "–",
                    Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = tr(
                    "Goal reached on ${uiState.monthDaysGoalReached} day(s)",
                    "${toBanglaNumber(uiState.monthDaysGoalReached)} দিন লক্ষ্য পূরণ হয়েছে"
                ),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun StatTile(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(IslamicGreen.copy(alpha = 0.08f))
            .padding(12.dp)
    ) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun DayRow(day: StepDay, goal: Int, heightCm: Double?) {
    val language = LocalAppLanguage.current
    val date = Date(day.dateKey)
    val dateLabel = if (language == AppLanguage.EN) {
        SimpleDateFormat("EEE, d MMM", Locale.US).format(date)
    } else {
        banglaWeekdayShort(date) + ", " + toBanglaNumber(Calendar.getInstance().apply { time = date }.get(Calendar.DAY_OF_MONTH))
    }
    val reached = day.steps >= goal
    Card(shape = RoundedCornerShape(14.dp), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = dateLabel, fontWeight = FontWeight.SemiBold)
                Text(
                    text = formatKm(StepMath.kilometers(day.steps.toLong(), heightCm)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(text = formatNumber(day.steps.toLong()), fontWeight = FontWeight.Bold)
            if (reached) {
                Spacer(modifier = Modifier.width(10.dp))
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(IslamicGreen),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                }
            }
        }
    }
}

@Composable
private fun GoalDialog(currentGoal: Int, onConfirm: (Int) -> Unit, onDismiss: () -> Unit) {
    var text by remember { mutableStateOf(currentGoal.toString()) }
    val goal = text.toIntOrNull()
    val valid = goal != null && goal in 1_000..100_000
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(tr("Daily step goal", "দৈনিক পদক্ষেপের লক্ষ্য")) },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { input -> if (input.length <= 6 && input.all { it.isDigit() }) text = input },
                singleLine = true,
                isError = !valid,
                supportingText = { Text(tr("Between 1,000 and 100,000", "১,০০০ থেকে ১,০০,০০০ এর মধ্যে")) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        },
        confirmButton = {
            TextButton(onClick = { goal?.let(onConfirm) }, enabled = valid) { Text(tr("Save", "সংরক্ষণ")) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(tr("Cancel", "বাতিল")) } }
    )
}

private fun formatNumber(value: Long): String = NumberFormat.getIntegerInstance(Locale.US).format(value)

private fun formatKm(km: Double): String = String.format(Locale.US, "%.2f km", km)
