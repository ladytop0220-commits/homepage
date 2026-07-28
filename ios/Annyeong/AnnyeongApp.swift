import SwiftUI

/// 앱 진입점. 전역 상태(AppModel)를 주입한다.
@main
struct AnnyeongApp: App {
    @StateObject private var model = AppModel()

    var body: some Scene {
        WindowGroup {
            HomeView()
                .environmentObject(model)
                .tint(AnnyeongColor.green)
        }
    }
}
