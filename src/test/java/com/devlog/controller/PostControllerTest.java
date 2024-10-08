package com.devlog.controller;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.devlog.annotation.CustomAcceptanceTest;
import com.devlog.annotation.WithMockTestUser;
import com.devlog.domain.Post;
import com.devlog.domain.User;
import com.devlog.repository.UserRepository;
import com.devlog.repository.post.PostRepository;
import com.devlog.request.post.PostCreate;
import com.devlog.request.post.PostEdit;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@CustomAcceptanceTest
@AutoConfigureMockMvc
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @WithMockTestUser
    @DisplayName("글 작성 요청 시 글 저장")
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
            .andExpect(jsonPath("$.success", is(true)))
            .andExpect(jsonPath("$.response", is(true)));
    }

    @Test
    @WithMockTestUser
    @DisplayName("글 작성 요청 시 title 값은 필수이다.")
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
            .andExpect(jsonPath("$.validation.title").exists())
            .andExpect(jsonPath("$.validation.title", is("제목을 입력해주세요.")));
    }

    @Test
    @WithMockTestUser
    @DisplayName("글 작성 요청시 content 값은 필수이다.")
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
            .andExpect(jsonPath("$.validation.content").exists())
            .andExpect(jsonPath("$.validation.content", is("본문을 입력해주세요.")));
    }

    @Test
    @WithMockTestUser
    @DisplayName("글 작성 요청시 DB에 값이 저장된다.")
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
        assertEquals(1L, postRepository.count());
        Post post = postRepository.findAll().get(0);
        assertEquals(request.getTitle(), post.getTitle());
        assertEquals(request.getContent(), post.getContent());

        result.andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success", is(true)))
            .andExpect(jsonPath("$.response", is(true)));
    }

    @Test
    @DisplayName("글 1개 조회")
    void testPosts_givenSingleReadRequest_shouldReturnPost() throws Exception {
        // given
        final User user = userRepository.save(
            User.builder()
                .name("annoymous")
                .email("test@gmail.com")
                .build()
        );

        final Post post = postRepository.save(Post.builder()
            .title("글 제목")
            .content("글 본문")
            .user(user)
            .build());

        // when
        ResultActions result = mockMvc.perform(
            get("/posts/{postId}", post.getId())
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
        );

        // then
        result.andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success", is(true)))
            .andExpect(jsonPath("$.response.title", is(post.getTitle())))
            .andExpect(jsonPath("$.response.content", is(post.getContent())));
    }

    @Test
    @WithMockTestUser
    @DisplayName("글 작성시 제목은 50글자 이하여야 한다.")
    void test3() throws Exception {
        final User user = userRepository.findAll().get(0);
        String inputTitle = "12345".repeat(11);
        String expectedTitle = "12345".repeat(10);

        // given
        Post post = postRepository.save(Post.builder()
            .title(inputTitle)
            .content("글 본문")
            .user(user)
            .build());

        // when
        ResultActions result = mockMvc.perform(
            get("/posts/{postId}", post.getId())
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
        );

        // then
        result.andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.response.title", is(expectedTitle)));
    }

    @Test
    @DisplayName("글 여러개 조회")
    void testPosts_givenMultipleReadRequest_shouldReturnPostList() throws Exception {
        // given
        final List<Post> posts = postRepository.saveAll(List.of(
            Post.builder()
                .title("글 제목 1")
                .content("글 본문 1")
                .build(),
            Post.builder()
                .title("글 제목 2")
                .content("글 본문 2")
                .build()));

        // when
        ResultActions result = mockMvc.perform(
            get("/posts")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
        );

        // then
        result.andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success", is(true)))
            .andExpect(jsonPath("$.response.content").isArray())
            .andExpect(jsonPath("$.response.content.length()", is(2)))
            .andExpect(jsonPath("$.response.content[0].id").value(posts.get(1).getId()))
            .andExpect(jsonPath("$.response.content[0].title", is(posts.get(1).getTitle())))
            .andExpect(jsonPath("$.response.content[0].content", is(posts.get(1).getContent())))
            .andExpect(jsonPath("$.response.content[1].id").value(posts.get(0).getId()))
            .andExpect(jsonPath("$.response.content[1].title", is(posts.get(0).getTitle())))
            .andExpect(jsonPath("$.response.content[1].content", is(posts.get(0).getContent())));
    }

    @Test
    @WithMockTestUser
    @DisplayName("글 1페이지 조회")
    void testPosts_givenPageOneRequest_shouldReturnPostList() throws Exception {
        // given
        final User user = userRepository.findAll().get(0);
        final List<Post> posts = IntStream.range(0, 30)
            .mapToObj(idx -> Post.builder()
                .title("제목 " + (idx + 1))
                .content("본문 " + (idx + 1))
                .user(user)
                .build()
            ).collect(Collectors.toList());

        postRepository.saveAll(posts);

        // when
        ResultActions result = mockMvc.perform(
            get("/posts")
                .param("page", "1")
                .param("size", "10")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
        );

        // then
        result.andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.response.content").isArray())
            .andExpect(jsonPath("$.response.content.length()", is(10)))
            .andExpect(jsonPath("$.response.content[0].id").value(posts.get(29).getId()))
            .andExpect(jsonPath("$.response.content[0].title", is("제목 30")))
            .andExpect(jsonPath("$.response.content[0].content", is("본문 30")));
    }

    @Test
    @DisplayName("페이지를 0으로 요청하면 첫 페이지를 가져온다.")
    void testPosts_givenPageNumberZero_shouldReturnFirstPage() throws Exception {
        // given
        final List<Post> posts = IntStream.range(0, 30)
            .mapToObj(idx -> Post.builder()
                .title("제목 " + (idx + 1))
                .content("본문 " + (idx + 1))
                .build()
            ).collect(Collectors.toList());

        postRepository.saveAll(posts);

        // when
        ResultActions result = mockMvc.perform(
            get("/posts")
                .param("page", "0")
                .param("size", "10")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
        );

        // then
        result.andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.response.content").isArray())
            .andExpect(jsonPath("$.response.content.length()", is(10)))
            .andExpect(jsonPath("$.response.content[0].id").value(posts.get(29).getId()))
            .andExpect(jsonPath("$.response.content[0].title", is("제목 30")))
            .andExpect(jsonPath("$.response.content[0].content", is("본문 30")));
    }

    @Test
    @WithMockTestUser
    @DisplayName("글 수정 요청시 title 값은 필수이다.")
    void testPosts_givenNullTitleEditRequest_shouldReturnErrorResponse() throws Exception {
        // given
        final User user = userRepository.findAll().get(0);
        final Post post = postRepository.save(Post.builder()
            .title("글 제목")
            .content("글 본문")
            .user(user)
            .build()
        );

        final PostEdit request = PostEdit.builder()
            .title(null)
            .content("수정된 본문")
            .build();

        // when
        ResultActions result = mockMvc.perform(
            patch("/posts/{postId}", post.getId())
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        );

        // then
        result.andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.validation.title").exists())
            .andExpect(jsonPath("$.validation.title", is("제목을 입력해주세요.")));
    }

    @Test
    @WithMockTestUser
    @DisplayName("글 제목 수정")
    void testPosts_givenEditRequest_shouldEditCorrectly() throws Exception {
        // given
        final User user = userRepository.findAll().get(0);
        final Post post = postRepository.save(Post.builder()
            .title("글 제목")
            .content("글 본문")
            .user(user)
            .build()
        );

        final PostEdit request = PostEdit.builder()
            .title("수정된 제목")
            .content("글 본문")
            .build();

        // when
        ResultActions result = mockMvc.perform(
            patch("/posts/{postId}", post.getId())
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        );

        // then
        result.andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success", is(true)))
            .andExpect(jsonPath("$.response", is(true)));
    }

    @Test
    @WithMockTestUser
    @DisplayName("글 삭제")
    void testPosts_givenDeleteRequest_shouldDeleteCorrectly() throws Exception {
        // given
        final User user = userRepository.findAll().get(0);
        final Post post = postRepository.save(Post.builder()
            .title("글 제목")
            .content("글 본문")
            .user(user)
            .build()
        );

        // when
        ResultActions result = mockMvc.perform(
            delete("/posts/{postId}", post.getId())
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
        );

        // then
        result.andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success", is(true)))
            .andExpect(jsonPath("$.response", is(true)));
    }

    @Test
    @WithMockTestUser
    @DisplayName("존재하지 않는 게시글 조회")
    void test9() throws Exception {
        // given
        long noExistPostId = -1L;

        // when
        ResultActions result = mockMvc.perform(
            delete("/posts/{postId}", noExistPostId)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
        );

        // then
        result.andDo(print())
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.success", is(false)))
            .andExpect(jsonPath("$.error").exists())
            .andExpect(jsonPath("$.error.status", is(404)));
    }

    @Test
    @WithMockTestUser
    @DisplayName("존재하지 않는 게시글 수정")
    void test10() throws Exception {
        // given
        long noExistPostId = -1L;

        PostEdit request = PostEdit.builder()
            .title("수정된 제목")
            .content("글 본문")
            .build();

        // when
        ResultActions result = mockMvc.perform(
            patch("/posts/{postId}", noExistPostId)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        );

        // then
        result.andDo(print())
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.success", is(false)))
            .andExpect(jsonPath("$.error").exists())
            .andExpect(jsonPath("$.error.status", is(404)));
    }
}