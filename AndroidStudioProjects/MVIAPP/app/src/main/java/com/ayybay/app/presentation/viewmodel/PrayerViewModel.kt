package com.ayybay.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ayybay.app.data.location.LocationProvider
import com.ayybay.app.domain.model.PrayerName
import com.ayybay.app.domain.model.PrayerSettings
import com.ayybay.app.domain.model.PrayerTime
import com.ayybay.app.domain.usecase.GetPrayerSettingsUseCase
import com.ayybay.app.domain.usecase.GetPrayerTimesUseCase
import com.ayybay.app.domain.usecase.SchedulePrayerNotificationsUseCase
import com.ayybay.app.domain.usecase.TogglePrayerNotificationUseCase
import com.ayybay.app.domain.usecase.UpdatePrayerSettingsUseCase
import com.ayybay.app.presentation.mvi.PrayerUiIntent
import com.ayybay.app.presentation.mvi.PrayerUiState
import com.ayybay.app.util.startOfDayMillis
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Date

class PrayerViewModel(
    private val getPrayerTimesUseCase: GetPrayerTimesUseCase,
    private val getPrayerSettingsUseCase: GetPrayerSettingsUseCase,
    private val updatePrayerSettingsUseCase: UpdatePrayerSettingsUseCase,
    private val togglePrayerNotificationUseCase: TogglePrayerNotificationUseCase,
    private val schedulePrayerNotificationsUseCase: SchedulePrayerNotificationsUseCase,
    private val locationProvider: LocationProvider
) : ViewModel() {

    private val _rawPrayerTimes = MutableStateFlow<List<PrayerTime>>(emptyList())

    private val _prayerSettings = MutableStateFlow(PrayerSettings())
    val prayerSettings: StateFlow<PrayerSettings> = _prayerSettings.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isLocating = MutableStateFlow(false)
    private val _locationError = MutableStateFlow<String?>(null)

    /**
     * Raw calculator output overlaid with the persisted per-prayer enabled flags from
     * settings, so the UI's Adhan switches reflect what's actually scheduled (settings,
     * not the daily prayer_times row, are the source of truth for these flags).
     */
    val prayerTimes: StateFlow<List<PrayerTime>> = combine(_rawPrayerTimes, _prayerSettings) { times, settings ->
        times.map { it.copy(isEnabled = settings.isPrayerEnabled(it.prayerName)) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    /**
     * MVI-standard surface for newer consumers (Qibla/Hijri/Ramadan land their state here);
     * existing screens keep reading [prayerTimes]/[prayerSettings]/[isLoading] directly and
     * calling the public methods below -- both surfaces stay in sync since they share state.
     */
    val uiState: StateFlow<PrayerUiState> = combine(
        prayerTimes, _prayerSettings, _isLoading, _isLocating, _locationError
    ) { times, settings, loading, locating, locationError ->
        PrayerUiState(
            prayerTimes = times,
            prayerSettings = settings,
            isLoading = loading,
            isLocating = locating,
            locationError = locationError
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), PrayerUiState())

    private var currentDayKey: Long = Date().startOfDayMillis()
    private var prayerTimesJob: Job? = null

    init {
        loadPrayerTimes()
        loadPrayerSettings()
    }

    fun handleIntent(intent: PrayerUiIntent) {
        when (intent) {
            is PrayerUiIntent.UpdateSettings -> updatePrayerSettings(intent.settings)
            is PrayerUiIntent.ToggleNotification -> togglePrayerNotification(intent.prayerName, intent.enabled)
            PrayerUiIntent.ScheduleNotifications -> scheduleNotifications()
            PrayerUiIntent.RefreshDay -> refreshDay()
            PrayerUiIntent.DetectLocation -> detectLocation()
            is PrayerUiIntent.SetManualLocation -> setManualLocation(intent.latitude, intent.longitude, intent.placeName)
        }
    }

    /** Looks up the device's current location via GPS and, if found, saves it as the prayer-time location (also re-triggers a reschedule with the new coordinates). */
    fun detectLocation() {
        viewModelScope.launch {
            _isLocating.value = true
            _locationError.value = null
            try {
                val location = locationProvider.getCurrentLocation()
                if (location != null) {
                    updatePrayerSettings(
                        _prayerSettings.value.copy(
                            locationLatitude = location.latitude,
                            locationLongitude = location.longitude,
                            placeName = location.placeName,
                            autoLocationEnabled = true
                        )
                    )
                } else {
                    _locationError.value = "unavailable"
                }
            } catch (e: Exception) {
                _locationError.value = e.message
            } finally {
                _isLocating.value = false
            }
        }
    }

    /** Sets the prayer-time location from a manually-picked city (the fallback when GPS is denied/unavailable). */
    fun setManualLocation(latitude: Double, longitude: Double, placeName: String) {
        updatePrayerSettings(
            _prayerSettings.value.copy(
                locationLatitude = latitude,
                locationLongitude = longitude,
                placeName = placeName,
                autoLocationEnabled = false
            )
        )
    }

    /**
     * Re-derives today's prayer times if the calendar day has rolled over since the last
     * load (the previous implementation captured `Date()` once in [init] and kept collecting
     * that same day's Flow forever). Safe to call often -- it's a no-op until the day changes.
     */
    fun refreshDay() {
        val newKey = Date().startOfDayMillis()
        if (newKey != currentDayKey) {
            currentDayKey = newKey
            loadPrayerTimes()
        }
    }

    private fun loadPrayerTimes() {
        prayerTimesJob?.cancel()
        prayerTimesJob = viewModelScope.launch {
            _isLoading.value = true
            try {
                val today = Date()
                getPrayerTimesUseCase(today).collect { times ->
                    _rawPrayerTimes.value = times
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
                // Immediately schedule or cancel this prayer's alarm to match the new flag,
                // rather than waiting for it to take effect next time a prayer fires.
                schedulePrayerNotificationsUseCase()
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
