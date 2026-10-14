package com.notes.digital.feature.booklist

import app.cash.turbine.test
import com.notes.digital.domain.model.Book
import com.notes.digital.domain.repository.BookRepository
import com.notes.digital.domain.usecase.book.DeleteBookUseCase
import com.notes.digital.domain.usecase.book.GetBooksUseCase
import com.notes.digital.domain.usecase.book.SaveBookUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
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
class BookListViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeRepository: FakeBookRepository
    private lateinit var viewModel: BookListViewModel

    private class FakeBookRepository : BookRepository {
        val booksFlow = MutableStateFlow<List<Book>>(emptyList())
        val booksMap = mutableMapOf<Long, Book>()
        var nextId = 1L

        override fun getAllBooks(): Flow<List<Book>> = booksFlow

        override fun getBookById(bookId: Long): Flow<Book?> = flowOf(booksMap[bookId])

        override suspend fun insertOrUpdateBook(book: Book): Long {
            val id = if (book.id == 0L) nextId++ else book.id
            val saved = book.copy(id = id)
            booksMap[id] = saved
            booksFlow.value = booksMap.values.toList()
            return id
        }

        override suspend fun deleteBook(bookId: Long) {
            booksMap.remove(bookId)
            booksFlow.value = booksMap.values.toList()
        }
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeBookRepository()
        viewModel = BookListViewModel(
            getBooksUseCase = GetBooksUseCase(fakeRepository),
            saveBookUseCase = SaveBookUseCase(fakeRepository),
            deleteBookUseCase = DeleteBookUseCase(fakeRepository)
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialState_loadsBooksAndDisablesLoading() = runTest(testDispatcher) {
        val initialBooks = listOf(
            Book(id = 1L, title = "Buku Matematika", colorHex = "#3F51B5", noteCount = 3),
            Book(id = 2L, title = "Buku Sejarah", colorHex = "#F57C00", noteCount = 0)
        )
        fakeRepository.booksFlow.value = initialBooks

        viewModel.uiState.test {
            val state = awaitItem()
            testDispatcher.scheduler.advanceUntilIdle()
            val loadedState = expectMostRecentItem()
            assertFalse(loadedState.isLoading)
            assertEquals(2, loadedState.books.size)
            assertEquals("Buku Matematika", loadedState.books[0].title)
            assertEquals(3, loadedState.books[0].noteCount)
        }
    }

    @Test
    fun onAddBookClicked_opensDialogForNewBook() = runTest(testDispatcher) {
        viewModel.onEvent(BookListUiEvent.OnAddBookClicked)

        val state = viewModel.uiState.value
        assertTrue(state.isAddEditDialogOpen)
        assertNull(state.selectedBookForEdit)
    }

    @Test
    fun onEditBookClicked_opensDialogWithExistingBook() = runTest(testDispatcher) {
        val book = Book(id = 5L, title = "Fisika Modern", colorHex = "#2E7D32")
        viewModel.onEvent(BookListUiEvent.OnEditBookClicked(book))

        val state = viewModel.uiState.value
        assertTrue(state.isAddEditDialogOpen)
        assertEquals(book, state.selectedBookForEdit)
    }

    @Test
    fun onDismissAddEditDialog_closesDialog() = runTest(testDispatcher) {
        viewModel.onEvent(BookListUiEvent.OnAddBookClicked)
        assertTrue(viewModel.uiState.value.isAddEditDialogOpen)

        viewModel.onEvent(BookListUiEvent.OnDismissAddEditDialog)
        assertFalse(viewModel.uiState.value.isAddEditDialogOpen)
        assertNull(viewModel.uiState.value.selectedBookForEdit)
    }

    @Test
    fun onSaveBook_validInput_savesBookAndClosesDialog() = runTest(testDispatcher) {
        viewModel.onEvent(BookListUiEvent.OnAddBookClicked)
        viewModel.onEvent(
            BookListUiEvent.OnSaveBook(
                id = 0L,
                title = "Biologi Sel",
                description = "Catatan bab 1-5",
                colorHex = "#2E7D32"
            )
        )
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isAddEditDialogOpen)
        assertEquals(1, fakeRepository.booksMap.size)
        assertEquals("Biologi Sel", fakeRepository.booksMap[1L]?.title)
    }

    @Test
    fun onSaveBook_blankTitle_showsErrorMessage() = runTest(testDispatcher) {
        viewModel.onEvent(BookListUiEvent.OnAddBookClicked)
        viewModel.onEvent(
            BookListUiEvent.OnSaveBook(
                id = 0L,
                title = "   ",
                description = null,
                colorHex = "#3F51B5"
            )
        )
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNotNull(state.userMessage)
        assertEquals("Judul tidak boleh kosong", state.userMessage)
    }

    @Test
    fun onDeleteBook_andConfirm_deletesFromRepository() = runTest(testDispatcher) {
        val book = Book(id = 10L, title = "Kimia Organik", colorHex = "#C2185B")
        fakeRepository.booksMap[10L] = book
        fakeRepository.booksFlow.value = listOf(book)

        viewModel.onEvent(BookListUiEvent.OnDeleteBookClicked(book))
        assertEquals(book, viewModel.uiState.value.bookToDelete)

        viewModel.onEvent(BookListUiEvent.OnConfirmDeleteBook)
        testDispatcher.scheduler.advanceUntilIdle()

        assertNull(viewModel.uiState.value.bookToDelete)
        assertNull(fakeRepository.booksMap[10L])
    }

    @Test
    fun onToggleLayoutClicked_togglesGridLayout() = runTest(testDispatcher) {
        assertFalse(viewModel.uiState.value.isGridLayout)

        viewModel.onEvent(BookListUiEvent.OnToggleLayoutClicked)
        assertTrue(viewModel.uiState.value.isGridLayout)

        viewModel.onEvent(BookListUiEvent.OnToggleLayoutClicked)
        assertFalse(viewModel.uiState.value.isGridLayout)
    }

    @Test
    fun onUserMessageDismissed_clearsMessage() = runTest(testDispatcher) {
        viewModel.onEvent(
            BookListUiEvent.OnSaveBook(
                id = 0L,
                title = "",
                description = null,
                colorHex = "#3F51B5"
            )
        )
        testDispatcher.scheduler.advanceUntilIdle()
        assertNotNull(viewModel.uiState.value.userMessage)

        viewModel.onEvent(BookListUiEvent.OnUserMessageDismissed)
        assertNull(viewModel.uiState.value.userMessage)
    }

    @Test
    fun onBackupRestoreClicked_opensBackupDialog() = runTest(testDispatcher) {
        assertFalse(viewModel.uiState.value.isBackupRestoreDialogOpen)

        viewModel.onEvent(BookListUiEvent.OnBackupRestoreClicked)
        assertTrue(viewModel.uiState.value.isBackupRestoreDialogOpen)

        viewModel.onEvent(BookListUiEvent.OnDismissBackupRestoreDialog)
        assertFalse(viewModel.uiState.value.isBackupRestoreDialogOpen)
    }
}
