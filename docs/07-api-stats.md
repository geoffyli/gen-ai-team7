# 07 · Market-stats endpoint — `[API only]` · parallel

**Start:** `git switch main && git pull && git switch -c feat/07-api-stats`

**Story.** An operator wants a one-call summary of activity.

**Acceptance criteria**
- AC1 `GET /api/stats` → 200 + `{ totalTransfers, busiestCurrency, latestRateDate }`.
- AC2 `totalTransfers` = **200** (matches the seeded `transfer` count in `/api/health/db`).  ← checkpoint
- AC3 `busiestCurrency` = the `currency_code` with the most transfers. Verify against
  `SELECT currency_code, COUNT(*) c FROM transfer GROUP BY currency_code ORDER BY c DESC LIMIT 1;`
- AC4 `latestRateDate` = **"2026-01-12"** (the newest `rate_date`).  ← checkpoint

**This is API-only.** JSON only — no UI. A `curl` proves it.

**Overlaps (light).** Adds a controller in the same web package as 08 — two people here will
touch neighbouring files. Small, resolvable; talk before you both edit one class.

**Tests** (README §7). Web-slice test for `GET /api/stats` (`@WebMvcTest`, mock the repo):
assert the three fields, including `totalTransfers` = 200 and `latestRateDate` = "2026-01-12".

**Done** (README §6). AC-verified on the branch, `./mvnw test` green, a teammate re-verified and
approved the PR, merged to `main`, and `main` is still green on a fresh clone.
