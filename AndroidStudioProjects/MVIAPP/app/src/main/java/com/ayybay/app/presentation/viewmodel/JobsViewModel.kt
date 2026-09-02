package com.ayybay.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ayybay.app.data.local.JobSampleData
import com.ayybay.app.domain.usecase.GetBookmarkedJobIdsUseCase
import com.ayybay.app.domain.usecase.ToggleJobBookmarkUseCase
import com.ayybay.app.presentation.mvi.JobUiIntent
import com.ayybay.app.presentation.mvi.JobUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class JobsViewModel(
    private val getBookmarkedJobIdsUseCase: GetBookmarkedJobIdsUseCase,
    private val toggleJobBookmarkUseCase: ToggleJobBookmarkUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(JobUiState(allJobs = JobSampleData.getSampleJobs()))
    val uiState: StateFlow<JobUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getBookmarkedJobIdsUseCase().collect { ids ->
                _uiState.value = _uiState.value.copy(bookmarkedIds = ids)
            }
        }
    }

    fun handleIntent(intent: JobUiIntent) {
        when (intent) {
            is JobUiIntent.Search -> _uiState.value = _uiState.value.copy(searchQuery = intent.query)
            is JobUiIntent.SelectTag -> _uiState.value = _uiState.value.copy(selectedTag = intent.tag)
            JobUiIntent.ToggleBookmarkedOnly -> _uiState.value = _uiState.value.copy(bookmarkedOnly = !_uiState.value.bookmarkedOnly)
            is JobUiIntent.ToggleBookmark -> toggleBookmark(intent.jobId)
        }
    }

    private fun toggleBookmark(jobId: Long) {
        viewModelScope.launch {
            val isBookmarked = _uiState.value.bookmarkedIds.contains(jobId)
            toggleJobBookmarkUseCase(jobId, !isBookmarked)
        }
    }
}
