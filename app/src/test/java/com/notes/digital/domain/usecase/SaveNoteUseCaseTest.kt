package com.notes.digital.domain.usecase

import com.notes.digital.core.util.AppResult
import com.notes.digital.domain.model.Note
import com.notes.digital.domain.repository.NoteRepository
import com.notes.digital.domain.usecase.note.SaveNoteUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SaveNoteUseCaseTest {

    private class FakeNoteRepository : NoteRepository {
        var lastSavedNote: Note? = null

        override fun getNotesByBookDescending(bookId: Long): Flow<List<Note>> = emptyFlow()
        override fun getNotesByBookAscending(bookId: Long): Flow<List<Note>> = emptyFlow()
        override fun getNoteById(noteId: Long): Flow<Note?> = emptyFlow()
        override fun searchNotes(query: String): Flow<List<Note>> = emptyFlow()

        override suspend fun insertOrUpdateNote(note: Note): Long {
            lastSavedNote = note
            return 10L
        }

        override suspend fun deleteNote(noteId: Long) {}
        override suspend fun updatePinStatus(noteId: Long, isPinned: Boolean) {}
    }

    @Test
    fun saveNote_withBlankTitle_returnsError() = runTest {
        val repo = FakeNoteRepository()
        val useCase = SaveNoteUseCase(repo)

        val note = Note(bookId = 1L, title = "   ", content = "Materi")
        val result = useCase(note)

        assertTrue(result is AppResult.Error)
        assertEquals("Judul tidak boleh kosong", (result as AppResult.Error).message)
    }

    @Test
    fun saveNote_withValidTitle_trimsAndReturnsSuccess() = runTest {
        val repo = FakeNoteRepository()
        val useCase = SaveNoteUseCase(repo)

        val note = Note(bookId = 1L, title = "  Pertemuan 1  ", content = "Materi")
        val result = useCase(note)

        assertTrue(result is AppResult.Success)
        assertEquals(10L, (result as AppResult.Success).data)
        assertEquals("Pertemuan 1", repo.lastSavedNote?.title)
    }
}
