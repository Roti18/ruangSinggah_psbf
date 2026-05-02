async function initDashboard() {
  const cards = [
    { api: '/api/rooms', id: 'stat-rooms' },
    { api: '/api/tenants', id: 'stat-tenants' },
    { api: '/api/rentals', id: 'stat-rentals' },
    { api: '/api/payments', id: 'stat-payments' },
    { api: '/api/boarding-houses', id: 'stat-bh' },
    { api: '/api/complaints', id: 'stat-complaints' }
  ];

  for (const c of cards) {
    try {
      const data = await apiFetch(c.api);
      const el = document.getElementById(c.id);
      if (el) el.textContent = data.length;
    } catch (err) {
      const el = document.getElementById(c.id);
      if (el) el.textContent = '0';
    }
  }

  // Load recent activity with a timeout mechanism
  try {
    const fetchPromise = apiFetch('/api/activity-logs');
    const timeoutPromise = new Promise((_, reject) => {
      setTimeout(() => reject(new Error('TIMEOUT')), 3000); // 3 seconds timeout
    });
    
    const logs = await Promise.race([fetchPromise, timeoutPromise]);
    
    const container = document.getElementById('recent-activity');
    if (container && logs && logs.length) {
      container.innerHTML = logs.slice(-5).reverse().map(l =>
        `<div class="flex items-center gap-3 py-2 border-b border-[#CED4DA]/30 last:border-0">
          <div class="w-2 h-2 rounded-full bg-[#B7E4C7] shrink-0"></div>
          <p class="text-sm text-[#1F2937]">${l.action}</p>
          <p class="text-xs text-gray-400 ml-auto">${l.createdAt || ''}</p>
        </div>`
      ).join('');
    } else if (container) {
      container.innerHTML = '<p class="text-sm text-gray-300">Belum ada aktivitas</p>';
    }
  } catch (err) {
    const container = document.getElementById('recent-activity');
    if (container) {
      container.innerHTML = '<p class="text-sm text-gray-400">Data tidak ditemukan / Gagal memuat data.</p>';
    }
  }
}

document.addEventListener('DOMContentLoaded', () => {
  const pd = document.getElementById('page-data');
  if (pd && pd.dataset.page === 'dashboard') {
    initDashboard();
  }
});
