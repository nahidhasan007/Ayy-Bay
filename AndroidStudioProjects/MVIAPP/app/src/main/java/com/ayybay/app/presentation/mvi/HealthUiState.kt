package com.ayybay.app.presentation.mvi

data class HealthUiState(
    val dateOfBirth: Long? = null,
    val heightCm: Double? = null,
    val weightKg: Double? = null
)
