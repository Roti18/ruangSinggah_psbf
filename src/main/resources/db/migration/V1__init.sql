-- ============================================
-- Ruang Singgah - Flyway Migration V1
-- Skema Lengkap 20 Tabel
-- ============================================

-- 1. boarding_houses (Gedung Kos)
CREATE TABLE IF NOT EXISTS boarding_houses (
    id      INTEGER PRIMARY KEY AUTOINCREMENT,
    name    TEXT    NOT NULL,
    address TEXT    NOT NULL
);

-- 2. room_types (Tipe Kamar: Standar, VIP, dll)
CREATE TABLE IF NOT EXISTS room_types (
    id   INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT    NOT NULL
);

-- 3. rooms (Kamar)
CREATE TABLE IF NOT EXISTS rooms (
    id                INTEGER PRIMARY KEY AUTOINCREMENT,
    name              TEXT    NOT NULL,
    price             REAL    NOT NULL DEFAULT 0,
    status            TEXT    NOT NULL, -- AVAILABLE, OCCUPIED, MAINTENANCE
    boarding_house_id INTEGER NOT NULL,
    room_type_id      INTEGER NOT NULL,
    FOREIGN KEY (boarding_house_id) REFERENCES boarding_houses(id),
    FOREIGN KEY (room_type_id)     REFERENCES room_types(id)
);

-- 4. facilities (Daftar Fasilitas: WiFi, AC, dll)
CREATE TABLE IF NOT EXISTS facilities (
    id   INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT    NOT NULL
);

-- 5. room_facilities (Tabel Penghubung Kamar & Fasilitas)
CREATE TABLE IF NOT EXISTS room_facilities (
    room_id     INTEGER NOT NULL,
    facility_id INTEGER NOT NULL,
    PRIMARY KEY (room_id, facility_id),
    FOREIGN KEY (room_id)     REFERENCES rooms(id),
    FOREIGN KEY (facility_id) REFERENCES facilities(id)
);

-- 6. tenants (Penyewa)
CREATE TABLE IF NOT EXISTS tenants (
    id    INTEGER PRIMARY KEY AUTOINCREMENT,
    name  TEXT    NOT NULL,
    phone TEXT    NOT NULL
);

-- 7. rentals (Transaksi Sewa)
CREATE TABLE IF NOT EXISTS rentals (
    id         INTEGER PRIMARY KEY AUTOINCREMENT,
    tenant_id  INTEGER NOT NULL,
    room_id    INTEGER NOT NULL,
    start_date DATE    NOT NULL,
    end_date   DATE    NOT NULL,
    FOREIGN KEY (tenant_id) REFERENCES tenants(id),
    FOREIGN KEY (room_id)   REFERENCES rooms(id)
);

-- 8. invoices (Tagihan)
CREATE TABLE IF NOT EXISTS invoices (
    id        INTEGER PRIMARY KEY AUTOINCREMENT,
    rental_id INTEGER NOT NULL,
    total     REAL    NOT NULL DEFAULT 0,
    FOREIGN KEY (rental_id) REFERENCES rentals(id)
);

-- 9. payments (Riwayat Pembayaran)
CREATE TABLE IF NOT EXISTS payments (
    id        INTEGER PRIMARY KEY AUTOINCREMENT,
    rental_id INTEGER NOT NULL,
    amount    REAL    NOT NULL DEFAULT 0,
    date      DATE    NOT NULL,
    FOREIGN KEY (rental_id) REFERENCES rentals(id)
);

-- 10. complaints (Komplain Penyewa)
CREATE TABLE IF NOT EXISTS complaints (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    tenant_id   INTEGER NOT NULL,
    description TEXT    NOT NULL,
    created_at  DATE    NOT NULL,
    FOREIGN KEY (tenant_id) REFERENCES tenants(id)
);

-- 11. maintenance_requests (Request Perbaikan Kamar)
CREATE TABLE IF NOT EXISTS maintenance_requests (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    room_id     INTEGER NOT NULL,
    description TEXT    NOT NULL,
    status      TEXT    NOT NULL, -- PENDING, IN_PROGRESS, DONE
    created_at  DATE    NOT NULL,
    FOREIGN KEY (room_id) REFERENCES rooms(id)
);

-- 12. users (Akun Pengguna Sistem)
CREATE TABLE IF NOT EXISTS users (
    id       INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT    NOT NULL UNIQUE,
    password TEXT    NOT NULL DEFAULT 'password123'
);

-- 13. roles (Role: Admin, Staff)
CREATE TABLE IF NOT EXISTS roles (
    id   INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT    NOT NULL UNIQUE
);

-- 14. user_roles (Tabel Penghubung User & Role)
CREATE TABLE IF NOT EXISTS user_roles (
    user_id INTEGER NOT NULL,
    role_id INTEGER NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (role_id) REFERENCES roles(id)
);

-- 15. activity_logs (Log Aktivitas System)
CREATE TABLE IF NOT EXISTS activity_logs (
    id         INTEGER PRIMARY KEY AUTOINCREMENT,
    action     TEXT    NOT NULL,
    created_at DATE    NOT NULL
);

-- 16. notifications (Pesan Notifikasi)
CREATE TABLE IF NOT EXISTS notifications (
    id         INTEGER PRIMARY KEY AUTOINCREMENT,
    message    TEXT    NOT NULL,
    created_at DATE    NOT NULL
);

-- 17. expenses (Pengeluaran Opersional)
CREATE TABLE IF NOT EXISTS expenses (
    id                INTEGER PRIMARY KEY AUTOINCREMENT,
    description       TEXT    NOT NULL,
    amount            REAL    NOT NULL DEFAULT 0,
    date              DATE    NOT NULL,
    boarding_house_id INTEGER NOT NULL,
    FOREIGN KEY (boarding_house_id) REFERENCES boarding_houses(id)
);

-- 18. inventory_items (Barang di dalam kamar)
CREATE TABLE IF NOT EXISTS inventory_items (
    id       INTEGER PRIMARY KEY AUTOINCREMENT,
    name     TEXT    NOT NULL,
    item_condition TEXT  NOT NULL, 
    room_id  INTEGER NOT NULL,
    FOREIGN KEY (room_id) REFERENCES rooms(id)
);

-- 19. reviews (Testimoni penyewa)
CREATE TABLE IF NOT EXISTS reviews (
    id                INTEGER PRIMARY KEY AUTOINCREMENT,
    tenant_id         INTEGER NOT NULL,
    boarding_house_id INTEGER NOT NULL,
    rating            INTEGER NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comment           TEXT,
    FOREIGN KEY (tenant_id)         REFERENCES tenants(id),
    FOREIGN KEY (boarding_house_id) REFERENCES boarding_houses(id)
);

-- 20. vouchers (Sistem Diskon)
CREATE TABLE IF NOT EXISTS vouchers (
    id           INTEGER PRIMARY KEY AUTOINCREMENT,
    code         TEXT    NOT NULL UNIQUE,
    discount     REAL    NOT NULL DEFAULT 0,
    valid_until  DATE    NOT NULL
);
