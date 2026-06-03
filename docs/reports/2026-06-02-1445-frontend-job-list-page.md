# 프론트엔드 — 데이터 요청 목록 페이지

> 2026-06-02 14:45

엑셀 job 목록을 최신순 페이징으로 보여주는 화면. 페이지네이션을 뷰포트 하단에 고정하고, 엑셀 생성 요청 버튼을 헤더에 배치했다.

## 작업 요청 요약

- `GET /api/excel-jobs` 기반 데이터 요청 목록 페이지 구현.
- **페이지네이션 하단 고정**: 행 수와 무관하게 항상 뷰포트 맨 아래 — 1개여도 바로 아래 붙지 않도록.
- 범위: 목록 + 하단 고정 페이지네이션 + 엑셀 생성 요청 버튼(레이아웃 확인용). polling·상태배지·다운로드 제외.
- 커밋하지 않음. 화면 육안 확인은 사용자가 직접 진행.

## 생성 · 수정 파일

| 파일 | 유형 | 내용 |
|------|------|------|
| `types/api.ts · types/excelJob.ts` | + | `BaseResponse<T>`, `ExcelJob`/`ExcelJobPage`/`ExcelJobStatus` — 백엔드 DTO와 필드·enum 일치 |
| `api/client.ts · api/excelJob.ts` | + | axios 인스턴스 + `apiGet`/`apiPost`(isSuccess 검사), `fetchExcelJobs`/`createExcelJob` |
| `utils/datetime.ts` | + | `formatDateTime` — null·미시작 시 `-` |
| `stores/excelJob.ts` | + | Pinia store — jobs·페이징 메타·loading·error, `loadJobs(page)`/`requestExcel()` |
| `components/ExcelJobTable.vue` | + | 테이블: job_id/요청일시/시작/완료/상태/파일경로. status plain text, null은 `-` |
| `components/JobPagination.vue` | + | 0-based 입력 → 1-based 표기. 윈도우 5개 + prev/next, 양끝 disabled |
| `views/JobListView.vue` | + | `h-screen flex-col` 레이아웃 + 헤더(생성 버튼) + 로딩/에러/빈 상태 |
| `router/index.ts` | ~ | `/` → `JobListView`(name `job-list`) |
| `HomeView.vue · stores/counter.ts · ExampleComponent.vue` | − | Vite 기본 스캐폴드 제거(미사용) |

## 하단 고정 레이아웃

- 루트 `div.flex.h-screen.flex-col` → 헤더(`shrink-0`) / 본문(`flex-1 overflow-auto`) / 푸터(`shrink-0 border-t`).
- 본문이 `flex-1` 로 남는 공간을 흡수 → 행이 1개든 10개든 페이지네이션은 항상 뷰포트 맨 아래.
- 로딩·에러·빈 목록 상태에서도 푸터 위치 동일. `totalPages` 0이면 페이지 버튼 비활성.

## 검증 결과

- `npm run type-check` (vue-tsc) → **통과**.
- `npm run lint` (oxlint + eslint) → **0 errors**.
- 화면 육안 확인은 사용자가 직접 진행(요청에 따라 내 검증 범위 제외).

## 알려진 제약 · follow-up

- **엑셀 생성 요청 버튼**: UI·배선만 우선 배치. 백엔드 `POST /api/excel-jobs` 미구현이라 클릭 시 에러 상태 표시 — 엔드포인트 완성 후 정상화.
- **다운로드 버튼**: 보류(백엔드 `download` 구현 후).
- **polling·상태 배지**: 범위 밖 — 후속.
- 커밋하지 않음.
