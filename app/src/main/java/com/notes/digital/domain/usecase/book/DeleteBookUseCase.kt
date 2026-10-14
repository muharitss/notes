package com.notes.digital.domain.usecase.book

import com.notes.digital.domain.repository.BookRepository

class DeleteBookUseCase(
    private val bookRepository: BookRepository
) {
    suspend operator fun invoke(bookId: Long) {
        bookRepository.deleteBook(bookId)
    }
}
