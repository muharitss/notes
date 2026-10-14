package com.notes.digital.data.mapper

import com.notes.digital.core.database.entity.NoteEntity
import com.notes.digital.domain.model.Note

fun NoteEntity.toDomain(): Note = Note(
    id = id,
    bookId = bookId,
    title = title,
    content = content,
    isPinned = isPinned,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun Note.toEntity(): NoteEntity = NoteEntity(
    id = id,
    bookId = bookId,
    title = title,
    content = content,
    isPinned = isPinned,
    createdAt = createdAt,
    updatedAt = updatedAt
)
