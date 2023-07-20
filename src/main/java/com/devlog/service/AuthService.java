package com.devlog.service;

import com.devlog.domain.User;
import com.devlog.errors.v2.UnauthorizedException;
import com.devlog.repository.UserRepository;
import com.devlog.request.LoginRequest;
import com.devlog.response.SessionResponse;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.util.Base64;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    private static final String JWS_SECRET_KEY = "UktjAbZfec6goTwhuYV6cppdp7cQ0/tQpvL3B22hIYs=";

    @Transactional
    public SessionResponse login(final LoginRequest request) {
        User user = userRepository
            .findByEmailAndPassword(request.getEmail(), request.getPassword())
            .orElseThrow(UnauthorizedException::new);

        return SessionResponse.builder()
            .accessToken(buildJWSToken(user.getId()))
            .build();
    }

    private static String buildJWSToken(final Long userId) {
        SecretKey key
            = Keys.hmacShaKeyFor(Base64.getDecoder().decode(JWS_SECRET_KEY));

        return Jwts.builder()
            .setSubject(String.valueOf(userId))
            .signWith(key)
            .compact();
    }
}
