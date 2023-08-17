package com.devlog.repository.post;

import static com.devlog.domain.QPost.post;
import static com.devlog.utils.QueryDslUtil.getSortedColumn;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.devlog.domain.Post;
import com.devlog.request.post.PostSearch;
import com.devlog.response.PostResponse;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<PostResponse> findAll(final PostSearch postSearch) {
        final PageRequest pageRequest = postSearch.toPageRequest();
        return new PageImpl<>(getContent(pageRequest), pageRequest, getCount());
    }

    private List<PostResponse> getContent(final Pageable pageable) {
        final List<Post> posts = jpaQueryFactory
            .selectFrom(post)
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .orderBy(getOrderSpecifiers(pageable.getSort()))
            .fetch();

        return posts.stream()
            .map(PostResponse::of)
            .collect(Collectors.toList());
    }

    private OrderSpecifier<?>[] getOrderSpecifiers(final Sort sort) {
        if (sort.isUnsorted()) {
            return new OrderSpecifier[] {post.id.desc()};
        }
        return sort.stream()
            .map(this::getOrderSpecifierBy)
            .toArray(OrderSpecifier[]::new);
    }

    private OrderSpecifier<?> getOrderSpecifierBy(final Sort.Order order) {
        return getSortedColumn(
            order.isAscending() ? Order.ASC : Order.DESC,
            post,
            order.getProperty()
        );
    }

    private Long getCount() {
        return jpaQueryFactory
            .select(post.count())
            .from(post)
            .fetchOne();
    }
}
