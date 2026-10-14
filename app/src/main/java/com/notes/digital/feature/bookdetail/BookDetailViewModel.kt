package com.notes.digital.feature.bookdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.notes.digital.core.di.AppContainer
import com.notes.digital.domain.model.Note
import com.notes.digital.domain.repository.BookRepository
import com.notes.digital.domain.usecase.note.DeleteNoteUseCase
import com.notes.digital.domain.usecase.note.GetNotesByBookUseCase
import com.notes.digital.domain.usecase.note.TogglePinNoteUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class BookDetailViewModel(
    val bookId: Long,
    private val bookRepository: BookRepository,
    private val getNotesByBookUseCase: GetNotesByBookUseCase,
    private val togglePinNoteUseCase: TogglePinNoteUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase
) : ViewModel() {

    private data class InternalState(
        val sortOrder: NoteSortOrder = NoteSortOrder.NEWEST_MODIFIED,
        val searchQuery: String = "",
        val isSearchActive: Boolean = false,
        val noteToDelete: Note? = null,
        val userMessage: String? = null
    )

    private val _internalState = MutableStateFlow(InternalState())

    private val _notesFlow = _internalState
        .map { it.sortOrder }
        .distinctUntilChanged()
        .flatMapLatest { sortOrder ->
            getNotesByBookUseCase(
                bookId = bookId,
                isAscending = sortOrder == NoteSortOrder.OLDEST_CREATED
            )
        }
        .catch { error ->
            _internalState.update {
                it.copy(userMessage = error.message ?: "Gagal memuat catatan")
            }
        }

    val uiState: StateFlow<BookDetailUiState> = combine(
        bookRepository.getBookById(bookId).catch { error ->
            _internalState.update {
                it.copy(userMessage = error.message ?: "Gagal memuat informasi buku")
            }
        },
        _notesFlow,
        _internalState
    ) { book, notes, internal ->
        val query = internal.searchQuery.trim()
        val filteredNotes = if (query.isEmpty()) {
            notes
        } else {
            notes.filter { note ->
                note.title.contains(query, ignoreCase = true) ||
                    note.content.contains(query, ignoreCase = true)
            }
        }
        BookDetailUiState(
            isLoading = false,
            book = book,
            notes = filteredNotes,
            sortOrder = internal.sortOrder,
            searchQuery = internal.searchQuery,
            isSearchActive = internal.isSearchActive,
            noteToDelete = internal.noteToDelete,
            userMessage = internal.userMessage
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = BookDetailUiState(isLoading = true)
    )

    fun onEvent(event: BookDetailUiEvent) {
        when (event) {
            is BookDetailUiEvent.OnTogglePinNote -> {
                viewModelScope.launch {
                    try {
                        togglePinNoteUseCase(event.note.id, event.note.isPinned)
                    } catch (e: Exception) {
                        _internalState.update {
                            it.copy(userMessage = e.message ?: "Gagal mengubah status pin catatan")
                        }
                    }
                }
            }
            is BookDetailUiEvent.OnSortOrderChanged -> {
                _internalState.update { it.copy(sortOrder = event.sortOrder) }
            }
            is BookDetailUiEvent.OnSearchQueryChanged -> {
                _internalState.update { it.copy(searchQuery = event.query) }
            }
            is BookDetailUiEvent.OnToggleSearchActive -> {
                _internalState.update {
                    val nextActive = !it.isSearchActive
                    it.copy(
                        isSearchActive = nextActive,
                        searchQuery = if (!nextActive) "" else it.searchQuery
                    )
                }
            }
            is BookDetailUiEvent.OnDeleteNoteClicked -> {
                _internalState.update { it.copy(noteToDelete = event.note) }
            }
            is BookDetailUiEvent.OnConfirmDeleteNote -> {
                val note = _internalState.value.noteToDelete
                if (note != null) {
                    viewModelScope.launch {
                        try {
                            deleteNoteUseCase(note.id)
                            _internalState.update { it.copy(noteToDelete = null) }
                        } catch (e: Exception) {
                            _internalState.update {
                                it.copy(userMessage = e.message ?: "Gagal menghapus catatan")
                            }
                        }
                    }
                }
            }
            is BookDetailUiEvent.OnDismissDeleteDialog -> {
                _internalState.update { it.copy(noteToDelete = null) }
            }
            is BookDetailUiEvent.OnUserMessageDismissed -> {
                _internalState.update { it.copy(userMessage = null) }
            }
        }
    }

    companion object {
        fun provideFactory(container: AppContainer, bookId: Long): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return BookDetailViewModel(
                        bookId = bookId,
                        bookRepository = container.bookRepository,
                        getNotesByBookUseCase = GetNotesByBookUseCase(container.noteRepository),
                        togglePinNoteUseCase = TogglePinNoteUseCase(container.noteRepository),
                        deleteNoteUseCase = DeleteNoteUseCase(container.noteRepository)
                    ) as T
                }
            }
    }
}
