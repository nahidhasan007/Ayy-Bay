package com.ayybay.app

import androidx.lifecycle.ViewModel
import com.ayybay.app.presentation.mvi.TransactionUiEffect
import com.ayybay.app.presentation.mvi.TransactionUiIntent
import com.ayybay.app.presentation.mvi.TransactionUiState
import com.ayybay.app.presentation.viewmodel.TransactionViewModel
import kotlinx.coroutines.flow.StateFlow

class AyyBayViewModel(
    private val transactionViewModel: TransactionViewModel
) : ViewModel() {

    val uiState: StateFlow<TransactionUiState> = transactionViewModel.uiState

    fun handleIntent(intent: TransactionUiIntent) {
        transactionViewModel.handleIntent(intent)
    }
}