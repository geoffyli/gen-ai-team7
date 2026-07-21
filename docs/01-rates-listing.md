# 01 · Rates listing — `[DB→API→FE]` · sequenced (foundation)

**Start:** `git switch main && git pull && git switch -c feat/01-rates-listing`

**Story.** As a customer I want to see the latest rate for each currency pair.

**Acceptance criteria** (verify each against the running app)
- AC1 `GET /api/rates` → 200 + JSON array; each item `{base, quote, rate, rateDate}`.
- AC2 **Exactly one row per pair** — the one with the most recent `rateDate` (the `fx_rate` table holds history).
- AC3 EUR/USD reads **rate = 1.0818**, **rateDate = "2026-01-12"**.  ← checkpoint
- AC4 The home page shows a table of every pair; the EUR/USD row shows **1.0818**.
- AC5 Empty DB → `[]` and HTTP 200 (never a 500).

**Contract.** `GET /api/rates` → `[{ "base":"EUR","quote":"USD","rate":1.0818,"rateDate":"2026-01-12" }, …]`

**Out of scope.** No filtering, no history rows, no auth. Read-only.

**Pattern.** Copy the `com.fx.sample` slice (model → repository → controller) + `currencies.html`/`.js`.

**Tests** (README §7). A web-slice test for `GET /api/rates` (`@WebMvcTest`, mock the repo):
a happy path asserting EUR/USD = 1.0818, **and** the empty-DB case → `[]`.

**Done** (README §6). AC-verified on the branch, `./mvnw test` green, a teammate re-verified and
approved the PR, merged to `main`, and `main` is still green on a fresh clone.
