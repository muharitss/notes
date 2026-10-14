package com.notes.digital.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface Destination {
    @Serializable
    data object BookList : Destination

    @Serializable
    data class BookDetail(val bookId: Long) : Destination

    @Serializable
    data class NoteEditor(val bookId: Long, val noteId: Long = -1L) : Destination

    @Serializable
    data object GlobalSearch : Destination
}
