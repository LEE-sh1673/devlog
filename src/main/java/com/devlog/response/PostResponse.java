package com.devlog.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostResponse {

    private static final int TRUNCATE_THRESHOLD = 50;

    private Long id;

    private String title;

    private String content;

    public PostResponse(final Long id, final String title, final String content) {
        this.id = id;
        this.title = truncateTitle(title);
        this.content = content;
    }

    private String truncateTitle(final String title) {
        return title.substring(0, Math.min(title.length(), TRUNCATE_THRESHOLD));
    }
}
