package com.ayybay.app.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ayybay.app.data.local.BangladeshCities
import com.ayybay.app.domain.model.DailyForecast
import com.ayybay.app.domain.model.HourlyForecast
import com.ayybay.app.domain.model.RainAlert
import com.ayybay.app.domain.model.WeatherLocation
import com.ayybay.app.domain.model.WeatherSnapshot
import com.ayybay.app.presentation.component.AppTopBar
import com.ayybay.app.presentation.component.LanguageToggle
import com.ayybay.app.presentation.language.AppLanguage
import com.ayybay.app.presentation.language.LocalAppLanguage
import com.ayybay.app.presentation.language.tr
import com.ayybay.app.presentation.language.trOf
import com.ayybay.app.presentation.mvi.WeatherUiState
import com.ayybay.app.presentation.util.formatHourLabel
import com.ayybay.app.presentation.util.formatLocalTime
import com.ayybay.app.presentation.util.formatWeekdayLabel
import com.ayybay.app.presentation.util.localizeDigits
import com.ayybay.app.presentation.util.weatherEmoji
import com.ayybay.app.presentation.util.weatherLabel
import com.ayybay.app.ui.theme.BalanceOrange
import com.ayybay.app.ui.theme.InfoBlue
import kotlin.math.roundToInt

private val ThresholdOptions = listOf(40, 60, 80)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(
    uiState: WeatherUiState,
    onRefresh: () -> Unit,
    onSetAlertsEnabled: (Boolean) -> Unit,
    onSetThreshold: (Int) -> Unit,
    onSelectLocation: (WeatherLocation?) -> Unit,
    onBack: () -> Unit
) {
    var showLocationSheet by remember { mutableStateOf(false) }
    val language = LocalAppLanguage.current
    val snapshot = uiState.snapshot

    if (showLocationSheet) {
        LocationSheet(
            usingCustomLocation = uiState.usingCustomLocation,
            onSelect = { onSelectLocation(it); showLocationSheet = false },
            onDismiss = { showLocationSheet = false }
        )
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = { Text(text = "🌦️  " + tr("Weather", "আবহাওয়া"), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = tr("Back", "পেছনে"))
                    }
                },
                actions = {
                    IconButton(onClick = onRefresh, enabled = !uiState.isLoading) {
                        Icon(Icons.Default.Refresh, contentDescription = tr("Refresh", "রিফ্রেশ"))
                    }
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
            if (uiState.isLoading) {
                item { LinearProgressIndicator(modifier = Modifier.fillMaxWidth()) }
            }

            item {
                LocationRow(
                    placeName = snapshot?.placeName,
                    onChange = { showLocationSheet = true }
                )
            }

            if (uiState.hasError) {
                item {
                    NoticeBanner(
                        text = if (snapshot != null) {
                            tr("Couldn't update. Showing the last saved forecast.", "আপডেট করা যায়নি। সর্বশেষ সংরক্ষিত পূর্বাভাস দেখানো হচ্ছে।")
                        } else {
                            tr("Couldn't load the forecast. Check your internet connection and try again.", "পূর্বাভাস লোড করা যায়নি। ইন্টারনেট সংযোগ যাচাই করে আবার চেষ্টা করুন।")
                        },
                        color = BalanceOrange
                    )
                }
            }

            uiState.upcomingRain?.let { rain ->
                if (snapshot != null) item { RainBanner(rain, snapshot.utcOffsetSeconds, language) }
            }

            if (snapshot != null) {
                item { CurrentCard(snapshot, language) }

                val nowHour = System.currentTimeMillis() / 1000 / 3600 * 3600
                val upcomingHours = snapshot.hourly.filter { it.timeSec >= nowHour }.take(24)
                if (upcomingHours.isNotEmpty()) {
                    item { SectionTitle(tr("Next 24 Hours", "পরবর্তী ২৪ ঘণ্টা")) }
                    item { HourlyStrip(upcomingHours, snapshot.utcOffsetSeconds, language) }
                }

                item { SectionTitle(tr("7-Day Forecast", "৭ দিনের পূর্বাভাস")) }
                item { DailyCard(snapshot.daily, snapshot.utcOffsetSeconds, language) }
            } else if (!uiState.isLoading && !uiState.hasError) {
                item { Spacer(modifier = Modifier.height(24.dp)) }
            }

            item { SectionTitle(tr("Rain Alerts", "বৃষ্টির সতর্কতা")) }
            item {
                AlertSettingsCard(
                    enabled = uiState.alertSettings.enabled,
                    thresholdPercent = uiState.alertSettings.thresholdPercent,
                    onSetEnabled = onSetAlertsEnabled,
                    onSetThreshold = onSetThreshold
                )
            }

            item {
                Text(
                    text = tr("Weather data by Open-Meteo.com", "আবহাওয়ার তথ্য: Open-Meteo.com"),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text = text, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
}

@Composable
private fun LocationRow(placeName: String?, onChange: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = placeName?.takeIf { it.isNotBlank() } ?: tr("Your location", "আপনার অবস্থান"),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f),
            maxLines = 1
        )
        TextButton(onClick = onChange) { Text(tr("Change", "পরিবর্তন")) }
    }
}

