import { promises as fs } from "node:fs";
import path from "node:path";

const DOCS_DIR = path.join(process.cwd(), "docs");

const FILE_MAP = {
  terms: "terms-of-service.md",
  privacy: "privacy-policy.md",
  return: "return-exchange-policy.md",
} as const;

export type PolicyKey = keyof typeof FILE_MAP;

const TITLE_MAP: Record<PolicyKey, string> = {
  terms: "이용약관",
  privacy: "개인정보 처리방침",
  return: "반품·교환 정책",
};

export async function loadPolicyMarkdown(key: PolicyKey): Promise<string> {
  const filePath = path.join(DOCS_DIR, FILE_MAP[key]);
  return fs.readFile(filePath, "utf-8");
}

export function getPolicyTitle(key: PolicyKey): string {
  return TITLE_MAP[key];
}
