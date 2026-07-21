# 04 · Record & list transfers — `[DB-write→API→FE]` · sequenced (needs 03)

**Start:** `git switch main && git pull && git switch -c feat/04-transfers`

**Story.** Every conversion is recorded and visible as history.

**Acceptance criteria**
- AC1 A conversion writes a `transfer` row (from, to, amount, currency, executed_at, status).
- AC2 `GET /api/transfers` → JSON array, **newest first**.
- AC3 A **new** `/history` page lists them, newest at top.
- AC4 Do three conversions, then load history → three new rows in reverse-time order.

**Write pattern.** Copy the sample slice's **write** half — `POST /api/currencies` in
`CurrencyController` + `CurrencyRepository.add()` (`jdbc.update("INSERT …", …)`) + the "Add"
form in `currencies.html`/`.js`. Same shape here: `@PostMapping` a request body,
`jdbc.update("INSERT INTO transfer …", …)`. Verify the row landed (`/api/health/db` count rises).

**Out of scope.** No editing/deleting transfers; no balance checks (that's 06-adjacent work).

**Coordinate.** Touches the DB (a write) — one person owns any schema/seed change.

**Tests** (README §7). Web-slice tests (mock the repo): recording a conversion calls the insert
(→ persisted), and `GET /api/transfers` returns **newest-first**. Copy the sample's
`createsACurrency` test as the write-test pattern.

**Done** (README §6). AC-verified on the branch, `./mvnw test` green, a teammate re-verified and
approved the PR, merged to `main`, and `main` is still green on a fresh clone.
