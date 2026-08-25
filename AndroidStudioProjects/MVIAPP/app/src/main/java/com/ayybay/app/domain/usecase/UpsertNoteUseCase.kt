package com.ayybay.app.domain.usecase

import com.ayybay.app.domain.model.Note
import com.ayybay.app.domain.repository.NoteRepository

class UpsertNoteUseCase(
    private val noteRepository: NoteRepository
) {
    suspend operator fun invoke(note: Note): Long = noteRepository.upsertNote(note)
}
