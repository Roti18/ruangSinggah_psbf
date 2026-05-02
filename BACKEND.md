# Ruang Singgah - Backend API Documentation

Sistem Manajemen Kos (Boarding House Management System) berbasis Spring Boot, SQLite, JPA, dan Flyway.

---

## Cara Menjalankan Project

1. **Pastikan Java 17 terinstal.**
2. **Jalankan aplikasi:**
   ```powershell
   ./mvnw spring-boot:run
   ```
3. **Akses API di:** `http://localhost:8080/api/{resource}`

### Fitur Auto-Reload (DevTools)
Aplikasi ini sudah dikonfigurasi menggunakan `spring-boot-devtools` (Hot-Reload) dan mematikan fungsi cache (penyimpanan sementara) khusus untuk berkas template Thymeleaf (`spring.thymeleaf.cache=false`). Jika ada perubahan terhadap kode JavaScript di berkas `.js`, tataetak desain di berkas CSS, atau tata letak HTML di `.html`, Anda hanya perlu menyimpan berkas tersebut (Save) lalu langsung mereload ulang halaman di penelusur web tanpa perlu mengkompilasi atau menjalankan ulang peladen secara fisis (restart manual).

---

## Database Info

- **Tipe:** SQLite (File tunggal)
- **Lokasi File:** `./data/ruangsinggah.db`
- **Migrasi:** Otomatis via Flyway (folder `src/main/resources/db/migration/`)

---

## Daftar Endpoint (API)

Semua resource di bawah ini mendukung operasi standard:
- `GET /api/{resource}` : Ambil semua data
- `GET /api/{resource}/{id}` : Ambil data berdasarkan ID
- `POST /api/{resource}` : Tambah data baru
- `PUT /api/{resource}/{id}` : Update data
- `DELETE /api/{resource}/{id}` : Hapus data

### Kelompok Utama (Main Resources)

| Resource | Base URL | Filter Tambahan |
| :--- | :--- | :--- |
| **Boarding Houses** | `/api/boarding-houses` | - |
| **Room Types** | `/api/room-types` | - |
| **Rooms** | `/api/rooms` | `/by-status/{status}`, `/by-boarding-house/{id}` |
| **Facilities** | `/api/facilities` | - |
| **Tenants** | `/api/tenants` | - |
| **Rentals** | `/api/rentals` | `/by-tenant/{id}`, `/by-room/{id}` |

### Kelompok Keuangan & Operasional

| Resource | Base URL | Filter Tambahan |
| :--- | :--- | :--- |
| **Payments** | `/api/payments` | `/by-rental/{id}` |
| **Invoices** | `/api/invoices` | `/by-rental/{id}` |
| **Complaints** | `/api/complaints` | `/by-tenant/{id}` |
| **Maintenance** | `/api/maintenance-requests` | `/by-room/{id}`, `/by-status/{status}` |

### Kelompok User & Log (Admin)

| Resource | Base URL | Deskripsi |
| :--- | :--- | :--- |
| **Users** | `/api/users` | Pengguna sistem |
| **Roles** | `/api/roles` | Role/Jabatan (Admin, Tenant, Staff) |
| **Activity Logs** | `/api/activity-logs` | Catatan aktivitas sistem |
| **Notifications** | `/api/notifications` | Pesan notifikasi |

---

## Contoh Cara Testing (cURL / PowerShell)

### 1. Ambil Semua Kamar (GET)
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/rooms" -Method Get
```

### 2. Tambah Penyewa Baru (POST)
```powershell
$body = @{ name = "Budi Santoso"; phone = "08123456789" } | ConvertTo-Json
Invoke-RestMethod -Uri "http://localhost:8080/api/tenants" -Method Post -Body $body -ContentType "application/json"
```

### 3. Update Status Kamar (PUT)
```powershell
$body = @{ 
    name = "Kamar 101"
    price = 600000 
    status = "OCCUPIED" 
    boardingHouse = @{ id = 1 }
    roomType = @{ id = 1 }
} | ConvertTo-Json
Invoke-RestMethod -Uri "http://localhost:8080/api/rooms/1" -Method Put -Body $body -ContentType "application/json"
```
