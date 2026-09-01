package com.ayybay.app.presentation.mvi

import com.ayybay.app.domain.model.Alarm

sealed class AlarmUiIntent {
    data class SaveAlarm(val alarm: Alarm) : AlarmUiIntent()
    data class DeleteAlarm(val alarm: Alarm) : AlarmUiIntent()
    data class ToggleAlarm(val alarm: Alarm, val enabled: Boolean) : AlarmUiIntent()
    object ClearError : AlarmUiIntent()
}
