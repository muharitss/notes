package com.notes.digital.domain.repository

import com.notes.digital.domain.model.Note
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    fun getNotesByBookDescending(bookId: Long): Flow<List<Note>>
    fun getNotesByBookAscending(bookId: Long): Flow<List<Note>>
    fun getNoteById(noteId: Long): Flow<Note?>
    fun searchNotes(query: String): Flow<List<Note>>
    suspend fun insertOrUpdateNote(note: Note): Long
    suspend fun deleteNote(noteId: Long)
    suspend fun updatePinStatus(noteId: Long, isPinned: Boolean)
}
