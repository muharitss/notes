package com.notes.digital.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.notes.digital.core.database.dao.BookDao
import com.notes.digital.core.database.dao.NoteDao
import com.notes.digital.core.database.entity.BookEntity
import com.notes.digital.core.database.entity.NoteEntity

@Database(
    entities = [
        BookEntity::class,
        NoteEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao
    abstract fun noteDao(): NoteDao

    companion object {
        private const val DATABASE_NAME = "notes_app.db"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                ).build().also { INSTANCE = it }
            }
        }
    }
}
