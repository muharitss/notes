package com.notes.digital.data.mapper

import com.notes.digital.core.database.dao.BookWithCountTuple
import com.notes.digital.core.database.entity.BookEntity
import com.notes.digital.domain.model.Book

fun BookEntity.toDomain(noteCount: Int = 0): Book = Book(
    id = id,
    title = title,
    description = description,
    colorHex = colorHex,
    noteCount = noteCount,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun BookWithCountTuple.toDomain(): Book = Book(
    id = book.id,
    title = book.title,
    description = book.description,
    colorHex = book.colorHex,
    noteCount = noteCount,
    createdAt = book.createdAt,
    updatedAt = book.updatedAt
)

fun Book.toEntity(): BookEntity = BookEntity(
    id = id,
    title = title,
    description = description,
    colorHex = colorHex,
    createdAt = createdAt,
    updatedAt = updatedAt
)
