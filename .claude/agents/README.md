# 역할별 개발 에이전트 (Claude Code 서브에이전트)

소프트웨어 개발 흐름을 5개의 전문 에이전트로 나눠, 각 단계가 **자기 역할에 맞는 권한과 모델**로만
동작하도록 구성했습니다. 메인(오케스트레이터)이 `Agent` 도구로 순서대로 호출하고,
앞 단계의 산출물을 다음 단계에 넘깁니다.

## 파이프라인

```
requirements → analyst → architect → implementer → verifier
 (요구사항)     (분석)     (설계)       (구현)        (검증)
```

| 에이전트 | 역할 | 권한(도구) | 모델 | 산출물 |
|---|---|---|---|---|
| **requirements** | 요구사항 수렴 | Read, Grep, Glob, Write, AskUserQuestion | sonnet | `docs/requirements.md` |
| **analyst** | 분석(제약·엣지케이스·타당성) | Read, Grep, Glob, WebSearch, WebFetch (읽기전용) | opus | 분석 리포트 |
| **architect** | 설계(구조·인터페이스·계획) | Read, Grep, Glob, Write | opus | `docs/design.md` |
| **implementer** | 구현(코드 작성·빌드·테스트) | Read, Grep, Glob, Edit, Write, Bash | opus | 코드 변경 |
| **verifier** | 검증(빌드·테스트·리뷰·보안) | Read, Grep, Glob, Bash (수정 불가) | sonnet | 검증 리포트 |

## 설계 원칙
- **권한 분리:** analyst·architect·verifier는 실제 코드를 수정하지 못합니다. 코드를 바꾸는 건
  implementer 뿐이라, 각 단계의 책임이 섞이지 않습니다.
- **모델 배분:** 고난이도 추론이 필요한 분석·설계·구현은 opus, 상대적으로 정형화된
  요구사항 수렴·검증은 sonnet으로 두어 속도와 품질/비용을 균형 잡았습니다.
- **단계별 산출물:** 각 에이전트는 다음 단계가 바로 쓸 수 있는 명확한 산출물을 남깁니다.

## 사용 예
```
# 메인 대화에서
"이 기능을 requirements 에이전트로 요구사항부터 정리해줘"
→ 이후 analyst → architect → implementer → verifier 순으로 이어서 호출
```
각 에이전트는 이름으로 호출하거나, 설명(description)에 맞는 상황에서 자동으로 선택됩니다.
