package com.ayybay.app.presentation.mvi

import com.ayybay.app.domain.model.Note

data class NoteUiState(
    val allNotes: List<Note> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
) {
    val visibleNotes: List<Note>
        get() = if (searchQuery.isBlank()) {
            allNotes
        } else {
            allNotes.filter {
                it.title.contains(searchQuery, ignoreCase = true) ||
                    it.body.contains(searchQuery, ignoreCase = true)
            }
        }
}
