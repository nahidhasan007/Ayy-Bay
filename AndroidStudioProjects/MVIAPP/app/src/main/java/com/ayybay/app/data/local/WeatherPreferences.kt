package com.ayybay.app.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.ayybay.app.domain.model.WeatherAlertSettings
import com.ayybay.app.domain.model.WeatherLocation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.weatherDataStore by preferencesDataStore(name = "weather_prefs")

/** The last successful API response, kept verbatim so it can be shown offline and re-parsed. */
data class CachedWeather(
    val body: String,
    val latitude: Double,
    val longitude: Double,
    val placeName: String,
    val fetchedAtMillis: Long
)

class WeatherPreferences(private val context: Context) {

    private object Keys {
        val ALERTS_ENABLED = booleanPreferencesKey("alerts_enabled")
        val ALERT_THRESHOLD = intPreferencesKey("alert_threshold")
        val LAST_ALERT_START = longPreferencesKey("last_alert_start_sec")
        val OVERRIDE_LAT = doublePreferencesKey("override_lat")
        val OVERRIDE_LON = doublePreferencesKey("override_lon")
        val OVERRIDE_NAME = stringPreferencesKey("override_name")
        val CACHE_BODY = stringPreferencesKey("cache_body")
        val CACHE_LAT = doublePreferencesKey("cache_lat")
        val CACHE_LON = doublePreferencesKey("cache_lon")
        val CACHE_NAME = stringPreferencesKey("cache_name")
        val CACHE_AT = longPreferencesKey("cache_at")
    }

    val alertSettings: Flow<WeatherAlertSettings> = context.weatherDataStore.data.map { prefs ->
        WeatherAlertSettings(
            enabled = prefs[Keys.ALERTS_ENABLED] ?: false,
            thresholdPercent = prefs[Keys.ALERT_THRESHOLD] ?: WeatherAlertSettings.DEFAULT_RAIN_THRESHOLD_PERCENT
        )
    }

    val locationOverride: Flow<WeatherLocation?> = context.weatherDataStore.data.map { prefs ->
        val lat = prefs[Keys.OVERRIDE_LAT]
        val lon = prefs[Keys.OVERRIDE_LON]
        if (lat != null && lon != null) WeatherLocation(lat, lon, prefs[Keys.OVERRIDE_NAME].orEmpty()) else null
    }

    val cached: Flow<CachedWeather?> = context.weatherDataStore.data.map { prefs ->
        val body = prefs[Keys.CACHE_BODY]
        val lat = prefs[Keys.CACHE_LAT]
        val lon = prefs[Keys.CACHE_LON]
        if (body != null && lat != null && lon != null) {
            CachedWeather(body, lat, lon, prefs[Keys.CACHE_NAME].orEmpty(), prefs[Keys.CACHE_AT] ?: 0L)
        } else null
    }

    suspend fun setAlertsEnabled(enabled: Boolean) {
        context.weatherDataStore.edit { it[Keys.ALERTS_ENABLED] = enabled }
    }

    suspend fun setAlertThreshold(percent: Int) {
        context.weatherDataStore.edit { it[Keys.ALERT_THRESHOLD] = percent }
    }

    suspend fun lastAlertStartSec(): Long? = context.weatherDataStore.data.first()[Keys.LAST_ALERT_START]

    suspend fun setLastAlertStartSec(value: Long) {
        context.weatherDataStore.edit { it[Keys.LAST_ALERT_START] = value }
    }

    /** Passing null clears the override so weather follows the prayer-time location again. */
    suspend fun setLocationOverride(location: WeatherLocation?) {
        context.weatherDataStore.edit { prefs ->
            if (location == null) {
                prefs.remove(Keys.OVERRIDE_LAT)
                prefs.remove(Keys.OVERRIDE_LON)
                prefs.remove(Keys.OVERRIDE_NAME)
            } else {
                prefs[Keys.OVERRIDE_LAT] = location.latitude
                prefs[Keys.OVERRIDE_LON] = location.longitude
                prefs[Keys.OVERRIDE_NAME] = location.placeName
            }
        }
    }

    suspend fun saveCache(cache: CachedWeather) {
        context.weatherDataStore.edit { prefs ->
            prefs[Keys.CACHE_BODY] = cache.body
            prefs[Keys.CACHE_LAT] = cache.latitude
            prefs[Keys.CACHE_LON] = cache.longitude
            prefs[Keys.CACHE_NAME] = cache.placeName
            prefs[Keys.CACHE_AT] = cache.fetchedAtMillis
        }
    }

    suspend fun clearCache() {
        context.weatherDataStore.edit { prefs ->
            prefs.remove(Keys.CACHE_BODY)
            prefs.remove(Keys.CACHE_LAT)
            prefs.remove(Keys.CACHE_LON)
            prefs.remove(Keys.CACHE_NAME)
            prefs.remove(Keys.CACHE_AT)
        }
    }
}
