# Spesifikasi Desain UI / UX (UI/UX Spec)
**Nama Proyek:** Buku Catatan Digital (Android Native Kotlin)  
**Versi Dokumen:** 1.0.0  
**Desain Standar:** Material Design 3 (M3)  

---

## 1. Filosofi Desain Antarmuka

1. **Buku Catatan Fisik yang Modern:** Antarmuka membangkitkan keakraban buku tulis fisik melalui aksen warna sampul (*cover badge*), namun dengan interaksi digital yang mulus dan bebas gesekan.
2. **Distraction-Free Writing:** Layar editor catatan dirancang minim tombol dekoratif, memberikan ruang maksimal pada teks dan keyboard.
3. **Penyimpanan Transparan:** Status penyimpanan draf ditampilkan halus tanpa dialog pop-up yang mengganggu alur pikir pengguna.

---

## 2. Palet Warna Aksen Cover Buku (Palette Presets)

Pengguna dapat memilih salah satu dari 8 warna aksen Material 3 yang telah dikurasi agar kontras terhadap teks dan latar belakang:

| Kode Warna | Nama Warna | Hex Code | Rekomendasi Subjek / Topik |
| :--- | :--- | :--- | :--- |
| **COLOR-01** | Deep Indigo | `#3F51B5` | Matematika, Sains Terapan |
| **COLOR-02** | Forest Emerald | `#2E7D32` | IPA, Biologi, Lingkungan |
| **COLOR-03** | Warm Amber | `#F57C00` | Sejarah, Budaya |
| **COLOR-04** | Crimson Ruby | `#C2185B` | Bahasa Indonesia, Sastra |
| **COLOR-05** | Ocean Teal | `#00897B` | Bahasa Inggris, Bahasa Asing |
| **COLOR-06** | Royal Violet | `#7B1FA2` | Seni, Filsafat, Ide Kreatif |
| **COLOR-07** | Steel Slate | `#455A64` | Proyek Kerja, Finansial |
| **COLOR-08** | Earth Brown | `#5D4037` | Jurnal Pribadi, Log Harian |

---

## 3. Rincian Desain Layar Utama (Screen Layouts)

### 3.1 Layar 1: Beranda (Daftar Buku - `BookListScreen`)
- **TopBar:** Menampilkan judul aplikasi *"Buku Catatan"*, ikon Pencarian Global, dan tombol toggle tampilan (Grid 2 kolom / List 1 kolom).
- **Body:**
  - Jika kosong: Menampilkan ilustrasi buku dan teks ajakan membuat buku pertama.
  - Jika ada buku: Kartu `BookCard` dengan aksen warna di sisi kiri/atas, menampilkan:
    - Judul Buku (Heading Medium, tebal).
    - Deskripsi singkat (opsional, maks 2 baris).
    - Baris bawah: Ikon catatan + counter jumlah pertemuan (misal: *"6 Catatan"*), dan tanggal akses terakhir.
    - Ikon aksi titik tiga (opsi: Edit Metadata, Hapus Buku).
- **Floating Action Button (FAB):** Di pojok kanan bawah dengan ikon `+` untuk membuat buku baru.

### 3.2 Modal Form: Tambah / Edit Buku (`AddEditBookDialog`)
- Dialog Material 3 atau Modal BottomSheet.
- Field Judul Buku: `OutlinedTextField` dengan label *"Nama Buku (misal: Matematika)"*.
- Field Deskripsi: `OutlinedTextField` multiline (maks 3 baris) dengan label *"Deskripsi / Keterangan (opsional)"*.
- Selector Warna: Baris horizontal lingkaran warna (8 warna) dengan indikator centang pada warna yang aktif.
- Tombol aksi: "Batal" dan "Simpan". Tombol "Simpan" dinonaktifkan jika field judul kosong.

### 3.3 Layar 2: Detail Buku (Daftar Pertemuan - `BookDetailScreen`)
- **TopBar:**
  - Tombol Navigasi Kembali `<-`.
  - Judul Buku (dengan indikator warna cover).
  - Aksi: Ikon Filter/Sortir (Dropdown: Terbaru, Terlama, Alfabetis), dan Ikon Pencarian Buku.
- **Body:**
  - `LazyColumn` yang menampilkan daftar `NoteListItem`:
    - Kartu catatan dengan judul pertemuan (misal: *"Pertemuan 1 — Persamaan Linear"*).
    - Ringkasan isi catatan (maks 2 baris, terpotong dengan ellipsis `...`).
    - Waktu terakhir diedit.
    - Ikon pin di pojok kanan atas (warna aksen jika disematkan).
    - Opsi swipe atau menu untuk menghapus / menyematkan catatan.
- **FAB:** Tombol `+` bertuliskan *"Catatan Baru"*.

### 3.4 Layar 3: Editor Catatan Bebas Distraksi (`NoteEditorScreen`)
- **TopBar:**
  - Tombol Kembali `<-` (mengeklik langsung menyimpan dan keluar).
  - Teks status kecil di tengah/kanan:
    - *"Menyimpan..."* (saat proses debouncing/penulisan disk).
    - *"Tersimpan"* (berubah setelah penulisan selesai).
  - Menu aksi: Opsi Sematkan (*Pin*), dan Opsi Hapus Catatan.
- **Area Editor:**
  - Input Judul: `BasicTextField` berukuran besar (`headlineSmall`), placeholder *"Judul pertemuan..."*, single-line dengan action Next.
  - Garis pembatas tipis (*subtle divider*).
  - Input Konten: `BasicTextField` memenuhi sisa ruang layar, multiline, padding nyaman (16 dp), mendukung line breaks bebas tanpa batasan karakter.

### 3.5 Layar 4: Pencarian Global (`GlobalSearchScreen`)
- **Search Bar di bagian atas:** Otomatis fokus saat layar terbuka, dengan tombol hapus teks `x`.
- **Hasil Pencarian:** Menampilkan daftar kartu catatan yang cocok:
  - Badge nama buku asal di atas judul catatan.
  - Judul catatan dengan highlight kata kunci.
  - Snippet isi yang memuat kata yang dicari.
  - Mengetuk item langsung membuka `NoteEditorScreen` catatan tersebut.

---

## 4. Standar Aksesibilitas (Accessibility & Usability)

1. **Touch Target Size:** Seluruh tombol, icon button, dan selector warna memiliki bounding box minimal **48 dp x 48 dp**.
2. **Screen Reader (TalkBack):**
   - Setiap FAB dan IconButton memiliki `contentDescription` yang eksplisit:
     - Contoh: `"Tombol kembali"`, `"Buat catatan baru"`, `"Sematkan catatan"`, `"Pilih warna aksen Deep Indigo"`.
3. **Umpan Balik Visual:**
   - Penghapusan data penting selalu memicu konfirmasi dialog.
   - Aksi penghapusan menampilkan `Snackbar` dengan durasi standar.
