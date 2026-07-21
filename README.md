# FX App — GenAI Team Sprint

A **running full-stack skeleton** (MySQL + Spring Boot + HTML/CSS/JS). Your team grows it into
a working currency-exchange app this afternoon by shipping features — using AI to go fast, and
**git to stay in sync**. The whole point: *how far can a team get in one afternoon, with every
feature actually working?*

> ## The one rule
> **A feature is done only when it WORKS and is MERGED to `main`.**
> "Works" = a teammate ran the app and ticked every acceptance criterion. A merge that breaks
> `main` costs the whole team — so verify, then merge. Working-and-merged is the only score.

---

## 1. Run it

```bash
docker compose up --build      # MySQL (seeded) + backend + front end, one command
```
- App + front end → **http://localhost:8080/** (Welcome) and `/currencies.html` (sample feature)
- Checks: `/health` → `{"status":"UP"}` · `/api/health/db` → table counts (`currency:8, account:20, fx_rate:30, transfer:200`)
- MySQL is published on host **3307** (not 3306, so it won't clash with your local MySQL). Stop: `docker compose down` (`-v` also wipes the DB).
- *Windows/JAVA_HOME:* if `./mvnw` picks the wrong JDK, set `JAVA_HOME` to JDK 21 for the session. Docker sidesteps this.

## 2. Set up the team repo (first 15 min, together)

1. **Owner:** create an empty GitHub repo, push this skeleton (`git init … git push -u origin main`),
   then **Settings → Collaborators** (add teammates) and **Branches → protect `main`** (require a PR + 1 review).
2. **Everyone:** clone it, run `docker compose up`, confirm the Welcome page loads *before* building anything.
3. Agree who takes which requirement (see the `docs/01…10` files) so two people don't build the same one. *(The instructor tracks all teams in `docs/progress-tracker.csv`.)*

## 3. Ship a feature (repeat all afternoon)

```
git switch main && git pull              # start from the freshest main
git switch -c feat/NN-slug               # one branch per requirement (each doc starts with this)
   …build it (AI + hand-code — both allowed)…
./mvnw verify                            # run the full test suite yourself (§7) — must be green
docker compose up  → tick every AC       # then verify each AC against the live app
git commit -am "feat(NN): …" && git push
   open a PR → list the ACs → a teammate runs it, ticks them, approves & merges
git switch main && git pull              # everyone pulls the merged main
```
Small branches, merged often. Pull `main` before you branch **and** before you PR — that's where
you catch conflicts on your machine instead of the reviewer's.

## 4. Working with AI (go fast, don't get burned)

You may hand-code, but when you let the AI write a whole feature, use this loop:
**describe precisely → generate → verify → refine.** Give it the acceptance criteria (with the
exact numbers — it can't guess `1.0818`), let it implement, then check **each AC against the
running app**. If something's wrong, first ask *"was my brief unambiguous?"* — usually it wasn't;
sharpen the criterion and regenerate rather than silently patching. *Your output quality tracks
your brief quality.* The `AGENTS.md` file tells your assistant the house rules.

## 5. The rules every feature obeys (the constitution)

- **Stack is fixed:** Spring Boot 3.3 / Java 21 / MySQL / plain HTML-CSS-JS from `resources/static`. No frontend framework, no new heavy deps.
- **Checkpoints (never invent numbers):** EUR/USD 2026-01-12 = **1.0818**; fee tiers retail `<1000`→1.0%, `1000–9999`→0.5%, `≥10000`→0.25%, min **1.00**; "latest rate" = max `rateDate` per pair.
- **No stack traces to the browser** — errors return clean JSON (`server.error.include-stacktrace=never` is already set; extend `com.fx.web.ApiExceptionHandler`).
- **Build only what the requirement asks** — respect its *Out of scope*; don't gold-plate.
- **Copy the sample slice:** `com.fx.sample` (Currency → repository → controller) + `currencies.html`/`.js` is your pattern for every feature — it shows **both** a read (`GET`) and a write (`POST` + `jdbc.update` INSERT + a form).

## 6. Definition of Done (every requirement — no exceptions)

A requirement is **done** only when **all** of these hold. This *is* the checklist a reviewer
runs on your PR:

1. **On the `feat/NN-…` branch:** `./mvnw verify` is **green** (unit + contract + DB
   integration — every test passes, none skipped), **and** `docker compose up` runs, **and**
   you have ticked **every acceptance criterion** in the requirement doc against the live app.
   *(No Docker on your machine? `./mvnw test` runs the fast Docker-free subset; CI runs the
   rest — see §7.)*
2. **The new behaviour has tests** and they pass (see §7 for what to add and how to run it).
3. **A teammate reviewed the PR:** they checked out your branch, ran `./mvnw verify` (green),
   ran the app, and **re-ticked every AC** themselves — then approved.
4. **Merged to `main`** through that PR (never a direct push to `main`).
5. **`main` is still green after the merge:** on a **fresh clone**, `./mvnw verify` passes and
   `docker compose up` works — and the CI checks on the PR are green — your feature broke
   nobody else's.

**Not done:** an AC you didn't check · a skipped or failing test · "works on my laptop" but not
on a fresh clone · code merged without a review. Record it as **MERGED** in
`docs/progress-tracker.csv` only once all five are true.

## 7. Tests — the three tiers, what to add, and how to run them

The skeleton ships one worked example of **each** tier on the sample Currency feature. You
don't need to understand them deeply today — **copy the matching example** when your feature
needs that tier.

| Tier | Runs with | Needs Docker? | Copy this example | Add it when your feature… |
|---|---|---|---|---|
| **Unit / contract** | `./mvnw test` | no | `CurrencyControllerTest`, `CurrencyContractTest` | …adds any endpoint or calculation (**always**) |
| **DB integration** | `./mvnw verify` | **yes** | `CurrencyRepositoryIT` (Testcontainers → real MySQL) | …has real SQL: a query or a write (01, 04, 06, 07, 08) |
| **Full-stack smoke** | CI job / `docker compose up` + curl | **yes** | the `smoke` job in `.github/workflows/ci.yml` | …runs automatically on every push — you rarely touch it |

**What every feature must add:**
- **Every new REST endpoint** → a **web-slice test** (`@WebMvcTest`, mock the repo): a
  **happy path** *and* **one failure path** (bad input → 400, unknown → 404).
- **Every calculation / business rule** (fees, conversion, "latest per pair") → a **unit
  test** covering the normal case **and the boundaries** (each fee tier edge *and* the min-fee floor).
- **Real SQL** (a tricky query, a write) → optionally a **DB integration test** (`*IT`, copy
  `CurrencyRepositoryIT`) so it's checked against a real MySQL, not a mock.
- **FE-only** (09, 10) and the **DB-seed** feature (06) have no unit test — verify in the
  browser / via `/api/health/db` and **say so in the PR**.
- **Bar:** the code you add is covered — rule of thumb **≥ 70% line coverage on backend logic
  you write**. **No PR merges with a failing or skipped test.**

### Verify locally after you finish a change (before you open the PR)

You need **JDK 21** and **Docker Desktop running** (the DB-integration and smoke tiers spin up
real containers).

**One-time preflight** (do it once before class — the first Maven run downloads dependencies,
needs internet):
```bash
./mvnw -v            # confirm Java 21  (Windows: set JAVA_HOME to your JDK 21 for the session)
docker ps            # confirm Docker Desktop is running
docker pull mysql:8  # pre-pull so the first DB test isn't a slow surprise
```

**Every time you finish a change**, run this from the repo root and watch it all pass:
```bash
# 1) fast tier — unit + contract (no Docker)
./mvnw test

# 2) full tier — adds the Testcontainers DB integration test (needs Docker)
./mvnw verify

# 3) smoke — boot the whole stack and hit it like a user would
docker compose up --build -d
curl -s localhost:8080/health                 # {"status":"UP"}
curl -s localhost:8080/api/health/db          # currency:8, account:20, fx_rate:30, transfer:200
curl -s localhost:8080/api/currencies         # your data, €/£/¥ rendering correctly
#   …plus a curl for the endpoint your change added, and click through the page in a browser…
docker compose down -v                        # stop + reset the DB when you're done
```
Only when **all three pass** and you've ticked every acceptance criterion is the change ready to
commit, push, and open a PR (see §6, Definition of Done).

**Gotcha — "port is already allocated":** a leftover container is still up. Clear it with
`docker compose down -v`, then retry. (App is on **8080**, the DB on **3307**.)

**Docker running → the DB test runs; Docker not running → it skips** and `./mvnw verify`
still succeeds (it won't block you). So just **start Docker Desktop** if you want the DB tier
to actually execute. And **CI runs all three tiers on every push**, so the DB and smoke tests
always gate your PR regardless of your local setup.

## 8. The requirements → `docs/`

Each is one branch → PR → merge. **01–05 are sequenced** (each builds on the last); **06–10 run
in parallel** (each is a single layer — DB, API, or FE — so different people can own them at once;
they overlap only lightly).

| # | Requirement | Layer | Order |
|---|---|---|---|
| [01](docs/01-rates-listing.md) | Rates listing | DB→API→FE | sequenced (foundation) |
| [02](docs/02-pair-lookup.md) | Single-pair lookup | API+FE | sequenced (needs 01) |
| [03](docs/03-conversion-calculator.md) | Conversion calculator | API+FE | sequenced (needs 01) |
| [04](docs/04-transfers.md) | Record & list transfers | DB-write→API→FE | sequenced (needs 03) |
| [05](docs/05-validation-errors.md) | Validation & error handling | API+FE | sequenced (needs 02–04) |
| [06](docs/06-db-currency-pairs.md) | Tradeable-pairs table | **DB only** | parallel |
| [07](docs/07-api-stats.md) | Market-stats endpoint | **API only** | parallel |
| [08](docs/08-api-rate-history.md) | Rate-history endpoint | **API only** | parallel |
| [09](docs/09-fe-about-page.md) | About page | **FE only** | parallel |
| [10](docs/10-fe-currencies-filter.md) | Filter the currencies list | **FE only** | parallel |

Land 01–04 working and you've had a strong afternoon. Nobody's expected to reach 10.

**Lost? Open [`docs/sprint-flow.html`](docs/sprint-flow.html) in a browser** — an interactive map
of the whole flow (fork → branch → build → test → verify → PR → CI → review → merge) and a live
board of what's done and what's next.

## Team 07

- Geoff Li
- 
- 
- 

---

<details>
<summary><strong>Instructor notes</strong> (not part of the student flow)</summary>

- **~15 min theory** on GenAI-for-code: great at boilerplate/wiring you haven't learned yet;
  burns you with confidently-wrong numbers and silent scope creep. It's a fast junior, not an oracle.
- **Mob 01 together** first (de-risks the loop, avoids the first home-page conflict, puts a working
  feature on `main` inside the hour), then fan out.
- **Engineer one merge conflict per team** (put two people on 02 + a parallel FE item) and coach the
  resolution — a resolved conflict is a better outcome than a clean feature.
- **Assessment lens:** working merges on `main` (does a fresh clone run?) > git health (PRs, reviews,
  a resolved conflict, green `main`) > verification discipline (did they catch the AI's misses?) >
  coordination. Say so at the start and the end.
- Same skeleton-plus-specs shape as the Week-3 neo-capstone — this is the rehearsal.
</details>
