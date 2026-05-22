import { notFound } from "next/navigation";
import Link from "next/link";
import { getProductBySlug, getActiveProducts } from "@/data/products";

interface PageProps {
  params: Promise<{ slug: string }>;
}

export async function generateStaticParams() {
  return getActiveProducts().map((p) => ({ slug: p.slug }));
}

export async function generateMetadata({ params }: PageProps) {
  const { slug } = await params;
  const product = getProductBySlug(slug);
  return { title: product?.name.ko ?? "Product" };
}

export default async function ProductDetailPage({ params }: PageProps) {
  const { slug } = await params;
  const product = getProductBySlug(slug);
  if (!product) notFound();

  const totalStock = product.options.reduce((sum, o) => sum + o.stock, 0);

  return (
    <section className="mx-auto max-w-7xl px-4 py-16 sm:px-8">
      <Link
        href="/products"
        className="label-caps text-ardoise/60 hover:text-ardoise"
      >
        ← Products
      </Link>

      <div className="mt-8 grid gap-12 lg:grid-cols-2 lg:gap-16">
        {/* Image */}
        <div className="aspect-square overflow-hidden bg-creme">
          {/* eslint-disable-next-line @next/next/no-img-element */}
          <img
            src={product.images[0]}
            alt={product.name.ko}
            className="h-full w-full object-cover"
          />
        </div>

        {/* Info */}
        <div className="space-y-6">
          <div className="space-y-2">
            <span className="label-caps text-laiton">
              {product.line === "cuisine" ? "Cuisine" : "Maison"}
            </span>
            <h1 className="font-display text-4xl text-ardoise">
              {product.name.ko}
            </h1>
            <p className="text-sm text-ardoise/60">{product.name.en}</p>
          </div>

          <p className="text-base leading-relaxed text-ardoise/80">
            {product.shortDescription.ko}
          </p>

          <div className="border-y border-ardoise/10 py-6">
            {product.priceRetail > 0 ? (
              <div className="text-3xl font-medium">
                ₩{product.priceRetail.toLocaleString("ko-KR")}
              </div>
            ) : (
              <div className="text-xl text-ardoise/40">가격 미정</div>
            )}
          </div>

          {/* Options */}
          <div className="space-y-3">
            <div className="label-caps text-ardoise/60">사이즈</div>
            <div className="flex gap-2">
              {product.options.map((opt) => (
                <button
                  key={opt.id}
                  type="button"
                  className="border border-ardoise px-4 py-2 text-sm transition hover:bg-ardoise hover:text-creme"
                >
                  {opt.label}
                </button>
              ))}
            </div>
          </div>

          <div className="flex gap-3 pt-4">
            <button
              type="button"
              disabled={totalStock === 0 && product.priceRetail > 0}
              className="flex h-12 flex-1 items-center justify-center border border-ardoise text-sm transition hover:bg-ardoise hover:text-creme disabled:cursor-not-allowed disabled:opacity-50"
            >
              장바구니 담기
            </button>
            <button
              type="button"
              disabled={totalStock === 0 && product.priceRetail > 0}
              className="flex h-12 flex-1 items-center justify-center bg-ardoise text-sm text-creme transition hover:bg-mousse disabled:cursor-not-allowed disabled:opacity-50"
            >
              바로 구매하기
            </button>
          </div>

          {/* Specs */}
          <div className="pt-8">
            <span className="label-caps text-laiton">— Specifications —</span>
            <dl className="mt-4 grid grid-cols-1 gap-y-2 text-sm sm:grid-cols-2">
              {product.specs.map((spec) => (
                <div key={spec.label} className="flex gap-2">
                  <dt className="text-ardoise/50">{spec.label}</dt>
                  <dd className="text-ardoise">{spec.value}</dd>
                </div>
              ))}
            </dl>
          </div>
        </div>
      </div>

      {/* Description */}
      <div className="mx-auto mt-24 max-w-2xl">
        <span className="label-caps block text-center text-laiton">
          — About this surface —
        </span>
        <p className="mt-6 whitespace-pre-line text-center text-base leading-loose text-ardoise/80">
          {product.description.ko}
        </p>
      </div>
    </section>
  );
}
