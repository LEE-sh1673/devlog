package com.devlog.jwt;

import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        // http.addFilterBefore(
        //     new JwtFilter(jwtTokenProvider),
        //     UsernamePasswordAuthenticationFilter.class
        // );
    }
}
