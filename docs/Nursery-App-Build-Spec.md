# Nursery Management App — Build Specification (v1)

**Purpose:** Replace the paper notebook for a vegetable seedling nursery (chilli, tomato, brinjal, capsicum, cabbage…) doing mostly custom/contract raising. Target users: small–medium nurseries in Andhra Pradesh / Telangana. Owner + workers. Telugu-first.

**Status:** A clickable front-end prototype exists (`nursery-prototype.html`) that demonstrates the full UX. This spec is what a developer builds into a real product with a backend. The prototype is the visual reference; this document is the source of truth for behaviour.

---

## 1. Core concepts (locked decisions)

**Order ≠ Batch.** These are separate, linked entities:
- **Order** = the farmer's deal. Holds the contract price, all payments, discount, write-off, closed state. One order per farmer request.
- **Batch** = a physical sowing run (a set of trays sown on one date). One order can be split into **multiple batches** (sown on different days / when tray space frees up). Batches hold production data only (counts, timeline, trays). Money lives on the order, not the batch.

**Money model (per order):**
- Contract total = quantity × rate, where rate is **per plant or per tray** (owner picks per order).
- A **running payment ledger** — any number of payments (advance, partials, later payments), each dated.
- At close, an optional **flat discount**.
- An order can be **closed with a balance still owed** (farmer pays later).
- After close, remaining balance can be paid, or **written off with a note** (maafi).
- Balance = contract − payments − discount − write-off.
- **Dues list** = every order (open OR closed) with balance > 0.

**Trays / capacity:**
- Total tray capacity is an owner-set **Settings** value (editable).
- **Reserve at booking** (soft hold on unseeded quantity), **occupy at seeding** (hard use), **release proportionally on each partial dispatch**, rest **released at order close**. No double-count (reserve for unseeded remainder + occupy for sown batches).
- Free = total − reserved − occupied. Over-capacity booking is **blocked** at save.

**Dates (forward model):** Owner enters the **seeded date**; app computes **ready date = seeded + crop lead time**. (Lead times: chilli ~40, tomato ~24, brinjal ~35, capsicum ~50, cabbage ~30 days — editable per crop.) A backward "want-by → sow-by" mode is a possible future toggle.

**Seed source:** per order — farmer-supplied or nursery-purchased. Nursery-purchased seed is a nursery expense; farmer-supplied is not.

---

## 2. Data model (entities & key fields)

**Order:** id, farmer, phone, crop, variety, qty, seededDate, readyDate, seed(source), priceMode(plant|tray), rate, seededQty, batchIds[], payments[{date, amount, kind}], discount, writeoff{amount, note}, closed.

**Batch:** id, orderId, crop, variety, seed, ordered(portion), sown(+germination buffer ~5%), alive, traysNum, sowDate, readyDate, currentDay(=today−sowDate), stage(germinating→growing→hardening→ready→closed), closed, timeline[].

**Timeline event:** day (relative to sow), date, type(seed|spray|water|count|dispatch|close), text, by(worker), qty(for dispatch).

**Task:** id, title, type(water|spray|seed|other), scopes[] (batch ids or ["nursery"]) OR order (for seed tasks), worker, repeat(daily|everyN|weekly|once), n, startDate, log{ "YYYY-MM-DD": {status:done|missed, note} }.

**Worker:** name, phone, payType(daily|monthly), rate, joinDate, attendance{ "YYYY-MM-DD": P|A }, advances[{date, amount}], payslips[{date, net, days, adv}].

**Expense:** category, amount, date, note, (optional link to batch/order).

**Request:** incoming farmer enquiry (pre-order) — accept → creates Order; reject → dismiss.

**Settings:** trayTotal, crop lead times, language.

---

## 3. Modules & behaviour

### Bookings / Orders
- Owner takes a booking directly (form) or accepts an incoming request.
- Fields: farmer, phone, crop, variety, qty, seeded date, seed source, price mode + rate. App shows contract total and ready date live, and a capacity check (blocks save if over capacity).
- Edit anytime; once seeded, crop/variety/price/qty **locked** (only contact editable) for data integrity.
- Delete allowed only if no batches exist. Reject request (with confirm).

### Seeding → Batches
- Seed an order in full or in **parts** (enter quantity now, rest later → separate batch). Each seeding records date + which worker sowed.
- Two ways to seed: (a) owner directly on the order; (b) **assign a seeding task** to a worker (order dropdown) — worker completes it, is asked how many sown, batch is created.

