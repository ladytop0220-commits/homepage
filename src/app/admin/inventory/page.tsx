import { ComingSoon } from "@/components/ui/ComingSoon";

export const metadata = { title: "재고 현황" };

export default function AdminInventoryPage() {
  return (
    <ComingSoon
      title="재고 현황"
      subtitle="DB 연동 후 활성화됩니다."
      backHref="/admin/dashboard"
      backLabel="대시보드"
    />
  );
}
