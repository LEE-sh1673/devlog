package com.devlog.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.devlog.config.CustomSpringBootTest;
import com.devlog.crypto.PasswordEncoder;
import com.devlog.domain.User;
import com.devlog.errors.v2.AlreadyExistsEmailException;
import com.devlog.repository.UserRepository;
import com.devlog.service.dto.SignUpRequestDto;

@Transactional
@CustomSpringBootTest
class AuthServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private PasswordEncoder encoder;

    @AfterEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("회원가입 성공 테스트")
    void test1() {
        // given
        SignUpRequestDto requestDto = SignUpRequestDto.builder()
            .name("name-1")
            .password("password-1")
            .email("email-1")
            .build();

        // when
        authService.signup(requestDto);

        // then
        User user = userRepository.findAll().iterator().next();
        assertEquals(1L, userRepository.count());
        assertEquals("email-1", user.getEmail());
        assertEquals("password-1", user.getPassword());
        assertEquals("name-1", user.getName());
    }

    @Test
    @DisplayName("회원가입 중복 테스트")
    void test2() {
        // given
        userRepository.save(User.builder()
            .name("name-1")
            .password(encoder.encrypt("password-1"))
            .email("email-1")
            .build());

        SignUpRequestDto signUpRequest = SignUpRequestDto.builder()
            .name("name-1")
            .password("password-1")
            .email("email-1")
            .build();

        // when & then
        assertThrows(
            AlreadyExistsEmailException.class,
            () -> authService.signup(signUpRequest));
    }
}