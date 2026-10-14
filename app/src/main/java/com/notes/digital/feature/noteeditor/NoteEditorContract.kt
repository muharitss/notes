package com.notes.digital.feature.noteeditor

enum class SaveStatus {
    IDLE,
    SAVING,
    SAVED
}

data class NoteEditorUiState(
    val bookId: Long = 0L,
    val noteId: Long = -1L,
    val title: String = "",
    val content: String = "",
    val isPinned: Boolean = false,
    val saveStatus: SaveStatus = SaveStatus.IDLE,
    val lastSavedAt: Long = 0L,
    val isNewNote: Boolean = true,
    val isLoading: Boolean = false,
    val showDeleteDialog: Boolean = false,
    val isDeleted: Boolean = false,
    val userMessage: String? = null
)

sealed interface NoteEditorUiEvent {
    data class OnTitleChanged(val title: String) : NoteEditorUiEvent
    data class OnContentChanged(val content: String) : NoteEditorUiEvent
    data object OnFlushSaveRequested : NoteEditorUiEvent
    data object OnTogglePinRequested : NoteEditorUiEvent
    data object OnDeleteNoteRequested : NoteEditorUiEvent
    data object OnConfirmDeleteNote : NoteEditorUiEvent
    data object OnDismissDeleteDialog : NoteEditorUiEvent
    data object OnUserMessageDismissed : NoteEditorUiEvent
}
