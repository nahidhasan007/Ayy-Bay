package com.ayybay.app.util

import java.util.Calendar
import java.util.Date

/**
 * Epoch millis for the start of [this]'s calendar day, used as a stable day key so that
 * a prayer-time row written earlier in the day (with its own precise [Date]) can still be
 * looked up later the same day, when `Date()` no longer matches to the millisecond.
 */
fun Date.startOfDayMillis(): Long =
    Calendar.getInstance().apply {
        time = this@startOfDayMillis
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
