package com.devlog.response;

import java.util.ArrayList;
import java.util.List;

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

    private final List<FieldError> validation = new ArrayList<>();

    @Builder
    public ErrorResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public void addValidation(final String fieldName, final String errorMessage) {
        FieldError fieldError = FieldError.builder()
            .fieldName(fieldName)
            .errorMessage(errorMessage)
            .build();
        validation.add(fieldError);
    }

    @Getter
    private static class FieldError {

        private final String fieldName;

        private final String errorMessage;

        @Builder
        public FieldError(String fieldName, String errorMessage) {
            this.fieldName = fieldName;
            this.errorMessage = errorMessage;
        }
    }
}
