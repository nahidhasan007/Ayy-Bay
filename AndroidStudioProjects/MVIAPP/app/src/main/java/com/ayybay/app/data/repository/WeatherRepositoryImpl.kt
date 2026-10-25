package com.ayybay.app.data.repository

import com.ayybay.app.data.local.CachedWeather
import com.ayybay.app.data.local.WeatherPreferences
import com.ayybay.app.data.remote.OpenMeteoClient
import com.ayybay.app.domain.model.WeatherAlertSettings
import com.ayybay.app.domain.model.WeatherLocation
import com.ayybay.app.domain.model.WeatherSnapshot
import com.ayybay.app.domain.repository.PrayerTimeRepository
import com.ayybay.app.domain.repository.WeatherRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class WeatherRepositoryImpl(
    private val client: OpenMeteoClient,
    private val weatherPreferences: WeatherPreferences,
    private val prayerTimeRepository: PrayerTimeRepository
) : WeatherRepository {

    override val snapshot: Flow<WeatherSnapshot?> = weatherPreferences.cached.map { cache ->
        // A cache that fails to parse (e.g. API shape changed) is treated as absent, not a crash.
        cache?.let {
            runCatching { client.parse(it.body, it.latitude, it.longitude, it.placeName, it.fetchedAtMillis) }.getOrNull()
        }
    }

    override val alertSettings: Flow<WeatherAlertSettings> = weatherPreferences.alertSettings

    override val locationOverride: Flow<WeatherLocation?> = weatherPreferences.locationOverride

    override suspend fun resolveLocation(): WeatherLocation {
        weatherPreferences.locationOverride.first()?.let { return it }
        val settings = prayerTimeRepository.getPrayerSettings().first()
        val lat = settings.locationLatitude
        val lon = settings.locationLongitude
        return if (lat != null && lon != null) {
            WeatherLocation(lat, lon, settings.placeName.orEmpty())
        } else {
            WeatherLocation(DHAKA_LAT, DHAKA_LON, "")
        }
    }

    override suspend fun refresh(): Result<WeatherSnapshot> {
        val location = resolveLocation()
        return try {
            val body = client.fetchBody(location.latitude, location.longitude)
            val now = System.currentTimeMillis()
            val snapshot = client.parse(body, location.latitude, location.longitude, location.placeName, now)
            weatherPreferences.saveCache(CachedWeather(body, location.latitude, location.longitude, location.placeName, now))
            Result.success(snapshot)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun setLocationOverride(location: WeatherLocation?) {
        weatherPreferences.setLocationOverride(location)
        // The cached forecast belongs to the previous place; drop it so the UI never shows it under the new name.
        weatherPreferences.clearCache()
    }

    override suspend fun setAlertsEnabled(enabled: Boolean) = weatherPreferences.setAlertsEnabled(enabled)

    override suspend fun setAlertThreshold(percent: Int) = weatherPreferences.setAlertThreshold(percent)

    override suspend fun lastRainAlertStartSec(): Long? = weatherPreferences.lastAlertStartSec()

    override suspend fun setLastRainAlertStartSec(value: Long) = weatherPreferences.setLastAlertStartSec(value)

    private companion object {
        const val DHAKA_LAT = 23.8103
        const val DHAKA_LON = 90.4125
    }
}
