# 08 · Rate-history endpoint — `[API only]` · parallel

**Start:** `git switch main && git pull && git switch -c feat/08-rate-history`

**Story.** A customer wants to see how a pair moved over time.

**Acceptance criteria**
- AC1 `GET /api/rates/EUR/USD/history` → 200 + JSON array of every `fx_rate` row for that pair,
  **oldest → newest** by `rateDate`.
- AC2 For EUR/USD it returns **3** rows (2026-01-10, -11, -12) ending at **1.0818**.  ← checkpoint
- AC3 Unknown pair → **200 + `[]`** (an empty history is not an error).

**Contract.** `[{ "rate":1.0812,"rateDate":"2026-01-10" }, … , { "rate":1.0818,"rateDate":"2026-01-12" }]`

**This is API-only.** No UI (a later FE feature could chart it). A `curl` proves it.

**Overlaps (light).** Same web package as 07, and reads the same `fx_rate` table as 06.
Different files; coordinate only if you and the 07 owner edit one shared class.

**Tests** (README §7). Web-slice test for the history endpoint: EUR/USD → **3 rows oldest→newest**
ending 1.0818, and an unknown pair → **200 + `[]`**.

**Done** (README §6). AC-verified on the branch, `./mvnw test` green, a teammate re-verified and
approved the PR, merged to `main`, and `main` is still green on a fresh clone.
