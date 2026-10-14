# TASK-006: Feature Notebook Management (Dashboard)

## 1. ID Task
`TASK-006`

## 2. Nama & Tujuan
- **Nama:** Fitur Manajemen Buku (Dashboard / Home Screen).
- **Tujuan:** Membangun antarmuka beranda yang menampilkan daftar buku (Grid/List), formulir modal tambah/edit buku, dan penghapusan buku dengan cascade delete.

## 3. Alasan Task Diperlukan
Merupakan gerbang utama aplikasi di mana pengguna mengorganisasi topik atau mata pelajaran mereka dalam bentuk buku.

## 4. Referensi Requirement & Spesifikasi
- **Requirements:** FR-001, FR-002, FR-003, FR-004, FR-012, AC-01, AC-05.
- **Spesifikasi:** [specs/functional-spec.md#alur-1-2-10-11](file:///home/alarm/notes/specs/functional-spec.md), [specs/ui-ux.md#31-layar-1-beranda-daftar-buku---booklistscreen](file:///home/alarm/notes/specs/ui-ux.md).

## 5. Dependensi
- **Dependensi:** `TASK-004` (Data Layer), `TASK-005` (Design System & Nav).

## 6. Batasan Scope
- Menangani daftar buku, dialog input buku baru, dialog edit metadata buku, dialog konfirmasi hapus, dan navigasi saat kartu buku ditekan.
- Tidak menangani penulisan catatan di dalam buku (dialokasikan ke TASK-007/008).

## 7. Daftar Pekerjaan Spesifik
1. Buat kontrak MVI `BookListContract.kt`:
   - `BookListUiState`: `isLoading`, `books: List<Book>`, `isAddEditDialogOpen`, `selectedBookForEdit`, `isDeleteDialogOpen`.
   - `BookListUiEvent`: `OnAddBookClicked`, `OnSaveBook`, `OnDeleteBookConfirmed`, `OnBookClicked`, dll.
2. Buat `BookListViewModel.kt` yang mengonsumsi `GetBooksUseCase`, `SaveBookUseCase`, dan `DeleteBookUseCase`.
3. Buat UI components:
   - `BookCard.kt`: Kartu buku menampilkan judul, counter catatan, warna aksen, dan menu aksi.
   - `AddEditBookDialog.kt`: Modal dialog pembuat buku.
4. Buat `BookListScreen.kt` dengan dukungan toggle layout Grid dan List.
5. Tulis unit test untuk `BookListViewModel` menggunakan Turbine.

## 8. File / Komponen yang Diperkirakan Terdampak
- `app/src/main/java/com/notes/digital/feature/booklist/BookListContract.kt`
- `app/src/main/java/com/notes/digital/feature/booklist/BookListViewModel.kt`
- `app/src/main/java/com/notes/digital/feature/booklist/BookListScreen.kt`
- `app/src/main/java/com/notes/digital/feature/booklist/components/BookCard.kt`
- `app/src/main/java/com/notes/digital/feature/booklist/components/AddEditBookDialog.kt`
- `app/src/test/java/com/notes/digital/feature/booklist/BookListViewModelTest.kt`

## 9. Acceptance Criteria
- [ ] Pengguna dapat membuat buku baru dengan judul dan warna aksen cover.
- [ ] Kartu buku menampilkan jumlah catatan secara akurat.
- [ ] Menghapus buku memicu konfirmasi peringatan cascade delete.
- [ ] Mengetuk buku mengarahkan navigasi ke `BookDetail(bookId)`.

## 10. Skenario Pengujian
- Unit test perubahan state `BookListViewModel` saat menambah dan menghapus buku.
- UI test untuk validasi form tambah buku (tombol simpan disabled jika judul kosong).

## 11. Langkah Verifikasi
1. Jalankan unit test ViewModel: `./gradlew testDebugUnitTest --tests "*.feature.booklist.*"`.
2. Verifikasi interaksi di layar Android.

## 12. Risiko & Catatan Implementasi
- Pastikan dialog konfirmasi hapus menyebutkan konsekuensi cascade delete secara eksplisit.

## 13. Checklist Pekerjaan
- [ ] Buat contract `BookListContract`
- [ ] Buat `BookListViewModel`
- [ ] Buat `BookCard` dan `AddEditBookDialog`
- [ ] Buat `BookListScreen`
- [ ] Tulis unit test ViewModel
- [ ] Verifikasi seluruh acceptance criteria

## 14. Status Task
`NOT_STARTED`
