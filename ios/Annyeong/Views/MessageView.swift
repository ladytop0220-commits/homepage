import SwiftUI
import MessageUI

/// 안부 보내기 — ① 받는 사람 고르기 → ② 프리셋/상태로 문자 만들기 → ③ 보내기.
struct MessageView: View {
    @EnvironmentObject var model: AppModel

    @State private var selected: Set<String> = []
    @State private var message = ""
    @State private var mood: MessageTemplates.Mood = .good
    @State private var activities: Set<MessageTemplates.Activity> = []
    @State private var note = ""
    @State private var showCompose = false
    @State private var showNoSMSAlert = false

    private let chipColumns = [GridItem(.adaptive(minimum: 100), spacing: 8)]

    private var candidates: [Contact] {
        model.contacts.sorted { a, b in
            let sa = model.starredIDs.contains(a.id)
            let sb = model.starredIDs.contains(b.id)
            if sa != sb { return sa }
            return a.name.localizedCompare(b.name) == .orderedAscending
        }
    }

    private var selectedContacts: [Contact] { candidates.filter { selected.contains($0.id) } }

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 18) {

                // ① 받는 사람
                sectionTitle("받는 사람")
                if candidates.isEmpty { Text("연락처가 없어요.").foregroundStyle(.secondary) }
                LazyVGrid(columns: chipColumns, alignment: .leading, spacing: 8) {
                    ForEach(candidates.prefix(40)) { c in
                        Chip(text: c.name, isOn: selected.contains(c.id)) {
                            toggle(&selected, c.id)
                        }
                    }
                }
                Text(selected.isEmpty ? "보낼 사람을 골라주세요" : "\(selected.count)명 선택됨")
                    .font(.system(size: 16)).foregroundStyle(AnnyeongColor.green)

                Divider()

                // ② 빠른 안부(프리셋)
                sectionTitle("빠른 안부")
                LazyVGrid(columns: chipColumns, alignment: .leading, spacing: 8) {
                    ForEach(MessageTemplates.presets(senderName: model.senderName), id: \.title) { preset in
                        Chip(text: preset.title, isOn: false) { message = preset.body }
                    }
                }

                Divider()

                // ② 오늘 상태로 만들기
                sectionTitle("오늘 나의 상태")
                Text("기분").font(.system(size: 17))
                LazyVGrid(columns: chipColumns, alignment: .leading, spacing: 8) {
                    ForEach(MessageTemplates.Mood.allCases) { m in
                        Chip(text: m.label, isOn: mood == m) { mood = m }
                    }
                }
                Text("오늘 한 일").font(.system(size: 17))
                LazyVGrid(columns: chipColumns, alignment: .leading, spacing: 8) {
                    ForEach(MessageTemplates.Activity.allCases) { a in
                        Chip(text: a.label, isOn: activities.contains(a)) { toggle(&activities, a) }
                    }
                }
                TextField("한마디 덧붙이기 (선택)", text: $note, axis: .vertical)
                    .font(.system(size: 18))
                    .textFieldStyle(.roundedBorder)
                Button("이 내용으로 문자 만들기") {
                    message = MessageTemplates.buildCheckIn(
                        mood: mood, activities: Array(activities),
                        senderName: model.senderName, note: note)
                }
                .font(.system(size: 18, weight: .semibold))

                Divider()

                // ③ 미리보기 & 보내기
                sectionTitle("보낼 내용")
                Text(message.isEmpty ? "위에서 안부 내용을 골라주세요." : message)
                    .font(.system(size: 20))
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .padding(14)
                    .background(Color(.secondarySystemBackground))
                    .clipShape(RoundedRectangle(cornerRadius: 14))

                Button {
                    if CommsService.canSendText { showCompose = true } else { showNoSMSAlert = true }
                } label: {
                    Label("문자 보내기", systemImage: "paperplane.fill")
                        .font(.system(size: 22, weight: .bold))
                        .frame(maxWidth: .infinity, minHeight: 60)
                        .foregroundStyle(.white)
                        .background(canSend ? AnnyeongColor.message : Color.gray)
                        .clipShape(RoundedRectangle(cornerRadius: 18))
                }
                .disabled(!canSend)
            }
            .padding(20)
        }
        .navigationTitle("안부 보내기")
        .navigationBarTitleDisplayMode(.inline)
        .sheet(isPresented: $showCompose) {
            MessageComposeView(
                recipients: selectedContacts.map { $0.phone },
                body: message
            ) { result in
                if result == .sent { model.recordMessage(selectedContacts.map { $0.id }) }
                showCompose = false
            }
            .ignoresSafeArea()
        }
        .alert("이 기기에서는 문자를 보낼 수 없어요", isPresented: $showNoSMSAlert) {
            Button("확인", role: .cancel) {}
        } message: {
            Text("문자(iMessage/SMS)를 사용할 수 있는 기기에서 다시 시도해 주세요.")
        }
    }

    private var canSend: Bool { !selected.isEmpty && !message.isEmpty }

    private func sectionTitle(_ t: String) -> some View {
        Text(t).font(.system(size: 22, weight: .bold))
    }

    private func toggle<T: Hashable>(_ set: inout Set<T>, _ value: T) {
        if set.contains(value) { set.remove(value) } else { set.insert(value) }
    }
}

/// 토글되는 캡슐 칩.
struct Chip: View {
    let text: String
    let isOn: Bool
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            Text(text)
                .font(.system(size: 17))
                .lineLimit(1)
                .padding(.horizontal, 14).padding(.vertical, 9)
                .frame(maxWidth: .infinity)
                .background(isOn ? AnnyeongColor.green : Color(.secondarySystemBackground))
                .foregroundStyle(isOn ? .white : .primary)
                .clipShape(Capsule())
        }
        .buttonStyle(.plain)
    }
}
