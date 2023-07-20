package com.devlog.controller;

import com.devlog.config.data.UserSession;
import com.devlog.request.LoginRequest;
import com.devlog.response.SessionResponse;
import com.devlog.service.AuthService;
import java.time.Duration;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/foo")
    public Long foo(final UserSession userSession) {
        log.info(">> id: {}", userSession.getId());
        return userSession.getId();
    }

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody @Valid final LoginRequest request) {
        ResponseCookie cookie
            = getCookieFromResponse(authService.login(request));

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .build();
    }

    private ResponseCookie getCookieFromResponse(final SessionResponse response) {
        return ResponseCookie
            .from("SESSION", response.getAccessToken())
            .domain("localhost") //TODO: 서버 환경에 따른 변경 필요
            .path("/")
            .httpOnly(true)
            .secure(false)
            .maxAge(Duration.ofDays(30))
            .sameSite("Strict")
            .build();
    }
}
