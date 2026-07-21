# 02 · Single-pair lookup — `[API+FE]` · sequenced (needs 01)

**Start:** `git switch main && git pull && git switch -c feat/02-pair-lookup`

**Story.** As a customer I want the rate for one specific pair.

**Acceptance criteria**
- AC1 `GET /api/rates/EUR/USD` → 200 + one object, **rate 1.0818**.  ← checkpoint
- AC2 Unknown pair (e.g. `EUR/XXX`) → **404** + JSON `{error}` message, no stack trace.
- AC3 The home page gains a "look up a pair" selector that shows the single rate.

**Contract.** `GET /api/rates/{base}/{quote}` → `{ "base":"EUR","quote":"USD","rate":1.0818,"rateDate":"2026-01-12" }`

**Out of scope.** No history, no conversion (that's 03).

**Coordinate.** Touches the **home page** like 01 — agree who owns that file, or rebase after 01 merges.

**Tests** (README §7). Web-slice test for `GET /api/rates/{base}/{quote}`: a happy path
(EUR/USD → 1.0818) **and** an unknown pair → **404** with a JSON `{error}`.

**Done** (README §6). AC-verified on the branch, `./mvnw test` green, a teammate re-verified and
approved the PR, merged to `main`, and `main` is still green on a fresh clone.
