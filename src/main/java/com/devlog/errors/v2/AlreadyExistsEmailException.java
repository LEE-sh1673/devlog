package com.devlog.errors.v2;

public class AlreadyExistsEmailException extends DevlogApiException {

    private static final int HTTP_BAD_REQUEST = 400;

    private static final String MESSAGE = "이미 가입된 이메일입니다.";

    public AlreadyExistsEmailException() {
        super(MESSAGE);
    }

    @Override
    public int httpStatus() {
        return HTTP_BAD_REQUEST;
    }
}
