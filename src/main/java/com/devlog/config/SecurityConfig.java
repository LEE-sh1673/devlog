package com.devlog.config;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.devlog.config.filter.JwtAuthenticationFilter;
import com.devlog.config.filter.JwtExceptionFilter;
import com.devlog.config.handler.Http401Handler;
import com.devlog.config.handler.Http403Handler;
import com.devlog.domain.User;
import com.devlog.jwt.JwtClaimExtractor;
import com.devlog.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableWebSecurity(debug = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final ObjectMapper objectMapper;

    private final UserRepository userRepository;

    private final JwtClaimExtractor claimExtractor;

    @Bean
    @Profile("dev")
    SecurityFilterChain h2ConsoleSecurityFilterChain(final HttpSecurity http) throws Exception {
        http.securityMatcher(toH2Console());
        return http.build();
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain defaultSecurityFilterChain(final HttpSecurity http)
        throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);
        http.headers((headers) -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin));
        http.authorizeHttpRequests((httpRequests) -> httpRequests.anyRequest().permitAll());
        http.sessionManagement((session) -> session.sessionCreationPolicy(STATELESS));
        // http.logout((logout) -> logout
        //     .logoutUrl("/api/v1/auth/logout")
        //     .logoutSuccessHandler((request, response, authentication) ->
        //         SecurityContextHolder.clearContext())
        // );
        return http.build();
    }

    @Bean
    SecurityFilterChain exceptionHandlingSecurityFilterChain(final HttpSecurity http)
        throws Exception {
        http.exceptionHandling((exceptionHandling) -> exceptionHandling
            .accessDeniedHandler(new Http403Handler(objectMapper))
            .authenticationEntryPoint(new Http401Handler(objectMapper))
        );
        return http.build();
    }

    @Bean
    SecurityFilterChain jwtAuthenticationSecurityFilterChain(final HttpSecurity http)
        throws Exception {
        http.addFilterBefore(
            jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class
        );
        http.addFilterBefore(
            jwtExceptionFilter(), JwtAuthenticationFilter.class
        );
        return http.build();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(
            claimExtractor,
            userDetailsService()
        );
    }

    @Bean
    public JwtExceptionFilter jwtExceptionFilter() {
        return new JwtExceptionFilter(objectMapper);
    }

    // @Bean
    // SecurityFilterChain emailPasswordAuthenticationSecurityFilterChain(final HttpSecurity http)
    //     throws Exception {
    //     http.addFilterBefore(
    //         emailPasswordAuthFilter(),
    //         UsernamePasswordAuthenticationFilter.class
    //     );
    //     return http.build();
    // }

    // @Bean
    // public EmailPasswordAuthFilter emailPasswordAuthFilter() {
    //     EmailPasswordAuthFilter filter = new EmailPasswordAuthFilter("/auth/login", objectMapper);
    //
    //     filter.setRememberMeServices(rememberMeServices());
    //     filter.setAuthenticationManager(authenticationManager());
    //
    //     filter.setAuthenticationFailureHandler(new LoginFailHandler(objectMapper));
    //     filter.setAuthenticationSuccessHandler(new LoginSuccessHandler(objectMapper));
    //     filter.setSecurityContextRepository(new HttpSessionSecurityContextRepository());
    //     return filter;
    // }

    // @Bean
    // public RememberMeServices rememberMeServices() {
    //     SpringSessionRememberMeServices rememberMeServices = new SpringSessionRememberMeServices();
    //     rememberMeServices.setValiditySeconds(3600 * 24 * 30);
    //     rememberMeServices.setRememberMeParameterName("remember");
    //     rememberMeServices.setAlwaysRemember(true);
    //     return rememberMeServices;
    // }

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
