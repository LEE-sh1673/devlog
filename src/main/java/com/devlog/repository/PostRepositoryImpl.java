package com.devlog.repository;

import static com.devlog.domain.QPost.*;

import java.util.List;

import com.devlog.domain.Post;
import com.devlog.request.PostSearch;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Post> findAll(final PostSearch postSearch) {
        return jpaQueryFactory.selectFrom(post)
            .limit(postSearch.getSize())
            .offset(postSearch.getOffset())
            .orderBy(post.id.desc())
            .fetch();
    }
}
