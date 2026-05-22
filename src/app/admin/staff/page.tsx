import { ComingSoon } from "@/components/ui/ComingSoon";

export const metadata = { title: "직원 계정" };

export default function AdminStaffPage() {
  return (
    <ComingSoon
      title="직원 계정 (대표만)"
      subtitle="DB 연동 + 권한 시스템 후 활성화됩니다."
      backHref="/admin/dashboard"
      backLabel="대시보드"
    />
  );
}
