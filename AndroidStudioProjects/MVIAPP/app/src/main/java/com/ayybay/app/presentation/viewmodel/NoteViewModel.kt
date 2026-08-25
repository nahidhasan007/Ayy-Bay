package com.ayybay.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ayybay.app.domain.model.Note
import com.ayybay.app.domain.usecase.DeleteNoteUseCase
import com.ayybay.app.domain.usecase.GetAllNotesUseCase
import com.ayybay.app.domain.usecase.ToggleNotePinUseCase
import com.ayybay.app.domain.usecase.UpsertNoteUseCase
import com.ayybay.app.presentation.mvi.NoteUiEffect
import com.ayybay.app.presentation.mvi.NoteUiIntent
import com.ayybay.app.presentation.mvi.NoteUiState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class NoteViewModel(
    private val getAllNotesUseCase: GetAllNotesUseCase,
    private val upsertNoteUseCase: UpsertNoteUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase,
    private val toggleNotePinUseCase: ToggleNotePinUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(NoteUiState())
    val uiState: StateFlow<NoteUiState> = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<NoteUiEffect>()
    val uiEffect: SharedFlow<NoteUiEffect> = _uiEffect.asSharedFlow()

    init {
        loadNotes()
    }

    fun handleIntent(intent: NoteUiIntent) {
        when (intent) {
            is NoteUiIntent.Search -> _uiState.value = _uiState.value.copy(searchQuery = intent.query)
            is NoteUiIntent.SaveNote -> saveNote(intent.note)
            is NoteUiIntent.DeleteNote -> deleteNote(intent.note)
            is NoteUiIntent.TogglePin -> togglePin(intent.note)
            is NoteUiIntent.ClearError -> _uiState.value = _uiState.value.copy(error = null)
        }
    }

    private fun loadNotes() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            getAllNotesUseCase().catch { exception ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = exception.message ?: "Failed to load notes"
                )
            }.collect { notes ->
                _uiState.value = _uiState.value.copy(allNotes = notes, isLoading = false, error = null)
            }
        }
    }

    private fun saveNote(note: Note) {
        viewModelScope.launch {
            try {
                upsertNoteUseCase(note)
                _uiEffect.emit(NoteUiEffect.NavigateBack)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message ?: "Failed to save note")
            }
        }
    }

    private fun deleteNote(note: Note) {
        viewModelScope.launch {
            try {
                deleteNoteUseCase(note)
                _uiEffect.emit(NoteUiEffect.ShowToast("Note deleted"))
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message ?: "Failed to delete note")
            }
        }
    }

    private fun togglePin(note: Note) {
        viewModelScope.launch {
            try {
                toggleNotePinUseCase(note.id, !note.isPinned)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message ?: "Failed to update note")
            }
        }
    }
}
