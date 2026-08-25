package com.ayybay.app.presentation.util

import androidx.compose.ui.graphics.Color
import com.ayybay.app.ui.theme.BalanceOrange
import com.ayybay.app.ui.theme.ExpenseRed
import com.ayybay.app.ui.theme.IncomeGreen
import com.ayybay.app.ui.theme.InfoBlue

enum class BmiCategory(val displayName: String, val color: Color) {
    UNDERWEIGHT("Underweight", InfoBlue),
    NORMAL("Normal", IncomeGreen),
    OVERWEIGHT("Overweight", BalanceOrange),
    OBESE("Obese", ExpenseRed)
}

fun bmiOf(heightCm: Double, weightKg: Double): Double {
    val heightM = heightCm / 100.0
    if (heightM <= 0.0) return 0.0
    return weightKg / (heightM * heightM)
}

fun categoryOf(bmi: Double): BmiCategory = when {
    bmi < 18.5 -> BmiCategory.UNDERWEIGHT
    bmi < 25.0 -> BmiCategory.NORMAL
    bmi < 30.0 -> BmiCategory.OVERWEIGHT
    else -> BmiCategory.OBESE
}

/** Healthy-BMI (18.5–24.9) weight range for a given height. */
fun healthyWeightRange(heightCm: Double): ClosedFloatingPointRange<Double> {
    val heightM = heightCm / 100.0
    val min = 18.5 * heightM * heightM
    val max = 24.9 * heightM * heightM
    return min..max
}
