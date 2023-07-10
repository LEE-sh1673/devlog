package com.devlog.errors.v1;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

@Getter
public abstract class DevlogException extends RuntimeException {

    private final Map<String, String> validation = new HashMap<>();

    public DevlogException(String message) {
        super(message);
    }

    public DevlogException(String message, Throwable cause) {
        super(message, cause);
    }

    public abstract int statusCode();

    protected void addValidation(final String fieldName, final String message) {
        validation.put(fieldName, message);
    }
}
