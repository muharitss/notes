package com.notes.digital.domain.repository

import com.notes.digital.domain.model.Book
import kotlinx.coroutines.flow.Flow

interface BookRepository {
    fun getAllBooks(): Flow<List<Book>>
    fun getBookById(bookId: Long): Flow<Book?>
    suspend fun insertOrUpdateBook(book: Book): Long
    suspend fun deleteBook(bookId: Long)
}
