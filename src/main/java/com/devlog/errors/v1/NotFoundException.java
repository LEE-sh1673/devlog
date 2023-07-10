package com.devlog.errors.v1;

import org.springframework.http.HttpStatus;

public class NotFoundException extends DevlogException {

    private static final String MESSAGE = "해당 리소스를 찾지 못했습니다.";

    public NotFoundException() {
        super(MESSAGE);
    }

    public NotFoundException(final String fieldName, final String message) {
        super(MESSAGE);
        super.addValidation(fieldName, message);
    }

    @Override
    public int statusCode() {
        return HttpStatus.NOT_FOUND.value();
    }
}