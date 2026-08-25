package com.ayybay.app.domain.usecase

import com.ayybay.app.domain.repository.NoteRepository

class ToggleNotePinUseCase(
    private val noteRepository: NoteRepository
) {
    suspend operator fun invoke(id: Long, pinned: Boolean) = noteRepository.setPinned(id, pinned)
}
