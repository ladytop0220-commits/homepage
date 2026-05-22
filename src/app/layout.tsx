import type { Metadata } from "next";
import "./globals.css";

export const metadata: Metadata = {
  title: {
    default: "Émelle — Atelier by Gipeel International",
    template: "%s · Émelle",
  },
  description:
    "두 번 구워낸 표면 위에서, 매일이 익는다. 에멜은 enamel을 한 단어로 정제한 프리미엄 주방·라이프스타일 브랜드입니다.",
  metadataBase: new URL("https://emelle.co.kr"),
  openGraph: {
    title: "Émelle — Atelier by Gipeel International",
    description: "Surfaces that hold the heat of memory.",
    type: "website",
    locale: "ko_KR",
  },
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="ko" className="h-full">
      <body className="min-h-full flex flex-col bg-creme text-ardoise">
        {children}
      </body>
    </html>
  );
}
