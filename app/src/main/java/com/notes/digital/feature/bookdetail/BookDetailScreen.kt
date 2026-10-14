package com.notes.digital.feature.bookdetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.notes.digital.core.designsystem.components.ConfirmationDialog
import com.notes.digital.core.designsystem.components.EmptyStatePlaceholder
import com.notes.digital.core.designsystem.components.NoteTopAppBar
import com.notes.digital.feature.bookdetail.components.NoteListItem
import com.notes.digital.feature.bookdetail.components.SortOrderDropdown

@Composable
fun BookDetailScreen(
    uiState: BookDetailUiState,
    onEvent: (BookDetailUiEvent) -> Unit,
    onNavigateBack: () -> Unit,
    onNoteClick: (Long) -> Unit,
    onAddNoteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.userMessage) {
        val message = uiState.userMessage
        if (message != null) {
            snackbarHostState.showSnackbar(message)
            onEvent(BookDetailUiEvent.OnUserMessageDismissed)
        }
    }

    val bookColor = remember(uiState.book?.colorHex) {
        try {
            uiState.book?.colorHex?.let { Color(android.graphics.Color.parseColor(it)) }
                ?: Color(0xFF3F51B5)
        } catch (_: Exception) {
            Color(0xFF3F51B5)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            NoteTopAppBar(
                title = uiState.book?.title ?: "Detail Buku",
                onNavigateBack = onNavigateBack,
                actions = {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(bookColor)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = { onEvent(BookDetailUiEvent.OnToggleSearchActive) },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = if (uiState.isSearchActive) {
                                "Tutup pencarian"
                            } else {
                                "Cari catatan dalam buku"
                            }
                        )
                    }

                    SortOrderDropdown(
                        currentSortOrder = uiState.sortOrder,
                        onSortOrderSelected = { order ->
                            onEvent(BookDetailUiEvent.OnSortOrderChanged(order))
                        }
                    )
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddNoteClick,
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Buat catatan baru"
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.isSearchActive) {
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = { onEvent(BookDetailUiEvent.OnSearchQueryChanged(it)) },
                    placeholder = { Text("Cari judul atau isi catatan...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null
                        )
                    },
                    trailingIcon = if (uiState.searchQuery.isNotEmpty()) {
                        {
                            IconButton(
                                onClick = { onEvent(BookDetailUiEvent.OnSearchQueryChanged("")) },
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Hapus teks pencarian"
                                )
                            }
                        }
                    } else null
                )
            }

            if (uiState.notes.isEmpty()) {
                if (uiState.searchQuery.isNotBlank()) {
                    EmptyStatePlaceholder(
                        title = "Tidak Ditemukan",
                        subtitle = "Tidak ada catatan yang cocok dengan \"${uiState.searchQuery}\""
                    )
                } else {
                    EmptyStatePlaceholder(
                        title = "Belum Ada Catatan",
                        subtitle = "Mulai tulis catatan pertemuan atau materi di buku ini.",
                        actionLabel = "Catatan Baru",
                        onActionClick = onAddNoteClick
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = uiState.notes,
                        key = { it.id }
                    ) { note ->
                        NoteListItem(
                            note = note,
                            onClick = { onNoteClick(note.id) },
                            onTogglePin = { onEvent(BookDetailUiEvent.OnTogglePinNote(note)) },
                            onDeleteClick = { onEvent(BookDetailUiEvent.OnDeleteNoteClicked(note)) }
                        )
                    }
                }
            }
        }
    }

    if (uiState.noteToDelete != null) {
        ConfirmationDialog(
            title = "Hapus Catatan?",
            message = "Apakah Anda yakin ingin menghapus catatan \"${uiState.noteToDelete.title}\" secara permanen?",
            confirmLabel = "Hapus",
            dismissLabel = "Batal",
            onConfirm = { onEvent(BookDetailUiEvent.OnConfirmDeleteNote) },
            onDismiss = { onEvent(BookDetailUiEvent.OnDismissDeleteDialog) }
        )
    }
}
