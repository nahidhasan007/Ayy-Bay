package com.ayybay.app.presentation.util

import java.util.Locale
import kotlin.math.abs
import kotlin.math.roundToLong

fun formatTaka(amount: Double, withSign: Boolean = false): String {
    if (amount.isNaN()) return "৳ 0"
    val rounded = abs(amount).roundToLong()
    val formatted = String.format(Locale.US, "%,d", rounded)
    val sign = if (withSign) (if (amount < 0) "-" else "+") else ""
    return "$sign৳ $formatted"
}
