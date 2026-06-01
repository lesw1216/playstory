# 코딩 컨벤션 (Frontend)

## 컴포넌트

- 모든 컴포넌트는 SFC `<script setup lang="ts">` + Composition API로 작성한다. Options API 금지.
- 단일 파일 컴포넌트 순서: `<script setup>` → `<template>` → (필요 시) `<style scoped>`.
- 스타일은 **Tailwind utility class 우선**(`@rules/coding-convention.md` 의 스타일링 섹션 참조). `<style scoped>` 는 utility로 표현하기 어려운 경우(복잡한 keyframe 애니메이션 등)에 한해 예외적으로 사용한다.

```vue
<script setup lang="ts">
import { ref, computed } from 'vue'

const count = ref(0)
const doubled = computed(() => count.value * 2)
</script>

<template>
  <button class="rounded px-3 py-1 font-semibold text-white bg-emerald-600 hover:bg-emerald-700"
          @click="count++">
    {{ doubled }}
  </button>
</template>
```

---

## 스타일링 — Tailwind CSS v4

이 프로젝트의 CSS 프레임워크는 **Tailwind CSS v4** 다. utility-first로 작성한다.

### 설정 (v4 / CSS-first)

- Vite 플러그인 `@tailwindcss/vite` 를 사용한다 (`vite.config.ts` 의 `plugins` 에 등록).
- 진입점은 `src/assets/main.css` — 최상단에 `@import "tailwindcss";` 한 줄로 활성화한다.
- 테마 커스터마이즈(색·폰트·간격)는 CSS의 `@theme` 블록에서 정의한다. **`tailwind.config.js` 는 v4에서 사용하지 않는다.**

```css
/* src/assets/main.css */
@import "tailwindcss";

@theme {
  --color-brand: hsl(160 100% 37%);
}
```

### 작성 원칙

- 마크업에 utility class를 직접 쓴다. 색·여백·타이포는 테마 토큰(`text-`, `p-`, `gap-`)으로 표현한다.
- 임의값(`w-[137px]`, `text-[13px]`)은 지양한다. 디자인 토큰으로 표현되지 않으면 `@theme` 에 토큰을 먼저 추가한다.
- 반응형은 `sm:`/`md:`/`lg:` prefix, 다크모드는 `dark:` prefix로 표기한다.
- 반복되는 class 묶음은 `@apply` 로 숨기지 말고 **컴포넌트로 추출**한다 (`JobStatusBadge.vue` 등). `@apply` 는 외부 라이브러리 마크업을 감쌀 때 등 제한적으로만 쓴다.
- class 나열 순서는 Tailwind 공식 권장 순서(레이아웃 → 박스 → 타이포 → 시각효과 → 상태)를 따른다. 자동 정렬이 필요하면 `prettier-plugin-tailwindcss` 도입을 권장하되, 라이브러리 추가는 별도 협의한다.

### 예시 — 상태 배지 컴포넌트

```vue
<script setup lang="ts">
import type { ExcelJobStatus } from '@/types/excelJob'

const props = defineProps<{ status: ExcelJobStatus }>()

const COLOR: Record<ExcelJobStatus, string> = {
  PENDING: 'bg-gray-100 text-gray-700',
  PROCESSING: 'bg-blue-100 text-blue-700',
  DONE: 'bg-emerald-100 text-emerald-700',
  FAILED: 'bg-red-100 text-red-700',
}
</script>

<template>
  <span class="rounded px-2 py-0.5 text-xs font-medium" :class="COLOR[props.status]">
    {{ props.status }}
  </span>
</template>
```

---

## 네이밍

| 대상 | 규칙 | 예 |
|------|------|----|
| 컴포넌트 파일·이름 | PascalCase | `ExcelJobTable.vue`, `JobStatusBadge.vue` |
| View 컴포넌트 | `*View.vue` | `JobListView.vue` |
| Composable | `use*` camelCase | `usePolling.ts` |
| Pinia store 파일 | camelCase, `use*Store` export | `excelJob.ts` → `useExcelJobStore` |
| 타입·인터페이스 | PascalCase | `ExcelJob`, `BaseResponse<T>` |
| 상수 | UPPER_SNAKE_CASE | `API_BASE_URL`, `POLLING_INTERVAL_MS` |
| 변수·함수 | camelCase, 의도 드러내기 | `excelJobs`, `fetchExcelJobs()` |

- `data`, `result`, `temp`, `handler` 같은 무내용 이름 지양.
- 템플릿 내 이벤트 핸들러는 `onXxx` 보다 동작을 드러내는 이름(`requestExcel`, `downloadJob`)을 권장.

---

## TypeScript

- `any` 금지. 불가피하면 `unknown` 후 좁히기.
- API 응답·도메인 타입은 `src/types/` 에 정의해 재사용한다 (`@rules/api-and-state.md` 참조).
- 함수 반환 타입은 공개 함수(composable·store action)에 명시한다.

---

## 디렉터리

```
src/
├── api/         ← axios 클라이언트·엔드포인트 함수
├── stores/      ← Pinia store
├── types/       ← 공유 타입 (BaseResponse, ExcelJob 등)
├── components/  ← 재사용 컴포넌트
├── views/       ← 라우트 단위 화면
├── router/      ← Vue Router 설정
└── assets/
    └── main.css ← Tailwind 진입점 (@import "tailwindcss" + @theme)
```

- `@` alias로 import 한다 (`import { useExcelJobStore } from '@/stores/excelJob'`). 상대경로 `../../` 남용 금지.

---

## 포맷팅

- prettier / eslint(oxlint + eslint) 설정이 최종 권한이다. 포맷 관련 스타일을 임의로 다투지 않는다.
- 커밋 전 `npm run lint` · `npm run type-check` 통과를 기본으로 한다.
