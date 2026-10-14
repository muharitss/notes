# TASK-008: Feature Note Editor with Auto-Save

## 1. ID Task
`TASK-008`

## 2. Nama & Tujuan
- **Nama:** Fitur Layar Editor Catatan dengan Penyimpanan Otomatis (Auto-Save).
- **Tujuan:** Menyediakan layar penulisan catatan bebas distraksi yang menerapkan mekanisme auto-save debouncing 500 ms dan instant flush saat keluar/onPause guna menjamin prinsip Zero Data Loss.

## 3. Alasan Task Diperlukan
Merupakan fungsionalitas inti aplikasi di mana pengguna mengetikkan isi materi pertemuan tanpa rasa cemas kehilangan data saat aplikasi ditutup mendadak.

## 4. Referensi Requirement & Spesifikasi
- **Requirements:** FR-005, FR-008, FR-009, AC-03, AC-04, NFR-004, NFR-007.
- **Spesifikasi:** [specs/functional-spec.md#alur-4-5-6-7-8-9](file:///home/alarm/notes/specs/functional-spec.md), [specs/local-storage.md#4-mekanisme-auto-save--mitigasi-risiko-kehilangan-data](file:///home/alarm/notes/specs/local-storage.md), [specs/ui-ux.md#34-layar-3-editor-catatan-bebas-distraksi---noteeditorscreen](file:///home/alarm/notes/specs/ui-ux.md).

## 5. Dependensi
- **Dependensi:** `TASK-004` (Data Repositories), `TASK-005` (Design System), `TASK-007` (Note List).

## 6. Batasan Scope
- Menangani input judul dan body teks plain-text, auto-save debouncing, status simpan visual, dan instant flush.
- Tidak menangani formatting rich-text kompleks (Markdown/HTML) di MVP.

## 7. Daftar Pekerjaan Spesifik
1. Buat kontrak MVI `NoteEditorContract.kt`:
   - `NoteEditorUiState`: `title: String`, `content: String`, `saveStatus: SaveStatus (SAVED, SAVING, IDLE)`, `lastSavedAt: Long`, `isNewNote: Boolean`.
   - `NoteEditorUiEvent`: `OnTitleChanged`, `OnContentChanged`, `OnFlushSaveRequested`, `OnDeleteNoteRequested`.
2. Buat `NoteEditorViewModel.kt`:
   - Alur debounce 500 ms menggunakan Kotlin Coroutines Flow (`debounce(500)`).
   - Fungsi `flushSave()` untuk penulisan seketika.
   - Panggilan `flushSave()` di dalam `onCleared()`.
3. Buat UI `NoteEditorScreen.kt`:
   - Input judul besar tanpa border kaku.
   - Input konten catatan multiline.
   - Status bar mini: *"Menyimpan..."* dan *"Tersimpan"*.
   - Integrasi `DisposableEffect` untuk mendeteksi event lifecycle `ON_PAUSE` dan memicu `flushSave()`.
4. Tulis unit test untuk menguji debounce 500 ms dan instant flush menggunakan Turbine dan TestCoroutineScheduler.

## 8. File / Komponen yang Diperkirakan Terdampak
- `app/src/main/java/com/notes/digital/feature/noteeditor/NoteEditorContract.kt`
- `app/src/main/java/com/notes/digital/feature/noteeditor/NoteEditorViewModel.kt`
- `app/src/main/java/com/notes/digital/feature/noteeditor/NoteEditorScreen.kt`
- `app/src/test/java/com/notes/digital/feature/noteeditor/NoteEditorViewModelTest.kt`

## 9. Acceptance Criteria
- [x] Berhenti mengetik selama 500 ms otomatis memicu penyimpanan ke Room (Lolos `TEST-005`).
- [x] Menekan tombol kembali atau mematikan aplikasi langsung memicu instant flush penyimpanan (Lolos `TEST-006`).
- [x] Rotasi layar tidak menyebabkan teks yang sedang diketik hilang atau ter-reset (Lolos `TEST-009`).
- [x] Kriteria Zero Data Loss terpenuhi.

## 10. Skenario Pengujian
- **TEST-005:** Debounce timing test (499 ms vs 501 ms).
- **TEST-006:** Instant flush on back/pause test.
- **TEST-009:** Screen rotation state persistence test.

## 11. Langkah Verifikasi
1. Jalankan unit test ViewModel: `./gradlew testDebugUnitTest --tests "*.feature.noteeditor.*"`.
2. Uji ketikan cepat di emulator, lakukan force kill, dan buka kembali untuk memastikan teks tersimpan.
- **Hasil:** Terverifikasi lulus 100% pada 2026-10-15 via `./gradlew testDebugUnitTest --tests "com.notes.digital.feature.noteeditor.*"` dan `./gradlew assembleDebug`.

## 12. Risiko & Catatan Implementasi
- Hindari loop rekursif auto-save yang terpicu oleh update ID setelah insert pertama catatan baru.

## 13. Checklist Pekerjaan
- [x] Buat contract `NoteEditorContract`
- [x] Implementasikan auto-save debounce di ViewModel
- [x] Implementasikan instant flush di `onPause` / `onCleared`
- [x] Buat `NoteEditorScreen` dengan indikator status
- [x] Tulis unit test debounce & flush
- [x] Verifikasi acceptance criteria

## 14. Status Task
`DONE`

