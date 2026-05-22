"use client";

import { useState } from "react";
import { parseKakaoOrder, type ParsedKakaoOrder } from "@/lib/kakao-parser";

const EXAMPLE = `1. 강남점
2. 5개
3. 박지영
4. 010-1234-5678
5. gangnam@example.com
6. 카드 결제
7. 발행 필요 (gangnam@example.com)`;

export function KakaoIntakeClient() {
  const [rawText, setRawText] = useState("");
  const [parsed, setParsed] = useState<ParsedKakaoOrder | null>(null);
  const [submitState, setSubmitState] = useState<"idle" | "submitting" | "success">("idle");

  function handleParse() {
    if (!rawText.trim()) return;
    setParsed(parseKakaoOrder(rawText));
    setSubmitState("idle");
  }

  function handleSubmit() {
    if (!parsed) return;
    setSubmitState("submitting");
    // TODO Phase 2: POST /api/admin/orders with parsed (minus card raw)
    setTimeout(() => {
      setSubmitState("success");
    }, 600);
  }

  function handleReset() {
    setRawText("");
    setParsed(null);
    setSubmitState("idle");
  }

  return (
    <div className="grid gap-6 lg:grid-cols-2">
      {/* Step 1: 입력 */}
      <section className="space-y-3">
        <div className="flex items-center justify-between">
          <h2 className="font-medium text-ardoise">
            <span className="label-caps text-ardoise/40">Step 1.</span> 답변 붙여넣기
          </h2>
          <button
            type="button"
            onClick={() => setRawText(EXAMPLE)}
            className="text-xs text-laiton hover:underline"
          >
            예시 채우기
          </button>
        </div>
        <textarea
          value={rawText}
          onChange={(e) => setRawText(e.target.value)}
          placeholder={`1. 지점명\n2. 주문 수량\n3. 담당자 성함\n4. 연락처\n5. 이메일\n6. 결제 방식\n7. 거래명세서 발행 여부`}
          className="h-72 w-full resize-none border border-ardoise/20 bg-creme/30 p-3 font-mono text-sm leading-relaxed outline-none focus:border-ardoise"
        />
        <div className="flex gap-2">
          <button
            type="button"
            onClick={handleParse}
            disabled={!rawText.trim()}
            className="h-10 flex-1 bg-ardoise text-sm text-creme transition hover:bg-mousse disabled:opacity-50"
          >
            자동 파싱
          </button>
          <button
            type="button"
            onClick={handleReset}
            className="h-10 border border-ardoise/30 px-4 text-sm text-ardoise/70 hover:bg-ardoise/5"
          >
            초기화
          </button>
        </div>
      </section>

      {/* Step 2: 결과 */}
      <section className="space-y-3">
        <h2 className="font-medium text-ardoise">
          <span className="label-caps text-ardoise/40">Step 2.</span> 파싱 결과
        </h2>

        {!parsed ? (
          <div className="flex h-72 items-center justify-center border border-dashed border-ardoise/20 bg-creme/20 text-sm text-ardoise/40">
            왼쪽에 붙여넣고 &quot;자동 파싱&quot;을 눌러주세요.
          </div>
        ) : (
          <div className="space-y-3 border border-ardoise/10 bg-white p-4">
            {parsed.warnings.length > 0 && (
              <ul className="space-y-1 border-l-2 border-argile bg-argile/5 px-3 py-2 text-xs text-argile">
                {parsed.warnings.map((w, i) => (
                  <li key={i}>{w}</li>
                ))}
              </ul>
            )}

            <Field label="1. 지점명" value={parsed.branchName} />
            <Field
              label="2. 주문 수량"
              value={parsed.quantity !== null ? `${parsed.quantity}개` : "—"}
            />
            <Field label="3. 담당자" value={parsed.contactName} />
            <Field label="4. 연락처" value={parsed.phone} />
            <Field label="5. 이메일" value={parsed.email} />
            <Field
              label="6. 결제 방식"
              value={
                {
                  card: "신용카드",
                  bank_transfer: "계좌이체",
                  deposit: "무통장입금",
                  unknown: "(미확인 — 수동 선택 필요)",
                }[parsed.paymentMethod]
              }
            />
            {parsed.cardMasked && (
              <div className="ml-4 border-l-2 border-argile/40 pl-3 text-xs text-ardoise/60">
                <div>카드번호: {parsed.cardMasked} (마스킹됨, 저장 안 함)</div>
                <div>유효기간: {parsed.cardExpiryMasked}</div>
                <div className="mt-1 text-argile">
                  → 토스 결제 링크 발송으로 처리 권장
                </div>
              </div>
            )}
            <Field
              label="7. 거래명세서"
              value={
                parsed.invoiceRequested
                  ? `발행 (수신: ${parsed.invoiceEmail ?? "없음"})`
                  : "미발행"
              }
            />

            <div className="border-t border-ardoise/10 pt-3">
              <button
                type="button"
                onClick={handleSubmit}
                disabled={submitState === "submitting" || submitState === "success"}
                className="h-10 w-full bg-mousse text-sm text-creme transition hover:bg-ardoise disabled:opacity-50"
              >
                {submitState === "idle" && "주문 등록"}
                {submitState === "submitting" && "등록 중..."}
                {submitState === "success" && "✓ 등록 완료 (Phase 2에서 DB 연동)"}
              </button>
            </div>
          </div>
        )}
      </section>
    </div>
  );
}

function Field({ label, value }: { label: string; value: string }) {
  return (
    <div className="flex gap-3 border-b border-ardoise/5 py-2 text-sm">
      <span className="w-24 shrink-0 text-ardoise/50">{label}</span>
      <span className="flex-1 text-ardoise">{value || "—"}</span>
    </div>
  );
}
