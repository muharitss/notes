package com.notes.digital.domain.usecase.note

import com.notes.digital.domain.repository.NoteRepository

class TogglePinNoteUseCase(
    private val noteRepository: NoteRepository
) {
    suspend operator fun invoke(noteId: Long, currentPinStatus: Boolean) {
        noteRepository.updatePinStatus(noteId, !currentPinStatus)
    }
}
