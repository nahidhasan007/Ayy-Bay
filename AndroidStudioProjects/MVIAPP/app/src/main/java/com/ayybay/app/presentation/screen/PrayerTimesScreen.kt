package com.ayybay.app.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ayybay.app.domain.model.CalculationMethod
import com.ayybay.app.domain.model.Madhab
import com.ayybay.app.domain.model.PrayerName
import com.ayybay.app.domain.model.PrayerSettings
import com.ayybay.app.domain.model.PrayerTime
import com.ayybay.app.presentation.component.LanguageToggle
import com.ayybay.app.presentation.language.AppLanguage
import com.ayybay.app.presentation.language.LocalAppLanguage
import com.ayybay.app.presentation.language.label
import com.ayybay.app.presentation.language.tr
import com.ayybay.app.presentation.util.banglaWeekday
import com.ayybay.app.presentation.util.formatBanglaDate
import com.ayybay.app.presentation.util.formatCountdown
import com.ayybay.app.presentation.util.nextPrayerOf
import com.ayybay.app.presentation.util.rememberTickingNow
import com.ayybay.app.service.AdhanForegroundService
import com.ayybay.app.ui.theme.IslamicGoldLight
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

private fun prayerIcon(name: PrayerName): String = when (name) {
    PrayerName.FAJR -> "🌙"
    PrayerName.DHUHR -> "🌤️"
    PrayerName.ASR -> "☀️"
    PrayerName.MAGHRIB -> "🌅"
    PrayerName.ISHA -> "🌠"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrayerTimesScreen(
    prayerTimes: List<PrayerTime>,
    prayerSettings: PrayerSettings,
    onTogglePrayerNotification: (PrayerName, Boolean) -> Unit,
    onUpdateSettings: (PrayerSettings) -> Unit,
    onBack: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "🕌  " + tr("Prayer Times", "নামাজের সময়"), fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = tr("Back", "পেছনে"))
                    }
                },
                actions = {
                    LanguageToggle(modifier = Modifier.padding(end = 12.dp))
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                val locationComingSoon = tr("Location settings coming soon", "লোকেশন সেটিংস শীঘ্রই আসছে")
                LocationRow(
                    onClick = {
                        scope.launch { snackbarHostState.showSnackbar(locationComingSoon) }
                    }
                )
            }

            item {
                PrayerCountdownCard(
                    prayerTimes = prayerTimes,
                    allEnabled = prayerTimes.isNotEmpty() && prayerTimes.all { it.isEnabled },
                    onToggleAll = { enabled -> prayerTimes.forEach { onTogglePrayerNotification(it.prayerName, enabled) } },
                    onPlayAdhan = {
                        val label = nextPrayerOf(prayerTimes, java.util.Date())?.first?.prayerName?.displayName ?: "Prayer"
                        AdhanForegroundService.startAdhan(context, label, 90)
                    }
                )
            }

            item {
                PrayerTable(prayerTimes = prayerTimes, onToggleNotification = onTogglePrayerNotification)
            }

            item {
                Text(
                    text = tr("Adhan Settings", "আজান সেটিংস"),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                SettingsRow(
                    icon = Icons.Default.Notifications,
                    title = tr("Enable Adhan (All Prayers)", "সকল নামাজে আজান চালু করুন"),
                    subtitle = tr("Plays the Adhan for every prayer time", "প্রতিটি নামাজের সময় আজান বাজবে"),
                    trailing = {
                        Switch(
                            checked = prayerTimes.isNotEmpty() && prayerTimes.all { it.isEnabled },
                            onCheckedChange = { enabled -> prayerTimes.forEach { onTogglePrayerNotification(it.prayerName, enabled) } }
                        )
                    }
                )
            }

            item {
                Text(
                    text = tr("Prayer Settings", "নামাজের সেটিংস"),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                var methodMenuExpanded by remember { mutableStateOf(false) }
                Box {
                    SettingsRow(
                        icon = Icons.Default.Public,
                        title = tr("Calculation Method", "হিসাব পদ্ধতি"),
                        subtitle = prayerSettings.calculationMethod.methodName,
                        onClick = { methodMenuExpanded = true },
                        trailing = { Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) }
                    )
                    DropdownMenu(expanded = methodMenuExpanded, onDismissRequest = { methodMenuExpanded = false }) {
                        CalculationMethod.entries.forEach { method ->
                            DropdownMenuItem(
                                text = { Text(method.methodName) },
                                onClick = {
                                    onUpdateSettings(prayerSettings.copy(calculationMethod = method))
                                    methodMenuExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            item {
                var madhabMenuExpanded by remember { mutableStateOf(false) }
                Box {
                    SettingsRow(
                        icon = Icons.AutoMirrored.Filled.MenuBook,
                        title = tr("Madhab", "মাযহাব"),
                        subtitle = if (prayerSettings.madhab == Madhab.HANAFI) tr("Hanafi", "হানাফি") else tr("Shafi'i", "শাফি'ই"),
                        onClick = { madhabMenuExpanded = true },
                        trailing = { Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) }
                    )
                    DropdownMenu(expanded = madhabMenuExpanded, onDismissRequest = { madhabMenuExpanded = false }) {
                        DropdownMenuItem(text = { Text(tr("Hanafi", "হানাফি")) }, onClick = {
                            onUpdateSettings(prayerSettings.copy(madhab = Madhab.HANAFI)); madhabMenuExpanded = false
                        })
                        DropdownMenuItem(text = { Text(tr("Shafi'i", "শাফি'ই")) }, onClick = {
                            onUpdateSettings(prayerSettings.copy(madhab = Madhab.SHAFI)); madhabMenuExpanded = false
                        })
                    }
                }
            }

            item {
                val locationPickerComingSoon = tr("Location picker coming soon", "লোকেশন নির্বাচন শীঘ্রই আসছে")
                SettingsRow(
                    icon = Icons.Default.LocationOn,
                    title = tr("Location", "অবস্থান"),
                    subtitle = tr("Dhaka, Bangladesh", "ঢাকা, বাংলাদেশ"),
                    onClick = { scope.launch { snackbarHostState.showSnackbar(locationPickerComingSoon) } },
                    trailing = { Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) }
                )
            }

            item {
                val notificationSettingsComingSoon = tr("Notification settings coming soon", "নোটিফিকেশন সেটিংস শীঘ্রই আসছে")
                SettingsRow(
                    icon = Icons.Default.Notifications,
                    title = tr("Notification Settings", "নোটিফিকেশন সেটিংস"),
                    subtitle = tr("Manage prayer time notifications", "নামাজের নোটিফিকেশন পরিচালনা করুন"),
                    onClick = { scope.launch { snackbarHostState.showSnackbar(notificationSettingsComingSoon) } },
                    trailing = { Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) }
                )
            }

            item { Spacer(modifier = Modifier.height(8.dp)) }
        }
    }
}

@Composable
private fun LocationRow(onClick: () -> Unit) {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(50)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = tr("Dhaka, Bangladesh", "ঢাকা, বাংলাদেশ"), fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.width(6.dp))
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun PrayerCountdownCard(
    prayerTimes: List<PrayerTime>,
    allEnabled: Boolean,
    onToggleAll: (Boolean) -> Unit,
    onPlayAdhan: () -> Unit
) {
    val now by rememberTickingNow()
    val language = LocalAppLanguage.current
    val dateFormat = remember { SimpleDateFormat("dd MMMM yyyy | EEEE", Locale.getDefault()) }
    val clockFormat = remember { SimpleDateFormat("hh:mm:ss a", Locale.getDefault()) }
    val nextPrayer = remember(prayerTimes, now) { nextPrayerOf(prayerTimes, now) }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (language == AppLanguage.EN) dateFormat.format(now) else "${formatBanglaDate(now)} | ${banglaWeekday(now)}",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White.copy(alpha = 0.85f)
                )
                Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = tr("Play Adhan", "আজান বাজান"), tint = Color.White, modifier = Modifier.clickable(onClick = onPlayAdhan))
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 14.dp), color = IslamicGoldLight.copy(alpha = 0.4f))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(text = tr("Next Prayer", "পরবর্তী নামাজ"), style = MaterialTheme.typography.labelMedium, color = Color.White.copy(alpha = 0.8f))
                    Text(
                        text = nextPrayer?.first?.prayerName?.label() ?: "-",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = IslamicGoldLight
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Schedule, contentDescription = null, tint = IslamicGoldLight, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = nextPrayer?.let { formatCountdown(it.second.time - now.time) } ?: "--:--:--",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = IslamicGoldLight
                        )
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = tr("Current Time", "বর্তমান সময়"), style = MaterialTheme.typography.labelMedium, color = Color.White.copy(alpha = 0.8f))
                    Text(
                        text = clockFormat.format(now),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(text = tr("Dhaka, Bangladesh", "ঢাকা, বাংলাদেশ"), style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.7f))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = tr("Adhan Notifications", "আজানের নোটিফিকেশন"), style = MaterialTheme.typography.bodyMedium, color = Color.White)
                Switch(
                    checked = allEnabled,
                    onCheckedChange = onToggleAll,
                    colors = SwitchDefaults.colors(checkedTrackColor = IslamicGoldLight, checkedThumbColor = Color.White)
                )
            }
        }
    }
}

