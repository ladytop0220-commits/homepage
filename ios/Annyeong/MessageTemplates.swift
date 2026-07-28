import Foundation

/// 어르신의 일상/건강 상태로 가족·친구에게 보낼 안부 문자를 만들어 주는 생성기.
/// 문장 생성은 전적으로 기기 안에서 이뤄진다(오프라인).
enum MessageTemplates {

    enum Mood: String, CaseIterable, Identifiable {
        case good, okay, tired, lonely
        var id: String { rawValue }
        var label: String {
            switch self {
            case .good: return "좋음"
            case .okay: return "보통"
            case .tired: return "피곤"
            case .lonely: return "적적함"
            }
        }
        var phrase: String {
            switch self {
            case .good: return "오늘은 기분이 아주 좋아요"
            case .okay: return "오늘은 그럭저럭 잘 지내고 있어요"
            case .tired: return "오늘은 조금 피곤하지만 괜찮아요"
            case .lonely: return "오늘은 조금 적적하네요. 목소리가 그리워요"
            }
        }
    }

    enum Activity: String, CaseIterable, Identifiable {
        case meal, walk, medicine, sleep, hospital, friends
        var id: String { rawValue }
        var label: String {
            switch self {
            case .meal: return "식사 잘함"
            case .walk: return "산책함"
            case .medicine: return "약 챙겨먹음"
            case .sleep: return "잘 잤음"
            case .hospital: return "병원 다녀옴"
            case .friends: return "친구 만남"
            }
        }
        var phrase: String {
            switch self {
            case .meal: return "밥도 잘 챙겨 먹었어요"
            case .walk: return "동네 한 바퀴 산책도 다녀왔어요"
            case .medicine: return "약도 잊지 않고 챙겨 먹었어요"
            case .sleep: return "어젯밤 푹 잘 잤어요"
            case .hospital: return "오늘 병원에 다녀왔어요"
            case .friends: return "오랜만에 친구도 만났어요"
            }
        }
    }

    static func greeting(date: Date = Date()) -> String {
        let hour = Calendar.current.component(.hour, from: date)
        switch hour {
        case 5...10: return "좋은 아침이에요"
        case 11...16: return "잘 지내고 있나요"
        case 17...21: return "좋은 저녁이에요"
        default: return "늦은 시간에 미안해요"
        }
    }

    /// 상태를 조합해 자연스러운 안부 문자를 만든다.
    static func buildCheckIn(mood: Mood, activities: [Activity], senderName: String, note: String = "") -> String {
        var parts: [String] = []
        parts.append("\(greeting()).")
        parts.append("\(mood.phrase).")
        for a in activities { parts.append("\(a.phrase).") }
        let trimmedNote = note.trimmingCharacters(in: .whitespacesAndNewlines)
        if !trimmedNote.isEmpty { parts.append(trimmedNote) }
        parts.append("걱정 말고 잘 지내요.")
        if !senderName.isEmpty { parts.append("- \(senderName) 드림") }
        return parts.joined(separator: " ")
    }

    /// 버튼 하나로 보내는 "나 잘 있어요" 안심 문자
    static func quickSafe(senderName: String) -> String {
        let sign = senderName.isEmpty ? "" : " -\(senderName)"
        return "\(greeting()). 저는 오늘도 건강하게 잘 지내고 있어요. 걱정 말아요.\(sign)"
    }

    /// 상황별 미리 준비된 문구 (탭 한 번으로 사용)
    static func presets(senderName: String) -> [(title: String, body: String)] {
        let sign = senderName.isEmpty ? "" : " -\(senderName)"
        return [
            ("안부 인사", quickSafe(senderName: senderName)),
            ("보고 싶어요", "요즘 목소리가 참 그립네요. 시간 되면 전화 한 통 주세요.\(sign)"),
            ("밥 잘 챙겨요", "끼니 거르지 말고 밥 잘 챙겨 드세요. 저도 잘 챙겨 먹고 있어요.\(sign)"),
            ("고마워요", "늘 챙겨줘서 고마워요. 덕분에 든든하게 잘 지내요.\(sign)"),
        ]
    }
}
