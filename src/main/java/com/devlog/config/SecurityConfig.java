package com.devlog.config;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.*;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final MvcRequestMatcher.Builder mvcMatcherBuilder;

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
                    .anyRequest().authenticated()
            )
            .formLogin((formLogin) ->
                formLogin
                    .usernameParameter("username")
                    .passwordParameter("password")
                    .loginPage("/auth/login")
                    .loginProcessingUrl("/auth/login")
                    .defaultSuccessUrl("/")
            )
            .userDetailsService(userDetailsService());
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.builder()
            .username("lsh")
            .password("1234")
            .roles("ADMIN")
            .build();
        return new InMemoryUserDetailsManager(user);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }
}
