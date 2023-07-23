package com.devlog.crypto;

public interface PasswordEncoder {

    String encrypt(final String rawPassword);

    boolean matches(final String rawPassword, final String encodedPassword);
}
