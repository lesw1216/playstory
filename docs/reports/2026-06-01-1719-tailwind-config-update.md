# Tailwind 도입에 따른 .claude 문서 + 보고서 스타일 갱신

> 2026-06-01 17:19

프론트엔드 Tailwind CSS v4 도입에 맞춰 frontend .claude 규칙을 utility-first로 갱신하고, 작업 완료 보고서 양식을 Tailwind 기반으로 재작성했다. (이 보고서가 새 양식의 첫 적용 예시)

## 작업 요청 요약

- 프론트엔드 CSS 프레임워크로 `Tailwind CSS` 도입 예정.
- 도입으로 인해 수정이 필요한 `.claude` 관련 파일 분석 및 갱신.
- 작업 완료 보고서를 **Tailwind를 사용한 HTML**로 작성할 수 있도록 양식 변경.

## 사전 계획 (실행 전)

- 영향 범위 파악: frontend는 Tailwind 미설치(스캐폴드 상태), `~/.claude/report-style.md`(global) 부재 → 보고서 스타일은 프로젝트 파일이 담당.
- 결정(사용자 확인): 범위는 **.claude 문서 + 보고서만**(실제 npm 설치 제외), **Tailwind v4**, 보고서는 **Play CDN**으로 렌더.
- 대상 파일: frontend `coding-convention.md` · `CLAUDE.md`, 루트 `report-style.md` · `CLAUDE.md`.

## 변경 파일 목록

| 파일 경로 | 유형 | 요약 |
|-----------|------|------|
| `frontend/.claude/rules/coding-convention.md` | ~ | 컴포넌트 스타일 utility-first 전환, "스타일링 — Tailwind CSS v4" 섹션 신설, 디렉터리에 main.css 진입점 명시 |
| `frontend/.claude/CLAUDE.md` | ~ | 스택 라인에 Tailwind CSS v4 추가, 아키텍처·규칙 항목에 스타일링 반영 |
| `.claude/report-style.md` | ~ | Tailwind Play CDN + 인라인 config 기반으로 전면 재작성(색 토큰·다크모드·카드·배지·코드블록을 utility class로) |
| `.claude/CLAUDE.md` | ~ | frontend 스택에 Tailwind 추가, 보고서 스타일이 Tailwind 기반임을 명시 |
| `.claude/reports/2026-06-01-1719-tailwind-config-update.html` | + | 이 보고서 (새 Tailwind 양식의 실사용 예시) |

## 분석 결과 — 변경 불필요 판정

- `frontend/.claude/rules/api-and-state.md` — API·상태 관리 전용, 스타일 무관 → 변경 없음.
- backend 트리 전체, 공통 skills(`pr`/`task`)·rules → Tailwind와 무관 → 변경 없음.
- frontend `done` 스킬 — type-check/lint/build 흐름은 Tailwind 도입과 무관 → 변경 없음.

## 검증 결과

- 코드 변경 없음(문서/스타일 규칙만) → 빌드·테스트 불필요.
- 이 보고서를 브라우저로 열어 Tailwind CDN 적용·다크모드(`prefers-color-scheme`)·레이아웃 렌더 확인 권장(인터넷 필요).
- 커밋은 진행하지 않음(사용자 지시 기조 유지).

## 알려진 제약 · follow-up

- 실제 Tailwind 설치는 미수행. 진행 시: `npm i -D tailwindcss @tailwindcss/vite` → `vite.config.ts` 플러그인 등록 → `src/assets/main.css` 에 `@import "tailwindcss";`. 라이브러리 추가 협의 절차 적용.
- 보고서는 Tailwind Play CDN을 사용 → **오프라인에서는 스타일이 적용되지 않음**. 완전 self-contained가 필요해지면 빌드 단계에서 정적 CSS를 인라인하는 방식으로 전환 검토.
- class 자동 정렬용 `prettier-plugin-tailwindcss` 도입은 별도 협의(라이브러리 추가).
- 기존 `src/assets/main.css`·`base.css` 의 Vue 스캐폴드 스타일은 Tailwind 설치 시 정리 대상.
