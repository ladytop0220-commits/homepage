@AGENTS.md

# Émelle — 프로젝트 가이드 (CLAUDE.md)

**Gipeel International(주식회사 기펠인터내셔날)**의 프리미엄 주방·라이프스타일 브랜드
**Émelle**의 공식 이커머스 웹사이트(emelle.co.kr 예정).

> ⚠️ **최우선 규칙:** 이 저장소는 **Next.js 16 + React 19 + Tailwind v4**를 사용합니다.
> 일부 API·규칙이 이전 버전과 다릅니다. 코드 작성 전 `node_modules/next/dist/docs/`의
> 관련 가이드를 먼저 확인하고, deprecation 경고를 준수하세요. (AGENTS.md 참고)

---

## 명령어

```bash
npm install          # 의존성 설치 (최초 1회)
npm run dev          # 개발 서버 — http://localhost:3000 (Turbopack)
npm run build        # 프로덕션 빌드
npm start            # 프로덕션 서버
npm run lint         # ESLint

cp .env.local.example .env.local   # 환경 변수 템플릿 복사 후 값 채우기
```

권장 환경: Node 20+ / npm 10+.

---

## 아키텍처

Next.js **App Router** 기반. 라우트 그룹으로 고객/어드민 영역을 분리합니다.

```
src/
├── app/
│   ├── layout.tsx            # 루트 레이아웃 (html/body)
│   ├── globals.css           # Tailwind v4 @theme — 브랜드 토큰·폰트 정의
│   ├── (customer)/           # 고객 라우트 그룹 (헤더/푸터 레이아웃)
│   │   ├── page.tsx          #   홈 /
│   │   ├── products/         #   제품 목록 · [slug] 상세
│   │   ├── cart/ checkout/   #   장바구니 · 결제
│   │   ├── account/          #   로그인 · 회원가입
│   │   ├── about/ support/   #   브랜드 스토리 · 문의
│   │   └── policy/[type]/    #   정책 페이지 (docs/*.md 렌더)
│   └── admin/                # 어드민 라우트 그룹 (사이드바 레이아웃)
│       ├── dashboard/ orders/ products/ inventory/ customers/ staff/
│       └── kakao-intake/     #   ⭐ 카톡 Q&A 주문 파싱 (Anthropic API)
├── components/               # layout / ui / product 등 도메인별 분류
├── data/products.ts          # 정적 제품 데이터 (Phase 2에 DB로 이전 예정)
└── lib/
    ├── env.ts                # 환경 변수 접근 헬퍼 + isFeatureEnabled
    ├── supabase.ts           # Supabase 클라이언트 (Phase 2)
    └── policy.ts             # docs/*.md 로드 헬퍼
```

- **정책 문서**: `docs/*.md`(약관·개인정보·반품교환)를 `policy/[type]` 라우트에서
  `react-markdown` + `remark-gfm`으로 렌더합니다. 정책 수정은 코드가 아니라 `docs/`의
  마크다운을 고칩니다.
- **DB/마이그레이션**: `supabase/migrations/`에 SQL 스키마. Phase 2에서 활성화.

---

## 환경 변수 & 기능 플래그

`src/lib/env.ts`가 모든 환경 변수 접근을 감쌉니다. 통합(Supabase·Anthropic·토스·이메일 등)은
**환경 변수가 있어야만 활성화**되며, `isFeatureEnabled.*()`로 각 기능 활성 여부를 확인합니다.
키가 없으면 UI에서 "기능 비활성 — 환경변수 누락"을 안내하는 방식이므로, 새 통합을 추가할 때도
이 패턴(옵셔널 접근 + 기능 플래그)을 따르세요. 전체 목록은 `.env.local.example` 참고.

| 통합 | 용도 |
|---|---|
| Supabase | Auth · DB (Phase 2) |
| Anthropic API | 카톡 Q&A 인테이크 파싱 |
| 토스페이먼츠 | 결제 (Phase 2) |
| Resend | 트랜잭션 이메일 |
| 국세청 사업자번호 진위확인 | 사업자 검증 (Phase 2) |

---

## 브랜드 디자인 토큰

`src/app/globals.css`의 `@theme`에서 정의 (Tailwind v4 CSS-first 방식). 하드코딩된 색상 대신
아래 토큰을 사용하세요.

| 토큰 | HEX | 용도 |
|---|---|---|
| `creme` | `#F4EDE0` | 마스터 베이스 / 배경 |
| `mousse` | `#4A5D3F` | Cuisine 라인 |
| `ardoise` | `#2B2926` | Maison / 다크 / 본문 텍스트 |
| `laiton` | `#A88B5C` | 액센트 |
| `argile` | `#B5694A` | 한정판·시즌 (**일반 사용 금지**) |

- **폰트**: Display(Serif) GT Sectra → Noto Serif KR / Body(Sans) Pretendard → Inter
- 사용 예: `bg-creme text-ardoise`, `font-display`, `label-caps`, `.brand-divider`

---

## 프로젝트 상태

- **Phase 1 (완료)**: 스캐폴딩, 브랜드 토큰·레이아웃, 홈·About·제품·정책 페이지,
  어드민 레이아웃 + placeholder. 제품 데이터는 `src/data/products.ts` 정적 데이터.
- **Phase 2 (예정)**: Supabase DB·Auth, 장바구니·토스 결제, 어드민 실기능,
  카톡 Q&A 인테이크, Resend 이메일.

기획·화면 설계 상세는 `docs/mvp-wireframes.md` 참고.

---

## 규칙 & 관례

- **언어**: 코드 주석·문서·UI 카피는 한국어가 기본입니다.
- **정적 데이터 → DB**: Phase 2 전까지 제품/콘텐츠는 정적 파일로 관리. 새 데이터도 우선
  `src/data/`에 두고, DB 이전을 염두에 둔 형태로 작성하세요.
- **비활성 기능 표시**: 아직 연동되지 않은 기능은 에러 대신 `ComingSoon` 등으로 안내.
