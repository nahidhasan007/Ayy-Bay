package com.ayybay.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ayybay.app.data.local.HealthPreferences
import com.ayybay.app.presentation.mvi.HealthUiState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HealthViewModel(
    private val healthPreferences: HealthPreferences
) : ViewModel() {

    val uiState: StateFlow<HealthUiState> = healthPreferences.profile
        .map { profile ->
            HealthUiState(
                dateOfBirth = profile.dateOfBirth,
                heightCm = profile.heightCm,
                weightKg = profile.weightKg
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HealthUiState())

    fun setDateOfBirth(millis: Long) {
        viewModelScope.launch { healthPreferences.setDateOfBirth(millis) }
    }

    fun setHeightCm(heightCm: Double) {
        viewModelScope.launch { healthPreferences.setHeightCm(heightCm) }
    }

    fun setWeightKg(weightKg: Double) {
        viewModelScope.launch { healthPreferences.setWeightKg(weightKg) }
    }
}
