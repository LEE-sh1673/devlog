package com.devlog.jwt;

import java.util.Date;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.devlog.errors.v2.jwt.JwtAuthenticationException;

import lombok.RequiredArgsConstructor;

import static com.devlog.errors.v2.jwt.ErrorType.EXPIRED_TOKEN;
import static com.devlog.errors.v2.jwt.ErrorType.INVALID_TOKEN;

@RequiredArgsConstructor
public class JwtAuthenticationService {

    private final JwtClaimParser claimExtractor;

    private final UserDetailsService userDetailsService;

    public AbstractAuthenticationToken getAuthentication(final String token)
        throws JwtAuthenticationException {
        validateToken(token);
        final UserDetails userDetails = getUserDetails(token);
        return new UsernamePasswordAuthenticationToken(
            userDetails, "",
            userDetails.getAuthorities()
        );
    }

    private void validateToken(final String token) {
        if (isTokenExpired(token)) {
            throw new JwtAuthenticationException(EXPIRED_TOKEN);
        }
    }

    private boolean isTokenExpired(final String token) throws JwtAuthenticationException {
        return claimExtractor.parseExpiration(token).before(new Date());
    }

    private UserDetails getUserDetails(final String token) {
        try {
            return userDetailsService.loadUserByUsername(
                claimExtractor.parseSubject(token)
            );
        } catch (UsernameNotFoundException e) {
            throw new JwtAuthenticationException(INVALID_TOKEN);
        }
    }
}
