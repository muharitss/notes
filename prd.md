# Product Requirements Document (PRD)
**Nama Proyek:** Buku Catatan Digital (Digital Notebook)  
**Tipe Aplikasi:** Android Native (Kotlin)  
**Versi Dokumen:** 1.0.0  
**Status Dokumen:** PROPOSED FOR REVIEW  
**Tanggal:** 2026-10-14  

---

## 1. Ringkasan Produk (Executive Summary)
Buku Catatan Digital adalah aplikasi Android native yang menyediakan ruang kerja pencatatan terstruktur berbasis metafora buku catatan fisik. Pengguna dapat mengelompokkan catatan ke dalam buku-buku terpisah (seperti mata pelajaran, proyek, atau jurnal pribadi) dan menambahkan banyak entri catatan (seperti pertemuan kelas, log harian, atau ringkasan rapat) di dalam setiap buku. Aplikasi ini dirancang beroperasi **100% offline (local-only)**, tanpa kebutuhan server, akun, atau koneksi internet, dengan mengutamakan persistensi data yang andal dan antarmuka pengetikan yang bebas distraksi.

---

## 2. Latar Belakang & Masalah Pengguna (Problem Statement)
Pencatat digital saat ini dihadapkan pada dua permasalahan utama:
1. **Masalah Catatan Tanpa Struktur Alami (The Flat-List Problem):** Aplikasi pencatat cepat populer cenderung menumpuk seluruh catatan dalam satu layar beranda tanpa batasan konteks yang jelas. Bagi pengguna yang memiliki rutinitas teratur (misalnya pelajar dengan 6 mata pelajaran, atau profesional dengan multi-proyek), catatan cepat bercampur baur dan sulit dikelompokkan secara kronologis per topik.
2. **Masalah Kompleksitas Berlebih & Ketergantungan Cloud (Over-engineering Problem):** Aplikasi berfitur lengkap (seperti Notion, Obsidian, atau Evernote) menuntut kurva belajar tinggi, proses registrasi akun, sinkronisasi cloud yang lambat atau berbayar, serta antarmuka yang berat. Pengguna sering kali hanya membutuhkan tempat yang cepat, stabil, dan aman di perangkat lokal untuk mencatat materi pertemuan secara berkelanjutan.

---

## 3. Tujuan Produk & Indikator Keberhasilan (Goals & Success Metrics)

### 3.1 Tujuan Produk (Objectives)
- Menyediakan wadah pengorganisasian catatan 2-tingkat (`Buku` ➔ `Catatan`) yang alami dan intuitif.
- Menjamin data tersimpan secara atomik dan persisten di penyimpanan internal perangkat lokal.
- Memungkinkan pengguna melanjutkan catatan yang sedang aktif dalam maksimal 2 kali ketukan (*two-tap resume*).
- Menjaga aplikasi tetap ringan, responsif, dan hemat daya tanpa telemetri atau proses latar belakang yang tidak perlu.

### 3.2 Indikator Keberhasilan Teknis & Produk (Success Metrics)
- **Zero Data Loss Rate:** 0% insiden kehilangan data saat aplikasi ditutup paksa (*force close*) atau perangkat kehabisan daya saat pengetikan.
- **Cold App Launch Time:** Di bawah 600 ms pada perangkat target menengah (Android 10+).
- **Zero Runtime Permissions:** Aplikasi dapat berjalan 100% tanpa meminta izin berbahaya (*runtime permissions*) seperti internet, kontak, atau lokasi.
- **Frame Render Budget:** Mempertahankan 60/120 fps tanpa jank saat scrolling daftar catatan yang panjang.

---

## 4. Target Pengguna (Target Audience)
1. **Pelajar & Mahasiswa:**
   - Membutuhkan buku catatan terpisah per mata pelajaran/kuliah (Matematika, Bahasa Indonesia, Fisika, Sejarah, dll.).
   - Setiap mata pelajaran memiliki banyak catatan pertemuan bertanggal.
