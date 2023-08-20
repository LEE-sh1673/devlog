package com.devlog.jwt;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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

    private final Long validityInMilliseconds;

    public JwtTokenProvider(
        @Value("${lsh.jwt.token.secret-key}") final String secretKey,
        @Value("${lsh.jwt.token.expire-length}") final Long validityInMilliseconds
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes(UTF_8));
        this.validityInMilliseconds = validityInMilliseconds;
    }

    public String generateToken(final String email) {
        final Date now = new Date(System.currentTimeMillis());

        return Jwts.builder()
            .setSubject(email)
            .setIssuedAt(now)
            .setExpiration(new Date(now.getTime() + validityInMilliseconds))
            .signWith(secretKey, HS256)
            .compact();
    }

}
