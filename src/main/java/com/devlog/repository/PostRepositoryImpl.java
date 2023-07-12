package com.devlog.repository;

import static com.devlog.domain.QPost.*;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import com.devlog.domain.Post;
import com.devlog.request.PostSearch;
import com.devlog.response.PostResponse;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<PostResponse> findAll(final PostSearch postSearch) {
        return new PageImpl<>(
            getContent(postSearch),
            postSearch.toPageRequest(),
            getTotalCount()
        );
    }

    private List<PostResponse> getContent(final PostSearch postSearch) {
        List<Post> posts = jpaQueryFactory.selectFrom(post)
            .limit(postSearch.getSize())
            .offset(postSearch.getOffset())
            .orderBy(post.id.desc())
            .fetch();

        return posts.stream()
            .map(PostResponse::new)
            .collect(Collectors.toList());
    }

    private Long getTotalCount() {
        return jpaQueryFactory.select(post.count())
            .from(post)
            .fetchOne();
    }
}