2. **Profesional & Freelancer:**
   - Membutuhkan buku catatan per proyek atau per klien.
   - Mengisi catatan per sesi meeting, log harian tugas, dan ringkasan keputusan.
3. **Pencatat Pribadi & Penulis:**
   - Menggunakan buku untuk jurnal harian, resep, catatan renungan, atau draf tulisan.

---

## 5. Skenario Penggunaan Utama (Core Scenarios)

### Skenario 1: Siswa Mengikuti Kelas Matematika
- Siswa membuka aplikasi sebelum pelajaran dimulai.
- Menekan kartu buku "Matematika".
- Menekan tombol tambah catatan baru. Catatan otomatis teridentifikasi untuk pertemuan baru.
- Siswa mencatat materi persamaan linear.
- Bel selesai berbunyi, siswa langsung menutup layar ponsel tanpa perlu mencari tombol simpan. Catatan tersimpan utuh.

### Skenario 2: Melanjutkan Catatan Bahasa Inggris Minggu Lalu
- Siswa membuka kembali buku "Bahasa Inggris".
- Membuka catatan "Pertemuan 2 — Simple Present Tense".
- Menambahkan latihan soal baru di bagian bawah catatan lama.
- Perubahan tersimpan secara atomik dengan waktu modifikasi terbarui.

---

## 6. User Stories

| ID Story | Sebagai... | Saya Ingin... | Sehingga... |
| :--- | :--- | :--- | :--- |
| **US-01** | Pengguna baru | Melihat tampilan beranda yang bersih dengan panduan membuat buku pertama | Saya paham cara kerja aplikasi tanpa perlu membaca instruksi rumit. |
| **US-02** | Pengguna | Membuat buku baru dengan judul dan aksen warna cover | Saya dapat membedakan kategori catatan secara visual dalam satu pandangan. |
| **US-03** | Pengguna | Membuka buku dan melihat seluruh daftar pertemuan di dalamnya secara berurutan | Saya mengetahui riwayat catatan yang sudah pernah saya buat. |
| **US-04** | Pengguna | Menulis catatan baru tanpa tombol simpan manual | Saya dapat fokus mencatat dengan cepat tanpa cemas lupa menyimpan draf. |
| **US-05** | Pengguna | Mengubah judul buku atau deskripsinya kapan saja | Struktur catatan tetap relevan ketika konteks belajar/proyek berubah. |
| **US-06** | Pengguna | Menyematkan (*pin*) catatan penting ke posisi teratas buku | Materi inti atau silabus dapat diakses seketika. |
| **US-07** | Pengguna | Mencari kata kunci tertentu di seluruh buku | Saya dapat menemukan materi lama tanpa harus membuka buku satu per satu. |
| **US-08** | Pengguna | Menghapus buku beserta seluruh catatannya setelah mendapat konfirmasi | Penyimpanan perangkat tetap rapi dari materi yang sudah tidak digunakan. |

---

## 7. Functional Requirements (Kebutuhan Fungsional)

Tabel kebutuhan fungsional dengan ID stabil:

