# API 호출 · 상태 관리 규칙

백엔드 통신과 job 상태 관리 방식을 정의한다. HTTP 클라이언트로 **axios** 를 사용한다.

---

## 응답 래퍼 타입

백엔드의 모든 응답은 `BaseResponse<T>` 래퍼를 따른다. 프론트 타입도 이를 그대로 반영한다.

```ts
// src/types/api.ts
export interface BaseResponse<T> {
  isSuccess: boolean
  code: number
  message: string
  result: T
}
```

```ts
// src/types/excelJob.ts
export type ExcelJobStatus = 'PENDING' | 'PROCESSING' | 'DONE' | 'FAILED'

export interface ExcelJob {
  jobId: number
  status: ExcelJobStatus
  requestedAt: string   // ISO datetime
  startedAt: string | null
  endedAt: string | null
  filePath: string | null
}
```

- 타입은 백엔드 DTO(`ExcelJobResponse` 등)와 필드명을 일치시킨다.
- enum 값(`ExcelJobStatus`)은 백엔드 enum과 문자열까지 동일하게 맞춘다.

---

## axios 클라이언트

`src/api/client.ts` 에 axios 인스턴스와 얇은 헬퍼를 둔다. 헬퍼는 `isSuccess` 를 검사해 `result` 만 반환한다.

```ts
// src/api/client.ts
import axios from 'axios'
import type { BaseResponse } from '@/types/api'

const client = axios.create({ baseURL: 'http://localhost:8080' })

export async function apiGet<T>(path: string, params?: object): Promise<T> {
  const { data } = await client.get<BaseResponse<T>>(path, { params })

  if (!data.isSuccess) {
    throw new Error(data.message)
  }
  return data.result
}

export async function apiPost<T>(path: string, body?: object): Promise<T> {
  const { data } = await client.post<BaseResponse<T>>(path, body)

  if (!data.isSuccess) {
    throw new Error(data.message)
  }
  return data.result
}
```

```ts
// src/api/excelJob.ts
export function fetchExcelJobs(page = 0, size = 10): Promise<ExcelJobPage> {
  return apiGet<ExcelJobPage>('/api/excel-jobs', { page, size })
}

export function createExcelJob(): Promise<ExcelJob> {
  return apiPost<ExcelJob>('/api/excel-jobs')
}
```

- 쿼리 파라미터는 직접 문자열로 잇지 말고 axios `params` 옵션으로 전달한다.
- 엔드포인트별 함수는 `fetch*` / `create*` 동사로 명명한다.
- 다운로드(`GET /api/excel-jobs/{jobId}/download`)는 JSON이 아닌 파일 스트림이므로 별도 처리한다 — `responseType: 'blob'` 으로 받은 뒤 다운로드하거나, 단순 다운로드는 `window.open` 으로 처리한다. (이 경우는 `BaseResponse` 래퍼를 거치지 않으므로 위 헬퍼를 쓰지 않는다.)

---

## Pinia store (job 상태)

job 목록·로딩·polling 제어는 Pinia store에서 관리한다.

```ts
// src/stores/excelJob.ts
export const useExcelJobStore = defineStore('excelJob', () => {
  const jobs = ref<ExcelJob[]>([])

  const hasOngoing = computed(() =>
    jobs.value.some((j) => j.status === 'PENDING' || j.status === 'PROCESSING'),
  )

  async function loadJobs(): Promise<void> {
    jobs.value = (await fetchExcelJobs()).content
  }

  async function requestExcel(): Promise<void> {
    await createExcelJob()
    await loadJobs()
  }

  return { jobs, hasOngoing, loadJobs, requestExcel }
})
```

- 컴포넌트는 API 함수를 직접 호출하지 않고 store action을 통해 호출한다.
- 서버 상태(job)는 store에, 화면 전용 UI 상태(모달 열림 등)는 컴포넌트 로컬에 둔다.

---

## Polling 규칙

설계상 상태 확인은 polling 으로 한다.

- 화면 진입 시 **1회 조회**가 기본이다.
- 조회 결과에 `PENDING` 또는 `PROCESSING` job이 하나라도 있으면(`hasOngoing`) 일정 주기로 `loadJobs()` 를 반복한다.
- 모든 job이 `DONE`/`FAILED` 이면 polling을 **중지**한다.
- polling 주기는 상수(`POLLING_INTERVAL_MS`)로 관리한다.
- 컴포넌트 언마운트 시 타이머를 반드시 정리한다 (`onUnmounted` 에서 `clearInterval`). 재사용 로직은 `usePolling` composable로 추출 가능.

```ts
// 개념 예시
watch(
  () => store.hasOngoing,
  (ongoing) => (ongoing ? start() : stop()),
  { immediate: true },
)
```
