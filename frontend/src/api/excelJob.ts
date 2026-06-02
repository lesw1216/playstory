import { apiGet, apiPost } from '@/api/client'
import type { ExcelJob, ExcelJobPage } from '@/types/excelJob'

export function fetchExcelJobs(page = 0, size = 10): Promise<ExcelJobPage> {
  return apiGet<ExcelJobPage>('/api/excel-jobs', { page, size })
}

export function createExcelJob(): Promise<ExcelJob> {
  return apiPost<ExcelJob>('/api/excel-jobs')
}
