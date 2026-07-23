# 안녕 (Annyeong) — 아이폰(iOS) 전화·안부 보조 앱

노인을 위한 **네이티브 iOS 앱**(SwiftUI). 큰 버튼과 간단한 흐름으로
전화 걸기 · 안부 문자 만들기 · 가까운 친구 정리를 돕습니다.

> **이름의 뜻** — "안녕"은 인사(hello)이자 안부(잘 지내지?)를 동시에 담은 말입니다.

---

## 주요 기능 (iOS에서 가능한 범위)

| 기능 | 설명 |
|---|---|
| 📞 **전화 걸기** | 폰 연락처를 큰 버튼 목록으로 보여주고, 누르면 전화(확인 후 발신) |
| 💬 **안부 문자 만들기** | 오늘의 기분·식사·산책·복약 등 상태를 고르면 자연스러운 안부 문장을 **자동 생성** → 문자앱에 채워서 열어줌(사용자가 '보내기' 탭) |
| 👥 **가까운 친구** | **이 앱으로** 연락한 횟수를 최근 30일 집계 + 즐겨찾기로 모아보기 |
| ⏰ **매일 안부 알림** | 정한 시각에 "안부 문자를 보내요" 알림(리마인더) |
| 🔒 **개인정보 보호** | 연락처·설정·연락기록 모두 **기기 안에서만** 처리, 외부 전송 없음 |

## ⚠️ iOS 정책상 불가능한 것 (안드로이드와 다름)

애플은 아래를 **어떤 앱에도** 허용하지 않습니다. 그래서 위와 같이 대체했습니다.

- ❌ 문자 **자동** 발송 → 대신 내용 자동생성 + 사용자가 '보내기' 탭
- ❌ 시스템 **통화·문자 기록** 읽기 → 대신 '이 앱으로 연락한 횟수'로 집계
- ❌ 정해진 시각 **자동** 문자 → 대신 **알림**으로 안내

## 기술 스택

- Swift, SwiftUI (iOS 16.0+)
- Contacts / MessageUI / UserNotifications 프레임워크
- 외부 네트워크·서버 의존성 **없음**

## 빌드 / 실행 방법 (Mac 필요)

이 클라우드에는 macOS·Xcode가 없어 빌드되지 않습니다. **Mac에서** 여세요.

### 방법 A — 프로젝트 파일 더블클릭 (가장 쉬움)
저장소에 완성된 `Annyeong.xcodeproj` 가 들어 있습니다. 별도 도구가 필요 없어요.
```bash
cd ios
open Annyeong.xcodeproj      # 또는 Finder 에서 더블클릭
```
그다음 Xcode에서:
1. 상단에서 본인 아이폰(USB 연결) 또는 시뮬레이터 선택
2. 왼쪽 **Annyeong** 프로젝트 클릭 > **Signing & Capabilities > Team** 에 본인 Apple ID 선택 (무료 계정도 가능)
3. ▶︎ (Run) 실행 — 실제 아이폰에 설치됩니다. (무료 계정은 7일마다 재설치 필요)
4. 아이폰에서 **설정 > 일반 > VPN 및 기기 관리** 에서 본인 개발자 앱을 '신뢰'로 설정하면 실행됩니다.

### 방법 B — XcodeGen 으로 재생성 (선택)
프로젝트를 새로 생성하고 싶으면:
```bash
cd ios
xcodegen generate           # project.yml 로부터 Annyeong.xcodeproj 재생성
open Annyeong.xcodeproj
```

### 방법 C — Xcode에서 직접
1. Xcode > New Project > iOS App (SwiftUI, 이름 Annyeong, Bundle ID `kr.annyeong.app`)
2. `ios/Annyeong/` 안의 `.swift` 파일과 `Assets.xcassets` 를 프로젝트로 드래그해 추가
3. Target Info에 **Privacy - Contacts Usage Description** 항목 추가 (문구는 project.yml 참고)
4. ▶︎ 실행

## 폴더 구조
```
ios/
├─ project.yml                 # XcodeGen 설정
└─ Annyeong/
   ├─ AnnyeongApp.swift        # 앱 진입점
   ├─ AppModel.swift           # 전역 상태 + 로컬 저장
   ├─ Models.swift             # Contact 등 데이터·색상
   ├─ MessageTemplates.swift   # 안부 문자 자동 생성
   ├─ ContactsService.swift    # 연락처 읽기
   ├─ CommsService.swift       # 전화 + 문자 작성창
   ├─ NotificationsService.swift # 매일 안부 알림
   ├─ Views/                   # 홈·전화·안부·친구·설정 화면
   └─ Assets.xcassets          # 앱 아이콘·강조색
```

## 참고
- 실제 문자·전화는 **시뮬레이터 말고 실제 아이폰**에서 테스트하세요.
- 앱스토어 배포는 유료 Apple Developer 계정($99/년)과 TestFlight/심사가 필요합니다. 원하면 그 준비도 도와드릴 수 있어요.
