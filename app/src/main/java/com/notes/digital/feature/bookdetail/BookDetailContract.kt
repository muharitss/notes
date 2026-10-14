package com.notes.digital.feature.bookdetail

import com.notes.digital.domain.model.Book
import com.notes.digital.domain.model.Note

enum class NoteSortOrder {
    NEWEST_MODIFIED,
    OLDEST_CREATED
}

data class BookDetailUiState(
    val isLoading: Boolean = false,
    val book: Book? = null,
    val notes: List<Note> = emptyList(),
    val sortOrder: NoteSortOrder = NoteSortOrder.NEWEST_MODIFIED,
    val searchQuery: String = "",
    val isSearchActive: Boolean = false,
    val noteToDelete: Note? = null,
    val userMessage: String? = null
)

sealed interface BookDetailUiEvent {
    data class OnTogglePinNote(val note: Note) : BookDetailUiEvent
    data class OnSortOrderChanged(val sortOrder: NoteSortOrder) : BookDetailUiEvent
    data class OnSearchQueryChanged(val query: String) : BookDetailUiEvent
    data object OnToggleSearchActive : BookDetailUiEvent
    data class OnDeleteNoteClicked(val note: Note) : BookDetailUiEvent
    data object OnConfirmDeleteNote : BookDetailUiEvent
    data object OnDismissDeleteDialog : BookDetailUiEvent
    data object OnUserMessageDismissed : BookDetailUiEvent
}
