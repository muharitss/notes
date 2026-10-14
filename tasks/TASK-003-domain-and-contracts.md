# TASK-003: Domain Models, Contracts & Use Cases

## 1. ID Task
`TASK-003`

## 2. Nama & Tujuan
- **Nama:** Pembuatan Model Domain Murni, Kontrak Interface Repository, dan Use Cases Bisnis.
- **Tujuan:** Mengisolasi logika bisnis aplikasi ke dalam domain layer (Pure Kotlin) yang terbebas dari dependensi framework Android atau database.

## 3. Alasan Task Diperlukan
Diperlukan untuk memvalidasi aturan bisnis (validasi judul, format nama, pengurutan, hasil operasi) dan mendefinisikan kontrak interface yang akan diimplementasikan oleh Data Layer.

## 4. Referensi Requirement & Spesifikasi
- **Requirements:** FR-001 s/d FR-012.
- **Spesifikasi:** [specs/data-model.md](file:///home/alarm/notes/specs/data-model.md), [specs/architecture.md](file:///home/alarm/notes/specs/architecture.md).

## 5. Dependensi
- **Dependensi:** `TASK-001` (Project Setup & Baseline Tooling).

## 6. Batasan Scope
- Tidak boleh mengimpor kelas dari package `android.*` atau `androidx.room.*`.
- Model domain murni data class immutability.

## 7. Daftar Pekerjaan Spesifik
1. Buat domain models: `Book.kt`, `Note.kt`, `BookWithNotes.kt`.
2. Buat wrapper hasil operasi: `AppResult.kt` (Success, Error).
3. Buat kontrak interface repository:
   - `BookRepository.kt`
   - `NoteRepository.kt`
4. Buat Use Cases bisnis:
   - `GetBooksUseCase`, `SaveBookUseCase`, `DeleteBookUseCase`.
   - `GetNotesByBookUseCase`, `SaveNoteUseCase`, `DeleteNoteUseCase`, `TogglePinNoteUseCase`, `SearchNotesUseCase`.
5. Tulis unit test untuk validasi judul pada `SaveBookUseCase` dan `SaveNoteUseCase`.

## 8. File / Komponen yang Diperkirakan Terdampak
- `app/src/main/java/com/notes/digital/domain/model/Book.kt`
- `app/src/main/java/com/notes/digital/domain/model/Note.kt`
- `app/src/main/java/com/notes/digital/domain/repository/BookRepository.kt`
- `app/src/main/java/com/notes/digital/domain/repository/NoteRepository.kt`
- `app/src/main/java/com/notes/digital/domain/usecase/book/SaveBookUseCase.kt`
- `app/src/main/java/com/notes/digital/domain/usecase/note/SaveNoteUseCase.kt`
- `app/src/test/java/com/notes/digital/domain/usecase/SaveBookUseCaseTest.kt`

## 9. Acceptance Criteria
- [x] Domain models murni tanpa dependensi Android SDK.
- [x] `SaveBookUseCase` menolak penyimpanan buku jika judul kosong atau hanya spasi (Lolos `TEST-004`).
- [x] 100% unit test domain layer berjalan cepat di JVM lokal.

## 10. Skenario Pengujian
- **TEST-004:** Title validation in UseCase unit test.

## 11. Langkah Verifikasi
1. Jalankan `./gradlew testDebugUnitTest --tests "*.domain.*"`.
2. Seluruh pengujian unit domain lolos dalam hitungan milidetik.

## 12. Risiko & Catatan Implementasi
- Pastikan tidak ada class Android Context yang bocor ke dalam UseCase.

## 13. Checklist Pekerjaan
- [x] Buat domain models
- [x] Buat repository interfaces
- [x] Buat seluruh Use Cases
- [x] Tulis unit test UseCase
- [x] Verifikasi tes unit lolos

## 14. Status Task
`DONE`

## 15. Ringkasan Eksekusi & Bukti Pengujian
- **Tanggal Penyelesaian:** 2026-10-15
- **Komponen Dibuat:**
  - `Book.kt`, `Note.kt`, `BookWithNotes.kt` (Pure Kotlin domain models)
  - `AppResult.kt` (Operation result wrapper)
  - `BookRepository.kt`, `NoteRepository.kt` (Domain repository interfaces)
  - `GetBooksUseCase.kt`, `SaveBookUseCase.kt`, `DeleteBookUseCase.kt`
  - `GetNotesByBookUseCase.kt`, `SaveNoteUseCase.kt`, `DeleteNoteUseCase.kt`, `TogglePinNoteUseCase.kt`, `SearchNotesUseCase.kt`
  - `SaveBookUseCaseTest.kt` (TEST-004), `SaveNoteUseCaseTest.kt`, `SearchNotesUseCaseTest.kt` (TEST-007)
- **Bukti Build & Pengujian:**
  - `./gradlew testDebugUnitTest` -> `BUILD SUCCESSFUL in 31s` (Semua unit test lolos).

