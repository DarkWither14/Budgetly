/* ============================================================
   Budgetly – Data Layer (localStorage)
   TODO: Replace each function with a fetch() API call once
         the Java backend is ready. The function signatures
         stay the same — only the bodies change.
   ============================================================ */

const KEYS = {
  profiles:     'budgetly_profiles',
  categories:   'budgetly_categories',
  transactions: 'budgetly_transactions',
  groups:       'budgetly_groups',
  goals:        'budgetly_goals',
  activeProfile:'budgetly_active_profile'
};

// ---- Internal helpers ----

function _load(key)       { try { return JSON.parse(localStorage.getItem(key) || '[]'); } catch { return []; } }
function _save(key, data) { localStorage.setItem(key, JSON.stringify(data)); }
function _genId()         { return Date.now().toString(36) + Math.random().toString(36).slice(2, 7); }

// ---- Formatting utilities (used by all pages) ----

function formatCurrency(n) {
  return new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(Number(n) || 0);
}
function formatDate(s) {
  if (!s) return '—';
  const d = new Date(s + 'T12:00:00');
  return d.toLocaleDateString('en-US', { year: 'numeric', month: 'short', day: 'numeric' });
}
function todayISO() { return new Date().toISOString().split('T')[0]; }

// ---- Active Profile ----

function getActiveProfileId()   { return localStorage.getItem(KEYS.activeProfile) || ''; }
function setActiveProfileId(id) { localStorage.setItem(KEYS.activeProfile, id || ''); }

// ---- Profiles ----

function getProfiles()   { return _load(KEYS.profiles); }
function getProfileById(id) { return getProfiles().find(p => p.id === id) || null; }

function createProfile({ name, description }) {
  const list = getProfiles();
  const item = { id: _genId(), name: name.trim(), description: (description || '').trim(), createdAt: todayISO() };
  list.push(item);
  _save(KEYS.profiles, list);
  if (!getActiveProfileId()) setActiveProfileId(item.id);
  return item;
}

function updateProfile(id, { name, description }) {
  _save(KEYS.profiles, getProfiles().map(p => p.id === id ? { ...p, name: name.trim(), description: (description||'').trim() } : p));
}

function deleteProfile(id) {
  _save(KEYS.profiles,     getProfiles().filter(p => p.id !== id));
  _save(KEYS.transactions, getTransactions().filter(t => t.profileId !== id));
  _save(KEYS.groups,       getGroups().filter(g => g.profileId !== id));
  _save(KEYS.goals,        getGoals().filter(g => g.profileId !== id));
  if (getActiveProfileId() === id) {
    const rem = getProfiles();
    setActiveProfileId(rem.length ? rem[0].id : '');
  }
}

// ---- Categories ----

function getCategories()      { return _load(KEYS.categories); }
function getCategoryById(id)  { return getCategories().find(c => c.id === id) || null; }
function getCategoriesByType(type) { return getCategories().filter(c => c.type === type); }

function createCategory({ name, type, description }) {
  const list = getCategories();
  if (list.some(c => c.name.toLowerCase() === name.trim().toLowerCase()))
    throw new Error('A category with that name already exists.');
  const item = { id: _genId(), name: name.trim(), type, description: (description||'').trim() };
  list.push(item);
  _save(KEYS.categories, list);
  return item;
}

function updateCategory(id, { name, type, description }) {
  const list = getCategories();
  if (list.some(c => c.id !== id && c.name.toLowerCase() === name.trim().toLowerCase()))
    throw new Error('A category with that name already exists.');
  _save(KEYS.categories, list.map(c => c.id === id ? { ...c, name: name.trim(), type, description: (description||'').trim() } : c));
}

function deleteCategory(id) {
  _save(KEYS.categories,   getCategories().filter(c => c.id !== id));
  _save(KEYS.transactions, getTransactions().map(t => t.categoryId === id ? { ...t, categoryId: null } : t));
}

// ---- Transaction Groups ----

function getGroups()               { return _load(KEYS.groups); }
function getGroupsByProfile(pid)   { return getGroups().filter(g => g.profileId === pid); }

