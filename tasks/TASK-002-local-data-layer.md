# TASK-002: Local Data Layer (Room Database & DAOs)

## 1. ID Task
`TASK-002`

## 2. Nama & Tujuan
- **Nama:** Pembangunan Lapisan Persistensi Room Database, Entities, dan DAOs.
- **Tujuan:** Menyediakan skema database SQLite lokal untuk tabel `books` dan `notes` dengan integritas foreign key cascade delete dan pengujian integrasi in-memory.

## 3. Alasan Task Diperlukan
Aplikasi membutuhkan fondasi penyimpanan lokal persisten sebelum repository dan antarmuka pengguna dapat menyimpan atau menampilkan data buku dan catatan.

## 4. Referensi Requirement & Spesifikasi
- **Requirements:** FR-001, FR-005, FR-012, NFR-002.
- **Spesifikasi:** [specs/data-model.md](file:///home/alarm/notes/specs/data-model.md), [specs/local-storage.md](file:///home/alarm/notes/specs/local-storage.md).

## 5. Dependensi
- **Dependensi:** `TASK-001` (Project Setup & Baseline Tooling).

## 6. Batasan Scope
- Hanya mencakup pembuatan entitas Room (`BookEntity`, `NoteEntity`), interface DAO (`BookDao`, `NoteDao`), dan database builder (`AppDatabase`).
- Tidak mencakup business logic domain atau UI Composable.

## 7. Daftar Pekerjaan Spesifik
1. Buat `BookEntity` dengan indeks pada `updated_at`.
2. Buat `NoteEntity` dengan indeks pada `book_id`, `updated_at`, dan foreign key cascade delete.
3. Buat `BookDao` dengan method query reaktif `getAllBooksWithCount()`, `insertOrUpdateBook()`, dan `deleteBookById()`.
4. Buat `NoteDao` dengan method query `getNotesByBookDescending()`, `getNotesByBookAscending()`, `searchNotesGlobal()`, `insertOrUpdateNote()`, dan `deleteNoteById()`.
5. Buat kelas abstrak `AppDatabase` turunan `RoomDatabase`.
6. Tulis integration test `BookDaoTest` dan `NoteDaoTest` menggunakan in-memory Room database.

## 8. File / Komponen yang Diperkirakan Terdampak
- `app/src/main/java/com/notes/digital/core/database/entity/BookEntity.kt`
- `app/src/main/java/com/notes/digital/core/database/entity/NoteEntity.kt`
- `app/src/main/java/com/notes/digital/core/database/dao/BookDao.kt`
- `app/src/main/java/com/notes/digital/core/database/dao/NoteDao.kt`
- `app/src/main/java/com/notes/digital/core/database/AppDatabase.kt`
- `app/src/androidTest/java/com/notes/digital/core/database/DatabaseIntegrationTest.kt`

## 9. Acceptance Criteria
- [ ] Database Room berhasil dibuat dengan versi skema `1`.
- [ ] Menghapus `BookEntity` otomatis menghapus seluruh `NoteEntity` anak melalui mekanisme cascade delete (Lolos `TEST-001`).
- [ ] Query Room mengembalikan `Flow` yang bereaksi instan saat data disisipkan atau diubah (Lolos `TEST-002`).

## 10. Skenario Pengujian
- **TEST-001:** Foreign key cascade delete test.
- **TEST-002:** Reactive Flow emission test.
- **TEST-003:** Note sorting ordering test (`is_pinned DESC, updated_at DESC`).

## 11. Langkah Verifikasi
1. Jalankan `./gradlew connectedCheck` atau test unit in-memory.
2. Pastikan seluruh pengujian DAO lolos tanpa error constraint violation.

## 12. Risiko & Catatan Implementasi
- Pastikan Room compiler KSP terpasang dengan benar agar code generation DAO tidak gagal.

## 13. Checklist Pekerjaan
- [ ] Buat `BookEntity` dan `NoteEntity`
- [ ] Buat `BookDao` dan `NoteDao`
- [ ] Buat `AppDatabase`
- [ ] Buat dan eksekusi Database Integration Tests
- [ ] Pastikan cascade delete berfungsi

## 14. Status Task
`NOT_STARTED`
