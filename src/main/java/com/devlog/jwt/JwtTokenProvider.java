package com.devlog.jwt;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.devlog.jwt.dto.AuthTokenDto;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import static io.jsonwebtoken.SignatureAlgorithm.HS256;
import static java.nio.charset.StandardCharsets.UTF_8;

@Slf4j
@Getter
@Component
public class JwtTokenProvider {

    private final SecretKey secretKey;

    @Value("${lsh.jwt.token.expire-length}")
    private Long validityInMilliseconds;

    public JwtTokenProvider(
        @Value("${lsh.jwt.token.secret-key}") final String secretKey
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes(UTF_8));
    }

    public AuthTokenDto generateToken(final Authentication authentication) {
        return AuthTokenDto.builder()
            .accessToken(buildAccessToken(authentication))
            .refreshToken(buildRefreshToken(authentication))
            .expiresIn(new Date(System.currentTimeMillis()).getTime() + validityInMilliseconds)
            .build();
    }

    private String buildAccessToken(final Authentication authentication) {
        return buildToken(authentication);
    }

    private String buildRefreshToken(final Authentication authentication) {
        final String refreshToken = buildToken(authentication);
        // TODO: save Refresh token to repository (in redis later)
        return refreshToken;
    }

    private String buildToken(final Authentication authentication) {
        final Date now = new Date(System.currentTimeMillis());

        return Jwts.builder()
            .setSubject(authentication.getName())
            .setIssuedAt(now)
            .setExpiration(new Date(now.getTime() + validityInMilliseconds))
            .signWith(secretKey, HS256)
            .compact();
    }
}