| ID FR | Kategori | Deskripsi Kebutuhan | Klasifikasi |
| :--- | :--- | :--- | :--- |
| **FR-001** | Manajemen Buku | Sistem harus memungkinkan pembuatan buku baru dengan atribut: Judul (wajib), Deskripsi (opsional), dan Kode Warna Cover (pilihan palet). | Wajib (MVP) |
| **FR-002** | Manajemen Buku | Sistem harus menampilkan daftar buku di beranda dalam layout Grid atau List dengan info: Judul, Jumlah Catatan, dan Waktu Pembaruan Terakhir. | Wajib (MVP) |
| **FR-003** | Manajemen Buku | Sistem harus memungkinkan pengeditan metadata buku (judul, deskripsi, warna). | Wajib (MVP) |
| **FR-004** | Manajemen Buku | Sistem harus memungkinkan penghapusan buku dengan dialog konfirmasi eksplisit. | Wajib (MVP) |
| **FR-005** | Manajemen Catatan | Sistem harus memungkinkan pembuatan catatan baru di dalam buku terpilih dengan atribut: Judul Catatan dan Isi Catatan. | Wajib (MVP) |
| **FR-006** | Manajemen Catatan | Sistem harus menampilkan daftar catatan dalam buku dengan opsi pengurutan: Waktu Diperbarui Terbaru (default), Waktu Dibuat Terlama, dan Alfabetis Judul. | Wajib (MVP) |
| **FR-007** | Manajemen Catatan | Sistem harus memungkinkan pengguna menyematkan (*pin*) catatan agar selalu berada di posisi teratas daftar catatan buku. | Wajib (MVP) |
| **FR-008** | Editor Catatan | Sistem harus menjalankan mekanisme penyimpanan otomatis (*auto-save*) saat pengguna berhenti mengetik selama rentang waktu debouncing (500 ms). | Wajib (MVP) |
| **FR-009** | Editor Catatan | Sistem harus menjamin penyimpanan instan (*flush save*) saat pengguna menekan tombol kembali, berganti aplikasi, atau layar memasuki siklus `onPause`. | Wajib (MVP) |
| **FR-010** | Pencarian | Sistem harus menyediakan fitur pencarian teks instan pada judul dan isi catatan di dalam buku yang sedang dibuka. | Wajib (MVP) |
| **FR-011** | Pencarian | Sistem harus menyediakan fitur pencarian global di beranda untuk mencari catatan di seluruh buku yang ada. | Wajib (MVP) |
| **FR-012** | Integritas Data | Menghapus buku harus secara otomatis menghapus seluruh catatan di dalam buku tersebut secara atomik (*cascade delete*). | Wajib (MVP) |
| **FR-013** | Cadangan Lokal | Sistem menyediakan utilitas ekspor data catatan ke file JSON lokal melalui Android Storage Access Framework (SAF) dan impor kembali. | Fitur Tambahan (Post-MVP) |
| **FR-014** | Format Teks Kaya | Sistem mendukung penulisan teks tebal, miring, dan daftar butir (*bullet list*). | Fitur Tambahan (Post-MVP) |
| **FR-015** | Penguncian Aplikasi | Sistem menyediakan opsi kunci biometrik perangkat untuk membuka buku catatan pribadi. | Fitur Belum Disetujui |

---

## 8. Non-Functional Requirements (Kebutuhan Non-Fungsional)

| ID NFR | Kategori | Kriteria & Metrik Pengukuran |
| :--- | :--- | :--- |
| **NFR-001** | **Offline-First & Local-Only** | Aplikasi tidak memiliki dependensi jaringan apapun. Tidak ada permission `android.permission.INTERNET` di `AndroidManifest.xml`. |
| **NFR-002** | **Persistensi Data** | Data disimpan pada database SQLite lokal melalui Room. Transaksi batch dan penghapusan cascade dieksekusi secara atomik (`@Transaction`). |
| **NFR-003** | **Performa Rendering** | Tampilan daftar buku dan catatan harus mempertahankan frame rate 60 fps tanpa terjadi UI freeze saat scrolling. Komputasi berat dijadwalkan pada background thread (`Dispatchers.IO`). |
| **NFR-004** | **Efisiensi Memori & Daya** | Konsumsi memori heap aplikasi tidak boleh melebihi 60 MB saat membuka catatan berukuran 10.000 kata. Operasi disk dibatasi menggunakan debouncing untuk mencegah degradasi media penyimpanan dan pemborosan baterai. |
| **NFR-005** | **Kompatibilitas Platform** | Minimum SDK: Android 7.0 (API Level 24 / Nougat). Target SDK: Android 14+ (API Level 34/35). |
| **NFR-006** | **Aksesibilitas (a11y)** | Semua elemen interaktif memiliki touch target minimum 48x48 dp dan menyertakan `contentDescription` yang jelas bagi pembaca layar (TalkBack). Kontras warna teks memenuhi standar WCAG AA (rasio minimal 4.5:1). |
| **NFR-007** | **Stabilitas Lifecycle** | Aplikasi harus tahan terhadap perubahan konfigurasi (*configuration change* seperti rotasi layar atau beralih ke dark mode) tanpa mereset input teks atau status navigasi. |
| **NFR-008** | **Privasi & Keamanan Lokal** | Data aplikasi tersimpan di dalam sandbox direktori privat internal (`/data/data/com.notes.digital/databases/`) yang tidak dapat diakses oleh aplikasi lain pada perangkat non-root. |

