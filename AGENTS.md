# AGENTS.md — how an AI assistant should help on this repo

*Read by Copilot / Claude / Cursor. The human rules and the requirements are in `README.md`
and `docs/`.*

## What this is
A full-stack FX app **skeleton** that already runs (`docker compose up`): seeded MySQL, Spring
Boot, static HTML/JS. A team is shipping an ordered backlog (`docs/01…10`) onto it — one branch
→ PR → merge each — in a ~3-hour sprint.

## Your job
Help the team **ship working features fast, and understand what you produced.** A feature nobody
on the team can explain is a liability, not a win.

## Rules (don't break)
1. **Obey the constitution** in `README.md` §5: fixed stack (Spring Boot 3.3 / Java 21 / MySQL /
   plain HTML-CSS-JS from `resources/static`); no frontend framework, no new heavy deps.
2. **Build only what the current requirement's acceptance criteria ask.** Respect its *Out of
   scope*. No auth, pagination, or extra endpoints nobody requested.
3. **Copy the sample slice** `com.fx.sample` (Currency → repository → controller) +
   `currencies.html`/`.js` as the pattern. Match its style. Data access is `JdbcTemplate` — no JPA.
4. **Use the pinned checkpoints — never invent numbers** (EUR/USD 1.0818; the fee tiers). Ask if unsure.
5. **No stack traces to the browser** — clean JSON via `com.fx.web.ApiExceptionHandler`.
6. **Explain as you go**, one line per change. Prefer small steps the team can follow.

## Working a feature
1. Restate the acceptance criteria as a short plan (files, endpoint, query, UI). Wait for "go".
2. Implement the smallest thing that satisfies them, following the sample slice.
3. Tell the human how to run and verify **each** criterion.
4. If one fails: was the brief ambiguous (fix the brief, regenerate) or a real bug (fix it, add a test)?

## Tests (part of Done — see README §6/§7)
Three tiers, each with a worked example on the sample feature — **copy the matching one**:
- **Unit / contract** (`./mvnw test`, no Docker) — copy `CurrencyControllerTest` /
  `CurrencyContractTest`. Every endpoint: a happy path **and** a failure path. Every
  calculation (fees, conversion): cover the **boundaries** (tier edges, min-fee floor).
- **DB integration** (`./mvnw verify`, needs Docker) — copy `CurrencyRepositoryIT`
  (Testcontainers → real MySQL). Add when the feature has real SQL (a query or a write).
- **Full-stack smoke** — the `smoke` job in `.github/workflows/ci.yml`; runs on every push.

When you add tests, tell the human **which command runs them** (`./mvnw test` for the fast
tier, `./mvnw verify` for the DB tier — Docker must be running). Aim for **≥70% coverage on
backend logic you write**; never leave a failing or skipped test. The sample's
`createsACurrency` test is the write-test pattern; `CurrencyRepositoryIT` is the real-DB pattern.
FE-only and DB-seed features have no unit test — verify in the browser / via `/api/health/db`.

## The database (seeded — read it, don't recreate it)
Schema `fxdb`: `currency`, `account`, `fx_rate` (history — "latest" = max `rate_date` per pair),
`transfer`. Data access is `JdbcTemplate` (no JPA). Don't change the schema unless a requirement
explicitly needs it.
