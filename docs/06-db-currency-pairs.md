# 06 · Tradeable-pairs table — `[DB only]` · parallel

**Start:** `git switch main && git pull && git switch -c feat/06-currency-pairs`

**Story.** The business wants an explicit list of which pairs are tradeable (not every
currency combination is offered).

**Acceptance criteria**
- AC1 A new table `currency_pair (base_code CHAR(3), quote_code CHAR(3), active BOOLEAN)`
  is created and seeded, in `ops/fxdb-seed.sql` (so a fresh `docker compose up` builds it).
- AC2 It's seeded with the pairs that exist in `fx_rate` (EUR/USD, USD/JPY, GBP/USD, …), all `active=1`.
- AC3 `/api/health/db` (already in the skeleton) lists `currency_pair` with its row count.

**This is DB-only.** No new endpoint, no UI. Prove it with SQL / the health endpoint.

**Overlaps (light).** Reads the same `fx_rate` data as 08; both touch nothing else. If 08 runs
in parallel, you won't collide — different files.

**Tests** (README §7). **DB-only — no unit test.** Verify with a fresh `docker compose down -v && up`:
`/api/health/db` lists `currency_pair` with its row count. **Say so explicitly in the PR** ("DB-only,
verified via health endpoint").

**Done** (README §6). AC-verified on the branch (`down -v && up` builds the seeded table),
`./mvnw test` still green, a teammate re-verified and approved the PR, merged to `main`, and `main`
is still green on a fresh clone.
