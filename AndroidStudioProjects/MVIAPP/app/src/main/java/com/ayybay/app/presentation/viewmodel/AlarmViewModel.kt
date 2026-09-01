package com.ayybay.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ayybay.app.domain.model.Alarm
import com.ayybay.app.domain.usecase.DeleteAlarmUseCase
import com.ayybay.app.domain.usecase.GetAllAlarmsUseCase
import com.ayybay.app.domain.usecase.ToggleAlarmUseCase
import com.ayybay.app.domain.usecase.UpsertAlarmUseCase
import com.ayybay.app.presentation.mvi.AlarmUiEffect
import com.ayybay.app.presentation.mvi.AlarmUiIntent
import com.ayybay.app.presentation.mvi.AlarmUiState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AlarmViewModel(
    private val getAllAlarmsUseCase: GetAllAlarmsUseCase,
    private val upsertAlarmUseCase: UpsertAlarmUseCase,
    private val deleteAlarmUseCase: DeleteAlarmUseCase,
    private val toggleAlarmUseCase: ToggleAlarmUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AlarmUiState())
    val uiState: StateFlow<AlarmUiState> = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<AlarmUiEffect>()
    val uiEffect: SharedFlow<AlarmUiEffect> = _uiEffect.asSharedFlow()

    init {
        loadAlarms()
    }

    fun handleIntent(intent: AlarmUiIntent) {
        when (intent) {
            is AlarmUiIntent.SaveAlarm -> saveAlarm(intent.alarm)
            is AlarmUiIntent.DeleteAlarm -> deleteAlarm(intent.alarm)
            is AlarmUiIntent.ToggleAlarm -> toggleAlarm(intent.alarm, intent.enabled)
            is AlarmUiIntent.ClearError -> _uiState.value = _uiState.value.copy(error = null)
        }
    }

    private fun loadAlarms() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            getAllAlarmsUseCase().catch { exception ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = exception.message ?: "Failed to load alarms"
                )
            }.collect { alarms ->
                _uiState.value = _uiState.value.copy(alarms = alarms, isLoading = false, error = null)
            }
        }
    }

    private fun saveAlarm(alarm: Alarm) {
        viewModelScope.launch {
            try {
                upsertAlarmUseCase(alarm)
                _uiEffect.emit(AlarmUiEffect.NavigateBack)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message ?: "Failed to save alarm")
            }
        }
    }

    private fun deleteAlarm(alarm: Alarm) {
        viewModelScope.launch {
            try {
                deleteAlarmUseCase(alarm)
                _uiEffect.emit(AlarmUiEffect.ShowToast("Alarm deleted"))
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message ?: "Failed to delete alarm")
            }
        }
    }

    private fun toggleAlarm(alarm: Alarm, enabled: Boolean) {
        viewModelScope.launch {
            try {
                toggleAlarmUseCase(alarm.id, enabled)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message ?: "Failed to update alarm")
            }
        }
    }
}
