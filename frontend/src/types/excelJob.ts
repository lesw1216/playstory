export type ExcelJobStatus = 'PENDING' | 'PROCESSING' | 'DONE' | 'FAILED'

export interface ExcelJob {
  jobId: number
  status: ExcelJobStatus
  requestedAt: string // ISO datetime
  startedAt: string | null
  endedAt: string | null
  filePath: string | null
}

export interface ExcelJobPage {
  content: ExcelJob[]
  page: number
  size: number
  totalElements: number
  totalPages: number
  last: boolean
}
