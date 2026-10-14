package com.notes.digital.domain.usecase.note

import com.notes.digital.core.util.AppResult
import com.notes.digital.domain.model.Note
import com.notes.digital.domain.repository.NoteRepository

class SaveNoteUseCase(
    private val noteRepository: NoteRepository
) {
    suspend operator fun invoke(note: Note): AppResult<Long> {
        if (note.title.isBlank()) {
            return AppResult.Error("Judul tidak boleh kosong")
        }
        return try {
            val id = noteRepository.insertOrUpdateNote(
                note.copy(
                    title = note.title.trim(),
                    updatedAt = System.currentTimeMillis()
                )
            )
            AppResult.Success(id)
        } catch (e: Exception) {
            AppResult.Error(e.message ?: "Gagal menyimpan catatan", e)
        }
    }
}