function createGroup({ profileId, name, description }) {
  const list = getGroups();
  const item = { id: _genId(), profileId, name: name.trim(), description: (description||'').trim(), createdAt: todayISO() };
  list.push(item);
  _save(KEYS.groups, list);
  return item;
}

function updateGroup(id, { name, description }) {
  _save(KEYS.groups, getGroups().map(g => g.id === id ? { ...g, name: name.trim(), description: (description||'').trim() } : g));
}

function deleteGroup(id) {
  _save(KEYS.groups,       getGroups().filter(g => g.id !== id));
  _save(KEYS.transactions, getTransactions().filter(t => t.groupId !== id));
}

// ---- Transactions ----

function getTransactions()            { return _load(KEYS.transactions); }
function getTransactionsByProfile(pid){ return getTransactions().filter(t => t.profileId === pid); }
function getTransactionsByGroup(gid)  { return getTransactions().filter(t => t.groupId === gid); }

function createTransaction({ profileId, categoryId, groupId, amount, type, date, note }) {
  const list = getTransactions();
  const item = {
    id: _genId(), profileId,
    categoryId: categoryId || null,
    groupId:    groupId    || null,
    amount:     parseFloat(amount),
    type, date: date || todayISO(),
    note: (note || '').trim(),
    createdAt: new Date().toISOString()
  };
  list.push(item);
  _save(KEYS.transactions, list);
  return item;
}

function updateTransaction(id, { categoryId, groupId, amount, type, date, note }) {
  _save(KEYS.transactions, getTransactions().map(t =>
    t.id === id ? { ...t, categoryId: categoryId||null, groupId: groupId||null,
                    amount: parseFloat(amount), type, date, note: (note||'').trim() } : t
  ));
}

function deleteTransaction(id) {
  _save(KEYS.transactions, getTransactions().filter(t => t.id !== id));
}

// ---- CSV Import / Export ----

function importCSV(csvText, profileId) {
  const lines = csvText.trim().split('\n').filter(l => l.trim());
  // Skip header row if present
  const start = lines[0].toLowerCase().startsWith('amount') ? 1 : 0;
  let imported = 0;
  const errors = [];
  lines.slice(start).forEach((line, i) => {
    const parts = line.split(',').map(p => p.trim());
    if (parts.length < 3) { errors.push(`Row ${i + 1 + start}: needs at least amount, category, type`); return; }
    const [amtStr, catName, typeRaw, dateStr, noteStr] = parts;
    const amount = parseFloat(amtStr);
    if (isNaN(amount) || amount <= 0) { errors.push(`Row ${i + 1 + start}: invalid amount "${amtStr}"`); return; }
    const type = typeRaw.toLowerCase().trim();
    if (!['income','expense'].includes(type)) { errors.push(`Row ${i + 1 + start}: type must be income or expense`); return; }
    const cat = getCategories().find(c => c.name.toLowerCase() === catName.toLowerCase());
    createTransaction({ profileId, categoryId: cat ? cat.id : null, groupId: null,
                        amount, type, date: dateStr || todayISO(), note: noteStr || '' });
    imported++;
  });
  return { imported, errors };
}

function exportCSV(profileId) {
  const txns = getTransactionsByProfile(profileId);
  const cats = getCategories();
  const rows = [['amount','category','type','date','note']];
  txns.forEach(t => {
    const cat = cats.find(c => c.id === t.categoryId);
    rows.push([t.amount, cat ? cat.name : 'Uncategorized', t.type, t.date, (t.note||'').replace(/,/g, ';')]);
  });
  return rows.map(r => r.join(',')).join('\n');
}

function triggerDownload(filename, content) {
  const blob = new Blob([content], { type: 'text/csv' });
  const url  = URL.createObjectURL(blob);
  const a    = document.createElement('a');
  a.href = url; a.download = filename; a.click();
  URL.revokeObjectURL(url);
}

// ---- Budget Goals ----

function getGoals()             { return _load(KEYS.goals); }
function getGoalsByProfile(pid) { return getGoals().filter(g => g.profileId === pid); }

