package com.ayybay.app.presentation.mvi

import com.ayybay.app.domain.model.PrayerName
import com.ayybay.app.domain.model.PrayerSettings

sealed class PrayerUiIntent {
    data class UpdateSettings(val settings: PrayerSettings) : PrayerUiIntent()
    data class ToggleNotification(val prayerName: PrayerName, val enabled: Boolean) : PrayerUiIntent()
    object ScheduleNotifications : PrayerUiIntent()
    object RefreshDay : PrayerUiIntent()
    object DetectLocation : PrayerUiIntent()
    data class SetManualLocation(val latitude: Double, val longitude: Double, val placeName: String) : PrayerUiIntent()
}
