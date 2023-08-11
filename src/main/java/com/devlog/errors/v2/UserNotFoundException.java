package com.devlog.errors.v2;

public class UserNotFoundException extends DevlogApiException {

    private static final int HTTP_NOT_FOUND = 404;

    private static final String MESSAGE = "해당 사용자를 찾지 못했습니다.";

    public UserNotFoundException() {
        super(MESSAGE);
    }

    @Override
    public int httpStatus() {
        return HTTP_NOT_FOUND;
    }
}