/* ============================================================
   Budgetly – Shared Navigation
   ============================================================ */

function renderNav(activePage) {
  const links = [
    { page: 'dashboard',     href: 'index.html',               icon: 'bi-speedometer2',    label: 'Dashboard' },
    { page: 'transactions',  href: 'transactions.html',        icon: 'bi-arrow-left-right',label: 'Transactions' },
    { page: 'categories',    href: 'categories.html',          icon: 'bi-tag-fill',        label: 'Categories' },
    { page: 'goals',         href: 'budget-goals.html',        icon: 'bi-bullseye',        label: 'Budget Goals' },
    { page: 'groups',        href: 'transaction-groups.html',  icon: 'bi-collection-fill', label: 'Groups' },
    { page: 'reports',       href: 'reports.html',             icon: 'bi-bar-chart-fill',  label: 'Reports' },
    { page: 'profiles',      href: 'profiles.html',            icon: 'bi-person-fill',     label: 'Profiles' },
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
        <span class="brand-logo">💰</span>
        <span class="brand-name">Budgetly</span>
      </div>
      <ul class="sidebar-nav">${navItems}</ul>
      <div class="sidebar-footer">
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
