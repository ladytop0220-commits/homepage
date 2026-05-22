-- =========================================================================
-- Émelle — Initial Schema (MVP Phase 1 → 2)
-- Project: bvxvtrtzphazjxbjgcpp.supabase.co
-- Apply via: Supabase Studio → SQL Editor → paste & run
-- Or via Supabase CLI: supabase db push
-- =========================================================================

-- Extensions
create extension if not exists "uuid-ossp";
create extension if not exists "pgcrypto";

-- =========================================================================
-- 1. Reference: enums
-- =========================================================================

do $$ begin
  create type order_status as enum (
    'new',           -- 신규
    'awaiting_payment',  -- 입금 대기 (무통장·후불)
    'paid',          -- 결제 완료
    'preparing',     -- 출고 준비
    'shipped',       -- 배송 중
    'delivered',     -- 배송 완료
    'cancelled',     -- 취소
    'refunded'       -- 환불 완료
  );
exception when duplicate_object then null; end $$;

do $$ begin
  create type order_source as enum ('web', 'kakao_channel', 'phone', 'manual');
exception when duplicate_object then null; end $$;

do $$ begin
  create type payment_method as enum ('card', 'bank_transfer', 'deposit', 'credit', 'unknown');
exception when duplicate_object then null; end $$;

do $$ begin
  create type customer_type as enum ('b2c', 'b2b');
exception when duplicate_object then null; end $$;

do $$ begin
  create type intake_status as enum ('parsed', 'review', 'confirmed', 'rejected');
exception when duplicate_object then null; end $$;

do $$ begin
  create type inventory_reason as enum ('inbound', 'outbound', 'adjustment', 'return', 'damage');
exception when duplicate_object then null; end $$;

do $$ begin
  create type staff_role as enum ('owner', 'order', 'inventory', 'marketing');
exception when duplicate_object then null; end $$;

-- =========================================================================
-- 2. Products & inventory
-- =========================================================================

