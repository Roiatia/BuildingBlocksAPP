-- BrickVault initial schema
-- Run in Supabase SQL Editor or via supabase db push

-- ── lego_sets ────────────────────────────────────────────────────────────────
create table if not exists public.lego_sets (
    id                   uuid primary key default gen_random_uuid(),
    user_id              uuid not null references auth.users(id) on delete cascade,
    lego_set_number      text,
    name                 text not null,
    theme                text,
    year                 int,
    piece_count          int,
    image_url            text,
    status               text not null default 'UNBUILT',
    notes                text,
    priority             text not null default 'MEDIUM',
    difficulty           text,
    storage_location_id  uuid,
    purchased_at         date,
    added_at             timestamptz not null default now(),
    updated_at           timestamptz not null default now(),
    created_at           timestamptz not null default now(),
    deleted_at           timestamptz
);

alter table public.lego_sets enable row level security;

create policy "users can select own sets"
    on public.lego_sets for select using (auth.uid() = user_id);
create policy "users can insert own sets"
    on public.lego_sets for insert with check (auth.uid() = user_id);
create policy "users can update own sets"
    on public.lego_sets for update using (auth.uid() = user_id);
create policy "users can delete own sets"
    on public.lego_sets for delete using (auth.uid() = user_id);

-- ── storage_locations ─────────────────────────────────────────────────────────
create table if not exists public.storage_locations (
    id                 uuid primary key default gen_random_uuid(),
    user_id            uuid not null references auth.users(id) on delete cascade,
    name               text not null,
    type               text not null default 'SHELF',
    parent_location_id uuid references public.storage_locations(id) on delete set null,
    notes              text,
    updated_at         timestamptz not null default now(),
    created_at         timestamptz not null default now(),
    deleted_at         timestamptz
);

alter table public.storage_locations enable row level security;

create policy "users can select own locations"
    on public.storage_locations for select using (auth.uid() = user_id);
create policy "users can insert own locations"
    on public.storage_locations for insert with check (auth.uid() = user_id);
create policy "users can update own locations"
    on public.storage_locations for update using (auth.uid() = user_id);
create policy "users can delete own locations"
    on public.storage_locations for delete using (auth.uid() = user_id);

-- ── missing_parts ─────────────────────────────────────────────────────────────
create table if not exists public.missing_parts (
    id          uuid primary key default gen_random_uuid(),
    user_id     uuid not null references auth.users(id) on delete cascade,
    set_id      uuid not null references public.lego_sets(id) on delete cascade,
    part_number text,
    color       text,
    quantity    int not null default 1,
    status      text not null default 'NEEDED',
    notes       text,
    updated_at  timestamptz not null default now(),
    created_at  timestamptz not null default now(),
    deleted_at  timestamptz
);

alter table public.missing_parts enable row level security;

create policy "users can select own missing parts"
    on public.missing_parts for select using (auth.uid() = user_id);
create policy "users can insert own missing parts"
    on public.missing_parts for insert with check (auth.uid() = user_id);
create policy "users can update own missing parts"
    on public.missing_parts for update using (auth.uid() = user_id);
create policy "users can delete own missing parts"
    on public.missing_parts for delete using (auth.uid() = user_id);

-- ── build_logs ────────────────────────────────────────────────────────────────
create table if not exists public.build_logs (
    id                  uuid primary key default gen_random_uuid(),
    user_id             uuid not null references auth.users(id) on delete cascade,
    set_id              uuid not null references public.lego_sets(id) on delete cascade,
    started_at          timestamptz,
    completed_at        timestamptz,
    build_time_minutes  int,
    notes               text,
    updated_at          timestamptz not null default now(),
    created_at          timestamptz not null default now()
);

alter table public.build_logs enable row level security;

create policy "users can select own build logs"
    on public.build_logs for select using (auth.uid() = user_id);
create policy "users can insert own build logs"
    on public.build_logs for insert with check (auth.uid() = user_id);
create policy "users can update own build logs"
    on public.build_logs for update using (auth.uid() = user_id);
create policy "users can delete own build logs"
    on public.build_logs for delete using (auth.uid() = user_id);

-- ── indexes ───────────────────────────────────────────────────────────────────
create index if not exists lego_sets_user_id_idx      on public.lego_sets(user_id);
create index if not exists lego_sets_updated_at_idx   on public.lego_sets(user_id, updated_at);
create index if not exists lego_sets_deleted_at_idx   on public.lego_sets(user_id, deleted_at);
create index if not exists lego_sets_status_idx       on public.lego_sets(user_id, status);
create index if not exists storage_loc_user_id_idx    on public.storage_locations(user_id);
create index if not exists storage_loc_updated_at_idx on public.storage_locations(user_id, updated_at);
create index if not exists missing_parts_set_id_idx   on public.missing_parts(set_id);
create index if not exists missing_parts_user_id_idx  on public.missing_parts(user_id);
create index if not exists build_logs_set_id_idx      on public.build_logs(set_id);
create index if not exists build_logs_user_id_idx     on public.build_logs(user_id);
