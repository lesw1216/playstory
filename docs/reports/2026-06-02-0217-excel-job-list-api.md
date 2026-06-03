# 엑셀 job 최신순 목록 조회 — Controller·DTO 레이어

> 2026-06-02 02:17

`GET /api/excel-jobs?page&size` 엔드포인트의 Controller·DTO·주변 인프라를 작성했다. Service 본문(비즈니스 로직)은 사용자 작성 예정.

## 작업 요청 요약

- 설계.md의 "최신순 요청 작업 목록 조회" 기능 구현.
- **비즈니스 로직(Service 본문)은 사용자**가 작성 — 내 역할은 Controller + 레이어 간 DTO 변환 + 필요한 DTO + 컴파일에 필요한 주변 인프라.
- 커밋하지 않음(사용자 검토 후 별도 진행).

## 생성 파일

| 파일 | 유형 | 내용 |
|------|------|------|
| `common/BaseResponse.java` | + | 공통 응답 래퍼. `@JsonProperty("isSuccess")` 로 JSON 키 고정 |
| `common/BaseResponseStatus.java` | + | SUCCESS / INVALID_INPUT / INTERNAL_SERVER_ERROR / EXCEL_JOB_NOT_FOUND |
| `common/BaseException.java` · `GlobalExceptionHandler.java` | + | 예외 → BaseResponse 변환 (error-handling.md) |
| `api/exceljob/controller/ExcelJobController.java` | + | `GET /api/excel-jobs` — page/size → Pageable → service → BaseResponse 래핑 |
| `api/exceljob/repository/ExcelJobRepository.java` | + | `findAllByOrderByRequestedAtDesc(Pageable)` (최신순 파생 쿼리) |
| `api/exceljob/service/ExcelJobService.java` | ~ | **스텁**: `findAllLatest(Pageable)` 시그니처 + TODO + 변환 예시 주석. 본문은 사용자 작성 |
| `dto/result/ExcelJobResult.java` | + | 엔티티→Result 변환 `from(ExcelJob)` |
| `dto/response/ExcelJobResponse.java` | + | Result→Response 변환 `from(ExcelJobResult)` (status는 `.name()`) |
| `dto/response/ExcelJobPageResponse.java` | + | content + page/size/totalElements/totalPages/last. `from(Page<ExcelJobResponse>)` |

## 사용자가 작성할 Service 본문 (참고)

변환 메서드는 모두 준비됨. 아래 한 줄 흐름으로 조합하면 된다(스텁 주석에도 동일하게 기재):

```java
return ExcelJobPageResponse.from(
    excelJobRepository.findAllByOrderByRequestedAtDesc(pageable)
        .map(job -> ExcelJobResponse.from(ExcelJobResult.from(job))));
```

## 검증 결과

- `./gradlew compileJava` → **BUILD SUCCESSFUL** (Service 스텁은 `throw`로 컴파일 통과).
- 실제 목록 동작은 사용자가 Service 본문 작성 + MySQL 기동 후 `bootRun` 으로 확인.

## 알려진 제약 · follow-up

- **Service 본문 미구현**: 현재 `UnsupportedOperationException` throw. 사용자가 비즈니스 로직 작성 필요.
- **Query DTO 미생성**: 필터 없는 페이징이라 `Pageable` 로 전달(dto-create의 "단순 조회는 Query 생략 가능" 적용). 필터가 생기면 `ExcelJobSearchQuery` 추가 권장.
- `ExcelJobPageResponse.from` 은 입력 파라미터로만 Spring `Page` 를 받고, DTO 상태에는 framework 타입을 보관하지 않음.
- 다른 엔드포인트(POST 생성, 단건 조회, 다운로드)는 이번 범위 밖.
