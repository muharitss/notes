package com.notes.digital.domain.usecase.note

import com.notes.digital.domain.model.Note
import com.notes.digital.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class SearchNotesUseCase(
    private val noteRepository: NoteRepository
) {
    operator fun invoke(query: String): Flow<List<Note>> {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) {
            return flowOf(emptyList())
        }
        return noteRepository.searchNotes(trimmed)
    }
}
