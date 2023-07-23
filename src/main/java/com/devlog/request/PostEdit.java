package com.devlog.request;

import static lombok.AccessLevel.*;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = PROTECTED)
public class PostEdit {

    @NotBlank(message = "제목을 입력해주세요.")
    private String title;

    @NotBlank(message = "본문을 입력해주세요.")
    private String content;

    @Builder
    public PostEdit(final String title, final String content) {
        this.title = title;
        this.content = content;
    }
}
