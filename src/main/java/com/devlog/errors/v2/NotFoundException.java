package com.devlog.errors.v2;

public class NotFoundException extends DevlogApiException {

    private static final int HTTP_NOT_FOUND = 404;

    private static final String MESSAGE = "해당 리소스를 찾지 못했습니다.";

    public NotFoundException() {
        super(MESSAGE);
    }

    @Override
    public int httpStatus() {
        return HTTP_NOT_FOUND;
    }
}