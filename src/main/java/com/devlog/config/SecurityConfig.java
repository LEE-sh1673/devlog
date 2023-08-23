package com.devlog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.devlog.config.filter.JwtAuthenticationFilter;
import com.devlog.config.handler.Http401Handler;
import com.devlog.config.handler.Http403Handler;
import com.devlog.domain.User;
import com.devlog.jwt.JwtAuthenticationService;
import com.devlog.jwt.JwtClaimParser;
import com.devlog.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;
import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final ObjectMapper objectMapper;

    private final UserRepository userRepository;

    private final JwtClaimParser jwtClaimParser;

    @Bean
    @Order(1)
    @Profile("dev")
    public SecurityFilterChain h2ConsoleSecurityFilterChain(final HttpSecurity http)
        throws Exception {
        http.securityMatcher(toH2Console());
        return http.build();
    }

    @Bean
    @Order(2)
    SecurityFilterChain authorizeRequestsSecurityFilterChain(final HttpSecurity http)
        throws Exception {
        http.authorizeHttpRequests((httpRequests) -> httpRequests
            .requestMatchers(antMatcher( "/**")).permitAll()
            .requestMatchers(antMatcher( "/docs/**")).permitAll()
            .requestMatchers(antMatcher(HttpMethod.GET, "/posts/**")).permitAll()
            .anyRequest().authenticated()
        );
        return http.build();
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain defaultSecurityFilterChain(final HttpSecurity http)
        throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);
        http.headers((headers) -> headers
            .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
        );
        http.exceptionHandling((exceptionHandling) -> exceptionHandling
            .accessDeniedHandler(new Http403Handler(objectMapper))
            .authenticationEntryPoint(new Http401Handler(objectMapper))
        );
        http.sessionManagement((session) -> session
            .sessionCreationPolicy(STATELESS)
        );
        http.addFilterBefore(
            jwtAuthenticationFilter(),
            UsernamePasswordAuthenticationFilter.class
        );
        return http.build();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtAuthenticationService(), objectMapper);
    }

    @Bean
    public JwtAuthenticationService jwtAuthenticationService() {
        return new JwtAuthenticationService(jwtClaimParser, userDetailsService());
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(authenticationProvider());
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService());
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return (username) -> {
            User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(username + " 을 찾을 수 없습니다."));
            return new UserPrincipal(user);
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new SCryptPasswordEncoder(
            16,
            8,
            1,
            32,
            64);
    }
}
