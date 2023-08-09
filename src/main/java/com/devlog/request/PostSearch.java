package com.devlog.request;

import java.util.Objects;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PostSearch {

    private static final int INITIAL_PAGE_NUMBER = 1;

    private static final int DEFAULT_PAGE_SIZE = 10;

    private static final int MAXIMUM_PAGE_SIZE = 50;

    private final int page;

    private final int size;

    private final Sort sort;

    @Builder
    public PostSearch(final Integer page, final Integer size, final Sort.Direction direction, final String[] sort) {
        this.page = Objects.requireNonNullElse(page, INITIAL_PAGE_NUMBER);
        this.size = Objects.requireNonNullElse(size, DEFAULT_PAGE_SIZE);
        this.sort = mapSort(direction, sort);
    }

    private Sort mapSort(Sort.Direction direction, String... properties) {
        direction = Objects.requireNonNullElse(direction, Sort.Direction.DESC);

        if (properties == null || properties.length == 0) {
            return Sort.by(direction, "id");
        }
        return Sort.by(direction, properties);
    }

    public long getOffset() {
        return (long)(Math.max(INITIAL_PAGE_NUMBER, page) - 1)
            * Math.min(size, MAXIMUM_PAGE_SIZE);
    }

    public PageRequest toPageRequest() {
        return PageRequest.of(Math.max(INITIAL_PAGE_NUMBER, page) - 1, size, sort);
    }
}
