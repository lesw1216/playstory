package com.playstory.backend.common;

import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 비즈니스 예외를 공통 응답으로 변환한다.
     *
     * @param e 발생한 비즈니스 예외
     * @return 에러 응답
     */
    @ExceptionHandler(BaseException.class)
    public BaseResponse<Void> handleBaseException(BaseException e) {

        return BaseResponse.error(e.getStatus());
    }

    /**
     * @Valid 검증 실패를 공통 응답으로 변환한다.
     *
     * @param e 검증 실패 예외
     * @return 입력 오류 응답
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public BaseResponse<Void> handleValidationException(MethodArgumentNotValidException e) {

        String message = e.getBindingResult().getFieldErrors().stream()
            .map(FieldError::getDefaultMessage)
            .findFirst()
            .orElse("입력값이 올바르지 않습니다.");

        return BaseResponse.error(BaseResponseStatus.INVALID_INPUT, message);
    }

    /**
     * 처리되지 않은 예외를 서버 오류 응답으로 변환한다.
     *
     * @param e 발생한 예외
     * @return 서버 오류 응답
     */
    @ExceptionHandler(Exception.class)
    public BaseResponse<Void> handleException(Exception e) {

        return BaseResponse.error(BaseResponseStatus.INTERNAL_SERVER_ERROR);
    }
}
