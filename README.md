# Buku Catatan Digital (Digital Notebook) — Android Native

Aplikasi Android native berbasis Kotlin dan Jetpack Compose dengan konsep buku catatan digital yang terstruktur, tahan lama, dan beroperasi **100% offline-first dan local-only**.

---

## 📑 Dokumentasi Resmi Proyek (Spec-Driven Development)

Seluruh arsitektur, spesifikasi, dan panduan rekayasa diatur secara formal dalam dokumen berikut:

### 1. Fondasi Produk & Tata Kerja Agen
- [prd.md](file:///home/alarm/notes/prd.md) — Dokumen Kebutuhan Produk (Latar Belakang, Problem Statement, Persona, FR, NFR, Scope MVP, Acceptance Criteria).
- [AGENTS.md](file:///home/alarm/notes/AGENTS.md) — Instruksi Kerja Permanen untuk AI Agent (Standar Kotlin, Layering, Guardrails, Workflow).

### 2. Spesifikasi Teknis (`specs/`)
- [specs/architecture.md](file:///home/alarm/notes/specs/architecture.md) — Blueprint Arsitektur Aplikasi (3-Layer Clean Architecture, UDF, Navigasi Type-Safe).
- [specs/data-model.md](file:///home/alarm/notes/specs/data-model.md) — Model Data, ERD, Relasi Foreign Key Cascade, dan Timestamps.
- [specs/local-storage.md](file:///home/alarm/notes/specs/local-storage.md) — Spesifikasi Room SQLite, Transaksi Atomik, Migrasi, dan Auto-Save.
- [specs/functional-spec.md](file:///home/alarm/notes/specs/functional-spec.md) — Spesifikasi Fungsional & Analisis 12 Alur Utama Pengguna.
- [specs/ui-ux.md](file:///home/alarm/notes/specs/ui-ux.md) — Desain UI Material 3, Palet Warna Sampul Buku, dan Standar Aksesibilitas.
- [specs/testing-strategy.md](file:///home/alarm/notes/specs/testing-strategy.md) — Piramida Pengujian & Matriks Skenario Uji Resmi (`TEST-001` s/d `TEST-010`).
- [specs/decisions.md](file:///home/alarm/notes/specs/decisions.md) — Catatan Keputusan Arsitektur (`ADR-001` s/d `ADR-009`).

### 3. Manajemen Task & Pelacak Progres (`tasks/`)
- [tasks/README.md](file:///home/alarm/notes/tasks/README.md) — **Pusat Pelacak Status Progres Seluruh Task (Single Source of Truth)**.
- Rincian Task Mandiri:
  - [TASK-001: Project Setup & Baseline Tooling](file:///home/alarm/notes/tasks/TASK-001-project-setup.md)
  - [TASK-002: Local Data Layer (Room & DAOs)](file:///home/alarm/notes/tasks/TASK-002-local-data-layer.md)
  - [TASK-003: Domain Models & Contracts](file:///home/alarm/notes/tasks/TASK-003-domain-and-contracts.md)
  - [TASK-004: Data Layer Repositories & Mappers](file:///home/alarm/notes/tasks/TASK-004-data-repositories.md)
  - [TASK-005: Design System & Navigation Base](file:///home/alarm/notes/tasks/TASK-005-design-system-and-nav.md)
  - [TASK-006: Feature Notebook Management](file:///home/alarm/notes/tasks/TASK-006-notebook-management.md)
  - [TASK-007: Feature Note List in Notebook](file:///home/alarm/notes/tasks/TASK-007-note-list-management.md)
  - [TASK-008: Feature Note Editor with Auto-Save](file:///home/alarm/notes/tasks/TASK-008-note-editor-autosave.md)
  - [TASK-009: Feature Local Search & Filtering](file:///home/alarm/notes/tasks/TASK-009-search-and-organization.md)
  - [TASK-010: Utility Local Backup (JSON SAF)](file:///home/alarm/notes/tasks/TASK-010-local-backup-saf.md)
  - [TASK-011: End-to-End Verification & QA Release](file:///home/alarm/notes/tasks/TASK-011-verification-and-release.md)

---

## 🔒 Prinsip Mutlak Produk
1. **100% Offline-First & Local-Only:** Bebas server, backend, registrasi akun, dan koneksi internet.
2. **Zero Data Loss:** Perlindungan pengetikan dengan auto-save debouncing dan instant flush onPause.
3. **Spec-Driven Governance:** Implementasi dieksekusi satu per satu sesuai urutan task dan spesifikasi resmi.
