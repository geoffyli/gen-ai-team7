# 05 · Validation & error handling — `[API+FE cross-cutting]` · sequenced (needs 02–04)

**Start:** `git switch main && git pull && git switch -c feat/05-validation-errors`

**Story.** The app never shows a stack trace to a customer; bad input gets a clear message.

**Acceptance criteria**
- AC1 Bad input (missing param, non-numeric amount, unknown pair) → **4xx** + JSON `{error}`;
  **never** a 500 with a stack trace.
- AC2 The UI shows the message inline, not a broken page.
- AC3 A `curl` of three deliberately-bad requests returns three clean messages (400 / 400 / 404).

**Where to build.** Extend `com.fx.web.ApiExceptionHandler` (add validation + a 404 for unknown pairs).
The skeleton already sets `server.error.include-stacktrace=never` — build on that.

**Out of scope.** No auth, no rate-limiting.

**Coordinate.** Touches **many controllers** — do it after 02–04 merge, or you'll conflict constantly.
Good one for a pair to own together.

**Tests** (README §7). Web-slice tests asserting each bad request returns the right status and a
JSON `{error}` (400 / 400 / 404) — **and never a 500 / stack trace**. One assertion per bad-input case.

**Done** (README §6). AC-verified on the branch, `./mvnw test` green, a teammate re-verified and
approved the PR, merged to `main`, and `main` is still green on a fresh clone.
