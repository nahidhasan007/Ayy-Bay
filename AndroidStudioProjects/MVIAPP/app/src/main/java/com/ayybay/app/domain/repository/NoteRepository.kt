package com.ayybay.app.domain.repository

import com.ayybay.app.domain.model.Note
import kotlinx.coroutines.flow.Flow

interface NoteRepository {

    fun getAllNotes(): Flow<List<Note>>

    fun getNoteById(id: Long): Flow<Note?>

    suspend fun upsertNote(note: Note): Long

    suspend fun deleteNote(note: Note)

    suspend fun setPinned(id: Long, pinned: Boolean)
}
