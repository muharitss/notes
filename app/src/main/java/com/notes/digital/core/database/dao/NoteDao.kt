package com.notes.digital.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.notes.digital.core.database.entity.NoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("""
        SELECT * FROM notes 
        WHERE book_id = :bookId 
        ORDER BY is_pinned DESC, updated_at DESC
    """)
    fun getNotesByBookDescending(bookId: Long): Flow<List<NoteEntity>>

    @Query("""
        SELECT * FROM notes 
        WHERE book_id = :bookId 
        ORDER BY is_pinned DESC, created_at ASC
    """)
    fun getNotesByBookAscending(bookId: Long): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE id = :noteId LIMIT 1")
    fun getNoteById(noteId: Long): Flow<NoteEntity?>

    @Query("""
        SELECT * FROM notes 
        WHERE title LIKE '%' || :query || '%' 
           OR content LIKE '%' || :query || '%' 
        ORDER BY updated_at DESC
    """)
    fun searchNotesGlobal(query: String): Flow<List<NoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateNote(note: NoteEntity): Long

    @Query("DELETE FROM notes WHERE id = :noteId")
    suspend fun deleteNoteById(noteId: Long)

    @Query("UPDATE notes SET is_pinned = :isPinned WHERE id = :noteId")
    suspend fun updatePinStatus(noteId: Long, isPinned: Boolean)
}
