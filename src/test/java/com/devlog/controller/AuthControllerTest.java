package com.devlog.controller;

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
import com.devlog.repository.UserRepository;
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

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
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