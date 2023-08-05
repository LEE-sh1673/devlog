package com.devlog.blog;

import java.util.List;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventSearch {

    private String title;

    private List<String> tags;

    @Builder
    public EventSearch(String title, List<String> tags) {
        this.title = title;
        this.tags = tags;
    }

    @Override
    public String toString() {
        return "EventSearch{" +
            "title='" + title + '\'' +
            ", tags=" + tags +
            '}';
    }
}
