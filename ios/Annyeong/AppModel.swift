import SwiftUI

/// 앱 전역 상태 + 로컬 저장(UserDefaults). 모든 데이터는 이 기기에만 저장된다.
@MainActor
final class AppModel: ObservableObject {

    // 화면 상태
    @Published var contacts: [Contact] = []
    @Published var contactsAuthorized = false
    @Published var loading = false

    // 설정 (저장됨)
    @Published var senderName: String { didSet { defaults.set(senderName, forKey: K.senderName) } }
    @Published var closeThreshold: Int { didSet { defaults.set(closeThreshold, forKey: K.threshold) } }
    @Published var checkInHour: Int { didSet { defaults.set(checkInHour, forKey: K.hour); rescheduleIfNeeded() } }
    @Published var autoReminder: Bool { didSet { defaults.set(autoReminder, forKey: K.reminder); rescheduleIfNeeded() } }

    // 즐겨찾기(가까운 친구 수동 지정) + 자동 안부 문자 받는 사람
    @Published var starredIDs: Set<String> { didSet { save(ids: starredIDs, key: K.starred) } }
    @Published var recipientIDs: Set<String> { didSet { save(ids: recipientIDs, key: K.recipients) } }

    // 앱으로 연락한 기록 (id -> 날짜들)
    @Published private var callEvents: [String: [Date]] { didSet { saveEvents(callEvents, key: K.callEvents) } }
    @Published private var msgEvents: [String: [Date]] { didSet { saveEvents(msgEvents, key: K.msgEvents) } }

    private let defaults = UserDefaults.standard
    private enum K {
        static let senderName = "senderName"
        static let threshold = "closeThreshold"
        static let hour = "checkInHour"
        static let reminder = "autoReminder"
        static let starred = "starredIDs"
        static let recipients = "recipientIDs"
        static let callEvents = "callEvents"
        static let msgEvents = "msgEvents"
    }

    init() {
        senderName = defaults.string(forKey: K.senderName) ?? ""
        closeThreshold = defaults.object(forKey: K.threshold) as? Int ?? 1
        checkInHour = defaults.object(forKey: K.hour) as? Int ?? 10
        autoReminder = defaults.bool(forKey: K.reminder)
        starredIDs = Set(defaults.stringArray(forKey: K.starred) ?? [])
        recipientIDs = Set(defaults.stringArray(forKey: K.recipients) ?? [])
        callEvents = Self.loadEvents(defaults.data(forKey: K.callEvents))
        msgEvents = Self.loadEvents(defaults.data(forKey: K.msgEvents))
    }

    // MARK: - 연락처

    func refresh() async {
        loading = true
        let ok = await ContactsService.requestAccess()
        contactsAuthorized = ok
        if ok {
            contacts = ContactsService.loadContacts()
        }
        loading = false
    }

    func contact(for id: String) -> Contact? { contacts.first { $0.id == id } }

    func recipients() -> [Contact] { contacts.filter { recipientIDs.contains($0.id) } }

    // MARK: - 연락 기록 (가까운 친구 집계용)

    func recordCall(_ id: String) {
        callEvents[id, default: []].append(Date())
    }

    func recordMessage(_ ids: [String]) {
        let now = Date()
        for id in ids { msgEvents[id, default: []].append(now) }
    }

    /// 최근 30일간 앱으로 연락한 횟수 기준 '가까운 친구'.
    func closeFriends() -> [ContactFrequency] {
        let since = Date().addingTimeInterval(-30 * 24 * 60 * 60)
        func count(_ dict: [String: [Date]], _ id: String) -> Int {
            (dict[id] ?? []).filter { $0 >= since }.count
        }
        return contacts.compactMap { c -> ContactFrequency? in
            let calls = count(callEvents, c.id)
            let msgs = count(msgEvents, c.id)
            let starred = starredIDs.contains(c.id)
            let total = calls + msgs
            // 즐겨찾기이거나, 기준 이상 연락한 사람
            guard starred || total >= closeThreshold else { return nil }
            return ContactFrequency(contact: c, callCount: calls, messageCount: msgs)
        }
        .sorted { $0.total > $1.total }
    }

    // MARK: - 즐겨찾기 / 받는 사람

    func toggleStar(_ id: String) {
        if starredIDs.contains(id) { starredIDs.remove(id) } else { starredIDs.insert(id) }
    }

    func toggleRecipient(_ id: String) {
        if recipientIDs.contains(id) { recipientIDs.remove(id) } else { recipientIDs.insert(id) }
    }

    /// 가까운 친구로 잡힌 사람들을 즐겨찾기에 한번에 추가.
    func starAllCloseFriends() {
        for f in closeFriends() { starredIDs.insert(f.contact.id) }
    }

    // MARK: - 알림

    func configureRemindersOnLaunch() async {
        guard autoReminder else { return }
        _ = await NotificationsService.requestAuthorization()
        NotificationsService.scheduleDaily(hour: checkInHour)
    }

    func setAutoReminder(_ on: Bool) {
        autoReminder = on
        if on {
            Task {
                _ = await NotificationsService.requestAuthorization()
                NotificationsService.scheduleDaily(hour: checkInHour)
            }
        } else {
            NotificationsService.cancelDaily()
        }
    }

    private func rescheduleIfNeeded() {
        if autoReminder {
            NotificationsService.scheduleDaily(hour: checkInHour)
        }
    }

    // MARK: - 저장 헬퍼

    private func save(ids: Set<String>, key: String) {
        defaults.set(Array(ids), forKey: key)
    }

    private func saveEvents(_ events: [String: [Date]], key: String) {
        if let data = try? JSONEncoder().encode(events) {
            defaults.set(data, forKey: key)
        }
    }

    private static func loadEvents(_ data: Data?) -> [String: [Date]] {
        guard let data, let decoded = try? JSONDecoder().decode([String: [Date]].self, from: data) else {
            return [:]
        }
        return decoded
    }
}
