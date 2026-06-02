import { onUnmounted } from 'vue'

export interface PollingControls {
  start: () => void
  stop: () => void
}

/**
 * 주어진 콜백을 일정 주기로 반복 실행하는 polling 제어를 제공한다.
 * 컴포넌트 언마운트 시 타이머를 자동 정리한다.
 *
 * @param callback 매 주기마다 실행할 함수
 * @param intervalMs 실행 주기 (밀리초)
 * @returns start / stop 제어 함수
 */
export function usePolling(callback: () => void, intervalMs: number): PollingControls {
  let timer: ReturnType<typeof setInterval> | null = null

  function start(): void {
    if (timer !== null) {
      return
    }
    timer = setInterval(callback, intervalMs)
  }

  function stop(): void {
    if (timer === null) {
      return
    }
    clearInterval(timer)
    timer = null
  }

  onUnmounted(stop)

  return { start, stop }
}
