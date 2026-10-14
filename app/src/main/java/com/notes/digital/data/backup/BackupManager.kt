package com.notes.digital.data.backup

import androidx.room.withTransaction
import com.notes.digital.core.database.AppDatabase
import com.notes.digital.core.database.entity.BookEntity
import com.notes.digital.core.database.entity.NoteEntity
import com.notes.digital.core.util.AppResult
import com.notes.digital.data.backup.dto.BackupDataDto
import com.notes.digital.data.backup.dto.BookDto
import com.notes.digital.data.backup.dto.NoteDto
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

class BackupManager(
    private val database: AppDatabase,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    suspend fun exportBackupToJson(outputStream: OutputStream): AppResult<Unit> = withContext(ioDispatcher) {
        try {
            val books = database.bookDao().getAllBooks().map {
                BookDto(
                    id = it.id,
                    title = it.title,
                    description = it.description,
                    colorHex = it.colorHex,
                    createdAt = it.createdAt,
                    updatedAt = it.updatedAt
                )
            }
            val notes = database.noteDao().getAllNotes().map {
                NoteDto(
                    id = it.id,
                    bookId = it.bookId,
                    title = it.title,
                    content = it.content,
                    isPinned = it.isPinned,
                    createdAt = it.createdAt,
                    updatedAt = it.updatedAt
                )
            }
            val backupData = BackupDataDto(
                version = 1,
                timestamp = System.currentTimeMillis(),
                books = books,
                notes = notes
            )
            val jsonString = json.encodeToString(BackupDataDto.serializer(), backupData)
            outputStream.bufferedWriter().use { writer ->
                writer.write(jsonString)
                writer.flush()
            }
            AppResult.Success(Unit)
        } catch (e: Exception) {
            AppResult.Error(e.message ?: "Gagal mengekspor data cadangan", e)
        }
    }

    suspend fun importBackupFromJson(inputStream: InputStream): AppResult<Unit> = withContext(ioDispatcher) {
        try {
            val jsonString = inputStream.bufferedReader().use { it.readText() }
            val backupData = json.decodeFromString(BackupDataDto.serializer(), jsonString)

            if (backupData.books.isEmpty() && backupData.notes.isEmpty()) {
                return@withContext AppResult.Error("File cadangan kosong atau tidak valid")
            }

            database.withTransaction {
                val bookIdMap = mutableMapOf<Long, Long>()
                for (bookDto in backupData.books) {
                    val newBookId = database.bookDao().insertOrUpdateBook(
                        BookEntity(
                            id = 0L,
                            title = bookDto.title,
                            description = bookDto.description,
                            colorHex = bookDto.colorHex,
                            createdAt = bookDto.createdAt,
                            updatedAt = bookDto.updatedAt
                        )
                    )
                    bookIdMap[bookDto.id] = newBookId
                }

                for (noteDto in backupData.notes) {
                    val targetBookId = bookIdMap[noteDto.bookId] ?: continue
                    database.noteDao().insertOrUpdateNote(
                        NoteEntity(
                            id = 0L,
                            bookId = targetBookId,
                            title = noteDto.title,
                            content = noteDto.content,
                            isPinned = noteDto.isPinned,
                            createdAt = noteDto.createdAt,
                            updatedAt = noteDto.updatedAt
                        )
                    )
                }
            }
            AppResult.Success(Unit)
        } catch (e: Exception) {
            AppResult.Error(e.message ?: "Gagal mengimpor file cadangan", e)
        }
    }
}
