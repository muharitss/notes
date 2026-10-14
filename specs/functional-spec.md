# Spesifikasi Fungsional & Alur Pengguna (Functional Spec)
**Nama Proyek:** Buku Catatan Digital (Android Native Kotlin)  
**Versi Dokumen:** 1.0.0  
**Status:** PROPOSED FOR REVIEW  

---

## 1. Analisis 12 Alur Utama Pengguna (Core MVP Flows)

### Alur 1: Pengguna Membuka Aplikasi untuk Pertama Kali
1. Sistem memeriksa database lokal.
2. Karena belum ada buku yang tersimpan, layar beranda menampilkan **Empty State** yang informatif dan ramah:
   - Ilustrasi visual buku terbuka.
   - Pesan: *"Belum ada buku catatan. Buat buku pertamamu untuk mulai mengorganisasi catatan pertemuan!"*
   - Tombol aksen utama (FAB) bertuliskan *"Buat Buku Pertama"*.

### Alur 2: Pengguna Membuat Buku Baru
1. Pengguna menekan tombol FAB tambah buku.
2. Tampil modal Bottom Sheet / Dialog:
   - Input judul buku (Wajib, misal: "Matematika", validasi: minimal 1 karakter non-spasi).
   - Input deskripsi buku (Opsional, misal: "Kelas X Semester 1").
   - Pilihan palet warna aksen cover (6 pilihan warna default Material 3).
3. Pengguna menekan "Simpan".
4. Buku tersimpan di database lokal, dialog menutup, dan buku langsung muncul di grid beranda.

### Alur 3: Pengguna Membuka Buku
1. Pengguna menekan kartu buku pada beranda.
2. Terjadi transisi navigasi menuju layar **Detail Buku**.
3. TopBar menampilkan judul buku dengan aksen warna cover yang dipilih.
4. Menampilkan daftar catatan di dalam buku tersebut (atau Empty State catatan jika buku baru).

### Alur 4: Pengguna Membuat Catatan Pertemuan Pertama
1. Di layar detail buku, pengguna menekan FAB "Catatan Baru".
2. Navigasi menuju layar **Editor Catatan**.
3. Fokus input secara otomatis diarahkan ke field judul catatan.
4. Pengguna mengetik judul: *"Pertemuan 1 — Persamaan Linear"*.
5. Pengguna berpindah ke area konten dan mencatat materi pembahasan.

### Alur 5: Pengguna Menyimpan Catatan (Auto-Save)
1. Selama pengguna mengetik, mekanisme debouncing 500 ms menghitung jeda waktu.
2. Ketika pengetikan terjeda selama 500 ms, sistem mengeksekusi operasi `upsert` ke database SQLite secara background.
3. Indikator status kecil di pojok kanan atas berubah sesaat dari *"Menyimpan..."* menjadi *"Tersimpan"*.
4. Pengguna tidak perlu menekan tombol simpan manual.

### Alur 6: Pengguna Keluar dan Kembali ke Aplikasi
1. Pengguna langsung menekan tombol Home ponsel atau menutup aplikasi via App Switcher saat berada di Editor.
2. Event lifecycle `onPause` memicu penyimpanan instan (*instant flush*) data teks yang tersisa di memory.
3. Saat aplikasi dibuka kembali di kemudian hari, database memuat data catatan terakhir tanpa kehilangan satu kata pun.

### Alur 7: Pengguna Membuka Kembali Catatan Lama
1. Pengguna membuka buku terkait, melihat daftar catatan yang diurutkan berdasarkan `updated_at DESC`.
2. Pengguna menekan item catatan lama.
3. Editor memuat judul dan isi catatan secara instan dari Room DB.

### Alur 8: Pengguna Menambahkan Catatan Pertemuan Berikutnya
1. Pengguna kembali ke buku yang sama.
2. Menekan tombol tambah catatan baru.
3. Mengetik *"Pertemuan 2 — Sistem Persamaan"* dan mengisi konten catatan baru.
4. Catatan tersimpan sebagai baris baru dengan ID unik, tanpa menimpa catatan Pertemuan 1.

