import Link from "next/link";
import { ProductCard } from "@/components/product/ProductCard";
import { getActiveProducts } from "@/data/products";

export default function HomePage() {
  const featuredProducts = getActiveProducts();

  return (
    <>
      {/* === Hero === */}
      <section className="relative isolate overflow-hidden bg-creme">
        <div className="mx-auto grid max-w-7xl gap-12 px-4 py-24 sm:px-8 sm:py-32 lg:grid-cols-2 lg:items-center lg:py-40">
          <div className="space-y-8">
            <span className="brand-divider" />
            <h1 className="font-display text-5xl leading-[1.1] tracking-tight text-ardoise sm:text-6xl lg:text-7xl">
              두 번 구워낸 표면 위에서,
              <br />
              매일이 익는다.
            </h1>
            <p className="max-w-md text-base leading-relaxed text-ardoise/70">
              에멜은 enamel(법랑)이라는 표면 기술을 한 단어로 정제한 이름입니다.
              매일을 견디는 표면을, 주방과 집 안으로 옮깁니다.
            </p>
            <div className="flex gap-4">
              <Link
                href="/products"
                className="inline-flex h-12 items-center justify-center bg-ardoise px-8 text-sm text-creme transition hover:bg-mousse"
              >
                제품 보기
              </Link>
              <Link
                href="/about"
                className="inline-flex h-12 items-center justify-center border border-ardoise px-8 text-sm text-ardoise transition hover:bg-ardoise hover:text-creme"
              >
                브랜드 이야기
              </Link>
            </div>
          </div>

          <div className="aspect-[4/5] overflow-hidden bg-creme lg:aspect-square">
            {/* eslint-disable-next-line @next/next/no-img-element */}
            <img
              src="/products/ceramic-pan-28-eisenach.png"
              alt="Émelle Ceramic Pan"
              className="h-full w-full object-cover"
            />
          </div>
        </div>
      </section>

      {/* === Featured Product === */}
      <section className="mx-auto max-w-7xl px-4 py-24 sm:px-8">
        <div className="mb-12 space-y-3 text-center">
          <span className="label-caps text-laiton">— Product —</span>
          <h2 className="font-display text-3xl text-ardoise sm:text-4xl">
            처음 만나는 에멜
          </h2>
        </div>

        <div className="mx-auto grid max-w-4xl gap-12 sm:grid-cols-2">
          {featuredProducts.map((product) => (
            <ProductCard key={product.slug} product={product} />
          ))}
        </div>
      </section>

      {/* === Brand Manifesto === */}
      <section className="bg-ardoise text-creme">
        <div className="mx-auto max-w-3xl px-4 py-32 text-center sm:px-8">
          <span className="brand-divider center mb-8 bg-laiton" />
          <p className="font-display text-2xl leading-relaxed sm:text-3xl">
            표면은 도구가 끝나는 자리가 아니라,
            <br />
            도구가 시작되는 자리다.
          </p>
          <p className="mt-12 text-sm leading-loose text-creme/70">
            도자기처럼 두 번 구워낸 enamel은 단단함과 우아함을 동시에 품는다.
            <br />
            그 표면 위에서 매일의 요리가 익고, 매일의 가전이 일한다.
          </p>
          <Link
            href="/about"
            className="label-caps mt-12 inline-block text-laiton hover:text-creme"
          >
            더 알아보기 →
          </Link>
        </div>
      </section>
    </>
  );
}
