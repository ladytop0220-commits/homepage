# Émelle — 공식 웹사이트

> **Émelle** by **Gipeel International (주식회사 기펠인터내셔날)**
> 프리미엄 주방·라이프스타일 브랜드의 공식 이커머스 웹사이트.

본 저장소는 Émelle 브랜드의 공식 웹사이트(emelle.co.kr 예정) Next.js 코드와 기획 문서를 함께 보관합니다.

> ⚠️ **Next.js 16 + React 19 + Tailwind v4** — 일부 API가 이전 버전과 다릅니다. 코드 작성 전 AGENTS.md 참고.

---

## 🚀 빠른 시작

```bash
# 의존성 설치
npm install

# 환경 변수 템플릿 복사 후 값 채우기
cp .env.local.example .env.local

# 개발 서버 (http://localhost:3000)
npm run dev

# 프로덕션 빌드
npm run build
npm start
```

> Node 20+ / npm 10+ 권장. (개발 환경: Node 25.9.0, npm 11.12.1)

---

## 🗂 폴더 구조

```
emelle/
├── docs/                              # 📋 기획 문서 (Git 추적)
│   ├── mvp-wireframes.md              # 사이트맵·플로우·화면 설계
│   ├── ai-image-prompts.md            # AI 이미지 생성 프롬프트
│   ├── return-exchange-policy.md      # 반품·교환 정책 (사이트에서 렌더됨)
│   ├── privacy-policy.md              # 개인정보 처리방침
│   └── terms-of-service.md            # 이용약관
│
├── public/
│   └── products/                      # 제품 이미지 (SVG placeholder)
│
├── src/
│   ├── app/
│   │   ├── layout.tsx                 # 루트 레이아웃 (html/body)
│   │   ├── globals.css                # Tailwind v4 + 브랜드 토큰
│   │   ├── (customer)/                # 고객 페이지 라우트 그룹
│   │   │   ├── layout.tsx             # 헤더/푸터 포함
│   │   │   ├── page.tsx               # 홈 /
│   │   │   ├── products/              # 제품 목록·상세
│   │   │   ├── cart/                  # 장바구니
│   │   │   ├── checkout/              # 결제
│   │   │   ├── account/               # 로그인·회원가입·마이페이지
│   │   │   ├── about/                 # 브랜드 스토리
│   │   │   ├── policy/[type]/         # 정책 페이지 (docs/*.md 렌더)
│   │   │   └── support/               # 문의
│   │   ├── admin/                     # 어드민 라우트 (사이드바 레이아웃)
│   │   │   ├── layout.tsx
│   │   │   ├── dashboard/
│   │   │   ├── orders/
│   │   │   ├── kakao-intake/          # ⭐ 카톡 Q&A 주문 파싱
│   │   │   ├── products/
│   │   │   ├── inventory/
│   │   │   ├── customers/
│   │   │   └── staff/
│   │   └── api/                       # API 라우트 (서버)
│   │
│   ├── components/
│   │   ├── layout/                    # GlobalHeader, GlobalFooter, AdminSidebar
│   │   ├── ui/                        # 공통 UI (ComingSoon 등)
│   │   ├── product/                   # ProductCard 등
│   │   ├── cart/                      # 장바구니 컴포넌트
│   │   ├── checkout/                  # 결제 컴포넌트
│   │   └── admin/                     # 어드민 전용 컴포넌트
│   │
│   ├── data/
│   │   └── products.ts                # 정적 제품 데이터 (Phase 2에 DB 이전)
│   │
│   ├── lib/
│   │   ├── env.ts                     # 환경 변수 헬퍼
│   │   ├── supabase.ts                # Supabase 클라이언트 (Phase 2)
│   │   └── policy.ts                  # docs/*.md 로드 헬퍼
│   │
│   └── types/                         # 공유 타입 정의
│
├── .env.local.example                 # 환경 변수 템플릿
├── package.json
├── tsconfig.json
├── next.config.ts
└── postcss.config.mjs
```

---

## 🎨 브랜드 디자인 토큰

`src/app/globals.css`의 `@theme`에서 정의 (Tailwind v4 CSS-first 방식).

| 토큰 | HEX | 용도 |
|---|---|---|
| `creme` | `#F4EDE0` | 마스터 베이스 / 배경 |
| `mousse` | `#4A5D3F` | Cuisine 라인 |
| `ardoise` | `#2B2926` | Maison / 다크 / 본문 텍스트 |
| `laiton` | `#A88B5C` | 액센트 |
| `argile` | `#B5694A` | 한정판·시즌 (일반 사용 금지) |

폰트:
- **Display (Serif)**: GT Sectra → Noto Serif KR (대체)
- **Body (Sans)**: Pretendard → Inter

Tailwind 사용 예: `bg-creme text-ardoise`, `font-display`, `label-caps`, `.brand-divider`.

---

## 📋 MVP Phase 1 상태

상세 범위·결정 사항은 [docs/mvp-wireframes.md](docs/mvp-wireframes.md) 참고.

✅ **완료**
- Next.js 16 + Tailwind v4 + TypeScript 스캐폴딩
- Émelle 브랜드 토큰·폰트·레이아웃
- 사이트맵 기반 폴더 구조 + 헤더/푸터/사이드바
- 홈·About·제품 목록·제품 상세 페이지
- 정책 3종 페이지 (docs/*.md 렌더링)
- 어드민 레이아웃 + 로그인·대시보드 placeholder
- 제품 이미지 SVG placeholder (Eisenach 박스 미노출)

⏳ **다음 (Phase 2 — DB & 결제)**
- Supabase 프로젝트 생성 + 스키마 마이그레이션
- 회원가입·로그인 (Supabase Auth)
- 장바구니 + 토스페이먼츠 결제 연동
- 어드민 주문·재고 실제 기능
- 카톡 Q&A 인테이크 (Anthropic API)
- 이메일 발송 (Resend)

---

## 🔗 외부 연동 체크리스트

| 서비스 | 상태 | 환경 변수 |
|---|---|---|
| Vercel 호스팅 | 결정 | — |
| Supabase | 미결정 | `NEXT_PUBLIC_SUPABASE_URL`, `_ANON_KEY` |
| Anthropic API (카톡 파싱) | 미결정 | `ANTHROPIC_API_KEY` |
| 토스페이먼츠 | 미결정 | `NEXT_PUBLIC_TOSS_CLIENT_KEY`, `TOSS_SECRET_KEY` |
| Resend (이메일) | 미결정 | `RESEND_API_KEY` |
| 국세청 사업자번호 진위확인 | 미결정 | `NTSBIZ_API_KEY` |

---

## 🏢 회사 정보

- **상호**: 주식회사 기펠인터내셔날 (Gipeel International Co., Ltd.)
- **대표**: 김미정
- **사업자등록번호**: 560-88-00664
- **본점**: 서울특별시 마포구 마포대로 92, A동 3층 (도화동, 효성해링턴 스퀘어)
- **카카오톡 채널**: 생활의 여유
