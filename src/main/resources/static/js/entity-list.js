async function initListPage() {
  const pd = document.getElementById('page-data');
  const entityKey = pd.dataset.entity;
  const config = ENTITIES[entityKey];
  if (!config) return;

  document.getElementById('page-title').textContent = config.displayName;
  document.getElementById('add-btn').href = `/${entityKey}/create`;

  const loader = document.getElementById('loader');
  loader.classList.remove('hidden');

  try {
    const data = await apiFetch(config.apiPath);
    loader.classList.add('hidden');
    renderTable(data, config, entityKey);
  } catch (err) {
    loader.classList.add('hidden');
    document.getElementById('table-body').innerHTML =
      `<tr>
        <td colspan="100" class="text-center py-10 text-gray-400">
          <div class="flex flex-col items-center gap-2">
            <span class="text-lg font-medium">Belum ada data</span>
            <span class="text-sm">Silakan tambahkan data baru</span>
          </div>
        </td>
      </tr>`;
  }
}

function renderTable(data, config, entityKey) {
  const thead = document.getElementById('table-head');
  const tbody = document.getElementById('table-body');
  const count = document.getElementById('data-count');
  if (count) count.textContent = `${data.length} data`;

  let hdr = '';
  config.columns.forEach(c => {
    hdr += `<th class="px-4 py-3 text-left text-xs font-semibold uppercase tracking-wider text-gray-500">${c.label}</th>`;
  });
  hdr += '<th class="px-4 py-3 text-center text-xs font-semibold uppercase tracking-wider text-gray-500">Aksi</th>';
  thead.innerHTML = `<tr class="border-b border-[#CED4DA]">${hdr}</tr>`;

  if (!data.length) {
    tbody.innerHTML = '<tr><td colspan="100" class="text-center py-12 text-gray-400">Belum ada data</td></tr>';
    return;
  }

  let rows = '';
  data.forEach(item => {
    rows += '<tr class="data-row border-b border-[#CED4DA]/40 transition-colors">';
    config.columns.forEach(c => {
      const val = getNestedValue(item, c.key);
      rows += `<td class="px-4 py-3 text-sm">${formatCell(val, c.format)}</td>`;
    });
    rows += `<td class="px-4 py-3 text-center whitespace-nowrap">
      <a href="/${entityKey}/edit/${item.id}" class="inline-block px-3 py-1 text-xs rounded-md bg-[#ABD8FF] text-[#1F2937] hover:bg-[#ABD8FF]/70 transition mr-1">Edit</a>
      <button onclick="handleDelete('${config.apiPath}',${item.id})" class="inline-block px-3 py-1 text-xs rounded-md bg-red-50 text-red-600 hover:bg-red-100 transition">Hapus</button>
    </td>`;
    rows += '</tr>';
  });
  tbody.innerHTML = rows;
}

window.handleDelete = async function(apiPath, id) {
  if (!confirm('Yakin ingin menghapus data ini?')) return;
  try {
    await apiFetch(`${apiPath}/${id}`, { method: 'DELETE' });
    showToast('Data berhasil dihapus', 'success');
    initListPage();
  } catch (err) {
    showToast('Gagal menghapus data', 'error');
  }
}

document.addEventListener('DOMContentLoaded', () => {
  const pd = document.getElementById('page-data');
  if (pd && pd.dataset.page === 'list') {
    initListPage();
  }
});
