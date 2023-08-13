package com.devlog.errors.v2;

public class PasswordMisMatchesException extends DevlogApiException {

    private static final int HTTP_BAD_REQUEST = 400;

    private static final String MESSAGE = "비밀번호가 올바르지 않습니다.";

    public PasswordMisMatchesException() {
        super(MESSAGE);
    }

    @Override
    public int httpStatus() {
        return HTTP_BAD_REQUEST;
    }
}
