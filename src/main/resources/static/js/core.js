function getNestedValue(obj, path) {
  return path.split(".").reduce((o, k) => (o || {})[k], obj);
}

function formatCell(val, format) {
  if (val === null || val === undefined)
    return '<span class="text-gray-300">-</span>';
  if (format === "currency") return "Rp " + Number(val).toLocaleString("id-ID");
  if (format === "role-badges") {
    const roles = Array.isArray(val) ? val : [];
    if (!roles.length) return '<span class="text-gray-300">-</span>';
    const colors = {
      ADMIN: "bg-red-100 text-red-700",
      STAFF: "bg-blue-100 text-blue-700",
    };
    return roles
      .map((r) => {
        const name = (typeof r === "string" ? r : r?.name) || "ROLE";
        const c = colors[name] || "bg-gray-100 text-gray-700";
        return `<span class="inline-flex items-center px-2 py-1 rounded-full text-xs font-medium mr-1 ${c}">${name}</span>`;
      })
      .join("");
  }
  if (format === "badge") {
    const colors = {
      AVAILABLE: "bg-green-100 text-green-700",
      OCCUPIED: "bg-yellow-100 text-yellow-700",
      MAINTENANCE: "bg-red-100 text-red-700",
      PENDING: "bg-blue-100 text-blue-700",
      IN_PROGRESS: "bg-yellow-100 text-yellow-700",
      DONE: "bg-green-100 text-green-700",
      GOOD: "bg-green-100 text-green-700",
      BROKEN: "bg-red-100 text-red-700",
      NEED_REPAIR: "bg-yellow-100 text-yellow-700",
    };
    const c = colors[val] || "bg-gray-100 text-gray-700";
    return `<span class="px-2 py-1 rounded-full text-xs font-medium ${c}">${val}</span>`;
  }
  return val;
}

function showToast(msg, type) {
  const t = document.createElement("div");
  t.className = `toast toast-${type}`;
  t.textContent = msg;
  document.body.appendChild(t);
  setTimeout(() => t.remove(), 3000);
}

async function apiFetch(url, options = {}) {
  const headers = { "Content-Type": "application/json" };
  const csrfToken = document
    .querySelector('meta[name="_csrf"]')
    ?.getAttribute("content");
  const csrfHeader = document
    .querySelector('meta[name="_csrf_header"]')
    ?.getAttribute("content");
  const method = (options.method || "GET").toUpperCase();
  if (csrfToken && csrfHeader && method !== "GET") {
    headers[csrfHeader] = csrfToken;
  }
  const res = await fetch(url, { headers, ...options });
  if (res.status === 204) return null;
  if (!res.ok) throw new Error(`HTTP ${res.status}`);
  return res.json();
}
