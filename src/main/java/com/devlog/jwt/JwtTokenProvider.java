package com.devlog.jwt;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
// @Component
public class JwtTokenProvider {

    private final SecretKey secretKey;

    private final Long validityInMilliseconds;

    public JwtTokenProvider(@Value("${lsh.jwt.token.secret-key}") final String secretKey,
        @Value("${lsh.jwt.token.expire-length}") final Long validityInMilliseconds) {
        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        this.validityInMilliseconds = validityInMilliseconds;
    }

    public String createToken(final String payload) {
        Date now = new Date();

        return Jwts.builder()
            .setSubject(payload)
            .setIssuedAt(now)
            .setExpiration(new Date(now.getTime() + validityInMilliseconds))
            .signWith(secretKey, SignatureAlgorithm.HS256)
            .compact();
    }

    public boolean validateToken(final String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token);
            return true;

        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.error("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.error("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.error("지원하지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.error("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }

    public Authentication getAuthentication(final String token) {
        Claims claims = Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .getBody();

        User principal = new User(claims.getSubject(), "",
            List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        return new UsernamePasswordAuthenticationToken(principal, token,
            principal.getAuthorities());
    }
}
