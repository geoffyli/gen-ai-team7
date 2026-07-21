// SAMPLE feature — the front-end half of the Currencies slice.
// Shows both halves of the pattern: READ (fetch the list) and WRITE (POST a new row).
// Copy this file as the template for your feature pages (rates.js, convert.js, ...).

// --- READ: GET /api/currencies and render the table ---
async function loadCurrencies() {
  const rows = document.getElementById('rows');
  const status = document.getElementById('status');
  try {
    const res = await fetch('/api/currencies');
    if (!res.ok) throw new Error('HTTP ' + res.status);
    const currencies = await res.json();

    if (currencies.length === 0) {
      rows.innerHTML = '<tr><td colspan="3" class="status">No currencies found.</td></tr>';
      return;
    }

    rows.innerHTML = currencies.map(c => `
      <tr>
        <td class="mono">${c.code}</td>
        <td>${c.name}</td>
        <td class="sym">${c.symbol ?? ''}</td>
      </tr>`).join('');
    status.textContent = `${currencies.length} currencies loaded from the database.`;
    status.classList.remove('err');
  } catch (err) {
    rows.innerHTML = '<tr><td colspan="3" class="status err">Could not load currencies.</td></tr>';
    status.textContent = 'Is the app running and the database seeded? Try /api/health/db. (' + err.message + ')';
    status.classList.add('err');
  }
}

// --- WRITE: POST /api/currencies with a JSON body, then re-read the list ---
async function addCurrency(event) {
  event.preventDefault();                       // don't reload the page
  const form = event.target;
  const formStatus = document.getElementById('form-status');
  const body = {
    code: form.code.value.trim().toUpperCase(),
    name: form.name.value.trim(),
    symbol: form.symbol.value.trim()
  };
  try {
    const res = await fetch('/api/currencies', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(body)
    });
    if (!res.ok) {
      const err = await res.json().catch(() => ({}));
      throw new Error(err.error || ('HTTP ' + res.status));   // show the API's 400 message
    }
    formStatus.textContent = `Added ${body.code}.`;
    formStatus.classList.remove('err');
    form.reset();
    loadCurrencies();                            // the write is only "done" once the read shows it
  } catch (err) {
    formStatus.textContent = 'Could not add: ' + err.message;
    formStatus.classList.add('err');
  }
}

loadCurrencies();
document.getElementById('add-form').addEventListener('submit', addCurrency);
