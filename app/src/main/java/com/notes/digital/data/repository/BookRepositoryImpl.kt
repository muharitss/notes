package com.notes.digital.data.repository

import com.notes.digital.core.database.dao.BookDao
import com.notes.digital.data.mapper.toDomain
import com.notes.digital.data.mapper.toEntity
import com.notes.digital.domain.model.Book
import com.notes.digital.domain.repository.BookRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class BookRepositoryImpl(
    private val bookDao: BookDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : BookRepository {

    override fun getAllBooks(): Flow<List<Book>> {
        return bookDao.getAllBooksWithCount()
            .map { list -> list.map { it.toDomain() } }
            .flowOn(ioDispatcher)
    }

    override fun getBookById(bookId: Long): Flow<Book?> {
        return bookDao.getBookById(bookId)
            .map { it?.toDomain() }
            .flowOn(ioDispatcher)
    }

    override suspend fun insertOrUpdateBook(book: Book): Long {
        return withContext(ioDispatcher) {
            bookDao.insertOrUpdateBook(book.toEntity())
        }
    }

    override suspend fun deleteBook(bookId: Long) {
        withContext(ioDispatcher) {
            bookDao.deleteBookById(bookId)
        }
    }
}
