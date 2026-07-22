import SwiftUI

/// 가까운 친구 — 앱으로 자주 연락한 사람 + 즐겨찾기. (iOS는 시스템 통화기록을 못 읽어
/// '이 앱으로 연락한 횟수'로 집계한다.)
struct CloseFriendsView: View {
    @EnvironmentObject var model: AppModel

    private var friends: [ContactFrequency] { model.closeFriends() }

    var body: some View {
        List {
            Section {
                Text("이 앱으로 최근 30일간 \(model.closeThreshold)번 이상 연락했거나, 즐겨찾기한 분들이에요.")
                    .font(.system(size: 16)).foregroundStyle(.secondary)
                if !friends.isEmpty {
                    Button {
                        model.starAllCloseFriends()
                    } label: {
                        Label("모두 즐겨찾기에 추가", systemImage: "star.fill")
                            .font(.system(size: 18, weight: .semibold))
                    }
                }
            }

            if friends.isEmpty {
                Text("아직 자주 연락한 기록이 없어요.\n전화나 문자를 보내면 여기에 모여요.")
                    .font(.system(size: 19))
                    .foregroundStyle(.secondary)
            }

            ForEach(friends) { f in
                Button {
                    model.recordCall(f.contact.id)
                    CommsService.call(f.contact.phone)
                } label: {
                    HStack(spacing: 14) {
                        InitialAvatar(initial: f.contact.initial)
                        VStack(alignment: .leading, spacing: 2) {
                            Text(f.contact.name).font(.system(size: 22, weight: .bold))
                            Text("전화 \(f.callCount)회 · 문자 \(f.messageCount)회")
                                .font(.system(size: 16)).foregroundStyle(.secondary)
                        }
                        Spacer()
                        Image(systemName: "phone.fill")
                            .font(.system(size: 24))
                            .foregroundStyle(AnnyeongColor.call)
                    }
                    .padding(.vertical, 6)
                }
                .buttonStyle(.plain)
            }
        }
        .listStyle(.insetGrouped)
        .navigationTitle("가까운 친구")
        .navigationBarTitleDisplayMode(.inline)
    }
}
