package com.devlog.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.transaction.Transactional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.devlog.config.CustomSpringBootTest;
import com.devlog.domain.Post;
import com.devlog.errors.v2.NotFoundException;
import com.devlog.repository.PostRepository;
import com.devlog.request.PostCreate;
import com.devlog.request.PostEdit;
import com.devlog.request.PostSearch;
import com.devlog.response.PageResponse;
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
        Post post = postRepository.save(Post.builder()
            .title("글 제목")
            .content("글 본문")
            .build());

        // when
        PostResponse postResponse = postService.findOne(post.getId());

        // then
        assertEquals(1L, postRepository.count());
        assertEquals("글 제목", postResponse.getTitle());
        assertEquals("글 본문", postResponse.getContent());
    }

    @Test
    @DisplayName("잘못된 글 1개 조회")
    void test8() {
        // given
        Post post = postRepository.save(Post.builder()
            .title("글 제목")
            .content("글 본문")
            .build());

        // when
        long wrongPostId = post.getId() + 1;

        // then
        assertThatThrownBy(() -> postService.findOne(wrongPostId))
            .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("글 여러 개 조회")
    void test3() {
        // given
        List<Post> posts = IntStream.range(0, 20)
            .mapToObj(idx -> Post.builder()
                .title("제목 " + (idx + 1))
                .content("본문 " + (idx + 1))
                .build()
            ).collect(Collectors.toList());

        postRepository.saveAll(posts);

        // when
        PageResponse postResponses = postService.findAll(
            PostSearch.builder().build()
        );

        // then
        assertEquals(0, postResponses.getPageNo());
        assertEquals(2, postResponses.getTotalPages());
        assertEquals(10L, postResponses.getPageSize());
        assertEquals(20L, postResponses.getTotalElements());
        assertEquals("제목 20", postResponses.getPosts().get(0).getTitle());
        assertFalse(postResponses.isLast());
    }

    @Test
    @DisplayName("글 제목 수정")
    void test4() {
        // given
        Post post = postRepository.save(Post.builder()
            .title("글 제목")
            .content("글 본문")
            .build());

        PostEdit request = PostEdit.builder()
            .title("수정된 제목")
            .content("글 본문")
            .build();

        // when
        postService.edit(post.getId(), request);
        Post actual = postRepository.findById(post.getId())
            .orElseThrow(NotFoundException::new);

        // then
        assertEquals("수정된 제목", actual.getTitle());
    }

    @Test
    @DisplayName("글 본문 수정")
    void test5() {
        // given
        Post post = postRepository.save(Post.builder()
            .title("글 제목")
            .content("글 본문")
            .build());

        PostEdit request = PostEdit.builder()
            .title("글 제목")
            .content("수정된 본문")
            .build();

        // when
        postService.edit(post.getId(), request);
        Post actual = postRepository.findById(post.getId())
            .orElseThrow(NotFoundException::new);

        // then
        assertEquals("글 제목", actual.getTitle());
        assertEquals("수정된 본문", actual.getContent());
    }

    @Test
    @DisplayName("글 제목, 본문에 null 또는 빈값이 들어와도 기존 데이터가 유지되어야 한다.")
    void test6() {
        // given
        Post post = postRepository.save(Post.builder()
            .title("글 제목")
            .content("글 본문")
            .build());

        PostEdit request = PostEdit.builder()
            .title(null)
            .content("")
            .build();

        // when
        postService.edit(post.getId(), request);
        Post actual = postRepository.findById(post.getId())
            .orElseThrow(NotFoundException::new);

        // then
        assertEquals("글 제목", actual.getTitle());
        assertEquals("글 본문", actual.getContent());
    }

    @Test
    @DisplayName("글 삭제")
    void test7() {
        // given
        Post post = postRepository.save(Post.builder()
            .title("글 제목")
            .content("글 본문")
            .build());

        // when
        postService.delete(post.getId());

        // then
        assertEquals(0L, postRepository.count());
        assertThrows(NotFoundException.class,
            () -> postService.findOne(post.getId()));
    }

    @Test
    @DisplayName("글 삭제 - 존재하지 않는 글")
    void test9() {
        // given
        Post post = postRepository.save(Post.builder()
            .title("글 제목")
            .content("글 본문")
            .build());

        // when
        long wrongPostId = post.getId() + 1;

        // then
        assertThatThrownBy(() -> postService.delete(wrongPostId))
            .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("글 수정 - 존재하지 않는 글")
    void test10() {
        // given
        Post post = postRepository.save(Post.builder()
            .title("글 제목")
            .content("글 본문")
            .build());

        PostEdit request = PostEdit.builder()
            .title("글 제목")
            .content("수정된 본문")
            .build();

        // when
        long wrongPostId = post.getId() + 1;

        // then
        assertThatThrownBy(() -> postService.edit(wrongPostId, request))
            .isInstanceOf(NotFoundException.class);
    }
}