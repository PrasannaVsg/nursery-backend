# Nursery Management App — Backend (Spring Boot)

> Rename this file to `CLAUDE.md` in the backend repo root. Claude Code reads it automatically each session.

## What this is
Offline-first app to fully digitize a vegetable seedling nursery (chilli, tomato, brinjal, capsicum, cabbage) in Andhra Pradesh/Telangana that does custom/contract raising. This repo is the **backend only**. The mobile app is a separate Flutter repo that talks to this API.

Source-of-truth docs (in `docs/`):
- `docs/Nursery-App-Build-Spec.md` — full requirements & business rules
- `docs/Nursery-App-Architecture.md` — system architecture & sync design
- `docs/nursery-prototype.html` — UX reference (open in a browser)

Read all three before writing code.

## Stack
- Java 21, Spring Boot 3.x
- Spring Web (REST), Spring Data JPA + Hibernate
- PostgreSQL
- Maven
- Docker + Docker Compose (local: app + Postgres)
- Add when reached: Spring Security + JWT (auth), Flyway (migrations), Bean Validation, Lombok

## Non-negotiable rules (from the architecture)
- **Every table** has: `id` UUID (client-generated, accept from client — do NOT auto-generate on insert), `updatedAt` timestamp, `deleted` boolean (soft delete — never hard-delete syncable rows).
- **Store inputs, never computed values.** No columns for balance, daysLeft, currentDay, stage, free trays. Compute them.
- **Order and Batch are separate entities.** One Order can have many Batches (split seeding). Money (contract, payments, discount, write-off, closed) lives on the **Order**, not the Batch. Batch holds production only (counts, trays, timeline).
- **Payments are append-only** — never overwrite a payment; add a new one or a reversal.
- **Business rules live in the service layer** and are enforced server-side regardless of client:
  - Block over-capacity booking (reserved + occupied must not exceed tray total).
  - Dispatch quantity ≤ remaining (never negative).
  - Net pay never negative.
  - Once a batch exists under an order, lock crop/variety/price/qty (only contact editable).
- Contract total = qty × rate, rate is per-plant OR per-tray (field on order).
- Dates forward model: seeded date entered → ready date = seeded + crop lead time.

## Conventions
- Package root `com.nursery`
- Layers: `controller/` → `service/` → `repository/` → `entity/`; also `dto/`, `security/`, `config/`
- DB URL inside Docker uses the compose service name, not localhost: `jdbc:postgresql://db:5432/nursery`
- Schema changes via Flyway migrations (once introduced).

## Build order — DO ONE STEP AT A TIME, confirm it runs before the next
1. **Skeleton + DB + health endpoint** — Spring Boot app, Postgres via Docker Compose, one `/health` endpoint returning OK. Prove the plumbing.
2. **Entities/tables** — Order, Batch, Payment, Task, TaskLog, Worker, Attendance, Advance, Payslip, Expense, Request, Settings. Each with id/updatedAt/deleted.
3. **Core loop API** — CRUD for orders → seeding (creates batch, partial allowed) → batches → the three counts (ordered/sown/alive) + dispatch + tray reserve/occupy/release.
4. **Money** (order ledger, dues, close/discount/write-off) → **Tasks** (types incl. seeding, repeat rules, isDue, multi-batch, logs) → **Labour** (attendance, advances, salary, payslips) → **Expenses/dashboard**.
5. **Auth + roles** — phone OTP, JWT, owner vs worker (server-enforced).
6. **Sync endpoints** — `POST /sync/push` (apply client changes) and `GET /sync/changes?since=...` (return this user's changed rows). Build LAST, once the API shape is stable.

Do not jump ahead. After each step: `docker compose up`, test in Postman, `git commit`.

## Current step
> Update this line as you go, e.g. "Working on: Build step 2 — entities."
Build step 2 DONE (all entities + Flyway V1 schema; 16 tables created, Hibernate ddl-auto=validate passes, verified end-to-end). Next: step 3 — core loop API (orders → seeding → batches → three counts + dispatch + tray reserve).
