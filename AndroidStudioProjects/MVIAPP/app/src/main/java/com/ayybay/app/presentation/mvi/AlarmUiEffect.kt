package com.ayybay.app.presentation.mvi

sealed class AlarmUiEffect {
    data class ShowToast(val message: String) : AlarmUiEffect()
    object NavigateBack : AlarmUiEffect()
}
