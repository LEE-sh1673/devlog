package com.devlog.config;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

import com.devlog.config.filter.EmailPasswordAuthFilter;
import com.devlog.config.handler.Http401Handler;
import com.devlog.config.handler.Http403Handler;
import com.devlog.config.handler.LoginFailHandler;
import com.devlog.config.handler.LoginSuccessHandler;
import com.devlog.domain.User;
import com.devlog.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.session.security.web.authentication.SpringSessionRememberMeServices;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final MvcRequestMatcher.Builder mvcMatcherBuilder;

    private final ObjectMapper objectMapper;

    private final UserRepository userRepository;

    @Profile("!test")
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
                    .anyRequest().permitAll()
            )
            .exceptionHandling((exceptionHandling) ->
                exceptionHandling
                    .accessDeniedHandler(new Http403Handler(objectMapper))
                    .authenticationEntryPoint(new Http401Handler(objectMapper))
            )
            .addFilterBefore(
                emailPasswordAuthFilter(),
                UsernamePasswordAuthenticationFilter.class
            );
        return http.build();
    }

    @Bean
    public EmailPasswordAuthFilter emailPasswordAuthFilter() {
        EmailPasswordAuthFilter filter = new EmailPasswordAuthFilter("/auth/login", objectMapper);

        filter.setRememberMeServices(rememberMeServices());
        filter.setAuthenticationManager(authenticationManager());

        filter.setAuthenticationFailureHandler(new LoginFailHandler(objectMapper));
        filter.setAuthenticationSuccessHandler(new LoginSuccessHandler(objectMapper));
        filter.setSecurityContextRepository(new HttpSessionSecurityContextRepository());
        return filter;
    }

    @Bean
    public RememberMeServices rememberMeServices() {
        SpringSessionRememberMeServices rememberMeServices = new SpringSessionRememberMeServices();
        rememberMeServices.setValiditySeconds(3600 * 24 * 30);
        rememberMeServices.setRememberMeParameterName("remember");
        rememberMeServices.setAlwaysRemember(true);
        return rememberMeServices;
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService());
        provider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(provider);
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
