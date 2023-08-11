package com.devlog.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class PostEditor {

    private final String title;

    private final String content;

    public PostEditor(final String title, final String content) {
        this.title = title;
        this.content = content;
    }

    public static PostEditorBuilder builder() {
        return new PostEditorBuilder();
    }

    @NoArgsConstructor
    public static class PostEditorBuilder {

        private String title;

        private String content;

        public PostEditorBuilder title(final String title) {
            if (hasTitle(title)) {
                this.title = title;
            }
            return this;
        }

        private boolean hasTitle(final String title) {
            return title != null && !title.isEmpty();
        }

        public PostEditorBuilder content(final String content) {
            if (hasContent(content)) {
                this.content = content;
            }
            return this;
        }

        private boolean hasContent(final String content) {
            return content != null && !content.isEmpty();
        }

        public PostEditor build() {
            return new PostEditor(this.title, this.content);
        }
    }
}
