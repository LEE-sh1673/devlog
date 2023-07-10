package com.devlog.errors.v1;

import org.springframework.http.HttpStatus;

public class InvalidRequest extends DevlogException {

    private static final String MESSAGE = "잘못된 요청입니다.";

    public InvalidRequest() {
        super(MESSAGE);
    }

    public InvalidRequest(final String fieldName, final String message) {
        super(MESSAGE);
        super.addValidation(fieldName, message);
    }

    @Override
    public int statusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }
}
