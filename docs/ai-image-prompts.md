# AI 이미지 생성 프롬프트 (Émelle 브랜드)

> SVG placeholder를 실제 사진/AI 생성 이미지로 교체할 때 사용할 프롬프트 모음입니다.
> Midjourney, DALL·E 3, Imagen 3, Flux 등 어느 모델에서도 쓸 수 있도록 작성되어 있습니다.

---

## 1. 제품 박스 목업 (Product Box Mockup)

**의도**: Eisenach 박스를 대체할 Émelle 브랜드 박스 이미지. 광고/카드용.

### Midjourney
```
Premium kitchenware product box mockup, "Émelle" wordmark in italic serif typography, cream background #F4EDE0, soft dark green accent #4A5D3F, brass gold trim #A88B5C, minimalist European atelier aesthetic, single ceramic frying pan in front of the box, natural soft window light from upper left, no clutter, no other text, marble or oak tabletop, editorial photography, shallow depth of field, ceramic coating pan with bakelite handle visible --ar 4:5 --style raw --v 6
```

### DALL·E 3 / GPT-Image
```
A premium product photograph for a French-inspired Korean kitchenware brand called "Émelle by Gipeel International". A minimalist beige cardboard product box stands upright on a clean cream surface (color #F4EDE0). The box front shows the wordmark "Émelle" in elegant italic serif (Georgia/GT Sectra style), with a thin brass-gold horizontal line beneath, and small uppercase letters reading "ATELIER · BY GIPEEL INTERNATIONAL". A single italic word "Cuisine" appears in dark olive green (#4A5D3F). In front of the box, a 28cm cream-and-beige ceramic frying pan rests on the surface with handle pointing right. Soft natural light from upper left, gentle shadows, no other props, no people. Editorial e-commerce style, 4:5 vertical, photorealistic.
```

### Negative prompt 권장
```
no random text, no other brand names, no Eisenach, no GIPFEL,
no logos other than Émelle, no people, no hands, no clutter,
no neon, no cartoon style
```

---

## 2. 홈 Hero 이미지

**의도**: 메인 페이지 상단에 들어갈 브랜드 무드 컷.

```
Editorial lifestyle photograph for premium kitchenware brand "Émelle". A single cream ceramic frying pan resting on a warm beige linen cloth, on a sun-lit oak countertop near a window. Soft morning light, dried thyme branches and a stone pestle in background, slightly out of focus. Color palette: cream #F4EDE0, deep olive green #4A5D3F, dark slate #2B2926, brass #A88B5C. Calm, atelier mood, European luxury minimalism. No text, no logos. Square format 1:1, photorealistic, shallow depth of field.
```

---

## 3. 제품 단독 컷 (Product-only)

**의도**: 제품 상세 페이지 메인 이미지. 박스 없이 팬만.

```
E-commerce style product photograph of a single 28cm ceramic-coated frying pan, cream colored interior and white exterior, beige bakelite handle pointing to the right. Pure cream background #F4EDE0, soft top-down light, slight angle showing the cooking surface, subtle natural shadow underneath. No props, no text. Editorial, photorealistic, 1:1 square, shallow depth of field, slight grain.
```

---

## 4. 매니페스토 무드 컷 (Manifesto Mood)

**의도**: About 페이지나 푸터 위의 분위기 이미지.

```
Moody close-up macro photograph of a glossy enamel/ceramic surface, showing fine texture and reflections, deep slate dark background #2B2926 fading into warm cream. Single golden brass-colored line of light reflection. Atmospheric, calm, premium atelier feel. No text, no objects, abstract surface study. 16:9 cinematic.
```

---

## 사용 시 주의

- 생성된 이미지에 **Eisenach, GIPFEL 등 기존 브랜드명이 보이면 안 됨**
- "Émelle" 철자가 잘못 나오면(예: "Emell", "Emele") **수동으로 후보정** 권장
- 박스 안 다른 브랜드 로고가 등장하면 폐기
- 색상이 사양과 다르면 후처리(보정) 또는 재생성

## 권장 워크플로

1. AI로 4~6장 생성 → 베스트 1장 선택
2. Photoshop에서:
   - "Émelle" 워드마크를 정확한 폰트로 다시 얹기 (가장 안전)
   - 배경 컬러 미세 조정 (#F4EDE0 정확히)
   - 1280×1600 (제품 카드), 2000×2000 (제품 상세) 두 사이즈 export
3. `/Users/kimmijung/emelle/public/products/` 에 저장
4. `src/data/products.ts`의 `images` 배열 경로 갱신
