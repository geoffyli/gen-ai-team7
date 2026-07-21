# Warm-up checklist — prove the pipeline before you build

Before anyone writes a feature, every team runs the **whole loop once with a trivial change**.
The goal isn't the code — it's to prove *repo → branch → PR → CI → merge* works end-to-end, so
you hit the setup snags now, not mid-feature. Aim: everyone at ✅ within the first 30–40 minutes.

---

## The steps (do them in order)

**1 · Create the repo + invite the team**
One member (the **owner**) creates an **empty** GitHub repo `fx-app-<team>` (no README).
Then **Settings → Collaborators** → add every teammate by username. Teammates **accept** the
invite (email or the repo's notifications). *Don't protect `main` yet — that comes after step 2.*

**2 · Push the skeleton as the initial commit**
Owner downloads the skeleton **zip**, unzips it, and pushes it to `main`:
```bash
cd fx-app
git init && git add -A && git commit -m "chore: fx-app skeleton"
git branch -M main
git remote add origin https://github.com/<owner>/fx-app-<team>.git
git push -u origin main
```
Now turn protection on: **Settings → Branches → protect `main`** (require a PR + 1 review).
Everyone else: `git clone` the repo and run `docker compose up` once to confirm it starts.

**3 · First branch, one-line change, push**
Each member proves they can branch and push. Make a *trivial* visible change — e.g. add your
team name to the top of `README.md`.
```bash
git switch main && git pull
git switch -c chore/hello-<yourname>
#   …edit README.md: add "## Team <name>" …
git commit -am "chore: hello from <name>"
git push -u origin chore/hello-<yourname>
```

**4 · Open a PR → watch CI go green**
Open a pull request into `main`. On the **Actions** tab, watch the CI checks (**test** + **smoke**)
run and turn **green** ✅. (Red? Read the log — that's the point of doing this now.)

**5 · Merge to main**
A teammate **approves** the PR (branch protection requires it), then **merge**. Everyone:
```bash
git switch main && git pull
```

**6 · ✅ Warm-up complete**
The pipeline works: you can branch, push, pass CI, review, and merge. **Now start the real
requirements** in [`01-rates-listing.md`](01-rates-listing.md) → and track them in
`progress-tracker.csv`.

---

## Instructor tracking grid (all 10 teams)

Tick each cell as a team clears it. A team is "green" (ready for real work) at the ✅ column.

| Team | 1 · Repo + collaborators | 2 · Skeleton pushed | 3 · Branch + push | 4 · CI green | 5 · Merged to main | ✅ Ready |
|------|:---:|:---:|:---:|:---:|:---:|:---:|
| 1.  | ☐ | ☐ | ☐ | ☐ | ☐ | ☐ |
| 2.  | ☐ | ☐ | ☐ | ☐ | ☐ | ☐ |
| 3.  | ☐ | ☐ | ☐ | ☐ | ☐ | ☐ |
| 4.  | ☐ | ☐ | ☐ | ☐ | ☐ | ☐ |
| 5.  | ☐ | ☐ | ☐ | ☐ | ☐ | ☐ |
| 6.  | ☐ | ☐ | ☐ | ☐ | ☐ | ☐ |
| 7.  | ☐ | ☐ | ☐ | ☐ | ☐ | ☐ |
| 8.  | ☐ | ☐ | ☐ | ☐ | ☐ | ☐ |
| 9.  | ☐ | ☐ | ☐ | ☐ | ☐ | ☐ |
| 10. | ☐ | ☐ | ☐ | ☐ | ☐ | ☐ |

---

## Watch for these (the usual snags)
- **Invite not accepted** → teammate can't push. Check email / GitHub notifications first.
- **Protected `main` before the first push** → owner's initial push is blocked. Push *then* protect.
- **`docker compose up` port clash** → `docker compose down -v` (app 8080, DB 3307).
- **JAVA_HOME wrong (Windows)** → set it to JDK 21 for the session.
- **CI red on first PR** → almost always a setup issue surfacing exactly when it should. Read the log.
