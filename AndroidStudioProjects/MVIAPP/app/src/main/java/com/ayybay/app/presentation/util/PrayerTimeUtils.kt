package com.ayybay.app.presentation.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.ayybay.app.domain.model.PrayerTime
import kotlinx.coroutines.delay
import java.util.Date

/** Ticks every [tickMillis] so countdowns stay live while a screen is visible. */
@Composable
fun rememberTickingNow(tickMillis: Long = 1000L): State<Date> {
    val now = remember { mutableStateOf(Date()) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(tickMillis)
            now.value = Date()
        }
    }
    return now
}

/**
 * Returns the next upcoming prayer and its due time. If every prayer for today has
 * already passed, falls back to today's earliest prayer time shifted by 24h as an
 * approximation of tomorrow's first prayer (exact recalculation happens once the
 * new day's prayer times are loaded).
 */
fun nextPrayerOf(prayerTimes: List<PrayerTime>, now: Date): Pair<PrayerTime, Date>? {
    val sorted = prayerTimes.sortedBy { it.prayerName.ordinal }
    if (sorted.isEmpty()) return null
    val upcoming = sorted.filter { it.time.after(now) }.minByOrNull { it.time }
    if (upcoming != null) return upcoming to upcoming.time
    val earliest = sorted.minByOrNull { it.time } ?: return null
    return earliest to Date(earliest.time.time + 24L * 60 * 60 * 1000)
}

fun formatCountdown(diffMs: Long): String {
    if (diffMs <= 0) return "00:00:00"
    val totalSeconds = diffMs / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}
