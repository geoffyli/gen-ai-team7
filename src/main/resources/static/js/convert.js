// Convert feature — the front-end half of the conversion-calculator slice.
// READ: GET /api/convert to compute amount -> converted + fee + total.
// WRITE: POST /api/transfers to record the conversion as a transfer (the 04 slice).

let lastConversion = null; // { base, quote, converted } — needed to record the transfer

async function convert(event) {
  event.preventDefault();
  const form = event.target;
  const status = document.getElementById("convert-status");
  const resultPanel = document.getElementById("convert-result");
  const savePanel = document.getElementById("save-panel");

  const base = form.base.value.trim().toUpperCase();
  const quote = form.quote.value.trim().toUpperCase();
  const amount = form.amount.value;

  try {
    const params = new URLSearchParams({ base, quote, amount });
    const res = await fetch("/api/convert?" + params.toString());
    if (!res.ok) {
      const err = await res.json().catch(() => ({}));
      throw new Error(err.error || "HTTP " + res.status);
    }
    const result = await res.json();

    document.getElementById("r-amount").textContent =
      `${result.amount} ${base}`;
    document.getElementById("r-rate").textContent = result.rate;
    document.getElementById("r-converted").textContent =
      `${result.converted} ${quote}`;
    document.getElementById("r-fee").textContent = `${result.fee} ${quote}`;
    document.getElementById("r-total").textContent = `${result.total} ${quote}`;

    resultPanel.style.display = "block";
    savePanel.style.display = "block";
    status.textContent = "";
    status.classList.remove("err");

    lastConversion = { base, quote, converted: result.converted };
    document.getElementById("save-status").textContent = "";
  } catch (err) {
    resultPanel.style.display = "none";
    savePanel.style.display = "none";
    status.textContent = "Could not convert: " + err.message;
    status.classList.add("err");
  }
}

// --- WRITE: POST /api/transfers, recording this conversion as a transfer ---
async function saveTransfer(event) {
  event.preventDefault();
  const form = event.target;
  const saveStatus = document.getElementById("save-status");
  if (!lastConversion) return;

  const body = {
    fromAccount: Number(form.fromAccount.value),
    toAccount: Number(form.toAccount.value),
    amount: lastConversion.converted,
    currency: lastConversion.quote,
  };
  try {
    const res = await fetch("/api/transfers", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(body),
    });
    if (!res.ok) {
      const err = await res.json().catch(() => ({}));
      throw new Error(err.error || "HTTP " + res.status);
    }
    saveStatus.textContent = "Saved. See it on the History page.";
    saveStatus.classList.remove("err");
  } catch (err) {
    saveStatus.textContent = "Could not save: " + err.message;
    saveStatus.classList.add("err");
  }
}

document.getElementById("convert-form").addEventListener("submit", convert);
document.getElementById("save-form").addEventListener("submit", saveTransfer);
