package com.ayybay.app.presentation.mvi

sealed class TransactionUiEffect {
    data class ShowToast(val message: String) : TransactionUiEffect()
    data class NavigateToDetail(val transactionId: Long) : TransactionUiEffect()
    object NavigateToAdd : TransactionUiEffect()
}