package com.chanyoung.usedmarket.global.exception;

import lombok.Getter;

@Getter
public class ErrorResponseDto {

    private final int status;
    private final String code;
    private final String message;

    public ErrorResponseDto(ErrorCode errorCode) {
        this.status = errorCode.getStatus().value();
        this.code = errorCode.name();
        this.message = errorCode.getMessage();
    }

}