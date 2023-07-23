package com.devlog.test;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment {

    @Id
    @GeneratedValue
    private Long id;

    private String author;

    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "POST_ID", foreignKey = @ForeignKey(name = "FK_COMMENT_POST"))
    private TestPost testPost;

    @Builder
    public Comment(final String author, final String content) {
        this.author = author;
        this.content = content;
    }

    public void updatePost(final TestPost testPost) {
        this.testPost = testPost;
    }
}
