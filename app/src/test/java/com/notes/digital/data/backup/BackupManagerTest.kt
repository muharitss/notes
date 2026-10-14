package com.notes.digital.data.backup

import com.notes.digital.data.backup.dto.BackupDataDto
import com.notes.digital.data.backup.dto.BookDto
import com.notes.digital.data.backup.dto.NoteDto
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class BackupManagerTest {

    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    @Test
    fun serializeBackupData_producesExpectedJsonStructure() {
        val backupData = BackupDataDto(
            version = 1,
            timestamp = 1700000000000L,
            books = listOf(
                BookDto(
                    id = 1L,
                    title = "Buku Catatan Kerja",
                    description = "Proyek 2026",
                    colorHex = "#3F51B5",
                    createdAt = 1700000000000L,
                    updatedAt = 1700000000000L
                )
            ),
            notes = listOf(
                NoteDto(
                    id = 101L,
                    bookId = 1L,
                    title = "Rapat Perencanaan",
                    content = "Membahas arsitektur offline-first",
                    isPinned = true,
                    createdAt = 1700000000000L,
                    updatedAt = 1700000000000L
                )
            )
        )

        val jsonString = json.encodeToString(BackupDataDto.serializer(), backupData)

        assertTrue(jsonString.contains(""""version": 1"""))
        assertTrue(jsonString.contains(""""title": "Buku Catatan Kerja""""))
        assertTrue(jsonString.contains(""""title": "Rapat Perencanaan""""))
        assertTrue(jsonString.contains(""""isPinned": true"""))

        val deserialized = json.decodeFromString(BackupDataDto.serializer(), jsonString)
        assertEquals(backupData.version, deserialized.version)
        assertEquals(backupData.timestamp, deserialized.timestamp)
        assertEquals(1, deserialized.books.size)
        assertEquals("Buku Catatan Kerja", deserialized.books[0].title)
        assertEquals("Proyek 2026", deserialized.books[0].description)
        assertEquals(1, deserialized.notes.size)
        assertEquals("Rapat Perencanaan", deserialized.notes[0].title)
        assertTrue(deserialized.notes[0].isPinned)
    }

    @Test
    fun deserializeBackupData_withMissingOptionalFields_usesDefaults() {
        val rawJson = """
            {
                "version": 1,
                "timestamp": 123456789,
                "books": [
                    {
                        "id": 2,
                        "title": "Buku Harian",
                        "colorHex": "#E91E63",
                        "createdAt": 1000,
                        "updatedAt": 2000
                    }
                ],
                "notes": []
            }
        """.trimIndent()

        val deserialized = json.decodeFromString(BackupDataDto.serializer(), rawJson)
        assertEquals(1, deserialized.books.size)
        assertEquals("Buku Harian", deserialized.books[0].title)
        assertNull(deserialized.books[0].description)
        assertTrue(deserialized.notes.isEmpty())
    }

    @Test(expected = SerializationException::class)
    fun deserializeBackupData_invalidJson_throwsException() {
        val corruptedJson = "{ invalid: json, broken }"
        json.decodeFromString(BackupDataDto.serializer(), corruptedJson)
    }
}
