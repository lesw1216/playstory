const formatter = new Intl.DateTimeFormat('ko-KR', {
  year: 'numeric',
  month: '2-digit',
  day: '2-digit',
  hour: '2-digit',
  minute: '2-digit',
  second: '2-digit',
  hour12: false,
})

/**
 * ISO datetime 문자열을 화면 표기용으로 변환한다.
 * 값이 없거나(미시작·미완료) 파싱 불가하면 '-' 를 반환한다.
 */
export function formatDateTime(iso: string | null): string {
  if (!iso) {
    return '-'
  }

  const date = new Date(iso)
  if (Number.isNaN(date.getTime())) {
    return '-'
  }
  return formatter.format(date)
}
