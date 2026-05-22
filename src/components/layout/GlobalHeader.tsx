import Link from "next/link";

const NAV_ITEMS = [
  { href: "/products", label: "Products" },
  { href: "/about", label: "About" },
  { href: "/support", label: "Support" },
];

export function GlobalHeader() {
  return (
    <header className="sticky top-0 z-40 border-b border-ardoise/10 bg-creme/85 backdrop-blur">
      <div className="mx-auto flex h-16 max-w-7xl items-center justify-between px-4 sm:px-8">
        {/* Brand */}
        <Link
          href="/"
          className="font-display text-2xl tracking-tight text-ardoise"
          aria-label="Émelle 홈"
        >
          Émelle
        </Link>

        {/* Nav */}
        <nav className="hidden items-center gap-8 md:flex">
          {NAV_ITEMS.map((item) => (
            <Link
              key={item.href}
              href={item.href}
              className="label-caps text-ardoise/70 transition hover:text-ardoise"
            >
              {item.label}
            </Link>
          ))}
        </nav>

        {/* Actions */}
        <div className="flex items-center gap-4">
          <Link
            href="/cart"
            className="label-caps text-ardoise/70 hover:text-ardoise"
            aria-label="장바구니"
          >
            Cart (0)
          </Link>
          <Link
            href="/account/login"
            className="label-caps text-ardoise/70 hover:text-ardoise"
          >
            Login
          </Link>
        </div>
      </div>
    </header>
  );
}
