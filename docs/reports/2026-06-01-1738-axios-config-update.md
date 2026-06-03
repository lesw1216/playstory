# axios·Tailwind 설치 반영 — frontend .claude 갱신

> 2026-06-01 17:38

프론트엔드에 Tailwind·axios가 실제 설치됨에 따라 .claude 문서를 점검하고, axios 도입분만 반영했다. (Tailwind는 기존 문서와 이미 일치 → 무변경)

## 작업 요청 요약

- 프론트엔드에 `tailwindcss`·`axios` 라이브러리 추가됨.
- 변경이 필요한 `.claude` 문서를 분석하고 수정.

## 설치 상태 분석

| 라이브러리 | 실제 설치 상태 | 문서 반영 |
|-----------|----------------|-----------|
| `tailwindcss 4.3` + `@tailwindcss/vite 4.3` | Vite 플러그인 등록, `main.css` = `@import 'tailwindcss'`, config 파일 없음(CSS-first) | 기존 문서와 일치 → 변경 없음 |
| `axios 1.16` | 설치됨, 아직 사용처 없음 | 문서가 "native fetch" 전제 → 수정 |

## 변경 파일 목록

| 파일 경로 | 유형 | 요약 |
|-----------|------|------|
| `frontend/.claude/rules/api-and-state.md` | ~ | intro를 axios로, "fetch 래퍼" → "axios 클라이언트" 섹션 재작성(인스턴스 + apiGet/apiPost 헬퍼, params 옵션, blob 다운로드 노트) |
| `frontend/.claude/CLAUDE.md` | ~ | 아키텍처 통신 라인 native fetch → axios 인스턴스(`src/api/client.ts`) |
| `frontend/.claude/rules/coding-convention.md` | ~ | 디렉터리 주석 "fetch 래퍼" → "axios 클라이언트" (Tailwind 부분은 무변경) |
| `.claude/reports/2026-06-01-1738-axios-config-update.html` | + | 이 보고서 |

## 변경 없음 (분석상 무관)

- Tailwind 문서(`coding-convention.md` 스타일링 섹션, `CLAUDE.md` 스택) — 실제 v4 설치 구성과 이미 일치.
- `.claude/report-style.md`, 루트 `CLAUDE.md`, backend 트리 전체, 공통 skills/rules.

## 검증 결과

- `grep -rin "fetch|axios" frontend/.claude` → 잔존 "fetch"는 의도된 함수명 `fetchExcelJobs`(읽기 동사) 뿐, 그 외 모두 axios 기조로 정합.
- 코드 변경 없음(문서만) → 빌드/테스트 불필요. 커밋은 진행하지 않음.

## 알려진 제약 · follow-up

- 실제 `src/api/client.ts`·store·polling 구현은 설계 구현 단계의 별도 작업.
- axios 공통 처리(인증 헤더·에러 토스트 등)가 필요해지면 인스턴스 interceptor로 확장 — 추가 시 `api-and-state.md` 갱신.
- class 자동 정렬용 `prettier-plugin-tailwindcss` 는 아직 미설치(권장 사항 유지).
