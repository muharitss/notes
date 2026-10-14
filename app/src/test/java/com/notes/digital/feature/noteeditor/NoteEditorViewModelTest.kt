package com.notes.digital.feature.noteeditor

import app.cash.turbine.test
import com.notes.digital.domain.model.Note
import com.notes.digital.domain.repository.NoteRepository
import com.notes.digital.domain.usecase.note.DeleteNoteUseCase
import com.notes.digital.domain.usecase.note.SaveNoteUseCase
import com.notes.digital.domain.usecase.note.TogglePinNoteUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NoteEditorViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeNoteRepository: FakeNoteRepository
    private lateinit var saveNoteUseCase: SaveNoteUseCase
    private lateinit var deleteNoteUseCase: DeleteNoteUseCase
    private lateinit var togglePinNoteUseCase: TogglePinNoteUseCase

    private class FakeNoteRepository : NoteRepository {
        val notesMap = mutableMapOf<Long, Note>()
        var nextId = 100L
        var saveCallCount = 0

        override fun getNotesByBookDescending(bookId: Long): Flow<List<Note>> =
            flowOf(notesMap.values.filter { it.bookId == bookId })

        override fun getNotesByBookAscending(bookId: Long): Flow<List<Note>> =
            flowOf(notesMap.values.filter { it.bookId == bookId })

        override fun getNoteById(noteId: Long): Flow<Note?> =
            flowOf(notesMap[noteId])

        override fun searchNotes(query: String): Flow<List<Note>> =
            flowOf(notesMap.values.filter { it.title.contains(query) || it.content.contains(query) })

        override suspend fun insertOrUpdateNote(note: Note): Long {
            saveCallCount++
            val id = if (note.id == 0L) nextId++ else note.id
            val saved = note.copy(id = id)
            notesMap[id] = saved
            return id
        }

        override suspend fun deleteNote(noteId: Long) {
            notesMap.remove(noteId)
        }

        override suspend fun updatePinStatus(noteId: Long, isPinned: Boolean) {
            val note = notesMap[noteId] ?: return
            notesMap[noteId] = note.copy(isPinned = isPinned)
        }
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeNoteRepository = FakeNoteRepository()
        saveNoteUseCase = SaveNoteUseCase(fakeNoteRepository)
        deleteNoteUseCase = DeleteNoteUseCase(fakeNoteRepository)
        togglePinNoteUseCase = TogglePinNoteUseCase(fakeNoteRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(noteId: Long = -1L): NoteEditorViewModel {
        return NoteEditorViewModel(
            bookId = 1L,
            initialNoteId = noteId,
            noteRepository = fakeNoteRepository,
            saveNoteUseCase = saveNoteUseCase,
            deleteNoteUseCase = deleteNoteUseCase,
            togglePinNoteUseCase = togglePinNoteUseCase,
            ioDispatcher = testDispatcher
        )
    }

    @Test
    fun debounceTiming_savesOnlyAfter500ms() = runTest(testDispatcher) {
        val viewModel = createViewModel()

        viewModel.uiState.test {
            awaitItem() // initial idle state

            // User types title
            viewModel.onEvent(NoteEditorUiEvent.OnTitleChanged("Pertemuan 1"))
            advanceTimeBy(499L)
            assertEquals(0, fakeNoteRepository.saveCallCount)

            // Advance past 500 ms threshold
            advanceTimeBy(2L)
            advanceUntilIdle()

            assertEquals(1, fakeNoteRepository.saveCallCount)
            val savedNote = fakeNoteRepository.notesMap.values.firstOrNull()
            assertNotNull(savedNote)
            assertEquals("Pertemuan 1", savedNote?.title)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun instantFlush_savesImmediatelyBeforeDebounceExpires() = runTest(testDispatcher) {
        val viewModel = createViewModel()

        viewModel.uiState.test {
            awaitItem()

            viewModel.onEvent(NoteEditorUiEvent.OnTitleChanged("Materi Cepat"))
            advanceTimeBy(100L) // Only 100 ms passed
            assertEquals(0, fakeNoteRepository.saveCallCount)

            // Instant flush requested (e.g. Back pressed or lifecycle onPause)
            viewModel.onEvent(NoteEditorUiEvent.OnFlushSaveRequested)
            advanceUntilIdle()

            assertEquals(1, fakeNoteRepository.saveCallCount)
            val saved = fakeNoteRepository.notesMap.values.firstOrNull()
            assertNotNull(saved)
            assertEquals("Materi Cepat", saved?.title)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun blankTitleWithContent_usesFallbackTitleZeroDataLoss() = runTest(testDispatcher) {
        val viewModel = createViewModel()

        viewModel.uiState.test {
            awaitItem()

            // User jumps straight to writing content without title
            viewModel.onEvent(NoteEditorUiEvent.OnContentChanged("Rumus turunan f'(x) = ..."))
            advanceTimeBy(501L)
            advanceUntilIdle()

            assertEquals(1, fakeNoteRepository.saveCallCount)
            val saved = fakeNoteRepository.notesMap.values.firstOrNull()
            assertNotNull(saved)
            assertEquals("Catatan Baru", saved?.title)
            assertEquals("Rumus turunan f'(x) = ...", saved?.content)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun existingNote_loadsInitialContent() = runTest(testDispatcher) {
        val existing = Note(
            id = 42L,
            bookId = 1L,
            title = "Pertemuan 2",
            content = "Integral Tentu",
            isPinned = true
        )
        fakeNoteRepository.notesMap[42L] = existing

        val viewModel = createViewModel(noteId = 42L)

        viewModel.uiState.test {
            awaitItem()
            advanceUntilIdle()

            val state = expectMostRecentItem()
            assertFalse(state.isLoading)
            assertEquals(42L, state.noteId)
            assertEquals("Pertemuan 2", state.title)
            assertEquals("Integral Tentu", state.content)
            assertTrue(state.isPinned)
            assertFalse(state.isNewNote)
            assertEquals(SaveStatus.SAVED, state.saveStatus)
        }
    }

    @Test
    fun togglePin_updatesStateAndRepository() = runTest(testDispatcher) {
        val existing = Note(
            id = 42L,
            bookId = 1L,
            title = "Catatan Uji",
            content = "Isi",
            isPinned = false
        )
        fakeNoteRepository.notesMap[42L] = existing

        val viewModel = createViewModel(noteId = 42L)
        advanceUntilIdle()

        viewModel.onEvent(NoteEditorUiEvent.OnTogglePinRequested)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isPinned)
        assertTrue(fakeNoteRepository.notesMap[42L]?.isPinned == true)
    }

    @Test
    fun deleteNote_showsDialogAndDeletes() = runTest(testDispatcher) {
        val existing = Note(
            id = 42L,
            bookId = 1L,
            title = "Catatan Dihapus",
            content = "Akan dibuang"
        )
        fakeNoteRepository.notesMap[42L] = existing

        val viewModel = createViewModel(noteId = 42L)
        advanceUntilIdle()

        viewModel.onEvent(NoteEditorUiEvent.OnDeleteNoteRequested)
        assertTrue(viewModel.uiState.value.showDeleteDialog)

        viewModel.onEvent(NoteEditorUiEvent.OnConfirmDeleteNote)
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.showDeleteDialog)
        assertTrue(viewModel.uiState.value.isDeleted)
        assertFalse(fakeNoteRepository.notesMap.containsKey(42L))
    }
}