### Alur 9: Pengguna Mengedit Catatan Lama
1. Pengguna membuka catatan Pertemuan 1.
2. Mengubah sebagian teks atau menambahkan rangkuman tambahan di bagian bawah.
3. Auto-save mendeteksi perbedaan teks, memperbarui konten dan kolom `updated_at`.
4. Saat kembali ke daftar catatan, catatan Pertemuan 1 otomatis naik ke posisi teratas (karena baru saja diperbarui).

### Alur 10: Pengguna Mengganti Nama Buku (Edit Metadata)
1. Pada layar Detail Buku atau opsi menu di kartu buku, pengguna memilih "Edit Buku".
2. Dialog terbuka dengan data awal buku terisi.
3. Pengguna mengubah judul atau warna buku, lalu menekan "Simpan".
4. Database memperbarui baris `BookEntity`. Semua catatan di dalamnya tetap utuh terhubung ke `book_id`.

### Alur 11: Pengguna Menghapus Buku atau Catatan
- **Kasus A: Menghapus Catatan Tunggal:**
  - Pengguna membuka menu catatan atau swipe ➔ Pilih "Hapus".
  - Tampil dialog konfirmasi: *"Hapus catatan ini secara permanen?"*.
  - Jika disetujui, catatan dihapus dari tabel `notes`.
- **Kasus B: Menghapus Buku:**
  - Pengguna memilih "Hapus Buku".
  - Tampil dialog konfirmasi peringatan tegas: *"Menghapus buku ini akan menghapus seluruh {N} catatan di dalamnya secara permanen. Tindakan ini tidak dapat dibatalkan."*.
  - Jika disetujui, SQLite menjalankan `DELETE FROM books WHERE id = ?` dan cascade menghapus semua catatan anak.

### Alur 12: Pengguna Menemukan Catatan yang Dibutuhkan
1. **Pencarian dalam Buku:** Input field pencarian di layar detail buku memfilter catatan pada buku aktif secara instan.
2. **Pencarian Global:** Ikon pencarian di layar beranda membuka layar pencarian global yang menelusuri seluruh buku, menampilkan judul catatan, snippet cuplikan teks, dan nama buku induknya.

---

## 2. Evaluasi Fitur: Rekomendasi MVP vs. Ditunda

| Fitur | Manfaat bagi Pengguna | Kompleksitas Teknis | Risiko | Rekomendasi |
| :--- | :--- | :--- | :--- | :---: |
| **Pencarian Teks Lokal** | Sangat Tinggi: Membantu menemukan materi lama dengan cepat. | Rendah: Menggunakan query SQLite `LIKE %query%` dengan debounce 300 ms. | Sangat Rendah | **MASUK MVP** |
| **Pengurutan (Sorting)** | Tinggi: Memungkinkan melihat urutan kronologis pertemuan (Asc) atau update terakhir (Desc). | Rendah: Cukup variasi klausul `ORDER BY` pada Room DAO. | Sangat Rendah | **MASUK MVP** |
| **Pin / Sematkan Catatan** | Tinggi: Menjaga silabus atau rumus penting selalu di atas. | Rendah: Flag boolean `is_pinned` pada database. | Sangat Rendah | **MASUK MVP** |
| **Pengarsipan Buku (Archive)** | Sedang: Menyembunyikan buku semester lalu tanpa menghapusnya. | Sedang: Perlu filter tambahan di semua query dan layar khusus arsip. | Rendah | **DITUNDA** (Post-MVP) |
| **Format Teks Kaya (Markdown/Rich Text)** | Sedang: Memberikan cetak tebal atau miring. | Tinggi: Menambah beban rendering, risiko glitch kursor saat auto-save cepat. | Sedang | **DITUNDA** (Post-MVP) |
| **Cadangan Lokal (Export JSON via SAF)** | Sangat Tinggi: Melindungi data jika pengguna uninstall aplikasi. | Sedang: Membutuhkan serialisasi JSON dan intent SAF. | Rendah | **DIUSULKAN** (Fitur Tambahan) |
