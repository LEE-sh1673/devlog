package com.devlog.controller;

import com.devlog.config.data.UserSession;
import com.devlog.request.LoginRequest;
import com.devlog.request.SignUpRequest;
import com.devlog.service.AuthService;

import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
        return userSession.getId();
    }

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody @Valid final LoginRequest request) {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(authService.login(request.getEmail(), request.getPassword()));
    }

    @PostMapping("/auth/signup")
    public ResponseEntity<Void> signup(@RequestBody @Valid final SignUpRequest request) {
        authService.signup(request.toServiceDto());
        return ResponseEntity.ok().build();
    }
}