function createGoal({ profileId, categoryId, targetAmount, note }) {
  const list = getGoals();
  const item = { id: _genId(), profileId, categoryId: categoryId||null,
                 targetAmount: parseFloat(targetAmount), note: (note||'').trim(), createdAt: todayISO() };
  list.push(item);
  _save(KEYS.goals, list);
  return item;
}

function updateGoal(id, { categoryId, targetAmount, note }) {
  _save(KEYS.goals, getGoals().map(g =>
    g.id === id ? { ...g, categoryId: categoryId||null, targetAmount: parseFloat(targetAmount), note: (note||'').trim() } : g
  ));
}

function deleteGoal(id) { _save(KEYS.goals, getGoals().filter(g => g.id !== id)); }

// ---- Reports helpers ----

function getSpendingByCategory(profileId) {
  const txns = getTransactionsByProfile(profileId).filter(t => t.type === 'expense');
  const map  = {};
  txns.forEach(t => { map[t.categoryId || '__none__'] = (map[t.categoryId || '__none__'] || 0) + t.amount; });
  return map;
}

function getIncomeByCategory(profileId) {
  const txns = getTransactionsByProfile(profileId).filter(t => t.type === 'income');
  const map  = {};
  txns.forEach(t => { map[t.categoryId || '__none__'] = (map[t.categoryId || '__none__'] || 0) + t.amount; });
  return map;
}

function getMonthlyTrend(profileId, type, months) {
  const txns  = getTransactionsByProfile(profileId).filter(t => t.type === type);
  const labels = [];
  const values = [];
  for (let i = months - 1; i >= 0; i--) {
    const d = new Date();
    d.setDate(1); d.setMonth(d.getMonth() - i);
    const y = d.getFullYear(), m = d.getMonth();
    labels.push(d.toLocaleString('default', { month: 'short', year: '2-digit' }));
    values.push(txns.filter(t => {
      const td = new Date(t.date);
      return td.getFullYear() === y && td.getMonth() === m;
    }).reduce((s, t) => s + t.amount, 0));
  }
  return { labels, values };
}

function getUnusualTransactions(profileId) {
  const txns = getTransactionsByProfile(profileId).filter(t => t.type === 'expense');
  if (txns.length < 3) return [];
  const amounts = txns.map(t => t.amount);
  const mean    = amounts.reduce((a, b) => a + b, 0) / amounts.length;
  const std     = Math.sqrt(amounts.reduce((a, b) => a + Math.pow(b - mean, 2), 0) / amounts.length);
  const cutoff  = mean + 2 * std;
  return txns.filter(t => t.amount > cutoff).sort((a, b) => b.amount - a.amount);
}

// ---- Seed default categories on first load ----

(function seedDefaults() {
  if (getCategories().length > 0) return;
  [
    { name: 'Food',           type: 'expense', description: 'Groceries, dining out' },
    { name: 'Bills',          type: 'expense', description: 'Utilities, subscriptions' },
    { name: 'Gas',            type: 'expense', description: 'Fuel for vehicles' },
    { name: 'Entertainment',  type: 'expense', description: 'Movies, hobbies, games' },
    { name: 'Healthcare',     type: 'expense', description: 'Medical, pharmacy' },
    { name: 'Clothing',       type: 'expense', description: 'Apparel and accessories' },
    { name: 'Transportation', type: 'expense', description: 'Public transit, rideshare' },
    { name: 'Other Expense',  type: 'expense', description: 'Miscellaneous expenses' },
    { name: 'Salary',         type: 'income',  description: 'Regular employment income' },
    { name: 'Gift',           type: 'income',  description: 'Money received as a gift' },
    { name: 'Freelance',      type: 'income',  description: 'Contract or gig work' },
    { name: 'Other Income',   type: 'income',  description: 'Miscellaneous income' }
  ].forEach(({ name, type, description }) => {
    const list = getCategories();
    list.push({ id: _genId(), name, type, description });
    _save(KEYS.categories, list);
  });
})();
