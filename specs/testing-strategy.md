# Strategi & Skenario Pengujian Kualitas (Testing Strategy Spec)
**Nama Proyek:** Buku Catatan Digital (Android Native Kotlin)  
**Versi Dokumen:** 1.0.0  
**Status:** PROPOSED FOR REVIEW  

---

## 1. Piramida & Komposisi Pengujian

Strategi pengujian difokuskan pada kecepatan eksekusi, stabilitas, dan pembuktian matematis terhadap kriteria *Zero Data Loss*:

```
          / \
         /   \      UI & End-to-End Tests (15%)
        /─────\     - Compose Test Rule, Semantics, Verifikasi Rotasi Layar
       /       \    
      /─────────\   Database Integration Tests (35%)
     /           \  - In-Memory Room SQLite, Cascade Delete, Mappers, Flow
    /─────────────\ 
   /               \ Unit Tests (50%)
  /─────────────────\ - ViewModels (Turbine), UseCases, Validasi Input
```

---

## 2. Perkakas & Pustaka Pengujian (Testing Tooling)

- **Unit Testing Core:** `JUnit 4` / `JUnit 5`
- **Coroutines Concurrency:** `kotlinx-coroutines-test` (`StandardTestDispatcher`, `runTest`, `advanceTimeBy`)
- **StateFlow & Flow Verification:** `Turbine` (memverifikasi emisi state secara sekuensial)
- **Database Testing:** `androidx.room:room-testing` (`Room.inMemoryDatabaseBuilder`)
- **UI & Semantics:** `androidx.compose.ui:ui-test-junit4` (`ComposeContentTestRule`)

---

## 3. Matriks Skenario Pengujian Resmi (Stable Test IDs)

Setiap skenario pengujian memiliki ID stabil yang dapat ditelusuri dari PRD dan Task Implementasi:

| ID Pengujian | Target Komponen | Deskripsi & Langkah Uji | Hasil yang Diharapkan | Tipe Uji |
| :--- | :--- | :--- | :--- | :---: |
| **TEST-001** | `BookDao` & `NoteDao` | Buat 1 `BookEntity` dan 3 `NoteEntity`. Jalankan `deleteBookById()`. Cek jumlah baris tabel `notes`. | Seluruh 3 catatan terhapus otomatis oleh SQLite engine (*Cascade Delete verification*). | Integration |
| **TEST-002** | `BookDao` | Subscribe `getAllBooksWithCount()`. Insert 1 buku baru dan 1 catatan. | Flow langsung meng-emit daftar baru berisi 1 buku dengan `noteCount = 1`. | Integration |
| **TEST-003** | `NoteDao` | Simpan 3 catatan dengan variasi waktu dan 1 catatan berstatus `is_pinned = true`. Query `getNotesByBookDescending()`. | Catatan pin muncul di indeks 0, diikuti catatan lainnya terurut waktu terbaru. | Integration |
| **TEST-004** | `SaveBookUseCase` | Jalankan UseCase dengan judul string kosong `""` atau hanya spasi `"   "`. | Mengembalikan `AppResult.Error` validasi: Judul tidak boleh kosong. | Unit Test |
| **TEST-005** | `NoteEditorViewModel` | Simulasikan ketikan teks. Majukan virtual clock coroutine sebesar 499 ms, lalu 501 ms via `advanceTimeBy()`. | Pada 499 ms belum ada penulisan ke repo. Pada 501 ms fungsi simpan terpanggil tepat 1 kali (*Debounce verification*). | Unit Test |
| **TEST-006** | `NoteEditorViewModel` | Ketik teks pada draf, pemicu event keluar/onPause dijalankan tanpa menunggu debounce. | Operasi penyimpanan seketika dieksekusi (*Instant flush verification*). | Unit Test |
| **TEST-007** | `SearchNotesUseCase` | Cari kata kunci "persamaan" pada database yang memiliki catatan matematika dan bahasa. | Hanya mengembalikan catatan yang memuat kata kunci di judul atau konten. | Unit Test |
| **TEST-008** | `BookListScreen` | Render layar saat database kosong. | `EmptyStatePlaceholder` tampil di layar dengan teks instruksi dan tombol FAB. | UI Test |
| **TEST-009** | `NoteEditorScreen` | Input teks panjang, simulasikan perubahan orientasi layar (Portrait ➔ Landscape). | Teks tetap utuh di form tanpa reset state atau kehilangan posisi kursor. | UI Test |
| **TEST-010** | End-to-End Persistensi | Tulis catatan, kill proses aplikasi di latar belakang, buka kembali aplikasi. | Catatan termuat kembali 100% utuh tanpa data corrupt (*Zero Data Loss proof*). | Manual Acceptance |

---

## 4. Kriteria Kelulusan Pengujian (Quality Gate)

Aplikasi hanya dapat dinyatakan siap rilis jika:
1. 100% Unit Test (`./gradlew testDebugUnitTest`) berstatus **PASSED**.
2. 100% Database Integration Test berstatus **PASSED**.
3. Tidak ada *race condition* atau kebocoran memori (*memory leak*) pada pengujian coroutine.
4. Matriks penerimaan manual **TEST-010** diverifikasi sukses pada perangkat fisik/emulator.
