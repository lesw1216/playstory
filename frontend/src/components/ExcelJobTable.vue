<script setup lang="ts">
import type { ExcelJob } from '@/types/excelJob'
import { formatDateTime } from '@/utils/datetime'

defineProps<{ jobs: ExcelJob[] }>()
</script>

<template>
  <table class="w-full border-collapse text-sm">
    <thead class="sticky top-0 bg-gray-50 text-left text-gray-600">
      <tr>
        <th class="border-b border-gray-200 px-4 py-2.5 font-semibold">job_id</th>
        <th class="border-b border-gray-200 px-4 py-2.5 font-semibold">요청일시</th>
        <th class="border-b border-gray-200 px-4 py-2.5 font-semibold">시작시간</th>
        <th class="border-b border-gray-200 px-4 py-2.5 font-semibold">완료시간</th>
        <th class="border-b border-gray-200 px-4 py-2.5 font-semibold">상태</th>
        <th class="border-b border-gray-200 px-4 py-2.5 font-semibold">파일경로</th>
      </tr>
    </thead>
    <tbody>
      <tr v-for="job in jobs" :key="job.jobId" class="hover:bg-gray-50">
        <td class="border-b border-gray-100 px-4 py-2.5 tabular-nums">{{ job.jobId }}</td>
        <td class="border-b border-gray-100 px-4 py-2.5">{{ formatDateTime(job.requestedAt) }}</td>
        <td class="border-b border-gray-100 px-4 py-2.5">{{ formatDateTime(job.startedAt) }}</td>
        <td class="border-b border-gray-100 px-4 py-2.5">{{ formatDateTime(job.endedAt) }}</td>
        <td class="border-b border-gray-100 px-4 py-2.5">{{ job.status }}</td>
        <td class="max-w-xs truncate border-b border-gray-100 px-4 py-2.5 text-gray-500">
          {{ job.filePath ?? '-' }}
        </td>
      </tr>
    </tbody>
  </table>
</template>
