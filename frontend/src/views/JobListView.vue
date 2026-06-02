<script setup lang="ts">
import { onMounted, watch } from 'vue'
import { POLLING_INTERVAL_MS, useExcelJobStore } from '@/stores/excelJob'
import { usePolling } from '@/composables/usePolling'
import ExcelJobTable from '@/components/ExcelJobTable.vue'
import JobPagination from '@/components/JobPagination.vue'

const store = useExcelJobStore()

const { start, stop } = usePolling(() => store.loadJobs(store.page), POLLING_INTERVAL_MS)

onMounted(() => store.loadJobs(0))

// 진행 중(PENDING/PROCESSING) job이 있을 때만 polling, 모두 완료되면 자동 중지
watch(
  () => store.hasOngoing,
  (ongoing) => (ongoing ? start() : stop()),
  { immediate: true },
)
</script>

<template>
  <div class="flex h-screen flex-col bg-white text-gray-900">
    <header class="flex shrink-0 items-center justify-between border-b border-gray-200 px-6 py-4">
      <div>
        <h1 class="text-lg font-semibold">데이터 요청 목록</h1>
        <p class="text-sm text-gray-500">엑셀 생성 job 목록 (최신순)</p>
      </div>
      <button
        type="button"
        class="rounded-md bg-emerald-600 px-4 py-2 text-sm font-semibold text-white hover:bg-emerald-700"
        @click="store.requestExcel"
      >
        엑셀 생성 요청
      </button>
    </header>

    <main class="flex-1 overflow-auto">
      <!-- 데이터가 있으면 polling 갱신 중에도 테이블을 유지(로딩 문구로 교체되어 깜빡이지 않게) -->
      <ExcelJobTable v-if="store.jobs.length > 0" :jobs="store.jobs" />
      <p v-else-if="store.loading" class="px-6 py-10 text-center text-sm text-gray-500">
        불러오는 중…
      </p>
      <p v-else-if="store.error" class="px-6 py-10 text-center text-sm text-red-600">
        {{ store.error }}
      </p>
      <p v-else class="px-6 py-10 text-center text-sm text-gray-500">요청한 작업이 없습니다.</p>
    </main>

    <footer class="shrink-0 border-t border-gray-200 px-6 py-3">
      <JobPagination
        :current-page="store.page"
        :total-pages="store.totalPages"
        @change="store.loadJobs"
      />
    </footer>
  </div>
</template>
