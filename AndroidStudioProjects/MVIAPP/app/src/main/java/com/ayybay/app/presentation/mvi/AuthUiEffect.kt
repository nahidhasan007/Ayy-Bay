package com.ayybay.app.presentation.mvi

sealed class AuthUiEffect {
    object NavigateToHome : AuthUiEffect()
    object NavigateToLogin : AuthUiEffect()
}
