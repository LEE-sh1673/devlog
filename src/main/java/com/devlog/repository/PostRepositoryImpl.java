package com.devlog.repository;

import static com.devlog.domain.QPost.*;

import java.util.List;

import com.devlog.domain.Post;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Post> findAll(int page) {
        return jpaQueryFactory.selectFrom(post)
            .limit(10)
            .offset((page - 1) * 10L)
            .orderBy(post.id.desc())
            .fetch();
    }
}
