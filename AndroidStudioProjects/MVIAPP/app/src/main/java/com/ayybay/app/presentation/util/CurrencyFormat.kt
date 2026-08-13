package com.ayybay.app.presentation.util

import java.util.Locale
import kotlin.math.abs
import kotlin.math.roundToLong

fun formatTaka(amount: Double, withSign: Boolean = false): String {
    val rounded = abs(amount).roundToLong()
    val formatted = String.format(Locale.US, "%,d", rounded)
    val sign = if (withSign) (if (amount < 0) "-" else "+") else ""
    return "$sign৳ $formatted"
}
