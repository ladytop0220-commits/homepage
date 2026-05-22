import { KakaoIntakeClient } from "./KakaoIntakeClient";

export const metadata = { title: "카톡 주문 가져오기" };

export default function KakaoIntakePage() {
  return (
    <div className="space-y-6">
      <header>
        <h1 className="font-display text-3xl text-ardoise">
          카톡 주문 가져오기
        </h1>
        <p className="mt-1 text-sm text-ardoise/60">
          카카오톡 채널 &quot;생활의 여유&quot;에서 받은 주문 양식 답변을
          붙여넣으면 자동 파싱됩니다.
        </p>
      </header>

      {/* 보안 경고 */}
      <div className="border border-argile bg-argile/5 p-4 text-sm">
        <strong className="block text-argile">⚠ 보안 권고</strong>
        <p className="mt-1 text-ardoise/80">
          6번 카드번호는 카카오톡으로 받지 않는 것을 강력히 권장합니다.
          (PCI DSS·개인정보보호법 위반 소지). 입력된 카드번호는{" "}
          <strong>자동 마스킹</strong>되며 DB에 저장되지 않습니다.
          토스페이먼츠 결제 링크 발송으로 전환을 권장합니다.
        </p>
      </div>

      <KakaoIntakeClient />
    </div>
  );
}
