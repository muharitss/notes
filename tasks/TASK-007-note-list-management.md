# TASK-007: Feature Note List in Notebook

## 1. ID Task
`TASK-007`

## 2. Nama & Tujuan
- **Nama:** Fitur Layar Detail Buku & Daftar Catatan Pertemuan.
- **Tujuan:** Menampilkan daftar catatan yang ada di dalam suatu buku terpilih, opsi pengurutan (terbaru/terlama), aksi pin catatan, dan pencarian internal buku.

## 3. Alasan Task Diperlukan
Pengguna membutuhkan tempat untuk melihat seluruh daftar pertemuan/bab yang terkait dengan buku tertentu sebelum membuka atau menambahkan catatan baru.

## 4. Referensi Requirement & Spesifikasi
- **Requirements:** FR-005, FR-006, FR-007, FR-010.
- **Spesifikasi:** [specs/functional-spec.md#alur-3-4](file:///home/alarm/notes/specs/functional-spec.md), [specs/ui-ux.md#33-layar-2-detail-buku-daftar-pertemuan---bookdetailscreen](file:///home/alarm/notes/specs/ui-ux.md).

## 5. Dependensi
- **Dependensi:** `TASK-004` (Data Repositories), `TASK-005` (Design System), `TASK-006` (Notebook Management).

## 6. Batasan Scope
- Menampilkan daftar catatan dalam buku, toggle pin, opsi sorting, dan navigasi ke `NoteEditor`.
- Tidak menangani pengetikan auto-save konten catatan (dialokasikan ke TASK-008).

## 7. Daftar Pekerjaan Spesifik
1. Buat kontrak MVI `BookDetailContract.kt`:
   - `BookDetailUiState`: `book: Book?`, `notes: List<Note>`, `sortOrder: SortOrder`, `searchQuery: String`.
   - `BookDetailUiEvent`: `OnTogglePinNote`, `OnSortOrderChanged`, `OnSearchQueryChanged`, `OnDeleteNote`, `OnNoteClicked`, `OnAddNoteClicked`.
2. Buat `BookDetailViewModel.kt` menerima `bookId` dari SavedStateHandle/Navigation.
3. Buat UI components:
   - `NoteListItem.kt`: Item list catatan dengan judul pertemuan, snippet isi, waktu modifikasi, dan pin indicator.
   - `SortOrderDropdown.kt`: Menu pilihan sortir catatan.
4. Buat `BookDetailScreen.kt` dengan `LazyColumn`.
5. Tulis unit test untuk verifikasi sorting dan pin note di ViewModel.

## 8. File / Komponen yang Diperkirakan Terdampak
- `app/src/main/java/com/notes/digital/feature/bookdetail/BookDetailContract.kt`
- `app/src/main/java/com/notes/digital/feature/bookdetail/BookDetailViewModel.kt`
- `app/src/main/java/com/notes/digital/feature/bookdetail/BookDetailScreen.kt`
- `app/src/main/java/com/notes/digital/feature/bookdetail/components/NoteListItem.kt`
- `app/src/test/java/com/notes/digital/feature/bookdetail/BookDetailViewModelTest.kt`

## 9. Acceptance Criteria
- [ ] Catatan yang disematkan (*is_pinned*) otomatis berpindah ke urutan teratas.
- [ ] Pengguna dapat beralih antara urutan "Terbaru Dimodifikasi" dan "Terlama Dibuat".
- [ ] Mengetuk FAB "Catatan Baru" menavigasi ke `NoteEditor(bookId, noteId = -1L)`.
- [ ] Mengetuk item catatan menavigasi ke `NoteEditor(bookId, noteId = note.id)`.

## 10. Skenario Pengujian
- Unit test pengurutan catatan (Lolos `TEST-003`).
- Unit test emisi state saat toggle pin.

## 11. Langkah Verifikasi
1. Jalankan `./gradlew testDebugUnitTest --tests "*.feature.bookdetail.*"`.
2. Pastikan logika sorting dan pin teruji.

## 12. Risiko & Catatan Implementasi
- Pastikan pencarian internal buku hanya memfilter catatan yang memiliki `book_id` sesuai.

## 13. Checklist Pekerjaan
- [ ] Buat contract `BookDetailContract`
- [ ] Buat `BookDetailViewModel`
- [ ] Buat `NoteListItem` dan `SortOrderDropdown`
- [ ] Buat `BookDetailScreen`
- [ ] Tulis unit test
- [ ] Verifikasi acceptance criteria

## 14. Status Task
`NOT_STARTED`
