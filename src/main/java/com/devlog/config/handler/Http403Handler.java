package com.devlog.config.handler;

import java.io.IOException;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static com.devlog.utils.ApiUtils.ApiResult;
import static com.devlog.utils.ApiUtils.error;
import static jakarta.servlet.http.HttpServletResponse.SC_FORBIDDEN;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RequiredArgsConstructor
public class Http403Handler implements AccessDeniedHandler {

    private static final String ERR_MESSAGE = "리소스에 접근할 수 없습니다.";

    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request,
        HttpServletResponse response,
        AccessDeniedException accessDeniedException) throws IOException, ServletException {
        log.error(ERR_MESSAGE);
        sendResponse(response, error(ERR_MESSAGE, FORBIDDEN));
    }

    private void sendResponse(HttpServletResponse response, ApiResult<?> error)
        throws IOException {
        response.setCharacterEncoding(UTF_8.displayName());
        response.setStatus(SC_FORBIDDEN);
        response.setContentType(APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(error));
    }
}
