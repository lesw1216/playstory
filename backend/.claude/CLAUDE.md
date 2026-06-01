# Backend — CLAUDE.md

PlayStory 백엔드. 특정 테이블 데이터를 Excel로 내보내는 요청을 **비동기 job**으로 처리한다.

**스택:** Spring Boot 3.5 · Java 17 · JPA · MySQL 8 · Lombok

---

## 주요 명령어

```bash
cd backend

./gradlew bootRun          # 개발 서버 기동
./gradlew test             # 전체 테스트
./gradlew test --tests "com.playstory.backend.SomeTest"  # 단일 테스트 클래스
./gradlew build            # 빌드 (테스트 포함)
./gradlew build -x test    # 테스트 없이 빌드
```

MySQL이 로컬(`localhost:3306`)에 떠 있어야 기동 가능하다.

---

## 비동기 처리 흐름

엑셀 생성은 즉시 결과를 반환할 수 없으므로 요청 자체를 하나의 job으로 저장한다.

1. 클라이언트가 엑셀 생성 요청 → `excel_job` 에 `PENDING` 상태 job 생성, **job_id 즉시 응답**
2. 별도 worker 스레드/`@Async` 에서 실제 엑셀 생성 시작 → `PROCESSING` + `startedAt` 기록
3. 성공 → `DONE` + `endedAt` + `filePath` 기록
4. 예외 → `FAILED` + `endedAt`, 실패 원인은 `error_log` 에 기록

상태 흐름: `PENDING → PROCESSING → DONE` / `PENDING → PROCESSING → FAILED`

상태 확인은 프론트의 polling 으로 이루어진다 (`GET /api/excel-jobs?page&size`).

---

## 도메인

| 테이블 | 주요 컬럼 |
|--------|-----------|
| `order` | id, user_name, product_name, category, amount, status, order_date — 더미 10만 건 |
| `excel_job` | id, requested_at, started_at, ended_at, status, file_path |
| `error_log` | id, message, created_at, excel_job_id |

`status`(order): `confirmed`, `cancelled`, `pending`
`ExcelJobStatus`: `PENDING`, `PROCESSING`, `DONE`, `FAILED`

---

## 규칙

`.claude/rules/` 아래 규칙을 따른다. 핵심:

- API 응답은 항상 `BaseResponse<T>` 래퍼 — `@rules/coding-convention.md`
- 예외는 `BaseException(BaseResponseStatus.XXX)` 만 — `@rules/error-handling.md`
- DTO 레이어 분리(Request/Command/Query/Result/Response) — `@rules/dto-layer.md`, `@rules/dto-create.md`
- 엔드포인트 추가·변경 시 `docs/api/openapi.yaml` 갱신 — `@rules/openapi.md`
- 보안 설정값은 `src/main/resources/.env` — `@rules/env.md`
- Service/Controller 테스트 필수 — `@rules/testing.md`

라이브러리(Excel 생성 등) 추가는 `build.gradle` 확인 후 반드시 협의한다.
