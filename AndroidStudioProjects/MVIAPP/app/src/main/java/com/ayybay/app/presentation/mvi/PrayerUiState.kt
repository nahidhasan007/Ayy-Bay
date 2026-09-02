package com.ayybay.app.presentation.mvi

import com.ayybay.app.domain.model.PrayerSettings
import com.ayybay.app.domain.model.PrayerTime

data class PrayerUiState(
    val prayerTimes: List<PrayerTime> = emptyList(),
    val prayerSettings: PrayerSettings = PrayerSettings(),
    val isLoading: Boolean = false,
    val isLocating: Boolean = false,
    val locationError: String? = null,
    // Populated by later phases (Qibla / Hijri / Ramadan); kept here now so those
    // features have a stable place to land without another ViewModel surface change.
    val qiblaDirection: Double? = null,
    val hijriDateLabel: String? = null,
    val isRamadan: Boolean = false
)
