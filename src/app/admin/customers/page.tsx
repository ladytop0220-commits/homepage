import { ComingSoon } from "@/components/ui/ComingSoon";

export const metadata = { title: "회원 목록" };

export default function AdminCustomersPage() {
  return (
    <ComingSoon
      title="회원 목록"
      subtitle="DB 연동 후 활성화됩니다."
      backHref="/admin/dashboard"
      backLabel="대시보드"
    />
  );
}
