# The workflow, in plain terms

The whole loop on one page. (Your git branch is called `main` — some people say "master";
same thing.)

> **Branches are cheap and disposable. `main` is sacred.**
> Nothing reaches `main` until it works locally, works on CI, and a teammate has seen it run.

---

## Once, at the start
Check out the project and confirm it runs before you touch anything.
```bash
git clone <your-team-repo>
cd fx-app
docker compose up --build        # Welcome page loads, DB seeded → you're ready
```

## Then, for every feature — the loop

**1. Branch out.** One feature = one fresh branch off the latest `main`.
```bash
git switch main && git pull
git switch -c feat/NN-feature-name
```

**2. Vibe-code it with AI.** Hand the AI the requirement's spec (acceptance criteria + the
exact checkpoint numbers) and let it write the code. Read what it produces.

**3. If it doesn't work — reset, don't fight it.** The branch is disposable. When the AI
goes down a bad path, throw the mess away and try again with a sharper prompt — that's faster
than untangling it.
```bash
git restore .                    # discard uncommitted changes, back to last good point
# or nuke the whole attempt and start the branch over:
git switch main && git branch -D feat/NN-feature-name && git switch -c feat/NN-feature-name
```

**4. Work until the tests pass — locally first.** You're not done because it "looks right."
You're done when the tests are green on your machine.
```bash
./mvnw test                      # fast: unit + contract
./mvnw verify                    # full: adds the real-database test (needs Docker)
docker compose up                # then tick every acceptance criterion by hand
```
Keep looping 2 → 4 until all of that is green.

**5. Push, and let CI prove it.** Green on your laptop isn't enough — it has to be green on
a clean machine too.
```bash
git commit -am "feat(NN): ..." && git push
# open a Pull Request → the CI checks (test + smoke) run automatically → wait for green
```

**6. Only then, merge to `main`.** With CI green **and** a teammate's review, merge.
```bash
# Merge the PR, then everyone:
git switch main && git pull
```

**7. Confirm it's still green on `main`.** After the merge, make sure the tests pass on `main`
too — a fresh clone should run and the CI on `main` should be green. Your feature must not
break anyone else's.

---

## Why it's shaped this way
- **Reset over debugging AI slop** — a wrong AI attempt is cheaper to discard than to repair;
  the safety of a throwaway branch is what lets you vibe-code boldly.
- **Local green, then CI green** — local tells you fast; CI proves it works somewhere that
  isn't just your laptop.
- **Merge last, never first** — `main` is what the whole team builds on next. Keep it working.

*See the interactive version in [`sprint-flow.html`](sprint-flow.html), and the full detail
(tests, Definition of Done) in [`../README.md`](../README.md).*
