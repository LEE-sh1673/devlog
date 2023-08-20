package com.devlog.config.filter;

import java.io.IOException;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.devlog.errors.v2.jwt.JwtAuthenticationException;
import com.devlog.jwt.AuthorizationExtractor;
import com.devlog.jwt.JwtAuthenticationService;
import com.devlog.utils.ApiUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static com.devlog.utils.ApiUtils.error;
import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtAuthenticationService jwtAuthService;

    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(
        @NonNull final HttpServletRequest request,
        @NonNull final HttpServletResponse response,
        @NonNull final FilterChain chain
    ) throws IOException, ServletException {

        final String token = resolveToken(request);

        if (StringUtils.hasText(token) &&
            SecurityContextHolder.getContext().getAuthentication() == null) {

            try {
                authenticate(token, request);
            } catch (JwtAuthenticationException e) {
                sendErrorResponse(response, error(e, UNAUTHORIZED));
                return;
            }
        }
        chain.doFilter(request, response);
    }

    private void authenticate(final String token, final HttpServletRequest request)
        throws JwtAuthenticationException {

        final AbstractAuthenticationToken authentication
            = jwtAuthService.getAuthentication(token);

        authentication.setDetails(
            new WebAuthenticationDetailsSource().buildDetails(request)
        );

        SecurityContextHolder.getContext()
            .setAuthentication(authentication);
    }

    private String resolveToken(final HttpServletRequest request) {
        return AuthorizationExtractor.extract(request).orElse(null);
    }

    private void sendErrorResponse(final HttpServletResponse response,
        final ApiUtils.ApiResult<?> error) throws IOException {
        response.setCharacterEncoding(UTF_8.displayName());
        response.setStatus(SC_UNAUTHORIZED);
        response.setContentType(APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(error));
    }
}
