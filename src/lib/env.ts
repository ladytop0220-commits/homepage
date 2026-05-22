/**
 * 환경 변수 접근 헬퍼.
 * 누락된 경우 명시적으로 에러를 던져 런타임에서 빨리 발견되도록 한다.
 */

function required(name: string, value: string | undefined): string {
  if (!value) {
    throw new Error(
      `Missing required environment variable: ${name}\n` +
        `Add it to .env.local (see .env.local.example).`
    );
  }
  return value;
}

function optional(value: string | undefined): string | undefined {
  return value && value.length > 0 ? value : undefined;
}

export const env = {
  site: {
    url: process.env.NEXT_PUBLIC_SITE_URL ?? "http://localhost:3000",
  },
  supabase: {
    url: optional(process.env.NEXT_PUBLIC_SUPABASE_URL),
    anonKey: optional(process.env.NEXT_PUBLIC_SUPABASE_ANON_KEY),
    serviceRoleKey: optional(process.env.SUPABASE_SERVICE_ROLE_KEY),
  },
  anthropic: {
    apiKey: optional(process.env.ANTHROPIC_API_KEY),
  },
  toss: {
    clientKey: optional(process.env.NEXT_PUBLIC_TOSS_CLIENT_KEY),
    secretKey: optional(process.env.TOSS_SECRET_KEY),
  },
  email: {
    resendApiKey: optional(process.env.RESEND_API_KEY),
    from: optional(process.env.EMAIL_FROM),
  },
  ntsbiz: {
    apiKey: optional(process.env.NTSBIZ_API_KEY),
  },
};

/**
 * 특정 통합이 활성 상태인지 확인하는 헬퍼.
 * UI에서 "기능 비활성 — 환경변수 누락" 안내에 사용.
 */
export const isFeatureEnabled = {
  supabase: () => Boolean(env.supabase.url && env.supabase.anonKey),
  anthropic: () => Boolean(env.anthropic.apiKey),
  toss: () => Boolean(env.toss.clientKey && env.toss.secretKey),
  email: () => Boolean(env.email.resendApiKey),
  ntsbiz: () => Boolean(env.ntsbiz.apiKey),
};

export { required };
