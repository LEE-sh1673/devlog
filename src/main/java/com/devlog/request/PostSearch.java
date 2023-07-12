package com.devlog.request;

import static org.apache.commons.lang3.builder.ToStringStyle.*;

import java.util.Objects;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.data.domain.PageRequest;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PostSearch {

    private static final int INITIAL_PAGE_NUMBER = 1;

    private static final int DEFAULT_PAGE_SIZE = 10;

    private static final int MAXIMUM_PAGE_SIZE = 50;

    private final int page;

    private final int size;

    @Builder
    public PostSearch(final Integer page, final Integer size) {
        this.page =  Objects.requireNonNullElse(page, INITIAL_PAGE_NUMBER);
        this.size = Objects.requireNonNullElse(size, DEFAULT_PAGE_SIZE);
    }

    public long getOffset() {
        return (long)(Math.max(INITIAL_PAGE_NUMBER, page) - 1)
            * Math.min(size, MAXIMUM_PAGE_SIZE);
    }

    public PageRequest toPageRequest() {
        return PageRequest.of(Math.max(INITIAL_PAGE_NUMBER, page) - 1, size);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, SHORT_PREFIX_STYLE)
            .append("page", page)
            .append("size", size)
            .toString();
    }
}
