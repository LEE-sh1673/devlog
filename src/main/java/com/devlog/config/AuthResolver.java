package com.devlog.config;

import com.devlog.config.data.UserSession;
import com.devlog.errors.v2.UnauthorizedException;
import com.devlog.repository.SessionRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Slf4j
@RequiredArgsConstructor
public class AuthResolver implements HandlerMethodArgumentResolver {

    private static final String JWS_SECRET_KEY = "UktjAbZfec6goTwhuYV6cppdp7cQ0/tQpvL3B22hIYs=";

    private final SessionRepository sessionRepository;

    @Override
    public boolean supportsParameter(final MethodParameter parameter) {
        return parameter.getParameterType().equals(UserSession.class);
    }

    @Override
    public Object resolveArgument(final MethodParameter parameter,
        final ModelAndViewContainer mavContainer,
        final NativeWebRequest webRequest,
        final WebDataBinderFactory binderFactory) throws Exception {

        final String jws = getJWSToken(webRequest);

        try {
            Jws<Claims> claimsJws = Jwts.parserBuilder()
                .setSigningKey(JWS_SECRET_KEY)
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

    private static Cookie[] getCookies(final NativeWebRequest webRequest) {
        final HttpServletRequest request
            = webRequest.getNativeRequest(HttpServletRequest.class);

        if (!hasCookies(request)) {
            log.error(">>> Invalid request (request is null or no cookie exists.)");
            throw new UnauthorizedException();
        }
        return request.getCookies();
    }

    private static boolean hasCookies(final HttpServletRequest request) {
        return request != null && request.getCookies() != null;
    }
}
