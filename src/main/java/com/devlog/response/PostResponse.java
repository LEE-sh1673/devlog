package com.devlog.response;

import com.devlog.domain.Post;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostResponse {

    private static final int TRUNCATE_THRESHOLD = 50;

    private Long id;

    private String title;

    private String content;

    @Builder
    public PostResponse(final Long id, final String title, final String content) {
        this.id = id;
        this.title = truncateTitle(title);
        this.content = content;
    }

    public static PostResponse of(final Post post) {
        return new PostResponse(post.getId(), post.getTitle(), post.getContent());
    }

    private String truncateTitle(final String title) {
        return title.substring(0, Math.min(title.length(), TRUNCATE_THRESHOLD));
    }
}
