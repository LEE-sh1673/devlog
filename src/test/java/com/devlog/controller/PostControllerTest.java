package com.devlog.controller;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.devlog.config.CustomSpringBootTest;
import com.devlog.domain.Post;
import com.devlog.repository.PostRepository;
import com.devlog.request.PostCreate;
import com.fasterxml.jackson.databind.ObjectMapper;

@AutoConfigureMockMvc
@CustomSpringBootTest
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PostRepository postRepository;

    @BeforeEach
    void setUp() {
        postRepository.deleteAll();
    }

    @Test
    @DisplayName("posts 요청 시 Hello world 추력")
    void testPosts_givenSaveRequest_shouldReturnPostId() throws Exception {
        // given
        PostCreate request = PostCreate.builder()
            .title("글 제목")
            .content("글 본문")
            .build();

        // when
        ResultActions result = mockMvc.perform(
            post("/posts")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        );

        // then
        result.andDo(print())
            .andExpect(status().isOk())
            .andExpect(handler().handlerType(PostController.class));
    }

    @Test
    @DisplayName("/posts 요청시 title 값은 필수이다.")
    void testPosts_givenNullTitle_shouldReturnErrorResponse() throws Exception {
        // given
        PostCreate request = PostCreate.builder()
            .title(null)
            .content("글 본문")
            .build();

        // when
        ResultActions result = mockMvc.perform(
            post("/posts")
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        );

        // then
        result.andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(handler().handlerType(PostController.class))
            .andExpect(jsonPath("$.validation[0].fieldName", is("title")))
            .andExpect(jsonPath("$.validation[0].errorMessage", is("제목을 입력해주세요.")));
    }

    @Test
    @DisplayName("/posts 요청시 content 값은 필수이다.")
    void testPosts_givenNullContent_shouldReturnErrorResponse() throws Exception {
        // given
        PostCreate request = PostCreate.builder()
            .title("글 제목")
            .content(null)
            .build();

        // when
        ResultActions result = mockMvc.perform(
            post("/posts")
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        );

        // then
        result.andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(handler().handlerType(PostController.class))
            .andExpect(jsonPath("$.validation").isArray())
            .andExpect(jsonPath("$.validation.length()", is(1)))
            .andExpect(jsonPath("$.validation[0].fieldName", is("content")))
            .andExpect(jsonPath("$.validation[0].errorMessage", is("본문을 입력해주세요.")));
    }

    @Test
    @DisplayName("/posts 요청시 DB에 값이 저장된다.")
    void testPosts_givenSaveRequest_shouldSaveInDatabase() throws Exception {
        // given
        PostCreate request = PostCreate.builder()
            .title("글 제목")
            .content("글 본문")
            .build();

        // when
        ResultActions result = mockMvc.perform(
            post("/posts")
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        );

        // then
        result.andDo(print())
            .andExpect(status().isOk())
            .andExpect(handler().handlerType(PostController.class));

        assertEquals(1L, postRepository.count());
        Post post = postRepository.findAll().get(0);
        assertEquals(request.getTitle(), post.getTitle());
        assertEquals(request.getContent(), post.getContent());
    }

    @Test
    @DisplayName("글 1개 조회")
    void testPosts_givenSingleReadRequest_shouldReturnPost() throws Exception {
        // given
        Post post = Post.builder()
            .title("글 제목")
            .content("글 본문")
            .build();

        postRepository.save(post);

        // when
        ResultActions result = mockMvc.perform(
            get("/posts/{postId}", post.getId())
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
        );

        // then
        result.andDo(print())
            .andExpect(status().isOk())
            .andExpect(handler().handlerType(PostController.class))
            .andExpect(jsonPath("$.title", is(post.getTitle())))
            .andExpect(jsonPath("$.content", is(post.getContent())));
    }

    @Test
    @DisplayName("글 작성시 제목은 10글자 이하여야 한다.")
    void test3() throws Exception {
        // given
        Post post = Post.builder()
            .title("123456789012345")
            .content("글 본문")
            .build();

        postRepository.save(post);

        // when
        ResultActions result = mockMvc.perform(
            get("/posts/{postId}", post.getId())
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
        );

        // then
        result.andDo(print())
            .andExpect(status().isOk())
            .andExpect(handler().handlerType(PostController.class))
            .andExpect(jsonPath("$.title", is("1234567890")))
            .andExpect(jsonPath("$.content", is(post.getContent())));
    }

    @Test
    @DisplayName("글 여러개 조회")
    void testPosts_givenMultipleReadRequest_shouldReturnPostList() throws Exception {
        // given
        Post post1 = postRepository.save(Post.builder()
            .title("글 제목 1")
            .content("글 본문 1")
            .build());

        Post post2 = postRepository.save(Post.builder()
            .title("글 제목 2")
            .content("글 본문 2")
            .build());

        // when
        ResultActions result = mockMvc.perform(
            get("/posts")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
        );

        // then
        result.andDo(print())
            .andExpect(status().isOk())
            .andExpect(handler().handlerType(PostController.class))
            .andExpect(handler().methodName("findAll"))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()", is(2)))
            .andExpect(jsonPath("$[0].id", is(post1.getId()), Long.class))
            .andExpect(jsonPath("$[0].title", is(post1.getTitle())))
            .andExpect(jsonPath("$[0].content", is(post1.getContent())))
            .andExpect(jsonPath("$[1].id").value(post2.getId()))
            .andExpect(jsonPath("$[1].title", is(post2.getTitle())))
            .andExpect(jsonPath("$[1].content", is(post2.getContent())));
    }

    @Test
    @DisplayName("글 1페이지 조회")
    void testPosts_givenPageOneRequest_shouldReturnPostList() throws Exception {
        // given
        List<Post> posts = IntStream.range(0, 30)
            .mapToObj(idx -> Post.builder()
                .title("제목 " + (idx + 1))
                .content("본문 " + (idx + 1))
                .build()
            ).collect(Collectors.toList());

        postRepository.saveAll(posts);

        // when
        ResultActions result = mockMvc.perform(
            get("/posts")
                .param("page", "1")
                .param("sort", "id,DESC")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
        );

        // then
        result.andDo(print())
            .andExpect(status().isOk())
            .andExpect(handler().handlerType(PostController.class))
            .andExpect(handler().methodName("findAll"))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()", is(5)))
            .andExpect(jsonPath("$[0].id").value(posts.get(29).getId()))
            .andExpect(jsonPath("$[0].title", is("제목 30")))
            .andExpect(jsonPath("$[0].content", is("본문 30")));
    }
}