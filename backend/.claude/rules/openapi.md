# OpenAPI 명세 규칙

엔드포인트 추가·변경 시 `docs/api/openapi.yaml` 을 업데이트한다.
OpenAPI 3.0 스펙을 따른다.

---

## 파일 위치

```
docs/api/openapi.yaml   ← 전체 API 명세를 하나의 파일로 관리 (저장소 루트의 docs/)
```

---

## 기본 구조

```yaml
openapi: 3.0.3
info:
  title: PlayStory API
  version: 1.0.0
servers:
  - url: http://localhost:8080
paths:
  ...
components:
  schemas:
    ...
```

---

## paths 작성 규칙

```yaml
paths:
  /api/excel-jobs:
    get:
      summary: 엑셀 생성 job 목록 조회 (최신순)
      tags:
        - ExcelJob
      parameters:
        - name: page
          in: query
          schema:
            type: integer
            default: 0
        - name: size
          in: query
          schema:
            type: integer
            default: 10
      responses:
        '200':
          description: 조회 성공
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ExcelJobListResponse'
    post:
      summary: 엑셀 생성 job 요청
      tags:
        - ExcelJob
      responses:
        '200':
          description: 생성 성공 (job_id 즉시 반환)
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ExcelJobResponse'

  /api/excel-jobs/{jobId}/download:
    get:
      summary: 완료된 job의 엑셀 파일 다운로드
      tags:
        - ExcelJob
      parameters:
        - name: jobId
          in: path
          required: true
          schema:
            type: integer
            format: int64
      responses:
        '200':
          description: 파일 스트림
        '400':
          description: 아직 완료되지 않은 job
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ErrorResponse'
        '404':
          description: 존재하지 않는 job
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ErrorResponse'
```

- `tags` 는 도메인 단위로 묶는다 (ExcelJob, Order)
- 경로 파라미터는 `{jobId}` 형식으로 표기

---

## components/schemas 작성 규칙

스키마 이름은 DTO 클래스명과 동일하게 사용한다.

```yaml
components:
  schemas:
    ExcelJobResponse:
      type: object
      properties:
        isSuccess:
          type: boolean
        code:
          type: integer
        message:
          type: string
        result:
          $ref: '#/components/schemas/ExcelJobResult'

    ExcelJobResult:
      type: object
      properties:
        jobId:
          type: integer
          format: int64
        status:
          type: string
          enum: [PENDING, PROCESSING, DONE, FAILED]
        requestedAt:
          type: string
          format: date-time
        startedAt:
          type: string
          format: date-time
        endedAt:
          type: string
          format: date-time
        filePath:
          type: string

    ErrorResponse:
      type: object
      properties:
        isSuccess:
          type: boolean
          example: false
        code:
          type: integer
        message:
          type: string
```

- 모든 응답은 `BaseResponse<T>` 래퍼를 반영한다 (`isSuccess`, `code`, `message`, `result`)
- Enum 값은 실제 열거형 값과 동일하게 작성한다 (`ExcelJobStatus`)
- `ErrorResponse` 는 공통 스키마로 한 번만 정의하고 `$ref` 로 재사용한다

---

## 공통 규칙

| 항목 | 규칙 |
|------|------|
| 포맷 | YAML |
| 버전 | OpenAPI 3.0.3 |
| 파일 | `docs/api/openapi.yaml` 단일 파일 |
| 태그 | 도메인 단위 (ExcelJob, Order) |
| 스키마명 | DTO 클래스명과 동일 |
| 응답 구조 | 항상 `BaseResponse<T>` 래퍼 반영 |
| 에러 응답 | 400, 404 등 예상 가능한 오류는 반드시 명시 |
