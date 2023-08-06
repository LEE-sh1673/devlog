package com.devlog.config.handler;

import static jakarta.servlet.http.HttpServletResponse.*;
import static java.nio.charset.StandardCharsets.*;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import com.devlog.response.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class LoginFailHandler implements AuthenticationFailureHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
        AuthenticationException exception) throws IOException {
        log.error("[인증오류] 아이디 혹은 비밀번호가 올바르지 않습니다.");
        sendResponse(response, "아이디 혹은 비밀번호가 올바르지 않습니다.");
    }

    private void sendResponse(HttpServletResponse response, String message) throws IOException {
        response.setCharacterEncoding(UTF_8.displayName());
        response.setStatus(SC_BAD_REQUEST);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(
            ErrorResponse.builder()
                .message(message)
                .code(String.valueOf(SC_BAD_REQUEST))
                .build()
        ));
    }
}
