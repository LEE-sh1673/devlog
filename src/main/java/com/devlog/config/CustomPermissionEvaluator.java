package com.devlog.config;

import com.devlog.domain.Post;
import com.devlog.errors.v2.UserNotFoundException;
import com.devlog.repository.PostRepository;
import java.io.Serializable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;

@Slf4j
@RequiredArgsConstructor
public class CustomPermissionEvaluator implements PermissionEvaluator {

    private final PostRepository postRepository;

    @Override
    public boolean hasPermission(final Authentication authentication, final Object targetDomainObject,
        final Object permission) {
        return false;
    }

    @Override
    public boolean hasPermission(final Authentication authentication, final Serializable targetId,
        final String targetType,
        final Object permission) {

        final UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

        final Post post = postRepository.findById((Long) targetId)
            .orElseThrow(UserNotFoundException::new);

        if (!post.matchUserId(principal.getUserId())) {
            log.error("[인증 오류] 존재하지 않는 글입니다");
            return false;
        }
        return true;
    }
}
