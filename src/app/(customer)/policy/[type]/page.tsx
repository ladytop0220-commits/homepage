import { notFound } from "next/navigation";
import ReactMarkdown from "react-markdown";
import remarkGfm from "remark-gfm";
import { loadPolicyMarkdown, getPolicyTitle, type PolicyKey } from "@/lib/policy";

const VALID_KEYS: PolicyKey[] = ["terms", "privacy", "return"];

interface PageProps {
  params: Promise<{ type: string }>;
}

export async function generateStaticParams() {
  return VALID_KEYS.map((type) => ({ type }));
}

export async function generateMetadata({ params }: PageProps) {
  const { type } = await params;
  if (!VALID_KEYS.includes(type as PolicyKey)) return { title: "Policy" };
  return { title: getPolicyTitle(type as PolicyKey) };
}

export default async function PolicyPage({ params }: PageProps) {
  const { type } = await params;
  if (!VALID_KEYS.includes(type as PolicyKey)) notFound();

  const markdown = await loadPolicyMarkdown(type as PolicyKey);

  return (
    <article className="mx-auto max-w-3xl px-4 py-16 sm:px-8 sm:py-24">
      <div
        className="prose prose-neutral max-w-none
          prose-headings:font-display prose-headings:text-ardoise
          prose-h1:text-4xl prose-h1:mb-8
          prose-h2:text-2xl prose-h2:mt-12 prose-h2:mb-4
          prose-h3:text-lg prose-h3:mt-8
          prose-p:text-ardoise/80 prose-p:leading-relaxed
          prose-li:text-ardoise/80
          prose-strong:text-ardoise
          prose-table:text-sm
          prose-th:bg-creme prose-th:text-ardoise
          prose-blockquote:border-laiton prose-blockquote:text-ardoise/70
          prose-hr:border-ardoise/10"
      >
        <ReactMarkdown remarkPlugins={[remarkGfm]}>{markdown}</ReactMarkdown>
      </div>
    </article>
  );
}
