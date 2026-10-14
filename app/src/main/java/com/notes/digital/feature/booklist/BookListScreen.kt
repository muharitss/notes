package com.notes.digital.feature.booklist

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.notes.digital.core.designsystem.components.ConfirmationDialog
import com.notes.digital.core.designsystem.components.EmptyStatePlaceholder
import com.notes.digital.core.designsystem.components.NoteTopAppBar
import com.notes.digital.feature.booklist.components.AddEditBookDialog
import com.notes.digital.feature.booklist.components.BackupRestoreDialog
import com.notes.digital.feature.booklist.components.BookCard

@Composable
fun BookListScreen(
    uiState: BookListUiState,
    onEvent: (BookListUiEvent) -> Unit,
    onBookClick: (Long) -> Unit,
    onNavigateToSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri: Uri? ->
        if (uri != null) {
            val stream = context.contentResolver.openOutputStream(uri)
            if (stream != null) {
                onEvent(BookListUiEvent.OnExportBackup(stream))
            }
        }
    }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            val stream = context.contentResolver.openInputStream(uri)
            if (stream != null) {
                onEvent(BookListUiEvent.OnImportBackup(stream))
            }
        }
    }

    LaunchedEffect(uiState.userMessage) {
        val message = uiState.userMessage
        if (message != null) {
            snackbarHostState.showSnackbar(message)
            onEvent(BookListUiEvent.OnUserMessageDismissed)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            NoteTopAppBar(
                title = "Buku Catatan",
                actions = {
                    IconButton(
                        onClick = onNavigateToSearch,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Pencarian global"
                        )
                    }

                    IconButton(
                        onClick = { onEvent(BookListUiEvent.OnBackupRestoreClicked) },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Backup,
                            contentDescription = "Cadangkan dan pulihkan data"
                        )
                    }

                    IconButton(
                        onClick = { onEvent(BookListUiEvent.OnToggleLayoutClicked) },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = if (uiState.isGridLayout) {
                                Icons.AutoMirrored.Filled.ViewList
                            } else {
                                Icons.Default.GridView
                            },
                            contentDescription = if (uiState.isGridLayout) {
                                "Beralih ke tampilan daftar"
                            } else {
                                "Beralih ke tampilan kisi"
                            }
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onEvent(BookListUiEvent.OnAddBookClicked) },
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Buat buku baru"
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        if (uiState.books.isEmpty() && !uiState.isLoading) {
            EmptyStatePlaceholder(
                title = "Belum Ada Buku",
                subtitle = "Ketuk tombol + di bawah untuk membuat buku catatan pertama Anda.",
                actionLabel = "Buat Buku Sekarang",
                onActionClick = { onEvent(BookListUiEvent.OnAddBookClicked) },
                modifier = Modifier.padding(innerPadding)
            )
        } else {
            if (uiState.isGridLayout) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    items(uiState.books, key = { it.id }) { book ->
                        BookCard(
                            book = book,
                            onClick = { onBookClick(book.id) },
                            onEditClick = { onEvent(BookListUiEvent.OnEditBookClicked(book)) },
                            onDeleteClick = { onEvent(BookListUiEvent.OnDeleteBookClicked(book)) }
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    items(uiState.books, key = { it.id }) { book ->
                        BookCard(
                            book = book,
                            onClick = { onBookClick(book.id) },
                            onEditClick = { onEvent(BookListUiEvent.OnEditBookClicked(book)) },
                            onDeleteClick = { onEvent(BookListUiEvent.OnDeleteBookClicked(book)) }
                        )
                    }
                }
            }
        }
    }

    if (uiState.isAddEditDialogOpen) {
        AddEditBookDialog(
            initialBook = uiState.selectedBookForEdit,
            onDismiss = { onEvent(BookListUiEvent.OnDismissAddEditDialog) },
            onSave = { id, title, description, colorHex ->
                onEvent(
                    BookListUiEvent.OnSaveBook(
                        id = id,
                        title = title,
                        description = description,
                        colorHex = colorHex
                    )
                )
            }
        )
    }

    uiState.bookToDelete?.let { book ->
        ConfirmationDialog(
            title = "Hapus Buku \"${book.title}\"?",
            message = "Seluruh catatan di dalam buku ini akan terhapus secara permanen. Tindakan ini tidak dapat dibatalkan.",
            confirmLabel = "Hapus Permanen",
            onConfirm = { onEvent(BookListUiEvent.OnConfirmDeleteBook) },
            onDismiss = { onEvent(BookListUiEvent.OnDismissDeleteDialog) }
        )
    }

    if (uiState.isBackupRestoreDialogOpen) {
        BackupRestoreDialog(
            onDismiss = { onEvent(BookListUiEvent.OnDismissBackupRestoreDialog) },
            onExportClick = {
                exportLauncher.launch("catatan_backup_${System.currentTimeMillis()}.json")
            },
            onImportClick = {
                importLauncher.launch(arrayOf("application/json", "*/*"))
            }
        )
    }
}
