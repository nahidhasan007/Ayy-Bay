package com.ayybay.app.presentation.mvi

import com.ayybay.app.domain.model.DailyLink

sealed class LinkUiIntent {
    object LoadCategories : LinkUiIntent()
    data class LoadLinksByCategory(val category: String) : LinkUiIntent()
    data class LoadLinkDetail(val id: Long) : LinkUiIntent()
    data class AddLink(val link: DailyLink) : LinkUiIntent()
    data class DeleteLink(val link: DailyLink) : LinkUiIntent()
    object ClearError : LinkUiIntent()
}