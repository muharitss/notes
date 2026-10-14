# Spesifikasi Penyimpanan Lokal & Durabilitas Data (Local Storage Spec)
**Nama Proyek:** Buku Catatan Digital (Android Native Kotlin)  
**Versi Dokumen:** 1.0.0  
**Basis Teknologi:** SQLite via Android Room Persistence Library  

---

## 1. Arsitektur Basis Data Lokal

Aplikasi menggunakan **Room Database** versi 2.6+ yang berjalan di atas engine SQLite bawaan Android. Database ini bersifat *single-instance* (Singleton) yang diinisialisasi pada saat aplikasi pertama kali dijalankan.

### 1.1 Lokasi Penyimpanan Fisik
- File basis data disimpan di direktori internal privat aplikasi:
  `/data/data/com.notes.digital/databases/notes_app.db`
- File pendukung SQLite Write-Ahead Logging (WAL):
  `notes_app.db-wal` dan `notes_app.db-shm`
- Mode Jurnal: **Write-Ahead Logging (WAL)** diaktifkan secara default oleh Room untuk memungkinkan pembacaan berkecepatan tinggi secara bersamaan dengan penulisan background.

---

## 2. Definisi DAO & Query Reaktif

### 2.1 `BookDao`
```kotlin
package com.notes.digital.core.database.dao

import androidx.room.*
import com.notes.digital.core.database.entity.BookEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {
    @Query("""
        SELECT b.*, COUNT(n.id) as noteCount 
        FROM books b 
        LEFT JOIN notes n ON b.id = n.book_id 
        GROUP BY b.id 
        ORDER BY b.updated_at DESC
    """)
    fun getAllBooksWithCount(): Flow<List<BookWithCountTuple>>

    @Query("SELECT * FROM books WHERE id = :bookId LIMIT 1")
    fun getBookById(bookId: Long): Flow<BookEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateBook(book: BookEntity): Long

    @Query("DELETE FROM books WHERE id = :bookId")
    suspend fun deleteBookById(bookId: Long)
}

data class BookWithCountTuple(
    @Embedded val book: BookEntity,
    val noteCount: Int
)
```

### 2.2 `NoteDao`
```kotlin
package com.notes.digital.core.database.dao

import androidx.room.*
import com.notes.digital.core.database.entity.NoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("""
        SELECT * FROM notes 
        WHERE book_id = :bookId 
        ORDER BY is_pinned DESC, updated_at DESC
    """)
    fun getNotesByBookDescending(bookId: Long): Flow<List<NoteEntity>>

    @Query("""
        SELECT * FROM notes 
        WHERE book_id = :bookId 
        ORDER BY is_pinned DESC, created_at ASC
    """)
    fun getNotesByBookAscending(bookId: Long): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE id = :noteId LIMIT 1")
    fun getNoteById(noteId: Long): Flow<NoteEntity?>

    @Query("""
        SELECT * FROM notes 
        WHERE title LIKE '%' || :query || '%' 
           OR content LIKE '%' || :query || '%' 
        ORDER BY updated_at DESC
    """)
    fun searchNotesGlobal(query: String): Flow<List<NoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateNote(note: NoteEntity): Long

    @Query("DELETE FROM notes WHERE id = :noteId")
    suspend fun deleteNoteById(noteId: Long)

    @Query("UPDATE notes SET is_pinned = :isPinned WHERE id = :noteId")
    suspend fun updatePinStatus(noteId: Long, isPinned: Boolean)
}
```

---

## 3. Transaksi Atomik & Integritas Relasi

1. **Cascade Delete via SQLite Engine:**
   - Karena anotasi `@ForeignKey(..., onDelete = ForeignKey.CASCADE)` dikompilasi langsung menjadi klausul foreign key SQLite, operasi `DELETE FROM books WHERE id = ?` akan mengeksekusi penghapusan seluruh entitas `notes` terkait dalam level mesin SQLite tanpa beban iterasi di memori aplikasi.
2. **Transaksi Atomik (`@Transaction`):**
   - Setiap operasi yang mengubah lebih dari satu tabel atau query gabungan di Data Layer dibungkus dalam blok `@Transaction` atau fungsi `RoomDatabase.withTransaction {}`. Jika sistem mengalami *power loss* atau crash di tengah proses, database SQLite akan otomatis melakukan *rollback* ke kondisi konsisten terakhir.

---

## 4. Mekanisme Auto-Save & Mitigasi Risiko Kehilangan Data

Untuk memenuhi prinsip *Zero Data Loss*, editor menerapkan strategi proteksi dua lapis:

```
                      Pengetikan Pengguna
                              │
                              ▼
                State Input di ViewModel
                              │
            ┌─────────────────┴─────────────────┐
            │                                   │
   Debounce 500 ms                   Event Lifecycle / Keluar
 (Pengetikan Berhenti)               (onPause / Navigasi Mundur)
            │                                   │
            ▼                                   ▼
 [Eksekusi DAO: Upsert Note]         [Instant Flush: Upsert Note]
            │                                   │
            └─────────────────┬─────────────────┘
                              ▼
                 Database Room (Disk SQLite)
```

1. **Jendela Debouncing 500 ms:** Saat pengetikan aktif, perubahan disimpan ke memori dan penulisan ke disk ditunda hingga ada jeda 500 ms. Ini mencegah degradasi I/O disk ponsel akibat penulisan per karakter.
2. **Instant Flush saat Lifecycle onPause:** Melalui integrasi `DisposableEffect` di Compose dan `onCleared()` di ViewModel, draf yang belum ter-flush akan seketika ditulis ke disk saat aplikasi berpindah ke latar belakang atau ditutup.

---

## 5. Kebijakan Migrasi Basis Data (Database Migration Policy)

- **Versi Awal:** Database dimulai dari versi skema `1`.
- **Evolusi Skema:**
  - Jika terjadi penambahan kolom di rilis mendatang (misalnya kolom `tags` atau `is_archived`), sistem akan menggunakan fitur **Room AutoMigration** (`@AutoMigration(from = 1, to = 2)`).
  - Jika terjadi perubahan relasi destruktif, skrip `Migration(1, 2)` eksplisit wajib dibuat dan diuji dengan `MigrationTestHelper`.
  - **DILARANG** menggunakan `.fallbackToDestructiveMigration()` pada build produksi agar data pengguna tidak pernah terhapus saat pembaruan aplikasi.

---

## 6. Penanganan Kesalahan I/O (Error Handling)

- Operasi DAO dibungkus menggunakan try-catch di Repository Layer:
  - `SQLiteDiskIOException` (penyimpanan internal penuh): Ditangkap dan dilaporkan ke UI sebagai pesan ramah: *"Penyimpanan perangkat penuh. Kosongkan ruang untuk melanjutkan penyimpanan."*
  - `SQLiteConstraintException`: Mengindikasikan pelanggaran integritas data, otomatis menghasilkan log error terstruktur dan memicu penolakan simpan tanpa crash.
