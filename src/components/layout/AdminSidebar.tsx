"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";

const NAV_GROUPS = [
  {
    label: "운영",
    items: [
      { href: "/admin/dashboard", label: "대시보드" },
      { href: "/admin/orders", label: "주문 관리" },
      { href: "/admin/kakao-intake", label: "카톡 주문 가져오기" },
    ],
  },
  {
    label: "재고",
    items: [
      { href: "/admin/products", label: "제품 관리" },
      { href: "/admin/inventory", label: "재고 현황" },
    ],
  },
  {
    label: "회원",
    items: [
      { href: "/admin/customers", label: "회원 목록" },
      { href: "/admin/staff", label: "직원 계정" },
    ],
  },
];

export function AdminSidebar() {
  const pathname = usePathname();

  return (
    <aside className="hidden w-60 shrink-0 border-r border-ardoise/10 bg-creme/50 lg:block">
      <div className="border-b border-ardoise/10 px-6 py-5">
        <div className="font-display text-xl text-ardoise">Émelle</div>
        <div className="label-caps text-ardoise/50">Admin</div>
      </div>
      <nav className="space-y-6 px-3 py-6">
        {NAV_GROUPS.map((group) => (
          <div key={group.label}>
            <div className="label-caps mb-2 px-3 text-ardoise/40">
              {group.label}
            </div>
            <ul className="space-y-1">
              {group.items.map((item) => {
                const active =
                  pathname === item.href ||
                  pathname.startsWith(item.href + "/");
                return (
                  <li key={item.href}>
                    <Link
                      href={item.href}
                      className={`block rounded px-3 py-2 text-sm transition ${
                        active
                          ? "bg-ardoise text-creme"
                          : "text-ardoise/70 hover:bg-ardoise/5 hover:text-ardoise"
                      }`}
                    >
                      {item.label}
                    </Link>
                  </li>
                );
              })}
            </ul>
          </div>
        ))}
      </nav>
    </aside>
  );
}
