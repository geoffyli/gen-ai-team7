// History feature — the front-end half of the transfers slice.
// READ: GET /api/transfers, already ordered newest-first by the server.

async function loadHistory() {
  const rows = document.getElementById("rows");
  const status = document.getElementById("status");
  try {
    const res = await fetch("/api/transfers");
    if (!res.ok) throw new Error("HTTP " + res.status);
    const transfers = await res.json();

    if (transfers.length === 0) {
      rows.innerHTML =
        '<tr><td colspan="6" class="status">No transfers yet.</td></tr>';
      status.textContent = "";
      return;
    }

    rows.innerHTML = transfers
      .map(
        (t) => `
      <tr>
        <td class="mono">${t.fromAccount}</td>
        <td class="mono">${t.toAccount}</td>
        <td>${t.amount}</td>
        <td class="mono">${t.currency}</td>
        <td>${t.executedAt}</td>
        <td>${t.status}</td>
      </tr>`,
      )
      .join("");
    status.textContent = `${transfers.length} transfers loaded, newest first.`;
    status.classList.remove("err");
  } catch (err) {
    rows.innerHTML =
      '<tr><td colspan="6" class="status err">Could not load history.</td></tr>';
    status.textContent =
      "Is the app running and the database seeded? (" + err.message + ")";
    status.classList.add("err");
  }
}

loadHistory();
