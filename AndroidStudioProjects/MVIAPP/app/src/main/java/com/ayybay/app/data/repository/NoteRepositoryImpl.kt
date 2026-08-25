package com.ayybay.app.data.repository

import com.ayybay.app.data.local.NoteDao
import com.ayybay.app.data.local.entity.NoteEntity
import com.ayybay.app.domain.model.Note
import com.ayybay.app.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NoteRepositoryImpl(
    private val noteDao: NoteDao
) : NoteRepository {

    override fun getAllNotes(): Flow<List<Note>> =
        noteDao.getAllNotes().map { it.map { e -> e.toDomain() } }

    override fun getNoteById(id: Long): Flow<Note?> =
        noteDao.getNoteById(id).map { it?.toDomain() }

    override suspend fun upsertNote(note: Note): Long {
        val now = System.currentTimeMillis()
        val entity = note.toEntity().copy(
            createdAt = if (note.id == 0L) now else note.createdAt,
            updatedAt = now
        )
        return noteDao.upsertNote(entity)
    }

    override suspend fun deleteNote(note: Note) =
        noteDao.deleteNote(note.toEntity())

    override suspend fun setPinned(id: Long, pinned: Boolean) =
        noteDao.setPinned(id, pinned)

    private fun NoteEntity.toDomain() = Note(
        id = id,
        title = title,
        body = body,
        isPinned = isPinned,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

    private fun Note.toEntity() = NoteEntity(
        id = id,
        title = title,
        body = body,
        isPinned = isPinned,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
