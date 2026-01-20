package com.ayybay.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ayybay.app.domain.usecase.*
import com.ayybay.app.presentation.mvi.TransactionUiEffect
import com.ayybay.app.presentation.mvi.TransactionUiIntent
import com.ayybay.app.presentation.mvi.TransactionUiState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class TransactionViewModel(
    private val getAllTransactionsUseCase: GetAllTransactionsUseCase,
    private val addTransactionUseCase: AddTransactionUseCase,
    private val updateTransactionUseCase: UpdateTransactionUseCase,
    private val deleteTransactionUseCase: DeleteTransactionUseCase,
    private val getMonthlySummaryUseCase: GetMonthlySummaryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(TransactionUiState())
    val uiState: StateFlow<TransactionUiState> = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<TransactionUiEffect>()
    val uiEffect: SharedFlow<TransactionUiEffect> = _uiEffect.asSharedFlow()

    init {
        handleIntent(TransactionUiIntent.LoadTransactions)
    }

    fun handleIntent(intent: TransactionUiIntent) {
        when (intent) {
            is TransactionUiIntent.LoadTransactions -> loadTransactions()
            is TransactionUiIntent.AddTransaction -> addTransaction(intent.transaction)
            is TransactionUiIntent.UpdateTransaction -> updateTransaction(intent.transaction)
            is TransactionUiIntent.DeleteTransaction -> deleteTransaction(intent.transaction)
            is TransactionUiIntent.ClearError -> clearError()
            is TransactionUiIntent.FilterByMonth -> filterByMonth(intent.month, intent.year)
            else -> {}
        }
    }

    private fun loadTransactions() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            getAllTransactionsUseCase().catch { exception ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = exception.message ?: "Unknown error occurred"
                )
            }.collect { transactions ->
                _uiState.value = _uiState.value.copy(
                    transactions = transactions,
                    isLoading = false,
                    error = null
                )
            }
        }
    }

    private fun addTransaction(transaction: com.ayybay.app.domain.model.Transaction) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                addTransactionUseCase(transaction)
                _uiEffect.emit(TransactionUiEffect.ShowToast("Transaction added successfully"))
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to add transaction"
                )
            }
        }
    }

    private fun updateTransaction(transaction: com.ayybay.app.domain.model.Transaction) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                updateTransactionUseCase(transaction)
                _uiEffect.emit(TransactionUiEffect.ShowToast("Transaction updated successfully"))
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to update transaction"
                )
            }
        }
    }

    private fun deleteTransaction(transaction: com.ayybay.app.domain.model.Transaction) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                deleteTransactionUseCase(transaction)
                _uiEffect.emit(TransactionUiEffect.ShowToast("Transaction deleted successfully"))
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to delete transaction"
                )
            }
        }
    }

    private fun filterByMonth(month: Int, year: Int) {
        _uiState.value = _uiState.value.copy(selectedMonth = month, selectedYear = year)
        loadTransactions()
    }

    private fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}