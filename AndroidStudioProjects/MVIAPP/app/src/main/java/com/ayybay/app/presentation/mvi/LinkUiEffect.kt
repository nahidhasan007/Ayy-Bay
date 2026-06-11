package com.ayybay.app.presentation.mvi

sealed class LinkUiEffect {
    data class ShowToast(val message: String) : LinkUiEffect()
    data class NavigateToList(val category: String) : LinkUiEffect()
    data class NavigateToDetail(val linkId: Long) : LinkUiEffect()
    object NavigateBack : LinkUiEffect()
    data class OpenUrl(val url: String) : LinkUiEffect()
}