export const metadata = { title: "대시보드" };

const STATS = [
  { label: "신규 주문", value: "0", note: "오늘" },
  { label: "입금 대기", value: "0", note: "무통장" },
  { label: "출고 대기", value: "0", note: "결제 완료 후" },
  { label: "오늘 매출", value: "₩ 0", note: "결제 완료 기준" },
];

const LOW_STOCK = [
  { name: "에멜 세라믹 팬 28cm", stock: 0, safety: 10 },
];

export default function AdminDashboardPage() {
  return (
    <div className="space-y-8">
      <header>
        <h1 className="font-display text-3xl text-ardoise">대시보드</h1>
        <p className="mt-1 text-sm text-ardoise/60">
          오늘의 운영 현황 한눈에 보기
        </p>
      </header>

      {/* Stats */}
      <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
        {STATS.map((s) => (
          <div
            key={s.label}
            className="border border-ardoise/10 bg-creme/30 p-5"
          >
            <div className="label-caps text-ardoise/50">{s.label}</div>
            <div className="mt-2 font-display text-3xl text-ardoise">
              {s.value}
            </div>
            <div className="mt-1 text-xs text-ardoise/40">{s.note}</div>
          </div>
        ))}
      </div>

      {/* Low stock alerts */}
      <section>
        <h2 className="font-display text-xl text-ardoise">⚠ 재고 부족 알림</h2>
        <div className="mt-3 border border-argile/30 bg-argile/5">
          {LOW_STOCK.length === 0 ? (
            <p className="p-6 text-sm text-ardoise/60">
              안전재고 미만 항목이 없습니다.
            </p>
          ) : (
            <ul className="divide-y divide-argile/20">
              {LOW_STOCK.map((item) => (
                <li
                  key={item.name}
                  className="flex items-center justify-between p-4 text-sm"
                >
                  <span className="text-ardoise">{item.name}</span>
                  <span className="text-argile">
                    {item.stock}개 (안전재고 {item.safety}개)
                  </span>
                </li>
              ))}
            </ul>
          )}
        </div>
      </section>

      <p className="text-xs text-ardoise/40">
        ※ 위 데이터는 MVP placeholder입니다. DB 연동 후 실제 수치로 교체됩니다.
      </p>
    </div>
  );
}