---

## 9. Ruang Lingkup MVP (In-Scope vs. Explicitly Out-of-Scope)

### 9.1 Fitur Termasuk MVP (In-Scope)
1. Dashboard Buku (tampilan grid/list, counter jumlah catatan, status warna).
2. Pembuatan, Pengeditan, dan Penghapusan Buku.
3. Layar Daftar Catatan dalam Buku (sorting terbaru/terlama/nama, pin note).
4. Editor Catatan Sederhana dengan Auto-save berkala dan on-back.
5. Pencarian catatan lokal (dalam buku dan global).
6. Cascade delete buku dan catatan.
7. Konfirmasi dialog sebelum penghapusan data permanen.

### 9.2 Fitur yang Secara Eksplisit Dikeluarkan dari MVP (Out-of-Scope)
- ❌ Tidak ada backend, web server, REST API, atau GraphQL.
- ❌ Tidak ada autentikasi pengguna, registrasi, OTP, atau login Google.
- ❌ Tidak ada sinkronisasi cloud pihak ketiga (Firebase, Supabase, Google Drive, dsb.).
- ❌ Tidak ada attachment media (gambar kamera/galeri, audio, video, PDF).
- ❌ Tidak ada nesting buku multi-level (Buku di dalam Buku). Struktur dibatasi tepat 2 tingkat: Buku ➔ Catatan.
- ❌ Tidak ada kolaborasi multi-pengguna (*real-time sharing*).
- ❌ Tidak ada SDK pelacak pihak ketiga (Google Analytics, Mixpanel, Crashlytics).

---

## 10. Kriteria Penerimaan Terukur (Acceptance Criteria)

- **AC-01 (Pembuatan Buku):** Pengguna dapat membuat buku dengan mengisi judul minimal 1 karakter non-spasi. Buku langsung muncul di beranda dalam waktu < 100 ms.
- **AC-02 (Pembuatan Catatan):** Menekan tombol tambah catatan di dalam buku langsung membuka editor. Catatan baru otomatis terikat dengan ID buku bersangkutan.
- **AC-03 (Auto-Save):** Mengetik teks pada editor dan mendiamkannya selama 500 ms otomatis memicu penyimpanan ke database. Indikator visual berubah menampilkan status "Tersimpan".
- **AC-04 (Integritas Saat Force Close):** Saat aplikasi ditutup paksa melalui OS task manager di tengah pengeditan, teks terakhir yang diketik hingga sebelum jendela debounce 500 ms tidak boleh hilang saat aplikasi dibuka kembali.
- **AC-05 (Cascade Delete):** Menghapus sebuah buku yang berisi 10 catatan menghasilkan penghapusan 1 baris buku dan 10 baris catatan di database SQLite.
- **AC-06 (Pencarian Responsif):** Hasil pencarian kata kunci muncul dalam waktu < 200 ms setelah pengetikan selesai (debounce 300 ms).

---

## 11. Persyaratan Khusus Offline & Local-Only
- Seluruh dependensi aplikasi beroperasi mandiri tanpa perlu handshake ke server manapun.
- Aplikasi tidak menampilkan pesan kesalahan jaringan atau banner "No Internet Connection".
- Perangkat dalam mode pesawat (*Airplane Mode*) memiliki fungsionalitas 100% identik dengan perangkat yang memiliki koneksi internet aktif.

---

