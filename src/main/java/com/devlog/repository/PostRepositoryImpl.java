package com.devlog.repository;

import static com.devlog.domain.QPost.*;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;

import com.devlog.domain.Post;
import com.devlog.request.PostSearch;
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
            .orderBy(getOrderSpecifiers(postSearch))
            .fetch();

        return posts.stream()
            .map(PostResponse::new)
            .collect(Collectors.toList());
    }

    private OrderSpecifier<?> getOrderSpecifiers(final PostSearch postSearch) {
        if (!postSearch.isUnsorted()) {
            return getOrderSpecifier(postSearch);
        }
        return null;
    }

    private static OrderSpecifier<? extends Serializable> getOrderSpecifier(
        final PostSearch postSearch) {

        for (Sort.Order order : postSearch.getSort()) {
            Order direction = getOrderDirection(order);

            switch (order.getProperty()){
                case "title":
                    return new OrderSpecifier<>(direction, post.title);
                default:
                    return new OrderSpecifier<>(direction, post.id);
            }
        }
        return null;
    }

    private static Order getOrderDirection(Sort.Order order) {
        return order.getDirection().isAscending()
            ? Order.ASC
            : Order.DESC;
    }

    private Long getTotalCount() {
        return jpaQueryFactory.select(post.count())
            .from(post)
            .fetchOne();
    }
}
