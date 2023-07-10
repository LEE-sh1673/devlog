package com.devlog.errors;

import static com.devlog.utils.ApiUtils.*;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.devlog.errors.v1.DevlogException;
import com.devlog.response.ErrorResponse;
import com.devlog.utils.ApiUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GeneralExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleBadRequestException(final MethodArgumentNotValidException e) {
        ErrorResponse response = ErrorResponse.builder()
            .code("400")
            .message("잘못된 요청입니다.")
            .build();

        for (FieldError fieldError : e.getFieldErrors()) {
            response.addValidation(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(response);
    }

    @ExceptionHandler(DevlogException.class)
    public ResponseEntity<ErrorResponse> handleDevlogException(final DevlogException e) {
        ErrorResponse body = ErrorResponse.builder()
            .code(String.valueOf(e.statusCode()))
            .message(e.getMessage())
            .validation(e.getValidation())
            .build();

        return ResponseEntity
            .status(e.statusCode())
            .body(body);
    }

    private ResponseEntity<ApiUtils.ApiResult<?>> newResponse(
        final String message,
        final HttpStatus status) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return ResponseEntity
            .status(status)
            .headers(headers)
            .body(error(message, status));
    }
}
