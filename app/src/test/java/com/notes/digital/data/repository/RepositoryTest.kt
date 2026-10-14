package com.notes.digital.data.repository

import app.cash.turbine.test
import com.notes.digital.core.database.dao.BookDao
import com.notes.digital.core.database.dao.BookWithCountTuple
import com.notes.digital.core.database.dao.NoteDao
import com.notes.digital.core.database.entity.BookEntity
import com.notes.digital.core.database.entity.NoteEntity
import com.notes.digital.domain.model.Book
import com.notes.digital.domain.model.Note
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RepositoryTest {

    private val testDispatcher = StandardTestDispatcher()

    private class FakeBookDao : BookDao {
        val booksFlow = MutableStateFlow<List<BookWithCountTuple>>(emptyList())
        val singleBookFlow = MutableStateFlow<BookEntity?>(null)
        val booksMap = mutableMapOf<Long, BookEntity>()
        var nextId = 1L

        override fun getAllBooksWithCount(): Flow<List<BookWithCountTuple>> = booksFlow

        override fun getBookById(bookId: Long): Flow<BookEntity?> = singleBookFlow

        override suspend fun insertOrUpdateBook(book: BookEntity): Long {
            val id = if (book.id == 0L) nextId++ else book.id
            val saved = book.copy(id = id)
            booksMap[id] = saved
            return id
        }

        override suspend fun deleteBookById(bookId: Long) {
            booksMap.remove(bookId)
        }
    }

    private class FakeNoteDao : NoteDao {
        val notesDescendingFlow = MutableStateFlow<List<NoteEntity>>(emptyList())
        val notesAscendingFlow = MutableStateFlow<List<NoteEntity>>(emptyList())
        val singleNoteFlow = MutableStateFlow<NoteEntity?>(null)
        val searchFlow = MutableStateFlow<List<NoteEntity>>(emptyList())
        val notesMap = mutableMapOf<Long, NoteEntity>()
        var nextId = 1L

        override fun getNotesByBookDescending(bookId: Long): Flow<List<NoteEntity>> = notesDescendingFlow

        override fun getNotesByBookAscending(bookId: Long): Flow<List<NoteEntity>> = notesAscendingFlow

        override fun getNoteById(noteId: Long): Flow<NoteEntity?> = singleNoteFlow

        override fun searchNotesGlobal(query: String): Flow<List<NoteEntity>> = searchFlow

        override suspend fun insertOrUpdateNote(note: NoteEntity): Long {
            val id = if (note.id == 0L) nextId++ else note.id
            val saved = note.copy(id = id)
            notesMap[id] = saved
            return id
        }

        override suspend fun deleteNoteById(noteId: Long) {
            notesMap.remove(noteId)
        }

        override suspend fun updatePinStatus(noteId: Long, isPinned: Boolean) {
            notesMap[noteId]?.let {
                notesMap[noteId] = it.copy(isPinned = isPinned)
            }
        }
    }

    @Test
    fun bookRepository_getAllBooks_emitsMappedBooks() = runTest(testDispatcher) {
        val fakeDao = FakeBookDao()
        val repository = BookRepositoryImpl(fakeDao, testDispatcher)

        val entity = BookEntity(id = 1L, title = "Buku A", colorHex = "#FFFFFF", createdAt = 100L, updatedAt = 200L)
        fakeDao.booksFlow.value = listOf(BookWithCountTuple(entity, noteCount = 3))

        repository.getAllBooks().test {
            val list = awaitItem()
            assertEquals(1, list.size)
            assertEquals("Buku A", list[0].title)
            assertEquals(3, list[0].noteCount)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun bookRepository_getBookById_emitsMappedBookOrNull() = runTest(testDispatcher) {
        val fakeDao = FakeBookDao()
        val repository = BookRepositoryImpl(fakeDao, testDispatcher)

        val entity = BookEntity(id = 1L, title = "Buku A", colorHex = "#FFFFFF", createdAt = 100L, updatedAt = 200L)
        fakeDao.singleBookFlow.value = entity

        repository.getBookById(1L).test {
            val book = awaitItem()
            assertEquals("Buku A", book?.title)
            cancelAndIgnoreRemainingEvents()
        }

        fakeDao.singleBookFlow.value = null
        repository.getBookById(2L).test {
            assertNull(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun bookRepository_insertAndDelete_delegatesToDao() = runTest(testDispatcher) {
        val fakeDao = FakeBookDao()
        val repository = BookRepositoryImpl(fakeDao, testDispatcher)

        val book = Book(title = "Buku Baru", colorHex = "#123456")
        val id = repository.insertOrUpdateBook(book)
        assertEquals(1L, id)
        assertEquals("Buku Baru", fakeDao.booksMap[1L]?.title)

        repository.deleteBook(1L)
        assertNull(fakeDao.booksMap[1L])
    }

    @Test
    fun noteRepository_queriesAndMutations_delegateCorrectly() = runTest(testDispatcher) {
        val fakeDao = FakeNoteDao()
        val repository = NoteRepositoryImpl(fakeDao, testDispatcher)

        val note = Note(bookId = 1L, title = "Catatan 1", content = "Isi")
        val id = repository.insertOrUpdateNote(note)
        assertEquals(1L, id)
        assertEquals("Catatan 1", fakeDao.notesMap[1L]?.title)

        repository.updatePinStatus(1L, true)
        assertEquals(true, fakeDao.notesMap[1L]?.isPinned)

        repository.deleteNote(1L)
        assertNull(fakeDao.notesMap[1L])
    }

    @Test
    fun noteRepository_flows_emitMappedNotes() = runTest(testDispatcher) {
        val fakeDao = FakeNoteDao()
        val repository = NoteRepositoryImpl(fakeDao, testDispatcher)

        val noteEntity = NoteEntity(id = 1L, bookId = 1L, title = "N1", content = "C1", createdAt = 10L, updatedAt = 20L)
        fakeDao.notesDescendingFlow.value = listOf(noteEntity)
        fakeDao.notesAscendingFlow.value = listOf(noteEntity)
        fakeDao.singleNoteFlow.value = noteEntity
        fakeDao.searchFlow.value = listOf(noteEntity)

        repository.getNotesByBookDescending(1L).test {
            assertEquals("N1", awaitItem()[0].title)
            cancelAndIgnoreRemainingEvents()
        }

        repository.getNotesByBookAscending(1L).test {
            assertEquals("N1", awaitItem()[0].title)
            cancelAndIgnoreRemainingEvents()
        }

        repository.getNoteById(1L).test {
            assertEquals("N1", awaitItem()?.title)
            cancelAndIgnoreRemainingEvents()
        }

        repository.searchNotes("N1").test {
            assertEquals("N1", awaitItem()[0].title)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