@Composable
private fun NoticeBanner(text: String, color: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.12f))
    ) {
        Text(text = text, modifier = Modifier.padding(14.dp), style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun RainBanner(rain: RainAlert, utcOffsetSeconds: Int, language: AppLanguage) {
    val time = formatHourLabel(rain.startSec, utcOffsetSeconds, language)
    val chance = localizeDigits(rain.probabilityPercent.toString(), language)
    val headline = when {
        rain.isThunderstorm -> tr("⛈️ Thunderstorm expected", "⛈️ বজ্রসহ বৃষ্টির সম্ভাবনা")
        rain.isHeavy -> tr("🌧️ Heavy rain expected", "🌧️ ভারী বৃষ্টির সম্ভাবনা")
        else -> tr("🌧️ Rain expected soon", "🌧️ শীঘ্রই বৃষ্টির সম্ভাবনা")
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = InfoBlue.copy(alpha = 0.14f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = headline, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = tr("$chance% chance around $time. Take an umbrella!", "$time নাগাদ $chance% সম্ভাবনা। ছাতা সঙ্গে রাখুন!"),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun CurrentCard(snapshot: WeatherSnapshot, language: AppLanguage) {
    val current = snapshot.current
    val today = snapshot.daily.firstOrNull()
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = weatherEmoji(current.weatherCode, current.isDay), fontSize = 56.sp)
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = degrees(current.tempC, language),
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Text(
                        text = weatherLabel(current.weatherCode, language),
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                CurrentStat(tr("Feels like", "অনুভূত"), degrees(current.feelsLikeC, language))
                CurrentStat(tr("Humidity", "আর্দ্রতা"), localizeDigits("${current.humidityPercent}%", language))
                CurrentStat(tr("Wind", "বাতাস"), localizeDigits("${current.windKmh.roundToInt()} km/h", language))
                today?.let {
                    CurrentStat(tr("Rain", "বৃষ্টি"), localizeDigits("${it.precipProbabilityPercent}%", language))
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = tr("Updated ", "আপডেট: ") + localizeDigits(formatLocalTime(snapshot.fetchedAtMillis / 1000, snapshot.utcOffsetSeconds, "h:mm a"), language),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.75f)
            )
        }
    }
}

@Composable
private fun CurrentStat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onPrimary)
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
        )
    }
}

@Composable
private fun HourlyStrip(hours: List<HourlyForecast>, utcOffsetSeconds: Int, language: AppLanguage) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        items(hours, key = { it.timeSec }) { hour ->
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = formatHourLabel(hour.timeSec, utcOffsetSeconds, language).let { localizeDigits(it, language) },
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = weatherEmoji(hour.weatherCode), fontSize = 24.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = degrees(hour.tempC, language), fontWeight = FontWeight.SemiBold)
                Text(
                    text = "💧" + localizeDigits("${hour.precipProbabilityPercent}%", language),
                    style = MaterialTheme.typography.labelSmall,
                    color = InfoBlue
                )
            }
        }
    }
}

@Composable
private fun DailyCard(days: List<DailyForecast>, utcOffsetSeconds: Int, language: AppLanguage) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            days.forEachIndexed { index, day ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (index == 0) tr("Today", "আজ") else formatWeekdayLabel(day.dateSec, utcOffsetSeconds, language),
                        modifier = Modifier.width(64.dp),
                        fontWeight = FontWeight.Medium
                    )
                    Text(text = weatherEmoji(day.weatherCode), fontSize = 22.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "💧" + localizeDigits("${day.precipProbabilityPercent}%", language),
                        style = MaterialTheme.typography.labelMedium,
                        color = InfoBlue,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = degrees(day.tempMaxC, language) + "  ",
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = degrees(day.tempMinC, language),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (index != days.lastIndex) {
                    HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                }
            }
        }
    }
}

@Composable
private fun AlertSettingsCard(
    enabled: Boolean,
    thresholdPercent: Int,
    onSetEnabled: (Boolean) -> Unit,
    onSetThreshold: (Int) -> Unit
) {
    val language = LocalAppLanguage.current
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = tr("Notify me about rain", "বৃষ্টির আগে জানান"), fontWeight = FontWeight.SemiBold)
                    Text(
                        text = tr(
                            "Get an alert when rain or a thunderstorm is likely in the next few hours.",
                            "আগামী কয়েক ঘণ্টায় বৃষ্টি বা বজ্রঝড়ের সম্ভাবনা থাকলে সতর্কতা পান।"
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Switch(checked = enabled, onCheckedChange = onSetEnabled)
            }
            if (enabled) {
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = tr("Alert when the chance of rain is at least", "বৃষ্টির সম্ভাবনা অন্তত এতটা হলে সতর্ক করুন"),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ThresholdOptions.forEach { option ->
                        FilterChip(
                            selected = thresholdPercent == option,
                            onClick = { onSetThreshold(option) },
                            label = { Text(localizeDigits("$option%", language)) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = tr(
                        "Checked about every 30 minutes while you have an internet connection.",
                        "ইন্টারনেট সংযোগ থাকলে প্রায় প্রতি ৩০ মিনিটে যাচাই করা হয়।"
                    ),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LocationSheet(
    usingCustomLocation: Boolean,
    onSelect: (WeatherLocation?) -> Unit,
    onDismiss: () -> Unit
) {
    val language = LocalAppLanguage.current
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
            Text(
                text = tr("Weather Location", "আবহাওয়ার অবস্থান"),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelect(null) },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
            ) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Home, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = tr("Use my prayer-time location", "আমার নামাজের সময়ের অবস্থান ব্যবহার করুন"), fontWeight = FontWeight.Medium)
                        if (!usingCustomLocation) {
                            Text(
                                text = tr("Currently selected", "বর্তমানে নির্বাচিত"),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = tr("Or choose a city", "অথবা একটি শহর বেছে নিন"),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(modifier = Modifier.heightIn(max = 360.dp)) {
                items(BangladeshCities.all(), key = { it.nameEn }) { city ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onSelect(WeatherLocation(city.latitude, city.longitude, trOf(language, city.nameEn, city.nameBn)))
                            }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = tr(city.nameEn, city.nameBn))
                    }
                    HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

private fun degrees(tempC: Double, language: AppLanguage): String =
    localizeDigits("${tempC.roundToInt()}°", language)
