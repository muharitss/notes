package com.notes.digital.core.designsystem

import com.notes.digital.navigation.Destination
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DesignSystemTest {

    @Test
    fun bookCoverColors_containsAll8CuratedColors() {
        assertEquals(8, BookCoverColors.size)

        val expectedIds = listOf(
            "COLOR-01", "COLOR-02", "COLOR-03", "COLOR-04",
            "COLOR-05", "COLOR-06", "COLOR-07", "COLOR-08"
        )
        val actualIds = BookCoverColors.map { it.id }
        assertEquals(expectedIds, actualIds)

        BookCoverColors.forEach { coverColor ->
            assertTrue(coverColor.hex.startsWith("#"))
            assertEquals(7, coverColor.hex.length)
            assertTrue(coverColor.name.isNotBlank())
        }

        assertEquals("COLOR-01", DefaultBookCoverColor.id)
    }

    @Test
    fun navigationDestinations_areSerializable() {
        val json = Json { ignoreUnknownKeys = true }

        val bookListJson = json.encodeToString<Destination>(Destination.BookList)
        val decodedBookList = json.decodeFromString<Destination>(bookListJson)
        assertEquals(Destination.BookList, decodedBookList)

        val bookDetail = Destination.BookDetail(bookId = 123L)
        val bookDetailJson = json.encodeToString<Destination>(bookDetail)
        val decodedBookDetail = json.decodeFromString<Destination>(bookDetailJson) as Destination.BookDetail
        assertEquals(123L, decodedBookDetail.bookId)

        val noteEditor = Destination.NoteEditor(bookId = 123L, noteId = 456L)
        val noteEditorJson = json.encodeToString<Destination>(noteEditor)
        val decodedNoteEditor = json.decodeFromString<Destination>(noteEditorJson) as Destination.NoteEditor
        assertEquals(123L, decodedNoteEditor.bookId)
        assertEquals(456L, decodedNoteEditor.noteId)

        val globalSearchJson = json.encodeToString<Destination>(Destination.GlobalSearch)
        val decodedGlobalSearch = json.decodeFromString<Destination>(globalSearchJson)
        assertEquals(Destination.GlobalSearch, decodedGlobalSearch)
    }
}