create table if not exists products (
  id uuid primary key default uuid_generate_v4(),
  sku text not null unique,
  slug text not null unique,
  line text not null,                 -- 'cuisine' | 'maison'
  category text not null,             -- 'frying-pan' | 'pot' | ...
  name_ko text not null,
  name_en text,
  short_description_ko text,
  short_description_en text,
  description_md_ko text,
  description_md_en text,
  price_retail integer not null default 0,    -- 소매가 (원)
  price_wholesale integer not null default 0, -- 도매가 (Phase 2)
  active boolean not null default true,
  specs jsonb not null default '[]'::jsonb,   -- [{label, value}]
  images text[] not null default '{}',        -- 경로 배열
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

create index if not exists products_active_idx on products(active);
create index if not exists products_line_idx on products(line);

create table if not exists product_options (
  id uuid primary key default uuid_generate_v4(),
  product_id uuid not null references products(id) on delete cascade,
  option_key text not null,           -- '28cm', '24cm' 등
  label text not null,
  price_adjustment integer not null default 0,
  stock integer not null default 0,
  safety_stock integer not null default 0,
  position integer not null default 0,
  active boolean not null default true,
  created_at timestamptz not null default now(),
  unique(product_id, option_key)
);

create index if not exists product_options_product_idx on product_options(product_id);

-- 입출고 이력 (Phase 2의 안전재고 알림에 사용)
create table if not exists inventory_transactions (
  id uuid primary key default uuid_generate_v4(),
  product_option_id uuid not null references product_options(id) on delete restrict,
  reason inventory_reason not null,
  delta integer not null,             -- +N(입고) / -N(출고)
  balance_after integer not null,     -- 트랜잭션 후 재고
  note text,
  related_order_id uuid,              -- references orders(id) — 출고/취소 시 연결
  created_by uuid,                    -- staff id
  created_at timestamptz not null default now()
);

create index if not exists inv_tx_option_idx on inventory_transactions(product_option_id, created_at desc);

-- =========================================================================
-- 3. Customers & addresses
-- =========================================================================

create table if not exists customers (
  id uuid primary key default uuid_generate_v4(),
  type customer_type not null default 'b2c',
  email text unique,
  phone text,
  name text,
  -- B2B 전용
  branch_name text,                   -- 카톡 1번 "지점명"
  business_number text,               -- 사업자등록번호
  business_name text,
  representative_name text,
  business_address text,
  tax_invoice_email text,
  -- 인증·메타
  auth_user_id uuid unique,           -- supabase auth user id
  marketing_opt_in boolean not null default false,
  notification_channels jsonb not null default '["email"]'::jsonb,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

create index if not exists customers_phone_idx on customers(phone);
create index if not exists customers_branch_idx on customers(branch_name);

create table if not exists addresses (
  id uuid primary key default uuid_generate_v4(),
  customer_id uuid references customers(id) on delete cascade,
  label text,                         -- '본점', '배송지' 등
  recipient_name text not null,
  recipient_phone text not null,
  postal_code text,
  address1 text not null,
  address2 text,
  is_default boolean not null default false,
  created_at timestamptz not null default now()
);

-- =========================================================================
-- 4. Orders
-- =========================================================================

create table if not exists orders (
  id uuid primary key default uuid_generate_v4(),
  order_number text not null unique,  -- 예: 2026-W0001 / 2026-K0001
  source order_source not null default 'web',
  status order_status not null default 'new',

  -- 고객 정보 (회원·비회원 공통 — 스냅샷)
  customer_id uuid references customers(id) on delete set null,
  customer_type customer_type not null default 'b2c',
  branch_name text,                   -- 카톡 1번
  contact_name text not null,
  contact_phone text not null,
  contact_email text,

  -- 배송지 스냅샷
  shipping_recipient text,
  shipping_phone text,
  shipping_postal text,
  shipping_address1 text,
  shipping_address2 text,
  shipping_memo text,

  -- 결제
  payment_method payment_method not null default 'unknown',
  payment_id text,                    -- PG 결제 ID
  subtotal_amount integer not null default 0,
  shipping_fee integer not null default 0,
  total_amount integer not null default 0,

  -- 배송 정보
  courier text,
  tracking_number text,
  shipped_at timestamptz,
  delivered_at timestamptz,

  -- 세금계산서/거래명세서
  invoice_requested boolean not null default false,
  invoice_type text,                  -- 'tax_invoice' | 'transaction_statement'
  invoice_email text,
  invoice_issued boolean not null default false,
  invoice_pdf_url text,

  -- 메모·이력
  staff_note text,
  customer_memo text,                 -- 카톡 7번 등

  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

create index if not exists orders_status_idx on orders(status, created_at desc);
create index if not exists orders_source_idx on orders(source);
create index if not exists orders_customer_idx on orders(customer_id);

create table if not exists order_items (
  id uuid primary key default uuid_generate_v4(),
  order_id uuid not null references orders(id) on delete cascade,
  product_id uuid not null references products(id),
  product_option_id uuid references product_options(id),
  sku text not null,                  -- 스냅샷
  name text not null,                 -- 스냅샷
  option_label text,                  -- 스냅샷
  quantity integer not null,
  unit_price integer not null,        -- 스냅샷
  subtotal integer not null,
  created_at timestamptz not null default now()
);

create index if not exists order_items_order_idx on order_items(order_id);

-- 주문 상태 변경 이력
create table if not exists order_events (
  id uuid primary key default uuid_generate_v4(),
  order_id uuid not null references orders(id) on delete cascade,
  event_type text not null,           -- 'status_change' | 'note' | 'shipped' ...
  payload jsonb not null default '{}'::jsonb,
  actor uuid,                         -- staff id
  created_at timestamptz not null default now()
);

create index if not exists order_events_order_idx on order_events(order_id, created_at desc);

-- =========================================================================
-- 5. Kakao channel intake (정형 Q&A 파싱 결과)
-- =========================================================================

create table if not exists kakao_order_intake (
  id uuid primary key default uuid_generate_v4(),
  raw_text text not null,             -- 카드번호는 파싱 단계에서 마스킹 후 저장
  parsed jsonb not null,              -- ParsedKakaoOrder JSON (마스킹 처리됨)
  status intake_status not null default 'parsed',
  warnings jsonb not null default '[]'::jsonb,
  order_id uuid references orders(id) on delete set null,
  staff_id uuid,                      -- 처리한 운영자
  confirmed_at timestamptz,
  created_at timestamptz not null default now()
);

create index if not exists kakao_intake_status_idx on kakao_order_intake(status, created_at desc);

comment on column kakao_order_intake.raw_text is
  '⚠️ 카드번호는 저장 전 src/lib/kakao-parser.ts에서 마스킹됨. 절대 원본 카드번호 저장 금지.';

-- =========================================================================
-- 6. Staff (어드민 사용자 — 별도 테이블; supabase auth와 연결)
-- =========================================================================

create table if not exists staff (
  id uuid primary key default uuid_generate_v4(),
  auth_user_id uuid not null unique,
  email text not null unique,
  name text not null,
  role staff_role not null default 'order',
  active boolean not null default true,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

-- =========================================================================
-- 7. Updated-at triggers
-- =========================================================================

create or replace function set_updated_at()
returns trigger language plpgsql as $$
begin
  new.updated_at = now();
  return new;
end $$;

do $$ begin
  create trigger trg_products_updated before update on products
    for each row execute function set_updated_at();
exception when duplicate_object then null; end $$;

do $$ begin
  create trigger trg_customers_updated before update on customers
    for each row execute function set_updated_at();
exception when duplicate_object then null; end $$;

do $$ begin
  create trigger trg_orders_updated before update on orders
    for each row execute function set_updated_at();
exception when duplicate_object then null; end $$;

do $$ begin
  create trigger trg_staff_updated before update on staff
    for each row execute function set_updated_at();
exception when duplicate_object then null; end $$;

-- =========================================================================
-- 8. Row-Level Security (RLS)
-- =========================================================================

alter table products enable row level security;
alter table product_options enable row level security;
alter table customers enable row level security;
alter table addresses enable row level security;
alter table orders enable row level security;
alter table order_items enable row level security;
alter table order_events enable row level security;
alter table inventory_transactions enable row level security;
alter table kakao_order_intake enable row level security;
alter table staff enable row level security;

-- 누구나 활성 제품·옵션을 조회 가능 (상점 페이지)
create policy "anyone_read_active_products"
  on products for select
  using (active = true);

create policy "anyone_read_active_options"
  on product_options for select
  using (active = true);

-- 본인 customer row 조회/수정
create policy "self_read_customer"
  on customers for select
  using (auth.uid() = auth_user_id);

create policy "self_update_customer"
  on customers for update
  using (auth.uid() = auth_user_id);

-- 본인 주소
create policy "self_manage_addresses"
  on addresses for all
  using (
    exists (
      select 1 from customers c
      where c.id = addresses.customer_id and c.auth_user_id = auth.uid()
    )
  );

-- 본인 주문 조회
create policy "self_read_orders"
  on orders for select
  using (
    exists (
      select 1 from customers c
      where c.id = orders.customer_id and c.auth_user_id = auth.uid()
    )
  );

create policy "self_read_order_items"
  on order_items for select
  using (
    exists (
      select 1 from orders o
      join customers c on c.id = o.customer_id
      where o.id = order_items.order_id and c.auth_user_id = auth.uid()
    )
  );

-- Staff (어드민 권한) — staff 테이블에 활성 row가 있는 사용자
create policy "staff_full_access_products"
  on products for all
  using (
    exists (select 1 from staff where auth_user_id = auth.uid() and active)
  );

create policy "staff_full_access_options"
  on product_options for all
  using (
    exists (select 1 from staff where auth_user_id = auth.uid() and active)
  );

create policy "staff_full_access_orders"
  on orders for all
  using (
    exists (select 1 from staff where auth_user_id = auth.uid() and active)
  );

create policy "staff_full_access_order_items"
  on order_items for all
  using (
    exists (select 1 from staff where auth_user_id = auth.uid() and active)
  );

create policy "staff_full_access_order_events"
  on order_events for all
  using (
    exists (select 1 from staff where auth_user_id = auth.uid() and active)
  );

create policy "staff_full_access_customers"
  on customers for all
  using (
    exists (select 1 from staff where auth_user_id = auth.uid() and active)
  );

create policy "staff_full_access_inventory"
  on inventory_transactions for all
  using (
    exists (select 1 from staff where auth_user_id = auth.uid() and active)
  );

create policy "staff_full_access_intake"
  on kakao_order_intake for all
  using (
    exists (select 1 from staff where auth_user_id = auth.uid() and active)
  );

create policy "staff_self_read"
  on staff for select
  using (auth.uid() = auth_user_id);

create policy "owner_manage_staff"
  on staff for all
  using (
    exists (select 1 from staff where auth_user_id = auth.uid() and role = 'owner' and active)
  );

-- =========================================================================
-- 9. Seed data — 첫 제품
-- =========================================================================

insert into products (sku, slug, line, category, name_ko, name_en, short_description_ko, short_description_en, description_md_ko, price_retail, price_wholesale, specs, images, active)
values (
  'EM-CUI-PAN-28',
  'ceramic-pan-28',
  'cuisine',
  'frying-pan',
  '에멜 세라믹 팬 28cm',
  'Émelle Ceramic Pan 28cm',
  '두 번 구워낸 표면. 매일의 요리를 위한 28cm IH 세라믹 팬.',
  'Twice-fired surface. A 28cm IH-ready ceramic pan for everyday cooking.',
  '세라믹 코팅 위에서 매일이 익는다. 알루미늄 본체와 베이클라이트 핸들. 인덕션을 포함한 모든 화구에서 안정적으로 사용 가능합니다.',
  0, 0,
  '[{"label":"사이즈","value":"28cm"},{"label":"코팅","value":"세라믹 (Ceramic)"},{"label":"본체","value":"알루미늄"},{"label":"핸들","value":"베이클라이트"},{"label":"인덕션 호환","value":"예 (IH)"}]'::jsonb,
  array['/products/ceramic-pan-28-eisenach.png'],
  true
)
on conflict (sku) do nothing;

insert into product_options (product_id, option_key, label, stock, safety_stock, position)
select id, '28cm', '28cm', 0, 10, 0 from products where sku = 'EM-CUI-PAN-28'
on conflict (product_id, option_key) do nothing;
