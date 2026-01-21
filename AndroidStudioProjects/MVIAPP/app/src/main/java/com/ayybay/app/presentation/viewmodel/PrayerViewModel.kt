package com.ayybay.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ayybay.app.domain.model.PrayerName
import com.ayybay.app.domain.model.PrayerSettings
import com.ayybay.app.domain.model.PrayerTime
import com.ayybay.app.domain.usecase.GetPrayerSettingsUseCase
import com.ayybay.app.domain.usecase.GetPrayerTimesUseCase
import com.ayybay.app.domain.usecase.SchedulePrayerNotificationsUseCase
import com.ayybay.app.domain.usecase.TogglePrayerNotificationUseCase
import com.ayybay.app.domain.usecase.UpdatePrayerSettingsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date

class PrayerViewModel(
    private val getPrayerTimesUseCase: GetPrayerTimesUseCase,
    private val getPrayerSettingsUseCase: GetPrayerSettingsUseCase,
    private val updatePrayerSettingsUseCase: UpdatePrayerSettingsUseCase,
    private val togglePrayerNotificationUseCase: TogglePrayerNotificationUseCase,
    private val schedulePrayerNotificationsUseCase: SchedulePrayerNotificationsUseCase
) : ViewModel() {

    private val _prayerTimes = MutableStateFlow<List<PrayerTime>>(emptyList())
    val prayerTimes: StateFlow<List<PrayerTime>> = _prayerTimes.asStateFlow()

    private val _prayerSettings = MutableStateFlow(PrayerSettings())
    val prayerSettings: StateFlow<PrayerSettings> = _prayerSettings.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadPrayerTimes()
        loadPrayerSettings()
    }

    private fun loadPrayerTimes() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val today = Date()
                getPrayerTimesUseCase(today).collect { prayerTimes ->
                    _prayerTimes.value = prayerTimes
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun loadPrayerSettings() {
        viewModelScope.launch {
            try {
                getPrayerSettingsUseCase().collect { settings ->
                    _prayerSettings.value = settings
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updatePrayerSettings(settings: PrayerSettings) {
        viewModelScope.launch {
            try {
                updatePrayerSettingsUseCase(settings)
                // Reschedule notifications with new settings
                schedulePrayerNotificationsUseCase()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun togglePrayerNotification(prayerName: PrayerName, enabled: Boolean) {
        viewModelScope.launch {
            try {
                togglePrayerNotificationUseCase(prayerName, enabled)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun scheduleNotifications() {
        viewModelScope.launch {
            try {
                schedulePrayerNotificationsUseCase()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}