package com.devlog.errors.v2;

public class InvalidRequest extends DevlogApiException {

    private static final int HTTP_BAD_REQUEST = 400;

    private static final String MESSAGE = "잘못된 요청입니다.";

    public InvalidRequest() {
        super(MESSAGE);
    }

    @Override
    public int httpStatus() {
        return HTTP_BAD_REQUEST;
    }
}
