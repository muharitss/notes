package com.notes.digital.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.notes.digital.core.designsystem.AppTheme
import com.notes.digital.core.designsystem.BookCoverColor
import com.notes.digital.core.designsystem.BookCoverColors

@Composable
fun ColorPalettePicker(
    selectedColorHex: String,
    onColorSelected: (BookCoverColor) -> Unit,
    modifier: Modifier = Modifier,
    colors: List<BookCoverColor> = BookCoverColors
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items(colors, key = { it.id }) { coverColor ->
            val isSelected = coverColor.hex.equals(selectedColorHex, ignoreCase = true)
            val description = if (isSelected) {
                "Warna aksen ${coverColor.name} terpilih"
            } else {
                "Pilih warna aksen ${coverColor.name}"
            }

            Box(
                modifier = Modifier
                    .size(48.dp) // Touch target minimum 48x48 dp
                    .semantics {
                        this.role = Role.RadioButton
                        this.selected = isSelected
                        this.contentDescription = description
                    }
                    .clickable { onColorSelected(coverColor) },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(coverColor.color)
                        .then(
                            if (isSelected) {
                                Modifier.border(
                                    width = 2.5.dp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    shape = CircleShape
                                )
                            } else {
                                Modifier
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ColorPalettePickerPreview() {
    AppTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            ColorPalettePicker(
                selectedColorHex = "#3F51B5",
                onColorSelected = {}
            )
        }
    }
}
