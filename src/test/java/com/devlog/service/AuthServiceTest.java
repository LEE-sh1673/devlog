package com.devlog.service;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import com.devlog.annotation.CustomSpringBootTest;
import com.devlog.config.AcceptanceTest;
import com.devlog.domain.User;
import com.devlog.errors.v2.AlreadyExistsEmailException;
import com.devlog.repository.UserRepository;
import com.devlog.service.dto.SignUpRequestDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Transactional
@CustomSpringBootTest
class AuthServiceTest extends AcceptanceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private PasswordEncoder encoder;

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

        List<String> userNames = userRepository.findAll()
            .stream()
            .map(User::getName)
            .toList();

        System.out.println("userRepository = " + userNames);

        // then
        User user = userRepository.findAll().iterator().next();
        assertEquals(1L, userRepository.count());
        assertEquals("email-1", user.getEmail());
        assertTrue(encoder.matches("password-1", user.getPassword()));
        assertEquals("name-1", user.getName());
    }

    @Test
    @DisplayName("회원가입 중복 테스트")
    void test2() {
        // given
        userRepository.save(User.builder()
            .name("name-1")
            .password(encoder.encode("password-1"))
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