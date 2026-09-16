package com.ayybay.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ayybay.app.data.local.QuranSurahData
import com.ayybay.app.domain.usecase.CancelQuranReadingPlanUseCase
import com.ayybay.app.domain.usecase.GetQuranReadingPlanStatusUseCase
import com.ayybay.app.domain.usecase.StartQuranReadingPlanUseCase
import com.ayybay.app.domain.usecase.UpdateReadingPlanReminderTimeUseCase
import com.ayybay.app.presentation.mvi.QuranPlanUiIntent
import com.ayybay.app.presentation.mvi.QuranPlanUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class QuranPlanViewModel(
    private val getQuranReadingPlanStatusUseCase: GetQuranReadingPlanStatusUseCase,
    private val startQuranReadingPlanUseCase: StartQuranReadingPlanUseCase,
    private val cancelQuranReadingPlanUseCase: CancelQuranReadingPlanUseCase,
    private val updateReadingPlanReminderTimeUseCase: UpdateReadingPlanReminderTimeUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuranPlanUiState())
    val uiState: StateFlow<QuranPlanUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getQuranReadingPlanStatusUseCase(QuranSurahData.surahs().size)
                .catch { }
                .collect { status ->
                    _uiState.value = QuranPlanUiState(status = status, isLoading = false)
                }
        }
    }

    fun handleIntent(intent: QuranPlanUiIntent) {
        when (intent) {
            is QuranPlanUiIntent.StartPlan -> viewModelScope.launch {
                startQuranReadingPlanUseCase(intent.durationDays, intent.reminderHour, intent.reminderMinute)
            }
            is QuranPlanUiIntent.UpdateReminderTime -> viewModelScope.launch {
                updateReadingPlanReminderTimeUseCase(intent.hour, intent.minute)
            }
            QuranPlanUiIntent.CancelPlan -> viewModelScope.launch {
                cancelQuranReadingPlanUseCase()
            }
        }
    }
}
