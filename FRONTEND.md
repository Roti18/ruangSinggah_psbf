# Dokumentasi Antarmuka Pengguna (Frontend) - Ruang Singgah

Dokumentasi ini berisi informasi mengenai arsitektur dan teknologi bahasa pemrograman antarmuka (frontend) yang diaplikasikan pada Sistem Manajemen Kos Ruang Singgah.

---

## Teknologi yang Digunakan

Proyek antarmuka ini didesain agar sangat ringan dan dapat beroperasi tanpa alat bantu kompilasi tambahan (seperti Webpack, Vite, Node.js, atau NPM). Semua dependensi utama dipanggil melalui CDN (jaringan pengirim konten) secara eksternal.

1. **Thymeleaf (Server-Side Templating)**
   - Digunakan sebagai perender halaman HTML sisi-server yang terintegrasi langsung dengan kerangka kerja Spring Boot.
   - Fitur utama yang dipakai meliputi penyisipan teks variabel sisi-server, manajemen tata letak (`layout dialect`), dan logika percabangan untuk kontrol akses berdasar jabatan pengguna (Role-Based Access Control).

2. **TailwindCSS via CDN (Styling Engine)**
   - Mengurus seluruh tata rias (CSS) antarmuka dan komponen tabel secara terpadu.
   - Diposisikan menggunakan panggilan langsung skrip `https://cdn.tailwindcss.com` pada bagian header berkas `base.html` sebagai titik pijakan utama sehingga aplikasi tetap bebas dari kerumitan bundel CSS berlebih.
   - Palet warna sistem tata kelola kos telah diterjemahkan masuk ke dalam konfigurasi skrip Tailwind di struktur HTML.

3. **Vanilla JavaScript (Logic & Fetching API)**
   - Sistem tidak mendayagunakan pustaka eksternal seperti React, Vue, atau jQuery. Seluruh perintah interaktif ditulis menggunakan perintah bawaan ECMAScript (Vanilla JS).
   - Pengambilan dan penyimpanan data dijalankan menggunakan fungsi asinkron (Async/Await) dengan antarmuka Pemrograman Aplikasi (API Fetch).

4. **Lucide Icons (Sistem Ikon)**
   - Pustaka ikon garis (line icons) berdesain modern, yang diintegrasikan juga menggunakan jaringan CDN.
   - Dipanggil langsung dengan elemen `<i data-lucide="nama-ikon"></i>` kemudian diinisiasi melalui panggilan fungsi `lucide.createIcons()` di akhir baris program.

---

## Struktur Berkas JavaScript (Modular)

Sistem logika JavaScript telah dipecah secara rapi (fragmented modular) ke dalam letak direktori `src/main/resources/static/js/` untuk memperjelas batas-batas fungsional, kemudahan pencarian kendala, dan mempermudah perbaikan sistem di masa mendatang.

* `config.js`
  Berisi konstanta kamus besar entitas tabel basis data yang diperlukan oleh sistem. Setiap entitas dirincikan struktur datanya guna dicocokkan dengan tipe kolom API dan nama label teks input form otomatis.

* `core.js`
  Memuat kumpulan fungsi pembantu inti (utilities) yang digunakan bersamaan (shared) di seluruh skrip lainnya. Fitur di dalamnya mencakup: penanganan *fetch* jaringan (tata kelola HTTP Request), notifikasi peringatan mengambang (Toast Notification), dan perombakan format sel pada tabel (*formatter* teks statis, angka, rupiah, maupun penunjuk lencana penanda status elemen tertentu).

* `sidebar.js`
  Sistem kendali fokus panel samping (Sidebar Highlight). Skrip ini memodifikasi CSS kelas navigasi apabila struktur URL merujuk pada direktori menu yang sesuai.

* `dashboard.js`
  Fungsi khusus tata letak *Dashboard* analitik awal pasca masuk otentikasi. Sistem dilengkapi hitung mundur batasan rentang waktu eksekusi jaringan, sehingga jika peladen backend terlalu lambat merespons data daftar riwayat atau log riwayat, aplikasi sanggup beralih melaporkan keterangan pesan galat koneksi "Data tidak ditemukan".

* `entity-list.js`
  Dijalankan pada setiap pemanggilan rute halaman induk untuk mencetak tabel HTML terpusat berdasarkan skema `config.js`. Juga menangani konfirmasi aksi penghapusan baris data.

* `entity-form.js`
  Membangun isi input antarmuka *form* pada halaman sub-rute penambahan (Create) maupun modifikasi (Edit) objek data spesifik ke tabel sasaran, mengambil masukan yang diserahkan, mengelompokkanya jadi data JSON, lalu memancarkannya menuju *backend* guna menyimpannya secara otomatis divalidasi.

---

## Palet Warna

- **Bright Snow**: `#FAFAFA` (Dasar latar aplikasi)
- **Celadon**: `#B7E4C7` (Sistem identitas / aksen dominan)
- **Jet Black**: `#1F2937` (Aksentuasi warna teks tajam atau nama tajuk navigasi)
- **Icy Blue**: `#ABD8FF` (Aksentuasi kedua/tombol opsional aksi tabel)
- **Pale Slate**: `#CED4DA` (Batas sekat komponen baris navigasi dan tepi tabel selang-seling baris datar)
