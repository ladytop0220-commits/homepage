import { ProductCard } from "@/components/product/ProductCard";
import { getActiveProducts } from "@/data/products";

export const metadata = { title: "Products" };

export default function ProductsPage() {
  const products = getActiveProducts();

  return (
    <section className="mx-auto max-w-7xl px-4 py-16 sm:px-8 sm:py-24">
      <div className="mb-12 space-y-3">
        <span className="label-caps text-laiton">— Cuisine —</span>
        <h1 className="font-display text-4xl text-ardoise sm:text-5xl">
          Products
        </h1>
      </div>

      {products.length === 0 ? (
        <div className="py-32 text-center">
          <p className="text-ardoise/60">곧 새로운 제품이 추가됩니다.</p>
        </div>
      ) : (
        <div className="grid gap-12 sm:grid-cols-2 lg:grid-cols-3">
          {products.map((product) => (
            <ProductCard key={product.slug} product={product} />
          ))}
        </div>
      )}
    </section>
  );
}
