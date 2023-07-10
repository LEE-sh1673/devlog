package com.devlog.response;

import static java.util.Objects.*;

import java.util.HashMap;
import java.util.Map;

import lombok.Builder;
import lombok.Getter;

/**
 * This will be like:
 * {
 *     "code": "400",
 *     "message": "잘못된 요청입니다."
 *     "validation": [
 *          { "fieldName" : "title", "errorMessage" : "값을 입력해주세요."},
 *          ...
 *     ]
 * }
 * */
@Getter
public class ErrorResponse {

    private final String code;

    private final String message;

    private final Map<String, String> validation;

    @Builder
    public ErrorResponse(final String code,
        final String message, final Map<String, String> validation) {
        this.code = code;
        this.message = message;
        this.validation = requireNonNullElse(validation, new HashMap<>());
    }

    public void addValidation(final String fieldName, final String errorMessage) {
        validation.put(fieldName, errorMessage);
    }
}
