// Rates feature — the front-end half of the "latest rates" + "look up a pair" slice.
// Lives on the Welcome page (index.html). Copy of the pattern in js/currencies.js.

// --- READ: GET /api/rates and render the table ---
async function loadRates() {
  const rows = document.getElementById("rates-rows");
  const status = document.getElementById("rates-status");
  if (!rows) return; // this script is shared; skip quietly on pages without the table
  try {
    const res = await fetch("/api/rates");
    if (!res.ok) throw new Error("HTTP " + res.status);
    const rates = await res.json();

    if (rates.length === 0) {
      rows.innerHTML =
        '<tr><td colspan="4" class="status">No rates found.</td></tr>';
      status.textContent = "";
      return;
    }

    rows.innerHTML = rates
      .map(
        (r) => `
      <tr>
        <td class="mono">${r.base}</td>
        <td class="mono">${r.quote}</td>
        <td>${r.rate}</td>
        <td>${r.rateDate}</td>
      </tr>`,
      )
      .join("");
    status.textContent = `${rates.length} pairs loaded from the database.`;
    status.classList.remove("err");
  } catch (err) {
    rows.innerHTML =
      '<tr><td colspan="4" class="status err">Could not load rates.</td></tr>';
    status.textContent =
      "Is the app running and the database seeded? (" + err.message + ")";
    status.classList.add("err");
  }
}

// --- Pair lookup: GET /api/rates/{base}/{quote} ---
async function lookupPair(event) {
  event.preventDefault();
  const form = event.target;
  const result = document.getElementById("lookup-result");
  const base = form.base.value.trim().toUpperCase();
  const quote = form.quote.value.trim().toUpperCase();
  try {
    const res = await fetch(`/api/rates/${base}/${quote}`);
    if (!res.ok) {
      const err = await res.json().catch(() => ({}));
      throw new Error(err.error || "HTTP " + res.status);
    }
    const rate = await res.json();
    result.textContent = `${rate.base}/${rate.quote} = ${rate.rate} (as of ${rate.rateDate})`;
    result.classList.remove("err");
  } catch (err) {
    result.textContent = "Could not look up pair: " + err.message;
    result.classList.add("err");
  }
}

loadRates();
const lookupForm = document.getElementById("lookup-form");
if (lookupForm) lookupForm.addEventListener("submit", lookupPair);
