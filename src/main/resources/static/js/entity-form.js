async function initFormPage() {
  const pd = document.getElementById('page-data');
  const entityKey = pd.dataset.entity;
  const mode = pd.dataset.mode;
  const entityId = pd.dataset.id;
  const config = ENTITIES[entityKey];
  if (!config) return;

  const isEdit = mode === 'edit';
  document.getElementById('page-title').textContent = (isEdit ? 'Edit ' : 'Tambah ') + config.displayName;
  document.getElementById('back-btn').href = `/${entityKey}`;

  let existingData = null;
  if (isEdit && entityId) {
    try { existingData = await apiFetch(`${config.apiPath}/${entityId}`); }
    catch (err) { showToast('Gagal memuat data', 'error'); return; }
  }

  await buildForm(config, existingData);

  document.getElementById('entity-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    const btn = document.getElementById('submit-btn');
    btn.disabled = true; btn.textContent = 'Menyimpan...';
    try {
      await submitForm(config, entityKey, isEdit, entityId);
    } catch (err) {
      btn.disabled = false; btn.textContent = 'Simpan';
    }
  });
}

function getFormValue(data, field) {
  if (field.type === 'ref') {
    const val = data[field.name];
    if (Array.isArray(val)) return val.length > 0 ? val[0].id : '';
    return val ? val.id : '';
  }
  return data[field.name] != null ? data[field.name] : '';
}

async function buildForm(config, data) {
  const container = document.getElementById('form-fields');
  let html = '';

  for (const f of config.fields) {
    const val = data ? getFormValue(data, f) : '';
    html += `<div class="mb-5">`;
    html += `<label class="block text-sm font-medium text-[#1F2937] mb-1.5">${f.label}${f.required ? ' <span class="text-red-400">*</span>' : ''}</label>`;

    const cls = 'w-full px-4 py-2.5 border border-[#CED4DA] rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-[#ABD8FF]/40 focus:border-[#ABD8FF] transition bg-white';

    if (f.type === 'textarea') {
      html += `<textarea name="${f.name}" class="${cls}" rows="3" ${f.required ? 'required' : ''}>${val}</textarea>`;
    } else if (f.type === 'select') {
      html += `<select name="${f.name}" class="${cls}" ${f.required ? 'required' : ''}>`;
      html += `<option value="">-- Pilih --</option>`;
      f.options.forEach(opt => { html += `<option value="${opt}" ${val === opt ? 'selected' : ''}>${opt}</option>`; });
      html += '</select>';
    } else if (f.type === 'ref') {
      html += `<select name="${f.name}" id="ref-${f.name}" class="${cls}" ${f.required ? 'required' : ''}>`;
      html += `<option value="">-- Pilih --</option></select>`;
    } else {
      const extras = (f.min !== undefined ? ` min="${f.min}"` : '') + (f.max !== undefined ? ` max="${f.max}"` : '');
      html += `<input type="${f.type || 'text'}" name="${f.name}" value="${val}" class="${cls}" ${f.required ? 'required' : ''}${extras}>`;
    }
    html += '</div>';
  }
  container.innerHTML = html;

  for (const f of config.fields) {
    if (f.type === 'ref') await loadRefOptions(f, data);
  }
}

async function loadRefOptions(field, existingData) {
  const select = document.getElementById(`ref-${field.name}`);
  try {
    const items = await apiFetch(field.refApi);
    items.forEach(item => {
      const label = field.refPrefix ? `${field.refPrefix}${item[field.refLabel]}` : item[field.refLabel];
      const opt = document.createElement('option');
      opt.value = item.id;
      opt.textContent = label || `#${item.id}`;
      if (existingData && existingData[field.name] && existingData[field.name].id === item.id) opt.selected = true;
      select.appendChild(opt);
    });
  } catch (err) { console.error('Failed to load ref:', field.name, err); }
}

async function submitForm(config, entityKey, isEdit, entityId) {
  const form = document.getElementById('entity-form');
  const fd = new FormData(form);
  const body = {};

  for (const f of config.fields) {
    const val = fd.get(f.name);
    if (f.type === 'ref') {
      if (f.name === 'roles') {
        body[f.name] = [{ id: parseInt(val) }];
      } else {
        body[f.name] = { id: parseInt(val) };
      }
    }
    else if (f.type === 'number') body[f.name] = parseFloat(val);
    else body[f.name] = val;
  }

  if (isEdit) await apiFetch(`${config.apiPath}/${entityId}`, { method: 'PUT', body: JSON.stringify(body) });
  else await apiFetch(config.apiPath, { method: 'POST', body: JSON.stringify(body) });

  showToast('Data berhasil disimpan', 'success');
  setTimeout(() => window.location.href = `/${entityKey}`, 500);
}

document.addEventListener('DOMContentLoaded', () => {
  const pd = document.getElementById('page-data');
  if (pd && pd.dataset.page === 'form') {
    initFormPage();
  }
});
