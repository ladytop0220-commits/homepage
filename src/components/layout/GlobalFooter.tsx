import Link from "next/link";

const BUSINESS_INFO = {
  name: "주식회사 기펠인터내셔날",
  representative: "김미정",
  bizNumber: "560-88-00664",
  address: "서울특별시 마포구 마포대로 92, A동 3층 (도화동, 효성해링턴 스퀘어)",
  contact: "[고객센터 번호 — 추후 입력]",
  email: "[고객센터 이메일 — 추후 입력]",
};

const POLICY_LINKS = [
  { href: "/policy/terms", label: "이용약관" },
  { href: "/policy/privacy", label: "개인정보처리방침" },
  { href: "/policy/return", label: "반품·교환 정책" },
];

export function GlobalFooter() {
  return (
    <footer className="mt-32 border-t border-ardoise/10 bg-creme">
      <div className="mx-auto max-w-7xl px-4 py-12 sm:px-8">
        {/* Top: brand + policy links */}
        <div className="flex flex-col gap-8 md:flex-row md:items-start md:justify-between">
          <div className="space-y-3">
            <div className="font-display text-2xl">Émelle</div>
            <div className="label-caps text-ardoise/60">
              Atelier · by Gipeel International
            </div>
            <span className="brand-divider mt-2 block" />
          </div>

          <nav className="flex flex-wrap gap-x-6 gap-y-2 text-sm">
            {POLICY_LINKS.map((link) => (
              <Link
                key={link.href}
                href={link.href}
                className="text-ardoise/70 hover:text-ardoise"
              >
                {link.label}
              </Link>
            ))}
          </nav>
        </div>

        {/* Business info */}
        <div className="mt-12 grid gap-1 text-xs leading-relaxed text-ardoise/60">
          <p>{BUSINESS_INFO.name} · 대표 {BUSINESS_INFO.representative}</p>
          <p>사업자등록번호 {BUSINESS_INFO.bizNumber}</p>
          <p>{BUSINESS_INFO.address}</p>
          <p>
            문의 {BUSINESS_INFO.contact} · {BUSINESS_INFO.email}
          </p>
          <p className="mt-4 text-ardoise/40">
            © {new Date().getFullYear()} Gipeel International Co., Ltd. All rights reserved.
          </p>
        </div>
      </div>
    </footer>
  );
}
