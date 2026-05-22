import { ComingSoon } from "@/components/ui/ComingSoon";

export const metadata = { title: "제품 관리" };

export default function AdminProductsPage() {
  return (
    <ComingSoon
      title="제품 관리"
      subtitle="MVP는 정적 data/products.ts에서 직접 관리합니다. Phase 2에서 DB UI 도입."
      backHref="/admin/dashboard"
      backLabel="대시보드"
    />
  );
}
