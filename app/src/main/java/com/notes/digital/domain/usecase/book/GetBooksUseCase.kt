package com.notes.digital.domain.usecase.book

import com.notes.digital.domain.model.Book
import com.notes.digital.domain.repository.BookRepository
import kotlinx.coroutines.flow.Flow

class GetBooksUseCase(
    private val bookRepository: BookRepository
) {
    operator fun invoke(): Flow<List<Book>> = bookRepository.getAllBooks()
}
