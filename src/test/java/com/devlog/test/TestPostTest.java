package com.devlog.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.devlog.config.CustomSpringBootTest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;


@CustomSpringBootTest
class TestPostTest {

    @Autowired
    private TestPostRepository testPostRepository;

    @Test
    @DisplayName("페치 조인 테스트")
    void fetch_join() {
        // given
        List<TestPost> posts = new ArrayList<>();

        IntStream.range(0, 10).forEach(i -> {
            TestPost post = TestPost.builder()
                .name("name-" + i)
                .content("content-" + i)
                .build();

            for (int j = 0; j < 10; j++) {
                post.addComment(Comment.builder()
                    .author("author-" + j)
                    .content("content-" + j)
                    .build());
            }
            posts.add(post);
        });
        testPostRepository.saveAll(posts);

        // when
        List<TestPost> testPosts = testPostRepository.findAll();

        // then
        for (TestPost testPost : testPosts) {
            assertEquals(10L, testPost.getComments().size());
        }
    }
}