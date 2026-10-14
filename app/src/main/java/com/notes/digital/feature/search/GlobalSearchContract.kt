package com.notes.digital.feature.search

import com.notes.digital.domain.model.Note

data class SearchResultItem(
    val note: Note,
    val bookTitle: String = "",
    val bookColorHex: String = "#2196F3"
)

data class GlobalSearchUiState(
    val query: String = "",
    val searchResults: List<SearchResultItem> = emptyList(),
    val isSearching: Boolean = false,
    val hasSearched: Boolean = false
)

sealed interface GlobalSearchUiEvent {
    data class OnQueryChanged(val query: String) : GlobalSearchUiEvent
    data object OnClearQuery : GlobalSearchUiEvent
    data class OnResultClicked(val item: SearchResultItem) : GlobalSearchUiEvent
}
