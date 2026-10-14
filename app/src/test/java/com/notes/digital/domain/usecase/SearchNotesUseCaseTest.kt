package com.notes.digital.domain.usecase

import com.notes.digital.domain.model.Note
import com.notes.digital.domain.repository.NoteRepository
import com.notes.digital.domain.usecase.note.SearchNotesUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SearchNotesUseCaseTest {

    private class FakeNoteRepository(
        private val notes: List<Note>
    ) : NoteRepository {
        var queriedKeyword: String? = null

        override fun getNotesByBookDescending(bookId: Long): Flow<List<Note>> = emptyFlow()
        override fun getNotesByBookAscending(bookId: Long): Flow<List<Note>> = emptyFlow()
        override fun getNoteById(noteId: Long): Flow<Note?> = emptyFlow()

        override fun searchNotes(query: String): Flow<List<Note>> {
            queriedKeyword = query
            val filtered = notes.filter {
                it.title.contains(query, ignoreCase = true) || it.content.contains(query, ignoreCase = true)
            }
            return flowOf(filtered)
        }

        override suspend fun insertOrUpdateNote(note: Note): Long = 0L
        override suspend fun deleteNote(noteId: Long) {}
        override suspend fun updatePinStatus(noteId: Long, isPinned: Boolean) {}
    }

    @Test
    fun searchNotes_withBlankQuery_returnsEmptyFlowDirectly() = runTest {
        val repo = FakeNoteRepository(emptyList())
        val useCase = SearchNotesUseCase(repo)

        val result = useCase("   ").first()
        assertTrue(result.isEmpty())
        assertEquals(null, repo.queriedKeyword)
    }

    @Test
    fun searchNotes_filtersByTitleOrContent_TEST_007() = runTest {
        val mathNote = Note(id = 1L, bookId = 1L, title = "Matematika", content = "Persamaan kuadrat")
        val englishNote = Note(id = 2L, bookId = 1L, title = "Bahasa Inggris", content = "Vocabulary")
        val repo = FakeNoteRepository(listOf(mathNote, englishNote))
        val useCase = SearchNotesUseCase(repo)

        val result = useCase("persamaan").first()
        assertEquals(1, result.size)
        assertEquals("Matematika", result[0].title)
        assertEquals("persamaan", repo.queriedKeyword)
    }
}
