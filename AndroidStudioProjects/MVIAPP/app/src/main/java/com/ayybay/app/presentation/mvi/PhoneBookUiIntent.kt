package com.ayybay.app.presentation.mvi

sealed class PhoneBookUiIntent {
    data class Search(val query: String) : PhoneBookUiIntent()
    data class PermissionResult(val granted: Boolean) : PhoneBookUiIntent()
    object ClearError : PhoneBookUiIntent()
}
