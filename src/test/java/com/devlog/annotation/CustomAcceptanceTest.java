package com.devlog.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import org.junit.jupiter.api.extension.ExtendWith;

@Target(TYPE)
@Retention(RUNTIME)
@CustomSpringBootTest
@ExtendWith(H2DatabaseCleaner.class)
public @interface CustomAcceptanceTest {
}

