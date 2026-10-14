package com.notes.digital.data.mapper

import com.notes.digital.core.database.dao.BookWithCountTuple
import com.notes.digital.core.database.entity.BookEntity
import com.notes.digital.core.database.entity.NoteEntity
import com.notes.digital.domain.model.Book
import com.notes.digital.domain.model.Note
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class MapperTest {

    @Test
    fun bookEntity_toDomain_mapsCorrectly() {
        val entity = BookEntity(
            id = 1L,
            title = "Catatan Kantor",
            description = "Deskripsi buku",
            colorHex = "#2E7D32",
            createdAt = 1000L,
            updatedAt = 2000L
        )

        val domain = entity.toDomain(noteCount = 5)

        assertEquals(1L, domain.id)
        assertEquals("Catatan Kantor", domain.title)
        assertEquals("Deskripsi buku", domain.description)
        assertEquals("#2E7D32", domain.colorHex)
        assertEquals(5, domain.noteCount)
        assertEquals(1000L, domain.createdAt)
        assertEquals(2000L, domain.updatedAt)
    }

    @Test
    fun bookWithCountTuple_toDomain_mapsCorrectly() {
        val entity = BookEntity(
            id = 2L,
            title = "Buku Riset",
            description = null,
            colorHex = "#1565C0",
            createdAt = 3000L,
            updatedAt = 4000L
        )
        val tuple = BookWithCountTuple(book = entity, noteCount = 12)

        val domain = tuple.toDomain()

        assertEquals(2L, domain.id)
        assertEquals("Buku Riset", domain.title)
        assertNull(domain.description)
        assertEquals("#1565C0", domain.colorHex)
        assertEquals(12, domain.noteCount)
        assertEquals(3000L, domain.createdAt)
        assertEquals(4000L, domain.updatedAt)
    }

    @Test
    fun bookDomain_toEntity_mapsCorrectly() {
        val domain = Book(
            id = 3L,
            title = "Personal",
            description = null,
            colorHex = "#C62828",
            noteCount = 99, // Should not affect entity
            createdAt = 5000L,
            updatedAt = 6000L
        )

        val entity = domain.toEntity()

        assertEquals(3L, entity.id)
        assertEquals("Personal", entity.title)
        assertNull(entity.description)
        assertEquals("#C62828", entity.colorHex)
        assertEquals(5000L, entity.createdAt)
        assertEquals(6000L, entity.updatedAt)
    }

    @Test
    fun noteEntity_toDomain_mapsCorrectly() {
        val entity = NoteEntity(
            id = 10L,
            bookId = 1L,
            title = "Notulensi Sprint",
            content = "Isi rapat sprint planning",
            isPinned = true,
            createdAt = 1000L,
            updatedAt = 2000L
        )

        val domain = entity.toDomain()

        assertEquals(10L, domain.id)
        assertEquals(1L, domain.bookId)
        assertEquals("Notulensi Sprint", domain.title)
        assertEquals("Isi rapat sprint planning", domain.content)
        assertEquals(true, domain.isPinned)
        assertEquals(1000L, domain.createdAt)
        assertEquals(2000L, domain.updatedAt)
    }

    @Test
    fun noteDomain_toEntity_mapsCorrectly() {
        val domain = Note(
            id = 20L,
            bookId = 2L,
            title = "Ide Fitur",
            content = "Detail ide fitur baru",
            isPinned = false,
            createdAt = 3000L,
            updatedAt = 4000L
        )

        val entity = domain.toEntity()

        assertEquals(20L, entity.id)
        assertEquals(2L, domain.bookId)
        assertEquals("Ide Fitur", entity.title)
        assertEquals("Detail ide fitur baru", entity.content)
        assertEquals(false, entity.isPinned)
        assertEquals(3000L, entity.createdAt)
        assertEquals(4000L, entity.updatedAt)
    }
}