### Batch tracking
- Day-wise timeline: Day-1 seeded (by whom) → sprays → counts → dispatches → close. Each event dated with the person.
- **Three counts:** ordered → sown → alive. Alive updated via a **count/mortality** action (records loss).
- **Dispatch** (partial takeout) reduces remaining; capped at remaining (can't go negative); frees trays proportionally.
- Stage should auto-advance from sow date vs today (see §5 — backend).

### Money (per order)
- Full ledger view: contract line, each payment (editable/deletable), discount, write-off, balance.
- Add payment anytime. Close order (optional discount, confirm). Write off remaining (with note, confirm).
- Dues screen lists all open+closed orders with balance > 0.

### Expenses & profit
- Add expense (category, amount, note). Edit amount. Month profit = income − expenses. "Today's outgoing" running total.

### Tasks (calendar-driven)
- Types: water, spray, seed, other. Repeat: daily / every N days / weekly / once. Start: today / pick date / **from batch sowing day** (anchors schedule to Day-1).
- A task appears **only on its due days** (computed from repeat + start). Multi-batch selection for one task (e.g. spray B-207 + B-214). Completing a spray/water task auto-writes the event onto each target batch's timeline.
- Complete, or mark **not-done with a note**. Edit or stop/delete a task.
- Tasks and attendance shown on a **week/month calendar**; tap a day to see/mark that day.
- A task on only-closed batches stops appearing.

### Labour
- Both **daily-wage and monthly-salary** workers. Attendance via calendar (P/A per day). Advances (deducted from net). Net = earned − advances (never pay if negative). Pay salary → records a **payslip**, logs expense, resets the cycle (with confirm). Worker details editable; add/delete workers.
- **Per-worker day-wise history:** attendance + tasks done/missed + batch activities they performed.

### Farmer-facing
- Prototype fires a "WhatsApp sent" toast on accept/ready/etc. Real intake channel is an **open decision** (see §6). v1 likely: manual owner entry + outbound WhatsApp status messages; farmer app later.

### Dashboard
- New orders, active batches, today's outgoing, total dues, free tray capacity, batches ready soon.

### Settings
- Total trays, crop lead times, language.

---

## 4. Cross-cutting requirements
- **Offline-first** (local DB + sync) — rural connectivity is poor. This is essential, not optional.
- **Telugu-first** UI, with English technical terms in brackets; large buttons, few taps, icons for low-literacy users.
- **Role-based access:** owner sees everything; worker sees only their own tasks and no money screens (blocked in prototype — needs real login).
- **Daily backup** — losing data once destroys trust.
- Web + mobile.
- Confirmations on irreversible money actions (close, write-off, delete payment, pay salary).

---

## 5. Backend / architecture (NOT in the prototype — the real engineering)
These cannot be faked in a front-end mockup and are the bulk of the build:
1. **Server + database + API.** Everything is currently in-memory and resets on refresh.
2. **Authentication & worker identity.** Real login so a worker sees only their tasks; role permissions enforced server-side.
3. **Time progression.** currentDay, daysLeft, and batch stage must compute from sowDate vs the real current date, and advance automatically each day (stage: germinating → growing → hardening → ready). Overdue tasks and ready batches surface on their own.
4. **Farmer intake + WhatsApp/SMS.** A real channel (WhatsApp Business API via a BSP like AiSensy/Wati, or a farmer form) — outbound status messages and, later, inbound booking. Utility-template messages in Telugu.
5. **Persistence, sync, conflict resolution** for offline-first.

---

## 6. Open decisions (owner to settle)
- **Farmer intake channel:** manual entry only for v1, or WhatsApp integration from the start?
- **Seed inventory depth:** only needed for nursery-purchased seed — light "bought − used" ledger, or skip in v1?
- **Tray reuse realism:** freed trays need cleaning/sterilising before reuse — model "available after cleaning," or treat as instantly free (v1)?
- **GST:** nursery seedlings are often GST-exempt but depends on turnover — confirm with a CA; keep GST optional in the app.
- **Backward date mode:** add a "want-by → sow-by" toggle for advance bookings, or forward-only?
- **Money-edit audit trail:** currently simple overwrite; add "edited on [date]" trace for payments/expenses later?

---

## 7. Known gaps still open in the prototype (for awareness)
- No phone-number validation.
- No search/filter on lists (fine now; needed past ~50 records).
- `user-scalable=no` disables pinch-zoom (accessibility — reconsider).
- Salary pay-cycle is basic (payslip history now recorded, but no formal period boundaries).
- Closed orders can't be reopened (by design — would need an explicit "cancel & reopen" action).

---

## 8. Recommended build order
1. Backend foundation: DB, API, auth, offline sync.
2. Orders + seeding + batches + the three counts (the core that replaces the notebook).
3. Money: ledger, dues, close/discount/write-off.
4. Tasks + calendar + worker completion writing to timelines.
5. Labour: attendance, advances, salary, payslips.
6. Expenses + dashboard + capacity.
7. Time progression + auto stage/reminders.
8. Farmer WhatsApp status messages.
9. Later: farmer self-service app, seed inventory, reports, search.

**Before writing backend code:** validate with 5–10 real nursery owners using the prototype. Their reactions decide priority order and confirm the open decisions in §6.
