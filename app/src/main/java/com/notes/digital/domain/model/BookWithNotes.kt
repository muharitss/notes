package com.notes.digital.domain.model

data class BookWithNotes(
    val book: Book,
    val notes: List<Note>
)
