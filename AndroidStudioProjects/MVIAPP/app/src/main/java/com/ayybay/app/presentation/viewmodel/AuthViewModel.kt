package com.ayybay.app.presentation.viewmodel

import android.content.Context
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ayybay.app.domain.usecase.ObserveAuthUserUseCase
import com.ayybay.app.domain.usecase.SignInWithGoogleUseCase
import com.ayybay.app.domain.usecase.SignOutUseCase
import com.ayybay.app.presentation.mvi.AuthUiEffect
import com.ayybay.app.presentation.mvi.AuthUiIntent
import com.ayybay.app.presentation.mvi.AuthUiState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val observeAuthUserUseCase: ObserveAuthUserUseCase,
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase,
    private val signOutUseCase: SignOutUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<AuthUiEffect>()
    val uiEffect: SharedFlow<AuthUiEffect> = _uiEffect.asSharedFlow()

    init {
        viewModelScope.launch {
            observeAuthUserUseCase().collect { user ->
                _uiState.value = _uiState.value.copy(user = user, isCheckingSession = false)
            }
        }
    }

    fun handleIntent(intent: AuthUiIntent) {
        when (intent) {
            is AuthUiIntent.SignInWithGoogle -> signIn(intent.context)
            is AuthUiIntent.SignOut -> signOut()
            is AuthUiIntent.ClearError -> _uiState.value = _uiState.value.copy(error = null)
        }
    }

    private fun signIn(context: Context) {
        if (_uiState.value.isSigningIn) return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSigningIn = true, error = null)
            signInWithGoogleUseCase(context)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(isSigningIn = false)
                    _uiEffect.emit(AuthUiEffect.NavigateToHome)
                }
                .onFailure { e ->
                    val message = if (e is GetCredentialCancellationException) {
                        null
                    } else {
                        e.message ?: "Google sign-in failed. Please try again."
                    }
                    _uiState.value = _uiState.value.copy(isSigningIn = false, error = message)
                }
        }
    }

    private fun signOut() {
        viewModelScope.launch {
            signOutUseCase()
            _uiEffect.emit(AuthUiEffect.NavigateToLogin)
        }
    }
}
