import SwiftUI

/// 폰 연락처의 한 사람
struct Contact: Identifiable, Hashable {
    let id: String        // CNContact.identifier
    let name: String
    let phone: String

    /// 아바타용 이름 첫 글자
    var initial: String {
        String(name.trimmingCharacters(in: .whitespaces).first ?? "?")
    }
}

/// 앱을 통해 연락(전화/문자)한 빈도 — iOS는 시스템 통화·문자 기록을 읽을 수 없어서
/// '이 앱으로 연락한 횟수'를 기준으로 가까운 친구를 집계한다.
struct ContactFrequency: Identifiable {
    var id: String { contact.id }
    let contact: Contact
    let callCount: Int
    let messageCount: Int
    var total: Int { callCount + messageCount }
}

/// 안녕 브랜드 색상 (편안한 초록 + 높은 대비)
enum AnnyeongColor {
    static let green = Color(red: 0.18, green: 0.49, blue: 0.36)   // 기본
    static let call = Color(red: 0.10, green: 0.54, blue: 0.29)    // 전화(초록)
    static let message = Color(red: 0.08, green: 0.40, blue: 0.75) // 문자(파랑)
    static let warn = Color(red: 0.69, green: 0.0, blue: 0.13)     // 긴급(빨강)
    static let cream = Color(red: 0.96, green: 0.95, blue: 0.93)
}
