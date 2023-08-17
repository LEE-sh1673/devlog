package com.devlog.response;

import java.util.List;

import org.springframework.data.domain.Page;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PageResponse {

    private List<PostResponse> content;

    private int page;

    private int size;

    private long totalElements;

    private int totalPages;

    @JsonProperty("is_end")
    private Boolean isEnd;

    public static PageResponse of(final Page<PostResponse> pages) {
        return PageResponse.builder()
            .content(pages.getContent())
            .page(pages.getNumber())
            .size(pages.getSize())
            .totalElements(pages.getTotalElements())
            .totalPages(pages.getTotalPages())
            .isEnd(pages.isLast())
            .build();
    }
}
