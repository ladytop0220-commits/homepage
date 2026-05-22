/**
 * Supabase 클라이언트 (브라우저 + 서버 양쪽).
 *
 * MVP Phase 1에서는 비활성 상태로 둠 (env 비어 있으면 throw).
 * Phase 2에서 @supabase/supabase-js 설치 후 활성화.
 *
 * 설치:
 *   npm install @supabase/supabase-js
 */

import { env, isFeatureEnabled } from "@/lib/env";

// 타입만 우선 정의 (실제 import는 패키지 설치 후)
type SupabaseClientLike = {
  from: (table: string) => unknown;
  auth: unknown;
};

let _client: SupabaseClientLike | null = null;

export function getSupabaseClient(): SupabaseClientLike {
  if (!isFeatureEnabled.supabase()) {
    throw new Error(
      "Supabase is not configured. Add NEXT_PUBLIC_SUPABASE_URL and " +
        "NEXT_PUBLIC_SUPABASE_ANON_KEY to .env.local."
    );
  }
  if (_client) return _client;

  // TODO Phase 2: 아래 주석을 해제
  // const { createClient } = require("@supabase/supabase-js");
  // _client = createClient(env.supabase.url!, env.supabase.anonKey!);
  // return _client!;

  throw new Error(
    "Supabase client not yet wired. Install @supabase/supabase-js " +
      "and enable the import in src/lib/supabase.ts."
  );
}

export const supabaseConfig = {
  url: env.supabase.url,
  anonKey: env.supabase.anonKey,
};
