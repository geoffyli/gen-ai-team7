# 09 · About page — `[FE only]` · parallel

**Start:** `git switch main && git pull && git switch -c feat/09-about-page`

**Story.** Visitors want to know what this app is and who built it.

**Acceptance criteria**
- AC1 A new static page `about.html` (in `src/main/resources/static/`) served at `/about.html`.
- AC2 It shows the app name, a one-line description, the team members, and the features shipped.
- AC3 It's styled with the existing `css/app.css` and added to the nav bar on every page.
- AC4 Loads on a clean `docker compose up` with no console errors.

**This is FE-only.** Pure HTML/CSS/JS — no backend, no DB. Copy `index.html` as a starting point.

**Overlaps (light).** Touches the shared **nav** like 10 — whoever edits the nav markup on two
pages should say so; it's a small, easy merge.

**Tests** (README §7). **FE-only — no unit test.** Verify in the browser: `/about.html` loads,
is styled, is in the nav on every page, no console errors. **Say so in the PR** ("FE-only, verified
in browser").

**Done** (README §6). AC-verified on the branch, `./mvnw test` still green, a teammate re-verified
and approved the PR, merged to `main`, and `main` is still green on a fresh clone.
