# 배포 가이드 — GitHub + Vercel

> **목표**: `main` 브랜치에 push하면 Vercel이 자동으로 프로덕션 빌드·배포.
> 한 번 셋업해두면 이후 모든 변경은 자동 반영됩니다.

---

## 1단계 — GitHub 인증 + 첫 푸시

로컬 커밋(`5fd7c2d ... Émelle MVP scaffolding`)은 이미 완료. 다음 한 번만 사용자 터미널에서 실행하면 됩니다.

### 옵션 A — GitHub CLI (권장, 가장 간단)

이미 `gh` 설치 완료. 본인 macOS 터미널에서:

```bash
gh auth login
# 1) GitHub.com 선택
# 2) HTTPS 선택
# 3) "Login with a web browser" 선택
# 4) 표시되는 one-time code 메모
# 5) Enter → 브라우저에서 코드 입력 → 인증 완료

# 인증 성공 후 푸시
cd /Users/kimmijung/emelle
git push -u origin main
```

### 옵션 B — Personal Access Token (PAT)

`gh` 안 쓰고 싶으면:
1. https://github.com/settings/tokens → **Generate new token (classic)**
2. 범위: `repo` 체크 → 생성
3. 토큰 복사
4. 터미널:
   ```bash
   cd /Users/kimmijung/emelle
   git push -u origin main
   # Username: ladytop0220-commits
   # Password: <복사한 토큰 붙여넣기>
   ```
5. macOS Keychain이 자동으로 저장 → 다음부터는 안 물어봄

### 옵션 C — SSH 키 (장기적으로 가장 깔끔)

1. `ssh-keygen -t ed25519 -C "kimmijung@emelle"` (엔터 3번)
2. `cat ~/.ssh/id_ed25519.pub` 출력 복사
3. https://github.com/settings/keys → **New SSH key** → 붙여넣기
4. 리모트를 SSH로 변경:
   ```bash
   git remote set-url origin git@github.com:ladytop0220-commits/homepage.git
   git push -u origin main
   ```

---

## 2단계 — Vercel 프로젝트 생성 + 자동 배포

### 권장 — GitHub 연동 (한 번만 셋업)

1. https://vercel.com/ladytop0220-6921s-projects 접속
2. **Add New... → Project** 클릭
3. **Import Git Repository** 섹션에서 `ladytop0220-commits/homepage` 선택
   - 처음이면 "Install Vercel for GitHub" 권한 부여 필요
4. 프로젝트 이름: `emelle` (또는 자유)
5. **Framework Preset**: Next.js (자동 감지됨)
6. **Root Directory**: `./` (기본)
7. **Environment Variables** — 추가 (없어도 빌드는 됨, Phase 2에서 필수):
   ```
   NEXT_PUBLIC_SUPABASE_URL=https://bvxvtrtzphazjxbjgcpp.supabase.co
   NEXT_PUBLIC_SUPABASE_ANON_KEY=<Supabase Studio에서 복사>
   SUPABASE_SERVICE_ROLE_KEY=<Supabase Studio에서 복사>
   ANTHROPIC_API_KEY=<https://console.anthropic.com 에서 발급>
   ```
8. **Deploy** 클릭
9. 1~2분 후 `https://emelle-<random>.vercel.app` URL 생성됨
10. 이후 `main` 브랜치에 push하면 자동 재배포

### 도메인 연결 (도메인 확보 후)

Vercel 프로젝트 → **Settings → Domains** → `emelle.co.kr` 추가
- Vercel이 안내하는 A/CNAME 레코드를 도메인 등록사(가비아·후이즈 등) DNS에 입력
- HTTPS 인증서는 자동 발급

### CLI로 배포 (선택, 빠른 일회성 배포에 유용)

```bash
npm i -g vercel
cd /Users/kimmijung/emelle
vercel login            # 브라우저 인증
vercel link             # 위에서 만든 emelle 프로젝트와 연결
vercel --prod           # 프로덕션 배포 (즉시)
```

---

## 3단계 — 배포 후 확인 체크리스트

- [ ] `https://<배포 URL>/` 홈 로딩
- [ ] `/products` 페이지 표시
- [ ] `/products/ceramic-pan-28` 제품 상세 표시 (이미지 포함)
- [ ] `/about` 브랜드 페이지
- [ ] `/policy/terms`, `/privacy`, `/return` 정책 페이지 (docs/*.md 렌더링)
- [ ] `/admin` → `/admin/dashboard` 리다이렉트
- [ ] `/admin/kakao-intake` 페이지 로딩 (붙여넣기·파싱 동작)
- [ ] 푸터 사업자 정보 표시

---

## 4단계 — 자주 만나는 이슈

### "Build failed: Module not found"
- 로컬에서 `npm run build` 먼저 시도 → 에러 수정 → push

### Pretendard/Noto Serif 폰트 안 나옴
- jsDelivr CDN에서 로드. Vercel 빌드 시 외부 폰트라 약간의 깜빡임 가능. 추후 `next/font/local`로 셀프 호스팅 권장.

### docs/*.md 정책 페이지 깨짐
- `src/lib/policy.ts`가 `fs`로 `docs/`를 읽음. Vercel은 이 디렉토리도 같이 배포됨(.gitignore에 없으므로 OK).

### Supabase env 변수 없이도 빌드되나?
- 빌드는 OK (현재 lib/supabase.ts는 lazy throw). 다만 Phase 2 기능 사용 시 런타임 에러.
