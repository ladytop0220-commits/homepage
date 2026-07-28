import UserNotifications

/// 매일 정한 시각에 "안부 문자를 보내요" 리마인더 알림을 예약한다.
/// (iOS는 문자 자동발송이 불가하므로, 알림으로 사용자가 직접 보내도록 돕는다.)
enum NotificationsService {

    static func requestAuthorization() async -> Bool {
        do {
            return try await UNUserNotificationCenter.current()
                .requestAuthorization(options: [.alert, .sound, .badge])
        } catch {
            return false
        }
    }

    static let dailyID = "annyeong_daily_checkin_reminder"

    /// 매일 hour 시에 반복되는 알림 예약.
    static func scheduleDaily(hour: Int) {
        let center = UNUserNotificationCenter.current()
        center.removePendingNotificationRequests(withIdentifiers: [dailyID])

        let content = UNMutableNotificationContent()
        content.title = "안녕"
        content.body = "가족·친구에게 안부 문자를 보낼 시간이에요. 눌러서 보내보세요."
        content.sound = .default

        var comps = DateComponents()
        comps.hour = hour
        comps.minute = 0
        let trigger = UNCalendarNotificationTrigger(dateMatching: comps, repeats: true)
        let request = UNNotificationRequest(identifier: dailyID, content: content, trigger: trigger)
        center.add(request)
    }

    static func cancelDaily() {
        UNUserNotificationCenter.current().removePendingNotificationRequests(withIdentifiers: [dailyID])
    }
}
