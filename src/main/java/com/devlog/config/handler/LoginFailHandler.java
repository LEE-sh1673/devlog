package com.devlog.config.handler;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static com.devlog.utils.ApiUtils.ApiResult;
import static com.devlog.utils.ApiUtils.error;
import static jakarta.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RequiredArgsConstructor
public class LoginFailHandler implements AuthenticationFailureHandler {

    private static final String ERR_MESSAGE = "아이디 혹은 비밀번호가 올바르지 않습니다.";

    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
        HttpServletResponse response,
        AuthenticationException exception) throws IOException {
        log.error("[인증오류] {}", ERR_MESSAGE);
        sendResponse(response, error(ERR_MESSAGE, BAD_REQUEST));
    }

    private void sendResponse(HttpServletResponse response, ApiResult<?> error)
        throws IOException {
        response.setCharacterEncoding(UTF_8.displayName());
        response.setStatus(SC_BAD_REQUEST);
        response.setContentType(APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(error));
    }
}
