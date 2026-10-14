# TASK-009: Feature Local Search & Filtering

## 1. ID Task
`TASK-009`

## 2. Nama & Tujuan
- **Nama:** Fitur Pencarian Global & Pemfilteran Catatan Lintas Buku.
- **Tujuan:** Menyediakan layar pencarian mandiri yang menelusuri judul dan isi catatan di seluruh buku dengan debounce query 300 ms dan penyorotan nama buku induk.

## 3. Alasan Task Diperlukan
Pengguna yang memiliki banyak buku dan catatan membutuhkan cara cepat menemukan materi tertentu tanpa harus membuka buku satu per satu.

## 4. Referensi Requirement & Spesifikasi
- **Requirements:** FR-011, AC-06.
- **Spesifikasi:** [specs/functional-spec.md#alur-12](file:///home/alarm/notes/specs/functional-spec.md), [specs/ui-ux.md#35-layar-4-pencarian-global---globalsearchscreen](file:///home/alarm/notes/specs/ui-ux.md).

## 5. Dependensi
- **Dependensi:** `TASK-007` (Note List), `TASK-008` (Note Editor).

## 6. Batasan Scope
- Pencarian teks SQLite `LIKE` pada judul dan isi catatan.
- Menampilkan hasil dengan badge nama buku, dan membuka editor saat hasil ditekan.

## 7. Daftar Pekerjaan Spesifik
1. Buat kontrak MVI `GlobalSearchContract.kt`:
   - `GlobalSearchUiState`: `query: String`, `searchResults: List<SearchResultItem>`, `isSearching: Boolean`.
   - `GlobalSearchUiEvent`: `OnQueryChanged`, `OnClearQuery`, `OnResultClicked`.
2. Buat `GlobalSearchViewModel.kt` dengan debounce query 300 ms memanggil `SearchNotesUseCase`.
3. Buat UI `GlobalSearchScreen.kt`:
   - Search bar di bagian atas dengan tombol hapus `X`.
   - List hasil pencarian dengan badge nama buku induk.
   - Tampilan jika pencarian tidak menemukan hasil (*No results found*).
4. Tulis unit test untuk `GlobalSearchViewModel` (Lolos `TEST-007`).

## 8. File / Komponen yang Diperkirakan Terdampak
- `app/src/main/java/com/notes/digital/feature/search/GlobalSearchContract.kt`
- `app/src/main/java/com/notes/digital/feature/search/GlobalSearchViewModel.kt`
- `app/src/main/java/com/notes/digital/feature/search/GlobalSearchScreen.kt`
- `app/src/test/java/com/notes/digital/feature/search/GlobalSearchViewModelTest.kt`

## 9. Acceptance Criteria
- [x] Hasil pencarian bereaksi dalam < 200 ms setelah pengetikan berhenti.
- [x] Mengetuk item hasil pencarian langsung membuka catatan di `NoteEditorScreen`.
- [x] Query pencarian kosong menampilkan panduan awal pencarian.

## 10. Skenario Pengujian
- **TEST-007:** Filter query search validation test (Lulus melalui `SearchNotesUseCaseTest` & `GlobalSearchViewModelTest`).

## 11. Langkah Verifikasi
1. Jalankan unit test: `./gradlew testDebugUnitTest --tests "*.feature.search.*"` -> Lulus 100%.
2. Kompilasi APK: `./gradlew assembleDebug` -> Sukses.

## 12. Risiko & Catatan Implementasi
- Lindungi karakter khusus SQL (seperti `%` dan `_`) agar query LIKE tidak menghasilkan kesalahan pola pencarian.
- Query kosong langsung mengembalikan daftar kosong tanpa memicu pembacaan disk.

## 13. Checklist Pekerjaan
- [x] Buat contract `GlobalSearchContract`
- [x] Buat `GlobalSearchViewModel`
- [x] Buat `GlobalSearchScreen`
- [x] Tulis unit test
- [x] Verifikasi acceptance criteria

## 14. Status Task
`DONE`
- Tanggal Selesai: 2026-10-15
- Verifikasi: `./gradlew testDebugUnitTest` dan `./gradlew assembleDebug` berhasil tanpa error.
