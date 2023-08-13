package com.devlog.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.devlog.annotation.CustomAcceptanceTest;
import com.devlog.annotation.WithMockTestUser;
import com.devlog.domain.Comment;
import com.devlog.domain.Post;
import com.devlog.repository.comment.CommentRepository;
import com.devlog.repository.post.PostRepository;
import com.devlog.request.comment.CommentCreate;
import com.devlog.request.comment.CommentDelete;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@CustomAcceptanceTest
@AutoConfigureMockMvc
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @WithMockTestUser
    @DisplayName("댓글 작성")
    void test1() throws Exception {
        // given
        final Post post = postRepository.save(Post.builder()
            .title("글 제목")
            .content("글 본문")
            .build()
        );

        final CommentCreate request = CommentCreate.builder()
            .author("lsh")
            .password("123456")
            .content("댓글 본문입니다!!!")
            .build();

        // when
        ResultActions result = mockMvc.perform(
            post("/posts/{postId}/comments", post.getId())
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        );

        // then
        result.andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success", is(true)))
            .andExpect(jsonPath("$.response", is(true)));

        final Comment comment = commentRepository.findAll().get(0);

        assertEquals(1L, commentRepository.count());
        assertEquals(request.getAuthor(), comment.getAuthor());
        assertEquals(request.getContent(), comment.getContent());
        assertTrue(passwordEncoder.matches(request.getPassword(), comment.getPassword()));
    }

    @Test
    @WithMockTestUser
    @DisplayName("댓글 삭제")
    void test2() throws Exception {
        // given
        final Comment comment = Comment.builder()
            .author("lsh")
            .content("content123456")
            .password(passwordEncoder.encode("123456"))
            .build();

        commentRepository.save(comment);

        final CommentDelete request = CommentDelete.builder()
            .password("123456")
            .build();

        // when
        ResultActions result = mockMvc.perform(
            post("/comments/{commentId}/delete", comment.getId())
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        );

        // then
        result.andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success", is(true)))
            .andExpect(jsonPath("$.response", is(true)));

        assertEquals(0L, commentRepository.count());
    }
}