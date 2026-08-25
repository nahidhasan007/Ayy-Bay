package com.ayybay.app.presentation.util

import java.util.Calendar
import java.util.Date

private val banglaDigits = charArrayOf('০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯')

private val banglaMonths = listOf(
    "জানুয়ারি", "ফেব্রুয়ারি", "মার্চ", "এপ্রিল", "মে", "জুন",
    "জুলাই", "আগস্ট", "সেপ্টেম্বর", "অক্টোবর", "নভেম্বর", "ডিসেম্বর"
)

private val banglaWeekdays = mapOf(
    Calendar.SATURDAY to "শনিবার",
    Calendar.SUNDAY to "রবিবার",
    Calendar.MONDAY to "সোমবার",
    Calendar.TUESDAY to "মঙ্গলবার",
    Calendar.WEDNESDAY to "বুধবার",
    Calendar.THURSDAY to "বৃহস্পতিবার",
    Calendar.FRIDAY to "শুক্রবার"
)

fun toBanglaNumber(number: Int): String =
    number.toString().map { c -> if (c.isDigit()) banglaDigits[c - '0'] else c }.joinToString("")

fun formatBanglaDate(date: Date): String {
    val cal = Calendar.getInstance().apply { time = date }
    val day = toBanglaNumber(cal.get(Calendar.DAY_OF_MONTH))
    val month = banglaMonths[cal.get(Calendar.MONTH)]
    val year = toBanglaNumber(cal.get(Calendar.YEAR))
    return "$day $month $year"
}

fun banglaWeekday(date: Date): String {
    val cal = Calendar.getInstance().apply { time = date }
    return banglaWeekdays[cal.get(Calendar.DAY_OF_WEEK)] ?: ""
}

private val banglaWeekdaysShort = mapOf(
    Calendar.SATURDAY to "শনি",
    Calendar.SUNDAY to "রবি",
    Calendar.MONDAY to "সোম",
    Calendar.TUESDAY to "মঙ্গল",
    Calendar.WEDNESDAY to "বুধ",
    Calendar.THURSDAY to "বৃহঃ",
    Calendar.FRIDAY to "শুক্র"
)

fun banglaWeekdayShort(date: Date): String {
    val cal = Calendar.getInstance().apply { time = date }
    return banglaWeekdaysShort[cal.get(Calendar.DAY_OF_WEEK)] ?: ""
}
