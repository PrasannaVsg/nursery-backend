-- V1: initial schema (Build step 2 — entities/tables)
--
-- Every syncable table carries: id (UUID, client-generated), updated_at, deleted (soft delete).
-- Computed values are NOT stored anywhere: contract total, balance, seededQty, ready_date,
-- current_day, stage, free trays. They are derived from these inputs.
-- Money lives on orders, never on batches. Payments are append-only.

create table orders (
    id              uuid primary key,
    updated_at      timestamp(6) not null,
    deleted         boolean not null default false,
    farmer          varchar(255) not null,
    phone           varchar(255),
    crop            varchar(255) not null,
    variety         varchar(255),
    qty             integer not null,
    seeded_date     date,
    seed_source     varchar(255) not null,
    price_mode      varchar(255) not null,
    rate            numeric(12,2) not null,
    discount        numeric(12,2),
    writeoff_amount numeric(12,2),
    writeoff_note   varchar(255),
    closed          boolean not null default false
);

create table batches (
    id          uuid primary key,
    updated_at  timestamp(6) not null,
    deleted     boolean not null default false,
    order_id    uuid not null,
    crop        varchar(255) not null,
    variety     varchar(255),
    seed_source varchar(255) not null,
    ordered     integer not null,
    sown        integer not null,
    alive       integer not null,
    trays_num   integer not null,
    sow_date    date not null,
    closed      boolean not null default false
);

create table payments (
    id           uuid primary key,
    updated_at   timestamp(6) not null,
    deleted      boolean not null default false,
    order_id     uuid not null,
    payment_date date not null,
    amount       numeric(12,2) not null,
    kind         varchar(255) not null
);

create table timeline_events (
    id           uuid primary key,
    updated_at   timestamp(6) not null,
    deleted      boolean not null default false,
    batch_id     uuid not null,
    day_number   integer not null,
    event_date   date not null,
    type         varchar(255) not null,
    event_text   varchar(255),
    by_worker_id uuid,
    qty          integer
);

create table tasks (
    id            uuid primary key,
    updated_at    timestamp(6) not null,
    deleted       boolean not null default false,
    title         varchar(255) not null,
    type          varchar(255) not null,
    nursery_scope boolean not null default false,
    order_id      uuid,
    worker_id     uuid,
    repeat_mode   varchar(255) not null,
    every_n       integer,
    start_date    date not null,
    stopped       boolean not null default false
);

create table task_batch_scopes (
    task_id  uuid not null,
    batch_id uuid not null
);

create table task_logs (
    id         uuid primary key,
    updated_at timestamp(6) not null,
    deleted    boolean not null default false,
    task_id    uuid not null,
    log_date   date not null,
    status     varchar(255) not null,
    note       varchar(255)
);

create table workers (
    id         uuid primary key,
    updated_at timestamp(6) not null,
    deleted    boolean not null default false,
    name       varchar(255) not null,
    phone      varchar(255),
    pay_type   varchar(255) not null,
    rate       numeric(12,2) not null,
    join_date  date
);

create table attendance (
    id              uuid primary key,
    updated_at      timestamp(6) not null,
    deleted         boolean not null default false,
    worker_id       uuid not null,
    attendance_date date not null,
    status          varchar(255) not null
);

create table advances (
    id           uuid primary key,
    updated_at   timestamp(6) not null,
    deleted      boolean not null default false,
    worker_id    uuid not null,
    advance_date date not null,
    amount       numeric(12,2) not null
);

create table payslips (
    id           uuid primary key,
    updated_at   timestamp(6) not null,
    deleted      boolean not null default false,
    worker_id    uuid not null,
    payslip_date date not null,
    net          numeric(12,2) not null,
    days         integer not null,
    adv_deducted numeric(12,2) not null
);

create table expenses (
    id           uuid primary key,
    updated_at   timestamp(6) not null,
    deleted      boolean not null default false,
    category     varchar(255) not null,
    amount       numeric(12,2) not null,
    expense_date date not null,
    note         varchar(255),
    batch_id     uuid,
    order_id     uuid
);

create table requests (
    id         uuid primary key,
    updated_at timestamp(6) not null,
    deleted    boolean not null default false,
    farmer     varchar(255) not null,
    phone      varchar(255),
    crop       varchar(255) not null,
    variety    varchar(255),
    qty        integer not null,
    note       varchar(255),
    status     varchar(255) not null
);

create table settings (
    id         uuid primary key,
    updated_at timestamp(6) not null,
    deleted    boolean not null default false,
    tray_total integer not null,
    language   varchar(255)
);

create table settings_crop_lead_times (
    settings_id uuid not null,
    crop        varchar(255) not null,
    days        integer not null
);

-- Indexes for the foreign-key lookups the API and sync will lean on.
create index idx_batches_order_id on batches (order_id);
create index idx_payments_order_id on payments (order_id);
create index idx_timeline_events_batch_id on timeline_events (batch_id);
create index idx_tasks_worker_id on tasks (worker_id);
create index idx_task_batch_scopes_task_id on task_batch_scopes (task_id);
create index idx_task_logs_task_id on task_logs (task_id);
create index idx_attendance_worker_id on attendance (worker_id);
create index idx_advances_worker_id on advances (worker_id);
create index idx_payslips_worker_id on payslips (worker_id);
create index idx_settings_crop_lead_times_settings_id on settings_crop_lead_times (settings_id);
