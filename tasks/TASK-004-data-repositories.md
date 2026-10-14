# TASK-004: Data Layer Repositories & Mappers

## 1. ID Task
`TASK-004`

## 2. Nama & Tujuan
- **Nama:** Implementasi Repository & Mapper Konversi Data.
- **Tujuan:** Menjembatani Room DAO ke domain repository interface melalui fungsi pemetaan (mapping) yang aman dan mengalokasikan eksekusi database ke `Dispatchers.IO`.

## 3. Alasan Task Diperlukan
Presenter/ViewModel membutuhkan sumber data konkret yang mengimplementasikan interface domain tanpa harus mengetahui detail teknis SQLite.

## 4. Referensi Requirement & Spesifikasi
- **Requirements:** FR-001 s/d FR-012, NFR-002, NFR-003.
- **Spesifikasi:** [specs/architecture.md](file:///home/alarm/notes/specs/architecture.md), [specs/local-storage.md](file:///home/alarm/notes/specs/local-storage.md).

## 5. Dependensi
- **Dependensi:** `TASK-002` (Local Data Layer), `TASK-003` (Domain Layer).

## 6. Batasan Scope
- Hanya mencakup implementasi `BookRepositoryImpl`, `NoteRepositoryImpl`, kelas mapper, dan kelas `AppContainer` (Manual DI).
- Tidak mencakup UI Compose.

## 7. Daftar Pekerjaan Spesifik
1. Buat ekstensi mapper:
   - `BookMapper.kt`: `BookEntity.toDomain()`, `Book.toEntity()`.
   - `NoteMapper.kt`: `NoteEntity.toDomain()`, `Note.toEntity()`.
2. Buat `BookRepositoryImpl.kt` mengimplementasikan `BookRepository`.
3. Buat `NoteRepositoryImpl.kt` mengimplementasikan `NoteRepository`.
4. Bungkus pemanggilan database di dalam coroutines `withContext(Dispatchers.IO)`.
5. Siapkan `AppContainer.kt` di modul core untuk inisialisasi singleton database dan repositori.
6. Tulis unit test untuk memverifikasi logika mapping data.

## 8. File / Komponen yang Diperkirakan Terdampak
- `app/src/main/java/com/notes/digital/data/mapper/BookMapper.kt`
- `app/src/main/java/com/notes/digital/data/mapper/NoteMapper.kt`
- `app/src/main/java/com/notes/digital/data/repository/BookRepositoryImpl.kt`
- `app/src/main/java/com/notes/digital/data/repository/NoteRepositoryImpl.kt`
- `app/src/main/java/com/notes/digital/core/di/AppContainer.kt`
- `app/src/test/java/com/notes/digital/data/mapper/MapperTest.kt`

## 9. Acceptance Criteria
- [ ] Data Entity dari Room terkonversi secara presisi ke Domain Model tanpa kehilangan data nilai ID atau timestamp.
- [ ] Operasi baca/tulis repository berjalan di `Dispatchers.IO`.
- [ ] `AppContainer` menyediakan instansiasi singleton repository yang siap diinjeksi ke ViewModel.

## 10. Skenario Pengujian
- Unit test mapping Entity ke Domain dan sebaliknya.
- Repository test menggunakan Fake DAO.

## 11. Langkah Verifikasi
1. Jalankan unit test mapper: `./gradlew testDebugUnitTest --tests "*.data.mapper.*"`.
2. Pastikan hasil test lolos.

## 12. Risiko & Catatan Implementasi
- Tangani nilai null pada deskripsi buku dengan aman saat mapping.

## 13. Checklist Pekerjaan
- [ ] Buat mapper Book & Note
- [ ] Implementasikan `BookRepositoryImpl`
- [ ] Implementasikan `NoteRepositoryImpl`
- [ ] Buat `AppContainer`
- [ ] Verifikasi test mapping lolos

## 14. Status Task
`NOT_STARTED`
