package com.devlog.controller;

import static org.hamcrest.Matchers.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.devlog.config.CustomSpringBootTest;
import com.devlog.domain.User;
import com.devlog.repository.SessionRepository;
import com.devlog.repository.UserRepository;
import com.devlog.request.LoginRequest;
import com.devlog.request.SignUpRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

@AutoConfigureMockMvc
@CustomSpringBootTest
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        sessionRepository.deleteAll();
    }

    @Test
    @DisplayName("로그인 성공 테스트")
    void testLogin_givenCorrectCredentials_shouldLogin() throws Exception {
        // given
        User user = userRepository.save(
            User.builder()
                .name("이승훈")
                .email("lsh901673@gmail.com")
                .password("1234")
                .build()
        );

        LoginRequest request = LoginRequest.builder()
            .email(user.getEmail())
            .password(user.getPassword())
            .build();

        // when
        ResultActions result = mockMvc.perform(
            post("/auth/login")
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        );

        // then
        result.andDo(print())
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.accessToken").exists());
    }

    @Test
    @DisplayName("로그인 성공 후 세션 생성 테스트")
    void testLogin_givenCorrectCredentials_shouldReturnSession() throws Exception {
        // given
        User user = userRepository.save(
            User.builder()
                .name("이승훈")
                .email("lsh901673@gmail.com")
                .password("1234")
                .build()
        );

        LoginRequest request = LoginRequest.builder()
            .email(user.getEmail())
            .password(user.getPassword())
            .build();

        // when
        ResultActions result = mockMvc.perform(
            post("/auth/login")
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        );

        // then
        result.andDo(print())
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.accessToken").exists());
    }

    @Test
    @DisplayName("로그인 요청 시 email 값은 필수이다.")
    void testLogin_givenNullEmail_shouldReturnErrorResponse() throws Exception {
        // given
        LoginRequest request = LoginRequest.builder()
            .email(null)
            .password("1234")
            .build();

        // when
        ResultActions result = mockMvc.perform(
            post("/auth/login")
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        );

        // then
        result.andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code", is("400")))
            .andExpect(jsonPath("$.validation").exists());
    }

    @Test
    @DisplayName("로그인 후 검증되지 않은 세션값으로 권한이 필요한 페이지에 접속할 수 없다.")
    void test5() throws Exception {
        // given
        User user = User.builder()
            .name("이승훈")
            .email("lsh901673@gmail.com")
            .password("1234")
            .build();

        userRepository.save(user);

        // when
        ResultActions result = mockMvc.perform(
            get("/foo")
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
        );

        // then
        result.andDo(print())
            .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("회원가입")
    void test6() throws Exception {
        // given
        SignUpRequest signUpRequest
            = new SignUpRequest("lsh901673@gmail.com", "1234", "이승훈");

        // when
        ResultActions result = mockMvc.perform(
            post("/auth/signup")
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequest))
        );

        // then
        result.andDo(print())
            .andExpect(status().isOk());
    }
}