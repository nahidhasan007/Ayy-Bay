package com.ayybay.app.presentation.mvi

import com.ayybay.app.domain.model.JobTag

sealed class JobUiIntent {
    data class Search(val query: String) : JobUiIntent()
    data class SelectTag(val tag: JobTag?) : JobUiIntent()
    object ToggleBookmarkedOnly : JobUiIntent()
    data class ToggleBookmark(val jobId: Long) : JobUiIntent()
}
