/**
 * Émelle 제품 카탈로그 (MVP 초기 데이터)
 *
 * MVP 단계에서는 DB 없이 정적 데이터로 운영.
 * Phase 2에서 Supabase products 테이블로 마이그레이션 예정.
 */

export type ProductLine = "cuisine" | "maison";
export type ProductCategory = "frying-pan" | "pot" | "cookware" | "kettle" | "blender" | "appliance";

export interface ProductOption {
  id: string;
  label: string;
  priceAdjustment?: number; // 기본가 대비 가산/할인 (원)
  stock: number;
}

export interface Product {
  slug: string;
  sku: string;
  brand: "Émelle";
  line: ProductLine;
  category: ProductCategory;
  name: { ko: string; en: string };
  shortDescription: { ko: string; en: string };
  description: { ko: string; en: string };
  specs: Array<{ label: string; value: string }>;
  priceRetail: number; // 소매가 (원)
  priceWholesale: number; // 도매가 (Phase 2 사용 예정)
  options: ProductOption[];
  images: string[]; // public/products/... 경로
  active: boolean;
}

export const PRODUCTS: Product[] = [
  {
    slug: "ceramic-pan-28",
    sku: "EM-CUI-PAN-28",
    brand: "Émelle",
    line: "cuisine",
    category: "frying-pan",
    name: {
      ko: "에멜 세라믹 팬 28cm",
      en: "Émelle Ceramic Pan 28cm",
    },
    shortDescription: {
      ko: "두 번 구워낸 표면. 매일의 요리를 위한 28cm IH 세라믹 팬.",
      en: "Twice-fired surface. A 28cm IH-ready ceramic pan for everyday cooking.",
    },
    description: {
      ko: `세라믹 코팅 위에서 매일이 익는다.\n\n알루미늄 본체와 베이클라이트 핸들. 인덕션을 포함한 모든 화구에서 안정적으로 사용 가능합니다. 표면은 도구가 끝나는 자리가 아니라, 도구가 시작되는 자리입니다.`,
      en: `Daily meals, cooked on a twice-fired surface.\n\nAluminum body, bakelite handle. Compatible with all heat sources including induction.`,
    },
    specs: [
      { label: "사이즈", value: "28cm" },
      { label: "코팅", value: "세라믹 (Ceramic)" },
      { label: "본체", value: "알루미늄 (Aluminum)" },
      { label: "핸들", value: "베이클라이트 (Bakelite)" },
      { label: "인덕션 호환", value: "예 (IH)" },
      { label: "원산지", value: "[기재 필요]" },
    ],
    priceRetail: 0, // TODO: 가격 책정 필요
    priceWholesale: 0,
    options: [{ id: "28cm", label: "28cm", stock: 0 }],
    images: [
      // ⚠️ 임시: 현재 보유 중인 Eisenach 패키지 사진. 사이트에 Émelle 브랜드로
      // 노출되므로 정식 출시 전 Émelle 브랜딩 이미지로 교체 필요.
      // 교체용 AI 프롬프트: docs/ai-image-prompts.md 참조.
      "/products/ceramic-pan-28-eisenach.png",
    ],
    active: true,
  },
];

export const getProductBySlug = (slug: string): Product | undefined =>
  PRODUCTS.find((p) => p.slug === slug && p.active);

export const getActiveProducts = (): Product[] =>
  PRODUCTS.filter((p) => p.active);
