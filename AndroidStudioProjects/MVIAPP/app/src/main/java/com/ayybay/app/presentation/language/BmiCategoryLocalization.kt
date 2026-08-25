package com.ayybay.app.presentation.language

import androidx.compose.runtime.Composable
import com.ayybay.app.presentation.util.BmiCategory

fun BmiCategory.bnLabel(): String = when (this) {
    BmiCategory.UNDERWEIGHT -> "কম ওজন"
    BmiCategory.NORMAL -> "স্বাভাবিক"
    BmiCategory.OVERWEIGHT -> "অতিরিক্ত ওজন"
    BmiCategory.OBESE -> "স্থূল"
}

@Composable
fun BmiCategory.label(): String = tr(displayName, bnLabel())
