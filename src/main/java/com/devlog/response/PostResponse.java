package com.devlog.response;

import com.devlog.domain.Post;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PostResponse {

    private static final int TRUNCATE_THRESHOLD = 50;

    private final Long id;

    private final String title;

    private final String content;

    public PostResponse(final Post post) {
        this(post.getId(), post.getTitle(), post.getContent());
    }

    @Builder
    public PostResponse(final Long id, final String title, final String content) {
        this.id = id;
        this.title = truncateTitle(title);
        this.content = content;
    }

    private String truncateTitle(final String title) {
        return title.substring(0, Math.min(title.length(), TRUNCATE_THRESHOLD));
    }
}
