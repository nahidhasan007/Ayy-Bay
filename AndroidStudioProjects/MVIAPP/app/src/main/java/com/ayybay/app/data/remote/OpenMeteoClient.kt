package com.ayybay.app.data.remote

import com.ayybay.app.domain.model.CurrentWeather
import com.ayybay.app.domain.model.DailyForecast
import com.ayybay.app.domain.model.HourlyForecast
import com.ayybay.app.domain.model.WeatherSnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

/**
 * Minimal client for the free Open-Meteo forecast API (no API key). Uses `timeformat=unixtime`
 * so every timestamp is epoch seconds and no ISO/zone parsing is needed.
 */
class OpenMeteoClient {

    /** Raw JSON body, so callers can cache it and re-[parse] later. Throws on network/HTTP failure. */
    suspend fun fetchBody(latitude: Double, longitude: Double): String =
        withContext(Dispatchers.IO) {
            val url = URL(
                "https://api.open-meteo.com/v1/forecast" +
                    "?latitude=$latitude&longitude=$longitude" +
                    "&current=temperature_2m,relative_humidity_2m,apparent_temperature,is_day," +
                    "precipitation,weather_code,wind_speed_10m" +
                    "&hourly=temperature_2m,precipitation_probability,precipitation,weather_code" +
                    "&daily=weather_code,temperature_2m_max,temperature_2m_min," +
                    "precipitation_sum,precipitation_probability_max" +
                    "&timezone=auto&timeformat=unixtime&forecast_days=7"
            )
            val connection = (url.openConnection() as HttpURLConnection).apply {
                connectTimeout = TIMEOUT_MS
                readTimeout = TIMEOUT_MS
                requestMethod = "GET"
            }
            try {
                val code = connection.responseCode
                if (code != HttpURLConnection.HTTP_OK) throw java.io.IOException("Weather request failed: HTTP $code")
                connection.inputStream.bufferedReader().use { it.readText() }
            } finally {
                connection.disconnect()
            }
        }

    fun parse(
        body: String,
        latitude: Double,
        longitude: Double,
        placeName: String,
        fetchedAtMillis: Long
    ): WeatherSnapshot {
        val root = JSONObject(body)
        val current = root.getJSONObject("current")
        val hourly = root.getJSONObject("hourly")
        val daily = root.getJSONObject("daily")

        val hourlyTimes = hourly.getJSONArray("time")
        val hourlyTemps = hourly.getJSONArray("temperature_2m")
        val hourlyProb = hourly.getJSONArray("precipitation_probability")
        val hourlyPrecip = hourly.getJSONArray("precipitation")
        val hourlyCodes = hourly.getJSONArray("weather_code")

        val dailyTimes = daily.getJSONArray("time")
        val dailyMax = daily.getJSONArray("temperature_2m_max")
        val dailyMin = daily.getJSONArray("temperature_2m_min")
        val dailyProb = daily.getJSONArray("precipitation_probability_max")
        val dailySum = daily.getJSONArray("precipitation_sum")
        val dailyCodes = daily.getJSONArray("weather_code")

        return WeatherSnapshot(
            placeName = placeName,
            latitude = latitude,
            longitude = longitude,
            utcOffsetSeconds = root.optInt("utc_offset_seconds", 0),
            fetchedAtMillis = fetchedAtMillis,
            current = CurrentWeather(
                tempC = current.optDouble("temperature_2m"),
                feelsLikeC = current.optDouble("apparent_temperature"),
                humidityPercent = current.optInt("relative_humidity_2m"),
                windKmh = current.optDouble("wind_speed_10m"),
                precipitationMm = current.optDouble("precipitation", 0.0),
                weatherCode = current.optInt("weather_code"),
                isDay = current.optInt("is_day", 1) == 1
            ),
            hourly = (0 until hourlyTimes.length()).map { i ->
                HourlyForecast(
                    timeSec = hourlyTimes.getLong(i),
                    tempC = hourlyTemps.doubleOrZero(i),
                    precipProbabilityPercent = hourlyProb.intOrZero(i),
                    precipitationMm = hourlyPrecip.doubleOrZero(i),
                    weatherCode = hourlyCodes.intOrZero(i)
                )
            },
            daily = (0 until dailyTimes.length()).map { i ->
                DailyForecast(
                    dateSec = dailyTimes.getLong(i),
                    tempMaxC = dailyMax.doubleOrZero(i),
                    tempMinC = dailyMin.doubleOrZero(i),
                    precipProbabilityPercent = dailyProb.intOrZero(i),
                    precipitationSumMm = dailySum.doubleOrZero(i),
                    weatherCode = dailyCodes.intOrZero(i)
                )
            }
        )
    }

    // The API returns JSON null for values it has no model data for (e.g. probability far out).
    private fun JSONArray.doubleOrZero(i: Int): Double = if (isNull(i)) 0.0 else getDouble(i)
    private fun JSONArray.intOrZero(i: Int): Int = if (isNull(i)) 0 else getInt(i)

    private companion object {
        const val TIMEOUT_MS = 15_000
    }
}
