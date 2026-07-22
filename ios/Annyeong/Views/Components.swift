import SwiftUI

/// 어르신용 큰 버튼 — 아이콘 + 큰 글씨, 넉넉한 터치 영역.
struct BigButton: View {
    let title: String
    var subtitle: String? = nil
    let systemImage: String
    let color: Color
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            HStack(spacing: 18) {
                Image(systemName: systemImage)
                    .font(.system(size: 40, weight: .semibold))
                    .foregroundStyle(.white)
                    .frame(width: 56)
                VStack(alignment: .leading, spacing: 2) {
                    Text(title)
                        .font(.system(size: 26, weight: .bold))
                        .foregroundStyle(.white)
                    if let subtitle {
                        Text(subtitle)
                            .font(.system(size: 17))
                            .foregroundStyle(.white.opacity(0.9))
                    }
                }
                Spacer()
            }
            .padding(.horizontal, 22)
            .frame(maxWidth: .infinity, minHeight: subtitle == nil ? 92 : 108, alignment: .leading)
            .background(color)
            .clipShape(RoundedRectangle(cornerRadius: 22, style: .continuous))
        }
        .buttonStyle(.plain)
    }
}

/// 이름 첫 글자 아바타.
struct InitialAvatar: View {
    let initial: String
    var size: CGFloat = 52

    var body: some View {
        Circle()
            .fill(AnnyeongColor.green.opacity(0.15))
            .frame(width: size, height: size)
            .overlay(
                Text(initial)
                    .font(.system(size: size * 0.42, weight: .bold))
                    .foregroundStyle(AnnyeongColor.green)
            )
    }
}
