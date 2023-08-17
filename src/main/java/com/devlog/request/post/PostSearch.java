package com.devlog.request.post;

import static java.util.Objects.requireNonNullElse;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PostSearch {

    private static final int INITIAL_PAGE_NUMBER = 1;

    private static final int DEFAULT_PAGE_SIZE = 10;

    private static final int MAXIMUM_PAGE_SIZE = 50;

    private static final String DEFAULT_SORT_CRITERIA = "id";

    private static final String DEFAULT_SORT_DIRECTION = "desc";

    private final int page;

    private final int size;

    private final Sort sort;

    @Builder
    public PostSearch(final Integer page, final Integer size, final String sort,
        final String dir) {
        this.page = requireNonNullElse(page, INITIAL_PAGE_NUMBER);
        this.size = requireNonNullElse(size, DEFAULT_PAGE_SIZE);
        this.sort = mapSort(
            requireNonNullElse(sort, DEFAULT_SORT_CRITERIA),
            requireNonNullElse(dir, DEFAULT_SORT_DIRECTION)
        );
    }

    private Sort mapSort(final String sort, final String direction) {
        if (isAscending(direction)) {
            return Sort.by(sort).ascending();
        }
        return Sort.by(sort).descending();
    }

    private boolean isAscending(final String direction) {
        return direction.equalsIgnoreCase(Sort.Direction.ASC.name());
    }

    public long getOffset() {
        return (long)(Math.max(INITIAL_PAGE_NUMBER, page) - 1)
            * Math.min(size, MAXIMUM_PAGE_SIZE);
    }

    public PageRequest toPageRequest() {
        return PageRequest.of(Math.max(INITIAL_PAGE_NUMBER, page) - 1, size, sort);
    }
}
