import { ComingSoon } from "@/components/ui/ComingSoon";

export const metadata = { title: "Cart" };

export default function CartPage() {
  return (
    <ComingSoon
      title="Cart"
      subtitle="장바구니 기능은 곧 활성화됩니다. (Phase 1 작업 중)"
      backHref="/products"
      backLabel="제품 보러 가기"
    />
  );
}
