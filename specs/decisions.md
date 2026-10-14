# Catatan Keputusan Arsitektur (Architectural Decision Records)
**Nama Proyek:** Buku Catatan Digital (Android Native Kotlin)  
**Versi Dokumen:** 1.0.0  

Dokumen ini mendokumentasikan seluruh pertimbangan teknologi, alasan pemilihan, alternatif yang dikaji, dampak implementasi, serta status persetujuannya sebelum fase implementasi dimulai.

---

## Daftar Rekapitulasi Keputusan (ADR Index)

| ID ADR | Topik Keputusan | Pilihan yang Diusulkan | Status |
| :--- | :--- | :--- | :---: |
| **ADR-001** | Platform & Bahasa | Android Native dengan 100% Kotlin | **APPROVED** |
| **ADR-002** | UI Toolkit | Jetpack Compose (Declarative UI) | **APPROVED** |
| **ADR-003** | Pendekatan Arsitektur | Layered Clean Architecture (Presentation, Domain, Data) | **PROPOSED** |
| **ADR-004** | Penyimpanan Terstruktur | Room Database (SQLite) | **APPROVED** |
| **ADR-005** | Asynchronous Processing | Kotlin Coroutines & Flow | **APPROVED** |
| **ADR-006** | State Management | Unidirectional Data Flow (UDF) dengan StateFlow | **PROPOSED** |
| **ADR-007** | Dependency Injection | Manual DI (AppContainer) atau Koin Ringan | **PROPOSED** |
| **ADR-008** | Format Konten & Editor | Plain Text dengan Natural Linebreaks (MVP) | **PROPOSED** |
| **ADR-009** | Mitigasi Kehilangan Data | Ekspor/Impor File Cadangan Lokal via SAF | **PROPOSED** |

---

## Rincian Keputusan Arsitektur

### ADR-001: Platform & Bahasa Pemrograman
1. **Keputusan:** Menggunakan platform **Android Native** dengan bahasa pemrograman **Kotlin 2.0+**.
2. **Alasan:**
   - Menyediakan akses langsung dan presisi ke siklus hidup Android (*Lifecycle*), manajemen memori hemat daya, dan performa tinggi pada penyimpanan lokal.
   - Kotlin memiliki fitur null-safety bawaan, coroutines native, dan dukungan resmi kelas satu dari Google.
3. **Alternatif yang Dipertimbangkan:**
   - *Flutter / React Native:* Ditolak karena menambah overhead runtime engine, ukuran APK membengkak, dan tidak memberikan manfaat tambahan untuk aplikasi lokal sederhana yang tidak memerlukan multi-platform.
   - *Java:* Ditolak karena terlalu banyak boilerplate, tidak mendukung deklarasi Compose modern secara optimal, dan minim fitur expressive type-system.
4. **Dampak terhadap Implementasi:** Minimum SDK ditetapkan pada API 24 (Android 7.0) dengan Target SDK 34/35.
5. **Status:** `APPROVED`.

---

### ADR-002: UI Toolkit (Jetpack Compose)
1. **Keputusan:** Menggunakan **Jetpack Compose** secara penuh tanpa XML Layouts atau Fragments.
2. **Alasan:**
   - Mengurangi baris kode UI hingga 60% dibanding sistem View XML lama.
   - Reaktif secara alami terhadap emisi `StateFlow`.
   - Menghilangkan siklus hidup Fragment yang kompleks dan rawan bug.
3. **Alternatif yang Dipertimbangkan:**
   - *Android XML Views + ViewBinding:* Ditolak karena memerlukan sinkronisasi manual state imperatif, banyak file XML terpisah, dan paradigma lama.
4. **Dampak terhadap Implementasi:** Seluruh navigasi antarmuka menggunakan Navigation Compose type-safe.
5. **Status:** `APPROVED`.

---

