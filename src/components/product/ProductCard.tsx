import Link from "next/link";
import type { Product } from "@/data/products";

interface ProductCardProps {
  product: Product;
}

export function ProductCard({ product }: ProductCardProps) {
  const totalStock = product.options.reduce((sum, o) => sum + o.stock, 0);
  const isSoldOut = totalStock === 0 && product.priceRetail > 0;

  return (
    <Link
      href={`/products/${product.slug}`}
      className="group block"
      aria-label={product.name.ko}
    >
      <div className="aspect-[4/5] overflow-hidden bg-creme">
        {/* eslint-disable-next-line @next/next/no-img-element */}
        <img
          src={product.images[0]}
          alt={product.name.ko}
          className="h-full w-full object-cover transition duration-500 group-hover:scale-[1.02]"
        />
      </div>

      <div className="mt-4 space-y-1">
        <div className="label-caps text-ardoise/50">
          {product.line === "cuisine" ? "Cuisine" : "Maison"}
        </div>
        <h3 className="font-display text-lg text-ardoise transition group-hover:text-laiton">
          {product.name.ko}
        </h3>
        <p className="text-sm text-ardoise/60">{product.shortDescription.ko}</p>
        <div className="pt-2 text-sm">
          {product.priceRetail > 0 ? (
            <span className="font-medium">
              ₩{product.priceRetail.toLocaleString("ko-KR")}
            </span>
          ) : (
            <span className="text-ardoise/40">가격 미정</span>
          )}
          {isSoldOut && (
            <span className="ml-2 text-argile">품절</span>
          )}
        </div>
      </div>
    </Link>
  );
}
