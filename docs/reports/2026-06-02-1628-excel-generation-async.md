# 엑셀 생성 비동기 기능 구현

> 2026-06-02 16:28

`POST /api/excel-jobs` 로 job을 요청하면 즉시 PENDING 응답 후 백그라운드에서 orders(10만건)를 .xlsx로 생성한다.

## 결정 사항

- 엑셀 라이브러리: **Apache POI `poi-ooxml:5.3.0`** — SXSSF 스트리밍(.xlsx).
- 비동기: **`@Async` + 전용 `ThreadPoolTaskExecutor`**.
- 실패 원인: 로그만(미영속) — status=FAILED만 DB 반영. 스키마 변경 없음.

## 비동기 흐름 (race 회피)

commit-before-async race를 `@TransactionalEventListener(AFTER_COMMIT)` + `@Async` 로 차단:

```
create() @Transactional
  save(ExcelJob.pending())            // PENDING insert
  publishEvent(ExcelJobRequestedEvent(jobId))
  return PENDING                      // 즉시 응답
        │ 트랜잭션 commit
        ▼ AFTER_COMMIT
ExcelGenerator.onExcelJobRequested()  @Async("excelExecutor")
  markProcessing(jobId)               // 짧은 @Transactional
  file = orderExcelWriter.write(jobId)// 트랜잭션 밖, 청크 스트리밍
  성공 → markDone(jobId, file)
  예외 → log.error + markFailed(jobId)
```

AFTER_COMMIT이라 비동기 스레드는 항상 트랜잭션 commit된 job을 조회. `ExcelJobService`는 이벤트 publisher만 의존 → 의존성 사이클 없음.

## 생성 · 수정 파일

| 파일 | 유형 | 내용 |
|------|------|------|
| `build.gradle` | ~ | poi-ooxml:5.3.0 추가 |
| `config/AsyncConfig.java` | + | @EnableAsync, excelExecutor(core1/max2/queue50/CallerRuns) |
| `application.yaml` | ~ | excel.output-dir (EXCEL_OUTPUT_DIR override) |
| `order/repository/OrderRepository.java` | + | keyset 페이징 findByIdGreaterThanOrderByIdAsc |
| `exceljob/event/ExcelJobRequestedEvent.java` | + | record(jobId) |
| `exceljob/service/ExcelJobService.java` | ~ | create + markProcessing/markDone/markFailed (각 @Transactional) |
| `exceljob/service/ExcelGenerator.java` | + | @Async @TransactionalEventListener(AFTER_COMMIT) 워커 |
| `exceljob/excel/OrderExcelWriter.java` | + | SXSSF(window100) 스트리밍, keyset 5,000행 청크 |
| `exceljob/controller/ExcelJobController.java` | ~ | @PostMapping create() |
| `docs/api/openapi.yaml` | ~ | POST /api/excel-jobs 추가 |
| `docs/tasks/엑셀-생성.md` | + | 기록용 task 파일(플랜) |

## 대용량(10만건) 전략

- 읽기: keyset 페이징(`id > lastId LIMIT 5000`) 청크 반복 — 전체 List 미적재, deep-offset 비용 없음.
- 쓰기: SXSSF window=100 → 메모리 100행만 유지, 초과분 temp flush. close()가 temp 정리(`dispose()` deprecated라 미사용).
- 트랜잭션: 파일 생성 동안 장기 트랜잭션 없음. 상태 전이만 짧은 @Transactional.

## 검증

- `./gradlew compileJava` → **BUILD SUCCESSFUL** (deprecation 경고 0).
- openapi.yaml YAML 파싱 OK (get + post).
- 런타임(백엔드 재기동 + MySQL/seed 필요)은 사용자 확인:

```
curl -X POST http://localhost:8080/api/excel-jobs        # PENDING 즉시
curl "http://localhost:8080/api/excel-jobs?page=0&size=10" # PROCESSING→DONE
ls -lh ./data/excel/                                       # orders-*.xlsx
```

## 알려진 제약 · follow-up

- **테스트 미작성**: Service/Controller 테스트(testing.md) 후속 — 비동기/POI는 슬라이스·mock 분리 필요.
- 다운로드(`GET /{jobId}/download`)는 별도 작업. 프론트 "엑셀 생성 요청" 버튼은 이 POST로 동작.
- 실패 원인은 로그만(결정) — DB 영속(error_log)은 후속 여지.
- 커밋하지 않음(미요청).
