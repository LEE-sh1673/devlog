package com.devlog.response;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.data.domain.Page;

import lombok.Getter;

@Getter
public class PageResponse {

    private final List<PostResponse> posts;

    private final int pageNo;

    private final int pageSize;

    private final long totalElements;

    private final int totalPages;

    private final boolean last;

    public PageResponse(final Page<PostResponse> posts) {
        this.posts = posts.getContent();
        this.pageNo = posts.getNumber();
        this.pageSize = posts.getSize();
        this.totalElements = posts.getTotalElements();
        this.totalPages = posts.getTotalPages();
        this.last = posts.isLast();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
            .append("posts", posts)
            .append("pageNo", pageNo)
            .append("pageSize", pageSize)
            .append("totalElements", totalElements)
            .append("totalPages", totalPages)
            .append("last", last)
            .build();
    }
}
