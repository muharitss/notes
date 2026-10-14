package com.notes.digital.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
            Surface(modifier = Modifier.fillMaxSize()) {
                Text(text = "Daftar Buku")
            }
        }
        composable<Destination.BookDetail> { backStackEntry ->
            val route = backStackEntry.toRoute<Destination.BookDetail>()
            Surface(modifier = Modifier.fillMaxSize()) {
                Text(text = "Detail Buku ID: ${route.bookId}")
            }
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
