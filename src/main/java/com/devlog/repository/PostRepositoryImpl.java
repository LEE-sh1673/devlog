package com.devlog.repository;

import static com.devlog.domain.QPost.post;
import static com.devlog.utils.QueryDslUtil.getSortedColumn;

import com.devlog.domain.Post;
import com.devlog.request.PostSearch;
import com.devlog.response.PostResponse;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;

@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<PostResponse> findAll(PostSearch postSearch, Pageable pageable) {
        List<PostResponse> pages = getContent(pageable);
        JPAQuery<Long> countQuery = getCount();
        return PageableExecutionUtils.getPage(pages, pageable, countQuery::fetchOne);
    }

    private List<PostResponse> getContent(final Pageable pageable) {
        List<Post> posts = jpaQueryFactory
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
            return new OrderSpecifier[]{post.id.desc()};
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

    private JPAQuery<Long> getCount() {
        return jpaQueryFactory
            .select(post.count())
            .from(post);
    }
}
