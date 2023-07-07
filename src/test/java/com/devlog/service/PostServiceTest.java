package com.devlog.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.data.domain.Sort.Direction.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.transaction.Transactional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import com.devlog.config.CustomSpringBootTest;
import com.devlog.domain.Post;
import com.devlog.repository.PostRepository;
import com.devlog.request.PostCreate;
import com.devlog.response.PostResponse;

@Transactional
@CustomSpringBootTest
class PostServiceTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostService postService;

    @Test
    @DisplayName("글 작성")
    void test1() {
        // given
        PostCreate request = PostCreate.builder()
            .title("글 제목")
            .content("글 본문")
            .build();

        // when
        postService.save(request);

        // then
        assertEquals(1L, postRepository.count());
    }

    @Test
    @DisplayName("글 1개 조회")
    void test2() {
        // given
        Post post = Post.builder()
            .title("글 제목")
            .content("글 본문")
            .build();

        postRepository.save(post);

        // when
        PostResponse postResponse = postService.findById(post.getId());

        // then
        assertEquals(1L, postRepository.count());
        assertEquals("글 제목", postResponse.getTitle());
        assertEquals("글 본문", postResponse.getContent());
    }

    @Test
    @DisplayName("글 여러 개 조회")
    void test3() {
        // given
        List<Post> posts = IntStream.range(0, 20)
            .mapToObj(idx -> Post.builder()
                .title("제목 " + (idx+1))
                .content("본문 " + (idx+1))
                .build()
            ).collect(Collectors.toList());

        postRepository.saveAll(posts);

        // when
        List<PostResponse> postResponses = postService.findAll(
            PageRequest.of(0, 5, DESC, "id")
        );

        // then
        assertEquals(10L, postResponses.size());
        assertEquals("제목 20", postResponses.get(0).getTitle());
    }
}