package com.notes.digital.domain.usecase.book

import com.notes.digital.core.util.AppResult
import com.notes.digital.domain.model.Book
import com.notes.digital.domain.repository.BookRepository

class SaveBookUseCase(
    private val bookRepository: BookRepository
) {
    suspend operator fun invoke(book: Book): AppResult<Long> {
        if (book.title.isBlank()) {
            return AppResult.Error("Judul tidak boleh kosong")
        }
        return try {
            val id = bookRepository.insertOrUpdateBook(
                book.copy(
                    title = book.title.trim(),
                    updatedAt = System.currentTimeMillis()
                )
            )
            AppResult.Success(id)
        } catch (e: Exception) {
            AppResult.Error(e.message ?: "Gagal menyimpan buku", e)
        }
    }
}
