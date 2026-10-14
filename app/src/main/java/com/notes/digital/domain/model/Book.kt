package com.notes.digital.domain.model

data class Book(
    val id: Long = 0L,
    val title: String,
    val description: String? = null,
    val colorHex: String,
    val noteCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
