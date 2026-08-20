package com.ayybay.app.presentation.language

import androidx.compose.runtime.Composable
import com.ayybay.app.domain.model.PrayerName

fun PrayerName.bnLabel(): String = when (this) {
    PrayerName.FAJR -> "ফজর"
    PrayerName.DHUHR -> "যোহর"
    PrayerName.ASR -> "আসর"
    PrayerName.MAGHRIB -> "মাগরিব"
    PrayerName.ISHA -> "ইশা"
}

@Composable
fun PrayerName.label(): String = tr(displayName, bnLabel())
