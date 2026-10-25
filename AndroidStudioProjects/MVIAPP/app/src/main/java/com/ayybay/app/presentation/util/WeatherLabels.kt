package com.ayybay.app.presentation.util

import com.ayybay.app.presentation.language.AppLanguage
import com.ayybay.app.presentation.language.trOf
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

private val banglaDigitChars = charArrayOf('০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯')

/** Swaps ASCII digits for Bangla digits when [language] is Bangla; other characters pass through. */
fun localizeDigits(text: String, language: AppLanguage): String =
    if (language == AppLanguage.EN) text
    else text.map { c -> if (c in '0'..'9') banglaDigitChars[c - '0'] else c }.joinToString("")

fun weatherEmoji(code: Int, isDay: Boolean = true): String = when (code) {
    0 -> if (isDay) "☀️" else "🌙"
    1, 2 -> if (isDay) "🌤️" else "☁️"
    3 -> "☁️"
    45, 48 -> "🌫️"
    in 51..57 -> "🌦️"
    in 61..67, in 80..82 -> "🌧️"
    in 71..77, 85, 86 -> "❄️"
    in 95..99 -> "⛈️"
    else -> "🌡️"
}

/** WMO weather-code description in the requested language. */
fun weatherLabel(code: Int, language: AppLanguage): String {
    val (en, bn) = when (code) {
        0 -> "Clear sky" to "পরিষ্কার আকাশ"
        1 -> "Mostly clear" to "প্রায় পরিষ্কার"
        2 -> "Partly cloudy" to "আংশিক মেঘলা"
        3 -> "Overcast" to "মেঘাচ্ছন্ন"
        45, 48 -> "Foggy" to "কুয়াশা"
        in 51..57 -> "Drizzle" to "গুঁড়ি গুঁড়ি বৃষ্টি"
        in 61..65 -> "Rain" to "বৃষ্টি"
        66, 67 -> "Freezing rain" to "হিমশীতল বৃষ্টি"
        in 71..77, 85, 86 -> "Snow" to "তুষারপাত"
        in 80..82 -> "Rain showers" to "ঝেপে বৃষ্টি"
        95 -> "Thunderstorm" to "বজ্রসহ বৃষ্টি"
        96, 99 -> "Thunderstorm with hail" to "শিলাবৃষ্টিসহ বজ্রঝড়"
        else -> "Unknown" to "অজানা"
    }
    return trOf(language, en, bn)
}

/** Formats [epochSec] as wall-clock time at a location [offsetSeconds] from UTC (e.g. "h a" -> "4 PM"). */
fun formatLocalTime(epochSec: Long, offsetSeconds: Int, pattern: String): String {
    val format = SimpleDateFormat(pattern, Locale.ENGLISH).apply { timeZone = TimeZone.getTimeZone("GMT") }
    return format.format(java.util.Date((epochSec + offsetSeconds) * 1000L))
}

/** Hour-of-day (0-23) of [epochSec] at a location [offsetSeconds] from UTC. */
fun localHourOfDay(epochSec: Long, offsetSeconds: Int): Int =
    (((epochSec + offsetSeconds) / 3600) % 24).toInt().let { if (it < 0) it + 24 else it }

/** "4 PM" in English, "বিকাল ৪টা" in Bangla. */
fun formatHourLabel(epochSec: Long, offsetSeconds: Int, language: AppLanguage): String {
    val hour24 = localHourOfDay(epochSec, offsetSeconds)
    if (language == AppLanguage.EN) return formatLocalTime(epochSec, offsetSeconds, "h a")
    val period = when (hour24) {
        in 4..5 -> "ভোর"
        in 6..11 -> "সকাল"
        in 12..14 -> "দুপুর"
        in 15..16 -> "বিকাল"
        in 17..18 -> "সন্ধ্যা"
        else -> "রাত"
    }
    val hour12 = (hour24 % 12).let { if (it == 0) 12 else it }
    return "$period ${localizeDigits(hour12.toString(), language)}টা"
}

private val banglaWeekdayShortNames = listOf("রবি", "সোম", "মঙ্গল", "বুধ", "বৃহ", "শুক্র", "শনি")

/** Short weekday name ("Mon" / "সোম") of [epochSec] at a location [offsetSeconds] from UTC. */
fun formatWeekdayLabel(epochSec: Long, offsetSeconds: Int, language: AppLanguage): String {
    if (language == AppLanguage.EN) return formatLocalTime(epochSec, offsetSeconds, "EEE")
    // 1970-01-01 was a Thursday, so day 0 of the epoch maps to index 4 (Sunday = 0).
    val day = Math.floorDiv(epochSec + offsetSeconds, 86400L)
    return banglaWeekdayShortNames[Math.floorMod(day + 4, 7L).toInt()]
}
