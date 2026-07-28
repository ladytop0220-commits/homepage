import SwiftUI

/// 홈 화면 — 큰 버튼으로 각 기능으로 이동.
struct HomeView: View {
    @EnvironmentObject var model: AppModel

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(spacing: 16) {
                    VStack(alignment: .leading, spacing: 4) {
                        Text(model.senderName.isEmpty ? "안녕하세요" : "\(model.senderName) 님, 안녕하세요")
                            .font(.system(size: 30, weight: .bold))
                        Text("무엇을 도와드릴까요?")
                            .font(.system(size: 20))
                            .foregroundStyle(.secondary)
                    }
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .padding(.bottom, 4)

                    NavigationLink { CallView() } label: {
                        BigButton(title: "전화 걸기", subtitle: "친구 목록에서 골라 전화",
                                  systemImage: "phone.fill", color: AnnyeongColor.call) {}
                            .allowsHitTesting(false)
                    }
                    NavigationLink { MessageView() } label: {
                        BigButton(title: "안부 보내기", subtitle: "가족·친구에게 안부 문자",
                                  systemImage: "message.fill", color: AnnyeongColor.message) {}
                            .allowsHitTesting(false)
                    }
                    NavigationLink { CloseFriendsView() } label: {
                        BigButton(title: "가까운 친구", subtitle: "자주 연락하는 사람 모아보기",
                                  systemImage: "person.2.fill", color: AnnyeongColor.green) {}
                            .allowsHitTesting(false)
                    }
                    NavigationLink { SettingsView() } label: {
                        BigButton(title: "설정", subtitle: "이름·안부 알림 설정",
                                  systemImage: "gearshape.fill", color: Color(.darkGray)) {}
                            .allowsHitTesting(false)
                    }
                }
                .padding(20)
            }
            .navigationTitle("안녕")
            .background(AnnyeongColor.cream.ignoresSafeArea())
            .task {
                await model.refresh()
                await model.configureRemindersOnLaunch()
            }
        }
    }
}
