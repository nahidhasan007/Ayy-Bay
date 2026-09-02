package com.ayybay.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ayybay.app.domain.usecase.GetNotificationsUseCase
import com.ayybay.app.domain.usecase.GetUnreadNotificationCountUseCase
import com.ayybay.app.domain.usecase.MarkAllNotificationsReadUseCase
import com.ayybay.app.domain.usecase.MarkNotificationReadUseCase
import com.ayybay.app.presentation.mvi.NotificationUiIntent
import com.ayybay.app.presentation.mvi.NotificationUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NotificationViewModel(
    private val getNotificationsUseCase: GetNotificationsUseCase,
    private val getUnreadNotificationCountUseCase: GetUnreadNotificationCountUseCase,
    private val markNotificationReadUseCase: MarkNotificationReadUseCase,
    private val markAllNotificationsReadUseCase: MarkAllNotificationsReadUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationUiState())
    val uiState: StateFlow<NotificationUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getNotificationsUseCase().collect { list ->
                _uiState.value = _uiState.value.copy(notifications = list)
            }
        }
        viewModelScope.launch {
            getUnreadNotificationCountUseCase().collect { count ->
                _uiState.value = _uiState.value.copy(unreadCount = count)
            }
        }
    }

    fun handleIntent(intent: NotificationUiIntent) {
        when (intent) {
            is NotificationUiIntent.MarkRead -> viewModelScope.launch { markNotificationReadUseCase(intent.id) }
            NotificationUiIntent.MarkAllRead -> viewModelScope.launch { markAllNotificationsReadUseCase() }
        }
    }
}
