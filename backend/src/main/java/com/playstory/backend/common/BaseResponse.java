package com.playstory.backend.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class BaseResponse<T> {

    @JsonProperty("isSuccess")
    private final boolean isSuccess;
    private final int code;
    private final String message;
    private final T result;

    private BaseResponse(BaseResponseStatus status, String message, T result) {

        this.isSuccess = status.isSuccess();
        this.code = status.getCode();
        this.message = message;
        this.result = result;
    }

    /**
     * 성공 응답을 결과 데이터와 함께 생성한다.
     *
     * @param result 응답 본문 데이터
     * @return 성공 응답
     */
    public static <T> BaseResponse<T> success(T result) {

        return new BaseResponse<>(BaseResponseStatus.SUCCESS, BaseResponseStatus.SUCCESS.getMessage(), result);
    }

    /**
     * 결과 데이터가 없는 성공 응답을 생성한다.
     *
     * @return 성공 응답
     */
    public static BaseResponse<Void> success() {

        return new BaseResponse<>(BaseResponseStatus.SUCCESS, BaseResponseStatus.SUCCESS.getMessage(), null);
    }

    /**
     * 상태 코드의 기본 메시지로 에러 응답을 생성한다.
     *
     * @param status 에러 상태
     * @return 에러 응답
     */
    public static BaseResponse<Void> error(BaseResponseStatus status) {

        return new BaseResponse<>(status, status.getMessage(), null);
    }

    /**
     * 커스텀 메시지로 에러 응답을 생성한다.
     *
     * @param status  에러 상태
     * @param message 상황에 맞는 상세 메시지
     * @return 에러 응답
     */
    public static BaseResponse<Void> error(BaseResponseStatus status, String message) {

        return new BaseResponse<>(status, message, null);
    }
}
