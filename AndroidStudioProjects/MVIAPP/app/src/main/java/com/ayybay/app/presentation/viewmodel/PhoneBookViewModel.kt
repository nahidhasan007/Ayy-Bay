package com.ayybay.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ayybay.app.domain.usecase.GetContactsUseCase
import com.ayybay.app.presentation.mvi.PhoneBookUiIntent
import com.ayybay.app.presentation.mvi.PhoneBookUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PhoneBookViewModel(
    private val getContactsUseCase: GetContactsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PhoneBookUiState())
    val uiState: StateFlow<PhoneBookUiState> = _uiState.asStateFlow()

    fun handleIntent(intent: PhoneBookUiIntent) {
        when (intent) {
            is PhoneBookUiIntent.Search -> _uiState.value = _uiState.value.copy(searchQuery = intent.query)
            is PhoneBookUiIntent.PermissionResult -> onPermissionResult(intent.granted)
            is PhoneBookUiIntent.ClearError -> _uiState.value = _uiState.value.copy(error = null)
        }
    }

    private fun onPermissionResult(granted: Boolean) {
        _uiState.value = _uiState.value.copy(hasPermission = granted)
        if (granted && _uiState.value.allContacts.isEmpty()) loadContacts()
    }

    private fun loadContacts() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val contacts = getContactsUseCase()
                _uiState.value = _uiState.value.copy(allContacts = contacts, isLoading = false, error = null)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message ?: "Failed to load contacts")
            }
        }
    }
}
