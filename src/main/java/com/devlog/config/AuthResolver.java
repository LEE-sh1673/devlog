package com.devlog.config;

import com.devlog.config.data.UserSession;
import com.devlog.domain.Session;
import com.devlog.errors.v2.UnauthorizedException;
import com.devlog.repository.SessionRepository;
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

    private final SessionRepository sessionRepository;

    @Override
    public boolean supportsParameter(final MethodParameter parameter) {
        return parameter.getParameterType().equals(UserSession.class);
    }

    @Override
    public Object resolveArgument(final MethodParameter parameter,
        final ModelAndViewContainer mavContainer,
        final NativeWebRequest webRequest, final WebDataBinderFactory binderFactory)
        throws Exception {

        final Cookie[] cookies = getCookies(webRequest);

        final Session session = sessionRepository
            .findByAccessToken(cookies[0].getValue())
            .orElseThrow(UnauthorizedException::new);

        return UserSession.builder()
            .id(session.getUser().getId())
            .build();
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
