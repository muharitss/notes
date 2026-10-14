# Arsitektur Sistem & Desain Perangkat Lunak (Architecture Spec)
**Nama Proyek:** Buku Catatan Digital (Android Native Kotlin)  
**Versi Dokumen:** 1.0.0  
**Status:** PROPOSED FOR REVIEW  

---

## 1. Prinsip Desain Arsitektur

Arsitektur aplikasi dirancang berlandaskan pada prinsip:
1. **Separation of Concerns (SoC):** Pemisahan tegas antara logika rendering UI, aturan bisnis (*business rules*), dan persistensi disk.
2. **Offline-First Reactive Flow:** Setiap data yang ditampilkan di layar merupakan representasi langsung dari database lokal via `Flow<List<T>>`. UI tidak pernah menyimpan *shadow state* yang tidak terikat ke database.
3. **Testability:** Komponen Domain dan ViewModel dapat diuji 100% menggunakan Unit Test tanpa perlu menjalankan emulator Android.

---

## 2. Struktur Tiga Lapisan (3-Layer Architecture)

```
┌─────────────────────────────────────────────────────────────┐
│                    PRESENTATION LAYER                       │
│  - Jetpack Compose Screens (BookList, BookDetail, Editor)   │
│  - Jetpack ViewModels (StateFlow-driven)                    │
│  - Contracts: UiState (Immutable) & UiEvent (Sealed)        │
│  - Reusable Design System Components (Material 3)           │
└──────────────────────────────▲──────────────────────────────┘
                               │ Observes Flow<DomainModel>
                               │ Triggers UseCases
┌──────────────────────────────┴──────────────────────────────┐
│                       DOMAIN LAYER                          │
│  - Pure Kotlin Domain Models (Book, Note, BookWithNotes)    │
│  - Repository Interfaces (BookRepository, NoteRepository)    │
│  - Use Cases (GetBooksUseCase, SaveNoteUseCase, dll)        │
│  * Catatan: Bebas dari dependensi Android framework          │
└──────────────────────────────▲──────────────────────────────┘
                               │ Implements Interfaces
                               │ Mappers (Entity <-> Domain)
┌──────────────────────────────┴──────────────────────────────┐
│                        DATA LAYER                           │
│  - Room Database (`AppDatabase`)                            │
│  - DAOs (`BookDao`, `NoteDao`)                              │
│  - Entities (`BookEntity`, `NoteEntity`)                    │
│  - Repository Implementations (`BookRepositoryImpl`, etc.)  │
└─────────────────────────────────────────────────────────────┘
```

### 2.1 Presentation Layer
- **Tanggung Jawab:** Menangani antarmuka visual pengguna, menerima sentuhan, dan menampilkan state data.
- **State Pattern:** Pola Unidirectional Data Flow (UDF).
  - Layar Composable bersifat *stateless*, menerima data state dan meneruskan lambda callback event ke ViewModel.
  - ViewModel menyimpan `_uiState` (`MutableStateFlow`) privat dan mengekspos `uiState` (`StateFlow`) publik.

### 2.2 Domain Layer (Pure Kotlin)
- **Tanggung Jawab:** Menampung logika bisnis inti yang independen dari platform:
  - Validasi bahwa judul buku tidak boleh hanya spasi.
  - Format perhitungan jumlah catatan dalam satu buku.
  - Logika pengurutan (*sorting*) gabungan antara status pin dan waktu pembaruan.
- **Isolasi:** Layer ini tidak boleh mengimpor kelas dari `android.*` atau `androidx.room.*`.

### 2.3 Data Layer
- **Tanggung Jawab:** Bertanggung jawab penuh terhadap I/O database SQLite lokal.
- **Mapping:** Mengubah `BookEntity` menjadi `Book` domain model, sehingga perubahan skema tabel di masa depan tidak berdampak langsung pada logika UI.
- **Concurrency:** Semua eksekusi DAO dijalankan di thread `Dispatchers.IO`.

---

## 3. Struktur Direktori Kode (Package Layout)

Kode diorganisasikan menggunakan pola **Feature-First**:

