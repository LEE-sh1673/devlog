package com.devlog.errors.v2.jwt;

import lombok.Getter;

@Getter
public enum ErrorType {
    ALREADY_AUTHENTICATED(1001, "이미 인증정보가 존재합니다."),
    EXPIRED_TOKEN(1002, "만료된 JWT 토큰입니다."),
    INVALID_TOKEN(1003, "유효하지 않은 토큰입니다."),
    FORBIDDEN(1004, "권한이 없습니다.");

    private final int code;
    private final String message;

    ErrorType(final int code, final String message) {
        this.code = code;
        this.message = message;
    }
}
