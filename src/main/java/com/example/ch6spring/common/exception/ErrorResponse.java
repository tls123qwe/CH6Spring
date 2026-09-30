package com.example.ch6spring.common.exception;

public record ErrorResponse(String code, String message) {

    public static ErrorResponse of(ErrorCode errorcode) {

        return new ErrorResponse(errorcode.name(), errorcode.getMessage());
    }

    public static ErrorResponse of(ErrorCode errorCode, String message) {

        return new ErrorResponse(errorCode.name(), message);
    }
}
