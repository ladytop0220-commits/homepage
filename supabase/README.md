# Supabase 셋업 가이드

**프로젝트**: `bvxvtrtzphazjxbjgcpp` (https://supabase.com/dashboard/project/bvxvtrtzphazjxbjgcpp)

---

## 1. 환경 변수 설정 (로컬)

Supabase Studio → **Project Settings → API** 에서 키 두 개를 복사해 `.env.local`에 입력:

```bash
NEXT_PUBLIC_SUPABASE_URL=https://bvxvtrtzphazjxbjgcpp.supabase.co
NEXT_PUBLIC_SUPABASE_ANON_KEY=eyJhb...  (Project API keys → anon public)
SUPABASE_SERVICE_ROLE_KEY=eyJhb...      (Project API keys → service_role, 절대 클라이언트 노출 금지)
```

## 2. 스키마 적용

### 옵션 A — Supabase Studio (가장 간단)
1. https://supabase.com/dashboard/project/bvxvtrtzphazjxbjgcpp/sql/new
2. `migrations/0001_initial_schema.sql` 내용 전체 복사·붙여넣기
3. **Run** 클릭
4. Tables 탭에서 `products`, `orders`, `kakao_order_intake` 등이 생성됐는지 확인

### 옵션 B — Supabase CLI
```bash
# 1) CLI 설치 (한 번만)
brew install supabase/tap/supabase

# 2) 프로젝트 로그인 + 연결
supabase login
supabase link --project-ref bvxvtrtzphazjxbjgcpp

# 3) 마이그레이션 적용
supabase db push
```

## 3. Auth 설정

Studio → **Authentication → Providers** 에서:
- **Email**: 활성화 (회원가입·로그인용)
- **Email 확인**: ON (이메일 검증 필수)

추후 (Phase 2):
- Kakao OAuth (소셜 로그인) 검토

## 4. Storage 버킷 (Phase 2)

Studio → **Storage** → New bucket
- `product-images` (public read) — 제품 이미지
- `invoices` (private) — 세금계산서·거래명세서 PDF

## 5. 시드 데이터

`0001_initial_schema.sql`의 마지막 섹션에 첫 제품(EM-CUI-PAN-28)이 시드됩니다.
- 가격은 0원으로 시작 (Studio에서 수정 또는 어드민 페이지에서 수정 — Phase 2)
- 재고도 0개 (입고 처리 후 +N)

## 6. 첫 어드민 staff 등록

회원가입 후 (이메일로) → Studio → **Authentication → Users** 에서 자신의 user UUID 복사 → SQL Editor에서:

```sql
insert into staff (auth_user_id, email, name, role, active)
values (
  '여기에-방금-복사한-uuid',
  'admin@emelle.co.kr',
  '김미정',
  'owner',
  true
);
```

이후 `/admin/dashboard` 접근 가능 (Phase 2 인증 연동 후).

## 7. 백업

Supabase 무료 플랜은 일 단위 자동 백업 7일치 보관.
중요 시점에는 Studio → **Database → Backups → Trigger backup** 으로 수동 백업.
