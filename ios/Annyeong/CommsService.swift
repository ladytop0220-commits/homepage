import SwiftUI
import MessageUI

/// 전화 걸기 유틸. tel:// URL 을 열면 iOS 가 "통화하시겠어요?" 확인 후 전화를 건다.
enum CommsService {
    static func call(_ phone: String) {
        let digits = phone.filter { $0.isNumber || $0 == "+" }
        guard let url = URL(string: "tel://\(digits)"), UIApplication.shared.canOpenURL(url) else { return }
        UIApplication.shared.open(url)
    }

    static var canSendText: Bool { MFMessageComposeViewController.canSendText() }
}

/// 문자 작성 화면(MFMessageComposeViewController)을 SwiftUI 에서 띄우는 래퍼.
/// 내용은 미리 채워지고, 실제 '보내기'는 사용자가 눌러야 한다(iOS 정책).
struct MessageComposeView: UIViewControllerRepresentable {
    let recipients: [String]
    let body: String
    var onFinish: (MessageComposeResult) -> Void

    func makeCoordinator() -> Coordinator { Coordinator(onFinish: onFinish) }

    func makeUIViewController(context: Context) -> MFMessageComposeViewController {
        let vc = MFMessageComposeViewController()
        vc.recipients = recipients
        vc.body = body
        vc.messageComposeDelegate = context.coordinator
        return vc
    }

    func updateUIViewController(_ vc: MFMessageComposeViewController, context: Context) {}

    final class Coordinator: NSObject, MFMessageComposeViewControllerDelegate {
        let onFinish: (MessageComposeResult) -> Void
        init(onFinish: @escaping (MessageComposeResult) -> Void) { self.onFinish = onFinish }

        func messageComposeViewController(_ controller: MFMessageComposeViewController,
                                          didFinishWith result: MessageComposeResult) {
            onFinish(result)
        }
    }
}
