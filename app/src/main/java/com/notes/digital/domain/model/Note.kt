package com.notes.digital.domain.model

data class Note(
    val id: Long = 0L,
    val bookId: Long,
    val title: String,
    val content: String,
    val isPinned: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
