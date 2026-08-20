package com.ayybay.app.presentation.mvi

import com.ayybay.app.domain.model.AuthUser

data class AuthUiState(
    val user: AuthUser? = null,
    val isCheckingSession: Boolean = true,
    val isSigningIn: Boolean = false,
    val error: String? = null
) {
    val isLoggedIn: Boolean get() = user != null
}
