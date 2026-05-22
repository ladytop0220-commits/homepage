import { AdminSidebar } from "@/components/layout/AdminSidebar";

export const metadata = { title: { default: "Admin", template: "%s · Admin" } };

export default function AdminLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <div className="flex min-h-screen flex-1">
      <AdminSidebar />
      <div className="flex flex-1 flex-col bg-white">
        <header className="flex h-14 items-center justify-between border-b border-ardoise/10 px-6">
          <div className="text-sm text-ardoise/60">Émelle Admin</div>
          <div className="text-sm text-ardoise">김미정 ▾</div>
        </header>
        <main className="flex-1 p-6 lg:p-8">{children}</main>
      </div>
    </div>
  );
}