```
com.notes.digital/
│
├── App.kt                           # Application class & DI AppContainer
│
├── core/
│   ├── database/                    # Room Database setup
│   │   ├── AppDatabase.kt
│   │   ├── dao/
│   │   │   ├── BookDao.kt
│   │   │   └── NoteDao.kt
│   │   └── entity/
│   │       ├── BookEntity.kt
│   │       └── NoteEntity.kt
│   │
│   ├── designsystem/                # Material 3 Styling
│   │   ├── Color.kt
│   │   ├── Theme.kt
│   │   ├── Type.kt
│   │   └── components/
│   │       ├── NoteTopAppBar.kt
│   │       ├── ConfirmationDialog.kt
│   │       ├── ColorPalettePicker.kt
│   │       └── EmptyStatePlaceholder.kt
│   │
│   └── util/
│       ├── DateTimeExtensions.kt
│       └── AppDispatchers.kt
│
├── data/                            # Implementasi Repository & Mapper
│   ├── mapper/
│   │   ├── BookMapper.kt
│   │   └── NoteMapper.kt
│   └── repository/
│       ├── BookRepositoryImpl.kt
│       └── NoteRepositoryImpl.kt
│
├── domain/                          # Model & Use Case Murni
│   ├── model/
│   │   ├── Book.kt
│   │   ├── Note.kt
│   │   └── BookWithNotes.kt
│   ├── repository/
│   │   ├── BookRepository.kt
│   │   └── NoteRepository.kt
│   └── usecase/
│       ├── book/
│       │   ├── GetBooksUseCase.kt
│       │   ├── SaveBookUseCase.kt
│       │   └── DeleteBookUseCase.kt
│       └── note/
│           ├── GetNotesByBookUseCase.kt
│           ├── SaveNoteUseCase.kt
│           ├── DeleteNoteUseCase.kt
│           ├── TogglePinNoteUseCase.kt
│           └── SearchNotesUseCase.kt
│
├── feature/                         # Fitur Layar Aplikasi
│   ├── booklist/
│   │   ├── BookListScreen.kt
│   │   ├── BookListViewModel.kt
│   │   ├── BookListContract.kt
│   │   └── components/
│   │       ├── BookCard.kt
│   │       └── AddEditBookDialog.kt
│   │
│   ├── bookdetail/
│   │   ├── BookDetailScreen.kt
│   │   ├── BookDetailViewModel.kt
│   │   ├── BookDetailContract.kt
│   │   └── components/
│   │       └── NoteListItem.kt
│   │
│   ├── noteeditor/
│   │   ├── NoteEditorScreen.kt
│   │   ├── NoteEditorViewModel.kt
│   │   └── NoteEditorContract.kt
│   │
│   └── search/
│       ├── GlobalSearchScreen.kt
│       ├── GlobalSearchViewModel.kt
│       └── GlobalSearchContract.kt
│
└── navigation/
    ├── AppNavHost.kt
    └── Destinations.kt              # Type-Safe Route Definitions
```

---

## 4. Arsitektur Navigasi (Type-Safe Navigation)

Menggunakan Navigation Compose resmi dengan rute berbasis `data object` dan `data class`:

```kotlin
sealed interface Destination {
    @Serializable
    data object BookList : Destination
    
    @Serializable
    data class BookDetail(val bookId: Long) : Destination
    
    @Serializable
    data class NoteEditor(val bookId: Long, val noteId: Long = -1L) : Destination
    
    @Serializable
    data object GlobalSearch : Destination
}
```

- Jika `noteId == -1L`, layar `NoteEditor` berada pada mode *pembuatan catatan baru*.
- Jika `noteId > 0L`, layar memuat catatan yang tersimpan untuk diedit.

---

## 5. Strategi Dependency Injection (DI)

Mengikuti hasil evaluasi [specs/decisions.md#adr-007](file:///home/alarm/notes/specs/decisions.md#adr-007):
Aplikasi menggunakan **Manual DI via `AppContainer`** di dalam class `Application`.
- `AppContainer` menginisialisasi singleton `AppDatabase`, `BookRepositoryImpl`, dan `NoteRepositoryImpl`.
- ViewModel diinstansiasi menggunakan ViewModel Factory standar Android Jetpack:
  ```kotlin
  class ViewModelFactory(
      private val appContainer: AppContainer
  ) : ViewModelProvider.Factory { ... }
  ```
- **Keunggulan:** Zero build overhead, tidak memerlukan plugin KSP/kapt tambahan, kode sangat mudah dibaca dan ditelusuri.
