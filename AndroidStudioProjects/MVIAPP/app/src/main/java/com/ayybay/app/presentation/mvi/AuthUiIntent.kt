package com.ayybay.app.presentation.mvi

import android.content.Context

sealed class AuthUiIntent {
    data class SignInWithGoogle(val context: Context) : AuthUiIntent()
    object ContinueAsGuest : AuthUiIntent()
    object SignOut : AuthUiIntent()
    object DeleteAccount : AuthUiIntent()
    object ClearError : AuthUiIntent()
}
