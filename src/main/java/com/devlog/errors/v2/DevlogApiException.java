package com.devlog.errors.v2;

public abstract class DevlogApiException extends RuntimeException {

    public DevlogApiException(final String message) {
        super(message);
    }

    public abstract int httpStatus();
}
