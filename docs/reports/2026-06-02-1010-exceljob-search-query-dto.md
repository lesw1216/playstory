# Controller→Service 경계 Query DTO 교정

> 2026-06-02 10:10

`GET /api/excel-jobs` 의 Controller→Service 전달 객체가 DTO 컨벤션을 위반(raw `Pageable` 노출)하던 것을 `ExcelJobSearchQuery` 로 교정했다.

## 작업 요청 요약

- Controller→Service 파라미터가 우리 DTO 컨벤션과 어긋난다는 지적 → 분석 후 교정.
- 커밋하지 않음(사용자 검토 후 별도 진행).

## 위반 분석 (사전 진단)

- **네이밍**: `dto-layer.md` 상 조회 요청은 `**Query` 로 Service에 전달해야 하나, 변환 DTO 없이 raw `Pageable` 을 넘기고 있었다.
- **레이어 독립성**: Spring Data `Pageable`/`PageRequest` 가 Service 시그니처에 노출 — "각 DTO는 해당 레이어 안에서만 생성·소비" 위반.
- **예외 조항 오적용**: "단순 단건 조회(ID 기반)는 Query 생략 가능"은 단건 ID 전용. 페이징 목록에는 해당 없음.

## 변경 파일

| 파일 | 유형 | 내용 |
|------|------|------|
| `dto/query/ExcelJobSearchQuery.java` | + | `@Getter @Builder` 불변. 필드 `page`/`size`. 추후 필터 확장 여지 |
| `controller/ExcelJobController.java` | ~ | `@RequestParam(defaultValue)` 로 개별 수신 후 Query 빌드. `PageRequest`/`Pageable` import 제거 |
| `service/ExcelJobService.java` | ~ | 시그니처 `findAllLatest(ExcelJobSearchQuery)`. `PageRequest.of` 를 Service 내부로 이동. Javadoc 갱신 |

## 교정 후 흐름

```java
// Controller — 개별 파라미터 수신 + Query 빌드
ExcelJobSearchQuery query = ExcelJobSearchQuery.builder()
    .page(page).size(size).build();
service.findAllLatest(query);

// Service — framework 타입은 여기서만 생성
Pageable pageable = PageRequest.of(query.getPage(), query.getSize());
Page<ExcelJob> jobs = repo.findAllByOrderByRequestedAtDesc(pageable);
return ExcelJobPageResponse.from(
    jobs.map(j -> ExcelJobResponse.from(ExcelJobResult.from(j))));
```

## 결정 근거 (수신 방식)

- **객체 바인딩 미채택**: Query 객체에 직접 바인딩 + 디폴트는 mutable(@Setter) 필요 → Query 불변 규칙 위반. 불변 생성자 바인딩은 디폴트 부여 불가(`int` 0 강제).
- **채택**: `@RequestParam(defaultValue)` 개별 수신 → 디폴트가 가장 깔끔, Query 불변 유지(`dto-create.md` "Request 없는 쿼리 파라미터 조회는 Controller에서 Query 빌드" 패턴과 일치).

## 검증 결과

- `./gradlew compileJava` → **BUILD SUCCESSFUL**.
- 런타임 목록 동작은 MySQL 기동 + `bootRun` 으로 별도 확인 필요.

## 알려진 제약 · follow-up

- `Repository.findAllByOrderByRequestedAtDesc(Pageable)` 는 Spring Data 메서드라 `Pageable` 유지(Repository 계층이 Spring Data를 쓰는 건 정상).
- Service/Controller 테스트 미작성 — 추가 시 `ExcelJobSearchQuery` 기준으로 작성(`testing.md`).
- status/날짜 필터가 생기면 `ExcelJobSearchQuery` 에 필드만 추가하면 됨.
- 커밋하지 않음 — 사용자 검토 후 별도 진행.
