# 안녕 (Annyeong) — 어르신 전화·안부 보조 앱

노인을 위한 **네이티브 안드로이드 앱**입니다. 큰 버튼과 간단한 흐름으로,
전화 걸기 · 자주 연락하는 친구 자동 정리 · 가족/친구에게 안부 문자 보내기를 도와줍니다.

> **이름의 뜻** — "안녕"은 인사(hello)이자 안부(잘 지내지?)를 동시에 담은 말입니다.

---

## 주요 기능

| 기능 | 설명 | 사용 권한 |
|---|---|---|
| 📞 **말로 전화 걸기** | "○○에게 전화"라고 말하면 이름을 인식해 바로 전화 | 음성인식, `CALL_PHONE` |
| 📞 **전화 걸기** | 폰 연락처를 큰 버튼 목록으로 보여주고 한 번에 전화 | `READ_CONTACTS`, `CALL_PHONE` |
| 👥 **가까운 친구 자동 선별** | 최근 한 달간 전화·문자를 자주 주고받은 사람을 자동으로 찾아, 폰 연락처 즐겨찾기(별표)에 한 번에 등록 | `READ_CALL_LOG`, `READ_SMS`, `WRITE_CONTACTS` |
| 💬 **안부 문자 자동 생성·발송** | 오늘의 기분·식사·산책·복약 등 상태를 고르면 자연스러운 안부 문장을 만들어 가족/친구에게 전송 | `SEND_SMS` |
| ⏰ **매일 자동 안부 문자** | 정한 시각에 "잘 지내요" 안부 문자를 등록한 가족에게 자동 발송 (WorkManager) | `SEND_SMS` |

## 개인정보 보호

- 연락처 · 통화기록 · 문자기록 · 설정 등 **모든 데이터는 이 기기 안에서만** 처리됩니다.
- **외부 서버로 전송하거나 저장하지 않습니다.** (네트워크 권한을 사용하지 않음)
- 안부 문장 생성도 전적으로 기기 내부(오프라인)에서 이뤄집니다.
- 설정 값은 앱 전용 저장소(DataStore)에만 저장됩니다.

## 기술 스택

- Kotlin, Jetpack Compose (Material 3)
- minSdk 26 (Android 8.0) / targetSdk 35
- DataStore(설정), WorkManager(자동 문자 예약)
- 외부 네트워크·서버 의존성 **없음**

## 빌드 / 실행 방법

이 클라우드 저장소에서는 안드로이드 SDK가 없어 빌드되지 않습니다.
아래처럼 **로컬 PC에서 Android Studio**로 여세요.

1. 이 저장소를 내려받습니다.
2. **Android Studio** (Ladybug 이상 권장)에서 `android/` 폴더를 **엽니다**.
3. Android Studio가 Gradle Wrapper와 SDK를 자동으로 맞춰줍니다.
   - 터미널을 쓰려면 프로젝트 루트에서 `gradle wrapper` 후 `./gradlew assembleDebug`
4. 실제 기기(권장) 또는 에뮬레이터에서 실행합니다.
   - 문자·통화 기능은 **실제 기기**에서 테스트하세요.
5. 첫 실행 시 권한을 허용하면 기능이 활성화됩니다.

```
android/
├─ settings.gradle.kts
├─ build.gradle.kts
├─ gradle/libs.versions.toml         # 버전 카탈로그
└─ app/
   ├─ build.gradle.kts
   └─ src/main/
      ├─ AndroidManifest.xml
      ├─ java/kr/annyeong/app/
      │  ├─ MainActivity.kt          # 화면 전환·권한·발신 처리
      │  ├─ ui/AppViewModel.kt       # 상태/동작
      │  ├─ ui/screens/              # 홈·전화·안부·친구·설정 화면
      │  ├─ ui/theme/                # 큰 글씨 테마
      │  ├─ data/                    # 연락처·통화기록·문자 생성·발송·설정
      │  └─ work/CheckInWorker.kt    # 매일 자동 안부 문자
      └─ res/                        # 아이콘·문자열·테마
```

## 참고 (제약)

- **iPhone(iOS)** 에서는 OS 정책상 문자 자동 발송·통화기록 분석이 불가능합니다. 이 앱은 안드로이드 전용입니다.
- 음성으로 전화 걸기는 기기에 음성 인식(구글 앱 등)이 설치되어 있어야 동작합니다.
- 자동 문자 발송(`SEND_SMS`)은 통신사 요금제의 문자 요금이 부과될 수 있습니다.
