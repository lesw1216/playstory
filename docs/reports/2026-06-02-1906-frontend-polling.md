# 프론트엔드 — job 목록 polling 구현

> 2026-06-02 19:06

진행 중(PENDING/PROCESSING) job이 있을 때만 주기 조회하고, 모두 DONE/FAILED면 자동 중지하는 polling을 추가했다.

## 작업 요청 요약

- 엑셀 생성 비동기 상태 전이를 화면에 반영하기 위한 polling(설계 §4, api-and-state.md).
- 현재 브랜치 `feat/excel-generation` 의 미커밋 작업에 이어서 진행 — 커밋하지 않음.
- 추후 커밋은 (1)백엔드 생성 (2)docs (3)프론트 polling 으로 분리 예정.

## 변경 파일

| 파일 | 유형 | 내용 |
|------|------|------|
| `stores/excelJob.ts` | ~ | `hasOngoing` computed + `POLLING_INTERVAL_MS=2000` 상수 |
| `composables/usePolling.ts` | + | 범용 polling(start/stop, 중복 가드, onUnmounted 정리) |
| `views/JobListView.vue` | ~ | `watch(hasOngoing)`→start/stop, flicker 방지 조건 재배치 |

## 동작

```
mount → loadJobs(0) → hasOngoing?
  ├─ true  → usePolling start: 2s마다 loadJobs(현재 page)
  │            매 응답마다 hasOngoing 재계산 → 모두 DONE/FAILED면 watch가 stop
  └─ false → polling 미시작
"엑셀 생성 요청" → loadJobs(0) → 새 PENDING → hasOngoing true → 시작
언마운트 → onUnmounted(stop)
```

- 폴링은 현재 페이지(`store.page`) 재조회 — 보고 있던 페이지 유지.
- flicker 방지: 데이터가 있으면 갱신 중에도 테이블 유지(로딩 문구는 최초 빈 목록일 때만).

## 검증

- `npm run type-check` → 통과.
- `npm run lint` → 0 errors.
- 화면 육안 확인은 사용자: 생성 요청 후 PROCESSING→DONE 자동 갱신, 완료 시 Network 호출 멈춤.

## 알려진 제약 · follow-up

- 다운로드 버튼·상태 배지는 별도 작업. 이번은 polling만.
- polling 동작 검증에는 백엔드 POST(엑셀 생성) 기동 필요.
- 커밋하지 않음.
