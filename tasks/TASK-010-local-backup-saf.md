# TASK-010: Utility Local Backup & Restore (JSON via SAF)

## 1. ID Task
`TASK-010`

## 2. Nama & Tujuan
- **Nama:** Utilitas Cadangan Lokal & Pemulihan Data (JSON via Storage Access Framework).
- **Tujuan:** Menyediakan mekanisme ekspor seluruh buku dan catatan ke file `.json` lokal di folder eksternal yang dipilih pengguna melalui Storage Access Framework (SAF), serta fitur pemulihan (*restore*).

## 3. Alasan Task Diperlukan
Karena aplikasi bersifat 100% offline dan data tersimpan di sandbox aplikasi, utilitas ekspor lokal memberikan jaminan rasa aman bagi pengguna dari risiko kehilangan data saat aplikasi di-uninstall atau berganti perangkat.

## 4. Referensi Requirement & Spesifikasi
- **Requirements:** FR-013, NFR-001, NFR-008.
- **Spesifikasi:** [specs/decisions.md#adr-009](file:///home/alarm/notes/specs/decisions.md#adr-009), [specs/local-storage.md](file:///home/alarm/notes/specs/local-storage.md).

## 5. Dependensi
- **Dependensi:** `TASK-004` (Data Repositories).

## 6. Batasan Scope
- Format file cadangan adalah JSON plain text terstruktur.
- Menggunakan Android Storage Access Framework (`ActivityResultContracts.CreateDocument` dan `OpenDocument`).
- Bebas dari koneksi internet atau cloud SDK apapun.

## 7. Daftar Pekerjaan Spesifik
1. Buat data transfer object (DTO) serialisasi: `BackupDataDto`, `BookDto`, `NoteDto`.
2. Buat `BackupRepository` dengan fungsi:
   - `exportBackupToJson(outputStream: OutputStream): AppResult<Unit>`
   - `importBackupFromJson(inputStream: InputStream): AppResult<Unit>`
3. Tambahkan menu aksi "Cadangkan Catatan" dan "Pulihkan Catatan" pada layar Beranda atau Dialog Pengaturan Sederhana.
4. Tulis unit test untuk memvalidasi serialisasi dan deserialisasi data cadangan JSON.

## 8. File / Komponen yang Diperkirakan Terdampak
- `app/src/main/java/com/notes/digital/data/backup/BackupManager.kt`
- `app/src/main/java/com/notes/digital/data/backup/dto/BackupDataDto.kt`
- `app/src/main/java/com/notes/digital/feature/booklist/components/BackupRestoreDialog.kt`
- `app/src/test/java/com/notes/digital/data/backup/BackupManagerTest.kt`

## 9. Acceptance Criteria
- [ ] Pengguna dapat mengekspor seluruh buku dan catatan menjadi file JSON ke direktori pilihan mereka.
- [ ] Pengguna dapat mengimpor kembali file cadangan JSON dan data berhasil dipulihkan secara utuh ke Room DB.
- [ ] File cadangan tidak memerlukan permission runtime berbahaya.

## 10. Skenario Pengujian
- Unit test serialisasi JSON terhadap data buku berelasi catatan.
- Integration test restore: import file JSON memulihkan baris database dengan tepat.

## 11. Langkah Verifikasi
1. Jalankan unit test backup: `./gradlew testDebugUnitTest --tests "*.data.backup.*"`.
2. Uji coba simpan file backup di perangkat dan pulihkan.

## 12. Risiko & Catatan Implementasi
- Pastikan proses import menangani konflik ID secara aman dengan melakukan insert sebagai data baru atau mengganti sesuai konfirmasi pengguna.

## 13. Checklist Pekerjaan
- [ ] Buat DTO dan parser JSON
- [ ] Buat `BackupManager`
- [ ] Integrasikan SAF File Picker di UI
- [ ] Tulis unit test serialisasi
- [ ] Verifikasi ekspor & impor berjalan sukses

## 14. Status Task
`NOT_STARTED`
