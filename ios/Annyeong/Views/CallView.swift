import SwiftUI

/// 전화 걸기 — 즐겨찾기 우선, 이름 검색, 행을 누르면 전화.
struct CallView: View {
    @EnvironmentObject var model: AppModel
    @State private var query = ""

    private var filtered: [Contact] {
        model.contacts
            .filter { query.isEmpty || $0.name.localizedCaseInsensitiveContains(query) }
            .sorted { a, b in
                let sa = model.starredIDs.contains(a.id)
                let sb = model.starredIDs.contains(b.id)
                if sa != sb { return sa }
                return a.name.localizedCompare(b.name) == .orderedAscending
            }
    }

    var body: some View {
        List {
            if !model.contactsAuthorized {
                Text("연락처 접근을 허용해 주세요. (설정 > 안녕 > 연락처)")
                    .font(.system(size: 18))
            }
            ForEach(filtered) { c in
                Button {
                    model.recordCall(c.id)
                    CommsService.call(c.phone)
                } label: {
                    HStack(spacing: 14) {
                        InitialAvatar(initial: c.initial)
                        VStack(alignment: .leading, spacing: 2) {
                            HStack(spacing: 6) {
                                Text(c.name).font(.system(size: 22, weight: .bold))
                                if model.starredIDs.contains(c.id) {
                                    Image(systemName: "star.fill")
                                        .foregroundStyle(AnnyeongColor.green)
                                        .font(.system(size: 15))
                                }
                            }
                            Text(c.phone).font(.system(size: 17)).foregroundStyle(.secondary)
                        }
                        Spacer()
                        Image(systemName: "phone.fill")
                            .font(.system(size: 26))
                            .foregroundStyle(AnnyeongColor.call)
                    }
                    .padding(.vertical, 6)
                }
                .buttonStyle(.plain)
            }
        }
        .listStyle(.plain)
        .searchable(text: $query, prompt: "이름 검색")
        .navigationTitle("전화 걸기")
        .navigationBarTitleDisplayMode(.inline)
    }
}
