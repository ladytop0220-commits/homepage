import { GlobalHeader } from "@/components/layout/GlobalHeader";
import { GlobalFooter } from "@/components/layout/GlobalFooter";

export default function CustomerLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <>
      <GlobalHeader />
      <main className="flex-1">{children}</main>
      <GlobalFooter />
    </>
  );
}
