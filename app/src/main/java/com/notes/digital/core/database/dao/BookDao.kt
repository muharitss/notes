package com.notes.digital.core.database.dao

import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.notes.digital.core.database.entity.BookEntity
import kotlinx.coroutines.flow.Flow

data class BookWithCountTuple(
    @Embedded val book: BookEntity,
    val noteCount: Int
)

@Dao
interface BookDao {
    @Query("""
        SELECT b.*, COUNT(n.id) as noteCount 
        FROM books b 
        LEFT JOIN notes n ON b.id = n.book_id 
        GROUP BY b.id 
        ORDER BY b.updated_at DESC
    """)
    fun getAllBooksWithCount(): Flow<List<BookWithCountTuple>>

    @Query("SELECT * FROM books WHERE id = :bookId LIMIT 1")
    fun getBookById(bookId: Long): Flow<BookEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateBook(book: BookEntity): Long

    @Query("DELETE FROM books WHERE id = :bookId")
    suspend fun deleteBookById(bookId: Long)
}
