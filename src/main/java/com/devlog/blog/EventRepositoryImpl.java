package com.devlog.blog;

import static com.devlog.blog.QEvent.*;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.devlog.utils.QueryDslUtil;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EventRepositoryImpl implements EventRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    private final ModelMapper mapper;

    @Override
    public Page<EventDto> findAll(EventSearch eventSearch, Pageable pageable) {
        List<EventDto> pages = getContent(eventSearch, pageable);
        return new PageImpl<>(pages, pageable, pages.size());
    }

    private List<EventDto> getContent(final EventSearch condition,
        final Pageable pageable) {

        List<Event> events = jpaQueryFactory
            .selectFrom(event)
            .where(tagsIn(condition.getTags()))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .orderBy(getOrderSpecifiers(pageable.getSort()))
            .fetch();

        return events.stream()
            .map(event -> mapper.map(event, EventDto.class))
            .collect(Collectors.toList());
    }

    private BooleanExpression tagsIn(List<String> tagNames) {
        return hasTags(tagNames) ? event.eventTags.any().tag.name.in(tagNames) : null;
    }

    private boolean hasTags(List<String> tags) {
        return tags != null && !tags.isEmpty();
    }

    private OrderSpecifier<?>[] getOrderSpecifiers(Sort sort) {
        return sort.stream()
            .map(this::getOrderSpecifierBy)
            .toArray(OrderSpecifier[]::new);
    }

    private OrderSpecifier<?> getOrderSpecifierBy(final Sort.Order order) {
        return QueryDslUtil.getSortedColumn(
            order.isAscending() ? Order.ASC : Order.DESC,
            event, order.getProperty());
    }
}
