package com.example.ch6spring.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor

public enum ErrorCode {

    // 400 error
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "잘못된 입력값입니다."),
    INVALID_AMOUNT(HttpStatus.BAD_REQUEST, "금액은 0보다 커야합니다."),

    // 404
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다."),
    MENU_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 메뉴입니다."),

    // 409
    INSUFFICIENT_POINT(HttpStatus.CONFLICT, "포인트 잔액이 부족합니다."),

    // 500
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String message;

}
