package com.devlog.errors.v2.jwt;

import lombok.Getter;

@Getter
public enum ErrorType {

    NOT_AUTHENTICATED(1001, "인증되지 않았습니다."),
    ALREADY_AUTHENTICATED(1002, "이미 인증정보가 존재합니다."),
    EXPIRED_TOKEN(1003, "만료된 JWT 토큰입니다."),
    INVALID_TOKEN(1004, "유효하지 않은 토큰입니다."),
    TOKEN_NOT_FOUND(1006, "토큰 정보가 없습니다."),
    FORBIDDEN(1005, "권한이 없습니다.");

    private final int code;
    private final String message;

    ErrorType(final int code, final String message) {
        this.code = code;
        this.message = message;
    }
}
