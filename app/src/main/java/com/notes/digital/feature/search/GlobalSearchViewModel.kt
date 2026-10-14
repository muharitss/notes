package com.notes.digital.feature.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.notes.digital.core.di.AppContainer
import com.notes.digital.domain.repository.BookRepository
import com.notes.digital.domain.usecase.note.SearchNotesUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
class GlobalSearchViewModel(
    private val searchNotesUseCase: SearchNotesUseCase,
    private val bookRepository: BookRepository
) : ViewModel() {

    private val _query = MutableStateFlow("")

    private val _searchFlow = _query
        .debounce { query -> if (query.isBlank()) 0L else 300L }
        .distinctUntilChanged()
        .flatMapLatest { query ->
            val trimmed = query.trim()
            if (trimmed.isEmpty()) {
                flowOf("" to emptyList<SearchResultItem>())
            } else {
                combine(
                    searchNotesUseCase(trimmed),
                    bookRepository.getAllBooks()
                ) { notes, books ->
                    val booksMap = books.associateBy { it.id }
                    val items = notes.map { note ->
                        val book = booksMap[note.bookId]
                        SearchResultItem(
                            note = note,
                            bookTitle = book?.title ?: "Buku Catatan",
                            bookColorHex = book?.colorHex ?: "#2196F3"
                        )
                    }
                    trimmed to items
                }
            }
        }

    val uiState: StateFlow<GlobalSearchUiState> = combine(
        _query,
        _searchFlow
    ) { query, (searchedQuery, searchResults) ->
        val trimmed = query.trim()
        val isBlank = trimmed.isEmpty()
        val isSearching = !isBlank && trimmed != searchedQuery
        val results = if (isBlank) {
            emptyList()
        } else if (trimmed == searchedQuery) {
            searchResults
        } else {
            emptyList()
        }

        GlobalSearchUiState(
            query = query,
            searchResults = results,
            isSearching = isSearching,
            hasSearched = !isBlank && !isSearching
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = GlobalSearchUiState()
    )

    fun onEvent(event: GlobalSearchUiEvent) {
        when (event) {
            is GlobalSearchUiEvent.OnQueryChanged -> {
                _query.update { event.query }
            }
            is GlobalSearchUiEvent.OnClearQuery -> {
                _query.update { "" }
            }
            is GlobalSearchUiEvent.OnResultClicked -> {
                // Handled via UI navigation callback
            }
        }
    }

    companion object {
        fun provideFactory(container: AppContainer): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return GlobalSearchViewModel(
                        searchNotesUseCase = SearchNotesUseCase(container.noteRepository),
                        bookRepository = container.bookRepository
                    ) as T
                }
            }
    }
}
