<script setup lang="ts">
import { onMounted } from 'vue'
import { useExcelJobStore } from '@/stores/excelJob'
import ExcelJobTable from '@/components/ExcelJobTable.vue'
import JobPagination from '@/components/JobPagination.vue'

const store = useExcelJobStore()

onMounted(() => store.loadJobs(0))
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
      <p v-if="store.loading" class="px-6 py-10 text-center text-sm text-gray-500">
        불러오는 중…
      </p>
      <p v-else-if="store.error" class="px-6 py-10 text-center text-sm text-red-600">
        {{ store.error }}
      </p>
      <p v-else-if="store.jobs.length === 0" class="px-6 py-10 text-center text-sm text-gray-500">
        요청한 작업이 없습니다.
      </p>
      <ExcelJobTable v-else :jobs="store.jobs" />
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
