package com.notes.digital.feature.search

import app.cash.turbine.test
import com.notes.digital.domain.model.Book
import com.notes.digital.domain.model.Note
import com.notes.digital.domain.repository.BookRepository
import com.notes.digital.domain.repository.NoteRepository
import com.notes.digital.domain.usecase.note.SearchNotesUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GlobalSearchViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeNoteRepository: FakeNoteRepository
    private lateinit var fakeBookRepository: FakeBookRepository
    private lateinit var searchNotesUseCase: SearchNotesUseCase

    private class FakeNoteRepository(
        val notes: List<Note>
    ) : NoteRepository {
        var searchCallCount = 0
        var lastSearchQuery: String? = null

        override fun getNotesByBookDescending(bookId: Long): Flow<List<Note>> = emptyFlow()
        override fun getNotesByBookAscending(bookId: Long): Flow<List<Note>> = emptyFlow()
        override fun getNoteById(noteId: Long): Flow<Note?> = emptyFlow()

        override fun searchNotes(query: String): Flow<List<Note>> {
            searchCallCount++
            lastSearchQuery = query
            val filtered = notes.filter {
                it.title.contains(query, ignoreCase = true) || it.content.contains(query, ignoreCase = true)
            }
            return flowOf(filtered)
        }

        override suspend fun insertOrUpdateNote(note: Note): Long = 0L
        override suspend fun deleteNote(noteId: Long) {}
        override suspend fun updatePinStatus(noteId: Long, isPinned: Boolean) {}
    }

    private class FakeBookRepository(
        val books: List<Book>
    ) : BookRepository {
        override fun getAllBooks(): Flow<List<Book>> = flowOf(books)
        override fun getBookById(bookId: Long): Flow<Book?> = flowOf(books.find { it.id == bookId })
        override suspend fun insertOrUpdateBook(book: Book): Long = 0L
        override suspend fun deleteBook(bookId: Long) {}
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        val books = listOf(
            Book(id = 1L, title = "Kalkulus", colorHex = "#4CAF50"),
            Book(id = 2L, title = "Sains Komputer", colorHex = "#2196F3")
        )
        val notes = listOf(
            Note(id = 101L, bookId = 1L, title = "Turunan", content = "Persamaan garis singgung"),
            Note(id = 102L, bookId = 1L, title = "Integral", content = "Luas daerah"),
            Note(id = 201L, bookId = 2L, title = "Algoritma", content = "Sorting dan searching graf")
        )
        fakeNoteRepository = FakeNoteRepository(notes)
        fakeBookRepository = FakeBookRepository(books)
        searchNotesUseCase = SearchNotesUseCase(fakeNoteRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(): GlobalSearchViewModel {
        return GlobalSearchViewModel(
            searchNotesUseCase = searchNotesUseCase,
            bookRepository = fakeBookRepository
        )
    }

    @Test
    fun initialState_isEmptyAndNotSearching() = runTest(testDispatcher) {
        val viewModel = createViewModel()

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("", state.query)
            assertTrue(state.searchResults.isEmpty())
            assertFalse(state.isSearching)
            assertFalse(state.hasSearched)
        }
    }

    @Test
    fun searchNotes_debounces300ms_returnsMappedResults_TEST_007() = runTest(testDispatcher) {
        val viewModel = createViewModel()

        viewModel.uiState.test {
            awaitItem() // initial state

            // User types "persamaan"
            viewModel.onEvent(GlobalSearchUiEvent.OnQueryChanged("persamaan"))

            // Advance 299 ms - debounce should not trigger yet
            advanceTimeBy(299L)
            assertEquals(0, fakeNoteRepository.searchCallCount)

            // Advance past 300 ms threshold
            advanceTimeBy(50L)
            advanceUntilIdle()

            assertEquals(1, fakeNoteRepository.searchCallCount)
            assertEquals("persamaan", fakeNoteRepository.lastSearchQuery)

            val state = expectMostRecentItem()
            assertEquals("persamaan", state.query)
            assertEquals(1, state.searchResults.size)
            val result = state.searchResults[0]
            assertEquals(101L, result.note.id)
            assertEquals("Turunan", result.note.title)
            assertEquals("Kalkulus", result.bookTitle)
            assertEquals("#4CAF50", result.bookColorHex)
            assertFalse(state.isSearching)
            assertTrue(state.hasSearched)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun clearQuery_resetsResultsAndSearchState() = runTest(testDispatcher) {
        val viewModel = createViewModel()

        viewModel.uiState.test {
            awaitItem()

            viewModel.onEvent(GlobalSearchUiEvent.OnQueryChanged("integral"))
            advanceTimeBy(350L)
            advanceUntilIdle()

            val searchedState = expectMostRecentItem()
            assertEquals(1, searchedState.searchResults.size)
            assertTrue(searchedState.hasSearched)

            // Clear query
            viewModel.onEvent(GlobalSearchUiEvent.OnClearQuery)
            advanceUntilIdle()

            val clearedState = expectMostRecentItem()
            assertEquals("", clearedState.query)
            assertTrue(clearedState.searchResults.isEmpty())
            assertFalse(clearedState.hasSearched)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun searchNotes_noMatchingResults_producesEmptyResultsWithHasSearchedTrue() = runTest(testDispatcher) {
        val viewModel = createViewModel()

        viewModel.uiState.test {
            awaitItem()

            viewModel.onEvent(GlobalSearchUiEvent.OnQueryChanged("kata_tidak_ada"))
            advanceTimeBy(350L)
            advanceUntilIdle()

            val state = expectMostRecentItem()
            assertEquals("kata_tidak_ada", state.query)
            assertTrue(state.searchResults.isEmpty())
            assertTrue(state.hasSearched)
            assertFalse(state.isSearching)

            cancelAndIgnoreRemainingEvents()
        }
    }
}
