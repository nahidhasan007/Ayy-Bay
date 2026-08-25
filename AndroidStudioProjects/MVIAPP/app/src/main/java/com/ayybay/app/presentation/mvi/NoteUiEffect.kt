package com.ayybay.app.presentation.mvi

sealed class NoteUiEffect {
    data class ShowToast(val message: String) : NoteUiEffect()
    object NavigateBack : NoteUiEffect()
}
