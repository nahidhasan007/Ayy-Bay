package com.ayybay.app.presentation.mvi

import com.ayybay.app.domain.model.DayPrayerProgress
import com.ayybay.app.domain.model.PrayerName
import com.ayybay.app.domain.model.QuranReadDay
import com.ayybay.app.domain.model.SurahProgress

data class TrackerUiState(
    val todayDateKey: Long = 0L,
    val todayPrayerLogs: Map<PrayerName, Boolean> = emptyMap(),
    val weeklyPrayerProgress: List<DayPrayerProgress> = emptyList(),
    val quranProgress: List<SurahProgress> = emptyList(),
    val quranWeeklyReading: List<QuranReadDay> = emptyList(),
    val quranStreak: Int = 0,
    val isLoading: Boolean = false
) {
    val todayPrayedCount: Int get() = todayPrayerLogs.values.count { it }
    val quranCompletedCount: Int get() = quranProgress.count { it.isCompleted }
}
