package com.notes.digital.core.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.notes.digital.core.database.dao.BookDao
import com.notes.digital.core.database.dao.NoteDao
import com.notes.digital.core.database.entity.BookEntity
import com.notes.digital.core.database.entity.NoteEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class DatabaseIntegrationTest {

    private lateinit var database: AppDatabase
    private lateinit var bookDao: BookDao
    private lateinit var noteDao: NoteDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        bookDao = database.bookDao()
        noteDao = database.noteDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        database.close()
    }

    /**
     * TEST-001: Foreign key cascade delete test.
     * Deleting a book automatically removes all child notes via SQLite cascade delete.
     */
    @Test
    fun testCascadeDeleteOnBookDeletion() = runTest {
        val bookId = bookDao.insertOrUpdateBook(
            BookEntity(
                title = "Matematika Diskrit",
                colorHex = "#4CAF50",
                createdAt = 1000L,
                updatedAt = 1000L
            )
        )

        noteDao.insertOrUpdateNote(
            NoteEntity(
                bookId = bookId,
                title = "Pertemuan 1",
                content = "Logika Proposisi",
                createdAt = 1001L,
                updatedAt = 1001L
            )
        )
        noteDao.insertOrUpdateNote(
            NoteEntity(
                bookId = bookId,
                title = "Pertemuan 2",
                content = "Tabel Kebenaran",
                createdAt = 1002L,
                updatedAt = 1002L
            )
        )
        noteDao.insertOrUpdateNote(
            NoteEntity(
                bookId = bookId,
                title = "Pertemuan 3",
                content = "Kalkulus Predikat",
                createdAt = 1003L,
                updatedAt = 1003L
            )
        )

        val notesBefore = noteDao.getNotesByBookDescending(bookId).first()
        assertEquals(3, notesBefore.size)

        // Delete parent book
        bookDao.deleteBookById(bookId)

        val notesAfter = noteDao.getNotesByBookDescending(bookId).first()
        assertTrue("All child notes must be deleted on book cascade delete", notesAfter.isEmpty())
    }

    /**
     * TEST-002: Reactive Flow emission test.
     * Flow emits updated list with correct note count when new items are added.
     */
    @Test
    fun testReactiveFlowEmissionWithNoteCount() = runTest {
        bookDao.getAllBooksWithCount().test {
            // Initial empty emission
            val initial = awaitItem()
            assertTrue(initial.isEmpty())

            // Insert book
            val bookId = bookDao.insertOrUpdateBook(
                BookEntity(
                    title = "Algoritma & Pemrograman",
                    colorHex = "#2196F3",
                    createdAt = 2000L,
                    updatedAt = 2000L
                )
            )

            val withBook = awaitItem()
            assertEquals(1, withBook.size)
            assertEquals("Algoritma & Pemrograman", withBook[0].book.title)
            assertEquals(0, withBook[0].noteCount)

            // Insert note in book
            noteDao.insertOrUpdateNote(
                NoteEntity(
                    bookId = bookId,
                    title = "Sorting Algorithm",
                    content = "QuickSort and MergeSort",
                    createdAt = 2001L,
                    updatedAt = 2001L
                )
            )

            val withNote = awaitItem()
            assertEquals(1, withNote.size)
            assertEquals(1, withNote[0].noteCount)

            cancelAndIgnoreRemainingEvents()
        }
    }

    /**
     * TEST-003: Note sorting ordering test (is_pinned DESC, updated_at DESC).
     * Pinned notes must appear at the top, followed by newest updated_at.
     */
    @Test
    fun testNoteSortingPinnedFirstThenUpdatedAtDescending() = runTest {
        val bookId = bookDao.insertOrUpdateBook(
            BookEntity(
                title = "Buku Catatan",
                colorHex = "#FF9800",
                createdAt = 1000L,
                updatedAt = 1000L
            )
        )

        noteDao.insertOrUpdateNote(
            NoteEntity(
                bookId = bookId,
                title = "Note Lama",
                content = "Konten 1",
                isPinned = false,
                createdAt = 1000L,
                updatedAt = 1000L
            )
        )
        noteDao.insertOrUpdateNote(
            NoteEntity(
                bookId = bookId,
                title = "Note Baru",
                content = "Konten 2",
                isPinned = false,
                createdAt = 2000L,
                updatedAt = 2000L
            )
        )
        noteDao.insertOrUpdateNote(
            NoteEntity(
                bookId = bookId,
                title = "Note Pinned",
                content = "Konten penting",
                isPinned = true,
                createdAt = 500L,
                updatedAt = 500L
            )
        )

        val notes = noteDao.getNotesByBookDescending(bookId).first()
        assertEquals(3, notes.size)
        // Index 0 must be the pinned note even though its updatedAt is older
        assertEquals("Note Pinned", notes[0].title)
        assertTrue(notes[0].isPinned)

        // Index 1 must be Note Baru (updatedAt 2000L)
        assertEquals("Note Baru", notes[1].title)

        // Index 2 must be Note Lama (updatedAt 1000L)
        assertEquals("Note Lama", notes[2].title)
    }
}
