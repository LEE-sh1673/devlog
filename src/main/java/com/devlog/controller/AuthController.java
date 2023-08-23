package com.devlog.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.devlog.request.LoginRequest;
import com.devlog.request.SignUpRequest;
import com.devlog.response.TokenResponse;
import com.devlog.service.AuthService;
import com.devlog.utils.ApiUtils;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static com.devlog.utils.ApiUtils.success;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/auth/signup")
    public ResponseEntity<?> signup(@RequestBody @Valid final SignUpRequest request) {
        authService.signup(request.toServiceDto());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/auth/login")
    public ApiUtils.ApiResult<TokenResponse> login(
        @RequestBody @Valid final LoginRequest request
    ) {
        return success(
            authService.login(request.getEmail(), request.getPassword())
        );
    }
}
