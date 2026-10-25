package com.ayybay.app.domain.repository

import com.ayybay.app.domain.model.WeatherAlertSettings
import com.ayybay.app.domain.model.WeatherLocation
import com.ayybay.app.domain.model.WeatherSnapshot
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    /** Last successfully fetched forecast (survives restarts), or null if none yet. */
    val snapshot: Flow<WeatherSnapshot?>
    val alertSettings: Flow<WeatherAlertSettings>
    /** Explicitly picked weather location; null means "follow the prayer-time location". */
    val locationOverride: Flow<WeatherLocation?>

    /** The override if set, else the saved prayer-time location, else Dhaka. */
    suspend fun resolveLocation(): WeatherLocation

    /** Fetches a fresh forecast for [resolveLocation] and caches it. */
    suspend fun refresh(): Result<WeatherSnapshot>

    suspend fun setLocationOverride(location: WeatherLocation?)
    suspend fun setAlertsEnabled(enabled: Boolean)
    suspend fun setAlertThreshold(percent: Int)

    suspend fun lastRainAlertStartSec(): Long?
    suspend fun setLastRainAlertStartSec(value: Long)
}
