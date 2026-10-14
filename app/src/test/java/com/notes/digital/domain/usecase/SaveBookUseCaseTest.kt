package com.notes.digital.domain.usecase

import com.notes.digital.core.util.AppResult
import com.notes.digital.domain.model.Book
import com.notes.digital.domain.repository.BookRepository
import com.notes.digital.domain.usecase.book.SaveBookUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SaveBookUseCaseTest {

    private class FakeBookRepository : BookRepository {
        var lastSavedBook: Book? = null

        override fun getAllBooks(): Flow<List<Book>> = emptyFlow()
        override fun getBookById(bookId: Long): Flow<Book?> = emptyFlow()

        override suspend fun insertOrUpdateBook(book: Book): Long {
            lastSavedBook = book
            return 1L
        }

        override suspend fun deleteBook(bookId: Long) {}
    }

    @Test
    fun saveBook_withEmptyTitle_returnsError() = runTest {
        val repo = FakeBookRepository()
        val useCase = SaveBookUseCase(repo)

        val book = Book(title = "", colorHex = "#FFFFFF")
        val result = useCase(book)

        assertTrue(result is AppResult.Error)
        assertEquals("Judul tidak boleh kosong", (result as AppResult.Error).message)
    }

    @Test
    fun saveBook_withWhitespaceTitle_returnsError() = runTest {
        val repo = FakeBookRepository()
        val useCase = SaveBookUseCase(repo)

        val book = Book(title = "    ", colorHex = "#FFFFFF")
        val result = useCase(book)

        assertTrue(result is AppResult.Error)
        assertEquals("Judul tidak boleh kosong", (result as AppResult.Error).message)
    }

    @Test
    fun saveBook_withValidTitle_trimsAndReturnsSuccess() = runTest {
        val repo = FakeBookRepository()
        val useCase = SaveBookUseCase(repo)

        val book = Book(title = "  Catatan Matematika  ", colorHex = "#FF0000")
        val result = useCase(book)

        assertTrue(result is AppResult.Success)
        assertEquals(1L, (result as AppResult.Success).data)
        assertEquals("Catatan Matematika", repo.lastSavedBook?.title)
    }
}
