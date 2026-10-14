package com.notes.digital.data.repository

import com.notes.digital.core.database.dao.NoteDao
import com.notes.digital.data.mapper.toDomain
import com.notes.digital.data.mapper.toEntity
import com.notes.digital.domain.model.Note
import com.notes.digital.domain.repository.NoteRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class NoteRepositoryImpl(
    private val noteDao: NoteDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : NoteRepository {

    override fun getNotesByBookDescending(bookId: Long): Flow<List<Note>> {
        return noteDao.getNotesByBookDescending(bookId)
            .map { list -> list.map { it.toDomain() } }
            .flowOn(ioDispatcher)
    }

    override fun getNotesByBookAscending(bookId: Long): Flow<List<Note>> {
        return noteDao.getNotesByBookAscending(bookId)
            .map { list -> list.map { it.toDomain() } }
            .flowOn(ioDispatcher)
    }

    override fun getNoteById(noteId: Long): Flow<Note?> {
        return noteDao.getNoteById(noteId)
            .map { it?.toDomain() }
            .flowOn(ioDispatcher)
    }

    override fun searchNotes(query: String): Flow<List<Note>> {
        return noteDao.searchNotesGlobal(query)
            .map { list -> list.map { it.toDomain() } }
            .flowOn(ioDispatcher)
    }

    override suspend fun insertOrUpdateNote(note: Note): Long {
        return withContext(ioDispatcher) {
            noteDao.insertOrUpdateNote(note.toEntity())
        }
    }

    override suspend fun deleteNote(noteId: Long) {
        withContext(ioDispatcher) {
            noteDao.deleteNoteById(noteId)
        }
    }

    override suspend fun updatePinStatus(noteId: Long, isPinned: Boolean) {
        withContext(ioDispatcher) {
            noteDao.updatePinStatus(noteId, isPinned)
        }
    }
}
