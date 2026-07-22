import SwiftUI

/// 설정 — 내 이름, 가까운 친구 기준, 매일 안부 알림.
struct SettingsView: View {
    @EnvironmentObject var model: AppModel

    var body: some View {
        Form {
            Section("내 이름") {
                TextField("예: 김순자", text: $model.senderName)
                    .font(.system(size: 20))
                Text("안부 문자 끝에 서명으로 들어가요.")
                    .font(.system(size: 15)).foregroundStyle(.secondary)
            }

            Section("가까운 친구 기준") {
                Stepper(value: $model.closeThreshold, in: 1...30) {
                    Text("한 달 \(model.closeThreshold)번 이상")
                        .font(.system(size: 19))
                }
                Text("이 앱으로 이 횟수 이상 연락한 사람을 가까운 친구로 봐요.")
                    .font(.system(size: 15)).foregroundStyle(.secondary)
            }

            Section("매일 안부 알림") {
                Toggle(isOn: Binding(
                    get: { model.autoReminder },
                    set: { model.setAutoReminder($0) }
                )) {
                    Text("매일 알림 받기").font(.system(size: 19))
                }
                if model.autoReminder {
                    Stepper(value: $model.checkInHour, in: 0...23) {
                        Text("\(model.checkInHour)시에 알림")
                            .font(.system(size: 19))
                    }
                }
                Text("정한 시각에 '안부 문자를 보내요' 알림을 드려요. (iOS는 문자 자동발송이 안 되어, 알림을 눌러 직접 보내시면 됩니다.)")
                    .font(.system(size: 15)).foregroundStyle(.secondary)
            }

            Section("받는 사람 (자녀·가족)") {
                if model.contacts.isEmpty {
                    Text("연락처가 없어요.").foregroundStyle(.secondary)
                }
                ForEach(model.contacts.sorted(by: { model.starredIDs.contains($0.id) && !model.starredIDs.contains($1.id) }).prefix(40)) { c in
                    Button {
                        model.toggleRecipient(c.id)
                    } label: {
                        HStack {
                            Text(c.name).font(.system(size: 19)).foregroundStyle(.primary)
                            Spacer()
                            if model.recipientIDs.contains(c.id) {
                                Image(systemName: "checkmark.circle.fill")
                                    .foregroundStyle(AnnyeongColor.green)
                            } else {
                                Image(systemName: "circle").foregroundStyle(.secondary)
                            }
                        }
                    }
                }
            }

            Section {
                Text("🔒 모든 정보(연락처·설정·연락기록)는 이 아이폰 안에서만 처리되고, 외부로 전송되지 않습니다.")
                    .font(.system(size: 15)).foregroundStyle(AnnyeongColor.green)
            }
        }
        .navigationTitle("설정")
        .navigationBarTitleDisplayMode(.inline)
    }
}
