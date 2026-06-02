<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{
  currentPage: number // 0-based
  totalPages: number
}>()

const emit = defineEmits<{ change: [page: number] }>()

const WINDOW = 5

const isFirst = computed(() => props.currentPage <= 0)
const isLast = computed(() => props.totalPages === 0 || props.currentPage >= props.totalPages - 1)

/** 현재 페이지 주변으로 최대 WINDOW 개의 0-based 페이지 번호를 만든다. */
const pages = computed<number[]>(() => {
  if (props.totalPages <= 0) {
    return []
  }

  const half = Math.floor(WINDOW / 2)
  let start = props.currentPage - half
  let end = start + WINDOW - 1

  if (start < 0) {
    start = 0
    end = Math.min(WINDOW - 1, props.totalPages - 1)
  }
  if (end > props.totalPages - 1) {
    end = props.totalPages - 1
    start = Math.max(0, end - WINDOW + 1)
  }

  const result: number[] = []
  for (let p = start; p <= end; p++) {
    result.push(p)
  }
  return result
})

function go(page: number): void {
  if (page < 0 || page > props.totalPages - 1 || page === props.currentPage) {
    return
  }
  emit('change', page)
}
</script>

<template>
  <nav class="flex items-center justify-center gap-1" aria-label="페이지 이동">
    <button
      type="button"
      class="rounded px-3 py-1.5 text-sm text-gray-600 hover:bg-gray-100 disabled:cursor-not-allowed disabled:opacity-40"
      :disabled="isFirst"
      @click="go(currentPage - 1)"
    >
      ‹
    </button>

    <button
      v-for="p in pages"
      :key="p"
      type="button"
      class="min-w-9 rounded px-3 py-1.5 text-sm tabular-nums"
      :class="
        p === currentPage
          ? 'bg-gray-900 font-semibold text-white'
          : 'text-gray-600 hover:bg-gray-100'
      "
      @click="go(p)"
    >
      {{ p + 1 }}
    </button>

    <button
      type="button"
      class="rounded px-3 py-1.5 text-sm text-gray-600 hover:bg-gray-100 disabled:cursor-not-allowed disabled:opacity-40"
      :disabled="isLast"
      @click="go(currentPage + 1)"
    >
      ›
    </button>
  </nav>
</template>
