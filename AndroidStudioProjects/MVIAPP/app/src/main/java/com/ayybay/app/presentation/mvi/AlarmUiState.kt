package com.ayybay.app.presentation.mvi

import com.ayybay.app.domain.model.Alarm

data class AlarmUiState(
    val alarms: List<Alarm> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
