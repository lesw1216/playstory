import axios from 'axios'
import type { BaseResponse } from '@/types/api'

// baseURL은 빈값(same-origin). dev는 Vite 개발 서버 proxy, prod는 nginx 리버스 프록시가
// /api 요청을 백엔드로 중계한다 → 환경별 분기·CORS 불필요.
const client = axios.create({ baseURL: '' })

export async function apiGet<T>(path: string, params?: object): Promise<T> {
  const { data } = await client.get<BaseResponse<T>>(path, { params })

  if (!data.isSuccess) {
    throw new Error(data.message)
  }
  return data.result
}

export async function apiPost<T>(path: string, body?: object): Promise<T> {
  const { data } = await client.post<BaseResponse<T>>(path, body)

  if (!data.isSuccess) {
    throw new Error(data.message)
  }
  return data.result
}
