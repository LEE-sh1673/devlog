package com.devlog.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.springframework.context.annotation.Profile;
import org.springframework.security.test.context.support.WithSecurityContext;

@Profile("test")
@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockTestUserSecurityContextFactory.class)
public @interface WithMockTestUser {

    String email() default "test-user@gmail.com";

    String password() default "1234";

    String[] roles() default "ADMIN";
}
