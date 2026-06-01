# 예외 처리 규칙

모든 예외는 `common/exception/` 에 정의된 공통 구조를 통해 처리한다.
Service에서 `IllegalArgumentException`, `IllegalStateException` 을 직접 던지지 않는다.

---

## 구조

```
common/
├── BaseResponseStatus.java     # 에러 코드 enum
├── BaseException.java          # 공통 런타임 예외
└── BaseResponse.java           # 공통 응답 래퍼 (success / error)
```

`GlobalExceptionHandler` 에서 `BaseException` 을 잡아 `BaseResponse.error(status)` 형태로 반환한다.

---

## BaseResponseStatus

모든 에러 코드는 `BaseResponseStatus` enum에 정의한다.

```java
@Getter
@RequiredArgsConstructor
public enum BaseResponseStatus {

    // 공통
    SUCCESS(true, 20000, "요청에 성공하였습니다."),
    INVALID_INPUT(false, 40000, "잘못된 입력입니다."),
    INTERNAL_SERVER_ERROR(false, 50000, "서버 오류가 발생했습니다."),

    // ExcelJob
    EXCEL_JOB_NOT_FOUND(false, 40401, "존재하지 않는 job입니다."),
    EXCEL_JOB_NOT_DONE(false, 40001, "아직 완료되지 않은 job입니다."),
    EXCEL_FILE_NOT_FOUND(false, 40402, "엑셀 파일을 찾을 수 없습니다."),
    EXCEL_GENERATION_FAILED(false, 50001, "엑셀 생성에 실패했습니다."),

    // Order
    ORDER_NOT_FOUND(false, 40403, "존재하지 않는 주문입니다.");

    private final boolean isSuccess;
    private final int code;
    private final String message;
}
```

새로운 도메인 추가 시 해당 도메인 블록을 추가한다.

---

## BaseException

비즈니스 로직 예외는 반드시 `BaseException` 을 사용한다.

```java
@Getter
public class BaseException extends RuntimeException {

    private final BaseResponseStatus status;

    public BaseException(BaseResponseStatus status) {
        super(status.getMessage());
        this.status = status;
    }
}
```

**사용 예시:**
```java
// Service에서 throw
ExcelJob job = excelJobRepository.findById(id)
    .orElseThrow(() -> new BaseException(BaseResponseStatus.EXCEL_JOB_NOT_FOUND));

// 상태 검증
if (!job.isDone()) {
    throw new BaseException(BaseResponseStatus.EXCEL_JOB_NOT_DONE);
}
```

---

## GlobalExceptionHandler

모든 예외는 `GlobalExceptionHandler` 에서 처리하여 `BaseResponse` 형태로 반환한다.

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 비즈니스 예외
    @ExceptionHandler(BaseException.class)
    public BaseResponse<Void> handleBaseException(BaseException e) {
        return BaseResponse.error(e.getStatus());
    }

    // @Valid 검증 예외
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public BaseResponse<Void> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
            .map(FieldError::getDefaultMessage)
            .findFirst()
            .orElse("입력값이 올바르지 않습니다.");
        return BaseResponse.error(BaseResponseStatus.INVALID_INPUT, message);
    }

    // 그 외 예외
    @ExceptionHandler(Exception.class)
    public BaseResponse<Void> handleException(Exception e) {
        return BaseResponse.error(BaseResponseStatus.INTERNAL_SERVER_ERROR);
    }
}
```

---

## 규칙 요약

- 새로운 도메인 추가 시 관련 `BaseResponseStatus` 를 먼저 정의한다
- Service에서 예외 발생 시 반드시 `BaseException(BaseResponseStatus.XXX)` 형태로 던진다
- `IllegalArgumentException`, `IllegalStateException` 직접 사용 금지
- Controller에서 try-catch 직접 사용 금지 → GlobalExceptionHandler에 위임
- 예외 메시지를 코드 내에 하드코딩 금지 → 반드시 BaseResponseStatus에서 관리
- 엑셀 생성 worker에서 발생한 예외는 job을 `FAILED` 로 전이시키고 `error_log` 에 원인을 기록한다
