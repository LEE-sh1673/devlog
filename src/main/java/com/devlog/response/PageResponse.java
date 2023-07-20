package com.devlog.response;

import java.util.List;
import lombok.Getter;

@Getter
public class PageResponse {

    private List<PostResponse> content;

    private int number;

    private int size;

    private long totalElements;

    private int totalPages;

    private boolean isLast;
}