### ADR-003: Pendekatan Arsitektur Berlapis (Clean Architecture)
1. **Keputusan:** Menerapkan **3-Layer Clean Architecture proporsional** (`Presentation`, `Domain`, `Data`).
2. **Alasan:**
   - Logika bisnis (aturan judul catatan, perhitungan jumlah catatan, validasi form, debounce) berada di `Domain Layer` (Pure Kotlin) yang terisolasi dari framework Android, sehingga 100% dapat di-unit test secara kilat.
   - `Data Layer` murni menangani detail Room SQLite dan konversi Entity ke Domain Model.
3. **Alternatif yang Dipertimbangkan:**
   - *2-Layer Architecture (UI & Data langsung):* Sangat ringkas, tetapi ViewModel cenderung menumpuk logika format data dan filter, menyulitkan pengujian skenario kompleks seperti pengurutan catatan gabungan pin.
4. **Dampak terhadap Implementasi:** Memerlukan mapping data (`toDomain()` dan `toEntity()`) antar layer, namun menjaga kode tetap bersih, decoupled, dan mudah dirawat.
5. **Status:** `PROPOSED` (Menunggu konfirmasi pengguna).

---

### ADR-004: Penyimpanan Terstruktur (Room Database)
1. **Keputusan:** Menggunakan **Room Persistence Library (SQLite)** sebagai basis penyimpanan lokal.
2. **Alasan:**
   - Pustaka resmi standar industri Android dari Google.
   - Verifikasi query SQL saat waktu kompilasi (*compile-time query verification*).
   - Mendukung operasi reaktif langsung via `Flow<List<T>>`.
   - Mendukung `ON DELETE CASCADE` di tingkat SQLite untuk membersihkan catatan saat buku dihapus secara atomik.
3. **Alternatif yang Dipertimbangkan:**
   - *Raw SQLiteOpenHelper:* Terlalu rawan kesalahan pengetikan string SQL dan memerlukan banyak boilerplate cursor mapping.
   - *SQLDelight:* Pilihan kuat, namun Room memiliki dokumentasi dan integrasi tooling Jetpack yang jauh lebih matang.
   - *Realm / ObjectBox:* Memerlukan engine native pihak ketiga yang berat dan meningkatkan ukuran APK.
4. **Dampak terhadap Implementasi:** Memerlukan plugin KSP (Kotlin Symbol Processing) pada Gradle build script.
5. **Status:** `APPROVED`.

---

### ADR-005: Pemrograman Asinkron (Kotlin Coroutines & Flow)
1. **Keputusan:** Menggunakan **Kotlin Coroutines** untuk operasi one-shot dan **Flow** untuk reactive stream.
2. **Alasan:**
   - Integrasi langsung dengan Room Database dan Jetpack ViewModel lifecycle.
   - Sangat ringan (*lightweight threads*) dan tidak memboroskan memori.
   - Structured concurrency mencegah terjadinya memory leak saat layar ditutup.
3. **Alternatif yang Dipertimbangkan:**
   - *RxJava 3:* Terlalu kompleks dan kurva belajar tinggi untuk aplikasi offline sederhana.
   - *LiveData:* Merupakan teknologi lama Google yang sudah mulai digantikan oleh `StateFlow` di era Jetpack Compose.
4. **Dampak terhadap Implementasi:** Room DAO akan mengembalikan `Flow<List<Entity>>` untuk query reaktif, dan `suspend fun` untuk penulisan data.
5. **Status:** `APPROVED`.

---

### ADR-006: State Management (Unidirectional Data Flow / UDF)
1. **Keputusan:** Menggunakan pola **Unidirectional Data Flow (UDF)** dengan `StateFlow` immutable dan event berbasis `sealed interface`.
2. **Alasan:**
   - Satu-satunya sumber kebenaran (*Single Source of Truth*) untuk kondisi UI.
   - Mencegah *race condition* saat operasi auto-save berjalan bersamaan dengan input teks pengguna.
3. **Alternatif yang Dipertimbangkan:**
   - *Two-way Data Binding:* Rentan menghasilkan siklus perubahan state yang tidak terprediksi.
