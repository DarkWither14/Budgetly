/* ============================================================
   Budgetly – Shared Navigation
   ============================================================ */

function renderNav(activePage) {
  const links = [
    { page: 'dashboard',     href: 'index.html',               icon: 'bi-grid-1x2-fill',        label: 'Dashboard' },
    { page: 'transactions',  href: 'transactions.html',        icon: 'bi-credit-card-2-front-fill', label: 'Transactions' },
    { page: 'categories',    href: 'categories.html',          icon: 'bi-bookmark-star-fill',   label: 'Categories' },
    { page: 'goals',         href: 'budget-goals.html',        icon: 'bi-trophy-fill',          label: 'Budget Goals' },
    { page: 'groups',        href: 'transaction-groups.html',  icon: 'bi-folder2-open',         label: 'Groups' },
    { page: 'reports',       href: 'reports.html',             icon: 'bi-graph-up-arrow',       label: 'Reports' },
    { page: 'profiles',      href: 'profiles.html',            icon: 'bi-person-badge-fill',    label: 'Profiles' },
  ];

  const navItems = links.map(l => `
    <li>
      <a href="${l.href}" class="${activePage === l.page ? 'active' : ''}">
        <i class="bi ${l.icon}"></i> ${l.label}
      </a>
    </li>`).join('');

  document.getElementById('navContainer').innerHTML = `
    <nav class="sidebar">
      <div class="sidebar-brand">
        <span class="brand-logo"><svg width="32" height="32" viewBox="0 0 32 32" fill="none" xmlns="http://www.w3.org/2000/svg"><rect width="32" height="32" rx="9" fill="#111827"/><polyline points="5,24 12,16 18,20 26,8" stroke="white" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round" fill="none"/><polyline points="20,7 26,8 25,14" stroke="white" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round" fill="none"/></svg></span>
        <span class="brand-name">Budgetly</span>
      </div>
      <ul class="sidebar-nav">${navItems}</ul>
      <div class="sidebar-footer">
        <button id="themeToggleBtn" onclick="toggleTheme()" class="btn btn-ghost btn-sm" style="width:100%;margin-bottom:0.75rem;justify-content:flex-start;gap:0.5rem;">
          <i class="bi bi-moon-fill"></i> Dark Mode
        </button>
        <div class="profile-select-label">Active Profile</div>
        <select id="globalProfileSelect" onchange="switchProfile(this.value)">
          <option value="">— Select Profile —</option>
        </select>
      </div>
    </nav>`;

  const sel    = document.getElementById('globalProfileSelect');
  const active = getActiveProfileId();
  getProfiles().forEach(p => {
    const opt   = document.createElement('option');
    opt.value   = p.id;
    opt.textContent = p.name;
    if (p.id === active) opt.selected = true;
    sel.appendChild(opt);
  });

  // Sync toggle button label with current theme
  _updateThemeBtn(document.documentElement.getAttribute('data-theme') || 'light');
}

function toggleTheme() {
  const current = document.documentElement.getAttribute('data-theme') || 'light';
  const next    = current === 'dark' ? 'light' : 'dark';
  document.documentElement.setAttribute('data-theme', next);
  localStorage.setItem('budgetly_theme', next);
  _updateThemeBtn(next);
}

function _updateThemeBtn(theme) {
  const btn = document.getElementById('themeToggleBtn');
  if (!btn) return;
  btn.innerHTML = theme === 'dark'
    ? '<i class="bi bi-sun-fill"></i> Light Mode'
    : '<i class="bi bi-moon-fill"></i> Dark Mode';
}

function switchProfile(id) {
  setActiveProfileId(id);
  window.location.reload();
}

// ---- Simple confirm dialog ----
// Usage: showConfirm('Are you sure?', () => doSomething())

function showConfirm(message, onConfirm) {
  document.getElementById('confirmMsg').textContent  = message;
  document.getElementById('confirmDialog').classList.add('open');
  document.getElementById('confirmOkBtn').onclick = () => {
    closeConfirm();
    onConfirm();
  };
}
function closeConfirm() { document.getElementById('confirmDialog').classList.remove('open'); }

// ---- Shared confirm dialog HTML (injected once per page) ----

function injectConfirmDialog() {
  const d = document.createElement('div');
  d.id = 'confirmDialog';
  d.className = 'modal-overlay';
  d.innerHTML = `
    <div class="modal" style="max-width:360px">
      <div class="modal-header">
        <span class="modal-title">Confirm</span>
      </div>
      <p id="confirmMsg" class="confirm-msg"></p>
      <div class="modal-actions">
        <button class="btn btn-ghost" onclick="closeConfirm()">Cancel</button>
        <button id="confirmOkBtn" class="btn btn-danger">Delete</button>
      </div>
    </div>`;
  document.body.appendChild(d);
}

document.addEventListener('DOMContentLoaded', injectConfirmDialog);
