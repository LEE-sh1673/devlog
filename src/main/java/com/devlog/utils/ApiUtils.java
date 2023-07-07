package com.devlog.utils;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class ApiUtils {

    public static <T> ApiResult<T> success(final T response) {
        return new ApiResult<>(true, response, null);
    }

    public static ApiResult<?> error(final Throwable throwable, final HttpStatus status) {
        return new ApiResult<>(false, null, new ApiError(throwable, status));
    }

    public static ApiResult<?> error(final String message, final HttpStatus status) {
        return new ApiResult<>(false, null, new ApiError(message, status));
    }

    @Getter
    public static class ApiError {

        private final String message;

        private final int status;

        ApiError(final Throwable throwable, final HttpStatus status) {
            this(throwable.getMessage(), status);
        }

        ApiError(final String message, final HttpStatus status) {
            this.message = message;
            this.status = status.value();
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("message", message)
                .append("status", status)
                .toString();
        }
    }

    @Getter
    @RequiredArgsConstructor
    public static class ApiResult<T> {

        private final boolean success;

        private final T response;

        private final ApiError error;

        @Override
        public String toString() {
            return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("success", success)
                .append("response", response)
                .append("error", error)
                .toString();
        }
    }

}
