package com.devlog.controller;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.devlog.config.CustomSpringBootTest;
import com.devlog.domain.Session;
import com.devlog.domain.User;
import com.devlog.repository.SessionRepository;
import com.devlog.repository.UserRepository;
import com.devlog.request.LoginRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

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
            .andExpect(status().isCreated());
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
        assertEquals(1L, sessionRepository.count());

        Session session = sessionRepository.findAll().get(0);

        result.andDo(print())
            .andExpect(status().isCreated())
            .andExpect(cookie().exists("SESSION"))
            .andExpect(cookie().value("SESSION", session.getAccessToken()));
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
    @DisplayName("로그인 후 권한이 필요한 페이지에 접속한다 /foo")
    void test4() throws Exception {
        // given
        User user = User.builder()
            .name("이승훈")
            .email("lsh901673@gmail.com")
            .password("1234")
            .build();

        Session session = user.addSession();
        userRepository.save(user);

        // when
        ResultActions result = mockMvc.perform(
            get("/foo")
                .cookie(new Cookie("SESSION", session.getAccessToken()))
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
        );

        // then
        result.andDo(print())
            .andExpect(status().isOk());
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

        Session session = user.addSession();
        userRepository.save(user);

        // when
        ResultActions result = mockMvc.perform(
            get("/foo")
                .cookie(new Cookie("SESSION", session.getAccessToken() + "LL"))
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
        );

        // then
        result.andDo(print())
            .andExpect(status().isUnauthorized());
    }
}