package com.devlog.errors.v2.jwt;

import com.devlog.errors.v2.DevlogApiException;

public class JwtAuthenticationException extends DevlogApiException {

    private final int httpStatus;

    public JwtAuthenticationException(final ErrorType errorType) {
        super(errorType.getMessage());
        this.httpStatus = errorType.getCode();
    }

    @Override
    public int httpStatus() {
        return httpStatus;
    }
}
