package com.devlog.annotation;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import com.devlog.config.UserPrincipal;
import com.devlog.domain.User;
import com.devlog.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class WithMockTestUserSecurityContextFactory
    implements WithSecurityContextFactory<WithMockTestUser> {

    private final UserRepository userRepository;

    @Override
    public SecurityContext createSecurityContext(final WithMockTestUser withUser) {
        final User user = userRepository.save(User.builder()
            .password(withUser.password())
            .email(withUser.email())
            .build()
        );
        final UserPrincipal principal = new UserPrincipal(user);
        final SecurityContext context = SecurityContextHolder.createEmptyContext();

        context.setAuthentication(new UsernamePasswordAuthenticationToken(
            principal,
            principal.getPassword(),
            principal.getAuthorities()
        ));
        return context;
    }
}
