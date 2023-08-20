package com.devlog.config.filter;

import static com.devlog.utils.ApiUtils.error;
import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.io.IOException;
import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.devlog.errors.v2.jwt.ErrorType;
import com.devlog.errors.v2.jwt.JwtAuthenticationException;
import com.devlog.jwt.AuthorizationExtractor;
import com.devlog.jwt.JwtClaimExtractor;
import com.devlog.utils.ApiUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtClaimExtractor claimExtractor;

    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
        @NonNull final HttpServletRequest request,
        @NonNull final HttpServletResponse response,
        @NonNull final FilterChain chain
    ) throws IOException, ServletException {

        final String token = resolveToken(request);

        if (!StringUtils.hasText(token)) {
            chain.doFilter(request, response);
            return;
        }
        try {
            if (!isTokenExpired(token)) {
                final Authentication authentication = getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (JwtAuthenticationException e) {
            sendResponse(response, error(e, HttpStatus.UNAUTHORIZED));
        }
        chain.doFilter(request, response);
    }

    private String resolveToken(final HttpServletRequest request) {
        return AuthorizationExtractor.extract(request).orElse(null);
    }

    private boolean isTokenExpired(final String token) {
        return claimExtractor.extractExpiration(token).before(new Date());
    }

    private void sendResponse(final HttpServletResponse response,
        final ApiUtils.ApiResult<?> error) throws IOException {
        response.setCharacterEncoding(UTF_8.displayName());
        response.setStatus(SC_UNAUTHORIZED);
        response.setContentType(APPLICATION_JSON_VALUE);
        response.getWriter().write(new ObjectMapper().writeValueAsString(error));
    }

    public Authentication getAuthentication(final String token) {
        final UserDetails userDetails = getUserDetails(token);
        return new UsernamePasswordAuthenticationToken(
            userDetails, "",
            userDetails.getAuthorities()
        );
    }

    private UserDetails getUserDetails(final String token) {
        return userDetailsService.loadUserByUsername(
            claimExtractor.extractUsername(token)
        );
    }
}
