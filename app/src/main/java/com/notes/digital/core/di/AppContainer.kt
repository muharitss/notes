package com.notes.digital.core.di

import android.content.Context
import com.notes.digital.core.database.AppDatabase
import com.notes.digital.data.repository.BookRepositoryImpl
import com.notes.digital.data.repository.NoteRepositoryImpl
import com.notes.digital.domain.repository.BookRepository
import com.notes.digital.domain.repository.NoteRepository

interface AppContainer {
    val database: AppDatabase
    val bookRepository: BookRepository
    val noteRepository: NoteRepository
}

class DefaultAppContainer(private val context: Context) : AppContainer {

    override val database: AppDatabase by lazy {
        AppDatabase.getInstance(context)
    }

    override val bookRepository: BookRepository by lazy {
        BookRepositoryImpl(database.bookDao())
    }

    override val noteRepository: NoteRepository by lazy {
        NoteRepositoryImpl(database.noteDao())
    }
}
