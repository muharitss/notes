package com.notes.digital.data.backup.dto

import kotlinx.serialization.Serializable

@Serializable
data class BackupDataDto(
    val version: Int = 1,
    val timestamp: Long = System.currentTimeMillis(),
    val books: List<BookDto> = emptyList(),
    val notes: List<NoteDto> = emptyList()
)

@Serializable
data class BookDto(
    val id: Long,
    val title: String,
    val description: String? = null,
    val colorHex: String,
    val createdAt: Long,
    val updatedAt: Long
)

@Serializable
data class NoteDto(
    val id: Long,
    val bookId: Long,
    val title: String,
    val content: String,
    val isPinned: Boolean = false,
    val createdAt: Long,
    val updatedAt: Long
)
