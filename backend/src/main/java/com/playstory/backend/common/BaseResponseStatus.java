package com.playstory.backend.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BaseResponseStatus {

    // 공통
    SUCCESS(true, 20000, "요청에 성공하였습니다."),
    INVALID_INPUT(false, 40000, "잘못된 입력입니다."),
    INTERNAL_SERVER_ERROR(false, 50000, "서버 오류가 발생했습니다."),

    // ExcelJob
    EXCEL_JOB_NOT_FOUND(false, 40401, "존재하지 않는 job입니다.");

    private final boolean isSuccess;
    private final int code;
    private final String message;
}
