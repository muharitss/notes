package com.notes.digital.feature.bookdetail.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.notes.digital.feature.bookdetail.NoteSortOrder

@Composable
fun SortOrderDropdown(
    currentSortOrder: NoteSortOrder,
    onSortOrderSelected: (NoteSortOrder) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        IconButton(
            onClick = { expanded = true },
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Sort,
                contentDescription = "Urutkan catatan"
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Terbaru Dimodifikasi") },
                onClick = {
                    expanded = false
                    onSortOrderSelected(NoteSortOrder.NEWEST_MODIFIED)
                },
                trailingIcon = if (currentSortOrder == NoteSortOrder.NEWEST_MODIFIED) {
                    {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                } else null
            )
            DropdownMenuItem(
                text = { Text("Terlama Dibuat") },
                onClick = {
                    expanded = false
                    onSortOrderSelected(NoteSortOrder.OLDEST_CREATED)
                },
                trailingIcon = if (currentSortOrder == NoteSortOrder.OLDEST_CREATED) {
                    {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                } else null
            )
        }
    }
}
