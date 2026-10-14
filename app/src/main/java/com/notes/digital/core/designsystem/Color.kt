package com.notes.digital.core.designsystem

import androidx.compose.ui.graphics.Color

// Material 3 Base Palette - Light
val PrimaryLight = Color(0xFF265EA7)
val OnPrimaryLight = Color(0xFFFFFFFF)
val PrimaryContainerLight = Color(0xFFD6E3FF)
val OnPrimaryContainerLight = Color(0xFF001B3E)

val SecondaryLight = Color(0xFF555F71)
val OnSecondaryLight = Color(0xFFFFFFFF)
val SecondaryContainerLight = Color(0xFFD9E3F8)
val OnSecondaryContainerLight = Color(0xFF121C2B)

val TertiaryLight = Color(0xFF6E5676)
val OnTertiaryLight = Color(0xFFFFFFFF)
val TertiaryContainerLight = Color(0xFFF7D8FF)
val OnTertiaryContainerLight = Color(0xFF271330)

val BackgroundLight = Color(0xFFFDFBFF)
val OnBackgroundLight = Color(0xFF1A1C1E)
val SurfaceLight = Color(0xFFFDFBFF)
val OnSurfaceLight = Color(0xFF1A1C1E)
val SurfaceVariantLight = Color(0xFFE0E2EC)
val OnSurfaceVariantLight = Color(0xFF44474E)
val OutlineLight = Color(0xFF74777F)

// Material 3 Base Palette - Dark
val PrimaryDark = Color(0xFFA9C7FF)
val OnPrimaryDark = Color(0xFF003063)
val PrimaryContainerDark = Color(0xFF00468C)
val OnPrimaryContainerDark = Color(0xFFD6E3FF)

val SecondaryDark = Color(0xFFBDC7DC)
val OnSecondaryDark = Color(0xFF283141)
val SecondaryContainerDark = Color(0xFF3E4758)
val OnSecondaryContainerDark = Color(0xFFD9E3F8)

val TertiaryDark = Color(0xFFDBBCE2)
val OnTertiaryDark = Color(0xFF3D2846)
val TertiaryContainerDark = Color(0xFF553E5D)
val OnTertiaryContainerDark = Color(0xFFF7D8FF)

val BackgroundDark = Color(0xFF1A1C1E)
val OnBackgroundDark = Color(0xFFE3E2E6)
val SurfaceDark = Color(0xFF1A1C1E)
val OnSurfaceDark = Color(0xFFE3E2E6)
val SurfaceVariantDark = Color(0xFF44474E)
val OnSurfaceVariantDark = Color(0xFFC4C6D0)
val OutlineDark = Color(0xFF8E9099)

// 8 Preset Warna Cover Buku (specs/ui-ux.md)
data class BookCoverColor(
    val id: String,
    val name: String,
    val hex: String,
    val color: Color
)

val BookCoverColors = listOf(
    BookCoverColor("COLOR-01", "Deep Indigo", "#3F51B5", Color(0xFF3F51B5)),
    BookCoverColor("COLOR-02", "Forest Emerald", "#2E7D32", Color(0xFF2E7D32)),
    BookCoverColor("COLOR-03", "Warm Amber", "#F57C00", Color(0xFFF57C00)),
    BookCoverColor("COLOR-04", "Crimson Ruby", "#C2185B", Color(0xFFC2185B)),
    BookCoverColor("COLOR-05", "Ocean Teal", "#00897B", Color(0xFF00897B)),
    BookCoverColor("COLOR-06", "Royal Violet", "#7B1FA2", Color(0xFF7B1FA2)),
    BookCoverColor("COLOR-07", "Steel Slate", "#455A64", Color(0xFF455A64)),
    BookCoverColor("COLOR-08", "Earth Brown", "#5D4037", Color(0xFF5D4037))
)

val DefaultBookCoverColor = BookCoverColors.first()
