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
- [ ] Hasil pencarian bereaksi dalam < 200 ms setelah pengetikan berhenti.
- [ ] Mengetuk item hasil pencarian langsung membuka catatan di `NoteEditorScreen`.
- [ ] Query pencarian kosong menampilkan panduan awal pencarian.

## 10. Skenario Pengujian
- **TEST-007:** Filter query search validation test.

## 11. Langkah Verifikasi
1. Jalankan unit test: `./gradlew testDebugUnitTest --tests "*.feature.search.*"`.
2. Uji coba pencarian berbagai kata kunci pada database lokal.

## 12. Risiko & Catatan Implementasi
- Lindungi karakter khusus SQL (seperti `%` dan `_`) agar query LIKE tidak menghasilkan kesalahan pola pencarian.

## 13. Checklist Pekerjaan
- [ ] Buat contract `GlobalSearchContract`
- [ ] Buat `GlobalSearchViewModel`
- [ ] Buat `GlobalSearchScreen`
- [ ] Tulis unit test
- [ ] Verifikasi acceptance criteria

## 14. Status Task
`NOT_STARTED`
