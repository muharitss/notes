# TASK-005: Design System & Navigation Base

## 1. ID Task
`TASK-005`

## 2. Nama & Tujuan
- **Nama:** Desain Sistem Material 3 & Kerangka Navigasi Type-Safe.
- **Tujuan:** Menyediakan komponen UI reusable, tema Material 3 (warna aksen, tipografi), dan kerangka navigasi Navigation Compose.

## 3. Alasan Task Diperlukan
Layar fitur membutuhkan fondasi visual terstandarisasi, palet warna sampul buku, dan rute navigasi sebelum UI spesifik dibangun.

## 4. Referensi Requirement & Spesifikasi
- **Requirements:** NFR-006, NFR-007.
- **Spesifikasi:** [specs/ui-ux.md](file:///home/alarm/notes/specs/ui-ux.md), [specs/architecture.md](file:///home/alarm/notes/specs/architecture.md).

## 5. Dependensi
- **Dependensi:** `TASK-001` (Project Setup & Baseline Tooling).

## 6. Batasan Scope
- Menyiapkan theme, typography, color palette (8 warna cover), komponen umum (`NoteTopAppBar`, `ConfirmationDialog`, `ColorPickerRow`, `EmptyStatePlaceholder`), dan deklarasi rute `Destination`.
- Tidak membuat logic ViewModel pada task ini.

## 7. Daftar Pekerjaan Spesifik
1. Buat tema Material 3 di `Theme.kt`, `Color.kt`, dan `Type.kt`.
2. Buat preset 8 warna cover buku sesuai spesifikasi UI/UX.
3. Buat reusable composables:
   - `NoteTopAppBar.kt`: TopBar dengan navigasi kembali dan menu aksi.
   - `ConfirmationDialog.kt`: Dialog konfirmasi tindakan destruktif.
   - `ColorPickerRow.kt`: Komponen pemilihan warna buku dengan touch target 48 dp.
   - `EmptyStatePlaceholder.kt`: Tampilan saat data kosong dengan ilustrasi dan teks.
4. Buat deklarasi rute navigasi `Destinations.kt` dan host navigasi `AppNavHost.kt`.

## 8. File / Komponen yang Diperkirakan Terdampak
- `app/src/main/java/com/notes/digital/core/designsystem/Theme.kt`
- `app/src/main/java/com/notes/digital/core/designsystem/Color.kt`
- `app/src/main/java/com/notes/digital/core/designsystem/components/NoteTopAppBar.kt`
- `app/src/main/java/com/notes/digital/core/designsystem/components/ConfirmationDialog.kt`
- `app/src/main/java/com/notes/digital/core/designsystem/components/EmptyStatePlaceholder.kt`
- `app/src/main/java/com/notes/digital/navigation/Destinations.kt`
- `app/src/main/java/com/notes/digital/navigation/AppNavHost.kt`

## 9. Acceptance Criteria
- [x] `@Preview` pada seluruh komponen reusable dapat di-render tanpa crash.
- [x] Touch target komponen interaktif minimal 48x48 dp.
- [x] Struktur rute type-safe terdefinisi untuk seluruh layar.

## 10. Skenario Pengujian
- Unit test serialisasi rute `Destination` dan validasi 8 warna palette (`DesignSystemTest`).
- Verifikasi kompilasi Preview dan build APK debug (`assembleDebug`).

## 11. Langkah Verifikasi
1. Jalankan unit test: `./gradlew testDebugUnitTest` -> Lolos (`BUILD SUCCESSFUL in 30s`).
2. Jalankan build debug APK: `./gradlew assembleDebug` -> Lolos (`BUILD SUCCESSFUL in 3m 53s`).
3. Seluruh komponen terkompilasi bersih tanpa peringatan deprecation.

## 12. Risiko & Catatan Implementasi
- Semua icon memiliki `contentDescription` eksplisit untuk aksesibilitas TalkBack.

## 13. Checklist Pekerjaan
- [x] Buat `Theme.kt`, `Color.kt`, `Type.kt`
- [x] Buat 4 komponen reusable
- [x] Definisikan rute `Destination`
- [x] Siapkan `AppNavHost`
- [x] Verifikasi render komponen

## 14. Status Task
`DONE`
