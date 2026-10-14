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
- [ ] 100% unit test dan integration test berstatus PASSED.
- [ ] 0% kehilangan data saat aplikasi di-kill paksa (Zero Data Loss rate).
- [ ] Scrolling daftar catatan mempertahankan 60 fps tanpa lag.
- [ ] Aplikasi berjalan stabil saat perangkat berada dalam Mode Pesawat (*Airplane Mode*).

## 10. Skenario Pengujian
- Seluruh matriks `TEST-001` s/d `TEST-010` pada `specs/testing-strategy.md`.

## 11. Langkah Verifikasi
1. Jalankan test suite lengkap di terminal.
2. Catat bukti verifikasi pada laporan akhir.

## 12. Risiko & Catatan Implementasi
- Pastikan tidak ada runtime exception yang terlewat pada logcat Android.

## 13. Checklist Pekerjaan
- [ ] Eksekusi `./gradlew test`
- [ ] Eksekusi verifikasi manual 12 skenario
- [ ] Verifikasi ketahanan force close
- [ ] Verifikasi rotasi layar
- [ ] Update `tasks/README.md` menjadi 100% DONE

## 14. Status Task
`NOT_STARTED`
