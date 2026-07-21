# 10 · Filter the currencies list — `[FE only]` · parallel

**Start:** `git switch main && git pull && git switch -c feat/10-currencies-filter`

**Story.** With many currencies, I want to filter the list as I type.

**Acceptance criteria**
- AC1 The existing **Currencies** page gains a text box above the table.
- AC2 Typing (e.g. "US" or "€") filters the rows **client-side** — no page reload, no new request.
- AC3 Clearing the box shows all 8 rows again; a no-match shows a friendly "no currencies" row.

**This is FE-only.** All in `currencies.html` / `currencies.js` against the existing
`/api/currencies` — no backend change. (Builds on the sample feature, not on 01–05.)

**Overlaps (light).** Touches the shared **nav**/static area like 09 — trivial to merge.

**Tests** (README §7). **FE-only — no unit test.** Verify in the browser: typing filters live,
clearing restores all 8 rows, a no-match shows the friendly row, no console errors. **Say so in the
PR** ("FE-only, verified in browser").

**Done** (README §6). AC-verified on the branch, `./mvnw test` still green, a teammate re-verified
and approved the PR, merged to `main`, and `main` is still green on a fresh clone.
