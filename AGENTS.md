# Instruksi Kerja Agen AI (AI Agent Working Instructions)
**Nama Proyek:** Buku Catatan Digital (Android Native Kotlin)  
**File Kanonis:** `AGENTS.md` *(Huruf kapital dipilih sebagai nama kanonis untuk konsistensi dengan standar perkakas modern seperti `README.md` dan konvensi AI coding assistants).*  
**Status Dokumen:** PERMANENT OPERATING PROCEDURE  
**Target Pembaca:** Seluruh AI Agent dan LLM yang berinteraksi dengan codebase proyek ini.

---

## 1. Ringkasan Proyek & Referensi Dokumentasi

Aplikasi ini adalah **Buku Catatan Digital** berbasis Android native murni (Kotlin & Jetpack Compose), beroperasi **100% offline-first dan local-only** tanpa backend, server, akun, atau layanan eksternal. Struktur data terdiri dari 2 tingkat: **Buku (Notebook)** ➔ **Catatan (Notes/Meetings)**.

Dokumentasi wajib yang menjadi sumber kebenaran tunggal (*Single Source of Truth*):
- [prd.md](file:///home/alarm/notes/prd.md) — Persyaratan produk, batasan scope, dan acceptance criteria.
- [specs/architecture.md](file:///home/alarm/notes/specs/architecture.md) — Arsitektur teknis, layering, dan pemisahan tanggung jawab.
- [specs/data-model.md](file:///home/alarm/notes/specs/data-model.md) — Model data, ERD, dan kontrak entitas.
- [specs/local-storage.md](file:///home/alarm/notes/specs/local-storage.md) — Persistensi Room, SQLite, transaksi, dan migrasi.
- [specs/functional-spec.md](file:///home/alarm/notes/specs/functional-spec.md) — Spesifikasi fungsional setiap alur interaksi.
- [specs/ui-ux.md](file:///home/alarm/notes/specs/ui-ux.md) — Panduan desain UI Material 3, tema warna, dan layout.
- [specs/testing-strategy.md](file:///home/alarm/notes/specs/testing-strategy.md) — Piramida dan matriks pengujian.
- [specs/decisions.md](file:///home/alarm/notes/specs/decisions.md) — Catatan keputusan arsitektur (ADR).
- [tasks/README.md](file:///home/alarm/notes/tasks/README.md) — Pusat pelacak status progres seluruh task.

---

## 2. Urutan Membaca Dokumen Sebelum Bekerja

Setiap kali Anda (AI Agent) memulai sesi baru atau menerima instruksi kerja:
1. **Langkah 1:** Baca file [AGENTS.md](file:///home/alarm/notes/AGENTS.md) ini untuk memahami batasan dan aturan operasional.
2. **Langkah 2:** Baca [tasks/README.md](file:///home/alarm/notes/tasks/README.md) untuk melihat fase aktif, task yang sedang berjalan, dan task berikutnya yang memenuhi dependensi.
3. **Langkah 3:** Baca file spesifikasi terkait di direktori `specs/` yang relevan dengan task yang akan dikerjakan.
4. **Langkah 4:** Buka file deskripsi task spesifik di dalam folder `tasks/` (contoh: `tasks/TASK-001-project-setup.md`) dan pahami Acceptance Criteria serta Verification Steps sebelum menulis baris kode apapun.

---

## 3. Kepatuhan Spesifikasi (Spec-Driven Rule)

- Seluruh implementasi harus **100% tunduk pada spesifikasi yang telah disetujui**.
- Jika Anda menemukan skenario yang belum diatur dalam spesifikasi, **JANGAN** mengambil keputusan sepihak atau berasumsi. Catat sebagai pertanyaan terbuka, laporkan kepada pengguna, dan tunggu instruksi.
- Dilarang menambahkan fitur "bonus" di luar scope PRD meskipun fitur tersebut umum pada aplikasi catatan lain.

---

## 4. Standar Bahasa Kotlin & Android Native

1. **Target Toolchain:** Kotlin 2.0+ dengan target Android SDK 34/35 dan minimum SDK 24 (Android 7.0 Nougat).
2. **Gaya Penulisan:** Wajib mematuhi [Official Kotlin Style Guide](https://developer.android.com/kotlin/style-guide).
3. **Immutability Pertama:**
   - Gunakan `val` untuk semua deklarasi properti dan variabel lokal, kecuali secara eksplisit memerlukan mutasi internal yang terukur.
   - Gunakan tipe koleksi read-only (`List<T>`, `Set<T>`, `Map<K, V>`).
4. **Strict Typing (Larangan Penggunaan `Any`):**
   - **DILARANG KERAS** menggunakan tipe data `Any`, `Object`, atau raw collection tanpa tipe generik yang jelas sebagai jalan pintas. Seluruh payload data, state, event, dan argumen navigasi wajib memiliki tipe data eksplisit (*type-safe*).
5. **Null-Safety:**
   - Dilarang menggunakan operator penegasan null `!!` (*not-null assertion operator*). Gunakan `?.`, `?:`, `let`, atau konstruksi aman lainnya.

---

## 5. Arsitektur Berlapis & Batas Tanggung Jawab (Layering Boundaries)

Kode diorganisasi dalam 3 layer utama:

```
┌─────────────────────────────────────────────────────────────────┐
│ PRESENTATION LAYER (UI & ViewModel)                             │
│ Tanggung Jawab:                                                 │
│ - Menampilkan data via Composable Functions.                     │
│ - Mengubah user action menjadi UiEvent menuju ViewModel.         │
│ - Mengonsumsi UiState immutable (StateFlow).                    │
│ Larangan: Tidak boleh mengakses Room DAO atau Entity database    │
│ secara langsung. Wajib melalui Domain UseCase / Repository.     │
├─────────────────────────────────────────────────────────────────┤
│ DOMAIN LAYER (Pure Kotlin Business Logic)                       │
│ Tanggung Jawab:                                                 │
│ - Menyimpan model bisnis murni (Book, Note).                    │
│ - Mendefinisikan kontrak interface Repository.                  │
│ - Menjalankan UseCase interaksi spesifik (misal validasi input).│
│ Larangan: Bebas 100% dari dependensi Android framework (Context,│
│ Room, Intent, dsb).                                             │
├─────────────────────────────────────────────────────────────────┤
│ DATA LAYER (Persistence & Mapping)                              │
│ Tanggung Jawab:                                                 │
│ - Mengelola Room Database, Entities, dan DAOs.                  │
│ - Mengimplementasikan Repository Interface dari Domain Layer.   │
│ - Melakukan mapping Entity <──> Domain Model.                   │
│ Larangan: Tidak boleh mengekspos Entity Room ke Presentation UI.│
└─────────────────────────────────────────────────────────────────┘
```

---

## 6. Aturan Penamaan (Naming Conventions)

- **Class & Interface:** PascalCase (`BookRepository`, `NoteEditorViewModel`).
- **Function & Property:** camelCase (`getNotesByBookId()`, `isPinned`).
- **Composable Functions:** PascalCase kata benda atau frasa kata benda (`BookCard`, `NoteEditorTopBar`).
- **UiState & UiEvent:** `{Feature}UiState` dan `{Feature}UiEvent` bertipe `sealed interface` atau `data class`.
- **Database Entity:** Akhiran `Entity` dalam PascalCase (`BookEntity`, `NoteEntity`).
- **Database Table & Column:** snake_case (`books`, `created_at`).
- **Resource (Drawable, String, Color):** snake_case dengan prefix jelas (`ic_book.xml`, `btn_save_label`).

---

## 7. Prinsip SOLID & Clean Code Proporsional

- Terapkan **Single Responsibility Principle (SRP)**: Setiap class, UseCase, dan Composable hanya memiliki satu alasan untuk berubah.
- Hindari *overengineering*: Jangan membuat abstraksi atau factory jika sebuah class konkrit sederhana sudah mencukupi kebutuhan lokal.
- Jangan membuat pattern multi-modul yang terlalu rumit di awal; gunakan struktur package modular yang bersih dalam satu module `app`.

---

## 8. Aturan Error Handling, Coroutines, & State Management

1. **Dispatcher Execution:**
   - Semua operasi database Room dan I/O disk **wajib** dijalankan pada `Dispatchers.IO`.
   - Transformasi data berat dijalankan pada `Dispatchers.Default`.
   - Update UI dijalankan pada `Dispatchers.Main.immediate`.
2. **Error Wrapping:**
   - Gunakan wrapper hasil operasi yang jelas (`Result<T>` atau sealed class `AppResult<T>`).
   - Jangan pernah membiarkan SQLite Exception meluncur tanpa penanganan hingga menyebabkan *app crash*.
3. **State Management:**
   - Gunakan **Unidirectional Data Flow (UDF)**.
   - ViewModel mengekspos state sebagai `StateFlow<UiState>`.
   - Composable mengonsumsi state menggunakan `collectAsStateWithLifecycle()`.

---

## 9. Aturan Penyimpanan Lokal & Durabilitas Data

1. **Auto-Save Debounce & Flush:**
   - Editor catatan wajib menerapkan debounce **500 ms** saat pengguna mengetik untuk menghindari spam penulisan SQLite.
   - Editor wajib memicu penyimpanan instan (*flush*) tanpa debounce saat event `onPause` atau ketika pengguna bernavigasi mundur.
2. **Atomic Cascade Delete:**
   - Penghapusan relasi buku ke catatan wajib menggunakan `ON DELETE CASCADE` di level SQLite/Room, dan dibungkus dalam `@Transaction`.

---

## 10. Standar UI & Aksesibilitas (a11y)

1. **Touch Target Size:** Minimal `48.dp x 48.dp` untuk semua elemen yang dapat diklik (tombol, ikon, item list).
2. **Screen Reader (TalkBack):** Setiap `Icon` dan `IconButton` wajib memiliki `contentDescription` deskriptif dalam Bahasa Indonesia yang wajar. Dilarang menggunakan `contentDescription = null` kecuali untuk elemen murni dekoratif.
3. **Kontras & Keterbacaan:** Mengikuti standar Material 3 Dynamic / Custom Theme dengan rasio kontras teks minimal 4.5:1.

---

## 11. Standar Testing (Pengujian)

1. Setiap fitur baru wajib disertai pengujian otomatis:
   - **Unit Tests:** Menguji UseCase dan ViewModel dengan `Turbine` dan `kotlinx-coroutines-test`.
   - **Database Integration Tests:** Menguji DAO dan relasi cascade menggunakan in-memory Room SQLite.
2. Dilarang keras memalsukan hasil pengujian atau menandai task lulus jika pengujian belum dieksekusi secara nyata di terminal.

---

## 12. Aturan Dependensi & Pustaka Pihak Ketiga

- Hanya gunakan library resmi dari ekosistem Google Android Jetpack / Kotlinx resmi (Compose, Room, Coroutines, Lifecycle, Navigation).
- Jangan memasukkan dependensi pihak ketiga yang tidak esensial tanpa evaluasi tertulis pada `specs/decisions.md`.
- Dilarang memasukkan library jaringan (Retrofit, OkHttp, Ktor-client) atau cloud SDK karena aplikasi ini strictly local-only.

---

## 13. Keamanan & Privasi Data

- Tidak boleh ada data yang dikirim ke jaringan luar.
- Jangan pernah meminta izin `android.permission.INTERNET`.
- Simpan file database secara privat di direktori internal aplikasi.

---

## 14. Prosedur Pengerjaan Task (Strict Single-Task Workflow)

Saat masuk ke fase implementasi:
1. **Satu Task dalam Satu Waktu:** AI Agent hanya diizinkan mengerjakan tepat **1 task** yang memiliki status `NOT_STARTED` dan dependensinya sudah terpenuhi.
2. **Ubah Status ke `IN_PROGRESS`:** Perbarui file task dan `tasks/README.md`.
3. **Tulis & Verifikasi Kode:** Tulis kode sesuai rincian task, jalankan unit test atau build di terminal.
4. **Verifikasi Terhadap Acceptance Criteria:** Periksa setiap butir kriteria penerimaan.
5. **Ubah Status ke `DONE`:** Catat ringkasan pekerjaan, bukti pengujian, dan tanggal penyelesaian.
6. **Sinkronisasi `tasks/README.md`:** Perbarui persentase dan daftar progres secara atomik.

---

## 15. Larangan Keras bagi AI Agent (Strict Guardrails)

> [!CAUTION]
> 1. **DILARANG MULAI CODING** sebelum dokumen persiapan (PRD, Specs, Tasks) mendapatkan persetujuan eksplisit dari pengguna.
> 2. **DILARANG MENGUBAH SCOPE** atau menghapus fungsionalitas PRD secara sepihak tanpa konfirmasi pengguna.
> 3. **DILARANG MENANDAI TASK SELESAI** jika kode belum terkompilasi bersih dan pengujian belum diverifikasi berhasil.
> 4. **DILARANG MEMBUAT DUA FILE DUPLIKAT** untuk instruksi agen; gunakan `AGENTS.md` sebagai satu-satunya nama kanonis.
> 5. **DILARANG MEMBUAT PERUBAHAN DESTRUKTIF** terhadap file proyek yang sudah ada tanpa justifikasi jelas.
