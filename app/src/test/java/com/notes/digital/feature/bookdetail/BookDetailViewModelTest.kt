package com.notes.digital.feature.bookdetail

import app.cash.turbine.test
import com.notes.digital.domain.model.Book
import com.notes.digital.domain.model.Note
import com.notes.digital.domain.repository.BookRepository
import com.notes.digital.domain.repository.NoteRepository
import com.notes.digital.domain.usecase.note.DeleteNoteUseCase
import com.notes.digital.domain.usecase.note.GetNotesByBookUseCase
import com.notes.digital.domain.usecase.note.TogglePinNoteUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BookDetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeBookRepository: FakeBookRepository
    private lateinit var fakeNoteRepository: FakeNoteRepository
    private lateinit var viewModel: BookDetailViewModel

    private class FakeBookRepository : BookRepository {
        val booksMap = mutableMapOf<Long, Book>()
        val bookFlow = MutableStateFlow<Book?>(null)

        override fun getAllBooks(): Flow<List<Book>> = flowOf(booksMap.values.toList())

        override fun getBookById(bookId: Long): Flow<Book?> = bookFlow

        override suspend fun insertOrUpdateBook(book: Book): Long {
            booksMap[book.id] = book
            bookFlow.value = book
            return book.id
        }

        override suspend fun deleteBook(bookId: Long) {
            booksMap.remove(bookId)
            bookFlow.value = null
        }
    }

    private class FakeNoteRepository : NoteRepository {
        val notesMap = mutableMapOf<Long, Note>()
        val notesFlow = MutableStateFlow<List<Note>>(emptyList())
        var nextId = 1L

        private fun updateFlow() {
            notesFlow.value = notesMap.values.toList()
        }

        override fun getNotesByBookDescending(bookId: Long): Flow<List<Note>> {
            return notesFlow.map { list ->
                list.filter { it.bookId == bookId }
                    .sortedWith(
                        compareByDescending<Note> { it.isPinned }
                            .thenByDescending { it.updatedAt }
                    )
            }
        }

        override fun getNotesByBookAscending(bookId: Long): Flow<List<Note>> {
            return notesFlow.map { list ->
                list.filter { it.bookId == bookId }
                    .sortedWith(
                        compareByDescending<Note> { it.isPinned }
                            .thenBy { it.createdAt }
                    )
            }
        }

        override fun getNoteById(noteId: Long): Flow<Note?> = flowOf(notesMap[noteId])

        override fun searchNotes(query: String): Flow<List<Note>> {
            return notesFlow.map { list ->
                list.filter {
                    it.title.contains(query, ignoreCase = true) ||
                        it.content.contains(query, ignoreCase = true)
                }
            }
        }

        override suspend fun insertOrUpdateNote(note: Note): Long {
            val id = if (note.id == 0L) nextId++ else note.id
            val saved = note.copy(id = id)
            notesMap[id] = saved
            updateFlow()
            return id
        }

        override suspend fun deleteNote(noteId: Long) {
            notesMap.remove(noteId)
            updateFlow()
        }

        override suspend fun updatePinStatus(noteId: Long, isPinned: Boolean) {
            val note = notesMap[noteId] ?: return
            notesMap[noteId] = note.copy(isPinned = isPinned)
            updateFlow()
        }
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeBookRepository = FakeBookRepository()
        fakeNoteRepository = FakeNoteRepository()

        val sampleBook = Book(
            id = 1L,
            title = "Kalkulus Lanjut",
            colorHex = "#3F51B5"
        )
        fakeBookRepository.bookFlow.value = sampleBook
        fakeBookRepository.booksMap[1L] = sampleBook

        viewModel = BookDetailViewModel(
            bookId = 1L,
            bookRepository = fakeBookRepository,
            getNotesByBookUseCase = GetNotesByBookUseCase(fakeNoteRepository),
            togglePinNoteUseCase = TogglePinNoteUseCase(fakeNoteRepository),
            deleteNoteUseCase = DeleteNoteUseCase(fakeNoteRepository)
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialState_loadsBookAndNotes() = runTest(testDispatcher) {
        val note1 = Note(id = 10L, bookId = 1L, title = "Pertemuan 1", content = "Limit", isPinned = false, updatedAt = 1000L)
        val note2 = Note(id = 20L, bookId = 1L, title = "Silabus Penting", content = "Rumus", isPinned = true, updatedAt = 500L)
        fakeNoteRepository.notesMap[10L] = note1
        fakeNoteRepository.notesMap[20L] = note2
        fakeNoteRepository.notesFlow.value = listOf(note1, note2)

        viewModel.uiState.test {
            awaitItem() // initial or loading
            testDispatcher.scheduler.advanceUntilIdle()
            val state = expectMostRecentItem()
            assertFalse(state.isLoading)
            assertEquals("Kalkulus Lanjut", state.book?.title)
            assertEquals(2, state.notes.size)
            // Silabus Penting should be first because isPinned = true
            assertEquals("Silabus Penting", state.notes[0].title)
            assertTrue(state.notes[0].isPinned)
            assertEquals("Pertemuan 1", state.notes[1].title)
        }
    }

    @Test
    fun togglePinNote_invertsPinStatus() = runTest(testDispatcher) {
        val note = Note(id = 10L, bookId = 1L, title = "Pertemuan 1", content = "Limit", isPinned = false)
        fakeNoteRepository.notesMap[10L] = note
        fakeNoteRepository.notesFlow.value = listOf(note)

        viewModel.uiState.test {
            awaitItem()
            testDispatcher.scheduler.advanceUntilIdle()
            val state = expectMostRecentItem()
            assertFalse(state.notes[0].isPinned)

            viewModel.onEvent(BookDetailUiEvent.OnTogglePinNote(note))
            testDispatcher.scheduler.advanceUntilIdle()

            val updatedState = expectMostRecentItem()
            assertTrue(updatedState.notes[0].isPinned)
        }
    }

    @Test
    fun changeSortOrder_changesNotesOrdering() = runTest(testDispatcher) {
        val noteOld = Note(id = 1L, bookId = 1L, title = "Bab 1", content = "Intro", isPinned = false, createdAt = 100L, updatedAt = 200L)
        val noteNew = Note(id = 2L, bookId = 1L, title = "Bab 2", content = "Lanjut", isPinned = false, createdAt = 200L, updatedAt = 500L)
        fakeNoteRepository.notesMap[1L] = noteOld
        fakeNoteRepository.notesMap[2L] = noteNew
        fakeNoteRepository.notesFlow.value = listOf(noteOld, noteNew)

        viewModel.uiState.test {
            awaitItem()
            testDispatcher.scheduler.advanceUntilIdle()
            val stateDesc = expectMostRecentItem()
            // Default newest modified first: Bab 2 then Bab 1
            assertEquals("Bab 2", stateDesc.notes[0].title)
            assertEquals("Bab 1", stateDesc.notes[1].title)

            // Switch to oldest created
            viewModel.onEvent(BookDetailUiEvent.OnSortOrderChanged(NoteSortOrder.OLDEST_CREATED))
            testDispatcher.scheduler.advanceUntilIdle()

            val stateAsc = expectMostRecentItem()
            assertEquals("Bab 1", stateAsc.notes[0].title)
            assertEquals("Bab 2", stateAsc.notes[1].title)
        }
    }

    @Test
    fun searchNotes_filtersByQuery() = runTest(testDispatcher) {
        val note1 = Note(id = 1L, bookId = 1L, title = "Persamaan Linear", content = "Variabel x dan y")
        val note2 = Note(id = 2L, bookId = 1L, title = "Matriks Transformasi", content = "Determinan")
        fakeNoteRepository.notesMap[1L] = note1
        fakeNoteRepository.notesMap[2L] = note2
        fakeNoteRepository.notesFlow.value = listOf(note1, note2)

        viewModel.uiState.test {
            awaitItem()
            testDispatcher.scheduler.advanceUntilIdle()
            val state = expectMostRecentItem()
            assertEquals(2, state.notes.size)

            viewModel.onEvent(BookDetailUiEvent.OnSearchQueryChanged("Linear"))
            testDispatcher.scheduler.advanceUntilIdle()

            val filteredState = expectMostRecentItem()
            assertEquals(1, filteredState.notes.size)
            assertEquals("Persamaan Linear", filteredState.notes[0].title)

            viewModel.onEvent(BookDetailUiEvent.OnSearchQueryChanged("Determinan"))
            testDispatcher.scheduler.advanceUntilIdle()

            val contentFilteredState = expectMostRecentItem()
            assertEquals(1, contentFilteredState.notes.size)
            assertEquals("Matriks Transformasi", contentFilteredState.notes[0].title)
        }
    }

    @Test
    fun deleteNoteFlow_showsConfirmationAndDeletes() = runTest(testDispatcher) {
        val note = Note(id = 1L, bookId = 1L, title = "Catatan Uji", content = "Hapus saya")
        fakeNoteRepository.notesMap[1L] = note
        fakeNoteRepository.notesFlow.value = listOf(note)

        viewModel.uiState.test {
            awaitItem()
            testDispatcher.scheduler.advanceUntilIdle()
            assertNull(expectMostRecentItem().noteToDelete)

            viewModel.onEvent(BookDetailUiEvent.OnDeleteNoteClicked(note))
            testDispatcher.scheduler.advanceUntilIdle()

            val stateWithDialog = expectMostRecentItem()
            assertNotNull(stateWithDialog.noteToDelete)
            assertEquals(note.id, stateWithDialog.noteToDelete?.id)

            viewModel.onEvent(BookDetailUiEvent.OnConfirmDeleteNote)
            testDispatcher.scheduler.advanceUntilIdle()

            val stateDeleted = expectMostRecentItem()
            assertNull(stateDeleted.noteToDelete)
            assertTrue(stateDeleted.notes.isEmpty())
        }
    }
}
