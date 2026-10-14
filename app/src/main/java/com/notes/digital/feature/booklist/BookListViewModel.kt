package com.notes.digital.feature.booklist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.notes.digital.core.di.AppContainer
import com.notes.digital.core.util.AppResult
import com.notes.digital.domain.model.Book
import com.notes.digital.domain.usecase.book.DeleteBookUseCase
import com.notes.digital.domain.usecase.book.GetBooksUseCase
import com.notes.digital.domain.usecase.book.SaveBookUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BookListViewModel(
    private val getBooksUseCase: GetBooksUseCase,
    private val saveBookUseCase: SaveBookUseCase,
    private val deleteBookUseCase: DeleteBookUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(BookListUiState(isLoading = true))
    val uiState: StateFlow<BookListUiState> = _uiState.asStateFlow()

    init {
        observeBooks()
    }

    private fun observeBooks() {
        viewModelScope.launch {
            getBooksUseCase()
                .catch { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            userMessage = error.message ?: "Gagal memuat buku"
                        )
                    }
                }
                .collect { books ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            books = books
                        )
                    }
                }
        }
    }

    fun onEvent(event: BookListUiEvent) {
        when (event) {
            is BookListUiEvent.OnAddBookClicked -> {
                _uiState.update {
                    it.copy(isAddEditDialogOpen = true, selectedBookForEdit = null)
                }
            }
            is BookListUiEvent.OnEditBookClicked -> {
                _uiState.update {
                    it.copy(isAddEditDialogOpen = true, selectedBookForEdit = event.book)
                }
            }
            is BookListUiEvent.OnDismissAddEditDialog -> {
                _uiState.update {
                    it.copy(isAddEditDialogOpen = false, selectedBookForEdit = null)
                }
            }
            is BookListUiEvent.OnSaveBook -> {
                saveBook(event.id, event.title, event.description, event.colorHex)
            }
            is BookListUiEvent.OnDeleteBookClicked -> {
                _uiState.update {
                    it.copy(bookToDelete = event.book)
                }
            }
            is BookListUiEvent.OnConfirmDeleteBook -> {
                val book = _uiState.value.bookToDelete
                if (book != null) {
                    deleteBook(book.id)
                }
            }
            is BookListUiEvent.OnDismissDeleteDialog -> {
                _uiState.update {
                    it.copy(bookToDelete = null)
                }
            }
            is BookListUiEvent.OnToggleLayoutClicked -> {
                _uiState.update {
                    it.copy(isGridLayout = !it.isGridLayout)
                }
            }
            is BookListUiEvent.OnUserMessageDismissed -> {
                _uiState.update {
                    it.copy(userMessage = null)
                }
            }
        }
    }

    private fun saveBook(id: Long, title: String, description: String?, colorHex: String) {
        viewModelScope.launch {
            val book = Book(
                id = id,
                title = title,
                description = description,
                colorHex = colorHex
            )
            when (val result = saveBookUseCase(book)) {
                is AppResult.Success -> {
                    _uiState.update {
                        it.copy(isAddEditDialogOpen = false, selectedBookForEdit = null)
                    }
                }
                is AppResult.Error -> {
                    _uiState.update {
                        it.copy(userMessage = result.message)
                    }
                }
            }
        }
    }

    private fun deleteBook(bookId: Long) {
        viewModelScope.launch {
            deleteBookUseCase(bookId)
            _uiState.update {
                it.copy(bookToDelete = null)
            }
        }
    }

    companion object {
        fun provideFactory(container: AppContainer): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return BookListViewModel(
                        getBooksUseCase = GetBooksUseCase(container.bookRepository),
                        saveBookUseCase = SaveBookUseCase(container.bookRepository),
                        deleteBookUseCase = DeleteBookUseCase(container.bookRepository)
                    ) as T
                }
            }
    }
}
