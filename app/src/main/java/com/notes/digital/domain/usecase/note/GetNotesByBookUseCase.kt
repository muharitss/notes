package com.notes.digital.domain.usecase.note

import com.notes.digital.domain.model.Note
import com.notes.digital.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow

class GetNotesByBookUseCase(
    private val noteRepository: NoteRepository
) {
    operator fun invoke(bookId: Long, isAscending: Boolean = false): Flow<List<Note>> {
        return if (isAscending) {
            noteRepository.getNotesByBookAscending(bookId)
        } else {
            noteRepository.getNotesByBookDescending(bookId)
        }
    }
}
