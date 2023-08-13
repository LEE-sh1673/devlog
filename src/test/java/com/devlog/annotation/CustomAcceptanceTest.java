package com.devlog.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.junit.jupiter.api.extension.ExtendWith;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(TYPE)
@Retention(RUNTIME)
@CustomSpringBootTest
@ExtendWith(TruncateTablesExtension.class)
public @interface CustomAcceptanceTest {
}

