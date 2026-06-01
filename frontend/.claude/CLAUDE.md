# Frontend — CLAUDE.md

PlayStory 프론트엔드. 엑셀 생성 job을 요청하고, 목록·상태를 polling으로 표시하며, 완료된 job을 다운로드한다.

**스택:** Vue 3 (Composition API) · TypeScript · Vite · Pinia · Vue Router 5 · Tailwind CSS v4

---

## 주요 명령어

```bash
cd frontend

npm run dev          # 개발 서버 (Vite, HMR)
npm run build        # type-check + 프로덕션 빌드
npm run type-check   # vue-tsc 타입 검사만
npm run lint         # oxlint + eslint (--fix 포함)
npm run format       # prettier (src/ 대상)
```

테스트 프레임워크(vitest 등)는 현재 미설치다. 도입 시 별도 협의한다.

---

## 화면 흐름 (설계.md 기준)

데이터 요청 목록 화면 하나가 중심이다.

1. job 목록을 테이블로 표시 — 컬럼: `job_id`, `요청일시`, `시작시간`, `완료시간`, `상태`, `파일경로`
2. "엑셀 생성 요청" 버튼 → `POST /api/excel-jobs` → 즉시 목록 갱신
3. 목록에 `PENDING`/`PROCESSING` job이 하나라도 있으면 **polling 유지**, 모두 `DONE`/`FAILED` 면 **중지**
4. `DONE` job은 다운로드 버튼 → `GET /api/excel-jobs/{jobId}/download`

상태 확인은 SSE가 아닌 **polling** 으로 한다 (요구사항이 단순하여 구현 복잡도·리소스 고려).

---

## 아키텍처

- `@` alias → `src/` 디렉터리
- 상태 관리: **Pinia store** — job 목록/상태/polling 제어를 store에서 관리
- 라우팅: Vue Router 5
- 스타일링: **Tailwind CSS v4** (utility-first, 진입점 `src/assets/main.css`, CSS-first `@theme`)
- 백엔드 통신: **axios** 인스턴스 (`src/api/client.ts`, baseURL `http://localhost:8080`)
- 응답은 백엔드 `BaseResponse<T>` 래퍼(`isSuccess`/`code`/`message`/`result`) 형태

---

## 규칙

`.claude/rules/` 아래 규칙을 따른다.

- 컴포넌트·TS·Pinia·**Tailwind 스타일링** 규칙 — `@rules/coding-convention.md`
- API 호출·타입·job store·polling 규칙 — `@rules/api-and-state.md`

라이브러리 추가는 `package.json` 확인 후 반드시 협의한다. eslint/prettier 설정과 충돌하는 포맷은 만들지 않는다.
