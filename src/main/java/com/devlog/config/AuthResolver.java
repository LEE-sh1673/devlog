package com.devlog.config;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.devlog.config.data.UserSession;
import com.devlog.errors.v2.UnauthorizedException;
import com.devlog.repository.SessionRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class AuthResolver implements HandlerMethodArgumentResolver {

    private final SessionRepository sessionRepository;

    private final AppConfig appConfig;

    @Override
    public boolean supportsParameter(final MethodParameter parameter) {
        return parameter.getParameterType().equals(UserSession.class);
    }

    @Override
    public Object resolveArgument(final MethodParameter parameter,
        final ModelAndViewContainer mavContainer,
        final NativeWebRequest webRequest,
        final WebDataBinderFactory binderFactory) throws Exception {

        log.info(">>> appConfig: {}", appConfig);
        final String jws = getJWSToken(webRequest);

        try {
            Jws<Claims> claimsJws = Jwts.parserBuilder()
                .setSigningKey(appConfig.getJwtKey())
                .build()
                .parseClaimsJws(jws);

            String subject = claimsJws.getBody().getSubject();

            return UserSession.builder()
                .id(Long.parseLong(subject))
                .build();

        } catch (JwtException e) {
            throw new UnauthorizedException();
        }
    }

    private static String getJWSToken(final NativeWebRequest webRequest) {
        String jws = webRequest.getHeader("Authorization");

        if (jws == null || jws.isBlank()) {
            throw new UnauthorizedException();
        }
        return jws;
    }
}
