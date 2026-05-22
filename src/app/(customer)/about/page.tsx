export const metadata = { title: "About" };

export default function AboutPage() {
  return (
    <article className="mx-auto max-w-3xl px-4 py-24 sm:px-8 sm:py-32">
      <header className="text-center">
        <span className="brand-divider center" />
        <h1 className="mt-8 font-display text-5xl text-ardoise">
          Émelle
        </h1>
        <p className="label-caps mt-3 text-ardoise/60">
          Atelier · by Gipeel International
        </p>
      </header>

      <section className="mt-16 space-y-8 text-base leading-loose text-ardoise/80">
        <p className="font-display text-2xl leading-relaxed text-ardoise">
          에멜은 enamel(법랑)이라는 표면 기술을 한 단어로 정제한 이름이다.
          매일을 견디는 표면을, 주방과 집 안으로 옮긴다.
        </p>

        <hr className="border-ardoise/10" />

        <p>
          <strong className="font-medium text-ardoise">네이밍</strong> —
          Enamel의 어원을 따와 프랑스어 여성형 어미 -elle을 결합. 짧고
          모던하면서도 유럽 아틀리에 톤을 환기한다.
        </p>

        <p>
          <strong className="font-medium text-ardoise">모회사</strong> — Émelle은
          Gipeel International(기펠인터내셔날)의 프리미엄 라이프스타일
          브랜드다. 30년 주방 제조 기술을 기반으로, 더 정교한 표면과 더 넓은
          라인업으로 확장한다.
        </p>

        <hr className="border-ardoise/10" />

        <h2 className="font-display text-2xl text-ardoise">매니페스토</h2>
        <blockquote className="border-l-2 border-laiton pl-6 italic text-ardoise/70">
          표면은 도구가 끝나는 자리가 아니라, 도구가 시작되는 자리다.
          <br />
          도자기처럼 두 번 구워낸 enamel은 단단함과 우아함을 동시에 품는다.
          <br />
          그 표면 위에서 매일의 요리가 익고, 매일의 가전이 일한다.
        </blockquote>

        <h2 className="font-display text-2xl text-ardoise">두 라인</h2>
        <div className="grid gap-6 sm:grid-cols-2">
          <div className="border border-mousse bg-mousse/5 p-6">
            <span className="label-caps text-mousse">Cuisine</span>
            <p className="mt-3 text-sm text-ardoise/80">
              손으로 만지는 표면. 팬의 코팅, 냄비의 결, 도마의 마감.
            </p>
          </div>
          <div className="border border-ardoise bg-ardoise text-creme p-6">
            <span className="label-caps text-laiton">Maison</span>
            <p className="mt-3 text-sm text-creme/80">
              일상을 보조하는 표면. 포트의 외피, 블렌더의 마감, 토스터의 정면.
            </p>
          </div>
        </div>
      </section>
    </article>
  );
}
