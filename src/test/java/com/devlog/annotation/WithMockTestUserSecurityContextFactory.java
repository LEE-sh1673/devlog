package com.devlog.annotation;

import com.devlog.config.UserPrincipal;
import com.devlog.domain.User;
import com.devlog.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;


@RequiredArgsConstructor
public class WithMockTestUserSecurityContextFactory implements WithSecurityContextFactory<WithMockTestUser> {

    private final UserRepository userRepository;

    @Override
    public SecurityContext createSecurityContext(final WithMockTestUser withUser) {
//        final User principal = new User(
//            withUser.email(),
//            withUser.password(),
//            getGrantedAuthorities(withUser.roles())
//        );
//

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

    private static List<GrantedAuthority> getGrantedAuthorities(final String[] roles) {
        final List<GrantedAuthority> grantedAuthorities = new ArrayList<>();

        for (final String role : roles) {
            if (role.startsWith("ROLE_")) {
                throw new IllegalArgumentException("roles cannot start with ROLE_ Got " + role);
            }
            grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + role));
        }
        return grantedAuthorities;
    }
}
