package com.playstory.backend.common;

import lombok.Getter;

@Getter
public class BaseException extends RuntimeException {

    private final BaseResponseStatus status;

    public BaseException(BaseResponseStatus status) {

        super(status.getMessage());
        this.status = status;
    }
}