## 12. Aturan Pengelolaan Buku & Catatan (Domain Business Rules)
1. **Aturan Judul Buku:** Judul buku tidak boleh kosong atau hanya berupa spasi. Dua buku boleh memiliki judul yang sama (identifikasi mengandalkan Primary Key ID numerik), namun UI akan menampilkan dialog peringatan jika ditemukan kesamaan nama untuk kenyamanan pengguna.
2. **Aturan Judul Catatan:** Jika pengguna menyimpan catatan tanpa mengisi judul, sistem secara otomatis memberikan judul fallback: `"Catatan Tanpa Judul"` atau `"Pertemuan {N}"`.
3. **Aturan Pengurutan Default:**
   - Catatan yang di-pin (`is_pinned = true`) selalu ditampilkan di bagian paling atas.
   - Catatan non-pin diurutkan berdasarkan `updated_at DESC` (catatan yang baru saja dibuka/diedit berada di urutan teratas).
4. **Aturan Penghapusan:** Penghapusan buku atau catatan bersifat permanen untuk MVP (hard delete via SQLite CASCADE). Setiap aksi hapus wajib didahului modal dialog konfirmasi yang menegaskan bahwa tindakan tidak dapat dibatalkan.

---

## 13. Keamanan & Privasi Data Lokal
- **Data Isolation:** Mengandalkan mekanisme Android Linux User ID (UID) isolation. Data database hanya dapat dibaca oleh proses aplikasi itu sendiri.
- **No Clipboard Leakage:** Konten catatan tidak dikirim ke clipboard sistem kecuali pengguna secara eksplisit menekan aksi copy.
- **No Analytics:** Tidak ada data input pengetikan yang direkam untuk tujuan analitik.

---

## 14. Risiko, Asumsi, & Batasan (Risks, Assumptions, Constraints)

### 14.1 Risiko & Mitigasi
- **Risiko: Pengguna menghapus aplikasi (Uninstall Data Loss).**
  - *Dampak:* Karena aplikasi local-only, penghapusan aplikasi akan menghapus sandbox database SQLite oleh OS.
  - *Mitigasi:* Dokumentasikan batasan ini secara transparan pada aplikasi dan siapkan fitur ekspor cadangan lokal (JSON) pada roadmap lanjutan.
- **Risiko: Crash saat penulisan disk.**
  - *Mitigasi:* Gunakan transaksi atomik Room Database dan penanganan pengecualian `SQLiteDiskIOException` dengan fallback gracefully.

### 14.2 Asumsi
- Pengguna memiliki sisa memori penyimpanan internal minimal 20 MB.
- Pengguna menggunakan sistem operasi Android resmi (non-modified ROM yang tidak merusak SQLite standard).

### 14.3 Batasan
- Aplikasi tidak mendukung sinkronisasi antar perangkat berbeda (misal antara HP dan Tablet) untuk fase MVP.

---

## 15. Klasifikasi Kebutuhan & Status Fitur

| Status Klasifikasi | Daftar Fitur |
| :--- | :--- |
| **Kebutuhan Wajib (Approved MVP)** | CRUD Buku, CRUD Catatan, Grid/List View, Auto-save (500 ms debounce), Pin Catatan, Pencarian Lokal & Global, Cascade Delete, Konfirmasi Hapus. |
| **Fitur Tambahan (Post-MVP Candidate)** | Ekspor/Impor file JSON cadangan lokal via SAF, Tampilan Jumlah Kata/Karakter, Pemilihan palet warna custom. |
| **Fitur Belum Disetujui (Unapproved / Requires Input)** | Kunci Biometrik/PIN, Format Rich-Text / Markdown, Pengarsipan Buku (*Archive Book*). |

---

## 16. Pertanyaan Terbuka & Keputusan yang Menunggu Persetujuan
1. **Pemberian Nama Otomatis Catatan Baru:** Apakah catatan baru sebaiknya otomatis diberi nama `"Pertemuan {N+1}"` berdasarkan hitungan jumlah catatan di buku tersebut, atau dibiarkan kosong dengan hint placeholder?
2. **Kebutuhan Fitur Ekspor Cadangan Lokal di MVP:** Apakah fitur ekspor data catatan ke file teks/JSON di memori eksternal perlu dimasukkan ke dalam MVP untuk mengantisipasi risiko kehilangan data saat uninstall, atau cukup dialokasikan untuk fase pembaruan pertama?
