package com.notes.digital.domain.usecase.note

import com.notes.digital.domain.repository.NoteRepository

class DeleteNoteUseCase(
    private val noteRepository: NoteRepository
) {
    suspend operator fun invoke(noteId: Long) {
        noteRepository.deleteNote(noteId)
    }
}
