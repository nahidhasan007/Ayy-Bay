package com.ayybay.app.domain.model

data class CurrentWeather(
    val tempC: Double,
    val feelsLikeC: Double,
    val humidityPercent: Int,
    val windKmh: Double,
    val precipitationMm: Double,
    val weatherCode: Int,
    val isDay: Boolean
)

/** One hour of forecast. [timeSec] is epoch seconds (UTC) of the start of the hour. */
data class HourlyForecast(
    val timeSec: Long,
    val tempC: Double,
    val precipProbabilityPercent: Int,
    val precipitationMm: Double,
    val weatherCode: Int
)

/** One day of forecast. [dateSec] is epoch seconds (UTC) of local midnight at the forecast location. */
data class DailyForecast(
    val dateSec: Long,
    val tempMaxC: Double,
    val tempMinC: Double,
    val precipProbabilityPercent: Int,
    val precipitationSumMm: Double,
    val weatherCode: Int
)

data class WeatherSnapshot(
    val placeName: String,
    val latitude: Double,
    val longitude: Double,
    /** Offset of the forecast location from UTC, used to render its local times. */
    val utcOffsetSeconds: Int,
    val fetchedAtMillis: Long,
    val current: CurrentWeather,
    val hourly: List<HourlyForecast>,
    val daily: List<DailyForecast>
)

/** Where weather is fetched for: either an explicit pick or the saved prayer-time location. */
data class WeatherLocation(
    val latitude: Double,
    val longitude: Double,
    val placeName: String
)

data class RainAlert(
    /** Start of the first hour that crosses the threshold, epoch seconds (UTC). */
    val startSec: Long,
    val probabilityPercent: Int,
    val precipitationMm: Double,
    val isThunderstorm: Boolean,
    val isHeavy: Boolean
)

data class WeatherAlertSettings(
    val enabled: Boolean = false,
    /** Minimum chance of rain (percent) in an upcoming hour that triggers an alert. */
    val thresholdPercent: Int = DEFAULT_RAIN_THRESHOLD_PERCENT
) {
    companion object {
        const val DEFAULT_RAIN_THRESHOLD_PERCENT = 60
    }
}
