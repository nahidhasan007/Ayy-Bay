package com.ayybay.app.presentation.mvi

import com.ayybay.app.domain.model.Note

sealed class NoteUiIntent {
    data class Search(val query: String) : NoteUiIntent()
    data class SaveNote(val note: Note) : NoteUiIntent()
    data class DeleteNote(val note: Note) : NoteUiIntent()
    data class TogglePin(val note: Note) : NoteUiIntent()
    object ClearError : NoteUiIntent()
}
