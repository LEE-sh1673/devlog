package com.devlog.config;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.*;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;

import com.devlog.config.handler.Http401Handler;
import com.devlog.config.handler.Http403Handler;
import com.devlog.config.handler.LoginFailHandler;
import com.devlog.domain.User;
import com.devlog.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final MvcRequestMatcher.Builder mvcMatcherBuilder;

    private final ObjectMapper objectMapper;

    //TODO: Security 설정 관련 변경점 (https://spring.io/security/cve-2023-34035)
    // Spring security와 Spring MVC에서 사용할 때
    // requestMatchers(String)을 사용하는 경우 여러 이유로 인해 인증 규칙이 잘못 구성될 수 있다고 합니다.
    // 코드가 동작하도록 수정하려면 아래와 같이 MvcRequestMatcher를 사용하도록 수정하면 됩니다.
    // 제 생각에 코드에서 "/auth/login" 의 경우 Spring MVC에서 등록한 end-point가 아니기 때문에
    // 오류가 발생한 거라 조심스럽게 추측해 봅니다..
    // 혹시 다른 의견 있으신 분들 계시면 말씀해주세요!
    // 참고: https://github.com/spring-projects/spring-security/issues/13568

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring()
            .requestMatchers(mvcMatcherBuilder.pattern("/favicon.ico"))
            .requestMatchers(mvcMatcherBuilder.pattern("/error"))
            .requestMatchers(toH2Console());
    }

    @Bean
    public SecurityFilterChain securityFilterChain(final HttpSecurity http)
        throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests((authorizeHttpRequests) ->
                authorizeHttpRequests
                    .requestMatchers(mvcMatcherBuilder.pattern("/auth/login")).permitAll()
                    .requestMatchers(mvcMatcherBuilder.pattern("/auth/signup")).permitAll()
                    .requestMatchers(mvcMatcherBuilder.pattern("/events")).permitAll()
                    .requestMatchers(mvcMatcherBuilder.pattern("/user")).hasRole("USER")
                    .requestMatchers(mvcMatcherBuilder.pattern("/admin")).hasRole("ADMIN")
                    .anyRequest().authenticated()
            )
            .formLogin((formLogin) ->
                formLogin
                    .usernameParameter("username")
                    .passwordParameter("password")
                    .loginPage("/auth/login")
                    .loginProcessingUrl("/auth/login")
                    .defaultSuccessUrl("/")
                    .failureHandler(new LoginFailHandler(objectMapper))
            ).rememberMe(rmConfigurer ->
                rmConfigurer
                    .rememberMeParameter("remember")
                    .alwaysRemember(false)
                    .tokenValiditySeconds((int)Duration.ofDays(30).getSeconds())
            ).exceptionHandling(exceptionHandling ->
                exceptionHandling
                    .accessDeniedHandler(new Http403Handler(objectMapper))
                    .authenticationEntryPoint(new Http401Handler(objectMapper))
            );
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService(final UserRepository userRepository) {
        return username -> {
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
