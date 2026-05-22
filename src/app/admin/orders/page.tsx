import { ComingSoon } from "@/components/ui/ComingSoon";

export const metadata = { title: "주문 관리" };

export default function AdminOrdersPage() {
  return (
    <ComingSoon
      title="주문 관리"
      subtitle="DB 연동 후 활성화됩니다."
      backHref="/admin/dashboard"
      backLabel="대시보드"
    />
  );
}
