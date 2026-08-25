package com.ayybay.app.domain.usecase

import com.ayybay.app.domain.model.Note
import com.ayybay.app.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow

class GetAllNotesUseCase(
    private val noteRepository: NoteRepository
) {
    operator fun invoke(): Flow<List<Note>> = noteRepository.getAllNotes()
}
