export interface BaseResponse<T> {
  isSuccess: boolean
  code: number
  message: string
  result: T
}
