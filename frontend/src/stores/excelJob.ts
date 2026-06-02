import { defineStore } from 'pinia'
import { computed, ref } from 'vue'
import { createExcelJob, fetchExcelJobs } from '@/api/excelJob'
import type { ExcelJob } from '@/types/excelJob'

export const PAGE_SIZE = 10
export const POLLING_INTERVAL_MS = 2000

export const useExcelJobStore = defineStore('excelJob', () => {
  const jobs = ref<ExcelJob[]>([])
  const page = ref(0)
  const totalPages = ref(0)
  const totalElements = ref(0)
  const last = ref(true)
  const loading = ref(false)
  const error = ref<string | null>(null)

  const hasOngoing = computed(() =>
    jobs.value.some((job) => job.status === 'PENDING' || job.status === 'PROCESSING'),
  )

  async function loadJobs(targetPage = 0): Promise<void> {
    loading.value = true
    error.value = null

    try {
      const result = await fetchExcelJobs(targetPage, PAGE_SIZE)

      jobs.value = result.content
      page.value = result.page
      totalPages.value = result.totalPages
      totalElements.value = result.totalElements
      last.value = result.last
    } catch (e) {
      error.value = e instanceof Error ? e.message : '목록을 불러오지 못했습니다.'
    } finally {
      loading.value = false
    }
  }

  async function requestExcel(): Promise<void> {
    error.value = null

    try {
      await createExcelJob()
      await loadJobs(0)
    } catch (e) {
      error.value = e instanceof Error ? e.message : '엑셀 생성 요청에 실패했습니다.'
    }
  }

  return {
    jobs,
    page,
    totalPages,
    totalElements,
    last,
    loading,
    error,
    hasOngoing,
    loadJobs,
    requestExcel,
  }
})
