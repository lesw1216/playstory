# OpenAPI 명세 신규 작성 — GET /api/excel-jobs

> 2026-06-02 11:10

현재 구현된 엔드포인트를 `openapi.md` 규칙에 맞춰 `docs/api/openapi.yaml` 로 신규 작성했다.

## 작업 요청 요약

- 현재 엔드포인트·규칙을 확인하고 `docs/` 아래 OpenAPI 명세서 작성.
- **스코프**: 실제 구현된 `GET /api/excel-jobs` 1개만. 설계.md §5 미구현 3개(POST·상세·download)는 제외.
- 커밋하지 않음(사용자 검토 후 별도).

## 생성 파일

| 파일 | 유형 | 내용 |
|------|------|------|
| `docs/api/openapi.yaml` | + | OpenAPI 3.0.3. `GET /api/excel-jobs` path + BaseResponse/ExcelJobResponse/ExcelJobPageResponse/ErrorResponse 스키마 |

## 명세 내용

- **path**: `GET /api/excel-jobs` — query `page`(default 0), `size`(default 10), tag `ExcelJob`.
- **200**: `allOf [BaseResponse, { result: ExcelJobPageResponse }]` 로 `BaseResponse<T>` 래퍼 표현(3.0 제네릭 미지원 대응).
- **500**: `ErrorResponse`(`$ref` 재사용).
- `status` enum: PENDING/PROCESSING/DONE/FAILED. `jobId`=int32, `totalElements`=int64.
- nullable 필드(startedAt/endedAt/filePath) `nullable: true` 표기. 스키마명=DTO 클래스명.

## 검증 결과

- YAML 파싱: **OK** — `python3 yaml.safe_load` 로 paths/schemas 정상 로드 확인.
- 코드 변경 없음(문서 단독), 빌드 영향 없음.

## 알려진 제약 · follow-up

- 미구현 3개 엔드포인트(POST 생성 · GET `{jobId}` 상세 · download)는 구현 시 추가.
- 별개 이슈: `schema.sql` 의 `create index idx_requested_at` 재기동 중복(Duplicate key name) — 미해결, 본 작업 범위 밖.
- 커밋하지 않음 — 사용자 검토 후 별도 진행.
