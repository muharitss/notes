package com.notes.digital.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Destination.BookList,
        modifier = modifier
    ) {
        composable<Destination.BookList> {
            val context = androidx.compose.ui.platform.LocalContext.current
            val app = context.applicationContext as com.notes.digital.App
            val viewModel: com.notes.digital.feature.booklist.BookListViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
                factory = com.notes.digital.feature.booklist.BookListViewModel.provideFactory(app.container)
            )
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            com.notes.digital.feature.booklist.BookListScreen(
                uiState = uiState,
                onEvent = viewModel::onEvent,
                onBookClick = { bookId ->
                    navController.navigate(Destination.BookDetail(bookId))
                },
                onNavigateToSearch = {
                    navController.navigate(Destination.GlobalSearch)
                }
            )
        }
        composable<Destination.BookDetail> { backStackEntry ->
            val route = backStackEntry.toRoute<Destination.BookDetail>()
            val context = androidx.compose.ui.platform.LocalContext.current
            val app = context.applicationContext as com.notes.digital.App
            val viewModel: com.notes.digital.feature.bookdetail.BookDetailViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
                factory = com.notes.digital.feature.bookdetail.BookDetailViewModel.provideFactory(app.container, route.bookId)
            )
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            com.notes.digital.feature.bookdetail.BookDetailScreen(
                uiState = uiState,
                onEvent = viewModel::onEvent,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNoteClick = { noteId ->
                    navController.navigate(Destination.NoteEditor(bookId = route.bookId, noteId = noteId))
                },
                onAddNoteClick = {
                    navController.navigate(Destination.NoteEditor(bookId = route.bookId, noteId = -1L))
                }
            )
        }
        composable<Destination.NoteEditor> { backStackEntry ->
            val route = backStackEntry.toRoute<Destination.NoteEditor>()
            Surface(modifier = Modifier.fillMaxSize()) {
                Text(text = "Editor Catatan (Book: ${route.bookId}, Note: ${route.noteId})")
            }
        }
        composable<Destination.GlobalSearch> {
            Surface(modifier = Modifier.fillMaxSize()) {
                Text(text = "Pencarian Global")
            }
        }
    }
}
