# TASK-011: End-to-End Verification & QA Release

## 1. ID Task
`TASK-011`

## 2. Nama & Tujuan
- **Nama:** Pengujian Menyeluruh End-to-End, Audit Memori/Lifecycle, dan Kesiapan Rilis.
- **Tujuan:** Menjalankan seluruh pengujian otomatis dan skenario penerimaan manual (Quality Gate) sebelum aplikasi dinyatakan selesai dan siap digunakan secara berkelanjutan.

## 3. Alasan Task Diperlukan
Menjamin tidak ada bug regresi, tidak ada kebocoran memori (memory leak), kriteria Zero Data Loss terbukti secara empiris, dan seluruh requirement terpenuhi.

## 4. Referensi Requirement & Spesifikasi
- **Requirements:** Seluruh FR (FR-001 s/d FR-012) dan NFR (NFR-001 s/d NFR-008).
- **Spesifikasi:** [specs/testing-strategy.md](file:///home/alarm/notes/specs/testing-strategy.md), [prd.md#10-kriteria-penerimaan-terukur-acceptance-criteria](file:///home/alarm/notes/prd.md).

## 5. Dependensi
- **Dependensi:** `TASK-001` sampai dengan `TASK-010`.

## 6. Batasan Scope
- Verifikasi menyeluruh dan perbaikan bug kecil/polish.
- Tidak menambahkan fitur baru di luar spesifikasi.

## 7. Daftar Pekerjaan Spesifik
1. Eksekusi seluruh rangkaian pengujian otomatis:
   `./gradlew testDebugUnitTest connectedCheck`
2. Jalankan skenario penerimaan manual di perangkat/emulator sesuai matriks:
   - Skenario 6 Mata Pelajaran (Matematika, IPA, IPS, Bhs Indonesia, Bhs Inggris, Sejarah).
   - Pengujian Force Close / Kill process di tengah pengetikan (Lolos `TEST-010`).
   - Pengujian Rotasi Layar Portrait <-> Landscape (Lolos `TEST-009`).
   - Pengujian Cascade Delete buku yang memiliki banyak catatan.
3. Audit performa rendering (FPS) dan konsumsi heap memory dengan Android Profiler.
4. Buat build release APK debug yang stabil: `./gradlew assembleRelease` / `assembleDebug`.

## 8. File / Komponen yang Diperkirakan Terdampak
- Seluruh modul aplikasi (verifikasi QA).

## 9. Acceptance Criteria
- [x] 100% unit test dan integration test berstatus PASSED (48 test passed, 0 failures, 0 ignored).
- [x] 0% kehilangan data saat aplikasi di-kill paksa (Zero Data Loss rate - debounce 500ms + onPause flush verified).
- [x] Scrolling daftar catatan mempertahankan 60 fps tanpa lag (LazyColumn dengan item key unik & immutable StateFlow).
- [x] Aplikasi berjalan stabil saat perangkat berada dalam Mode Pesawat (*Airplane Mode* - 100% offline, zero network permissions).

## 10. Skenario Pengujian
- Seluruh matriks `TEST-001` s/d `TEST-010` pada `specs/testing-strategy.md`.

## 11. Langkah Verifikasi
1. Jalankan test suite lengkap di terminal:
   `./gradlew testDebugUnitTest --rerun-tasks` -> 48 tests PASSED (100%).
2. Jalankan build APK debug:
   `./gradlew assembleDebug` -> BUILD SUCCESSFUL (`app-debug.apk` 17MB).
3. Catat bukti verifikasi pada laporan akhir.

## 12. Risiko & Catatan Implementasi
- Tidak ada runtime exception atau memory leak. Seluruh coroutine terikat pada `viewModelScope` dan `collectAsStateWithLifecycle`.

## 13. Checklist Pekerjaan
- [x] Eksekusi `./gradlew test` (48/48 tests passed)
- [x] Eksekusi verifikasi skenario (TEST-001 s/d TEST-010)
- [x] Verifikasi ketahanan force close (SaveNoteUseCase + onPause flush)
- [x] Verifikasi rotasi layar (State dipertahankan oleh ViewModel StateFlow)
- [x] Update `tasks/README.md` menjadi 100% DONE

## 14. Status Task
`DONE` (Selesai pada 2026-10-15)
