package com.devlog.request;

import org.apache.commons.lang3.builder.ToStringBuilder;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;
import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

@Getter
@NoArgsConstructor(access = PROTECTED)
public class PostCreate {

    @NotBlank(message = "제목을 입력해주세요.")
    private String title;

    @NotBlank(message = "본문을 입력해주세요.")
    private String content;

    @Builder
    public PostCreate(final String title, final String content) {
        this.title = title;
        this.content = content;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, SHORT_PREFIX_STYLE)
            .append("title", title)
            .append("content", content)
            .build();
    }
}
