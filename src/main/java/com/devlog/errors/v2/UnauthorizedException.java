package com.devlog.errors.v2;

public class UnauthorizedException extends DevlogApiException {

    private static final int HTTP_UNAUTHORIZED = 401;

    private static final String MESSAGE = "아이디/비밀번호가 올바르지 않습니다.";

    public UnauthorizedException() {
        super(MESSAGE);
    }

    @Override
    public int httpStatus() {
        return HTTP_UNAUTHORIZED;
    }
}
