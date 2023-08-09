package com.devlog.config.handler;

import static com.devlog.utils.ApiUtils.*;
import static jakarta.servlet.http.HttpServletResponse.*;
import static java.nio.charset.StandardCharsets.*;
import static org.springframework.http.MediaType.*;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.devlog.config.UserPrincipal;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
        HttpServletResponse response,
        Authentication authentication)
        throws IOException {

        UserPrincipal principal = (UserPrincipal)authentication.getPrincipal();
        log.info("[인증성공] user = {}", principal.getUsername());
        sendResponse(response, success(principal.getUserId()));
    }

    private void sendResponse(HttpServletResponse response, ApiResult<?> apiResult)
        throws IOException {
        response.setCharacterEncoding(UTF_8.displayName());
        response.setStatus(SC_OK);
        response.setContentType(APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(apiResult));
    }
}
