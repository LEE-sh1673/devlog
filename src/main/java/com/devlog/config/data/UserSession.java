package com.devlog.config.data;

import lombok.Builder;
import lombok.Getter;

@Getter
public class UserSession {

    private final Long id;

    @Builder
    public UserSession(final Long id) {
        this.id = id;
    }
}
