package com.ayybay.app.presentation.mvi

import com.ayybay.app.domain.model.RainAlert
import com.ayybay.app.domain.model.WeatherAlertSettings
import com.ayybay.app.domain.model.WeatherSnapshot

data class WeatherUiState(
    val snapshot: WeatherSnapshot? = null,
    val isLoading: Boolean = false,
    /** True when the last refresh failed; [snapshot] may still hold older cached data. */
    val hasError: Boolean = false,
    val alertSettings: WeatherAlertSettings = WeatherAlertSettings(),
    /** True when the user picked a weather city instead of following the prayer-time location. */
    val usingCustomLocation: Boolean = false,
    /** Rain expected within the next few hours at the user's threshold, regardless of whether alerts are on. */
    val upcomingRain: RainAlert? = null
)
