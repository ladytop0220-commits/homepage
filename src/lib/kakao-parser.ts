/**
 * 카카오톡 채널 "생활의 여유" — 주문 양식 파서
 *
 * 고객이 보내는 양식 (확정):
 *   1. 지점명
 *   2. 주문 수량
 *   3. 담당자 성함
 *   4. 핸드폰 번호 or 지점 전화번호
 *   5. 이메일 주소
 *   6. 카드 번호 / 유효 기간       ⚠️ 보안상 결제 링크 전환 권고
 *   7. 거래명세서 발행 여부
 *
 * MVP는 정규식 기반의 빠른 파싱. Phase 2에서 Claude API로 정규화 보강.
 */

export interface ParsedKakaoOrder {
  branchName: string;
  quantity: number | null;
  contactName: string;
  phone: string;
  email: string;
  paymentMethod: "card" | "bank_transfer" | "deposit" | "unknown";
  /** 보안상 카드 번호는 마스킹만 보존. 절대 원본 저장 금지. */
  cardMasked?: string;
  cardExpiryMasked?: string;
  invoiceRequested: boolean;
  invoiceEmail?: string;
  warnings: string[]; // UI에서 ⚠ 배지로 표시
  rawByQuestion: Record<number, string>; // 원본 답변 (감사용)
}

const NUMBER_LINE = /^\s*(\d+)\s*[.)\-:]\s*(.*)$/;

/** 본문에서 1~7번 답변을 추출 (질문 사이의 후속 줄도 포함) */
function extractByNumber(input: string): Record<number, string> {
  const lines = input.split(/\r?\n/);
  const buckets: Record<number, string[]> = {};
  let current: number | null = null;

  for (const line of lines) {
    const m = line.match(NUMBER_LINE);
    if (m) {
      current = parseInt(m[1], 10);
      buckets[current] = buckets[current] ?? [];
      if (m[2].trim()) buckets[current].push(m[2].trim());
    } else if (current && line.trim()) {
      buckets[current].push(line.trim());
    }
  }

  const result: Record<number, string> = {};
  for (const [k, v] of Object.entries(buckets)) {
    result[parseInt(k, 10)] = v.join(" ").trim();
  }
  return result;
}

/** 카드번호 패턴 감지 — 매치된 경우 마지막 4자리만 남기고 마스킹 */
function maskCard(raw: string): { masked?: string; expiry?: string } {
  // 13~19자리 숫자 (공백·하이픈 허용)
  const cardMatch = raw.match(/(?:\d[ \-]?){13,19}/);
  let masked: string | undefined;
  if (cardMatch) {
    const digits = cardMatch[0].replace(/\D/g, "");
    if (digits.length >= 13 && digits.length <= 19) {
      const last4 = digits.slice(-4);
      masked = `**** **** **** ${last4}`;
    }
  }

  // 유효기간: MM/YY 또는 MM/YYYY 또는 MM-YY
  const expMatch = raw.match(/\b(0[1-9]|1[0-2])[ \/\-](\d{2}|\d{4})\b/);
  const expiry = expMatch ? "**/**" : undefined;

  return { masked, expiry };
}

function detectPaymentMethod(raw: string): ParsedKakaoOrder["paymentMethod"] {
  const text = raw.toLowerCase();
  if (/카드|신용|체크|card/.test(text)) return "card";
  if (/계좌|이체|transfer/.test(text)) return "bank_transfer";
  if (/무통장|입금|deposit/.test(text)) return "deposit";
  return "unknown";
}

function normalizePhone(raw: string): string {
  const digits = raw.replace(/\D/g, "");
  if (digits.length === 11 && digits.startsWith("010")) {
    return `${digits.slice(0, 3)}-${digits.slice(3, 7)}-${digits.slice(7)}`;
  }
  if (digits.length === 10 && digits.startsWith("02")) {
    return `${digits.slice(0, 2)}-${digits.slice(2, 6)}-${digits.slice(6)}`;
  }
  if (digits.length === 10) {
    return `${digits.slice(0, 3)}-${digits.slice(3, 6)}-${digits.slice(6)}`;
  }
  // 알아볼 수 없으면 원문 유지
  return raw.trim();
}

function extractEmail(raw: string): string {
  const m = raw.match(/[\w.+-]+@[\w-]+\.[\w.-]+/);
  return m ? m[0] : "";
}

function parseInvoice(raw: string): { requested: boolean; email?: string } {
  const trimmed = raw.trim();
  const requested = /발행|필요|예|네|yes|y\b|o\b/i.test(trimmed) && !/불필요|아니오|아니요|미발행|x\b|n\b/i.test(trimmed);
  const email = extractEmail(trimmed);
  return { requested, email: email || undefined };
}

export function parseKakaoOrder(rawText: string): ParsedKakaoOrder {
  const byNum = extractByNumber(rawText);
  const warnings: string[] = [];

  // 1. 지점명
  const branchName = byNum[1] ?? "";
  if (!branchName) warnings.push("1번 지점명을 찾을 수 없습니다.");

  // 2. 주문 수량 — 숫자 추출
  const qtyMatch = (byNum[2] ?? "").match(/\d+/);
  const quantity = qtyMatch ? parseInt(qtyMatch[0], 10) : null;
  if (quantity === null) warnings.push("2번 주문 수량을 숫자로 인식하지 못했습니다.");

  // 3. 담당자 성함
  const contactName = byNum[3] ?? "";
  if (!contactName) warnings.push("3번 담당자 성함을 찾을 수 없습니다.");

  // 4. 연락처
  const phone = normalizePhone(byNum[4] ?? "");
  if (!phone) warnings.push("4번 연락처를 찾을 수 없습니다.");

  // 5. 이메일
  const email = extractEmail(byNum[5] ?? byNum[5] ?? "");
  if (!email) warnings.push("5번 이메일을 인식하지 못했습니다.");

  // 6. 카드/결제 — 카드번호 마스킹 후 즉시 폐기
  const raw6 = byNum[6] ?? "";
  const paymentMethod = detectPaymentMethod(raw6);
  const { masked, expiry } = maskCard(raw6);
  if (masked) {
    warnings.push(
      "⚠ 카드번호가 감지되어 자동 마스킹되었습니다. 원본은 DB에 저장하지 않습니다. 결제 링크(토스) 발송을 권장합니다.",
    );
  }
  if (paymentMethod === "unknown") {
    warnings.push("6번 결제 방식을 자동 판별하지 못했습니다. 수동 선택 필요.");
  }

  // 7. 거래명세서
  const invoice = parseInvoice(byNum[7] ?? "");
  // 발행 원하면서 7번에 이메일이 없으면 5번 이메일을 fallback
  const invoiceEmail = invoice.email ?? (invoice.requested ? email : undefined);
  if (invoice.requested && !invoiceEmail) {
    warnings.push("7번 거래명세서 발행 요청이지만 수신 이메일이 없습니다.");
  }

  // 6번 원본 텍스트를 rawByQuestion에서 즉시 마스킹된 버전으로 교체 (저장 안전성)
  const safeRawByQuestion: Record<number, string> = { ...byNum };
  if (masked) {
    safeRawByQuestion[6] = `[REDACTED CARD ${masked}] ${paymentMethod}`;
  }

  return {
    branchName: branchName.trim(),
    quantity,
    contactName: contactName.trim(),
    phone,
    email,
    paymentMethod,
    cardMasked: masked,
    cardExpiryMasked: expiry,
    invoiceRequested: invoice.requested,
    invoiceEmail,
    warnings,
    rawByQuestion: safeRawByQuestion,
  };
}
