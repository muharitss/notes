# Model Data & Relasi Entitas (Data Model Spec)
**Nama Proyek:** Buku Catatan Digital (Android Native Kotlin)  
**Versi Dokumen:** 1.0.0  
**Status:** PROPOSED FOR REVIEW  

---

## 1. Diagram Relasi Entitas (Entity Relationship)

Aplikasi memiliki relasi data berhierarki tepat 2 tingkat: **Satu Buku memiliki Banyak Catatan (1 : N)**.

```
┌────────────────────────────────────────┐
│              books                     │
├────────────────────────────────────────┤
│ PK  id          : INTEGER (Auto-Inc)   │
│     title       : TEXT (NOT NULL)      │
│     description : TEXT (NULLABLE)      │
│     color_hex   : TEXT (NOT NULL)      │
│     created_at  : INTEGER (NOT NULL)   │
│     updated_at  : INTEGER (NOT NULL)   │
└────────────────────────────────────────┘
                    │
                    │ 1
                    │
                    │ N (ON DELETE CASCADE)
                    ▼
┌────────────────────────────────────────┐
│              notes                     │
├────────────────────────────────────────┤
│ PK  id          : INTEGER (Auto-Inc)   │
│ FK  book_id     : INTEGER (NOT NULL)   │
│     title       : TEXT (NOT NULL)      │
│     content     : TEXT (NOT NULL)      │
│     is_pinned   : INTEGER (DEFAULT 0)  │
│     created_at  : INTEGER (NOT NULL)   │
│     updated_at  : INTEGER (NOT NULL)   │
└────────────────────────────────────────┘
```

---

## 2. Definisi Entitas Room (Data Layer)

### 2.1 `BookEntity`
Representasi baris tabel buku pada database SQLite:
```kotlin
package com.notes.digital.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "books",
    indices = [
        Index(value = ["updated_at"], name = "idx_books_updated_at")
    ]
)
data class BookEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0L,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "description")
    val description: String? = null,

    @ColumnInfo(name = "color_hex")
    val colorHex: String,

    @ColumnInfo(name = "created_at")
    val createdAt: Long,

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long
)
```

### 2.2 `NoteEntity`
Representasi baris tabel catatan dengan referensi foreign key ke tabel buku:
```kotlin
package com.notes.digital.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "notes",
    foreignKeys = [
        ForeignKey(
            entity = BookEntity::class,
            parentColumns = ["id"],
            childColumns = ["book_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["book_id"], name = "idx_notes_book_id"),
        Index(value = ["updated_at"], name = "idx_notes_updated_at"),
        Index(value = ["is_pinned"], name = "idx_notes_is_pinned")
    ]
)
data class NoteEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0L,

    @ColumnInfo(name = "book_id")
    val bookId: Long,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "content")
    val content: String,

    @ColumnInfo(name = "is_pinned", defaultValue = "0")
    val isPinned: Boolean = false,

    @ColumnInfo(name = "created_at")
    val createdAt: Long,

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long
)
```

---

## 3. Definisi Model Domain (Domain Layer)

Model domain adalah kelas Kotlin murni (*Plain Old Kotlin Objects*) tanpa anotasi Room:

```kotlin
package com.notes.digital.domain.model

data class Book(
    val id: Long = 0L,
    val title: String,
    val description: String? = null,
    val colorHex: String,
    val noteCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

data class Note(
    val id: Long = 0L,
    val bookId: Long,
    val title: String,
    val content: String,
    val isPinned: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

data class BookWithNotes(
    val book: Book,
    val notes: List<Note>
)
```

---

## 4. Aturan Integritas & Perilaku Data (Data Rules)

1. **Identitas Data:**
   - Semua entitas menggunakan tipe `Long` sebagai Primary Key dengan mekanisme `autoGenerate = true`.
   - Nilai `id = 0L` menandakan entitas belum tersimpan di database.
2. **Integritas Referensial (Cascade Delete):**
   - SQLite secara ketat memberlakukan `ON DELETE CASCADE`. Ketika suatu buku dihapus, seluruh catatan anak yang memiliki `book_id` sama akan langsung dihapus oleh database engine secara atomik.
   - Tidak akan pernah ada catatan yatim piatu (*orphaned records*).
3. **Format Waktu (Timestamps):**
   - Semua waktu (`created_at` dan `updated_at`) disimpan dalam bilangan bulat **Epoch Milliseconds** (`System.currentTimeMillis()`).
   - Format waktu ini efisien untuk pengurutan numerik langsung dan mudah diformat ke locale lokal ("14 Okt 2026, 21:15") di Presentation Layer.
4. **Logika Pengurutan Catatan (Note Sorting):**
   - Pengurutan default diatur oleh klausul SQL:
     ```sql
     ORDER BY is_pinned DESC, updated_at DESC
     ```
   - Catatan yang disematkan (`is_pinned = 1`) selalu berada di atas, diikuti catatan non-pin yang paling baru dimodifikasi.
   - Opsi pengurutan sekunder: `created_at ASC` (untuk meninjau materi kelas secara kronologis pertemuan 1, 2, 3...).
