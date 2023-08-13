package com.devlog.request.comment;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
public class CommentDelete {

    private String password;

    @Builder
    public CommentDelete(final String password) {
        this.password = password;
    }
}
