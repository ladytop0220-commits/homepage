import Link from "next/link";

interface ComingSoonProps {
  title: string;
  subtitle?: string;
  backHref?: string;
  backLabel?: string;
}

export function ComingSoon({
  title,
  subtitle = "준비 중입니다.",
  backHref = "/",
  backLabel = "홈으로",
}: ComingSoonProps) {
  return (
    <section className="mx-auto max-w-3xl px-4 py-32 text-center sm:px-8">
      <span className="brand-divider center" />
      <h1 className="mt-8 font-display text-4xl text-ardoise">{title}</h1>
      <p className="mt-4 text-ardoise/60">{subtitle}</p>
      <Link
        href={backHref}
        className="label-caps mt-12 inline-block text-laiton hover:text-ardoise"
      >
        ← {backLabel}
      </Link>
    </section>
  );
}
