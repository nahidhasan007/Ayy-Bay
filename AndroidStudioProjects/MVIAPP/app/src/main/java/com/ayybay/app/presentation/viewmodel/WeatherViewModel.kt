package com.ayybay.app.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ayybay.app.domain.model.WeatherLocation
import com.ayybay.app.domain.repository.WeatherRepository
import com.ayybay.app.presentation.mvi.WeatherUiState
import com.ayybay.app.util.RainAlertLogic
import com.ayybay.app.worker.RainAlertWorker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WeatherViewModel(
    private val weatherRepository: WeatherRepository,
    private val appContext: Context
) : ViewModel() {

    private val isLoading = MutableStateFlow(false)
    private val hasError = MutableStateFlow(false)

    val uiState: StateFlow<WeatherUiState> = combine(
        weatherRepository.snapshot,
        weatherRepository.alertSettings,
        weatherRepository.locationOverride,
        isLoading,
        hasError
    ) { snapshot, settings, override, loading, error ->
        WeatherUiState(
            snapshot = snapshot,
            isLoading = loading,
            hasError = error,
            alertSettings = settings,
            usingCustomLocation = override != null,
            upcomingRain = snapshot?.let {
                RainAlertLogic.findRainAlert(it.hourly, System.currentTimeMillis() / 1000, settings.thresholdPercent)
            }
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), WeatherUiState())

    init {
        refreshIfStale()
    }

    /** Re-fetches unless the cached forecast is only a few minutes old. */
    fun refreshIfStale() {
        viewModelScope.launch {
            val cached = weatherRepository.snapshot.first()
            val age = System.currentTimeMillis() - (cached?.fetchedAtMillis ?: 0L)
            if (cached == null || age > STALE_AFTER_MILLIS) refreshNow()
        }
    }

    fun refresh() {
        viewModelScope.launch { refreshNow() }
    }

    private suspend fun refreshNow() {
        if (isLoading.value) return
        isLoading.value = true
        hasError.value = weatherRepository.refresh().isFailure
        isLoading.value = false
    }

    fun setAlertsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            weatherRepository.setAlertsEnabled(enabled)
            if (enabled) RainAlertWorker.schedule(appContext) else RainAlertWorker.cancel(appContext)
        }
    }

    fun setAlertThreshold(percent: Int) {
        viewModelScope.launch { weatherRepository.setAlertThreshold(percent) }
    }

    /** Picks a weather city; passing null goes back to following the prayer-time location. */
    fun setLocation(location: WeatherLocation?) {
        viewModelScope.launch {
            weatherRepository.setLocationOverride(location)
            refreshNow()
        }
    }

    private companion object {
        const val STALE_AFTER_MILLIS = 10 * 60 * 1000L
    }
}
