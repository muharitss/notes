package com.notes.digital.feature.booklist

import com.notes.digital.domain.model.Book
import java.io.InputStream
import java.io.OutputStream

data class BookListUiState(
    val isLoading: Boolean = false,
    val books: List<Book> = emptyList(),
    val isGridLayout: Boolean = false,
    val isAddEditDialogOpen: Boolean = false,
    val isBackupRestoreDialogOpen: Boolean = false,
    val selectedBookForEdit: Book? = null,
    val bookToDelete: Book? = null,
    val userMessage: String? = null
)

sealed interface BookListUiEvent {
    data object OnAddBookClicked : BookListUiEvent
    data class OnEditBookClicked(val book: Book) : BookListUiEvent
    data object OnDismissAddEditDialog : BookListUiEvent
    data class OnSaveBook(
        val id: Long = 0L,
        val title: String,
        val description: String?,
        val colorHex: String
    ) : BookListUiEvent
    data class OnDeleteBookClicked(val book: Book) : BookListUiEvent
    data object OnConfirmDeleteBook : BookListUiEvent
    data object OnDismissDeleteDialog : BookListUiEvent
    data object OnToggleLayoutClicked : BookListUiEvent
    data object OnBackupRestoreClicked : BookListUiEvent
    data object OnDismissBackupRestoreDialog : BookListUiEvent
    data class OnExportBackup(val outputStream: OutputStream) : BookListUiEvent
    data class OnImportBackup(val inputStream: InputStream) : BookListUiEvent
    data object OnUserMessageDismissed : BookListUiEvent
}
