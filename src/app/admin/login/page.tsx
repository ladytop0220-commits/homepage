export const metadata = { title: "어드민 로그인" };

export default function AdminLoginPage() {
  return (
    <div className="mx-auto flex min-h-[60vh] max-w-sm flex-col justify-center">
      <h1 className="font-display text-3xl text-ardoise">Admin Login</h1>
      <p className="mt-2 text-sm text-ardoise/60">
        Émelle 운영자 계정으로 로그인하세요.
      </p>
      <form className="mt-8 space-y-5">
        <label className="block">
          <span className="label-caps text-ardoise/60">이메일</span>
          <input
            type="email"
            className="mt-2 block w-full border-b border-ardoise/30 bg-transparent py-2 outline-none focus:border-ardoise"
            placeholder="admin@emelle.co.kr"
            disabled
          />
        </label>
        <label className="block">
          <span className="label-caps text-ardoise/60">비밀번호</span>
          <input
            type="password"
            className="mt-2 block w-full border-b border-ardoise/30 bg-transparent py-2 outline-none focus:border-ardoise"
            disabled
          />
        </label>
        <button
          type="button"
          disabled
          className="h-12 w-full bg-ardoise text-sm text-creme disabled:opacity-50"
        >
          로그인 (인증 연동 후 활성화)
        </button>
      </form>
    </div>
  );
}