@Composable
private fun PrayerTable(
    prayerTimes: List<PrayerTime>,
    onToggleNotification: (PrayerName, Boolean) -> Unit
) {
    val timeFormat = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }
    val now by rememberTickingNow(tickMillis = 30_000L)
    val nextPrayer = remember(prayerTimes, now) { nextPrayerOf(prayerTimes, now) }
    val sorted = remember(prayerTimes) { prayerTimes.sortedBy { it.prayerName.ordinal } }

    Card(shape = RoundedCornerShape(16.dp), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(tr("Prayer", "নামাজ"), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, modifier = Modifier.weight(2f))
                Text(tr("Time", "সময়"), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Text(tr("Adhan", "আজান"), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.7f), textAlign = androidx.compose.ui.text.style.TextAlign.End)
            }
            HorizontalDivider()
            sorted.forEachIndexed { index, prayer ->
                val isNext = prayer.prayerName == nextPrayer?.first?.prayerName
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(if (isNext) MaterialTheme.colorScheme.primary.copy(alpha = 0.08f) else Color.Transparent)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(modifier = Modifier.weight(2f), verticalAlignment = Alignment.CenterVertically) {
                        Text(text = prayerIcon(prayer.prayerName), fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = prayer.prayerName.label(),
                            fontWeight = if (isNext) FontWeight.Bold else FontWeight.Medium,
                            color = if (isNext) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = timeFormat.format(prayer.time),
                        modifier = Modifier.weight(1f),
                        fontWeight = if (isNext) FontWeight.Bold else FontWeight.Normal,
                        color = if (isNext) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                    Box(modifier = Modifier.weight(0.7f), contentAlignment = Alignment.CenterEnd) {
                        Switch(
                            checked = prayer.isEnabled,
                            onCheckedChange = { onToggleNotification(prayer.prayerName, it) },
                            modifier = Modifier.height(20.dp)
                        )
                    }
                }
                if (index < sorted.lastIndex) HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            }
        }
    }
}

@Composable
private fun SettingsRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: (() -> Unit)? = null,
    trailing: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        shape = RoundedCornerShape(14.dp),
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
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontWeight = FontWeight.SemiBold)
                Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            trailing()
        }
    }
}
