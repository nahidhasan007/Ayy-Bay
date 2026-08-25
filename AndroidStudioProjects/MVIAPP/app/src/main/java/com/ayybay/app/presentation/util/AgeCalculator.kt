package com.ayybay.app.presentation.util

import java.util.Calendar

data class AgeBreakdown(
    val years: Int,
    val months: Int,
    val days: Int,
    val totalDays: Long,
    val nextBirthdayInDays: Long
)

/** Pure calendar-field arithmetic — no locale/timezone dependence beyond [Calendar]'s default. */
fun calculateAge(dobMillis: Long, nowMillis: Long): AgeBreakdown {
    val dob = Calendar.getInstance().apply { timeInMillis = dobMillis }
    val now = Calendar.getInstance().apply { timeInMillis = nowMillis }

    var years = now.get(Calendar.YEAR) - dob.get(Calendar.YEAR)
    var months = now.get(Calendar.MONTH) - dob.get(Calendar.MONTH)
    var days = now.get(Calendar.DAY_OF_MONTH) - dob.get(Calendar.DAY_OF_MONTH)

    if (days < 0) {
        months -= 1
        val prevMonth = Calendar.getInstance().apply {
            timeInMillis = nowMillis
            add(Calendar.MONTH, -1)
        }
        days += prevMonth.getActualMaximum(Calendar.DAY_OF_MONTH)
    }
    if (months < 0) {
        years -= 1
        months += 12
    }

    val totalDays = (nowMillis - dobMillis) / (24 * 60 * 60 * 1000L)

    val nextBirthday = Calendar.getInstance().apply {
        timeInMillis = nowMillis
        set(Calendar.MONTH, dob.get(Calendar.MONTH))
        set(Calendar.DAY_OF_MONTH, dob.get(Calendar.DAY_OF_MONTH))
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
        if (before(now) || timeInMillis == now.timeInMillis) {
            add(Calendar.YEAR, 1)
        }
    }
    val nextBirthdayInDays = (nextBirthday.timeInMillis - nowMillis) / (24 * 60 * 60 * 1000L)

    return AgeBreakdown(
        years = years,
        months = months,
        days = days,
        totalDays = totalDays,
        nextBirthdayInDays = nextBirthdayInDays
    )
}
