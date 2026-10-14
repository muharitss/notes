package com.notes.digital.feature.booklist.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.notes.digital.core.designsystem.AppTheme
import com.notes.digital.core.designsystem.DefaultBookCoverColor
import com.notes.digital.core.designsystem.components.ColorPalettePicker
import com.notes.digital.domain.model.Book

@Composable
fun AddEditBookDialog(
    onDismiss: () -> Unit,
    onSave: (id: Long, title: String, description: String?, colorHex: String) -> Unit,
    modifier: Modifier = Modifier,
    initialBook: Book? = null
) {
    var title by remember { mutableStateOf(initialBook?.title ?: "") }
    var description by remember { mutableStateOf(initialBook?.description ?: "") }
    var selectedColorHex by remember {
        mutableStateOf(initialBook?.colorHex ?: DefaultBookCoverColor.hex)
    }

    val isEditMode = initialBook != null
    val isTitleValid = title.isNotBlank()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isEditMode) "Edit Buku" else "Buku Baru",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Nama Buku (misal: Matematika)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Deskripsi / Keterangan (opsional)") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Pilih Warna Sampul",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                ColorPalettePicker(
                    selectedColorHex = selectedColorHex,
                    onColorSelected = { selectedColorHex = it.hex }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (isTitleValid) {
                        onSave(
                            initialBook?.id ?: 0L,
                            title.trim(),
                            description.trim().ifEmpty { null },
                            selectedColorHex
                        )
                    }
                },
                enabled = isTitleValid,
                modifier = Modifier.height(48.dp)
            ) {
                Text("Simpan")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.height(48.dp)
            ) {
                Text("Batal")
            }
        },
        modifier = modifier
    )
}

@Preview
@Composable
private fun AddEditBookDialogPreview() {
    AppTheme {
        AddEditBookDialog(
            onDismiss = {},
            onSave = { _, _, _, _ -> }
        )
    }
}
