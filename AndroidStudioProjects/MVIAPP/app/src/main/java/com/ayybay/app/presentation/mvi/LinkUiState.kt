package com.ayybay.app.presentation.mvi

import com.ayybay.app.domain.model.DailyLink
import com.ayybay.app.domain.model.LinkCategory

data class LinkUiState(
    val categories: List<LinkCategoryItem> = emptyList(),
    val links: List<DailyLink> = emptyList(),
    val selectedLink: DailyLink? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

data class LinkCategoryItem(
    val category: LinkCategory,
    val count: Int
)