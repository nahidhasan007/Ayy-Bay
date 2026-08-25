package com.ayybay.app.presentation.mvi

import com.ayybay.app.domain.model.PrayerName

sealed class TrackerUiIntent {
    data class TogglePrayer(val prayerName: PrayerName, val isPrayed: Boolean) : TrackerUiIntent()
    data class ToggleSurahComplete(val surahNumber: Int, val completed: Boolean) : TrackerUiIntent()
    data class MarkSurahOpened(val surahNumber: Int) : TrackerUiIntent()
}
