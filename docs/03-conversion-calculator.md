# 03 · Conversion calculator — `[API+FE]` · sequenced (needs 01)

**Start:** `git switch main && git pull && git switch -c feat/03-conversion-calculator`

**Story.** As a customer I want to convert an amount and see the fee.

**Acceptance criteria**
- AC1 `GET /api/convert?base=EUR&quote=USD&amount=100` → `{amount, rate, converted, fee, total}`.
- AC2 `converted` uses the latest rate: 100 EUR → **108.18** USD.  ← checkpoint
- AC3 `fee` uses the fee tiers (retail: `<1000`→1.0%, `1000–9999`→0.5%, `≥10000`→0.25%, **min 1.00**).
  Check two: amount **5000 → fee 25.00**; amount **100 → fee 1.00** (1% = 1.00, meets the floor).
- AC4 A **new** `/convert` page: pick pair, enter amount → shows converted + fee + total.
- AC5 Amount ≤ 0 → friendly error, no conversion.

**Out of scope.** No persistence yet (that's 04), no account balances (that's 04/parallel).

**Pattern.** New endpoint + **new page** — parallel-safe with 02.

**Tests** (README §7). **Unit tests for the fee tiers** — cover **each tier edge and the min-fee
floor**, including the checkpoints 5000 → 25.00 and 100 → 1.00; **plus** a web-slice test for
`/api/convert` (happy path → 108.18, and amount ≤ 0 → 400). This feature has real logic — it needs
the most tests.

**Done** (README §6). AC-verified on the branch, `./mvnw test` green, a teammate re-verified and
approved the PR, merged to `main`, and `main` is still green on a fresh clone.
