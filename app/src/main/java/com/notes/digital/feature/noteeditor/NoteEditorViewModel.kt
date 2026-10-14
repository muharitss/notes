package com.notes.digital.feature.noteeditor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.notes.digital.core.di.AppContainer
import com.notes.digital.core.util.AppResult
import com.notes.digital.domain.model.Note
import com.notes.digital.domain.repository.NoteRepository
import com.notes.digital.domain.usecase.note.DeleteNoteUseCase
import com.notes.digital.domain.usecase.note.SaveNoteUseCase
import com.notes.digital.domain.usecase.note.TogglePinNoteUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class NoteEditorViewModel(
    val bookId: Long,
    val initialNoteId: Long = -1L,
    private val noteRepository: NoteRepository,
    private val saveNoteUseCase: SaveNoteUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase,
    private val togglePinNoteUseCase: TogglePinNoteUseCase,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        NoteEditorUiState(
            bookId = bookId,
            noteId = initialNoteId,
            isNewNote = initialNoteId == -1L,
            isLoading = initialNoteId != -1L
        )
    )
    val uiState: StateFlow<NoteEditorUiState> = _uiState.asStateFlow()

    private var debounceJob: Job? = null
    private val saveMutex = Mutex()

    private var lastSavedTitle: String = ""
    private var lastSavedContent: String = ""
    private var lastSavedPinned: Boolean = false

    init {
        if (initialNoteId != -1L) {
            viewModelScope.launch(ioDispatcher) {
                val existingNote = noteRepository.getNoteById(initialNoteId).firstOrNull()
                if (existingNote != null) {
                    lastSavedTitle = existingNote.title
                    lastSavedContent = existingNote.content
                    lastSavedPinned = existingNote.isPinned
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            noteId = existingNote.id,
                            title = existingNote.title,
                            content = existingNote.content,
                            isPinned = existingNote.isPinned,
                            isNewNote = false,
                            saveStatus = SaveStatus.SAVED,
                            lastSavedAt = existingNote.updatedAt
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            userMessage = "Catatan tidak ditemukan"
                        )
                    }
                }
            }
        }
    }

    private fun isDirty(): Boolean {
        val state = _uiState.value
        if (state.isNewNote && state.title.isBlank() && state.content.isBlank()) {
            return false
        }
        return state.title != lastSavedTitle ||
            state.content != lastSavedContent ||
            state.isPinned != lastSavedPinned
    }

    private fun scheduleAutoSave() {
        debounceJob?.cancel()
        debounceJob = viewModelScope.launch(ioDispatcher) {
            delay(500L)
            performSave()
        }
    }

    suspend fun performSave() = saveMutex.withLock {
        val state = _uiState.value
        if (!isDirty() || state.isDeleted) return@withLock

        val noteIdToUse = if (state.noteId == -1L) 0L else state.noteId
        val titleToSave = state.title.ifBlank { "Catatan Baru" }

        _uiState.update { it.copy(saveStatus = SaveStatus.SAVING) }

        val note = Note(
            id = noteIdToUse,
            bookId = bookId,
            title = titleToSave,
            content = state.content,
            isPinned = state.isPinned
        )

        when (val result = saveNoteUseCase(note)) {
            is AppResult.Success -> {
                val savedId = result.data
                lastSavedTitle = state.title
                lastSavedContent = state.content
                lastSavedPinned = state.isPinned
                _uiState.update {
                    it.copy(
                        noteId = savedId,
                        isNewNote = false,
                        saveStatus = SaveStatus.SAVED,
                        lastSavedAt = System.currentTimeMillis()
                    )
                }
            }
            is AppResult.Error -> {
                _uiState.update {
                    it.copy(
                        saveStatus = SaveStatus.IDLE,
                        userMessage = result.message
                    )
                }
            }
        }
    }

    fun flushSave(): Job {
        debounceJob?.cancel()
        return viewModelScope.launch(ioDispatcher) {
            performSave()
        }
    }

    fun onEvent(event: NoteEditorUiEvent) {
        when (event) {
            is NoteEditorUiEvent.OnTitleChanged -> {
                _uiState.update { it.copy(title = event.title) }
                if (isDirty()) {
                    _uiState.update { it.copy(saveStatus = SaveStatus.SAVING) }
                    scheduleAutoSave()
                }
            }
            is NoteEditorUiEvent.OnContentChanged -> {
                _uiState.update { it.copy(content = event.content) }
                if (isDirty()) {
                    _uiState.update { it.copy(saveStatus = SaveStatus.SAVING) }
                    scheduleAutoSave()
                }
            }
            is NoteEditorUiEvent.OnFlushSaveRequested -> {
                flushSave()
            }
            is NoteEditorUiEvent.OnTogglePinRequested -> {
                val currentPin = _uiState.value.isPinned
                val newPin = !currentPin
                _uiState.update { it.copy(isPinned = newPin) }
                val currentId = _uiState.value.noteId
                if (currentId != -1L) {
                    viewModelScope.launch(ioDispatcher) {
                        try {
                            togglePinNoteUseCase(currentId, currentPin)
                            lastSavedPinned = newPin
                        } catch (e: Exception) {
                            _uiState.update {
                                it.copy(userMessage = e.message ?: "Gagal mengubah status pin")
                            }
                        }
                    }
                } else {
                    if (isDirty()) {
                        scheduleAutoSave()
                    }
                }
            }
            is NoteEditorUiEvent.OnDeleteNoteRequested -> {
                _uiState.update { it.copy(showDeleteDialog = true) }
            }
            is NoteEditorUiEvent.OnDismissDeleteDialog -> {
                _uiState.update { it.copy(showDeleteDialog = false) }
            }
            is NoteEditorUiEvent.OnConfirmDeleteNote -> {
                val noteId = _uiState.value.noteId
                _uiState.update { it.copy(showDeleteDialog = false, isDeleted = true) }
                if (noteId != -1L) {
                    viewModelScope.launch(ioDispatcher) {
                        try {
                            deleteNoteUseCase(noteId)
                        } catch (e: Exception) {
                            _uiState.update {
                                it.copy(userMessage = e.message ?: "Gagal menghapus catatan")
                            }
                        }
                    }
                }
            }
            is NoteEditorUiEvent.OnUserMessageDismissed -> {
                _uiState.update { it.copy(userMessage = null) }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        // shortcut: flush to IO if still dirty during ViewModel termination, prevents edge data loss
        if (isDirty() && !_uiState.value.isDeleted) {
            runBlocking(ioDispatcher) {
                performSave()
            }
        }
    }

    companion object {
        fun provideFactory(
            container: AppContainer,
            bookId: Long,
            noteId: Long
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return NoteEditorViewModel(
                    bookId = bookId,
                    initialNoteId = noteId,
                    noteRepository = container.noteRepository,
                    saveNoteUseCase = SaveNoteUseCase(container.noteRepository),
                    deleteNoteUseCase = DeleteNoteUseCase(container.noteRepository),
                    togglePinNoteUseCase = TogglePinNoteUseCase(container.noteRepository)
                ) as T
            }
        }
    }
}
