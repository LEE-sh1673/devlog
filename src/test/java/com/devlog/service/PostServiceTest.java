package com.devlog.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.devlog.annotation.CustomSpringBootTest;
import com.devlog.config.AcceptanceTest;
import com.devlog.domain.Post;
import com.devlog.domain.User;
import com.devlog.errors.v2.NotFoundException;
import com.devlog.repository.PostRepository;
import com.devlog.repository.UserRepository;
import com.devlog.request.PostCreate;
import com.devlog.request.PostEdit;
import com.devlog.request.PostSearch;
import com.devlog.response.PageResponse;
import com.devlog.response.PostResponse;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

@Transactional
@CustomSpringBootTest
class PostServiceTest extends AcceptanceTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostService postService;

    @AfterEach
    void tearDown() {
        /*
            테스트 코드에서 @Transactional을 붙이는 이유는 DB 등을 연동하는 통합 테스트의 경우
            테스트는 여러번 수행해야 하는데, 기존 데이터가 계속 누적되면 다음 테스트에 영향을 줄 수 있다.
            따라서 테스트 이후에 저장된 데이터를 모두 삭제해야 한다.

            이런 문제를 해결하기 위해 테스트에서 @Transactional을 붙여주게 되면
            특별하게 데이터베이스에 저장된 결과를 커밋하지 않고 롤백하게 된다.
            그래서 테스트를 여러번 실행해도 동일한 상황에서 테스트를 할 수 있습니다.

            만약 테스트에서 @SpringBootTest의 RANDOM_PORT 등을 이용하는 경우
            스프링 컨테이너와 실행되는 테스트 메서드가 각각 다른 쓰레드에서 수행되기 때문에
            하나의 트랜잭션으로 묶이지 않는다.

            [참고로 @Transactional 테스트는 테스트 메소드 단위로 트랜잭션 경계가 각각 만들어지며,
            테스트 수행 중에 해당 트랜잭션 경계만 사용이 되고,
            그 경계를 테스트 메소드로 확장을 해도 문제가 없는 상황에서만 유효하다고 한다.
            즉, 위의 경우 두 개 이상의 트랜잭션 경계에서 각각 수행되며, 하나의 트랜잭션 안에 존재하지 않기 때문에
            롤백이 제대로 되지 않는 것이다.]

            [... 결론부터 말하면 @Transactional 어노테이션을 사용해서 트랜잭션을 롤백하는 전략은 인수 테스트에서는 사용할 수 없다.
            아마 많은 사람들이 테스트 프레임워크에서 관리하는 @Transactional 어노테이션을 붙이면
            트랜잭션이 끝난 뒤 롤백된다고 알고 있다. 물론 틀린 말은 아니다.
            하지만, 인수 테스트의 경우 @SpringBootTest 어노테이션에 port를 지정하여 서버를 띄우게 되는 데 이때,
            HTTP 클라이언트와 서버는 각각 다른 스레드에서 실행된다.
            따라서 아무리 테스트 코드에 @Transactional 어노테이션이 있다고 하더라도 호출되는 쪽은 다른 스레드에서
            새로운 트랜잭션으로 커밋하기 때문에 롤백 전략이 무의미해지는 것이다.]

            이는 즉 태스트에 트랜잭션이 적용되지 않으므로 데이터베이스에 저장된 결과를 롤백하지 않고
            그대로 반영됨을 말하며, 테스트 간의 격리성을 보장할 수 없게 한다.

            @Transactional 대신 tearDown 등에서 db를 클리어 하는 작업은 불가능한 건 아니지만 별로 추천되지 않는 방법이라 한다.
            이는 테스트 이전 상태가 모든 데이터가 다 비어있는 것으로 하기도 하지만, 어느 정도 초기 데이터 상태를 db에 넣고 하는 경우도 많은데,
            데이터를 클리어하는 작업에서 이를 정확하게 원복한다는게 롤백 방식을 쓰지 않으면 매우 귀찮고 실수하기 쉽기 때문이다.

            초기 데이터가 달라지기라도 하면 모든 db 정리하는 코드를 또 다 고쳐야 하는데,
            거기에 오류가 있으면 테스트가 다 깨지거나, 실패해야 할 다음 테스트가 성공하게 만들 수도 있다.
            그래서 아주 간단한 경우가 아니면 권장하지 않는다고 한다.

            [ 아니면 테스트 하나 수행할 때마다 db 전체를 다 날리고 초기화 하는 작업을 하는 방법도 있긴한데,
            애플리케이션이 커지면서 테스트가 매우 느려질테니 결국 테스트를 덜 만들거나 잘 하지 않게 될 겁니다.
            단점이 더 많은 거죠.]

            참고:
            - https://mangkyu.tistory.com/264
            - https://www.inflearn.com/questions/257700
            - https://tecoble.techcourse.co.kr/post/2020-08-31-jpa-transaction-test/
            - https://docs.spring.io/spring-framework/reference/testing/testcontext-framework/tx.html#testcontext-tx-rollback-and-commit-behavior

            참고::테스트 격리 관련
            - https://tecoble.techcourse.co.kr/post/2020-09-15-test-isolation/
         */
//        postRepository.deleteAll();
    }

    @Test
    @DisplayName("글 작성")
    void test1() {
        // given
        final User user = userRepository.save(User.builder()
            .name("lsh")
            .email("test@gmail.com")
            .build()
        );

        final PostCreate request = PostCreate.builder()
            .title("글 제목")
            .content("글 본문")
            .build();

        // when
        postService.save(user.getId(), request);

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
            PostSearch.builder().build(),
            PageRequest.of(0, 10)
        );

        // then
        assertEquals(0, postResponses.getNumber());
        assertEquals(2, postResponses.getTotalPages());
        assertEquals(10L, postResponses.getSize());
        assertEquals(20L, postResponses.getTotalElements());
        assertEquals("제목 20", postResponses.getContent().get(0).getTitle());
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