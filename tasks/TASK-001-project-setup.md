# TASK-001: Project Setup & Baseline Tooling

## 1. ID Task
`TASK-001`

## 2. Nama & Tujuan
- **Nama:** Inisialisasi Fondasi Proyek Android Native & Konfigurasi Build Tooling.
- **Tujuan:** Menyiapkan struktur proyek Android resmi dengan Gradle Kotlin DSL (`build.gradle.kts`), konfigurasi Jetpack Compose, KSP, Room, serta dependensi pengujian dasar.

## 3. Alasan Task Diperlukan
Proyek belum memiliki skrip build dasar dan pustaka Android Jetpack yang diperlukan untuk mengompilasi kode Kotlin native dan UI Compose.

## 4. Referensi Requirement & Spesifikasi
- **Requirements:** NFR-001, NFR-005.
- **Spesifikasi:** [specs/decisions.md#adr-001](file:///home/alarm/notes/specs/decisions.md#adr-001), [specs/architecture.md](file:///home/alarm/notes/specs/architecture.md).

## 5. Dependensi
- **Dependensi:** Tidak ada (Task awal).

## 6. Batasan Scope
- Hanya mencakup konfigurasi `settings.gradle.kts`, `build.gradle.kts`, `gradle/libs.versions.toml` (Version Catalog), `AndroidManifest.xml` dasar tanpa permission internet, dan paket root `com.notes.digital`.
- Dilarang membuat layar aplikasi atau entity database pada task ini.

## 7. Daftar Pekerjaan Spesifik
1. Siapkan Gradle Version Catalog (`libs.versions.toml`) dengan versi stabil:
   - Kotlin 2.0+, Android Gradle Plugin (AGP) 8.5+, KSP.
   - Compose BOM, Material 3, Navigation Compose, Room DB, Coroutines.
   - Test: JUnit, Turbine, Coroutines Test.
2. Konfigurasi `build.gradle.kts` (Project & App) dengan min SDK 24 dan target SDK 34/35.
3. Buat file `AndroidManifest.xml` minimalis tanpa permission `INTERNET`.
4. Siapkan file entry point `MainActivity.kt` kosong dengan tema Compose dasar.

## 8. File / Komponen yang Diperkirakan Terdampak
- `build.gradle.kts`
- `app/build.gradle.kts`
- `gradle/libs.versions.toml`
- `app/src/main/AndroidManifest.xml`
- `app/src/main/java/com/notes/digital/MainActivity.kt`

## 9. Acceptance Criteria
- [x] Perintah `./gradlew assembleDebug` berhasil dikompilasi tanpa error.
- [x] Perintah `./gradlew testDebugUnitTest` dapat dieksekusi tanpa error.
- [x] Tidak ada permission internet pada file manifest.

## 10. Skenario Pengujian
- Verifikasi build pipeline lokal dan eksekusi tes unit baseline berjalan sukses.

## 11. Langkah Verifikasi
1. Jalankan `./gradlew assembleDebug` di terminal -> `BUILD SUCCESSFUL` (menghasilkan `app/build/outputs/apk/debug/app-debug.apk`).
2. Jalankan `./gradlew testDebugUnitTest` di terminal -> `BUILD SUCCESSFUL` (seluruh unit test tereksekusi tanpa error).

## 12. Risiko & Catatan Implementasi
- Lingkungan Linux AArch64 dikonfigurasi menggunakan native AAPT2 override melalui `android.aapt2FromMavenOverride=/usr/local/bin/aapt2` di `gradle.properties`.

## 13. Checklist Pekerjaan
- [x] Konfigurasi `libs.versions.toml`
- [x] Konfigurasi `build.gradle.kts` (Project & App)
- [x] Konfigurasi manifest tanpa permission internet
- [x] Verifikasi build sukses di terminal (`assembleDebug` & `testDebugUnitTest`)

## 14. Status Task
`DONE`

## 15. Ringkasan Eksekusi & Bukti Pengujian
- **Tanggal Penyelesaian:** 2026-10-14
- **Bukti Build:** `./gradlew assembleDebug` menghasilkan status `BUILD SUCCESSFUL in 5m 26s`.
- **Bukti Unit Test:** `./gradlew testDebugUnitTest` menghasilkan status `BUILD SUCCESSFUL in 38s`.
- **Manifest:** Bersih tanpa `uses-permission`.