4. **Dampak terhadap Implementasi:** Setiap layar mendefinisikan `{Feature}UiState` dan `{Feature}UiEvent`.
5. **Status:** `PROPOSED`.

---

### ADR-007: Dependency Injection (DI Evaluation)
1. **Keputusan yang Diusulkan:** Menggunakan **Manual Dependency Injection berbasis `AppContainer`** atau **Koin** (Lightweight Service Locator), **bukan Dagger Hilt**.
2. **Alasan:**
   - Aplikasi ini beroperasi 100% offline-first, tanpa modul jaringan (Retrofit/OkHttp), tanpa flavor build multi-server yang kompleks.
   - Dagger Hilt menambahkan overhead build time yang signifikan karena proses KSP/kapt annotation processing yang berat.
   - `AppContainer` manual yang diinisialisasi di class `Application` hanya membutuhkan ~50 baris kode Kotlin murni, 100% transparan, zero build-overhead, dan sangat mudah di-mock pada unit testing.
3. **Alternatif yang Dipertimbangkan:**
   - *Dagger Hilt:* Sangat baik untuk enterprise app skala besar, namun over-engineered untuk aplikasi catatan lokal sederhana.
   - *Koin:* Pilihan menengah yang sangat nyaman jika pengguna menginginkan sintaks injeksi deklaratif seperti `koinViewModel()`.
4. **Dampak terhadap Implementasi:** Jika Manual DI disetujui, proyek bebas dari dependensi library DI eksternal. Jika Koin disetujui, hanya menambahkan satu runtime library kecil tanpa KSP.
5. **Status:** `PROPOSED` (Menunggu Keputusan Pengguna).

---

### ADR-008: Format Konten & Editor (Plain Text vs. Rich-Text)
1. **Keputusan yang Diusulkan:** Menggunakan **Plain Text murni dengan natural line-breaks** untuk MVP, disimpan sebagai tipe `TEXT` di SQLite.
2. **Alasan:**
   - Pengetikan instan dan ringan tanpa latensi rendering.
   - Menghilangkan bug parsing format teks atau korupsi tag HTML saat proses auto-save cepat (500 ms).
   - Tipe kolom `TEXT` di SQLite tetap 100% kompatibel jika di masa mendatang ditambahkan parser Markdown/Spannable.
3. **Alternatif yang Dipertimbangkan:**
   - *HTML / Spannable String:* Sangat rawan inkonsistensi saat copy-paste antar perangkat.
   - *Markdown WYSIWYG:* Menambah kompleksitas state editing dan potensi delay rendering pada catatan panjang di HP spesifikasi rendah.
4. **Dampak terhadap Implementasi:** UI editor menggunakan `BasicTextField` atau `OutlinedTextField` standar Material 3 dengan penataan tipografi yang nyaman.
5. **Status:** `PROPOSED`.

---

### ADR-009: Mitigasi Kehilangan Data Lokal (Backup Ekspor/Impor SAF)
1. **Keputusan yang Diusulkan:** Menyediakan utilitas **Ekspor Data ke File JSON** ke folder eksternal yang dipilih pengguna melalui Android Storage Access Framework (SAF).
2. **Alasan:**
   - Karena aplikasi bersifat local-only tanpa cloud, data di sandbox internal (`/data/data/...`) akan musnah jika pengguna menghapus aplikasi (*uninstall*).
   - Utilitas ekspor JSON memberikan kontrol penuh kepada pengguna untuk mencadangkan catatannya tanpa perlu koneksi internet atau server pihak ketiga.
3. **Alternatif yang Dipertimbangkan:**
   - *Google Drive Backup API:* Ditolak karena melanggar prinsip anti-cloud dan membutuhkan akun Google.
   - *Menulis langsung ke External Storage publik:* Ditolak karena dibatasi oleh Scoped Storage Android 11+ dan membutuhkan permission berisiko. SAF adalah standar resmi Android modern.
4. **Dampak terhadap Implementasi:** Direncanakan sebagai task utilitas mandiri setelah fitur inti selesai.
5. **Status:** `PROPOSED`.
